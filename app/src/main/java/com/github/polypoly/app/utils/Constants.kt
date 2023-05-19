package com.github.polypoly.app.utils

/**
 * Constants used in the app
 */
class Constants {
    companion object {
        // Used to determine if the player is close enough to a location to interact with it
        const val MAX_INTERACT_DISTANCE = 10.0 // In meters
        const val LANDLORD_TAX_POLL_RATE: Long = 60 // In seconds
    }
}
