package com.github.polypoly.app.base.game.location

import com.github.polypoly.app.base.game.Player

/**
 * A class that represent a bet on a location
 * @property player The player who made the bet
 * @property amount The amount of money bet
 * @property randomNumber The random number generated by the player to know if he/she has won the
 * bet in the case of a tie
 * @property timeOfTheBet The time of the bet in Unix time (i.e. the number of milliseconds since
 * January 1, 1970, 00:00:00 GMT)
 */
class LocationBet (
    val player: Player,
    val amount: Int,
    val randomNumber: Float,
    val timeOfTheBet: Long,
) {}