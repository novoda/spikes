package com.novoda.pianohero;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.util.UsbMidiDriver;

@SuppressWarnings("NullableProblems")
abstract class SimpleUsbMidiDriver extends UsbMidiDriver {

    SimpleUsbMidiDriver(Context context) {
        super(context);
    }

    @Override
    public void onDeviceAttached(UsbDevice usbDevice) {
        Log.v("!!!", "device attached");
    }

    @Override
    public void onMidiInputDeviceAttached(MidiInputDevice midiInputDevice) {
        Log.v("!!!", "midi input device attached");
    }

    @Override
    public void onMidiOutputDeviceAttached(MidiOutputDevice midiOutputDevice) {
        Log.v("!!!", "midi output device attached");
    }

    @Override
    public void onDeviceDetached(UsbDevice usbDevice) {
        Log.v("!!!", "device detached");
    }

    @Override
    public void onMidiInputDeviceDetached(MidiInputDevice midiInputDevice) {
        Log.v("!!!", "midi input device detached");
    }

    @Override
    public void onMidiOutputDeviceDetached(MidiOutputDevice midiOutputDevice) {
        Log.v("!!!", "midi output device detached");
    }

    @Override
    public void onMidiMiscellaneousFunctionCodes(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
    }

    @Override
    public void onMidiCableEvents(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
    }

    @Override
    public void onMidiSystemCommonMessage(MidiInputDevice midiInputDevice, int i, byte[] bytes) {
    }

    @Override
    public void onMidiSystemExclusive(MidiInputDevice midiInputDevice, int i, byte[] bytes) {
    }

    /**
     * Note-on
     * Code Index Number : 0x9
     *
     * @param midiInputDevice the Object which the event sent
     * @param cable           the cable ID 0-15
     * @param channel         the MIDI channel number 0-15
     * @param note            0-127
     * @param velocity        0-127
     */
    @Override
    public void onMidiNoteOn(
            MidiInputDevice midiInputDevice,
            int cable,
            int channel,
            int note,
            int velocity) {
    }

    /**
     * Note-off
     * Code Index Number : 0x8
     *
     * @param midiInputDevice the Object which the event sent
     * @param cable           the cable ID 0-15
     * @param channel         0-15
     * @param note            0-127
     * @param velocity        0-127
     */
    @Override
    public void onMidiNoteOff(
            MidiInputDevice midiInputDevice,
            int cable,
            int channel,
            int note,
            int velocity) {
    }

    @Override
    public void onMidiPolyphonicAftertouch(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
    }

    @Override
    public void onMidiControlChange(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
    }

    @Override
    public void onMidiProgramChange(MidiInputDevice midiInputDevice, int i, int i1, int i2) {
    }

    @Override
    public void onMidiChannelAftertouch(MidiInputDevice midiInputDevice, int i, int i1, int i2) {
    }

    @Override
    public void onMidiPitchWheel(MidiInputDevice midiInputDevice, int i, int i1, int i2) {
    }

    @Override
    public void onMidiSingleByte(MidiInputDevice midiInputDevice, int i, int i1) {
    }

    @Override
    public void onMidiTimeCodeQuarterFrame(MidiInputDevice midiInputDevice, int i, int i1) {
    }

    @Override
    public void onMidiSongSelect(MidiInputDevice midiInputDevice, int i, int i1) {
    }

    @Override
    public void onMidiSongPositionPointer(MidiInputDevice midiInputDevice, int i, int i1) {
    }

    @Override
    public void onMidiTuneRequest(MidiInputDevice midiInputDevice, int i) {
    }

    @Override
    public void onMidiTimingClock(MidiInputDevice midiInputDevice, int i) {
    }

    @Override
    public void onMidiStart(MidiInputDevice midiInputDevice, int i) {
    }

    @Override
    public void onMidiContinue(MidiInputDevice midiInputDevice, int i) {
    }

    @Override
    public void onMidiStop(MidiInputDevice midiInputDevice, int i) {
    }

    @Override
    public void onMidiActiveSensing(MidiInputDevice midiInputDevice, int i) {
    }

    @Override
    public void onMidiReset(MidiInputDevice midiInputDevice, int i) {
    }

}
