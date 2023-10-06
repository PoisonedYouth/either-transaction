package com.poisonedyouth.eithertransaction.user.adapter

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

fun User.toUserDto() = UserDto(
    id = this.id.getIdValue(),
    name = this.name.value,
    email = this.email.value,
    birthDate = this.birthDate.value.toString()
)