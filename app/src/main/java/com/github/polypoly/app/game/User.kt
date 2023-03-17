package com.github.polypoly.app.game

import java.time.LocalDateTime

class Skin(
    val idHead: Int,
    val idBody: Int,
    val idLegs: Int
)

class Stats(
    val accountCreation: LocalDateTime,
    val lastConnection: LocalDateTime,
    val numberOfWins: Int
)

/**
 * Stub implementation of a User
 */
class User(
    val id: Long,
    val name: String,
    val bio: String,
    val skin: Skin,
    val stats: Stats
){
    override fun toString(): String {
        return "User{$id: $name}"
    }
}