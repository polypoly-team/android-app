package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.menu.lobby.GameLobbyDB
import com.github.polypoly.app.network.StorableObject
import com.github.polypoly.app.utils.global.Settings
import java.util.concurrent.CompletableFuture

/**
 * A class that represent a trade request
 * @param playerApplicant The player that send the request
 * @param playerReceiver The player that receive the request
 * @param locationGiven The location that the applicant want to give
 * @param locationReceived The location that the receiver want to give
 * @param currentPlayerApplicantAcceptation if the applicant accept or not the trade request (null if not answered yet)
 * @param currentPlayerReceiverAcceptation if the receiver accept or not the trade request (null if not answered yet)
 */
data class TradeRequest(
    val playerApplicant: Player = Player(),
    val playerReceiver: Player = Player(),
    val locationGiven: InGameLocation? = null,
    var locationReceived: InGameLocation? = null,
    val currentPlayerApplicantAcceptation: Boolean? = null,
    val currentPlayerReceiverAcceptation: Boolean? = null,
    val code: String = "defaultCode",
) : StorableObject<TradeRequest>(TradeRequest::class, Settings.DB_TRADE_REQUESTS_PATH, code) {

    override fun toDBObject(): TradeRequest {
        return TradeRequest(playerApplicant, playerReceiver, locationGiven, locationReceived, currentPlayerApplicantAcceptation, currentPlayerReceiverAcceptation, code)
    }

    override fun toLocalObject(dbObject: TradeRequest): CompletableFuture<StorableObject<TradeRequest>> {
        return CompletableFuture.completedFuture(dbObject)
    }

}