package com.github.pgutkowski.fda

import com.github.pgutkowski.fda.customer.CustomerRepository
import com.github.pgutkowski.fda.customer.controller.CreateCustomerRequest
import com.github.pgutkowski.fda.notification.Notification
import com.github.pgutkowski.fda.notification.NotificationListener
import com.github.pgutkowski.fda.order.Order
import com.github.pgutkowski.fda.order.OrderRepository
import com.github.pgutkowski.fda.order.controller.CreateOrderRequest
import com.github.pgutkowski.fda.order.controller.UpdateStatusRequest
import com.github.pgutkowski.fda.restaurant.Restaurant
import com.github.pgutkowski.fda.restaurant.RestaurantRepository
import com.github.pgutkowski.fda.restaurant.controller.CreateRestaurantRequest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
@SpringBootTest
class FoodDeliveryAppApplicationTests {

    companion object TestData {
        private val createCustomerRequest = CreateCustomerRequest(
                firstName = "Bright",
                lastName = "Inventions",
                phoneNumber = "123456789",
                emailAddress = "bright@inventions.com",
                address = "Matejki 12, 80-232 Gdańsk"
        )
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var restaurantRepository: RestaurantRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var testNotificationListener: TestNotificationListener

    @TestConfiguration
    class NotficationListenerTestConfiguration {

        @Bean
        fun testNotificationListener() = TestNotificationListener()
    }

    class TestNotificationListener : NotificationListener {
        val queue = ConcurrentLinkedQueue<Notification<*>>()

        override suspend fun <T> fire(notification: Notification<T>) {
            queue.add(notification)
        }

    }

    @BeforeEach
    fun beforeTest() {
        testNotificationListener.queue.clear()
    }

    @Test
    fun testOrderFlow() {
        //there are several restaurants offering various dishes
        val restaurantsCount = 20

        runBlocking {
            for (i in 1..restaurantsCount) {
                restaurantRepository.create(CreateRestaurantRequest(i.toString()))
            }
        }

        //Customer creates account
        val customer = runBlocking {
            customerRepository.create(createCustomerRequest).get()
        }

        //Customer fetches full list of restaurants
        val restaurantsPage = webTestClient.get()
                .uri("/api/v1/restaurant")
                .exchange()
                .expectStatus().isOk
                .expectBody(object : ParameterizedTypeReference<Page<Restaurant>>() {})
                .returnResult().responseBody ?: throw AssertionError("Expected non null response body")

        assertThat(restaurantsPage.list).isNotEmpty

        val restaurant = restaurantsPage.list.first()

        //Customer selects restaurants and creates order
        val order = runBlocking {
            orderRepository.create(CreateOrderRequest(
                    orderText = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    restaurant = restaurant.uuid,
                    customer = customer.uuid
            )).get()
        }

        //Order is accepted by restaurant
        updateOrderAndVerifyNotification(order, Order.Status.ACCEPTED_BY_RESTAURANT)

        //Order is being prepared by restaurant
        updateOrderAndVerifyNotification(order, Order.Status.IN_PREPARATION)

        //Order is in transport
        updateOrderAndVerifyNotification(order, Order.Status.IN_TRANSPORT)

        //Little client hiccup, sending old status update is ignored
        webTestClient.post()
                .uri("/api/v1/order/${order.uuid}/status")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(UpdateStatusRequest(Order.Status.IN_PREPARATION)))
                .exchange()
                .expectStatus().isBadRequest

        //Order is delivered
        updateOrderAndVerifyNotification(order, Order.Status.DELIVERED)
    }

    private fun updateOrderAndVerifyNotification(order: Order, newStatus: Order.Status) {
        val orderWithNewStatus = updateOrderStatus(order.uuid, newStatus)
        assertThat(orderWithNewStatus.status).isEqualTo(newStatus)
        val notification = testNotificationListener.queue.poll() as Notification<Order>
        assertThat(notification.body.status).isEqualTo(newStatus)
    }

    private fun updateOrderStatus(uuid: UUID, newStatus: Order.Status): Order {
        return webTestClient.post()
                .uri("/api/v1/order/$uuid/status")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(UpdateStatusRequest(newStatus)))
                .exchange()
                .expectStatus().isOk
                .expectBody<Order>()
                .returnResult().responseBody ?: throw AssertionError("Expected non null response body")
    }

}
