package com.poisonedyouth.eithertransaction.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun Exception.toResponseEntity(): ResponseEntity<Any> {
    return when (this) {
        is IllegalArgumentException -> ResponseEntity(this.message ?: "Unknown error", HttpStatus.BAD_REQUEST)
        else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}