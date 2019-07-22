package com.novoda.pianohero;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class C4ToB5ViewPiano extends PercentRelativeLayout implements Piano {

    private static final NoteListener LOGGING_KEY_LISTENER = new NoteListener() {
        @Override
        public void onStart(Note note) {
            Log.e("!!!", "onStart " + note);
        }

        @Override
        public void onStop(Note note) {
            Log.e("!!!", "onStop " + note);
        }
    };

    private final Map<Note, View> noteToViewsMap = new HashMap<>();
    private NoteListener noteListener = LOGGING_KEY_LISTENER;

    private View c4View;
    private View d4View;
    private View e4View;
    private View f4View;
    private View g4View;
    private View a4View;
    private View b4View;

    private View c4sView;
    private View d4sView;
    private View f4sView;
    private View g4sView;
    private View a4sView;

    private View c5View;
    private View d5View;
    private View e5View;
    private View f5View;
    private View g5View;
    private View a5View;
    private View b5View;

    private View c5sView;
    private View d5sView;
    private View f5sView;
    private View g5sView;
    private View a5sView;

    public C4ToB5ViewPiano(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_piano_octave, this);

        noteToViewsMap.put(Note.C4, c4View = findViewById(R.id.piano_octave_c4));
        noteToViewsMap.put(Note.D4, d4View = findViewById(R.id.piano_octave_d4));
        noteToViewsMap.put(Note.E4, e4View = findViewById(R.id.piano_octave_e4));
        noteToViewsMap.put(Note.F4, f4View = findViewById(R.id.piano_octave_f4));
        noteToViewsMap.put(Note.G4, g4View = findViewById(R.id.piano_octave_g4));
        noteToViewsMap.put(Note.A4, a4View = findViewById(R.id.piano_octave_a4));
        noteToViewsMap.put(Note.B4, b4View = findViewById(R.id.piano_octave_b4));

        noteToViewsMap.put(Note.C4_S, c4sView = findViewById(R.id.piano_octave_c4_s));
        noteToViewsMap.put(Note.D4_S, d4sView = findViewById(R.id.piano_octave_d4_s));
        noteToViewsMap.put(Note.F4_S, f4sView = findViewById(R.id.piano_octave_f4_s));
        noteToViewsMap.put(Note.G4_S, g4sView = findViewById(R.id.piano_octave_g4_s));
        noteToViewsMap.put(Note.A4_S, a4sView = findViewById(R.id.piano_octave_a4_s));

        noteToViewsMap.put(Note.C5, c5View = findViewById(R.id.piano_octave_c5));
        noteToViewsMap.put(Note.D5, d5View = findViewById(R.id.piano_octave_d5));
        noteToViewsMap.put(Note.E5, e5View = findViewById(R.id.piano_octave_e5));
        noteToViewsMap.put(Note.F5, f5View = findViewById(R.id.piano_octave_f5));
        noteToViewsMap.put(Note.G5, g5View = findViewById(R.id.piano_octave_g5));
        noteToViewsMap.put(Note.A5, a5View = findViewById(R.id.piano_octave_a5));
        noteToViewsMap.put(Note.B5, b5View = findViewById(R.id.piano_octave_b5));

        noteToViewsMap.put(Note.C5_S, c5sView = findViewById(R.id.piano_octave_c5_s));
        noteToViewsMap.put(Note.D5_S, d5sView = findViewById(R.id.piano_octave_d5_s));
        noteToViewsMap.put(Note.F5_S, f5sView = findViewById(R.id.piano_octave_f5_s));
        noteToViewsMap.put(Note.G5_S, g5sView = findViewById(R.id.piano_octave_g5_s));
        noteToViewsMap.put(Note.A5_S, a5sView = findViewById(R.id.piano_octave_a5_s));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        float ratio = 1f * 329 / 141;
        if (width * 1f / 329 > height * 1f / 141) {
            int desiredWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (0.5f + (height * ratio)), MeasureSpec.EXACTLY);
            super.onMeasure(desiredWidthMeasureSpec, heightMeasureSpec);
        } else {
            int desiredHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (0.5f + (width / ratio)), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, desiredHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutBlackKeysBasedOnWhiteKeys();
    }

    private void layoutBlackKeysBasedOnWhiteKeys() {
        int halfBlackKeyWidth = c4sView.getWidth() / 2;
        int blackKeyHeight = c4sView.getHeight();

        c4sView.layout(c4View.getRight() - halfBlackKeyWidth, 0, c4View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        d4sView.layout(d4View.getRight() - halfBlackKeyWidth, 0, d4View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        f4sView.layout(f4View.getRight() - halfBlackKeyWidth, 0, f4View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        g4sView.layout(g4View.getRight() - halfBlackKeyWidth, 0, g4View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        a4sView.layout(a4View.getRight() - halfBlackKeyWidth, 0, a4View.getRight() + halfBlackKeyWidth, blackKeyHeight);

        c5sView.layout(c5View.getRight() - halfBlackKeyWidth, 0, c5View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        d5sView.layout(d5View.getRight() - halfBlackKeyWidth, 0, d5View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        f5sView.layout(f5View.getRight() - halfBlackKeyWidth, 0, f5View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        g5sView.layout(g5View.getRight() - halfBlackKeyWidth, 0, g5View.getRight() + halfBlackKeyWidth, blackKeyHeight);
        a5sView.layout(a5View.getRight() - halfBlackKeyWidth, 0, a5View.getRight() + halfBlackKeyWidth, blackKeyHeight);
    }

    @Override
    public void attachListener(NoteListener noteListener) {
        this.noteListener = noteListener;

        bindKey(c4View, Note.C4, c4sView);
        bindKey(d4View, Note.D4, c4sView, d4sView);
        bindKey(e4View, Note.E4, d4sView);
        bindKey(f4View, Note.F4, f4sView);
        bindKey(g4View, Note.G4, f4sView, g4sView);
        bindKey(a4View, Note.A4, g4sView, a4sView);
        bindKey(b4View, Note.B4, a4sView);

        bindKey(c4sView, Note.C4_S);
        bindKey(d4sView, Note.D4_S);
        bindKey(f4sView, Note.F4_S);
        bindKey(g4sView, Note.G4_S);
        bindKey(a4sView, Note.A4_S);

        bindKey(c5View, Note.C5, c5sView);
        bindKey(d5View, Note.D5, c5sView, d5sView);
        bindKey(e5View, Note.E5, d5sView);
        bindKey(f5View, Note.F5, f5sView);
        bindKey(g5View, Note.G5, f5sView, g5sView);
        bindKey(a5View, Note.A5, g5sView, a5sView);
        bindKey(b5View, Note.B5, a5sView);

        bindKey(c5sView, Note.C5_S);
        bindKey(d5sView, Note.D5_S);
        bindKey(f5sView, Note.F5_S);
        bindKey(g5sView, Note.G5_S);
        bindKey(a5sView, Note.A5_S);
    }

    private void bindKey(View keyView, Note note, View... adjacentBlackKeys) {
        KeyTouchListener touchListener = new KeyTouchListener(noteListener, note, adjacentBlackKeys);
        keyView.setOnTouchListener(touchListener);
    }

    public void display(Sequence sequence) {
        for (View view : noteToViewsMap.values()) {
            view.setActivated(false);
        }
        if (noteToViewsMap.containsKey(sequence.getCurrentNote())) {
            noteToViewsMap.get(sequence.getCurrentNote()).setActivated(true);
        }
    }

    private static class KeyTouchListener implements OnTouchListener {

        private final NoteListener noteListener;
        private final Note note;
        private final View[] adjacentBlackKeys;

        KeyTouchListener(NoteListener noteListener, Note note, View[] adjacentBlackKeys) {
            this.noteListener = noteListener;
            this.note = note;
            this.adjacentBlackKeys = adjacentBlackKeys;
        }

        @Override
        public boolean onTouch(View key, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchEventInsideKeyBounds(key);
                    return true;
                case MotionEvent.ACTION_UP:
                    onTouchEventOutsideKeyBounds(key);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Point touchEventRelativeToParentView = new Point(key.getLeft() + (int) event.getX(), (int) event.getY());
                    if (eventInsideView(key, touchEventRelativeToParentView) && !eventInsideOneOfViews(adjacentBlackKeys, touchEventRelativeToParentView)) {
                        onTouchEventInsideKeyBounds(key);
                        return true;
                    } else {
                        onTouchEventOutsideKeyBounds(key);
                        return false;
                    }
                default:
                    return false;
            }
        }

        private void onTouchEventInsideKeyBounds(View key) {
            if (!key.isPressed()) {
                key.setPressed(true);
                noteListener.onStart(note);
            }
        }

        private void onTouchEventOutsideKeyBounds(View key) {
            key.setPressed(false);
            noteListener.onStop(note);
        }

        private static boolean eventInsideView(View view, Point touchEventRelativeToParentView) {
            boolean xWithinBounds = touchEventRelativeToParentView.x >= view.getLeft() && touchEventRelativeToParentView.x <= view.getRight();
            boolean yWithinBounds = touchEventRelativeToParentView.y >= view.getTop() && touchEventRelativeToParentView.y <= view.getBottom();
            return xWithinBounds && yWithinBounds;
        }

        private static boolean eventInsideOneOfViews(View[] views, Point touchEventRelativeToParentView) {
            for (View view : views) {
                if (eventInsideView(view, touchEventRelativeToParentView)) {
                    return true;
                }
            }
            return false;
        }
    }

}
