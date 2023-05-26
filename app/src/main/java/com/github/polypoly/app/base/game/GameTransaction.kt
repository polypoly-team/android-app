package com.github.polypoly.app.base.game

/**
 * A class that represent a transaction in the game
 * @param transactionName The name of the transaction
 * @param playerInvolved The player involved in the transaction
 * @param isExecuted If the transaction has been executed or not
 */
abstract class GameTransaction(
    private val transactionName : String = "Default Transaction",
    private val playerInvolved : Player = Player(),
    private var isExecuted : Boolean = false
    ) {

    /**
     * launches the transaction's execution
     */
    fun execute(){
        if(!isExecuted){
            executeTransaction()
            isExecuted = true
        } else {
            throw IllegalStateException("Transaction already done")
        }
    }

    fun isExecuted() : Boolean{
        return isExecuted
    }

    fun getName() : String{
        return transactionName
    }

    fun getInvolvedPlayer() : Player{
        return playerInvolved
    }

    /**
     * Executes the transaction
     */
    abstract fun executeTransaction()

    /**
     * Get the description of the transaction destined for the UI
     */
    abstract fun getTransactionDescription(): String
}