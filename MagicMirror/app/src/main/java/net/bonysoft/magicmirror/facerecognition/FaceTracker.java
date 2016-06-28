package net.bonysoft.magicmirror.facerecognition;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class FaceTracker extends Tracker<Face> {

    private final FaceListener faceListener;

    private FaceExpression currentExpression;

    private FaceTracker(FaceListener faceListener) {
        this.faceListener = faceListener;
    }

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        float smiling = face.getIsSmilingProbability();
        FaceExpression expression = FaceExpression.fromSmilingProbability(smiling);
        setNewFace(expression);
    }

    private void setNewFace(FaceExpression expression) {
        if (currentExpression != expression) {
            faceListener.onNewFace(expression);
            currentExpression = expression;
        }
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        setNewFace(FaceExpression.LOOKING);
    }

    @Override
    public void onDone() {
        setNewFace(FaceExpression.LOOKING);
    }

    public static class Factory implements MultiProcessor.Factory<Face> {
        private final FaceListener faceListener;

        public Factory(FaceListener faceListener) {
            this.faceListener = faceListener;
        }

        @Override
        public Tracker<Face> create(Face face) {
            return new FaceTracker(faceListener);
        }
    }

    public interface FaceListener {

        void onNewFace(FaceExpression expression);

    }
}
