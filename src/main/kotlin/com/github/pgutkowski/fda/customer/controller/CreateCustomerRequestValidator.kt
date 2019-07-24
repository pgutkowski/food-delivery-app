package com.github.pgutkowski.fda.customer.controller

import com.github.kittinunf.result.coroutines.SuspendableResult

object CreateCustomerRequestValidator {
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