package com.github.pgutkowski.fda.customer

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.customer.controller.CreateCustomerRequest
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import java.util.*
import javax.validation.Valid

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    suspend fun create(uuid: UUID, createCustomerRequest: CreateCustomerRequest) : SuspendableResult<Customer, Exception> {
        return customerRepository.create(uuid, createCustomerRequest)
    }

}