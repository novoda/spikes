package com.novoda.spikes.arcore

import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.vr.sdk.audio.GvrAudioEngine

private const val SOUND_FILE = "sounds/sams_song.wav"

// From: http://www.hasper.info/combining-arcore-tracking-and-cardboard-spatial-audio/
class SoundPlayer(private val audioEngine: GvrAudioEngine) {

    private val soundIds = ArrayList<Int>()

    fun init() {
        Thread(
                Runnable {
                    // Prepare the audio file and set the room configuration to an office-like setting
                    audioEngine.preloadSoundFile(SOUND_FILE)
                    audioEngine.setRoomProperties(15f, 15f, 15f, GvrAudioEngine.MaterialName.PLASTER_SMOOTH, GvrAudioEngine.MaterialName.PLASTER_SMOOTH, GvrAudioEngine.MaterialName.CURTAIN_HEAVY)
                }
        ).start()
    }


    fun resume() {
        audioEngine.resume()
    }

    fun pause() {
        audioEngine.pause()
    }

    fun playSound(hit: HitResult) {
        val soundId = audioEngine.createSoundObject(SOUND_FILE)
        val translation = FloatArray(3)
        hit.hitPose.getTranslation(translation, 0)
        audioEngine.setSoundObjectPosition(soundId, translation[0], translation[1], translation[2])
        audioEngine.playSound(soundId, true)
        // Set a logarithmic rolloff model and mute after four meters to limit audio chaos
        audioEngine.setSoundObjectDistanceRolloffModel(soundId, GvrAudioEngine.DistanceRolloffModel.LOGARITHMIC, 0f, 4f)
        soundIds.add(soundId)
    }

    fun stopSoundAt(position: Int) {
        val soundId = soundIds[position]
        audioEngine.stopSound(soundId)
        soundIds.remove(soundId)
    }

    fun updateUserPosition(pose: Pose) {
        // Extract positional data
        val translation = FloatArray(3)
        pose.getTranslation(translation, 0)
        val rotation = FloatArray(4)
        pose.getRotationQuaternion(rotation, 0)

        // Update audio engine
        audioEngine.setHeadPosition(translation[0], translation[1], translation[2])
        audioEngine.setHeadRotation(rotation[0], rotation[1], rotation[2], rotation[3])
        audioEngine.update()
    }

}
