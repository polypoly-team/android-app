package com.github.polypoly.app.base.game.location

import com.github.polypoly.app.R

/**
 * Repository for providing the zones and localizations on the map.
 */
object LocationPropertyRepository {
    /**
     * Array of different pastel colors used to represent the zones.
     */
    private val colours = listOf(
        // Pastel red
        0xFFE57373.toInt(),
        // Pastel green
        0xFF81C784.toInt(),
        // Pastel blue
        0xFF64B5F6.toInt(),
        // Pastel yellow
        0xFFFFF176.toInt(),
        // Pastel purple
        0xFFBA68C8.toInt(),
        // Pastel orange
        0xFFFFB74D.toInt(),
        // Pastel pink
        0xFFF06292.toInt(),
        // Pastel brown
        0xFFA1887F.toInt(),
        // Pastel grey
        0xFF90A4AE.toInt(),
        // Pastel cyan
        0xFF4DD0E1.toInt(),
        // Pastel lime
        0xFFDCE775.toInt(),
        // Pastel teal
        0xFF4DB6AC.toInt(),
        // Pastel indigo
        0xFF7986CB.toInt()
    )

    /**
     * List of localizations in the EPFL campus area.
     */
    private val locationProperties = listOf(
        LocationProperty(
            name = "BC",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51861304708622,
            longitude = 6.561904544895297,
            description = R.string.bc_description,
            positivePoint = R.string.bc_positive_point,
            negativePoint = R.string.bc_negative_point
        ),
        LocationProperty(
            name = "INM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51862967945017,
            longitude = 6.563195429654059,
            description = R.string.inm_description,
            positivePoint = R.string.inm_positive_point,
            negativePoint = R.string.inm_negative_point
        ),
        LocationProperty(
            name = "INF",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5187569432044,
            longitude = 6.563754302940894,
            description = R.string.inf_description,
            positivePoint = R.string.inf_positive_point,
            negativePoint = R.string.inf_negative_point
        ),
        LocationProperty(
            name = "INJ",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5183787509451,
            longitude = 6.563782977860895,
            description = R.string.inj_description,
            positivePoint = R.string.inj_positive_point,
            negativePoint = R.string.inj_negative_point
        ),
        LocationProperty(
            name = "INN",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5187602318236,
            longitude = 6.562524865762242,
        ),
        LocationProperty(
            name = "INR",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51837464014728,
            longitude = 6.562565488564992,
        ),
        LocationProperty(
            name = "QIG",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51757926537674,
            longitude = 6.561402515609107,
        ),
        LocationProperty(
            name = "QIH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude= 46.51682504051627,
            longitude = 6.5608779628377905,
        ),
        LocationProperty(
            name = "QII",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.516334883735226,
            longitude = 6.561316930683261,
        ),
        LocationProperty(
            name = "QIJ",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51641847667725,
            longitude = 6.562045782954984,
        ),
        LocationProperty(
            name = "QIF",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51678956668743,
            longitude = 6.56166017548698,
        ),
        LocationProperty(
            name = "Logitech",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5165653868565,
            longitude = 6.562648543340301,
        ),
        LocationProperty(
            name = "PSE D",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51735208130024,
            longitude = 6.561982124516758,
        ),
        LocationProperty(
            name = "PSE C",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51739626509118,
            longitude = 6.562762759441413,
        ),
        LocationProperty(
            name = "PSE A",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51777097627283,
            longitude = 6.5625767820914955,
        ),
        LocationProperty(
            name = "PPH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51768958561605,
            longitude = 6.564391504839671,
        ),
        LocationProperty(
            name = "PPB",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.517154729671965,
            longitude = 6.564712545176651,
        ),
        LocationProperty(
            name = "TCV",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51764075116125,
            longitude = 6.565030206141661,
        ),
        LocationProperty(
            name = "Rolex Learning Center",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.518370937968015,
            longitude = 6.568348749416791,
        ),
        LocationProperty(
            name = "ELG",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51877090875509,
            longitude = 6.564442195419994,
        ),
        LocationProperty(
            name = "ELH", // NORTH WEST BUILDING (FAKE TEMPORARY NAME)
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.519142491902066,
            longitude = 6.564474343002977,
        ),
        LocationProperty(
            name = "ELI", // SOUTH EAST BUILDING (FAKE TEMPORARY NAME)
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.518740557775104,
            longitude = 6.565082406173954,
        ),
        LocationProperty(
            name = "ELE",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51914375981869,
            longitude = 6.56506213740159,
        ),
        LocationProperty(
            name = "ELL",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51836017574591,
            longitude = 6.565227972811845,
        ),
        LocationProperty(
            name = "ELA",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51948102716419,
            longitude = 6.5645167231633765,
        ),
        LocationProperty(
            name = "ELB",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51953808346133,
            longitude = 6.565047396476193,
        ),
        LocationProperty(
            name = "MX",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5194856751895,
            longitude = 6.5631361906295265,
        ),
        LocationProperty(
            name = "SV",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52007453561807,
            longitude = 6.563888081639078,
        ),
        LocationProperty(
            name = "CO2",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52006946399603,
            longitude = 6.56463065575446,
        ),
        LocationProperty(
            name = "CO1",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.519989585944444,
            longitude = 6.56529768262682,
        ),
        LocationProperty(
            name = "ME",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51957371101376,
            longitude = 6.566930240111198,
        ),
        LocationProperty(
            name = "MA",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51997056734838,
            longitude = 6.567847862715424,
        ),
        LocationProperty(
            name = "CM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52049801409303,
            longitude = 6.567322717249148,
        ),
        LocationProperty(
            name = "SG",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52093959210354,
            longitude = 6.564199076849092,
        ),
        LocationProperty(
            name = "BM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.521132310189024,
            longitude = 6.565170135307404,
        ),
        LocationProperty(
            name = "BP",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52145181497848,
            longitude = 6.5648052974047575,
        ),
        LocationProperty(
            name = "GC",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52147463873658,
            longitude = 6.566549644954401,
        ),
        LocationProperty(
            name = "GR",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52125783221227,
            longitude = 6.567854216848852,
        ),
        LocationProperty(
            name = "CE",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.520504533910575,
            longitude = 6.570065102079756,
        ),
        LocationProperty(
            name = "PH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.521198067781015,
            longitude = 6.569884525744014,
        ),
        LocationProperty(
            name = "CH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51976884199191,
            longitude = 6.570168811306288,
        ),
        LocationProperty(
            name = "BS",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52051818638828,
            longitude = 6.5714660668000695,
        ) // 42 locations (0-41)
    )

