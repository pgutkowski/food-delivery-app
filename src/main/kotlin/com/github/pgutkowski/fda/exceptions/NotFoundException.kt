package com.github.pgutkowski.fda.exceptions

class NotFoundException(
        override val cause: Throwable? = null,
        override val message: String?
) : Exception()