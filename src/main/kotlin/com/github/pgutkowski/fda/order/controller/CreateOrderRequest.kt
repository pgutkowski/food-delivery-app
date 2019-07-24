package com.github.pgutkowski.fda.order.controller

import java.util.*

data class CreateOrderRequest(
        val orderText: String,
        val customer: UUID,
        val restaurant: UUID
)