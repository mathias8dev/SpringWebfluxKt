package com.mathias8dev.springwebfluxkt.repositories.post

import com.mathias8dev.springwebfluxkt.dtos.PageableDto
import com.mathias8dev.springwebfluxkt.dtos.category.CategoryResponseDto
import com.mathias8dev.springwebfluxkt.dtos.comment.CommentResponseDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
import com.mathias8dev.springwebfluxkt.dtos.toPageable
import com.mathias8dev.springwebfluxkt.filtering.FilterCriteria
import com.mathias8dev.springwebfluxkt.filtering.FilterMode
import com.mathias8dev.springwebfluxkt.models.Post
import com.mathias8dev.springwebfluxkt.utils.buildWhereClause
import com.mathias8dev.springwebfluxkt.utils.camelToSnakeCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.bind
import java.time.LocalDateTime

class CustomPostRepositoryImpl(
    private val template: R2dbcEntityTemplate,
    private val client: DatabaseClient,
) : CustomPostRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Retrieves all posts along with their associated categories and comments.
     *
     * @return a [Flow] of [PostResponseDto] containing the posts with their categories and comments.
     */
    override suspend fun findAllPostWithCategoriesAndComments(): Flow<PostResponseDto> {
        return client.sql(
            """
            SELECT p.id AS post_id, p.title, p.content, p.tags, p.created_at, p.updated_at,
                   c.id AS category_id, c.name AS category_name,
                   cm.id AS comment_id, cm.comment AS comment_comment, cm.created_at AS comment_created_at, cm.updated_at AS comment_updated_at
            FROM posts p
            LEFT JOIN posts_categories pc ON p.id = pc.post_id
            LEFT JOIN categories c ON pc.category_id = c.id
            LEFT JOIN comments cm ON p.id = cm.post_id
        """.trimIndent()
        )
            .fetch()
            .all()
            .bufferUntilChanged { it["post_id"] as Long }
            .flatMap(PostResponseDto::fromRows)
            .asFlow()
    }

    /**
     * Adds a category to a post by inserting a record into the `posts_categories` table.
     *
     * @param postId the ID of the post to which the category will be added.
     * @param categoryId the ID of the category to be added to the post.
     */
    override suspend fun addCategory(postId: Long, categoryId: Long) {
        val query = """
            INSERT INTO posts_categories (post_id, category_id) 
            VALUES (:postId, :categoryId)
            ON CONFLICT (post_id, category_id) DO NOTHING
        """.trimIndent()
        client.sql(query)
            .bind("postId", postId)
            .bind("categoryId", categoryId)
            .await()
    }

    override suspend fun findAll(
        pageableDto: PageableDto,
        filterCriteria: List<FilterCriteria>,
        filterMode: FilterMode
    ): Page<PostResponseDto> {
        return findAll(
            pageableDto.toPageable(),
            filterCriteria,
            filterMode
        )
    }


    /**
     * Retrieves a paginated list of posts with their associated categories and comments.
     *
     * @param pageable the pagination information including page number, size, and sorting options.
     * @param filterCriteria the list of filter criteria to apply to the query.
     * @param filterMode the mode to apply the filters (e.g., AND, OR).
     * @return a [Page] of [PostResponseDto] containing the paginated posts with their categories and comments.
     * @throws Exception if any error occurs during the database operations.
     */
    override suspend fun findAll(
        pageable: Pageable,
        filterCriteria: List<FilterCriteria>,
        filterMode: FilterMode
    ): Page<PostResponseDto> = coroutineScope {

        val orderByClause = buildOrderByClause(pageable)
        val whereClause = buildWhereClause(filterCriteria)

        // Fetch the total count of posts for pagination metadata
        val now = System.currentTimeMillis()


        logger.debug("Page: ${pageable.pageNumber}, Size: ${pageable.pageSize}, Offset: ${pageable.offset}")
        logger.debug("Order by: $orderByClause")

        // 1. Query to fetch posts with pagination
        val posts = client.sql(
            """
        SELECT p.id AS post_id, p.title, p.content, p.tags, p.created_at, p.updated_at
        FROM posts p
        $whereClause
        ORDER BY $orderByClause        
        LIMIT ${pageable.pageSize} OFFSET ${pageable.offset}
        """.trimIndent()
        )
            .fetch()
            .all()
            .collectList()  // collect posts into a list
            .awaitFirst()

        // Extract the post IDs from the result to use in the next queries
        val postIds = posts.map { it["post_id"] as Long }

        if (postIds.isEmpty()) {
            return@coroutineScope PageImpl(emptyList(), pageable, 0)
        }

        val totalAsync = async {
            client
                .sql("""SELECT COUNT(p.id) FROM posts p $whereClause""".trimIndent())
                .fetch()
                .one()
                .map { it["count"] as Long }
                .awaitFirst()
        }

        // 2. Query to fetch categories for the fetched posts
        val categoriesMapAsync = async {
            client.sql(
                """
        SELECT pc.post_id, c.id AS category_id, c.name AS category_name
        FROM posts_categories pc
        LEFT JOIN categories c ON pc.category_id = c.id
        WHERE pc.post_id IN (:postIds)
        """.trimIndent()
            )
                .bind("postIds", postIds)
                .fetch()
                .all()
                .collectList()
                .awaitFirst()
                .groupBy { it["post_id"] as Long }
        }  // Group categories by post_id

        // 3. Query to fetch comments for the fetched posts
        val commentsMapAsync = async {
            client.sql(
                """
        SELECT cm.post_id, cm.id AS comment_id, cm.comment AS comment_comment, cm.created_at AS comment_created_at, cm.updated_at AS comment_updated_at
        FROM comments cm
        WHERE cm.post_id IN (:postIds)
        """.trimIndent()
            )
                .bind("postIds", postIds)
                .fetch()
                .all()
                .collectList()
                .awaitFirst()
                .groupBy { it["post_id"] as Long }
        } // Group comments by post_id

        // 4. Map posts, categories, and comments to PostResponseDto
        val content = posts.map { post ->
            val postId = post["post_id"] as Long
            val postCategories = categoriesMapAsync.await()[postId]?.map(CategoryResponseDto::fromRowPrefixed)
            val postComments = commentsMapAsync.await()[postId]?.map(CommentResponseDto::fromRowPrefixed)

            PostResponseDto(
                id = postId,
                title = post["title"] as String,
                content = post["content"] as String,
                tags = (post["tags"] as? String)?.split(",")?.toMutableList(),
                createdAt = post["created_at"] as LocalDateTime,
                updatedAt = post["updated_at"] as LocalDateTime,
                categories = postCategories,
                comments = postComments
            )
        }

        logger.debug("It takes ${System.currentTimeMillis() - now} ms to fetch content")
        // Return a PageImpl with the content, pageable, and total count
        PageImpl(content, pageable, totalAsync.await())
    }

    /**
     * Inserts a new post into the `posts` table.
     *
     * @param post the [Post] object containing the details of the post to be inserted.
     * @return the inserted [Post] object with the generated ID and other details.
     * @throws Exception if any error occurs during the database operation.
     */
    override suspend fun insert(post: Post): Post {
        val query = """
        INSERT INTO posts (title, content, tags, created_at, updated_at) 
        VALUES (:title, :content, :tags, :createdAt, :updatedAt)
        RETURNING id, title, content, tags, published, created_at, updated_at
    """.trimIndent()

        return client.sql(query)
            .bind("title", post.title)
            .bind("content", post.content)
            .bind("tags", post.tags)
            .bind("createdAt", post.createdAt)
            .bind("updatedAt", post.updatedAt)
            .fetch()
            .one()
            .map(Post::fromRow)
            .awaitFirst()
    }

    override suspend fun findAllByAuthorId(
        authorId: Long,
        pageable: Pageable,
        filterCriteria: List<FilterCriteria>,
        filterMode: FilterMode
    ): Page<PostResponseDto> {

        val orderByClause = buildOrderByClause(pageable)
        val whereClause = buildWhereClause(filterCriteria)

        val totalAsync = client
            .sql("""SELECT COUNT(p.id) FROM posts p $whereClause AND p.author_id = :authorId""".trimIndent())
            .bind("authorId", authorId)
            .fetch()
            .one()
            .map { it["count"] as Long }


        val postListAsync = client.sql(
            """
            SELECT p.id AS post_id, p.title, p.content, p.tags, p.created_at, p.updated_at,
                   c.id AS category_id, c.name AS category_name,
            FROM posts p
            LEFT JOIN posts_categories pc ON p.id = pc.post_id
            LEFT JOIN categories c ON pc.category_id = c.id
            $whereClause AND p.author_id = :authorId
            ORDER BY $orderByClause        
            LIMIT ${pageable.pageSize} OFFSET ${pageable.offset}
        """.trimIndent()
        )
            .bind("authorId", authorId)
            .fetch()
            .all()
            .bufferUntilChanged { it["post_id"] as Long }
            .flatMap(PostResponseDto::fromRows)
            .collectList()


        return PageImpl(postListAsync.awaitFirst(), pageable, totalAsync.awaitFirst())
    }

    private fun buildOrderByClause(pageable: Pageable): String {
        return pageable.sort.joinToString(", ") {
            "${if (it.property == "id") "post_id" else it.property.camelToSnakeCase()} ${if (it.isAscending) "ASC" else "DESC"}"
        }
    }

}