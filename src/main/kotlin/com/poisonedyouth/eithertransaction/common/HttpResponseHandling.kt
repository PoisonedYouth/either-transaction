package com.poisonedyouth.eithertransaction.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun Failure.respondFailure(): ResponseEntity<Any> {
    return when (this) {
        is Failure.ValidationFailure -> ResponseEntity(this.message, HttpStatus.BAD_REQUEST)
        is Failure.InvalidStateFailure -> ResponseEntity(this.message, HttpStatus.INTERNAL_SERVER_ERROR)
        is Failure.GenericFailure -> ResponseEntity(this.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}