package net.bonysoft.magicmirror.facerecognition;

public class FaceDetectionUnavailableException extends Exception {
    public FaceDetectionUnavailableException(String reason) {
        super(reason);
    }
}
