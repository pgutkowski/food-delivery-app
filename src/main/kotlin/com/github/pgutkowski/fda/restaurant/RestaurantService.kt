package com.github.pgutkowski.fda.restaurant

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.Page
import com.github.pgutkowski.fda.PageRequest
import com.github.pgutkowski.fda.restaurant.controller.CreateRestaurantRequest
import org.springframework.stereotype.Service

@Service
class RestaurantService(private val restaurantRepository: RestaurantRepository) {

    suspend fun create(createRestaurantRequest: CreateRestaurantRequest): SuspendableResult<Restaurant, Exception> {
        return restaurantRepository.create(createRestaurantRequest)
    }

    suspend fun findAll(pageRequest: PageRequest): SuspendableResult<Page<Restaurant>, Exception> {
        return restaurantRepository.findAll(pageRequest)
    }

}