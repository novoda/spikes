package com.novoda.pianohero;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.Nullable;

class AndroidSynthSpeaker implements Speaker {

    private static final double durationSeconds = 0.1;
    private static final int sampleRate = 8000;
    private static final int numSamples = (int) (durationSeconds * sampleRate);
    private static final double sample[] = new double[numSamples];

    @Nullable
    private AudioTrack audioTrack;

    @Override
    public void start(double frequency) {
        byte[] sound = generateTone(frequency);

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                sound.length,
                AudioTrack.MODE_STATIC
        );
        audioTrack.setLoopPoints(0, audioTrack.getBufferSizeInFrames(), -1);
        audioTrack.write(sound, 0, sound.length);
        audioTrack.play();
    }

    private byte[] generateTone(double freqOfTone) {
        byte generatedSound[] = new byte[2 * numSamples];
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        int idx = 0;
        for (double dVal : sample) {
            // scale to maximum amplitude
            short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[idx++] = (byte) (val & 0x00ff);
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        return generatedSound;
    }

    @Override
    public void stop() {
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
    }

}
