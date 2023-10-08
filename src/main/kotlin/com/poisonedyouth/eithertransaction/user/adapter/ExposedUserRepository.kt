package com.poisonedyouth.eithertransaction.user.adapter

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.common.eval
import com.poisonedyouth.eithertransaction.user.domain.BirthDate
import com.poisonedyouth.eithertransaction.user.domain.Email
import com.poisonedyouth.eithertransaction.user.domain.EmptyUserId
import com.poisonedyouth.eithertransaction.user.domain.IntUserId
import com.poisonedyouth.eithertransaction.user.domain.User
import com.poisonedyouth.eithertransaction.user.domain.UserId
import com.poisonedyouth.eithertransaction.user.domain.UserName
import com.poisonedyouth.eithertransaction.user.port.UserRepository
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ExposedUserRepository : UserRepository {
    override fun save(user: User): Either<Failure, User> = either {
        val existingUser = findById(user.id).bind()
        eval {
            if (existingUser == null) {
                val id = UserTable.insertAndGetId {
                    it[name] = user.name.value
                    it[email] = user.email.value
                    it[birthDate] = user.birthDate.value
                }
                user.copy(
                    id = IntUserId(id.value).bind()
                )
            } else {
                UserTable.update({ UserTable.id eq user.id.getIdValue().bind() }) {
                    it[name] = user.name.value
                    it[email] = user.email.value
                    it[birthDate] = user.birthDate.value
                }
                user
            }
        }.bind()
    }

    override fun findById(id: UserId): Either<Failure, User?> = either {
        when (id) {
            is EmptyUserId -> {
                null
            }

            else -> {
                eval {
                    UserTable.select { UserTable.id eq id.getIdValue().bind() }
                        .map {
                            User(
                                id = IntUserId(it[UserTable.id].value).bind(),
                                name = UserName(it[UserTable.name]).bind(),
                                email = Email(it[UserTable.email]).bind(),
                                birthDate = BirthDate(it[UserTable.birthDate]).bind()
                            )
                        }.firstOrNull()
                }.bind()
            }
        }
    }

    override fun deleteById(id: UserId): Either<Failure, Unit> = either {
        eval {
            UserTable.deleteWhere { UserTable.id eq id.getIdValue().bind() }
        }
    }

    override fun deleteAll(): Either<Failure, Unit> = eval {
        UserTable.deleteAll()
    }
}

object UserTable : IntIdTable("user") {
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val birthDate = date("birthDate")
}