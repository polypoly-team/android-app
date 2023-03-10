package com.github.polypoly.app.game

/**
 * Stub implementation of a User
 */
class User(
    val name: String,
    val id: Long
){
    override fun toString(): String {
        return "User{$name.$id}"
    }
}