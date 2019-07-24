package com.github.pgutkowski.fda

import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.future.await
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

data class SuspendableDatabase(val database: Database, private val executor: ExecutorService) {

    suspend fun <T : Any> transaction(block: Transaction.() -> T): SuspendableResult<T, Exception> {
        return SuspendableResult.of { CompletableFuture.supplyAsync(Supplier { transaction(database) { block() } }, executor).await() }
    }
}