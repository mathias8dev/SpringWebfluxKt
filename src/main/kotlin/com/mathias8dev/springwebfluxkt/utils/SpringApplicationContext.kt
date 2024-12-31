package com.mathias8dev.springwebfluxkt.utils

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component


@Component
class SpringApplicationContext : ApplicationContextAware {

    @Throws(BeansException::class)
    override fun setApplicationContext(context: ApplicationContext) {
        CONTEXT = context
    }

    companion object {
        private lateinit var CONTEXT: ApplicationContext


        fun <T> getBean(clazz: Class<T>): T {
            return CONTEXT.getBean(clazz)
        }
    }
}