package com.poisonedyouth.eithertransaction.account.adapter

import com.poisonedyouth.eithertransaction.account.port.AccountUseCase
import com.poisonedyouth.eithertransaction.common.getResultOrThrow
import com.poisonedyouth.eithertransaction.common.toResponseEntity
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
        return try{
            val createdAccount = accountUseCase.createAccount(
                name = accountDto.name,
                userId = accountDto.userId
            ).getResultOrThrow()
            ResponseEntity(createdAccount.toAccountDto().getResultOrThrow(), HttpStatus.CREATED)
        } catch (e: Exception) {
            logger.error("Failed to create account '$accountDto'", e)
            e.toResponseEntity()
        }
    }
}