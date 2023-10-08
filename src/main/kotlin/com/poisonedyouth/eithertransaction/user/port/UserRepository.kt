package com.poisonedyouth.eithertransaction.user.port

import arrow.core.Either
import com.poisonedyouth.eithertransaction.common.Failure
import com.poisonedyouth.eithertransaction.user.domain.User
import com.poisonedyouth.eithertransaction.user.domain.UserId

interface UserRepository {
    fun save(user: User): Either<Failure, User>
    fun findById(id: UserId): Either<Failure, User?>
    fun deleteById(id: UserId): Either<Failure, Unit>
    fun deleteAll(): Either<Failure, Unit>
}