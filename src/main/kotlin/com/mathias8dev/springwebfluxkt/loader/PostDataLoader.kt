package com.mathias8dev.springwebfluxkt.loader

import com.mathias8dev.springwebfluxkt.annotations.DataLoader
import com.mathias8dev.springwebfluxkt.kotlinx.CoroutineScopeOwner
import com.mathias8dev.springwebfluxkt.kotlinx.CoroutineScopeProvider
import com.mathias8dev.springwebfluxkt.services.CategoryService
import com.mathias8dev.springwebfluxkt.services.CommentService
import com.mathias8dev.springwebfluxkt.services.PostService
import kotlinx.coroutines.currentCoroutineContext
import org.slf4j.LoggerFactory


@DataLoader
class PostDataLoader(
    private val postService: PostService,
    private val categoryService: CategoryService,
    private val commentService: CommentService
) : DataPopulator, CoroutineScopeProvider by CoroutineScopeOwner() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun populate() {
        
        /*coroutineScope.launch {
            repeat(1000) {
                (1..10000).map {
                    async {
                        val categoryDto = CategoryRequestDto(name = "Spring")

                        val category = categoryService.insert(categoryDto)
                        val postDto = PostRequestDto(
                            title = "Spring WebFlux",
                            content = "Spring WebFlux is a reactive web framework",
                            categoryId = category.id
                        )
                        val post = postService.insert(postDto)
                        val commentDto = CommentRequestDto(
                            postId = post.id,
                            comment = "This is a comment"
                        )

                        commentService.insert(commentDto)
                        logger.debug("Post inserted $post")
                    }
                }.awaitAll()
            }

        }*/

    }
}
