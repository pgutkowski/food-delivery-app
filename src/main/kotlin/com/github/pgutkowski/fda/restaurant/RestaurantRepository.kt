package com.github.pgutkowski.fda.restaurant

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Database
import java.util.*

object RestaurantTable : LongIdTable("restaurant") {
    val uuid = uuid("uuid").uniqueIndex()
}

class RestaurantEntity(id: EntityID<Long>) : LongEntity(id), Restaurant {
    companion object : LongEntityClass<RestaurantEntity>(RestaurantTable)

    override val uuid: UUID by RestaurantTable.uuid
}

class RestaurantRepository(private val database: Database) {

}