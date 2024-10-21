package com.mathias8dev.springwebfluxkt.filtering

import com.mathias8dev.springwebfluxkt.exceptions.HttpException
import com.mathias8dev.springwebfluxkt.utils.Utils
import com.mathias8dev.springwebfluxkt.utils.toTypedData
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.reflect.full.memberProperties


abstract class SelfFilteringHandler : SelfFilter {

    protected abstract val filterableFields: Collection<String>

    private val logger = LoggerFactory.getLogger(SelfFilteringHandler::class.java)


    @Throws(Exception::class)
    override fun applyFilter(filterCriteria: FilterCriteria): Boolean {
        logger.debug("Applying filter")
        logger.debug("The filterCriteria is {} and the function is {}", filterCriteria, filterCriteria.filterFnToEnum())

        val dotIndex = filterCriteria.id.indexOf('.')


        if (!(filterableFields.contains(filterCriteria.id) ||
                    (dotIndex > 2 && filterableFields.find {
                        it.contains(filterCriteria.id.substring(0, dotIndex))
                    } != null))
        ) return true


        if (filterCriteria.id.split('.').size > 1) {

            val value = getValue(filterCriteria.id.substring(0, dotIndex))
            val criteria = filterCriteria.copy(id = filterCriteria.id.substring(dotIndex + 1))
            return when (value) {
                is SelfFilteringHandler -> value.applyFilter(
                    criteria
                )

                is Collection<*> -> {
                    value.map {
                        if (it is SelfFilteringHandler) it.applyFilter(criteria)
                        else throw HttpException(
                            httpStatus = HttpStatus.BAD_REQUEST,
                            message = "FilterException, the key: ${filterCriteria.id} is not filterable"
                        )
                    }.any { it }
                }

                else -> throw HttpException(
                    httpStatus = HttpStatus.BAD_REQUEST,
                    message = "FilterException, the key: ${filterCriteria.id} is not filterable"
                )
            }
        }

        return when (filterCriteria.filterFnToEnum()) {
            FilterFunction.FUZZY -> handleFuzzy(filterCriteria)
            FilterFunction.LESS_THAN -> handleLessThan(filterCriteria)
            FilterFunction.LESS_THAN_OR_EQUAL_TO -> handleLessThanOrEqualTo(filterCriteria)
            FilterFunction.GREATER_THAN -> handleGreaterThan(filterCriteria)
            FilterFunction.GREATER_THAN_OR_EQUAL_TO -> handleGreaterThanOrEqualTo(filterCriteria)
            FilterFunction.EQUALS -> handleEquals(filterCriteria)
            FilterFunction.LIKE -> handleLike(filterCriteria)
            FilterFunction.MEMBER -> handleMember(filterCriteria)
            FilterFunction.BETWEEN -> handleBetween(filterCriteria)
            FilterFunction.NOT_EQUALS -> handleNotEquals(filterCriteria)
            else -> true
        }
    }


    protected open fun handleFuzzy(filterCriteria: FilterCriteria): Boolean {
        return getValue(filterCriteria.id).toString()
            .contains(filterCriteria.value.toString(), true)
    }

    protected open fun handleLessThan(filterCriteria: FilterCriteria): Boolean {
        val value = getValue(filterCriteria.id)
        if (SelfFilter.dateFields.contains(filterCriteria.id)) {
            return getValue(filterCriteria.id).let { (it as Date) < Date(filterCriteria.value as Long) }
        }
        logger.debug("Comparing {}, {}", value is Comparable<*>, filterCriteria.value is Number)
        logger.debug("Comparing {} {}", value?.javaClass, filterCriteria.value?.javaClass)
        return if (areNumbers(value, filterCriteria.value)) {
            (value as Number).toDouble() < (filterCriteria.value as Number).toDouble()
        } else (value.toString() < filterCriteria.value.toString())
    }

    protected open fun handleLessThanOrEqualTo(filterCriteria: FilterCriteria): Boolean {
        val value = getValue(filterCriteria.id)
        if (SelfFilter.dateFields.contains(filterCriteria.id)) {
            return getValue(filterCriteria.id).let { (it as Date) <= Date(filterCriteria.value as Long) }
        }

        return if (areNumbers(value, filterCriteria.value)) {
            (value as Number).toDouble() <= (filterCriteria.value as Number).toDouble()
        } else (value.toString() <= filterCriteria.value.toString())
    }

