package com.github.pgutkowski.fda.order

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.failure
import com.github.pgutkowski.fda.bind
import com.github.pgutkowski.fda.getLogger
import com.github.pgutkowski.fda.notification.Notification
import com.github.pgutkowski.fda.notification.NotificationService
import com.github.pgutkowski.fda.order.controller.CreateOrderRequest
import com.github.pgutkowski.fda.order.controller.UpdateStatusRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
        private val orderRepository: OrderRepository,
        private val notificationService: NotificationService
) {
    companion object {
        val logger = getLogger<OrderService>()
    }

    suspend fun create(createOrderRequest: CreateOrderRequest): SuspendableResult<Order, Exception> {
        return orderRepository.create(createOrderRequest)
    }

    suspend fun updateStatus(id: UUID, updateStatusRequest: UpdateStatusRequest): SuspendableResult<Order, Exception> {
        val updatedOrder = orderRepository.updateStatus(id, updateStatusRequest)

        //do not fail request because of failed notification
        updatedOrder
                .bind { order ->
                    notificationService.sendNotification(Notification(order))
                }.failure { exception ->
                    logger.error("Failed to send notification", exception)
                }

        return updatedOrder
    }

    suspend fun fetchOrder(uuid: UUID): SuspendableResult<Order, Exception> {
        return orderRepository.fetchOrder(uuid)
    }
}