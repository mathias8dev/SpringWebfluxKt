package com.mathias8dev.springwebfluxkt.filtering


data class FilterCriteria(
    val id: String,
    val filterFn: String,
    val value: Any
) {


    val filterFnIsLessThan: Boolean
        get() = FilterFunction.LESS_THAN.key.equals(filterFn, true)

    val filterFnIsGreaterThan: Boolean
        get() = FilterFunction.GREATER_THAN.key.equals(filterFn, true)

    val filterFnIsLessThanOrEqualTo: Boolean
        get() = FilterFunction.LESS_THAN_OR_EQUAL_TO.key.equals(filterFn, true)

    val filterFnIsGreaterThanOrEqualTo: Boolean
        get() = FilterFunction.GREATER_THAN_OR_EQUAL_TO.key.equals(filterFn, true)

    val filterFnIsFuzzy: Boolean
        get() = FilterFunction.FUZZY.key.equals(filterFn, true)

    val filterFnIsEquals: Boolean
        get() = FilterFunction.EQUALS.key.equals(filterFn, true)

    val filterFnIsLike: Boolean
        get() = FilterFunction.LIKE.key.equals(filterFn, true)


    val filterFnIsMember: Boolean
        get() = FilterFunction.MEMBER.key.equals(filterFn, true)


    fun filterFnToEnum() = FilterFunction.entries
        .find { it.key.equals(filterFn, true) } ?: FilterFunction.FUZZY


    data class _Range(
        val min: Double?,
        val max: Double?
    )

}


enum class FilterFunction(
    val key: String
) {

    LESS_THAN("lessThan"),

    LESS_THAN_OR_EQUAL_TO("lessThanOrEqualTo"),

    GREATER_THAN("greaterThan"),

    GREATER_THAN_OR_EQUAL_TO("greaterThanOrEqualTo"),

    FUZZY("fuzzy"),

    EQUALS("equals"),

    LIKE("like"),

    MEMBER("member"),

    BETWEEN("between"),

    NOT_EQUALS("notEquals"), ;
}


enum class FilterMode(val key: String) {


    AND("and"),
    OR("or"), ;

    val isAnd: Boolean
        get() = this == AND

    val isOr: Boolean
        get() = this == OR


}