package com.github.polypoly.app.game

/**
 * A class that represent a bonus card
 * @property id The id of the [BonusCard]
 * @property title The title of the [BonusCard]
 * @property description The description of the [BonusCard]
 */
data class BonusCard(
    val id: Long,
    val title: String,
    val description: String,
) {

    /**
     * Apply the bonus of the [BonusCard] to the [Player]
     * @param player the [Player] to apply the bonus to
     */
    fun applyBonus(player: Player) {
        when(id) {
            0L -> {
                player.earnMoney(100)
            }
            1L -> {
                player.loseMoney(100)
            }
            // TODO : implement other bonus cards
        }
    }

    companion object {
        /**
         * The list of all [BonusCard]s possible to collect during a [Game]
         */
        val allBonusCards: List<BonusCard> = listOf(
            BonusCard(0, title = "Teaching assistant payday", description = "You finally got your student assistant salary you've been waiting for after spending hours helping BA1s debug their code. Collect 100CHF!"),
            BonusCard(1, title = "Tuition fees to pay!", description = "It's time to pay your tuition fees: you have passed the is academia deadline for far too long... Lose 100CHF."),
        )
    }
}