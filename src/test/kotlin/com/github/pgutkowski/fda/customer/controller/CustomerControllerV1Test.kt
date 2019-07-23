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
    fun testCreateWithPut(){
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

}