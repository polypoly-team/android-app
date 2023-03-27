package com.github.polypoly.app.game.user

/**
 * Stub implementation of a User
 */
data class User(
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