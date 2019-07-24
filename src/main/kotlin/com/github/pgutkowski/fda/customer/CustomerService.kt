package com.github.pgutkowski.fda.customer

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.customer.controller.CreateCustomerRequest
import org.springframework.stereotype.Service

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    suspend fun create(createCustomerRequest: CreateCustomerRequest): SuspendableResult<Customer, Exception> {
        return customerRepository.create(createCustomerRequest)
    }

}