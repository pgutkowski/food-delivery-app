package com.github.pgutkowski.fda.restaurant.controller

import com.github.pgutkowski.fda.restaurant.Restaurant
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import java.util.*

@RunWith(SpringRunner::class)
@AutoConfigureWebTestClient
@SpringBootTest
class RestaurantControllerV1Test {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `PUT with valid request body should return 201 CREATED`(){
        val createRestaurantRequest = CreateRestaurantRequest(name = "Bright Inventions")

        val createdCustomer = webTestClient.put()
                .uri("/api/v1/restaurant/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createRestaurantRequest))
                .exchange()
                .expectStatus().isCreated
                .expectBody<Restaurant>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body customer info")

        assertThat(createdCustomer.name).isEqualTo(createRestaurantRequest.name)
    }

    @Test
    fun `PUT with request body with blank first name should return 400 BAD REQUEST`(){
        val createRestaurantRequest = CreateRestaurantRequest(name = "")

        val errorMessage = webTestClient.put()
                .uri("/api/v1/restaurant/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createRestaurantRequest))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<String>()
                .returnResult()
                .responseBody

        assertThat(errorMessage).contains("name", "blank")
    }

    @Test
    fun `PUT with request body with null phone number should return 400 BAD REQUEST`(){
        val createCustomerRequest = mapOf<String, Any>()

        webTestClient.put()
                .uri("/api/v1/restaurant/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createCustomerRequest))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<String>()
    }

}