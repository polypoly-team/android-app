package com.github.polypoly.app.base.game.location

/**
 * Repository for providing the zones and localizations on the map.
 */
object LocationRepository {
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
    private val locations = listOf(
        Location(
            name = "BC",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51861304708622,
            longitude = 6.561904544895297,
        ),
        Location(
            name = "INM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51862967945017,
            longitude = 6.563195429654059,
        ),
        Location(
            name = "INF",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5187569432044,
            longitude = 6.563754302940894,
        ),
        Location(
            name = "INJ",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5183787509451,
            longitude = 6.563782977860895,
        ),
        Location(
            name = "INN",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5187602318236,
            longitude = 6.562524865762242,
        ),
        Location(
            name = "INR",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51837464014728,
            longitude = 6.562565488564992,
        ),
        Location(
            name = "QIG",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51757926537674,
            longitude = 6.561402515609107,
        ),
        Location(
            name = "QIH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude= 46.51682504051627,
            longitude = 6.5608779628377905,
        ),
        Location(
            name = "QII",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.516334883735226,
            longitude = 6.561316930683261,
        ),
        Location(
            name = "QIJ",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51641847667725,
            longitude = 6.562045782954984,
        ),
        Location(
            name = "QIF",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51678956668743,
            longitude = 6.56166017548698,
        ),
        Location(
            name = "Logitech",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5165653868565,
            longitude = 6.562648543340301,
        ),
        Location(
            name = "PSE D",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51735208130024,
            longitude = 6.561982124516758,
        ),
        Location(
            name = "PSE C",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51739626509118,
            longitude = 6.562762759441413,
        ),
        Location(
            name = "PSE A",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51777097627283,
            longitude = 6.5625767820914955,
        ),
        Location(
            name = "PPH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51768958561605,
            longitude = 6.564391504839671,
        ),
        Location(
            name = "PPB",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.517154729671965,
            longitude = 6.564712545176651,
        ),
        Location(
            name = "TCV",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51764075116125,
            longitude = 6.565030206141661,
        ),
        Location(
            name = "Rolex Learning Center",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.518370937968015,
            longitude = 6.568348749416791,
        ),
        Location(
            name = "ELG",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51877090875509,
            longitude = 6.564442195419994,
        ),
        Location(
            name = "ELH", // NORTH WEST BUILDING (FAKE TEMPORARY NAME)
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.519142491902066,
            longitude = 6.564474343002977,
        ),
        Location(
            name = "ELI", // SOUTH EAST BUILDING (FAKE TEMPORARY NAME)
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.518740557775104,
            longitude = 6.565082406173954,
        ),
        Location(
            name = "ELE",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51914375981869,
            longitude = 6.56506213740159,
        ),
        Location(
            name = "ELL",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51836017574591,
            longitude = 6.565227972811845,
        ),
        Location(
            name = "ELA",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51948102716419,
            longitude = 6.5645167231633765,
        ),
        Location(
            name = "ELB",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51953808346133,
            longitude = 6.565047396476193,
        ),
        Location(
            name = "MX",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.5194856751895,
            longitude = 6.5631361906295265,
        ),
        Location(
            name = "SV",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52007453561807,
            longitude = 6.563888081639078,
        ),
        Location(
            name = "CO2",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52006946399603,
            longitude = 6.56463065575446,
        ),
        Location(
            name = "CO1",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.519989585944444,
            longitude = 6.56529768262682,
        ),
        Location(
            name = "ME",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51957371101376,
            longitude = 6.566930240111198,
        ),
        Location(
            name = "MA",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51997056734838,
            longitude = 6.567847862715424,
        ),
        Location(
            name = "CM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52049801409303,
            longitude = 6.567322717249148,
        ),
        Location(
            name = "SG",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52093959210354,
            longitude = 6.564199076849092,
        ),
        Location(
            name = "BM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.521132310189024,
            longitude = 6.565170135307404,
        ),
        Location(
            name = "BP",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52145181497848,
            longitude = 6.5648052974047575,
        ),
        Location(
            name = "GC",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52147463873658,
            longitude = 6.566549644954401,
        ),
        Location(
            name = "GR",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.52125783221227,
            longitude = 6.567854216848852,
        ),
        Location(
            name = "CE",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.520504533910575,
            longitude = 6.570065102079756,
        ),
        Location(
            name = "PH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.521198067781015,
            longitude = 6.569884525744014,
        ),
        Location(
            name = "CH",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            latitude = 46.51976884199191,
            longitude = 6.570168811306288,
        ),
        Location(
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
            locations = listOf(
                locations[0],
                locations[4],
                locations[5]
            ),
            color = colours[0]
        ),
        Zone( // INM, INJ, INF
            locations = listOf(
                locations[3],
                locations[2],
                locations[1]
            ),
            color = colours[1]
        ),
        Zone( // QIG, QIH, QII
            locations = listOf(
                locations[6],
                locations[7],
                locations[8]
            ),
            color = colours[2]
        ),
        Zone( // locations with names QIF, QIJ, Logitech
            locations = listOf(
                locations[9],
                locations[10],
                locations[11]
            ),
            color = colours[3]
        ),
        Zone( // PSE A, PSE D, PSE C
            locations = listOf(
                locations[12],
                locations[14],
                locations[13]
            ),
            color = colours[4]
        ),
        Zone( // TCV, PPH, PPB
            locations = listOf(
                locations[15],
                locations[17],
                locations[16]
            ),
            color = colours[5]
        ),
        Zone( // ELA, ELB, ELE
            locations = listOf(
                locations[24],
                locations[25],
                locations[22]
            ),
            color = colours[6]
        ),
        Zone( // ELG, ELH, ELI, ELL
            locations = listOf(
                locations[19],
                locations[20],
                locations[21],
                locations[23]
            ),
            color = colours[7]
        ),
        Zone( // MX, SV
            locations = listOf(
                locations[26],
                locations[27]
            ),
            color = colours[8]
        ),
        Zone( // CO1, CO2, CM
            locations = listOf(
                locations[28],
                locations[29],
                locations[32]
            ),
            color = colours[9]
        ),
        Zone( // ME, MA,
            locations = listOf(
                locations[30],
                locations[31]
            ),
            color = colours[10]
        ),
        Zone( // SG, BM, BP, GC, GR
            locations = listOf(
                locations[33],
                locations[34],
                locations[35],
                locations[36],
                locations[37]
            ),
            color = colours[11]
        ),
        Zone( // CE, PH, CH, BS
            locations = listOf(
                locations[38],
                locations[39],
                locations[40],
                locations[41]
            ),
            color = colours[12]
        )
    )

    /**
     * Returns the list of localizations.
     */
    fun getZones(): List<Zone> = zones
}