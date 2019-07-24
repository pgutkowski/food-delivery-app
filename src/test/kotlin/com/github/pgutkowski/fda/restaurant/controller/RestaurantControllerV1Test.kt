package com.github.pgutkowski.fda.restaurant.controller

import com.github.pgutkowski.fda.Page
import com.github.pgutkowski.fda.customer.CustomerTable
import com.github.pgutkowski.fda.order.OrderTable
import com.github.pgutkowski.fda.restaurant.Restaurant
import com.github.pgutkowski.fda.restaurant.RestaurantRepository
import com.github.pgutkowski.fda.restaurant.RestaurantTable
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters

@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
@SpringBootTest
class RestaurantControllerV1Test {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var restaurantRepository: RestaurantRepository

    @BeforeEach
    fun beforeEach() {
        transaction {
            OrderTable.deleteAll()
            CustomerTable.deleteAll()
            RestaurantTable.deleteAll()
        }
    }

    @Test
    fun `PUT with valid request body should return 201 CREATED`() {
        val createRestaurantRequest = CreateRestaurantRequest(name = "Bright Inventions")

        val createdCustomer = webTestClient.put()
                .uri("/api/v1/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createRestaurantRequest))
                .exchange()
                .expectStatus().isCreated
                .expectBody<Restaurant>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(createdCustomer.name).isEqualTo(createRestaurantRequest.name)
    }

    @Test
    fun `PUT with request body with blank first name should return 400 BAD REQUEST`() {
        val createRestaurantRequest = CreateRestaurantRequest(name = "")

        val errorMessage = webTestClient.put()
                .uri("/api/v1/restaurant")
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
    fun `PUT with request body with null phone number should return 400 BAD REQUEST`() {
        val createRestaurantRequest = mapOf<String, Any>()

        webTestClient.put()
                .uri("/api/v1/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createRestaurantRequest))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<String>()
    }

    @Test
    fun `GET to list all should return 200 OK paginated results`() {

        val restaurantsCount = 20
        runBlocking {
            for (i in 1..restaurantsCount) {
                restaurantRepository.create(CreateRestaurantRequest(i.toString()))
            }
        }

        val restaurantsPage = webTestClient.get()
                .uri("/api/v1/restaurant")
                .exchange()
                .expectStatus().isOk
                .expectBody<Page<Restaurant>>()
                .returnResult().responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(restaurantsPage.list).hasSize(10)
        assertThat(restaurantsPage.pageNumber).isEqualTo(0)

        val restaurantsSecondPage = webTestClient.get()
                .uri("/api/v1/restaurant?pageNumber=1")
                .exchange()
                .expectStatus().isOk
                .expectBody<Page<Restaurant>>()
                .returnResult().responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(restaurantsSecondPage.list).hasSize(10)
        assertThat(restaurantsSecondPage.pageNumber).isEqualTo(1)

        val restaurantsBigPage = webTestClient.get()
                .uri("/api/v1/restaurant?pageSize=100")
                .exchange()
                .expectStatus().isOk
                .expectBody<Page<Restaurant>>()
                .returnResult().responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(restaurantsBigPage.list).hasSize(restaurantsCount)
        assertThat(restaurantsBigPage.pageNumber).isEqualTo(0)
    }

}