package com.github.polypoly.app.base.game.bonus_card

import com.github.polypoly.app.R
import com.github.polypoly.app.base.game.Player

/**
 * A class that represent a bonus card
 * @property id The id of the [BonusCard]
 * @property title The title of the [BonusCard]
 * @property description The description of the [BonusCard]
 * @property effect The effect of the [BonusCard]
 */
enum class BonusCard(
    val id: Long,
    val title: Int,
    val description: Int,
    val effect: (Player) -> Unit,
) {
    TEACHING_ASSISTANT_PAYDAY(0, title = R.string.teaching_assistant_payday_title, description = R.string.teaching_assistant_payday_description,
        { player -> player.earnMoney(100) }),
    TUITION_FEES_TO_PAY(1, title = R.string.tuition_fees_to_pay_title, description =
            R.string.tuition_fees_to_pay_description,
        { player -> player.loseMoney(100) }),
    // TODO : implement other bonus cards
}