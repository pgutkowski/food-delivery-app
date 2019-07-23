package com.github.pgutkowski.fda

import com.github.kittinunf.result.coroutines.SuspendableResult
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

suspend fun <NV : Any, V : Any, E : Exception> SuspendableResult<V, E>.bind(block: suspend (V) -> SuspendableResult<NV, E>) : SuspendableResult<NV, E> {
	return when(this) {
		is SuspendableResult.Success -> { block.invoke(this.value) }
		is SuspendableResult.Failure -> { SuspendableResult.error(this.error) }
	}
}