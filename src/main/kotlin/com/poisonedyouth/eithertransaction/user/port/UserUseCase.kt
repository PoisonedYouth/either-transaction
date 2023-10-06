package com.poisonedyouth.eithertransaction.user.port

import com.poisonedyouth.eithertransaction.user.domain.User

interface UserUseCase {

    fun createUser(name: String, email: String, birthDate: String): User

    fun getUser(userid: Int): User?

    fun deleteUser(userid: Int)
}