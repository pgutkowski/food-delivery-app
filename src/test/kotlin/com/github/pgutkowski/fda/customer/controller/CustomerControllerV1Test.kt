package com.github.pgutkowski.fda.customer.controller

import com.github.pgutkowski.fda.customer.Customer
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
class CustomerControllerV1Test {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `PUT with valid request body should return 201 CREATED`(){
        val createCustomerRequest = CreateCustomerRequest(
                firstName = "Bright",
                lastName = "Inventions",
                phoneNumber = "123456789",
                emailAddress = "bright@inventions.com",
                address = "Matejki 12, 80-232 Gdańsk"
        )

        val createdCustomer = webTestClient.put()
                .uri("/api/v1/customer/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createCustomerRequest))
                .exchange()
                .expectStatus().isCreated
                .expectBody<Customer>()
                .returnResult()
                .responseBody ?: throw AssertionError("Expected non null response body customer info")

        assertThat(createdCustomer.firstName).isEqualTo(createCustomerRequest.firstName)
        assertThat(createdCustomer.lastName).isEqualTo(createCustomerRequest.lastName)
        assertThat(createdCustomer.phoneNumber).isEqualTo(createCustomerRequest.phoneNumber)
        assertThat(createdCustomer.emailAddress).isEqualTo(createCustomerRequest.emailAddress)
        assertThat(createdCustomer.address).isEqualTo(createCustomerRequest.address)
    }

    @Test
    fun `PUT with request body with blank first name should return 400 BAD REQUEST`(){
        val createCustomerRequest = CreateCustomerRequest(
                firstName = "",
                lastName = "Inventions",
                phoneNumber = "123456789",
                emailAddress = "bright@inventions.com",
                address = "Matejki 12, 80-232 Gdańsk"
        )

        val errorMessage = webTestClient.put()
                .uri("/api/v1/customer/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createCustomerRequest))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<String>()
                .returnResult()
                .responseBody

        assertThat(errorMessage).contains("first name", "blank")
    }

    @Test
    fun `PUT with request body with null phone number should return 400 BAD REQUEST`(){
        val createCustomerRequest = mapOf(
                "firstName" to "Bright",
                "lastName" to "Inventions",
                "phoneNumber" to null,
                "emailAddress" to "bright@inventions.com",
                "address" to "Matejki 12, 80-232 Gdańsk"
        )

        webTestClient.put()
                .uri("/api/v1/customer/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(createCustomerRequest))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<String>()
    }

}