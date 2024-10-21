package com.mathias8dev.springwebfluxkt.loader

import com.mathias8dev.springwebfluxkt.annotations.Populator
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent


@Populator
class GlobalDataLoader(
    private val postDataLoader: PostDataLoader,
) : ApplicationListener<ContextRefreshedEvent?> {


    private var alreadySetup = false
    private val logger = LoggerFactory.getLogger(javaClass)


    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        logger.info("Loading data")
        if (alreadySetup) return

        postDataLoader.populate()
        alreadySetup = true
    }
}