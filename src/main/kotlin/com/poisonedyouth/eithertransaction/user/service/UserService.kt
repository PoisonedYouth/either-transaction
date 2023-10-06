package com.poisonedyouth.eithertransaction.user.service

import com.poisonedyouth.eithertransaction.account.port.AccountUseCase
import com.poisonedyouth.eithertransaction.user.domain.BirthDate
import com.poisonedyouth.eithertransaction.user.domain.Email
import com.poisonedyouth.eithertransaction.user.domain.IntUserId
import com.poisonedyouth.eithertransaction.user.domain.User
import com.poisonedyouth.eithertransaction.user.domain.UserName
import com.poisonedyouth.eithertransaction.user.port.UserRepository
import com.poisonedyouth.eithertransaction.user.port.UserUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository,
    private val accountUseCase: AccountUseCase
) : UserUseCase {
    override fun createUser(name: String, email: String, birthDate: String): User {
        val user = User(
            name = UserName(name),
            email = Email(email),
            birthDate = BirthDate(LocalDate.parse(birthDate))
        )
        return userRepository.save(user)
    }

    override fun getUser(userid: Int): User? {
        return userRepository.findById(IntUserId(userid))
    }

    @Transactional
    override fun deleteUser(userid: Int) {
        val user = userRepository.findById(IntUserId(userid))
        if (user != null) {
            accountUseCase.deleteAccounts(user.id)
            userRepository.deleteById(user.id)
        }
    }
}