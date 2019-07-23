package com.github.pgutkowski.fda.order.controller

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/order")
@RestController
class OrderControllerV1 {

    @PutMapping
    suspend fun createWithPut() {

    }

}