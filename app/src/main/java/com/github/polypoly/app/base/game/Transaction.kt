package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.menu.lobby.GameMode

/**
 * Represent the different transactions that can happen in a round
 */
enum class Transaction(private val title : String, private val duringRound: Boolean, private val gameModes : List<GameMode>) {
    TAX_TO_PAY ("Taxes to pay:", false, listOf(GameMode.RICHEST_PLAYER, GameMode.LAST_STANDING)),
    TAX_TO_RECEIVE("You taxed players: ", false, listOf(GameMode.RICHEST_PLAYER, GameMode.LAST_STANDING)),
    MILESTONES_INCOME_TO_RECEIVE("Milestones reward: ", false, GameMode.values().toList()),
    PASSIVE_LANDLORD_TAX_PAID("Passive taxes to pay: ", true, listOf(GameMode.LANDLORD)),
    PASSIVE_LANDLORD_TAX_RECEIVED("You taxed passively players: ", true, listOf(GameMode.LANDLORD)),
    TRADE_MONEY_PAID("Spent in trades: ", true, GameMode.values().toList()),
    TRADE_MONEY_RECEIVED("Earned in trades: ", true, GameMode.values().toList()),
    BUILDING_BOUGHT_PAID("Spent in buildings: ", true, GameMode.values().toList());

    private val amount : Float = 0.0f

    fun getAmount() : Float {
        return amount
    }

    /**
     * This function is used to add or substract an amount to the transaction
     */
    fun addAmount(amount : Float) : Float {
        return this.amount + amount
    }

    fun getTitle() : String {
        return title
    }

    /**
     * This function is used to know if the transaction is during a round or not
     * In other words, if the transaction already happened or should be taken into account when the round ends
     */
    fun isDuringRound() : Boolean {
        return duringRound
    }

    /**
     * This function is used to know if the transaction is compatible with the game mode
     */
    fun isGameModeCompatible(gameMode: GameMode) : Boolean {
        return gameModes.contains(gameMode)
    }

    override fun toString(): String {
        return "$title$amount $"
    }
}