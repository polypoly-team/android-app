package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.network.StorableObject
import com.github.polypoly.app.utils.global.Settings
import java.util.concurrent.CompletableFuture

/**
 * A class that represent a trade request
 * @param playerApplicant The player that send the request
 * @param playerReceiver The player that receive the request
 * @param locationGiven The location that the applicant want to give
 * @param locationReceived The location that the receiver want to give
 * @param currentPlayerApplicantAcceptance if the applicant accept or not the trade request (null if not answered yet)
 * @param currentPlayerReceiverAcceptance if the receiver accept or not the trade request (null if not answered yet)
 */
data class TradeRequest(
    val playerApplicant: Player = Player(),
    val playerReceiver: Player = Player(),
    val locationGiven: InGameLocation? = null,
    var locationReceived: InGameLocation? = null,
    var currentPlayerApplicantAcceptance: Boolean? = null,
    var currentPlayerReceiverAcceptance: Boolean? = null,
    val code: String = "defaultCode",
) : StorableObject<TradeRequest>(TradeRequest::class, Settings.DB_TRADE_REQUESTS_PATH, code) {

    override fun toDBObject(): TradeRequest {
        return TradeRequest(playerApplicant, playerReceiver, locationGiven, locationReceived, currentPlayerApplicantAcceptance, currentPlayerReceiverAcceptance, code)
    }

    override fun toLocalObject(dbObject: TradeRequest): CompletableFuture<StorableObject<TradeRequest>> {
        return CompletableFuture.completedFuture(dbObject)
    }

    /**
     * Test if the trade request is accepted by both players
     * @return true if the trade request is accepted by both players, false if a player refuse
     * the trade request and null if the trade request is not answered yet
     */
    fun isAccepted(): Boolean? {
        if(currentPlayerApplicantAcceptance == false || currentPlayerReceiverAcceptance == false)
            return false
        if(currentPlayerApplicantAcceptance == true && currentPlayerReceiverAcceptance == true)
            return true
        return null
    }

    /**
     * This function is used to know if the player is the receiver of the trade
     */
    fun isReceiver(player: Player): Boolean {
        return playerReceiver.user.id == player.user.id
    }

    /**
     * This function is used to know if the player is the applicant of the trade
     */
    fun isApplicant(player: Player): Boolean {
        return playerApplicant.user.id == player.user.id
    }

}