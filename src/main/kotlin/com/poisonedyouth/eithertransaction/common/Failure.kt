package com.poisonedyouth.eithertransaction.common

import arrow.core.Either
import org.jetbrains.exposed.sql.transactions.transaction

sealed interface Failure {
    val message: String

    data class ValidationFailure(override val message: String) : Failure

    data class InvalidStateFailure(override val message: String) : Failure

    data class GenericFailure(val e: Throwable) : Failure {
        override val message: String = e.localizedMessage
    }
}

fun <T> evalInTransaction(exec: () -> T): Either<Failure, T> {
    return Either.catch {
        transaction {
            exec()
        }
    }.mapLeft {
        Failure.GenericFailure(it)
    }
}

fun <T> executeInTransaction(exec: () -> Either<Failure, T>): Either<Failure, T> {
    return transaction {
        exec().onLeft {
            this.rollback()
        }
    }
}
