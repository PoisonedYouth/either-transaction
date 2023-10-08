package com.poisonedyouth.eithertransaction.common

import arrow.core.Either

sealed interface Failure {
    val message: String

    data class ValidationFailure(override val message: String) : Failure

    data class InvalidStateFailure(override val message: String) : Failure

    data class GenericFailure(val e: Throwable) : Failure {
        override val message: String = e.localizedMessage
    }
}

fun <T> eval(exec: () -> T): Either<Failure, T> {
    return Either.catch {
        exec()
    }.mapLeft {
        Failure.GenericFailure(it)
    }
}

fun <T> Either<Failure, T>.getResultOrThrow(): T = this.fold(
    { failure ->
        throw mapFailureToException(failure)
    },
) {
    it
}

private fun mapFailureToException(failure: Failure): Throwable {
    return when (failure) {
        is Failure.GenericFailure -> RuntimeException(failure.message)
        is Failure.InvalidStateFailure -> IllegalStateException(failure.message)
        is Failure.ValidationFailure -> IllegalArgumentException(failure.message)
    }
}