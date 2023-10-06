package com.poisonedyouth.eithertransaction.user.domain

import java.time.LocalDate

data class User(
    val id: UserId = EmptyUserId,
    val name: UserName,
    val email: Email,
    val birthDate: BirthDate
)

sealed interface UserId {
    fun getIdValue(): Int
}

object EmptyUserId : UserId {
    override fun getIdValue(): Int {
        error("UserId not yet set.")
    }
}

@JvmInline
value class IntUserId(val value: Int) : UserId {
    init {
        require(value > 0) { "User id must be positive" }
    }

    override fun getIdValue(): Int {
        return value
    }
}

@JvmInline
value class UserName(val value: String) {
    init {
        require(value.length in 3..50) { "User name must be between 3 and 50 characters" }
    }
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Email must contain @" }
    }
}

@JvmInline
value class BirthDate(val value: LocalDate) {
    init {
        require(value.isBefore(LocalDate.now())) { "Birth date must be in the past" }
    }
}