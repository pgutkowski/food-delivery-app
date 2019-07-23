package com.github.pgutkowski.fda.customer.controller

import com.github.pgutkowski.fda.customer.Customer
import com.github.pgutkowski.fda.customer.CustomerService
import com.github.pgutkowski.fda.exceptions.ExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("/api/v1/customer")
@RestController
class CustomerControllerV1(private val customerService: CustomerService) {

    @PutMapping("/{uuid}")
    suspend fun createWithPut(
            @RequestBody createCustomerRequest: CreateCustomerRequest,
            @PathVariable("uuid") uuid: UUID
    ) : ResponseEntity<Customer> {
        return customerService.create(uuid, createCustomerRequest).fold(
                success = { customer -> ResponseEntity.status(HttpStatus.CREATED).body(customer) },
                failure = { exception -> ExceptionHandler.handle<CustomerControllerV1, Customer>(exception) }
        )
    }

}