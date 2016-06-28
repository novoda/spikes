package net.bonysoft.magicmirror.facerecognition;

import android.os.Handler;

import java.util.Random;

public class DummyFaceSource implements FaceReactionSource {

    private static final int DELAY_MILLIS = 300;

    private final FaceTracker.FaceListener faceListener;
    private final Handler handler;
    private final Random smilingProbabilityGenerator;

    public DummyFaceSource(FaceTracker.FaceListener faceListener, Handler handler) {
        this.faceListener = faceListener;
        this.handler = handler;
        this.smilingProbabilityGenerator = new Random();
    }

    private final Runnable updateFaceRunnable = new Runnable() {
        @Override
        public void run() {
            FaceExpression expression = FaceExpression.fromSmilingProbability(smilingProbabilityGenerator.nextFloat());
            faceListener.onNewFace(expression);
            handler.postDelayed(this, DELAY_MILLIS);
        }
    };

    @Override
    public void start() {
        handler.postDelayed(updateFaceRunnable, DELAY_MILLIS);
    }

    @Override
    public void release() {
        handler.removeCallbacks(updateFaceRunnable);
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
