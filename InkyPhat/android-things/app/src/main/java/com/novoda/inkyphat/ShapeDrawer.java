package com.novoda.inkyphat;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

class ShapeDrawer {

    private final int width;
    private final int height;
    private final InkyPhat inkyPhat;

    ShapeDrawer(int width, int height, InkyPhat inkyPhat) {
        this.width = width;
        this.height = height;
        this.inkyPhat = inkyPhat;
    }

    /**
     * Clear the inside of the given Rectangle.
     */
    public void clearRect(int fx, int fy, int fw, int fh, InkyPhat.Palette color) {
        int maxX, maxY;
        maxX = (fx + fw) > width ? width : (fx + fw);
        maxY = (fy + fh) > height ? height : (fy + fh);

        for (int col = fx; col < maxX; col++) {
            for (int row = fy; row < maxY; row++) {
                inkyPhat.setPixel(col, row, color);
            }
        }
    }

    /**
     * Bresenham's Line Algorithm
     */
    public void drawLine(int col0, int row0, int col1, int row1, InkyPhat.Palette color) {
        boolean steep = Math.abs(row1 - row0) > Math.abs(col1 - col0);

        if (steep) {
            row0 = swap(col0, col0 = row0);
            row1 = swap(col1, col1 = row1);
        }

        if (col0 > col1) {
            col1 = swap(col0, col0 = col1);
            row1 = swap(row0, row0 = row1);
        }

        int dCol, dRow;
        dCol = col1 - col0;
        dRow = Math.abs(row1 - row0);

        int err = dCol / 2;
        int ystep;

        if (row0 < row1) {
            ystep = 1;
        } else {
            ystep = -1;
        }

        for (; col0 <= col1; col0++) {
            if (steep) {
                inkyPhat.setPixel(row0, col0, color);
            } else {
                inkyPhat.setPixel(col0, row0, color);
            }

            err -= dRow;

            if (err < 0) {
                row0 += ystep;
                err += dCol;
            }
        }
    }

    /**
     * so gross
     * http://stackoverflow.com/a/20600020
     */
    private int swap(int a, int b) {
        return a;
    }

    // Draw a vertical line
    public void drawVLine(int x, int y, int h, InkyPhat.Palette color) {
        drawLine(x, y, x, y + h - 1, color);
    }

    // Draw a horizontal line
    public void drawHLine(int x, int y, int w, InkyPhat.Palette color) {
        drawLine(x, y, x + w - 1, y, color);
    }

    // Draw the outline of a rectangle (no fill)
    public void drawRect(int x, int y, int w, int h, InkyPhat.Palette color) {
        drawHLine(x, y, w, color);
        drawHLine(x, y + h - 1, w, color);
        drawVLine(x, y, h, color);
        drawVLine(x + w - 1, y, h, color);
    }

    public void fillRect(int x, int y, int w, int h, InkyPhat.Palette color) {
        for (int i = x; i < x + w; i++) {
            drawVLine(i, y, h, color);
        }
    }

    // Draw a rounded rectangle with radius r.
    public void drawRoundRect(int x, int y, int w, int h, int r, InkyPhat.Palette color) {
        drawHLine(x + r, y, w - 2 * r, color);
        drawHLine(x + r, y + h - 1, w - 2 * r, color);
        drawVLine(x, y + r, h - 2 * r, color);
        drawVLine(x + w - 1, y + r, h - 2 * r, color);

        drawCircleQuadrant(x + r, y + r, r, 1, color);
        drawCircleQuadrant(x + w - r - 1, y + r, r, 2, color);
        drawCircleQuadrant(x + w - r - 1, y + h - r - 1, r, 4, color);
        drawCircleQuadrant(x + r, y + h - r - 1, r, 8, color);
    }

    public void fillRoundRect(int x, int y, int w, int h, int r, InkyPhat.Palette color) {
        fillRect(x + r, y, w - 2 * r, h, color);

        fillCircleHalf(x + r, y + r, r, 1, h - 2 * r - 1, color);
        fillCircleHalf(x + w - r - 1, y + r, r, 2, h - 2 * r - 1, color);
    }

