package com.poisonedyouth.eithertransaction.user.adapter

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
    override fun save(user: User): User {
        val existingUser = findById(user.id)
        return if (existingUser == null) {
            val id = UserTable.insertAndGetId {
                it[name] = user.name.value
                it[email] = user.email.value
                it[birthDate] = user.birthDate.value
            }
            user.copy(
                id = IntUserId(id.value)
            )
        } else {
            UserTable.update({ UserTable.id eq user.id.getIdValue() }) {
                it[name] = user.name.value
                it[email] = user.email.value
                it[birthDate] = user.birthDate.value
            }
            user
        }
    }

    override fun findById(id: UserId): User? {
        return when (id) {
            is EmptyUserId -> {
                null
            }

            else -> {
                UserTable.select { UserTable.id eq id.getIdValue() }
                    .map {
                        User(
                            id = IntUserId(it[UserTable.id].value),
                            name = UserName(it[UserTable.name]),
                            email = Email(it[UserTable.email]),
                            birthDate = BirthDate(it[UserTable.birthDate])
                        )
                    }.firstOrNull()
            }
        }
    }

    override fun deleteById(id: UserId) {
        UserTable.deleteWhere { UserTable.id eq id.getIdValue() }
    }

    override fun deleteAll() {
        UserTable.deleteAll()
    }
}

object UserTable : IntIdTable("user") {
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val birthDate = date("birthDate")
}