package com.github.polypoly.app.base.game.location

import com.github.polypoly.app.base.game.Player
import kotlin.random.Random

/**
 * A class that represent a bid on a location
 * @property player The player who made the bet
 * @property amount The amount of money bet
 * @property randomNumber The random number generated by the player to know if he/she has won the
 * bet in the case of a tie
 * @property timeOfTheBid The time of the bet in Unix time (i.e. the number of milliseconds since
 * January 1, 1970, 00:00:00 GMT)
 */
data class LocationBid (
    val location: LocationProperty = LocationProperty(),
    val player: Player = Player(),
    val amount: Int = 100,
    val randomNumber: Float = Random.nextFloat(),
    val timeOfTheBid: Long = System.currentTimeMillis()
) {
    companion object {
        private fun compare(a: LocationBid, b: LocationBid): Int {
            if(a.location.name != b.location.name)
                return a.location.name.compareTo(b.location.name)

            if (a.amount != b.amount)
                return a.amount.compareTo(b.amount)

            if (a.randomNumber != b.randomNumber)
                return a.randomNumber.compareTo(b.randomNumber)

            return a.timeOfTheBid.compareTo(b.timeOfTheBid)
        }

        val comparator = Comparator<LocationBid> { a, b -> compare(a, b) }
    }
}