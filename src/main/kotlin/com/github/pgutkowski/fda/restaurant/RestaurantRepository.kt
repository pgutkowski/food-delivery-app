package com.github.pgutkowski.fda.restaurant

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.AsyncDatabase
import com.github.pgutkowski.fda.customer.CustomerTable
import com.github.pgutkowski.fda.restaurant.controller.CreateRestaurantRequest
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.springframework.stereotype.Repository
import java.sql.SQLException
import java.util.*

object RestaurantTable : LongIdTable("restaurant") {
    val uuid = uuid("uuid").uniqueIndex()

    val name = varchar("name", 256)
}

class RestaurantEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RestaurantEntity>(RestaurantTable)

    var uuid: UUID by RestaurantTable.uuid

    var name: String by RestaurantTable.name
}

@Repository
class RestaurantRepository(private val asyncDatabase: AsyncDatabase) {
    suspend fun create(uuid: UUID, createRestaurantRequest: CreateRestaurantRequest) : SuspendableResult<Restaurant, SQLException> {
        return SuspendableResult.of {
            asyncDatabase.asyncTransaction {
                RestaurantEntity.new {
                    this.uuid = uuid
                    this.name = createRestaurantRequest.name
                }.toRestaurant()
            }
        }
    }
}

fun RestaurantEntity.toRestaurant() : Restaurant {
    return Restaurant(uuid = this.uuid, name = this.name)
}