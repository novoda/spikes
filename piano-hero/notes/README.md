## HALP!

Simple piano View with onTouch listeners on the keys:

![](notes_dispatch.gif)

Because we need to support multi-touch, but keep the view simple, we have NotesPlayedDispatcher that keeps record of which keys are in the pressed state.

See that even though we're using the emulator (digital), we can't get two notes that hit at exactly the same time, so we _build up_ and _break down_ sets of notes. This is similar to how the MIDI controller will work because it also sends NOTE_ON and NOTE_OFF events:

```
03-31 22:29:08.830 10288-10288/com.novoda.pianohero D/!!!: user played: Notes{[]}
03-31 22:29:10.497 10288-10288/com.novoda.pianohero D/!!!: user played: Notes{[Note{72}]}
03-31 22:29:10.498 10288-10288/com.novoda.pianohero D/!!!: user played: Notes{[Note{62}, Note{72}]}
03-31 22:29:13.228 10288-10288/com.novoda.pianohero D/!!!: user played: Notes{[Note{62}]}
03-31 22:29:13.228 10288-10288/com.novoda.pianohero D/!!!: user played: Notes{[]}
```

If we want to make the user lose points, then we don't want to send all those events to our "brain" (or whatever). We want the brain only to get `Notes{[Note{62}, Note{72}]}` (and maybe the empties).

If we don't care about mistakes/intermediate steps, then perhaps this is enough?

## Stories

--
