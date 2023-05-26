package com.github.polypoly.app.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Constants used in the app
 */
class Constants {
    companion object {
        // Used to determine if the player is close enough to a location to interact with it
        const val MAX_INTERACT_DISTANCE = 10.0 // In meters

        const val LANDLORD_TAX_POLL_RATE: Long = 60 // In seconds

        val NOTIFICATION_DURATION: Duration = 10.seconds // Duration of a notification such as for a successful bid
    }
}
