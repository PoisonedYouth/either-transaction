package com.poisonedyouth.eithertransaction.account.service

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountName
import com.poisonedyouth.eithertransaction.account.domain.IntAccountId
import com.poisonedyouth.eithertransaction.account.port.AccountRepository
import com.poisonedyouth.eithertransaction.account.port.AccountUseCase
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.IntUserId
import com.poisonedyouth.eithertransaction.user.domain.UserId
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository
) : AccountUseCase {
    override fun createAccount(name: String, userId: Int): Either<Failure, Account> = either {
        val account = Account(
            name = AccountName(name).bind(),
            userId = IntUserId(userId).bind(),
            balance = 0
        )
        accountRepository.save(account).bind()
    }

    override fun getAccount(accountId: Int): Either<Failure, Account?> = either {
        accountRepository.findById(IntAccountId(accountId).bind()).bind()
    }

    override fun deleteAccounts(userId: UserId): Either<Failure, Unit> = either {
        accountRepository.deleteByUserId(userId).bind()
    }
}