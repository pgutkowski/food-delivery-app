package com.github.pgutkowski.fda.order

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.SuspendableDatabase
import com.github.pgutkowski.fda.customer.CustomerEntity
import com.github.pgutkowski.fda.customer.CustomerTable
import com.github.pgutkowski.fda.customer.toCustomer
import com.github.pgutkowski.fda.getReferenced
import com.github.pgutkowski.fda.order.controller.CreateOrderRequest
import com.github.pgutkowski.fda.order.controller.UpdateStatusRequest
import com.github.pgutkowski.fda.restaurant.RestaurantEntity
import com.github.pgutkowski.fda.restaurant.RestaurantTable
import com.github.pgutkowski.fda.restaurant.toRestaurant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.*
import javax.annotation.PostConstruct

object OrderTable : UUIDTable("order") {
    val customer = reference("customer", CustomerTable)

    val restaurant = reference("restaurant", RestaurantTable)

    val status = enumeration("status", Order.Status::class).default(Order.Status.CREATED)

    val orderText = text("order_text")
}

class OrderEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OrderEntity>(OrderTable)

    var orderText by OrderTable.orderText

    var status by OrderTable.status

    var customer by CustomerEntity referencedOn OrderTable.customer

    var restaurant by RestaurantEntity referencedOn OrderTable.restaurant
}

fun OrderEntity.toOrder(): Order {
    return Order(
            uuid = this.id.value,
            orderText = this.orderText,
            status = this.status,
            customer = this.customer.toCustomer(),
            restaurant = this.restaurant.toRestaurant()
    )
}

@Repository
class OrderRepository(private val suspendableDatabase: SuspendableDatabase) {

    @PostConstruct
    fun initializeTable() {
        transaction(suspendableDatabase.database) {
            SchemaUtils.createMissingTablesAndColumns(RestaurantTable, CustomerTable, OrderTable)
        }
    }

    suspend fun create(createOrderRequest: CreateOrderRequest): SuspendableResult<Order, Exception> {
        return suspendableDatabase.transaction {
            val customerEntity = CustomerEntity.getReferenced(createOrderRequest.customer) {
                "Customer with id=$it does not exist"
            }

            val restaurantEntity = RestaurantEntity.getReferenced(createOrderRequest.restaurant) {
                "Restaurant with id=$it does not exist"
            }

            OrderEntity.new {
                customer = customerEntity
                restaurant = restaurantEntity
                orderText = createOrderRequest.orderText
            }.toOrder()
        }
    }

    suspend fun updateStatus(uuid: UUID, updateStatusRequest: UpdateStatusRequest): SuspendableResult<Order, Exception> {
        return suspendableDatabase.transaction {
            val orderEntity = OrderEntity[uuid]

            if (orderEntity.status > updateStatusRequest.newStatus) {
                throw IllegalArgumentException(
                        "Cannot update status ${orderEntity.status} to previous status ${updateStatusRequest.newStatus}"
                )
            }

            orderEntity.status = updateStatusRequest.newStatus
            orderEntity.toOrder()
        }
    }

    suspend fun fetchOrder(uuid: UUID): SuspendableResult<Order, Exception> {
        return suspendableDatabase.transaction {
            OrderEntity[uuid].toOrder()
        }
    }
}