package com.poisonedyouth.eithertransaction.account.port

import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountId
import com.poisonedyouth.eithertransaction.user.domain.UserId

interface AccountRepository {
    fun save(account: Account): Account
    fun findById(id: AccountId): Account?
    fun deleteById(id: AccountId)
    fun deleteByUserId(userId: UserId)
    fun deleteAll()
}