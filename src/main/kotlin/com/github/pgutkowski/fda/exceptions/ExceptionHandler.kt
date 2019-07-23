package com.github.pgutkowski.fda.exceptions

import com.github.pgutkowski.fda.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.lang.IllegalArgumentException
import java.sql.SQLException
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException

object ExceptionHandler {
    inline fun <reified T, R> handle(exception: Exception) : ResponseEntity<R> {
        return when(exception) {
            is ConstraintViolationException -> {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.constraintViolations)
            }
            is IllegalArgumentException -> {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.message)
            }
            else -> {
                getLogger<T>().error("Encountered exception", exception)
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build<R>()
            }
        } as ResponseEntity<R>
    }
}