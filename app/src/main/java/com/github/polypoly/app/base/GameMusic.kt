package com.github.polypoly.app.base

import android.content.Context
import android.media.MediaPlayer
import androidx.core.math.MathUtils.clamp

/**
 * The music that is played in background during the game and in the menus
 */
object GameMusic {
    private lateinit var mediaPlayer: MediaPlayer
    private var volume = 0.8f // default value
    private var isMute = false
    private var isOn = false

    /**
     * Set the song that will be played in background
     */
    fun setSong(context: Context, songId: Int) {
        mediaPlayer = MediaPlayer.create(context, songId)
    }

    /**
     * Start the song
     */
    fun startSong() {
        if(!isOn) {
            mediaPlayer.isLooping = true
            mediaPlayer.start()
            setVolume(volume)
            isOn = true
        }
    }

    /**
     * Stop the song
     */
    fun stopSong() {
        if(isOn) {
            mediaPlayer.stop()
            isOn = false
        }
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

    /**
     * Get the current volume
     */
    fun getVolume(): Float {
        return volume
    }

    /**
     * Mute the music
     */
    fun mute() {
        mediaPlayer.setVolume(0f, 0f)
        isMute = true
    }

    /**
     * Unmute the music
     */
    fun unMute() {
        mediaPlayer.setVolume(volume, volume)
        isMute = false
    }

    /**
     * Get the mute state
     * @return true if the music is mute, false otherwise
     */
    fun getMuteState(): Boolean {
        return isMute
    }
}