package com.poisonedyouth.eithertransaction.user.port

import com.poisonedyouth.eithertransaction.user.domain.User
import com.poisonedyouth.eithertransaction.user.domain.UserId

interface UserRepository {
    fun save(user: User): User
    fun findById(id: UserId): User?
    fun deleteById(id: UserId)
    fun deleteAll()
}