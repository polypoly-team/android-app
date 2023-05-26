package com.github.polypoly.app.base.menu

import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_BASICS_TEXT
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_BASICS_TITLE
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_DISTANCE_INCOME_TEXT
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_DISTANCE_INCOME_TITLE
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_DURING_ROUND_TEXT
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_DURING_ROUND_TITLE
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_LANDLORD_TEXT
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_LANDLORD_TITLE
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_LAST_STANDING_TEXT
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_LAST_STANDING_TITLE
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_RICHEST_PLAYER_TEXT
import com.github.polypoly.app.ui.menu.RulesStrings.Companion.RULES_RICHEST_PLAYER_TITLE

/**
 * This object stores all the rules of the game.
 * Each small part of the rules is represented by the data class RulesChapter
 */
object RulesObject {
    const val rulesTitle = "polypoly, the rules"
    val rulesChapters : List<RulesChapter> = listOf(
        // Mock chapters
        RulesChapter(RULES_BASICS_TITLE, RULES_BASICS_TEXT),
        RulesChapter(RULES_RICHEST_PLAYER_TITLE, RULES_RICHEST_PLAYER_TEXT),
        RulesChapter(RULES_LAST_STANDING_TITLE, RULES_LAST_STANDING_TEXT),
        RulesChapter(RULES_LANDLORD_TITLE, RULES_LANDLORD_TEXT),
        RulesChapter(RULES_DURING_ROUND_TITLE, RULES_DURING_ROUND_TEXT),
        RulesChapter(RULES_DISTANCE_INCOME_TITLE, RULES_DISTANCE_INCOME_TEXT)
    )
}

/**
 * A chapter of the rules
 */
data class RulesChapter(val title: String, val content: String)