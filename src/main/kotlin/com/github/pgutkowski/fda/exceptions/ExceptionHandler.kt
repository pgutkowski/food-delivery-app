package com.github.pgutkowski.fda.exceptions

import com.github.pgutkowski.fda.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.sql.SQLException
import javax.validation.ValidationException

object ExceptionHandler {
    inline fun <reified T, R> handle(exception: Exception) : ResponseEntity<R> {
        getLogger<T>().error("Encountered exception", exception)

        return when(exception) {
            is ValidationException -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build<R>()
            is SQLException -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build<R>()
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build<R>()
        }
    }
}