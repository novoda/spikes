package com.novoda.pianohero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

public class TrebleStaffWidget extends FrameLayout {

    private static final String SHARP_SYMBOL = "#";

    private final SimplePitchNotationFormatter formatter = new SimplePitchNotationFormatter();
    private final Drawable completedNoteDrawable;
    private final Drawable completedSharpDrawable;
    private final Drawable noteDrawable;
    private final Drawable sharpDrawable;
    private final Drawable errorNoteDrawable;
    private final Drawable errorSharpDrawable;

    public TrebleStaffWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        completedNoteDrawable = noteDrawable(context, R.drawable.note_completed);
        completedSharpDrawable = sharpDrawable(context, R.drawable.sharp_completed);
        noteDrawable = noteDrawable(context, R.drawable.note);
        sharpDrawable = sharpDrawable(context, R.drawable.sharp);
        errorNoteDrawable = noteDrawable(context, R.drawable.note_error);
        errorSharpDrawable = sharpDrawable(context, R.drawable.sharp_error);
    }

    private Drawable noteDrawable(Context context, @DrawableRes int res) {
        Drawable drawable = ContextCompat.getDrawable(context, res);
        int width = context.getResources().getDimensionPixelSize(R.dimen.note_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.note_height);
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

    private Drawable sharpDrawable(Context context, @DrawableRes int res) {
        Drawable drawable = ContextCompat.getDrawable(context, res);
        int width = context.getResources().getDimensionPixelSize(R.dimen.sharp_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.sharp_height);
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

    public void show(List<Note> notes, int nextNoteToPlayIndex) {
        show(notes, nextNoteToPlayIndex, null);
    }

    public void show(List<Note> notes, int indexNextPlayableNote, @Nullable Note lastErrorNote) {
        removeAllViews();

        for (int index = 0; index < notes.size(); index++) {
            Note note = notes.get(index);
            if (index < indexNextPlayableNote) {
                addCompletedNoteWidget(note);
            } else {
                addNoteWidget(note);
            }
        }

        if (lastErrorNote != null) {
            addErrorNoteWidget(lastErrorNote);
        }
    }

    private void addNoteWidget(Note note) {
        boolean shouldDisplaySharp = formatter.format(note).endsWith(SHARP_SYMBOL);
        if (shouldDisplaySharp) {
            addNoteWidget(note, noteDrawable, sharpDrawable);
        } else {
            addNoteWidget(note, noteDrawable, null);
        }
    }

    private void addErrorNoteWidget(Note note) {
        boolean shouldDisplaySharp = formatter.format(note).endsWith(SHARP_SYMBOL);
        if (shouldDisplaySharp) {
            addNoteWidget(note, errorNoteDrawable, errorSharpDrawable);
        } else {
            addNoteWidget(note, errorNoteDrawable, null);
        }
    }

    private void addCompletedNoteWidget(Note note) {
        boolean shouldDisplaySharp = formatter.format(note).endsWith(SHARP_SYMBOL);
        if (shouldDisplaySharp) {
            addNoteWidget(note, completedNoteDrawable, completedSharpDrawable);
        } else {
            addNoteWidget(note, completedNoteDrawable, null);
        }
    }

    private void addNoteWidget(Note note, Drawable noteDrawable, Drawable sharpDrawable) {
        NoteWidget view = new NoteWidget(getContext(), noteDrawable, sharpDrawable);
        view.setTag(R.id.tag_treble_staff_widget_note, note);
        addView(view);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO: position the NoteWidgets, taking into account the treble clef
        for (int i = 0; i < getChildCount(); i++) {
            NoteWidget noteWidget = (NoteWidget) getChildAt(i);
            Note note = (Note) noteWidget.getTag(R.id.tag_treble_staff_widget_note);
            // TODO: calculate the Y position based on the note's midi value
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: draw the Treble clef, staff lines and ledger lines (where necessary)
    }

}