    // Draw the outline of a cirle (no fill) - Midpoint Circle Algorithm
    public void drawCircle(int x, int y, int r, InkyPhat.Palette color) {
        int f = 1 - r;
        int ddFx = 1;
        int ddFy = -2 * r;
        int x1 = 0;
        int y1 = r;

        inkyPhat.setPixel(x, y + r, color);
        inkyPhat.setPixel(x, y - r, color);
        inkyPhat.setPixel(x + r, y, color);
        inkyPhat.setPixel(x - r, y, color);

        while (x1 < y1) {
            if (f >= 0) {
                y1--;
                ddFy += 2;
                f += ddFy;
            }

            x1++;
            ddFx += 2;
            f += ddFx;

            inkyPhat.setPixel(x + x1, y + y1, color);
            inkyPhat.setPixel(x - x1, y + y1, color);
            inkyPhat.setPixel(x + x1, y - y1, color);
            inkyPhat.setPixel(x - x1, y - y1, color);
            inkyPhat.setPixel(x + y1, y + x1, color);
            inkyPhat.setPixel(x - y1, y + x1, color);
            inkyPhat.setPixel(x + y1, y - x1, color);
            inkyPhat.setPixel(x - y1, y - x1, color);
        }
    }

    /**
     * Draw one of the four quadrants of a circle.
     */
    public void drawCircleQuadrant(int x, int y, int r, int quadrant, InkyPhat.Palette color) {
        int f = 1 - r;
        int ddFx = 1;
        int ddFy = -2 * r;
        int x1 = 0;
        int y1 = r;

        while (x1 < y1) {
            if (f >= 0) {
                y1--;
                ddFy += 2;
                f += ddFy;
            }

            x1++;
            ddFx += 2;
            f += ddFx;

            //Upper Left
            if ((quadrant & 0x1) == 0x1) {
                inkyPhat.setPixel(x - y1, y - x1, color);
                inkyPhat.setPixel(x - x1, y - y1, color);
            }

            //Upper Right
            if ((quadrant & 0x2) == 0x2) {
                inkyPhat.setPixel(x + x1, y - y1, color);
                inkyPhat.setPixel(x + y1, y - x1, color);
            }

            //Lower Right
            if ((quadrant & 0x4) == 0x4) {
                inkyPhat.setPixel(x + x1, y + y1, color);
                inkyPhat.setPixel(x + y1, y + x1, color);
            }

            //Lower Left
            if ((quadrant & 0x8) == 0x8) {
                inkyPhat.setPixel(x - y1, y + x1, color);
                inkyPhat.setPixel(x - x1, y + y1, color);
            }
        }
    }

    public void fillCircle(int x, int y, int r, InkyPhat.Palette color) {
        drawVLine(x, y - r, 2 * r + 1, color);
        fillCircleHalf(x, y, r, 3, 0, color);
    }

    public void fillCircleHalf(int x, int y, int r, int half, int stretch, InkyPhat.Palette color) {
        int f = 1 - r;
        int ddFx = 1;
        int ddFy = -2 * r;
        int x1 = 0;
        int y1 = r;

        while (x1 < y1) {
            if (f >= 0) {
                y1--;
                ddFy += 2;
                f += ddFy;
            }

            x1++;
            ddFx += 2;
            f += ddFx;

            //Left
            if ((half & 0x1) == 0x1) {
                drawVLine(x - x1, y - y1, 2 * y1 + 1 + stretch, color);
                drawVLine(x - y1, y - x1, 2 * x1 + 1 + stretch, color);
            }

            //Right
            if ((half & 0x2) == 0x2) {
                drawVLine(x + x1, y - y1, 2 * y1 + 1 + stretch, color);
                drawVLine(x + y1, y - x1, 2 * x1 + 1 + stretch, color);
            }
        }
    }

    // Draw an Arc
    public void drawArc(int x, int y, int r,
                        float startAngle, float endAngle,
                        InkyPhat.Palette color) {
        // Convert degrees to radians
        float degreesPerRadian = (float) (Math.PI / 180);

        startAngle *= degreesPerRadian;
        endAngle *= degreesPerRadian;
        float step = 1 * degreesPerRadian; //number of degrees per point on the arc

        float prevX = (float) (x + r * cos(startAngle));
        float prevY = (float) (y + r * sin(startAngle));

        // Draw the arc
        for (float theta = startAngle; theta < endAngle; theta += Math.min(step, endAngle - theta)) {
            int arcCol1 = (int) (x + r * cos(theta));
            int arcCol0 = (int) prevX;
            int arcRow0 = (int) prevY;
            int arcRow1 = (int) (y + r * sin(theta));
            drawLine(arcCol0, arcRow0, arcCol1, arcRow1, color);

            prevX = (float) (x + r * cos(theta));
            prevY = (float) (y + r * sin(theta));
        }

        drawLine((int) prevX, (int) prevY, (int) (x + r * cos(endAngle)), (int) (y + r * sin(endAngle)), color);
    }

