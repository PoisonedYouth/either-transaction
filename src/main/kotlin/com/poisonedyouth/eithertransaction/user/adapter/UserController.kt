package com.poisonedyouth.eithertransaction.user.adapter

import com.poisonedyouth.eithertransaction.common.toResponseEntity
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
        return try {
            val createdUser = userUseCase.createUser(
                name = userDto.name,
                email = userDto.email,
                birthDate = userDto.birthDate
            )
            ResponseEntity(createdUser.toUserDto(), HttpStatus.CREATED)
        } catch (e: Exception) {
            logger.error("Failed to create user '$userDto'", e)
            e.toResponseEntity()
        }

    }

    @GetMapping("/user")
    fun getUser(@RequestParam userId: Int): ResponseEntity<Any> {
        return try {
            val user = userUseCase.getUser(userId)
            ResponseEntity(user?.toUserDto(), HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to get user with id '$userId'", e)
            e.toResponseEntity()
        }
    }

    @DeleteMapping("/user")
    fun deleteUser(@RequestParam userId: Int): ResponseEntity<Any> {
        return try {
            userUseCase.deleteUser(userId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to delete user with id '$userId'", e)
            e.toResponseEntity()
        }
    }
}