package com.github.pgutkowski.fda.order

import com.github.pgutkowski.fda.AsyncDatabase
import com.github.pgutkowski.fda.customer.CustomerTable
import com.github.pgutkowski.fda.restaurant.RestaurantTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.*
import javax.annotation.PostConstruct

object OrderTable : LongIdTable("order") {
    val uuid = uuid("uuid").uniqueIndex()

    val customer = reference("customer", CustomerTable)

    val restaurant = reference("restaurant", RestaurantTable)
}

class OrderEntity(id: EntityID<Long>) : LongEntity(id), Order {
    companion object : LongEntityClass<OrderEntity>(OrderTable)

    override val uuid: UUID by OrderTable.uuid
}

@Repository
class OrderRepository(private val asyncDatabase: AsyncDatabase) {

    @PostConstruct
    fun initializeTable(){
        transaction(asyncDatabase.database){
            SchemaUtils.createMissingTablesAndColumns(RestaurantTable, CustomerTable, OrderTable)
        }
    }

    suspend fun findByUuid(uuid: UUID): Order? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}