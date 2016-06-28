package net.bonysoft.magicmirror.facerecognition;

import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public class KeyToFaceMappings {

    private final Map<Integer, FaceExpression> faceExpressions;

    public static KeyToFaceMappings newInstance() {
        Map<Integer, FaceExpression> expressions = new HashMap<>();
        expressions.put(KeyEvent.KEYCODE_1, FaceExpression.SAD);
        expressions.put(KeyEvent.KEYCODE_2, FaceExpression.NEUTRAL);
        expressions.put(KeyEvent.KEYCODE_3, FaceExpression.HAPPY);
        expressions.put(KeyEvent.KEYCODE_4, FaceExpression.JOYFUL);
        return new KeyToFaceMappings(expressions);
    }

    public KeyToFaceMappings(Map<Integer, FaceExpression> faceExpressions) {
        this.faceExpressions = faceExpressions;
    }

    public FaceExpression getFaceFromKeyCode(int keyCode) {
        return faceExpressions.get(keyCode);
    }

    public boolean contains(int keyCode) {
        return faceExpressions.containsKey(keyCode);
    }
}
