package com.poisonedyouth.eithertransaction.account.port

import arrow.core.Either
import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.UserId

interface AccountUseCase {
    fun createAccount(name: String, userId: Int): Either<Failure, Account>
    fun getAccount(accountId: Int): Either<Failure, Account?>
    fun deleteAccounts(userId: UserId): Either<Failure, Unit>
}