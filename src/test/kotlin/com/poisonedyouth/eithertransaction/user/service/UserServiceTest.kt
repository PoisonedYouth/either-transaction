package com.poisonedyouth.eithertransaction.user.service

import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountName
import com.poisonedyouth.eithertransaction.account.port.AccountRepository
import com.poisonedyouth.eithertransaction.user.domain.BirthDate
import com.poisonedyouth.eithertransaction.user.domain.Email
import com.poisonedyouth.eithertransaction.user.domain.User
import com.poisonedyouth.eithertransaction.user.domain.UserName
import com.poisonedyouth.eithertransaction.user.port.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import java.time.LocalDate

@SpringBootTest
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    @SpyBean
    private lateinit var userRepository: UserRepository

    @SpyBean
    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun cleanDatabase() {
        accountRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `deleteUser also deletes all accounts of user`() {
        // given
        val user = User(
            name = UserName("John Doe"),
            email = Email("john.doe@mail.com"),
            birthDate = BirthDate(
                LocalDate.parse("1990-01-01")
            )
        )
        val existingUser = userRepository.save(user)

        val account = Account(
            userId = existingUser.id,
            name = AccountName("John Doe's Account"),
            balance = 0
        )
        val existingAccount = accountRepository.save(account)

        // when
        userService.deleteUser(existingUser.id.getIdValue())

        // then
        assertThat(userRepository.findById(existingUser.id)).isNull()
        assertThat(accountRepository.findById(existingAccount.id)).isNull()
    }

    @Test
    fun `deleteUser rolls back transaction when deletion of user fails`() {
        // given
        val user = User(
            name = UserName("John Doe"),
            email = Email("john.doe@mail.com"),
            birthDate = BirthDate(
                LocalDate.parse("1990-01-01")
            )
        )
        val existingUser = userRepository.save(user)

        val account = Account(
            userId = existingUser.id,
            name = AccountName("John Doe's Account"),
            balance = 0
        )
        val existingAccount = accountRepository.save(account)

        doThrow(RuntimeException("Could not delete user")).whenever(userRepository).deleteById(existingUser.id)

        // when
        assertThatThrownBy {
            userService.deleteUser(existingUser.id.getIdValue())
        }.isInstanceOf(RuntimeException::class.java)

        // then
        assertThat(userRepository.findById(existingUser.id)).isNotNull()
        assertThat(accountRepository.findById(existingAccount.id)).isNotNull()
    }

    @Test
    fun `deleteUser rolls back transaction when deletion of account fails`() {
        // given
        val user = User(
            name = UserName("John Doe"),
            email = Email("john.doe@mail.com"),
            birthDate = BirthDate(
                LocalDate.parse("1990-01-01")
            )
        )
        val existingUser = userRepository.save(user)

        val account = Account(
            userId = existingUser.id,
            name = AccountName("John Doe's Account"),
            balance = 0
        )
        val existingAccount = accountRepository.save(account)

        doThrow(RuntimeException("Could not delete user")).whenever(accountRepository).deleteByUserId(existingUser.id)

        // when
        assertThatThrownBy {
            userService.deleteUser(existingUser.id.getIdValue())
        }.isInstanceOf(RuntimeException::class.java)

        // then
        assertThat(userRepository.findById(existingUser.id)).isNotNull()
        assertThat(accountRepository.findById(existingAccount.id)).isNotNull()
    }
}