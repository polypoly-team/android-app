package com.github.polypoly.app.utils

import android.annotation.SuppressLint
import android.provider.Settings.System.getString
import com.github.polypoly.app.R
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_CODE_LENGTH
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MAX_CHARACTERS
import com.github.polypoly.app.utils.global.GlobalInstances
import com.github.polypoly.app.utils.global.Settings
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * Generates a unique code for a game lobby
 */
class UniqueCodeGenerator(private val codeLength: Int = GAME_LOBBY_CODE_LENGTH) {

    fun generateUniqueCode(): String {
        var code: String
        do {
            code = generateCode()
        } while (!codeIsValid(code))
        return code
    }

    /**
     * Generates a random code of length [codeLength]
     */
    private fun generateCode(): String {
        val digits = GAME_LOBBY_MAX_CHARACTERS
        val sb = StringBuilder(codeLength)
        for (i in 0 until codeLength) {
            sb.append(digits[Random.nextInt(digits.length)])
        }
        return sb.toString()
    }

    /**
     * Checks if the code is valid by checking if it is not already in the database
     */
    @SuppressLint("NewApi")
    private fun codeIsValid(code: String): Boolean {
        var codeIsValid = true
        val lobbyKey = Settings.DB_GAME_LOBBIES_PATH + code
        GlobalInstances.remoteDB.keyExists(lobbyKey).thenCompose { keyExists ->
            CompletableFuture.completedFuture(keyExists)
        }.thenAccept { codeIsValid = !it}

        return codeIsValid
    }
}
