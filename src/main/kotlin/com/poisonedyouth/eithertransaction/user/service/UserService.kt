package com.poisonedyouth.eithertransaction.user.service

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.eithertransaction.account.port.AccountUseCase
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.BirthDate
import com.poisonedyouth.eithertransaction.user.domain.Email
import com.poisonedyouth.eithertransaction.user.domain.IntUserId
import com.poisonedyouth.eithertransaction.user.domain.User
import com.poisonedyouth.eithertransaction.user.domain.UserName
import com.poisonedyouth.eithertransaction.user.port.UserRepository
import com.poisonedyouth.eithertransaction.user.port.UserUseCase
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository,
    private val accountUseCase: AccountUseCase
) : UserUseCase {
    override fun createUser(name: String, email: String, birthDate: String): Either<Failure, User> = either {
        val user = User(
            name = UserName(name).bind(),
            email = Email(email).bind(),
            birthDate = BirthDate(LocalDate.parse(birthDate)).bind()
        )
        userRepository.save(user).bind()
    }

    override fun getUser(userid: Int): Either<Failure, User?> = either {
        userRepository.findById(IntUserId(userid).bind()).bind()
    }

    override fun deleteUser(userid: Int): Either<Failure, Unit> = either {
        transaction {
            val user = userRepository.findById(IntUserId(userid).bind()).bind()
            if (user != null) {
                accountUseCase.deleteAccounts(user.id)
                userRepository.deleteById(user.id)
            }
        }
    }
}