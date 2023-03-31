package com.github.polypoly.app.game

import android.os.Build
import androidx.annotation.RequiresApi

class Skin(
    val idHead: Int = 0,
    val idBody: Int = 0,
    val idLegs: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        return other is Skin &&
            idHead == other.idHead && idBody == other.idBody && idLegs == other.idLegs
    }
}
class Stats constructor(
    val accountCreation: Int = 0, //> TODO improve unix-representation to something better
    val lastConnection: Int = 0,
    val numberOfWins: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        return other is Stats &&
                accountCreation == other.accountCreation && lastConnection == other.lastConnection &&
                numberOfWins == other.numberOfWins
    }
}

/**
 * Stub implementation of a User
 */
class User constructor(
    val id: Long = 0,
    val name: String = "no-name",
    val bio: String = "no-bio",
    val skin: Skin = Skin(),
    val stats: Stats = Stats()
){
    override fun toString(): String {
        return "User{$id: $name}"
    }

    override fun equals(other: Any?): Boolean {
        return other is User &&
                id == other.id && name == other.name && bio == other.bio &&
                skin == other.skin && stats == other.stats
    }
}