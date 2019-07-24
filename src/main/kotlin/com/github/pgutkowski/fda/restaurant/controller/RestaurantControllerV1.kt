package com.github.pgutkowski.fda.restaurant.controller

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.Page
import com.github.pgutkowski.fda.PageRequest
import com.github.pgutkowski.fda.bind
import com.github.pgutkowski.fda.exceptions.ExceptionHandler
import com.github.pgutkowski.fda.restaurant.Restaurant
import com.github.pgutkowski.fda.restaurant.RestaurantService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("api/v1/restaurant")
@RestController
class RestaurantControllerV1(private val restaurantService: RestaurantService) {

    @GetMapping
    suspend fun findAllWithGET(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") pageNumber: Int,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") pageSize: Int
    ): ResponseEntity<Page<Restaurant>> {
        return restaurantService.findAll(PageRequest(pageSize, pageNumber)).fold(
                success = { restaurantPage -> ResponseEntity.ok(restaurantPage) },
                failure = { exception -> ExceptionHandler.handle<RestaurantControllerV1, Page<Restaurant>>(exception) }
        )
    }

    @PutMapping
    suspend fun createWithPut(
            @RequestBody createRestaurantRequest: CreateRestaurantRequest
    ): ResponseEntity<Restaurant> {
        return createRestaurantRequest
                .validated()
                .bind { validRequest -> restaurantService.create(validRequest) }
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