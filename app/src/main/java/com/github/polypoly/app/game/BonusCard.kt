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
        // TODO: implement this method
        throw NotImplementedError("Not implemented yet")
    }

    companion object {
        /**
         * The list of all bonus cards possible to collect during a game
         */
        val allBonusCards: List<BonusCard> = listOf(
            BonusCard(0, title = "bonus card 0", description = "description 0"),
            BonusCard(1, title = "bonus card 1", description = "description 1"),
            BonusCard(2, title = "bonus card 2", description = "description 2"),
        )
    }
}