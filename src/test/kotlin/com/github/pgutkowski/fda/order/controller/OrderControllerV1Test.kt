package com.github.pgutkowski.fda.order.controller

import com.github.pgutkowski.fda.customer.CustomerRepository
import com.github.pgutkowski.fda.customer.controller.CreateCustomerRequest
import com.github.pgutkowski.fda.order.Order
import com.github.pgutkowski.fda.restaurant.RestaurantRepository
import com.github.pgutkowski.fda.restaurant.controller.CreateRestaurantRequest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
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
import java.util.*

@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
@SpringBootTest
class OrderControllerV1Test {

    companion object TestData {
        private val createCustomerRequest = CreateCustomerRequest(
                firstName = "Bright",
                lastName = "Inventions",
                phoneNumber = "123456789",
                emailAddress = "bright@inventions.com",
                address = "Matejki 12, 80-232 Gdańsk"
        )

        private val createRestaurantRequest = CreateRestaurantRequest("Bright Foods")
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var restaurantRepository: RestaurantRepository

    @Test
    fun `PUT with valid request body should return 201 CREATED`() {
        val customer = runBlocking {
            customerRepository.create(createCustomerRequest).get()
        }

        val restaurant = runBlocking {
            restaurantRepository.create(createRestaurantRequest).get()
        }

        val createOrderRequest = CreateOrderRequest(
                orderText = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                restaurant = restaurant.uuid,
                customer = customer.uuid
        )

        val createdOrder = webTestClient.put()
                .uri("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createOrderRequest))
                .exchange()
                .expectStatus().isCreated
                .expectBody<Order>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(createdOrder.status).isEqualTo(Order.Status.CREATED)
        assertThat(createdOrder.restaurant.name).isEqualTo(restaurant.name)
        assertThat(createdOrder.restaurant.uuid).isEqualTo(restaurant.uuid)
    }

    @Test
    fun `PUT with invalid customer id should return 400 BAD REQUEST`() {
        val restaurant = runBlocking {
            restaurantRepository.create(createRestaurantRequest).get()
        }

        val createOrderRequest = CreateOrderRequest(
                orderText = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                restaurant = restaurant.uuid,
                customer = UUID.randomUUID()
        )

        val badRequestResponse = webTestClient.put()
                .uri("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createOrderRequest))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<String>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(badRequestResponse).contains("Customer", createOrderRequest.customer.toString())
    }

    @Test
    fun `PUT with invalid restaurant id should return 400 BAD REQUEST`() {
        val customer = runBlocking {
            customerRepository.create(createCustomerRequest).get()
        }

        val createOrderRequest = CreateOrderRequest(
                orderText = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                restaurant = UUID.randomUUID(),
                customer = customer.uuid
        )

        val badRequestResponse = webTestClient.put()
                .uri("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createOrderRequest))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<String>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(badRequestResponse).contains("Restaurant", createOrderRequest.restaurant.toString())
    }

    @Test
    fun `GET existing order should return 200 OK`() {
        val customer = runBlocking {
            customerRepository.create(createCustomerRequest).get()
        }

        val restaurant = runBlocking {
            restaurantRepository.create(createRestaurantRequest).get()
        }

        val createOrderRequest = CreateOrderRequest(
                orderText = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                restaurant = restaurant.uuid,
                customer = customer.uuid
        )

        val createdOrder = webTestClient.put()
                .uri("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createOrderRequest))
                .exchange()
                .expectStatus().isCreated
                .expectBody<Order>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body")

        webTestClient.get()
                .uri("/api/v1/order/${createdOrder.uuid}")
                .exchange()
                .expectStatus().isOk
                .expectBody<Order>()
    }

    @Test
    fun `GET not existing order should return 404 NOT FOUND`() {
        webTestClient.get()
                .uri("/api/v1/order/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isNotFound
    }

    @Test
    fun `POST update existing order status should return 200 OK`() {
        val customer = runBlocking {
            customerRepository.create(createCustomerRequest).get()
        }

        val restaurant = runBlocking {
            restaurantRepository.create(createRestaurantRequest).get()
        }

        val createOrderRequest = CreateOrderRequest(
                orderText = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                restaurant = restaurant.uuid,
                customer = customer.uuid
        )

        val createdOrder = webTestClient.put()
                .uri("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createOrderRequest))
                .exchange()
                .expectStatus().isCreated
                .expectBody<Order>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body")

        val updatedStatusOrder = webTestClient.post()
                .uri("/api/v1/order/${createdOrder.uuid}/status")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(UpdateStatusRequest(Order.Status.ACCEPTED_BY_RESTAURANT)))
                .exchange()
                .expectStatus().isOk
                .expectBody<Order>()
                .returnResult().responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(updatedStatusOrder.status).isEqualTo(Order.Status.ACCEPTED_BY_RESTAURANT)

        val updatedStatusOrderFetchedAgain = webTestClient.get()
                .uri("/api/v1/order/${createdOrder.uuid}")
                .exchange()
                .expectStatus().isOk
                .expectBody<Order>()
                .returnResult().responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(updatedStatusOrderFetchedAgain.status).isEqualTo(Order.Status.ACCEPTED_BY_RESTAURANT)
    }

}