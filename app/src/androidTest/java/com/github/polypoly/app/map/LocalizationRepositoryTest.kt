package com.github.polypoly.app.map

import org.junit.Test

// These tests are not really useful, but they're here symbolically, this is only a data class
class LocalizationRepositoryTest {
    @Test
    fun testGetLocalizationsWorks() {
        assert(LocalizationRepository.getZones().flatMap { zone -> zone.localizations }
            .isNotEmpty())
    }

    @Test
    fun testGetZonesWorks() {
        assert(LocalizationRepository.getZones().isNotEmpty())
    }
}