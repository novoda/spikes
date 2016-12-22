package com.novoda.tpbot.human;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.novoda.tpbot.R;

public class ServerDeclarationView extends LinearLayout {

    public ServerDeclarationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_server_declaration, this);
    }

}
