package com.github.pgutkowski.fda.restaurant

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.Page
import com.github.pgutkowski.fda.PageRequest
import com.github.pgutkowski.fda.SuspendableDatabase
import com.github.pgutkowski.fda.restaurant.controller.CreateRestaurantRequest
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import org.springframework.stereotype.Repository
import java.util.*

object RestaurantTable : UUIDTable("restaurant") {
    val name = varchar("name", 256)
}

class RestaurantEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RestaurantEntity>(RestaurantTable)

    var name: String by RestaurantTable.name
}

@Repository
class RestaurantRepository(private val suspendableDatabase: SuspendableDatabase) {

    suspend fun create(createRestaurantRequest: CreateRestaurantRequest): SuspendableResult<Restaurant, Exception> {
        return suspendableDatabase.transaction {
            RestaurantEntity.new {
                this.name = createRestaurantRequest.name
            }.toRestaurant()
        }
    }

    suspend fun findAll(pageRequest: PageRequest): SuspendableResult<Page<Restaurant>, Exception> {
        return suspendableDatabase.transaction {
            val list = RestaurantEntity
                    .all()
                    .limit(n = pageRequest.pageSize, offset = pageRequest.offset)
                    .map(RestaurantEntity::toRestaurant)

            Page(list = list, pageNumber = pageRequest.pageNumber)
        }
    }
}

fun RestaurantEntity.toRestaurant(): Restaurant {
    return Restaurant(uuid = this.id.value, name = this.name)
}