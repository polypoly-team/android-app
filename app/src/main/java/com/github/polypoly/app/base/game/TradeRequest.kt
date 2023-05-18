package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.menu.lobby.GameLobbyDB
import com.github.polypoly.app.network.StorableObject
import com.github.polypoly.app.utils.global.Settings
import java.util.concurrent.CompletableFuture

/**
 * A class that represent a trade request
 */
data class TradeRequest(
    val playerApplicant: Player = Player(),
    val playerReceiver: Player = Player(),
    val locationGiven: InGameLocation?,
    val locationReceived: InGameLocation?,
    val currentPlayerApplicantAcceptation: Boolean?,
    val currentPlayerReceiverAcceptation: Boolean?,
    val code: String = "defaultCode",
) : StorableObject<TradeRequest>(TradeRequest::class, Settings.DB_TRADE_REQUESTS_PATH, code) {

    override fun toDBObject(): TradeRequest {
        return this
    }

    override fun toLocalObject(dbObject: TradeRequest): CompletableFuture<StorableObject<TradeRequest>> {
        return CompletableFuture.completedFuture(dbObject)
    }

}