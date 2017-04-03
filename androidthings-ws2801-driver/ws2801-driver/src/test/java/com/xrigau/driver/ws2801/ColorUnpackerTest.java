package com.xrigau.driver.ws2801;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ColorUnpackerTest {

    private static final byte R = (byte) 111;
    private static final byte G = (byte) 222;
    private static final byte B = (byte) 333;

    @Test
    public void orderedBytesWhenModeIsRBG() {
        Ws2801.Mode mode = Ws2801.Mode.RBG;

        byte[] result = ColorUnpacker.getOrderedRgbBytes(mode, R, G, B);

        assertBytesOrder(result, R, B, G);
    }

    @Test
    public void orderedBytesWhenModeIsBGR() {
        Ws2801.Mode mode = Ws2801.Mode.BGR;

        byte[] result = ColorUnpacker.getOrderedRgbBytes(mode, R, G, B);

        assertBytesOrder(result, B, G, R);
    }

    @Test
    public void orderedBytesWhenModeIsBRG() {
        Ws2801.Mode mode = Ws2801.Mode.BRG;

        byte[] result = ColorUnpacker.getOrderedRgbBytes(mode, R, G, B);

        assertBytesOrder(result, B, R, G);
    }

    @Test
    public void orderedBytesWhenModeIsGRB() {
        Ws2801.Mode mode = Ws2801.Mode.GRB;

        byte[] result = ColorUnpacker.getOrderedRgbBytes(mode, R, G, B);

        assertBytesOrder(result, G, R, B);
    }

    @Test
    public void orderedBytesWhenModeIsGBR() {
        Ws2801.Mode mode = Ws2801.Mode.GBR;

        byte[] result = ColorUnpacker.getOrderedRgbBytes(mode, R, G, B);

        assertBytesOrder(result, G, B, R);
    }

    private void assertBytesOrder(byte[] bytes, byte... order) {
        assertEquals(order[0], bytes[0]);
        assertEquals(order[1], bytes[1]);
        assertEquals(order[2], bytes[2]);
    }

}