    protected open fun handleGreaterThan(filterCriteria: FilterCriteria): Boolean {
        logger.debug("HandleGreaterThan")
        val value = getValue(filterCriteria.id)
        logger.debug("Are dates ?")
        if (SelfFilter.dateFields.contains(filterCriteria.id)) {
            return getValue(filterCriteria.id).let { (it as Date) > Date(filterCriteria.value as Long) }
        }
        logger.debug("Are not dates")
        logger.debug("Are numbers ?")
        return if (areNumbers(value, filterCriteria.value)) {
            logger.debug("Are numbers")
            (value as Number).toDouble() > (filterCriteria.value as Number).toDouble()
        } else (value.toString() > filterCriteria.value.toString())
    }

    protected open fun handleGreaterThanOrEqualTo(filterCriteria: FilterCriteria): Boolean {
        val value = getValue(filterCriteria.id)
        if (SelfFilter.dateFields.contains(filterCriteria.id)) {
            return getValue(filterCriteria.id).let { (it as Date) >= Date(filterCriteria.value as Long) }
        }

        return if (areNumbers(value, filterCriteria.value)) {
            (value as Number).toDouble() >= (filterCriteria.value as Number).toDouble()
        } else value.toString() >= filterCriteria.value.toString()
    }

    protected open fun handleEquals(filterCriteria: FilterCriteria): Boolean {
        logger.debug("HandleEquals")
        val value = getValue(filterCriteria.id)
        return if (areNumbers(value, filterCriteria.value)) {
            (value as Number).toDouble().toInt() == (filterCriteria.value as Number).toDouble().toInt()
        } else if (value !is String) value.toString() == filterCriteria.value.toString()
        else value == filterCriteria.value
    }

    protected open fun handleNotEquals(filterCriteria: FilterCriteria): Boolean {
        val value = getValue(filterCriteria.id)
        return if (areNumbers(value, filterCriteria.value)) {
            (value as Number).toDouble().toInt() != (filterCriteria.value as Number).toDouble().toInt()
        } else if (value !is String) value.toString() != filterCriteria.value.toString()
        else value != filterCriteria.value
    }

    protected open fun handleLike(filterCriteria: FilterCriteria): Boolean {
        val value = getValue(filterCriteria.id)
        return value.toString().contains(filterCriteria.value.toString(), true)
    }


    @Throws(HttpException::class)
    protected open fun handleMember(filterCriteria: FilterCriteria): Boolean {
        logger.debug("HandleMember")
        val value = getValue(filterCriteria.id)
        logger.debug("HandleMember The value is {} : {}", value, value is Collection<*>)
        return if (value !is Collection<*>) {
            throw HttpException(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = "Filter error, the field ${filterCriteria.id} in the corresponding table is not a collection"
            )
        } else {
            value.find { it.toString().contains(filterCriteria.value.toString(), true) } != null
        }
    }

    protected open fun handleBetween(filterCriteria: FilterCriteria): Boolean {
        return kotlin.runCatching {
            val criteriaValue: FilterCriteria._Range = filterCriteria.value.toTypedData()
            val value = getValue(filterCriteria.id)
            if (SelfFilter.dateFields.contains(filterCriteria.id))
                Utils.isBetween(
                    (value as Date).time.toDouble(),
                    criteriaValue.min,
                    criteriaValue.max,
                    ifMin = { true },
                    ifMax = { true },
                    ifBetween = { true },
                    otherwise = { false }
                )
            else {
                value as Number
                Utils.isBetween(
                    value.toDouble(),
                    criteriaValue.min,
                    criteriaValue.max,
                    ifMin = { true },
                    ifMax = { true },
                    ifBetween = { true },
                    otherwise = { false }
                )
            }
        }.getOrElse {
            throw HttpException(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = "It is impossible to filter using the provided filter criteria: $filterCriteria",
                cause = it
            )
        }
    }

    private fun isBetween(
        value: Double,
        min: Double?,
        max: Double?
    ): Boolean {
        return if (min != null && max == null && value >= min) true
        else if (max != null && min == null && value <= max) true
        else if (min != null && max != null && value >= min && value <= max) true
        else false
    }


    protected fun getValue(key: String): Any? {
        return kotlin.runCatching {
            this::class.memberProperties
                .firstOrNull { it.name == key }
                ?.getter
                ?.call(this)!!
        }.getOrNull()
    }

    private fun areNumbers(value: Any?, otherValue: Any?): Boolean {
        return value != null &&
                otherValue != null &&
                value is Number &&
                otherValue is Number
    }
}