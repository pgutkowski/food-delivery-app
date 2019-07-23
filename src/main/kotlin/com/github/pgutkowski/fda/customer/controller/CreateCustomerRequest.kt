package com.github.pgutkowski.fda.customer.controller

data class CreateCustomerRequest(
        val firstName: String,
        val lastName: String,
        val phoneNumber: String,
        val emailAddress: String,
        val address: String
)