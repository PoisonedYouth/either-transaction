package com.poisonedyouth.eithertransaction.user.port

import arrow.core.Either
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.adapter.UserDto
import com.poisonedyouth.eithertransaction.user.domain.User

interface UserUseCase {

    fun createUser(name: String, email: String, birthDate: String): Either<Failure, UserDto>

    fun getUser(userid: Int): Either<Failure, UserDto?>

    fun deleteUser(userid: Int): Either<Failure, Unit>
}