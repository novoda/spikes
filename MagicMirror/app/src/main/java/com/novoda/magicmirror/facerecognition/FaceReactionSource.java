package com.novoda.magicmirror.facerecognition;

public interface FaceReactionSource {
    void start();

    void release();

    boolean onKeyDown(int keyCode);

    boolean onKeyUp(int keyCode);
}
