package com.github.polypoly.app.game

/**
 * Stub implementation of a User
 */
class User(
    private val name: String,
    private val id: Long
){
    fun getName(): String {
        return name;
    }

    fun getId(): Long {
        return id;
    }

    override fun toString(): String {
        return "User{$name.$id}"
    }
}