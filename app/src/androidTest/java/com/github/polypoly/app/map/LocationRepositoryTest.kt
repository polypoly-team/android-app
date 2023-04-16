package com.github.polypoly.app.map

import com.github.polypoly.app.base.game.location.Zone
import org.junit.Test

// These tests are not really useful, but they're here symbolically, this is only a data class
class LocationRepositoryTest {
    @Test
    fun testGetLocalizationsWorks() {
        assert(LocationRepository.getZones().flatMap(Zone::locations).isNotEmpty())
    }

    @Test
    fun testGetZonesWorks() {
        assert(LocationRepository.getZones().isNotEmpty())
    }
}