package com.github.pgutkowski.fda.customer.controller

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.bind
import com.github.pgutkowski.fda.customer.Customer
import com.github.pgutkowski.fda.customer.CustomerService
import com.github.pgutkowski.fda.exceptions.ExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("/api/v1/customer")
@RestController
class CustomerControllerV1(
        private val customerService: CustomerService
) {

    @PutMapping("/{uuid}")
    suspend fun createWithPut(
            @RequestBody createCustomerRequest: CreateCustomerRequest,
            @PathVariable("uuid") uuid: UUID
    ): ResponseEntity<Customer> {

        return createCustomerRequest
                .validated()
                .bind { validRequest -> customerService.create(uuid, validRequest) }
                .fold(
                        success = { customer -> ResponseEntity.status(HttpStatus.CREATED).body(customer) },
                        failure = { exception -> ExceptionHandler.handle<CustomerControllerV1, Customer>(exception) }
                )
    }

    suspend fun CreateCustomerRequest.validated(): SuspendableResult<CreateCustomerRequest, Exception> {
        return SuspendableResult.of {
            if (firstName.isBlank())
                throw IllegalArgumentException("Customer first name must not be blank")

            if (lastName.isBlank())
                throw IllegalArgumentException("Customer last name must not be blank")

            if (emailAddress.isBlank())
                throw IllegalArgumentException("Customer emailAddress must not be blank")

            if (phoneNumber.isBlank())
                throw IllegalArgumentException("Customer phoneNumber must not be blank")

            if (address.isBlank())
                throw IllegalArgumentException("Customer address must not be blank")

            this
        }
    }

}