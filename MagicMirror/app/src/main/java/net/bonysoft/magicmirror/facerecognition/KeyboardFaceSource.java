package net.bonysoft.magicmirror.facerecognition;

public class KeyboardFaceSource implements FaceReactionSource {

    private static final FaceExpression NEUTRAL_EXPRESSION = FaceExpression.LOOKING;

    private final FaceTracker.FaceListener faceListener;
    private final KeyToFaceMappings mappings;

    private FaceExpression currentExpression = FaceExpression.LOOKING;

    public KeyboardFaceSource(FaceTracker.FaceListener faceListener, KeyToFaceMappings mappings) {
        this.faceListener = faceListener;
        this.mappings = mappings;
    }

    public boolean onKeyDown(int keyCode) {
        if (sameKeyCodeIsTriggered(keyCode)) {
            return false;
        }
        FaceExpression faceExpression = mappings.getFaceFromKeyCode(keyCode);
        if (faceExpression != null) {
            currentExpression = faceExpression;
            faceListener.onNewFace(faceExpression);
            return true;
        }
        currentExpression = NEUTRAL_EXPRESSION;
        return false;
    }

    private boolean sameKeyCodeIsTriggered(int keyCode) {
        FaceExpression mappedExpression = mappings.getFaceFromKeyCode(keyCode);
        if (mappedExpression == null) {
            return currentExpression == NEUTRAL_EXPRESSION;
        }
        return currentExpression == mappedExpression;
    }

    public boolean onKeyUp(int keyCode) {
        if (holdsAnExpression() && sameKeyCodeIsTriggered(keyCode)) {
            faceListener.onNewFace(NEUTRAL_EXPRESSION);
            currentExpression = NEUTRAL_EXPRESSION;
        }
        return true;
    }

    private boolean holdsAnExpression() {
        return currentExpression != NEUTRAL_EXPRESSION;
    }

    @Override
    public void start() {
        // no-op
    }

    @Override
    public void release() {
        // no-op
    }
}
