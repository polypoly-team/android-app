package com.github.polypoly.app.ui.menu.lobby

import android.annotation.SuppressLint
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.network.keyExists
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_CHARACTERS
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_CODE_LENGTH
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * Generates a unique code for a game lobby
 */
class UniqueGameLobbyCodeGenerator(private val codeLength: Int = GAME_LOBBY_CODE_LENGTH) {

    fun generateUniqueGameLobbyCode(): CompletableFuture<String> {
        val code = generateCode()
        return codeIsUsed(code).thenCompose { isUsed ->
            if(isUsed) {
                generateUniqueGameLobbyCode()
            } else {
                CompletableFuture.completedFuture(code)
            }
        }
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
    private fun codeIsUsed(code: String): CompletableFuture<Boolean> {
        return remoteDB.keyExists<GameLobby>(code)
    }
}
