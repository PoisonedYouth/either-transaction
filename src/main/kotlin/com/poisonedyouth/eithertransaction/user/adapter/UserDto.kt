package com.poisonedyouth.eithertransaction.user.adapter

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.User

data class NewUserDto(
    val name: String,
    val email: String,
    val birthDate: String
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val birthDate: String
)

fun User.toUserDto(): Either<Failure, UserDto> = either {
    UserDto(
        id = this@toUserDto.id.getIdValue().bind(),
        name = this@toUserDto.name.value,
        email = this@toUserDto.email.value,
        birthDate = this@toUserDto.birthDate.value.toString()
    )
}