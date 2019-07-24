package com.github.pgutkowski.fda.order

import com.github.pgutkowski.fda.customer.Customer
import com.github.pgutkowski.fda.restaurant.Restaurant
import java.util.*

data class Order(
        val uuid: UUID,
        val customer: Customer,
        val restaurant: Restaurant,
        val status: Status,
        val orderText: String
) {
    enum class Status {
        CREATED,
        ACCEPTED_BY_RESTAURANT,
        IN_PREPARATION,
        IN_TRANSPORT,
        DELIVERED
    }
}