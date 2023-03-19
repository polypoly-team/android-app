package com.github.polypoly.app.menu.kotlin

import android.content.Context
import android.media.MediaPlayer
import androidx.core.math.MathUtils.clamp

class GameMusic(private val context: Context, private val songId: Int) {
    private lateinit var mediaPlayer: MediaPlayer

    fun startSong() {
        mediaPlayer = MediaPlayer.create(context, songId)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    /**
     * The function setVolume of MediaPlayer only accepts values between 0f and 1f.
     * To avoid issues, before applying the given value, we force it to be in the correct range
      */
    fun setVolume(value: Float) {
        val validValue = clamp(value, 0f, 1f)
        mediaPlayer.setVolume(validValue, validValue)
    }

    fun mute() {
        setVolume(0f)
    }
}