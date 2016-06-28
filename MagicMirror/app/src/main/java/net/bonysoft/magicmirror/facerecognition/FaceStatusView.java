package net.bonysoft.magicmirror.facerecognition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FaceStatusView extends TextView {

    public FaceStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void setExpression(FaceExpression expression) {
        setText(expression.toString());
    }
}
