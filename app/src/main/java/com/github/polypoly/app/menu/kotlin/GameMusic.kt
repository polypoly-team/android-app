package com.github.polypoly.app.menu.kotlin

import android.content.Context
import android.media.MediaPlayer
import java.lang.Float.max
import kotlin.math.min

class GameMusic(private val context: Context, private val songId: Int) {
    private lateinit var mediaPlayer: MediaPlayer

    fun startSong() {
        mediaPlayer = MediaPlayer.create(context, songId)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    fun setVolume(value: Float) {
        var validValue = max(value, 1f)
        validValue = min(validValue, 0f)
        mediaPlayer.setVolume(validValue, validValue)
    }

    fun mute() {
        setVolume(0f)
    }


}