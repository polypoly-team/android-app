package com.github.polypoly.app

/**
 * This object stores all the rules of the game.
 * Each small part of the rules is represented by the data class RulesChapter
 */
object RulesObject {
    const val rulesTitle = "polypoly, the rules"
    val rulesChapters : List<RulesChapter> = listOf(
        RulesChapter("Chap1", ((0..100).map { "empty" }).toString()),
        RulesChapter("Chap2", ((0..100).map { "empty" }).toString()),
        RulesChapter("Conclusion", ((0..100).map { "empty" }).toString())
    )
}

data class RulesChapter(val title: String, val content: String)