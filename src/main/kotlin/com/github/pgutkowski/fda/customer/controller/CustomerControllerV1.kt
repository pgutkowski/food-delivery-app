package com.github.pgutkowski.fda.customer.controller

import com.github.pgutkowski.fda.bind
import com.github.pgutkowski.fda.customer.Customer
import com.github.pgutkowski.fda.customer.CustomerService
import com.github.pgutkowski.fda.exceptions.ExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/customer")
@RestController
class CustomerControllerV1(
        private val customerService: CustomerService
) {

    @PutMapping
    suspend fun createWithPut(
            @RequestBody createCustomerRequest: CreateCustomerRequest
    ): ResponseEntity<Customer> {
        return with(CreateCustomerRequestValidator) {
            createCustomerRequest
                    .validated()
                    .bind { validRequest -> customerService.create(validRequest) }
                    .fold(
                            success = { customer -> ResponseEntity.status(HttpStatus.CREATED).body(customer) },
                            failure = { exception -> ExceptionHandler.handle<CustomerControllerV1, Customer>(exception) }
                    )
        }
    }


}