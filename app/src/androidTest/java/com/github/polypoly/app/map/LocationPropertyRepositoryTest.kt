package com.github.polypoly.app.map

import com.github.polypoly.app.base.game.location.Zone
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import org.junit.Test

// These tests are not really useful, but they're here symbolically, this is only a data class
class LocationPropertyRepositoryTest {
    @Test
    fun testGetLocalizationsWorks() {
        assert(LocationPropertyRepository.getZones().flatMap(Zone::locationProperties).isNotEmpty())
    }

    @Test
    fun testGetZonesWorks() {
        assert(LocationPropertyRepository.getZones().isNotEmpty())
    }
}