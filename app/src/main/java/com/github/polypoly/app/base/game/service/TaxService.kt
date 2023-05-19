package com.github.polypoly.app.base.game.service

import android.location.Location
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.utils.Constants

/**
 * A service that runs in the background, receives location updates and taxes the [Player]
 * if they are in a [LocationProperty] that is owned by another [Player].
 */
class TaxService: LocationService() {
    override fun processLocationUpdate(location: Location) {
        val playerLocation = location // For clarity

        // Iterate through all the locations and check if the player is close enough to interact
        for (inGameLocation in GameRepository.game?.inGameLocations!!) {
            // Create a location object from the in-game location
            val propertyLocation = Location("").apply {
                latitude = inGameLocation.locationProperty.latitude
                longitude = inGameLocation.locationProperty.longitude
            }

            // Compute distance between player and location in meters
            val distance = playerLocation.distanceTo(propertyLocation)

            if (inGameLocation.owner != null && inGameLocation.owner != GameRepository.player && distance <= Constants.MAX_INTERACT_DISTANCE) {
                /**
                 * Perform tax payment and money transfer if the player is close enough to a location
                 * that is owned by another player.
                 */
                val taxAmount = inGameLocation.currentTax()
                GameRepository.player?.loseMoney(taxAmount)
                inGameLocation.owner?.earnMoney(taxAmount)
            }
        }
    }
}