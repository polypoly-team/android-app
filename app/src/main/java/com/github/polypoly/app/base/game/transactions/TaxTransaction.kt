package com.github.polypoly.app.base.game.transactions

import com.github.polypoly.app.base.game.GameTransaction
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.LocationProperty

class TaxTransaction(
    transactionName: String,
    playerInvolved: Player,
    isExecuted: Boolean,
    val taxAmount: Int,
    val toPlayer: Player,
    val location: LocationProperty
): GameTransaction(transactionName, playerInvolved, isExecuted) {
    override fun executeTransaction() {
        getInvolvedPlayer().loseMoney(taxAmount)
        toPlayer.earnMoney(taxAmount)
    }

    override fun getTransactionDescription(): String {
        return "You have to pay $taxAmount to ${toPlayer.user.name} as a tax for going through ${location.name}"
    }
}