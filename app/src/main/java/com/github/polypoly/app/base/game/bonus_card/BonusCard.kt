package com.github.polypoly.app.base.game.bonus_card

import com.github.polypoly.app.base.game.Player

/**
 * A class that represent a bonus card
 * @property id The id of the [BonusCard]
 * @property title The title of the [BonusCard]
 * @property description The description of the [BonusCard]
 */
enum class BonusCard(
    val id: Long,
    val title: String,
    val description: String,
    val effect: (Player) -> Unit,
) {

    TEACHING_ASSISTANT_PAYDAY(0, title = "Teaching assistant payday", description = "You finally got your student assistant salary you've been waiting for after spending hours helping BA1s debug their code. Collect 100CHF!",
        { player -> player.earnMoney(100) }),
    TUITION_FEES_TO_PAY(1, title = "Tuition fees to pay!", description = "It's time to pay your tuition fees: you have passed the is academia deadline for far too long... Lose 100CHF.",
        { player -> player.loseMoney(100) });
    // TODO : implement other bonus cards
}