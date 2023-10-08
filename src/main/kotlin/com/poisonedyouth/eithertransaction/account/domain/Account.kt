package com.poisonedyouth.eithertransaction.account.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.UserId

data class Account(
    val id: AccountId = EmptyAccountId,
    val name: AccountName,
    val userId: UserId,
    val balance: Int
)

sealed interface AccountId {
    fun getIdValue(): Either<Failure, Int>
}

object EmptyAccountId : AccountId {
    override fun getIdValue(): Either<Failure, Int> = either {
        raise(Failure.InvalidStateFailure("AccountId not yet set."))
    }
}

@JvmInline
value class IntAccountId private constructor(private val value: Int) : AccountId {
    override fun getIdValue(): Either<Failure, Int> {
        return value.right()
    }

    companion object {
        operator fun invoke(value: Int): Either<Failure, IntAccountId> = either {
            ensure(value > 0) { Failure.ValidationFailure("Account id must be positive") }
            IntAccountId(value)
        }
    }
}

@JvmInline
value class AccountName private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<Failure, AccountName> = either {
            ensure(value.length in 3..50) { Failure.ValidationFailure("Account name must be between 3 and 50 characters") }
            AccountName(value)
        }
    }
}
