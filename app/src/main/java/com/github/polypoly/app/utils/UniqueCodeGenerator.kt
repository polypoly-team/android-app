package com.github.polypoly.app.utils

import kotlin.random.Random

class UniqueCodeGenerator(private val codeLength: Int = 6) {

    // TODO: replace this with a real database query
    private val database = mutableListOf<String>()

    fun generateUniqueCode(): String {
        var code: String
        do {
            code = generateCode()
        } while (database.contains(code))
        database.add(code)
        return code
    }

    private fun generateCode(): String {
        val digits = "0123456789"
        val sb = StringBuilder(codeLength)
        for (i in 0 until codeLength) {
            sb.append(digits[Random.nextInt(digits.length)])
        }
        return sb.toString()
    }
}
