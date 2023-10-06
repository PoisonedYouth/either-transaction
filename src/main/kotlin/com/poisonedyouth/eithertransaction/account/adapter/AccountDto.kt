package com.poisonedyouth.eithertransaction.account.adapter

import com.poisonedyouth.eithertransaction.account.domain.Account

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

fun Account.toAccountDto() = AccountDto(
    id = this.id.getIdValue(),
    name = this.name.value,
    userId = this.userId.getIdValue(),
    balance = this.balance
)
