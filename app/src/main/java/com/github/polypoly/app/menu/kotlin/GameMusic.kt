package com.github.polypoly.app.menu.kotlin

import android.content.Context
import android.media.MediaPlayer
import androidx.core.math.MathUtils.clamp

object GameMusic {
    private lateinit var mediaPlayer: MediaPlayer
    private var volume = 0.8f // default value
    private var isMute = false

    fun setSong(context: Context, songId: Int) {
        mediaPlayer = MediaPlayer.create(context, songId)
    }

    fun startSong() {
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        setVolume(volume)
    }

    /**
     * The function setVolume of MediaPlayer only accepts values between 0f and 1f.
     * To avoid issues, before applying the given value, we force it to be in the correct range
      */
    fun setVolume(value: Float) {
        volume = clamp(value, 0f, 1f)
        if(!isMute) {
            mediaPlayer.setVolume(volume, volume)
        }
    }

    fun getVolume(): Float {
        return volume
    }

    fun mute() {
        mediaPlayer.setVolume(0f, 0f)
        isMute = true
    }

    fun unMute() {
        mediaPlayer.setVolume(volume, volume)
        isMute = false
    }
}