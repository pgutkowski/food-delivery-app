package com.github.pgutkowski.fda.customer

import java.util.*

data class Customer (
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val address: String
)