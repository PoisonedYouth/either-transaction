package com.poisonedyouth.eithertransaction.user.port

import arrow.core.Either
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.User

interface UserUseCase {

    fun createUser(name: String, email: String, birthDate: String): Either<Failure, User>

    fun getUser(userid: Int): Either<Failure, User?>

    fun deleteUser(userid: Int): Either<Failure, Unit>
}