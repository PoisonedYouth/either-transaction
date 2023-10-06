package com.poisonedyouth.eithertransaction.account.adapter

import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountId
import com.poisonedyouth.eithertransaction.account.domain.AccountName
import com.poisonedyouth.eithertransaction.account.domain.EmptyAccountId
import com.poisonedyouth.eithertransaction.account.domain.IntAccountId
import com.poisonedyouth.eithertransaction.account.port.AccountRepository
import com.poisonedyouth.eithertransaction.user.adapter.UserTable
import com.poisonedyouth.eithertransaction.user.domain.IntUserId
import com.poisonedyouth.eithertransaction.user.domain.UserId
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ExposedAccountRepository : AccountRepository {
    override fun save(account: Account): Account {
        val existingAccount = findById(account.id)
        return if (existingAccount == null) {
            val id = AccountTable.insertAndGetId {
                it[name] = account.name.value
                it[userId] = account.userId.getIdValue()
                it[balance] = account.balance
            }
            account.copy(
                id = IntAccountId(id.value)
            )
        } else {
            AccountTable.update({ AccountTable.id eq account.id.getIdValue() }) {
                it[name] = account.name.value
                it[userId] = account.userId.getIdValue()
                it[balance] = account.balance
            }
            account
        }
    }

    override fun findById(id: AccountId): Account? {
        return when (id) {
            is EmptyAccountId -> {
                null
            }

            else -> {
                AccountTable.select { AccountTable.id eq id.getIdValue() }
                    .map {
                        Account(
                            id = IntAccountId(it[AccountTable.id].value),
                            name = AccountName(it[AccountTable.name]),
                            userId = IntUserId(it[AccountTable.userId].value),
                            balance = it[AccountTable.balance]
                        )
                    }.firstOrNull()
            }
        }
    }

    override fun deleteById(id: AccountId) {
        AccountTable.deleteWhere { AccountTable.id eq id.getIdValue() }
    }

    override fun deleteByUserId(userId: UserId) {
        AccountTable.deleteWhere { AccountTable.userId eq userId.getIdValue() }
    }

    override fun deleteAll() {
        AccountTable.deleteAll()
    }
}

object AccountTable : IntIdTable("account") {
    val name = varchar("name", 50)
    val userId = reference("user_id", UserTable)
    val balance = integer("balance")
}