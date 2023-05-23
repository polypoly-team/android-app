package com.github.polypoly.app.ui.menu

class RulesStrings {
    companion object {

        const val RULES_BASICS_TITLE = "Basics"
        const val RULES_BASICS_TEXT =
            "Before starting a game, the game owner must choose the following parameters:\n\n" +
                    "- The duration of each round (from minutes to weeks!)\n\n" +
                    "- The number of players (from 2 up to 8) \n\n" +
                    "- The game mode : Richest player, Last standing and Landlord!\n\n" +
                    "- The number of rounds\n\n" +
                    "- If the lobby is private or not:\n" +
                    "If the lobby is set private, the only way to join it is by the entering the lobby code. Otherwise, the lobby will show in the public lobbies list to join!\n\n" +
                    "- the initial player balance, to help buying your first buildings!.\n\n" +
                    "- The lobby name (of course!)\n\n" +
                    "To make it short, if your money goes below 0 you will be bankrupt and you will lose the game!\n\n"

        const val RULES_RICHEST_PLAYER_TITLE = "Richest player mode"
        const val RULES_RICHEST_PLAYER_TEXT =
            "In this mode, the winner is the player that end up with the highest balance when the game ends " +
                    "(when all the rounds are played). Build a powerful empire and be the RICHEST!\n\n"

        const val RULES_LAST_STANDING_TITLE = "Last standing mode"
        const val RULES_LAST_STANDING_TEXT =
            "In this mode, the winner is the player that is the last in the game. " +
                    "This mode is not round base, this means it runs until someone wins! " +
                    "Drain down your friends balance and become the only survivor!\n\n"

        const val RULES_LANDLORD_TITLE = "Landlord mode"
        const val RULES_LANDLORD_TEXT =
            "In this mode, the main way of getting money is by passively collecting rent from your buildings. " +
                    "Player will be taxed whenever they step in a building owned by someone else! " +
                    "In this mode, you can randomly give starting buildings to players, to spice things up! " +
                    "The winner is the player that has the highest balance when the game ends."

        const val RULES_DURING_ROUND_TITLE = "During a round"
        const val RULES_DURING_ROUND_TEXT =
            "During rounds, you will not see other players moves neither their actions. Once the round ends, players will be able to see the moves of other players and the results of their actions!\n" +
                    "At the beginning of each rounds, players will get to roll a dice and get prompted 3 locations to visit. " +
                    "They will have to choose only one, which they will have to physically go to by the end of the round, otherwise you will get a malus!\n\n" +
                    "When visiting a location:\n" +
                    "- If the location is owned by another player, you must pay their tax. Make sure to remember who owns which buildings, to not fall into it again!\n\n" +
                    "- If the location is not owned by anyone, you can place a bid on it! " +
                    "The minimum amount is the building's minimum value. " +
                    "But you can overprice! " +
                    "Why? " +
                    "Well, if another player visits this location and places a bids too, the highest bidder will win the location by the end of the round. " +
                    "But the bids are blind, you will never know if someone else is trying to buy the same buildings as you!\n\n" +
                    "- If the location is owned by you, you can upgrade it! "

        const val RULES_DISTANCE_INCOME_TITLE = "Distance income"
        const val RULES_DISTANCE_INCOME_TEXT = "Every 1km walked, you will gain a money income. So you better walk around your campus a lot!"



    }
}