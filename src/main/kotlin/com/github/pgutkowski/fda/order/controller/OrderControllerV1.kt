package com.github.pgutkowski.fda.order.controller

import com.github.pgutkowski.fda.bind
import com.github.pgutkowski.fda.exceptions.ExceptionHandler
import com.github.pgutkowski.fda.order.Order
import com.github.pgutkowski.fda.order.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("api/v1/order")
@RestController
class OrderControllerV1(
        private val orderService: OrderService,
        private val createOrderRequestValidator: CreateOrderRequestValidator
) {

    @PutMapping
    suspend fun createWithPUT(@RequestBody createOrderRequest: CreateOrderRequest): ResponseEntity<Order> {
        return with(createOrderRequestValidator) {
            createOrderRequest
                    .validated()
                    .bind { validatedRequest -> orderService.create(validatedRequest) }
                    .fold(
                            success = { order -> ResponseEntity.status(HttpStatus.CREATED).body(order) },
                            failure = { exception -> ExceptionHandler.handle<OrderControllerV1, Order>(exception) }
                    )

        }
    }

    @GetMapping("/{id}")
    suspend fun fetchOrderWithGET(
            @PathVariable("id") uuid: UUID
    ): ResponseEntity<Order> {
        return orderService.fetchOrder(uuid).fold(
                success = { order -> ResponseEntity.ok(order) },
                failure = { exception -> ExceptionHandler.handle<OrderControllerV1, Order>(exception) }
        )
    }

    @PostMapping("/{id}/status")
    suspend fun updateStatusWithPOST(
            @PathVariable("id") uuid: UUID,
            @RequestBody updateStatusRequest: UpdateStatusRequest
    ): ResponseEntity<Order> {
        return orderService.updateStatus(uuid, updateStatusRequest).fold(
                success = { order -> ResponseEntity.ok(order) },
                failure = { exception -> ExceptionHandler.handle<OrderControllerV1, Order>(exception) }
        )
    }

}