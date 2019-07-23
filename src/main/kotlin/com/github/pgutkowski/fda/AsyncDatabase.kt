package com.github.pgutkowski.fda

import kotlinx.coroutines.future.await
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

data class AsyncDatabase(val database: Database, private val executor : ExecutorService) {
    suspend fun <T> asyncTransaction(block: Transaction.() -> T) : T {
        return CompletableFuture.supplyAsync(Supplier { transaction(database) { block() } }, executor).await()
    }
}