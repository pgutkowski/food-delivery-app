package com.github.pgutkowski.fda.order.controller

import com.github.kittinunf.result.coroutines.SuspendableResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CreateOrderRequestValidator {


    @Value("\${orderTextLengthLimit:32767}")
    private var orderTextLengthLimit = Short.MAX_VALUE.toInt()

    suspend fun CreateOrderRequest.validated(): SuspendableResult<CreateOrderRequest, Exception> {
        return SuspendableResult.of {
            if (orderText.isBlank())
                throw IllegalArgumentException("Order text must not be blank")

            if (orderText.length > Short.MAX_VALUE) //TODO: set limit by configuration
                throw IllegalArgumentException("Order text must not be longer than $orderTextLengthLimit")

            this
        }
    }

}