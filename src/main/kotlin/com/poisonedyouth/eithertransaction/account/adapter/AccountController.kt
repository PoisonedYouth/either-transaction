package com.poisonedyouth.eithertransaction.account.adapter

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.identity
import arrow.core.raise.either
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
    fun createNewAccount(@RequestBody accountDto: NewAccountDto): ResponseEntity<out Any> =
        accountUseCase.createAccount(
            name = accountDto.name,
            userId = accountDto.userId
        ).onLeft { logger.error("Failed to create account '$accountDto'") }
            .flatMap { it.toAccountDto() }
            .fold(
                ifLeft = {
                    logger.error("error $it")
                    it.respondFailure()
                },
                ifRight = {
                    ResponseEntity(it, HttpStatus.CREATED)
                })

    @PostMapping("/account2")
    fun createNewAccount2(@RequestBody accountDto: NewAccountDto): ResponseEntity<out Any> =
        either{
            val accountResult  = accountUseCase.createAccount(
            name = accountDto.name,
            userId = accountDto.userId
            ).bind()
             accountResult.toAccountDto().bind()
        } .fold(
            ifLeft = {
                logger.error("error $it")
                it.respondFailure()
            },
            ifRight = {
                ResponseEntity(it, HttpStatus.CREATED)
            })

}
