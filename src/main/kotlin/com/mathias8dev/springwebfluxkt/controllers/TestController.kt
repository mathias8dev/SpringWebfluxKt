package com.mathias8dev.springwebfluxkt.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/hello")
    suspend fun hello(): String {
        return "Hello World"
    }
}