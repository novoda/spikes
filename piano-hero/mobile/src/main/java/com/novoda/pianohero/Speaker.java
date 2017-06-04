package com.novoda.pianohero;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.Pwm;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Speaker {

    private final Random threadNamer = new Random();

    private final PackageManager packageManager;

    private Pwm bus;

    public Speaker(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    public void open() {
        if (!isThingsDevice()) {
            return;
        }
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            bus = service.openPwm("PWM1");
            bus.setPwmDutyCycle(50);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open buzzer bus");
        }

    }

    private boolean isThingsDevice() {
        return packageManager.hasSystemFeature("android.hardware.type.embedded");
        // TODO once targeting 'O' use constant
        // PackageManager.FEATURE_EMBEDDED
    }

    public void play(double frequency) {
        if (isThingsDevice()) {
            try {
                bus.setPwmFrequencyHz(frequency);
                final HandlerThread thread = new HandlerThread("ThreadForEverything" + threadNamer);
                thread.start();
                new Handler(thread.getLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bus.setEnabled(true);
                            SystemClock.sleep(TimeUnit.MILLISECONDS.toMillis(100));
                            bus.setEnabled(false);
                        } catch (IOException e) {
                            throw new IllegalStateException("can't make noise", e);
                        }
                        thread.quit();
                    }
                });
            } catch (IOException e) {
                throw new IllegalStateException("can't setup to make noise", e);
            }
        } else {
            playSound(frequency);
        }
    }

    public void close() {
        if (!isThingsDevice()) {
            return;
        }
        try {
            bus.close();
        } catch (IOException e) {
            Log.e("!!!", "not much we can do", e);
        }
    }

    private final double duration = 0.1; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = (int) (duration * sampleRate);
    private final double sample[] = new double[numSamples];

    private void playSound(double frequency) {
        byte[] sound = genTone(frequency);
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                                     sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                                                     AudioFormat.ENCODING_PCM_16BIT, sound.length,
                                                     AudioTrack.MODE_STATIC
        );
        audioTrack.write(sound, 0, sound.length);
        audioTrack.play();
    }

    private byte[] genTone(double freqOfTone) {
        byte generatedSnd[] = new byte[2 * numSamples];
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        return generatedSnd;
    }
}
