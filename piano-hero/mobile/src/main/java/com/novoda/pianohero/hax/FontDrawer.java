package com.novoda.pianohero.hax;

public class FontDrawer {

    private final int displayWidth;
    private final ShapeDrawer shapeDrawer;

    private int textCursorX, textCursorY;
    private int fontColor;
    private int fontSize;
    private int fontWidth;
    private int fontHeight;
    private boolean wordWrap;

    public FontDrawer(int displayWidth, ShapeDrawer shapeDrawer) {
        this.displayWidth = displayWidth;
        this.shapeDrawer = shapeDrawer;

        textCursorX = 0;
        textCursorY = 0;
        fontColor = android.graphics.Color.WHITE;
        fontSize = 1;
        fontWidth = 3;
        fontHeight = 5;
        wordWrap = true;
    }

    void setTextCursor(int x, int y) {
        textCursorX = x;
        textCursorY = y;
    }

    void setFontColor(int color) {
        fontColor = color;
    }

    public void setFontSize(int size) {
        fontSize = (size >= 3) ? 3 : size; //only 3 sizes for now

        if (fontSize == 1) {
            fontWidth = 3;
            fontHeight = 5;
        } else if (fontSize == 2) //medium (4x6)
        {
            fontWidth = 4;
            fontHeight = 6;
        } else if (fontSize == 3) //large (5x7)
        {
            fontWidth = 5;
            fontHeight = 7;
        }
    }

    public void setWordWrap(boolean wrap) {
        wordWrap = wrap;
    }

    public void writeText(String text) {
        for (char c : text.toCharArray()) {
            writeChar(c);
        }
    }

    // Write a character using the Text cursor and stored Font settings.
    public void writeChar(char c) {
        if (c == '\n') {
            textCursorX = 0;
            textCursorY += fontHeight;
        } else if (c == '\r') {
            ; //ignore
        } else {
            putChar(textCursorX, textCursorY, c, fontSize, fontColor);

            textCursorX += fontWidth + 1;

            if (wordWrap && (textCursorX > (displayWidth - fontWidth))) {
                textCursorX = 0;
                textCursorY += fontHeight + 1;
            }
        }
    }

    // Put a character on the display using glcd fonts.
    private void putChar(int x, int y, char c, int size, int color) {
        char[] font;
        short fontWidth;
        short fontHeight;

        if (size == 1) { // small
            font = Fonts.FONT_3_X_5;
            fontWidth = 3;
            fontHeight = 5;
        } else if (size == 2) { // medium
            font = Fonts.FONT_4_X_6;
            fontWidth = 4;
            fontHeight = 6;
        } else if (size == 3) { // large
            font = Fonts.FONT_5_X_7;
            fontWidth = 5;
            fontHeight = 7;
        } else {
            throw new IllegalStateException(size + " is not supported. Try a number between 1-3 inclusive.");
        }

        for (int i = 0; i < fontWidth + 1; i++) {
            int line;

            if (i == fontWidth) {
                line = 0x0;
            } else {
                line = font[((c - 0x20) * fontWidth) + i];
            }

            for (int j = 0; j < fontHeight + 1; j++) {
                if ((line & 0x1) == 1) {
                    shapeDrawer.drawPixel(x + i, y + j, color);
                }

                line >>= 1;
            }
        }
    }

}
