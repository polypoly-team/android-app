package com.github.polypoly.app.game

import java.time.LocalDateTime

class Skin(
    val idHead: Int? = null,
    val idBody: Int? = null,
    val idLegs: Int? = null
)

class Stats(
//    TODO(Add several other stats)
//    val accountCreation: LocalDateTime? = null,
//    val lastConnection: LocalDateTime? = null,
    val numberOfWins: Int? = null
)

/**
 * Stub implementation of a User
 */
class User(
    val id: Long? = null,
    val name: String? = null,
    val bio: String? = null,
    val skin: Skin? = null,
    val stats: Stats? = null
){
    override fun toString(): String {
        return "User{$id: $name}"
    }
}