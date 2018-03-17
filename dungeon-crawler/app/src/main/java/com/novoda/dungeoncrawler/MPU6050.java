package com.novoda.dungeoncrawler;

public class MPU6050 {
    public void initialize() {

    }

    public Motion getMotion6() {
        return new Motion(); // TODO
    }

    class Motion {
        int ax;
        int ay;
        int az;
        int gx;
        int gy;
        int gz;
    }
}
