# Piano Hero!

Piano Hero! is a musical teaching tool, compatible with Android Things, helping you learn notes on the treble clef and their corresponding keys on a piano.

## Notes (ha!)

- can use [MIDI number](https://en.wikipedia.org/wiki/Scientific_pitch_notation#Table_of_note_frequencies) to represent pitch
- UI will display a series of notes or chords (2 or more notes).
- input device (touchscreen, buttons, MIDI controller) will send events which correspond to a note or chord

The cool thing is that the only constants will be the way we represent pitches. This means we can display notes in anyway we want:

- using traditional musical notation, in _any_ key
- just using text
- playing the sound audibly at the correct pitch (whaaat?)

and also input in anyway we want:

- pressing the correct key(s) on a MIDI controller
- typing the scientific pitch notation (e.g. "C4")
- singing/playing the correct pitch and using a microphone to determine pitch (whaaat?)