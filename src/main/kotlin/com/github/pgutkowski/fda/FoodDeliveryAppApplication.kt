package com.github.pgutkowski.fda

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FoodDeliveryAppApplication

fun main(args: Array<String>) {
	runApplication<FoodDeliveryAppApplication>(*args)
}

inline fun <reified T> getLogger() : Logger {
	return LoggerFactory.getLogger(T::class.java)
}