package com.github.polypoly.app.game

/**
 * A class that represent a bonus card
 */
data class BonusCard(
    /**
     * The id of the bonus card
     */
    val id: Long,

    /**
     * The title of the bonus card
     */
    val title: String,

    /**
     * The description of the bonus card
     */
    val description: String,
) {

    /**
     * Apply the bonus of the bonus card to the player
     * @param player the player to apply the bonus to
     */
    fun applyBonus(player: Player) {
        when(id) {
            0L -> {
                player.winMoney(100)
            }
            1L -> {
                player.loseMoney(100)
            }
            // TODO : implement other bonus cards
        }
    }

    companion object {
        /**
         * The list of all bonus cards possible to collect during a game
         */
        val allBonusCards: List<BonusCard> = listOf(
            BonusCard(0, title = "Teaching assistant payday", description = "You finally got your student assistant salary you've been waiting for after spending hours helping BA1s debug their code. Collect 100CHF!"),
            BonusCard(1, title = "Tuition fees to pay!", description = "It's time to pay your tuition fees: you have passed the is academia deadline for far too long... Lose 100CHF."),
        )
    }
}