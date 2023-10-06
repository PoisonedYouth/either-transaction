package com.poisonedyouth.eithertransaction.account.domain

import com.poisonedyouth.eithertransaction.user.domain.UserId

data class Account(
    val id: AccountId = EmptyAccountId,
    val name: AccountName,
    val userId: UserId,
    val balance: Int
)

sealed interface AccountId {
    fun getIdValue(): Int
}

object EmptyAccountId : AccountId {
    override fun getIdValue(): Int {
        error("AccountId not yet set.")
    }
}

@JvmInline
value class IntAccountId(val value: Int) : AccountId {
    init {
        require(value > 0) { "Account id must be positive" }
    }

    override fun getIdValue(): Int {
        return value
    }
}

@JvmInline
value class AccountName(val value: String) {
    init {
        require(value.length in 3..50) { "Account name must be between 3 and 50 characters" }
    }
}
