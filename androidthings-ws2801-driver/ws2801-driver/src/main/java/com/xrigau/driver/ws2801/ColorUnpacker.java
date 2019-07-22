package com.xrigau.driver.ws2801;

import com.xrigau.driver.ws2801.Ws2801.Mode;

import android.graphics.Color;

class ColorUnpacker {

    private final Mode ledMode;

    ColorUnpacker(Mode ledMode) {
        this.ledMode = ledMode;
    }

    /**
     * Returns an WS2801 packet corresponding to the current brightness and given {@link Color}.
     *
     * @param color The {@link Color} to retrieve the protocol packet for.
     * @return WS2801 packet corresponding to the current brightness and given {@link Color}.
     */
    byte[] unpack(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return getOrderedRgbBytes(ledMode, (byte) r, (byte) g, (byte) b);
    }

    static byte[] getOrderedRgbBytes(Mode ledMode, byte r, byte g, byte b) {
        switch (ledMode) {
            case RBG:
                return new byte[]{r, b, g};
            case BGR:
                return new byte[]{b, g, r};
            case BRG:
                return new byte[]{b, r, g};
            case GRB:
                return new byte[]{g, r, b};
            case GBR:
                return new byte[]{g, b, r};
            default:
                throw new IllegalArgumentException(ledMode.name() + " is an unknown " + Mode.class.getSimpleName());
        }
    }

}
