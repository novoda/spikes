package com.novoda.pianohero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

public class TrebleStaffWidget extends FrameLayout {

    private static final String SHARP_SYMBOL = "#";
    private static final int VIEW_HEIGHT_IN_NUMBER_OF_NOTES = 8;

    private final SimplePitchNotationFormatter formatter = new SimplePitchNotationFormatter();
    private final Drawable completedNoteDrawable;
    private final Drawable completedSharpDrawable;
    private final Drawable noteDrawable;
    private final Drawable sharpDrawable;
    private final Drawable errorNoteDrawable;
    private final Drawable errorSharpDrawable;
    private final C4ToB5TrebleStaffPositioner positioner;
    private final Paint linesPaint;

    public TrebleStaffWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        completedNoteDrawable = noteDrawable(context, R.drawable.note_completed);
        completedSharpDrawable = sharpDrawable(context, R.drawable.sharp_completed);
        noteDrawable = noteDrawable(context, R.drawable.note);
        sharpDrawable = sharpDrawable(context, R.drawable.sharp);
        errorNoteDrawable = noteDrawable(context, R.drawable.note_error);
        errorSharpDrawable = sharpDrawable(context, R.drawable.sharp_error);
        linesPaint = new Paint();
        linesPaint.setColor(Color.BLACK);
        linesPaint.setStrokeWidth(2);

        positioner = C4ToB5TrebleStaffPositioner.createPositionerGivenNoteHeight(noteDrawable.getBounds().height());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = noteDrawable.getBounds().height() * VIEW_HEIGHT_IN_NUMBER_OF_NOTES;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
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

    public void show(List<Note> notes, int indexNextPlayableNote) {
        show(notes, indexNextPlayableNote, null);
    }

    public void show(List<Note> notes, int indexNextPlayableNote, @Nullable Note lastErrorNote) {
        removeAllViews();

        for (int index = 0; index < notes.size(); index++) {
            Note note = notes.get(index);
            if (index < indexNextPlayableNote) {
                addCompletedNoteWidget(new SequenceNote(note, index));
            } else {
                addNoteWidget(new SequenceNote(note, index));
            }
        }

        if (lastErrorNote != null) {
            addErrorNoteWidget(new SequenceNote(lastErrorNote, indexNextPlayableNote));
        }
    }

    private void addNoteWidget(SequenceNote sequenceNote) {
        boolean shouldDisplaySharp = formatter.format(sequenceNote.note).endsWith(SHARP_SYMBOL);
        if (shouldDisplaySharp) {
            addNoteWidget(sequenceNote, noteDrawable, sharpDrawable);
        } else {
            addNoteWidget(sequenceNote, noteDrawable, null);
        }
    }

    private void addErrorNoteWidget(SequenceNote sequenceNote) {
        boolean shouldDisplaySharp = formatter.format(sequenceNote.note).endsWith(SHARP_SYMBOL);
        if (shouldDisplaySharp) {
            addNoteWidget(sequenceNote, errorNoteDrawable, errorSharpDrawable);
        } else {
            addNoteWidget(sequenceNote, errorNoteDrawable, null);
        }
    }

    private void addCompletedNoteWidget(SequenceNote sequenceNote) {
        boolean shouldDisplaySharp = formatter.format(sequenceNote.note).endsWith(SHARP_SYMBOL);
        if (shouldDisplaySharp) {
            addNoteWidget(sequenceNote, completedNoteDrawable, completedSharpDrawable);
        } else {
            addNoteWidget(sequenceNote, completedNoteDrawable, null);
        }
    }

    private void addNoteWidget(SequenceNote note, Drawable noteDrawable, Drawable sharpDrawable) {
        NoteWidget view = new NoteWidget(getContext(), noteDrawable, sharpDrawable);
        view.setTag(R.id.tag_treble_staff_widget_note, note);
        addView(view);
    }

    private static class SequenceNote {
        public final Note note;
        public final int positionInSequence;

        private SequenceNote(Note note, int positionInSequence) {
            this.note = note;
            this.positionInSequence = positionInSequence;
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            layout((NoteWidget) getChildAt(i));
        }
    }

    private void layout(NoteWidget noteWidget) {
        SequenceNote sequenceNote = (SequenceNote) noteWidget.getTag(R.id.tag_treble_staff_widget_note);

        int noteLeft = sequenceNote.positionInSequence * (sharpDrawable.getBounds().width() + noteDrawable.getBounds().width() + noteDrawable.getBounds().width());
        int noteTop = (int) (positioner.yPosition(sequenceNote.note) - (0.5 * noteWidget.getMeasuredHeight()));
        int noteRight = noteLeft + noteWidget.getMeasuredWidth();
        int noteBottom = noteTop + noteWidget.getMeasuredHeight();
        noteWidget.layout(noteLeft, noteTop, noteRight, noteBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: draw ledger lines (where necessary) and the Treble clef
        int noteHeight = noteDrawable.getBounds().height();
        int startY = 2 * noteHeight;
        drawStaffLine(canvas, startY);
        drawStaffLine(canvas, startY + noteHeight);
        drawStaffLine(canvas, startY + 2 * noteHeight);
        drawStaffLine(canvas, startY + 3 * noteHeight);
        drawStaffLine(canvas, startY + 4 * noteHeight);
    }

    private void drawStaffLine(Canvas canvas, int y) {
        canvas.drawLine(0, y, getRight(), y, linesPaint);
    }

}
