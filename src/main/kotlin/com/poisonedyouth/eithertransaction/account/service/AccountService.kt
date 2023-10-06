package com.poisonedyouth.eithertransaction.account.service

import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountName
import com.poisonedyouth.eithertransaction.account.domain.IntAccountId
import com.poisonedyouth.eithertransaction.account.port.AccountRepository
import com.poisonedyouth.eithertransaction.account.port.AccountUseCase
import com.poisonedyouth.eithertransaction.user.domain.IntUserId
import com.poisonedyouth.eithertransaction.user.domain.UserId
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository
) : AccountUseCase {
    override fun createAccount(name: String, userId: Int): Account {
        val account = Account(
            name = AccountName(name),
            userId = IntUserId(userId),
            balance = 0
        )
        return accountRepository.save(account)
    }

    override fun getAccount(accountId: Int): Account? {
        return accountRepository.findById(IntAccountId(accountId))
    }

    override fun deleteAccounts(userId: UserId) {
        accountRepository.deleteByUserId(userId)
    }
}