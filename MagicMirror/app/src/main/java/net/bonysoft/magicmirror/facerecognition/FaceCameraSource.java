package net.bonysoft.magicmirror.facerecognition;

import android.content.Context;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;
import com.novoda.notils.logger.simple.Log;

import java.io.IOException;

public class FaceCameraSource implements FaceReactionSource {

    private static final float CAMERA_SOURCE_REQUESTED_FPS = 30.0f;
    private static final int CAMERA_SOURCE_WIDTH = 640;
    private static final int CAMERA_SOURCE_HEIGHT = 360;
    private CameraSource cameraSource;
    private CameraSourcePreview preview;

    public static FaceReactionSource createFrom(Context context,
                                                FaceTracker.FaceListener faceListener,
                                                CameraSourcePreview preview)
            throws FaceDetectionUnavailableException {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new FaceTracker.Factory(faceListener))
                        .build()
        );
        if (!detector.isOperational()) {
            throw new FaceDetectionUnavailableException("Detector is not Operational");
        }
        CameraSource cameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(CAMERA_SOURCE_WIDTH, CAMERA_SOURCE_HEIGHT)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(CAMERA_SOURCE_REQUESTED_FPS)
                .build();
        return new FaceCameraSource(cameraSource, preview);
    }

    public FaceCameraSource(CameraSource cameraSource, CameraSourcePreview preview) {
        this.cameraSource = cameraSource;
        this.preview = preview;
    }

    @Override
    public void start() {
        try {
            preview.start(cameraSource);
        } catch (IOException e) {
            Log.e(e, "Unable to start camera source.");
            cameraSource.release();
        }
    }

    @Override
    public void release() {
        cameraSource.release();
    }

    @Override
    public boolean onKeyDown(int keyCode) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode) {
        return false;
    }
}
