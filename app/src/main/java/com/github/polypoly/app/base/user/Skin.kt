package com.github.polypoly.app.base.user

/**
 * A class that determine the skin of the player
 * @property idHead The id of the head in the skin of the player
 * Current possible values: [0,0]
 * @property idBody The id of the body in the skin of the player
 * Current possible values: [0,0]
 * @property idLegs The id of the legs in the skin of the player
 * Current possible values: [0,0]
 */
data class Skin(
    val idHead: Int = 0,
    val idBody: Int = 0,
    val idLegs: Int = 0
) {
    init {
        fun testIdRange(id: Int, nb: Int) {
            if (id < 0 || id >= nb)
                throw java.lang.IllegalArgumentException("The id must be between 0 and ${nb-1}")
        }
        // Checks that the skin exists
        testIdRange(idHead, NB_HEADS)
        testIdRange(idBody, NB_BODIES)
        testIdRange(idLegs, NB_LEGS)
    }

    companion object {
        // TODO: Replace this by a list of skin parts
        // The number of possible heads, bodies and legs
        const val NB_HEADS = 1
        const val NB_BODIES = 1
        const val NB_LEGS = 1

        /**
         * Generate a random skin
         * @return a random skin
         */
        fun random(): Skin {
            return Skin(
                idHead = (0 until NB_HEADS).random(),
                idBody = (0 until NB_BODIES).random(),
                idLegs = (0 until NB_LEGS).random()
            )
        }

        /**
         * Generate the default skin (skin with id 0 for each part)
         * @return the default skin
         */
        fun default(): Skin {
            return Skin(
                idHead = 0,
                idBody = 0,
                idLegs = 0
            )
        }
    }
}
