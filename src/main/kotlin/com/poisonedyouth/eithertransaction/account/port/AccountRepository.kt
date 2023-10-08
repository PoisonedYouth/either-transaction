package com.poisonedyouth.eithertransaction.account.port

import arrow.core.Either
import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountId
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.UserId

interface AccountRepository {
    fun save(account: Account): Either<Failure, Account>
    fun findById(id: AccountId): Either<Failure, Account?>
    fun deleteById(id: AccountId): Either<Failure, Unit>
    fun deleteByUserId(userId: UserId): Either<Failure, Unit>
    fun deleteAll(): Either<Failure, Unit>
}