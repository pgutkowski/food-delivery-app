package com.github.pgutkowski.fda

data class Page<T>(val list: List<T> = emptyList(), val pageNumber: Int = 0)