    // Draw the outline of a wedge. //TODO: add inner radius
    public void drawWedge(int x, int y, int r, float startAngle, float endAngle, InkyPhat.Palette color) {
        // Convert degrees to radians
        float degreesPerRadian = (float) (Math.PI / 180);

        float startAngleDeg = startAngle * degreesPerRadian;
        float endAngleDeg = endAngle * degreesPerRadian;

        int prevX = (int) (x + r * cos(startAngleDeg));
        int prevY = (int) (y + r * sin(startAngleDeg));

        //Special cases to overcome floating point limitations
        if (startAngle == 90 || startAngle == 270) {
            prevX = x;
        } else if (startAngle == 0 || startAngle == 180 || startAngle == 360) {
            prevY = y;
        }

        drawLine(x, y, prevX, prevY, color);

        drawArc(x, y, r, startAngle, endAngle, color);

        prevX = (int) (x + r * cos(endAngleDeg));
        prevY = (int) (y + r * sin(endAngleDeg));

        //Special cases to overcome floating point limitations
        if (endAngle == 90 || endAngle == 270) {
            prevX = x;
        } else if (endAngle == 0 || endAngle == 180 || endAngle == 360) {
            prevY = y;
        }

        drawLine(prevX, prevY, x, y, color);
    }

    public void drawTriangle(int x1, int y1,
                             int x2, int y2,
                             int x3, int y3, InkyPhat.Palette color) {
        drawLine(x1, y1, x2, y2, color);
        drawLine(x2, y2, x3, y3, color);
        drawLine(x3, y3, x1, y1, color);
    }

    public void fillTriangle(int x1, int y1,
                             int x2, int y2,
                             int x3, int y3, InkyPhat.Palette color) {
        int a, b, y, last;

        // Sort coordinates by Y order (y3 >= y2 >= y1)
        if (y1 > y2) {
            swap(y1, y1 = y2);
            swap(x1, x1 = x2);
        }

        if (y2 > y3) {
            swap(y3, y3 = y2);
            swap(x3, x3 = x2);
        }

        if (y1 > y2) {
            swap(y1, y1 = y2);
            swap(x1, x1 = x2);
        }

        // Handle case where all points are on the same line.
        if (y1 == y3) {
            a = b = x1;
            if (x2 < a) {
                a = x2;
            } else if (x2 > b) {
                b = x2;
            }
            if (x3 < a) {
                a = x3;
            } else if (x3 > b) {
                b = x3;
            }

            drawHLine(a, y1, b - a + 1, color);
            return;
        }

        int dx12 = x2 - x1,
                dy12 = y2 - y1,
                dx13 = x3 - x1,
                dy13 = y3 - y1,
                dx23 = x3 - x2,
                dy23 = y3 - y2,
                sa = 0,
                sb = 0;

        // For upper part of triangle, find scanline crossings for segments
        // 1-2 and 1-3.  If y2==y3 (flat-bottomed triangle), the scanline y2
        // is included here (and second loop will be skipped, avoiding a /0
        // error there), otherwise scanline y2 is skipped here and handled
        // in the second loop...which also avoids a /0 error here if y1=y2
        // (flat-topped triangle).
        if (y2 == y3) {
            last = y2;   // Include y2 scanline
        } else {
            last = y2 - 1; // Skip it
        }

        for (y = y1; y <= last; y++) {
            a = x1 + sa / dy12;
            b = x1 + sb / dy13;
            sa += dx12;
            sb += dx13;

    /* longhand:
    a = x1 + (x2 - x1) * (y - y1) / (y2 - y1);
    b = x1 + (x3 - x1) * (y - y1) / (y3 - y1);
    */

            if (a > b) {
                swap(a, a = b);
            }

            drawHLine(a, y, b - a + 1, color);
        }

        // For lower part of triangle, find scanline crossings for segments
        // 1-3 and 2-3.  This loop is skipped if y2==y3.
        sa = dx23 * (y - y2);
        sb = dx12 * (y - y1);

        for (; y <= y3; y++) {
            a = x2 + sa / dy23;
            b = x1 + sb / dy13;
            sa += dx23;
            sb += dx13;

    /* longhand:
    a = x2 + (x3 - x2) * (y - y2) / (y3 - y2);
    b = x1 + (x3 - x1) * (y - y1) / (y3 - y1);
    */

            if (a > b) {
                swap(a, a = b);
            }

            drawHLine(a, y, b - a + 1, color);
        }
    }

    public void drawPixel(int row, int col, InkyPhat.Palette color) {
        inkyPhat.setPixel(row, col, color);
    }
}
