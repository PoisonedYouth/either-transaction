package com.poisonedyouth.eithertransaction.account.port

import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.user.domain.UserId

interface AccountUseCase {
    fun createAccount(name: String, userId: Int): Account
    fun getAccount(accountId: Int): Account?
    fun deleteAccounts(userId: UserId)
}