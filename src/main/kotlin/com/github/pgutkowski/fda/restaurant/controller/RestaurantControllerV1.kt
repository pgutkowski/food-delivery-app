package com.github.pgutkowski.fda.restaurant.controller

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.bind
import com.github.pgutkowski.fda.exceptions.ExceptionHandler
import com.github.pgutkowski.fda.restaurant.Restaurant
import com.github.pgutkowski.fda.restaurant.RestaurantService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("api/v1/restaurant")
@RestController
class RestaurantControllerV1(private val restaurantService: RestaurantService) {

    @PutMapping("/{uuid}")
    suspend fun createWithPut(
            @RequestBody createRestaurantRequest: CreateRestaurantRequest,
            @PathVariable("uuid") uuid: UUID
    ): ResponseEntity<Restaurant> {
        return createRestaurantRequest
                .validated()
                .bind { validRequest -> restaurantService.create(uuid, validRequest) }
                .fold(
                        success = { restaurant -> ResponseEntity.status(HttpStatus.CREATED).body(restaurant) },
                        failure = { exception -> ExceptionHandler.handle<RestaurantControllerV1, Restaurant>(exception) }
                )
    }

    suspend fun CreateRestaurantRequest.validated(): SuspendableResult<CreateRestaurantRequest, Exception> {
        return SuspendableResult.of {
            if (name.isBlank())
                throw IllegalArgumentException("Customer name must not be blank")

            this
        }
    }

}