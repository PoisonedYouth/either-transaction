package com.poisonedyouth.eithertransaction.user.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import com.poisonedyouth.eithertransaction.common.Failure
import java.time.LocalDate

data class User(
    val id: UserId = EmptyUserId,
    val name: UserName,
    val email: Email,
    val birthDate: BirthDate
)

sealed interface UserId {
    fun getIdValue(): Either<Failure, Int>
}

object EmptyUserId : UserId {
    override fun getIdValue(): Either<Failure, Int> = either {
        raise(Failure.InvalidStateFailure("UserId not yet set."))
    }
}

@JvmInline
value class IntUserId private constructor(private val value: Int) : UserId {
    override fun getIdValue(): Either<Failure, Int> {
        return value.right()
    }

    companion object {
        operator fun invoke(value: Int): Either<Failure, IntUserId> = either {
            ensure(value > 0) {
                Failure.ValidationFailure("User id must be positive")
            }
            IntUserId(value)
        }
    }
}

@JvmInline
value class UserName private constructor(val value: String) {

    companion object {
        operator fun invoke(value: String): Either<Failure, UserName> = either {
            ensure(value.length in 3..50) {
                Failure.ValidationFailure("User name must be between 3 and 50 characters")
            }
            UserName(value)
        }
    }
}

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<Failure, Email> = either {
            ensure(value.contains("@")) {
                Failure.ValidationFailure("Email must contain @")
            }
            Email(value)
        }
    }
}

@JvmInline
value class BirthDate private constructor(val value: LocalDate) {
    companion object {
        operator fun invoke(value: LocalDate): Either<Failure, BirthDate> = either {
            ensure(value.isBefore(LocalDate.now())) {
                Failure.ValidationFailure("Birth date must be in the past")
            }
            BirthDate(value)
        }
    }
}