    /**
     * List of zones in the EPFL campus area. Each zone has a color associated with it.
     */
    private val zones = listOf(
        Zone( // BC, INN, INR
            locationProperties = listOf(
                locationProperties[0],
                locationProperties[4],
                locationProperties[5]
            ),
            color = colours[0]
        ),
        Zone( // INM, INJ, INF
            locationProperties = listOf(
                locationProperties[3],
                locationProperties[2],
                locationProperties[1]
            ),
            color = colours[1]
        ),
        Zone( // QIG, QIH, QII
            locationProperties = listOf(
                locationProperties[6],
                locationProperties[7],
                locationProperties[8]
            ),
            color = colours[2]
        ),
        Zone( // locations with names QIF, QIJ, Logitech
            locationProperties = listOf(
                locationProperties[9],
                locationProperties[10],
                locationProperties[11]
            ),
            color = colours[3]
        ),
        Zone( // PSE A, PSE D, PSE C
            locationProperties = listOf(
                locationProperties[12],
                locationProperties[14],
                locationProperties[13]
            ),
            color = colours[4]
        ),
        Zone( // TCV, PPH, PPB
            locationProperties = listOf(
                locationProperties[15],
                locationProperties[17],
                locationProperties[16]
            ),
            color = colours[5]
        ),
        Zone( // ELA, ELB, ELE
            locationProperties = listOf(
                locationProperties[24],
                locationProperties[25],
                locationProperties[22]
            ),
            color = colours[6]
        ),
        Zone( // ELG, ELH, ELI, ELL
            locationProperties = listOf(
                locationProperties[19],
                locationProperties[20],
                locationProperties[21],
                locationProperties[23]
            ),
            color = colours[7]
        ),
        Zone( // MX, SV
            locationProperties = listOf(
                locationProperties[26],
                locationProperties[27]
            ),
            color = colours[8]
        ),
        Zone( // CO1, CO2, CM
            locationProperties = listOf(
                locationProperties[28],
                locationProperties[29],
                locationProperties[32]
            ),
            color = colours[9]
        ),
        Zone( // ME, MA,
            locationProperties = listOf(
                locationProperties[30],
                locationProperties[31]
            ),
            color = colours[10]
        ),
        Zone( // SG, BM, BP, GC, GR
            locationProperties = listOf(
                locationProperties[33],
                locationProperties[34],
                locationProperties[35],
                locationProperties[36],
                locationProperties[37]
            ),
            color = colours[11]
        ),
        Zone( // CE, PH, CH, BS
            locationProperties = listOf(
                locationProperties[38],
                locationProperties[39],
                locationProperties[40],
                locationProperties[41]
            ),
            color = colours[12]
        )
    )

    /**
     * Returns the list of localizations.
     */
    fun getZones(): List<Zone> = zones
}