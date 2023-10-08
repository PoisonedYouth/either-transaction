package com.poisonedyouth.eithertransaction.user.service

import arrow.core.Either
import arrow.core.left
import com.poisonedyouth.eithertransaction.account.domain.Account
import com.poisonedyouth.eithertransaction.account.domain.AccountName
import com.poisonedyouth.eithertransaction.account.port.AccountRepository
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.BirthDate
import com.poisonedyouth.eithertransaction.user.domain.Email
import com.poisonedyouth.eithertransaction.user.domain.User
import com.poisonedyouth.eithertransaction.user.domain.UserName
import com.poisonedyouth.eithertransaction.user.port.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
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
            name = UserName("John Doe").shouldBeRight(),
            email = Email("john.doe@mail.com").shouldBeRight(),
            birthDate = BirthDate(
                LocalDate.parse("1990-01-01")
            ).shouldBeRight()
        )
        val existingUser = userRepository.save(user).shouldBeRight()

        val account = Account(
            userId = existingUser.id,
            name = AccountName("John Doe's Account").shouldBeRight(),
            balance = 0
        )
        val existingAccount = accountRepository.save(account).shouldBeRight()

        // when
        userService.deleteUser(existingUser.id.getIdValue().shouldBeRight())

        // then
        assertThat(userRepository.findById(existingUser.id).shouldBeRight()).isNull()
        assertThat(accountRepository.findById(existingAccount.id).shouldBeRight()).isNull()
    }

    @Test
    fun `deleteUser rolls back transaction when deletion of user fails`() {
        // given
        val user = User(
            name = UserName("John Doe").shouldBeRight(),
            email = Email("john.doe@mail.com").shouldBeRight(),
            birthDate = BirthDate(
                LocalDate.parse("1990-01-01")
            ).shouldBeRight()
        )
        val existingUser = userRepository.save(user).shouldBeRight()

        val account = Account(
            userId = existingUser.id,
            name = AccountName("John Doe's Account").shouldBeRight(),
            balance = 0
        )
        val existingAccount = accountRepository.save(account).shouldBeRight()

        doReturn(Failure.GenericFailure(RuntimeException("Could not delete user")).left()).whenever(userRepository)
            .deleteById(existingUser.id)

        // when
        assertThatThrownBy {
            userService.deleteUser(existingUser.id.getIdValue().shouldBeRight())
        }.isInstanceOf(RuntimeException::class.java)

        // then
        assertThat(userRepository.findById(existingUser.id).shouldBeRight()).isNotNull()
        assertThat(accountRepository.findById(existingAccount.id).shouldBeRight()).isNotNull()
    }

    @Test
    fun `deleteUser rolls back transaction when deletion of account fails`() {
        // given
        val user = User(
            name = UserName("John Doe").shouldBeRight(),
            email = Email("john.doe@mail.com").shouldBeRight(),
            birthDate = BirthDate(
                LocalDate.parse("1990-01-01")
            ).shouldBeRight()
        )
        val existingUser = userRepository.save(user).shouldBeRight()

        val account = Account(
            userId = existingUser.id,
            name = AccountName("John Doe's Account").shouldBeRight(),
            balance = 0
        )
        val existingAccount = accountRepository.save(account).shouldBeRight()

        doReturn(Failure.GenericFailure(RuntimeException("Could not delete user")).left()).whenever(accountRepository)
            .deleteByUserId(existingUser.id)

        // when
        assertThatThrownBy {
            userService.deleteUser(existingUser.id.getIdValue().shouldBeRight())
        }.isInstanceOf(RuntimeException::class.java)

        // then
        assertThat(userRepository.findById(existingUser.id).shouldBeRight()).isNotNull()
        assertThat(accountRepository.findById(existingAccount.id).shouldBeRight()).isNotNull()
    }
}

private fun <A, B> Either<A, B>.shouldBeRight(): B =
    when (this) {
        is Either.Right -> this.value
        else -> fail("Expected Either.Right, but found: $this")
    }
