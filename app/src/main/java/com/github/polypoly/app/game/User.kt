package com.github.polypoly.app.game

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

class Skin(
    val idHead: Int = 0,
    val idBody: Int = 0,
    val idLegs: Int = 0
)
class Stats constructor(
    val accountCreation: LocalDateTime = LocalDateTime.MIN,
    val lastConnection: LocalDateTime = LocalDateTime.MIN,
    val numberOfWins: Int = 0
)

/**
 * Stub implementation of a User
 */
class User(
    val id: Long = 0,
    val name: String = "no-name",
    val bio: String = "no-bio",
    val skin: Skin = Skin(),
    val stats: Stats = Stats()
){
    override fun toString(): String {
        return "User{$id: $name}"
    }
}