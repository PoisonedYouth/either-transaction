package com.poisonedyouth.eithertransaction.user.adapter

import arrow.core.Either
import com.poisonedyouth.eithertransaction.common.respondFailure
import com.poisonedyouth.eithertransaction.user.port.UserUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userUseCase: UserUseCase
) {
    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/user")
    fun createNewUser(@RequestBody userDto: NewUserDto): ResponseEntity<Any> {
        val userResult = userUseCase.createUser(
            name = userDto.name,
            email = userDto.email,
            birthDate = userDto.birthDate
        )
        return when (userResult) {
            is Either.Left -> {
                logger.error("Failed to create user '$userDto'")
                userResult.value.respondFailure()
            }

            is Either.Right -> {
                logger.info("Successfully created user '$userDto'")
                ResponseEntity(userResult.value, HttpStatus.CREATED)

            }
        }
    }

    @GetMapping("/user")
    fun getUser(@RequestParam userId: Int): ResponseEntity<Any> {
        return when (val userResult = userUseCase.getUser(userId)) {
            is Either.Left -> {
                logger.error("Failed to get user with id '$userId'")
                userResult.value.respondFailure()
            }

            is Either.Right -> {
                if (userResult.value != null) {
                    logger.info("Successfully got user with id '$userId'")
                    ResponseEntity(userResult.value, HttpStatus.OK)
                } else {
                    logger.info("No user with id '$userId' exist.")
                    ResponseEntity(HttpStatus.NOT_FOUND)
                }
            }

        }
    }

    @DeleteMapping("/user")
    fun deleteUser(@RequestParam userId: Int): ResponseEntity<Any> {
        return when (val userResult = userUseCase.deleteUser(userId)) {
            is Either.Left -> {
                logger.error("Failed to delete user with id '$userId'")
                userResult.value.respondFailure()
            }

            is Either.Right ->
                ResponseEntity(HttpStatus.OK)
        }
    }
}