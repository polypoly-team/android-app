package com.github.polypoly.app.game.user

/**
 * A class that determine the skin of the player
 */
data class Skin(
    /**
     * The id of the head in the skin of the player
     * Current possible values: [0,0]
     */
    val idHead: Int = 0,

    /**
     * The id of the body in the skin of the player
     * Current possible values: [0,0]
     */
    val idBody: Int = 0,

    /**
     * The id of the legs in the skin of the player
     * Current possible values: [0,0]
     */
    val idLegs: Int = 0
) {
    init {
        // Checks that the skin exists
        if (idHead < 0 || idHead >= NB_HEADS)
            throw java.lang.IllegalArgumentException("The idHead must be between 0 and ${NB_HEADS-1}")
        if (idBody < 0 || idBody >= NB_BODIES)
            throw java.lang.IllegalArgumentException("The idBody must be between 0 and ${NB_BODIES-1}")
        if (idLegs < 0 || idLegs >= NB_LEGS)
            throw java.lang.IllegalArgumentException("The idLegs must be between 0 and ${NB_LEGS-1}")
    }

    companion object {
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
