package com.github.pgutkowski.fda.restaurant

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.restaurant.controller.CreateRestaurantRequest
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.util.*

@Service
class RestaurantService(private val restaurantRepository: RestaurantRepository) {
    
    suspend fun create(uuid: UUID, createRestaurantRequest: CreateRestaurantRequest) : SuspendableResult<Restaurant, SQLException> {
        return restaurantRepository.create(uuid, createRestaurantRequest)
    }
    
}