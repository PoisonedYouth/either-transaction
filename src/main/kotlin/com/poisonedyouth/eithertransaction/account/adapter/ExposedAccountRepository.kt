package com.poisonedyouth.eithertransaction.account.adapter

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountId
import com.poisonedyouth.eithertransaction.account.domain.AccountName
import com.poisonedyouth.eithertransaction.account.domain.EmptyAccountId
import com.poisonedyouth.eithertransaction.account.domain.IntAccountId
import com.poisonedyouth.eithertransaction.account.port.AccountRepository
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.common.eval
import com.poisonedyouth.eithertransaction.user.adapter.UserTable
import com.poisonedyouth.eithertransaction.user.domain.IntUserId
import com.poisonedyouth.eithertransaction.user.domain.UserId
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class ExposedAccountRepository : AccountRepository {
    override fun save(account: Account): Either<Failure, Account> = either {
        val existingAccount = findById(account.id).bind()
        eval {
            if (existingAccount == null) {
                val id = transaction {
                    AccountTable.insertAndGetId {
                        it[name] = account.name.value
                        it[userId] = account.userId.getIdValue().bind()
                        it[balance] = account.balance
                    }
                }
                account.copy(
                    id = IntAccountId(id.value).bind()
                )
            } else {
                transaction {
                    AccountTable.update({ AccountTable.id eq account.id.getIdValue().bind() }) {
                        it[name] = account.name.value
                        it[userId] = account.userId.getIdValue().bind()
                        it[balance] = account.balance
                    }
                }
                account
            }
        }.bind()
    }

    override fun findById(id: AccountId): Either<Failure, Account?> = either {
        when (id) {
            is EmptyAccountId -> {
                null
            }

            else -> {
                eval {
                    transaction {
                        AccountTable.select { AccountTable.id eq id.getIdValue().bind() }
                            .map {
                                Account(
                                    id = IntAccountId(it[AccountTable.id].value).bind(),
                                    name = AccountName(it[AccountTable.name]).bind(),
                                    userId = IntUserId(it[AccountTable.userId].value).bind(),
                                    balance = it[AccountTable.balance]
                                )
                            }.firstOrNull()
                    }
                }.bind()
            }
        }
    }

    override fun deleteById(id: AccountId): Either<Failure, Unit> = either {
        eval {
            transaction {
                AccountTable.deleteWhere { AccountTable.id eq id.getIdValue().bind() }
            }
        }
    }

    override fun deleteByUserId(userId: UserId): Either<Failure, Unit> = either {
        eval {
            transaction {
                AccountTable.deleteWhere { AccountTable.userId eq userId.getIdValue().bind() }
            }
        }.bind()
    }

    override fun deleteAll(): Either<Failure, Unit> = eval {
        AccountTable.deleteAll()
    }
}

object AccountTable : IntIdTable("account") {
    val name = varchar("name", 50)
    val userId = reference("user_id", UserTable)
    val balance = integer("balance")
}