package com.mathias8dev.springwebfluxkt.annotations

import org.springframework.stereotype.Component


@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class DataLoader