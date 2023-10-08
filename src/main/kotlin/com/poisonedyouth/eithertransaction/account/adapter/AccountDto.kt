package com.poisonedyouth.eithertransaction.account.adapter

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.common.Failure

data class NewAccountDto(
    val name: String,
    val userId: Int
)

data class AccountDto(
    val id: Int,
    val name: String,
    val userId: Int,
    val balance: Int
)

fun Account.toAccountDto(): Either<Failure, AccountDto> = either {
    AccountDto(
        id = this@toAccountDto.id.getIdValue().bind(),
        name = this@toAccountDto.name.value,
        userId = this@toAccountDto.userId.getIdValue().bind(),
        balance = this@toAccountDto.balance
    )
}
