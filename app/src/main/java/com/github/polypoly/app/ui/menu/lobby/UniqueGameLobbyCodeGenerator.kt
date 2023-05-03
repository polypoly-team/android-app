package com.github.polypoly.app.ui.menu.lobby

import android.annotation.SuppressLint
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_CHARACTERS
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_CODE_LENGTH
import com.github.polypoly.app.utils.global.GlobalInstances
import com.github.polypoly.app.utils.global.Settings
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * Generates a unique code for a game lobby
 */
class UniqueGameLobbyCodeGenerator(private val codeLength: Int = GAME_LOBBY_CODE_LENGTH) {

    fun generateUniqueGameLobbyCode(): String {
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
        val digits = GAME_LOBBY_CHARACTERS
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
        //TODO: DB call to fix after new DB implementation
        var codeIsValid = true
        val lobbyKey = Settings.DB_GAME_LOBBIES_PATH + code
        GlobalInstances.remoteDB.keyExists(lobbyKey).thenCompose { keyExists ->
            CompletableFuture.completedFuture(keyExists)
        }.thenAccept { codeIsValid = !it}

        return codeIsValid
    }
}
