package com.poisonedyouth.eithertransaction.account.adapter

import arrow.core.Either
import com.poisonedyouth.eithertransaction.account.port.AccountUseCase
import com.poisonedyouth.eithertransaction.common.respondFailure
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AccountController(
    private val accountUseCase: AccountUseCase
) {
    private val logger: Logger = LoggerFactory.getLogger(AccountController::class.java)

    @PostMapping("/account")
    fun createNewAccount(@RequestBody accountDto: NewAccountDto): ResponseEntity<Any> {
        return when (val accountResult = accountUseCase.createAccount(
            name = accountDto.name,
            userId = accountDto.userId
        )) {
            is Either.Left -> {
                logger.error("Failed to create account '$accountDto'")
                accountResult.value.respondFailure()
            }

            is Either.Right -> {
                when (val accountDtoResult = accountResult.value.toAccountDto()) {
                    is Either.Left -> {
                        accountDtoResult.value.respondFailure()
                    }

                    is Either.Right -> {
                        logger.info("Successfully created account '$accountDto'")
                        ResponseEntity(accountDtoResult, HttpStatus.CREATED)
                    }
                }
            }

        }
    }
}