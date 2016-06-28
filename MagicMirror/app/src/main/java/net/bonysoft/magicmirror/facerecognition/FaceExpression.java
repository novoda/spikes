package net.bonysoft.magicmirror.facerecognition;

import com.novoda.notils.exception.DeveloperError;

import net.bonysoft.magicmirror.sfx.Particle;

public enum FaceExpression {
    SAD(0.05f, 0x1F614),
    NEUTRAL(0.25f, 0x1F613),
    HAPPY(0.7f, 0x1F60A),
    JOYFUL(1.0f, 0x1F604),
    LOOKING(Float.MAX_VALUE, 0x1F440);

    private final float threshold;
    private final int emojiUnicode;

    FaceExpression(float threshold, int emojiUnicode) {
        this.threshold = threshold;
        this.emojiUnicode = emojiUnicode;
    }

    @Override
    public String toString() {
        return new String(Character.toChars(emojiUnicode));
    }

    public static FaceExpression fromSmilingProbability(float smilingProbability) {
        for (FaceExpression faceExpression : values()) {
            if (smilingProbability <= faceExpression.threshold) {
                return faceExpression;
            }
        }
        throw new DeveloperError("FaceExpression not found with smiling probability: " + smilingProbability);
    }

    public boolean isMissing() {
        return this == LOOKING;
    }
}
