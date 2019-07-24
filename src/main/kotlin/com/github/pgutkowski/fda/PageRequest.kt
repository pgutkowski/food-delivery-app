package com.github.pgutkowski.fda

data class PageRequest(val pageSize: Int, val pageNumber: Int) {
    val offset = pageSize * pageNumber
}