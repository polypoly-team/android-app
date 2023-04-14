package com.github.polypoly.app.map

import org.junit.Test

// These tests are not really useful, but they're here symbolically, this is only a data class
class LocationRepositoryTest {
    @Test
    fun testGetLocalizationsWorks() {
        assert(LocationRepository.getZones().flatMap { zone -> zone.locations }
            .isNotEmpty())
    }

    @Test
    fun testGetZonesWorks() {
        assert(LocationRepository.getZones().isNotEmpty())
    }
}