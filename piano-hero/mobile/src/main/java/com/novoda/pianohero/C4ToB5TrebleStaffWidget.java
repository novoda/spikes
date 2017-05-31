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

import java.util.ArrayList;
import java.util.List;

public class C4ToB5TrebleStaffWidget extends FrameLayout {

    private static final String SHARP_SYMBOL = "#";
    private static final int VIEW_HEIGHT_IN_NUMBER_OF_NOTES = 8;

    private final List<NoteWidget> noteWidgetsThatRequireLedgerLines = new ArrayList<>();
    private final SimplePitchNotationFormatter formatter = new SimplePitchNotationFormatter();
    private final Drawable trebleClefDrawable;
    private final Drawable completedNoteDrawable;
    private final Drawable completedSharpDrawable;
    private final Drawable noteDrawable;
    private final Drawable sharpDrawable;
    private final Drawable errorNoteDrawable;
    private final Drawable errorSharpDrawable;
    private final C4ToB5TrebleStaffPositioner positioner;
    private final Paint linesPaint;

    public C4ToB5TrebleStaffWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        trebleClefDrawable = trebleClefDrawable(context);
        completedNoteDrawable = noteDrawable(context, R.drawable.note_completed);
        completedSharpDrawable = sharpDrawable(context, R.drawable.sharp_completed);
        noteDrawable = noteDrawable(context, R.drawable.note);
        sharpDrawable = sharpDrawable(context, R.drawable.sharp);
        errorNoteDrawable = noteDrawable(context, R.drawable.note_error);
        errorSharpDrawable = sharpDrawable(context, R.drawable.sharp_error);
        linesPaint = new Paint();
        linesPaint.setColor(Color.BLACK);

        positioner = C4ToB5TrebleStaffPositioner.createPositionerGivenNoteHeight(noteDrawable.getBounds().height());
    }

    private Drawable trebleClefDrawable(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.treble_clef);
        int width = context.getResources().getDimensionPixelSize(R.dimen.treble_clef_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.treble_clef_height);
        drawable.setBounds(0, 0, width, height);
        return drawable;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = noteDrawable.getBounds().height() * VIEW_HEIGHT_IN_NUMBER_OF_NOTES;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    public void show(List<Note> notes, int indexNextPlayableNote) {
        show(notes, indexNextPlayableNote, null);
    }

    public void show(List<Note> notes, int indexNextPlayableNote, @Nullable Note lastErrorNote) {
        removeAllViews();
        noteWidgetsThatRequireLedgerLines.clear();

        if (lastErrorNote != null) {
            addErrorNoteWidget(new SequenceNote(lastErrorNote, indexNextPlayableNote));
        }

        for (int index = 0; index < notes.size(); index++) {
            Note note = notes.get(index);
            if (index < indexNextPlayableNote) {
                addCompletedNoteWidget(new SequenceNote(note, index));
            } else {
                addNoteWidget(new SequenceNote(note, index));
            }
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
        NoteWidget noteWidget = new NoteWidget(getContext(), noteDrawable, sharpDrawable);
        noteWidget.setTag(R.id.tag_treble_staff_widget_note, note);
        if (note.note.midi() < Note.D4.midi() || Note.G4_S.midi() < note.note.midi()) {
            noteWidgetsThatRequireLedgerLines.add(noteWidget);
        }
        addView(noteWidget);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            layout((NoteWidget) getChildAt(i));
        }
    }

    private void layout(NoteWidget noteWidget) {
        SequenceNote sequenceNote = (SequenceNote) noteWidget.getTag(R.id.tag_treble_staff_widget_note);

        int trebleClefOffset = (int) (trebleClefDrawable.getBounds().width() * 1.5);
        int noteLeft;
        if (noteWidget.getMeasuredWidth() > noteDrawable.getBounds().width()) {
            noteLeft = trebleClefOffset + (sequenceNote.positionInSequence * (sharpDrawable.getBounds().width() + noteDrawable.getBounds().width() + noteDrawable.getBounds().width()));
        } else {
            noteLeft = trebleClefOffset + sharpDrawable.getBounds().width() + sequenceNote.positionInSequence * (sharpDrawable.getBounds().width() + noteDrawable.getBounds().width() + noteDrawable.getBounds().width());
        }
        int noteTop = (int) (positioner.yPosition(sequenceNote.note) - (0.5 * noteWidget.getMeasuredHeight()));
        int noteRight = noteLeft + noteWidget.getMeasuredWidth();
        int noteBottom = noteTop + noteWidget.getMeasuredHeight();
        noteWidget.layout(noteLeft, noteTop, noteRight, noteBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int noteHeight = noteDrawable.getBounds().height();

        int saveCount = canvas.save();
        canvas.translate(0, (float) (noteHeight * 0.5));
        trebleClefDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);

        int topStaffY = 2 * noteHeight;
        int bottomStaffY = topStaffY + 4 * noteHeight;
        drawStaffLines(canvas, noteHeight, topStaffY, bottomStaffY);
        drawLedgerLines(canvas, noteHeight, topStaffY, bottomStaffY);
    }

    private void drawStaffLines(Canvas canvas, int noteHeight, int topStaffY, int bottomStaffY) {
        for (int i = 0; i <= (bottomStaffY - topStaffY) / noteHeight; i++) {
            drawStaffLine(canvas, topStaffY + i * noteHeight);
        }
    }

    private void drawStaffLine(Canvas canvas, int y) {
        canvas.drawLine(0, y, getRight(), y, linesPaint);
    }

    private void drawLedgerLines(Canvas canvas, int noteHeight, int topStaffY, int bottomStaffY) {
        for (NoteWidget noteWidget : noteWidgetsThatRequireLedgerLines) {
            drawLedgerLines(canvas, noteWidget, noteHeight, topStaffY, bottomStaffY);
        }
    }

    private void drawLedgerLines(Canvas canvas, NoteWidget noteWidget, int noteHeight, int topStaffY, int bottomStaffY) {
        int noteWidgetCenterY = noteWidget.getTop() + (int) ((noteWidget.getBottom() - noteWidget.getTop()) * 0.5);
        if (noteWidgetCenterY > bottomStaffY) {
            drawLedgerLinesForNote(canvas, noteWidget, noteHeight, bottomStaffY, noteWidgetCenterY);
        } else {
            drawLedgerLinesForNote(canvas, noteWidget, noteHeight, topStaffY - noteHeight, noteWidgetCenterY);
        }
    }

    private void drawLedgerLinesForNote(Canvas canvas, NoteWidget noteWidget, int noteHeight, int firstLineY, int lastLineY) {
        for (int i = 0; i <= (lastLineY - firstLineY) / noteHeight; i++) {
            drawLedgerLineWithABitExtraEitherSide(canvas, noteWidget, firstLineY + i * noteHeight);
        }
    }

    private void drawLedgerLineWithABitExtraEitherSide(Canvas canvas, NoteWidget noteWidget, int y) {
        int extra = (int) (noteWidget.getWidth() * 0.3);
        canvas.drawLine(noteWidget.getLeft() - extra, y, noteWidget.getRight() + extra, y, linesPaint);
    }

    private static class SequenceNote {
        final Note note;
        final int positionInSequence;

        SequenceNote(Note note, int positionInSequence) {
            this.note = note;
            this.positionInSequence = positionInSequence;
        }
    }

}
