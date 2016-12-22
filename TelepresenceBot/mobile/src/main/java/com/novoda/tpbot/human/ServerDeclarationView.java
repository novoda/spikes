package com.novoda.tpbot.human;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.R;

public class ServerDeclarationView extends LinearLayout {

    private ServerDeclarationListener serverDeclarationListener = ServerDeclarationListener.NO_OP;

    public ServerDeclarationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_server_declaration, this);

        final EditText serverAddressDeclaration = Views.findById(this, R.id.bot_server_declaration);
        View connectToServer = Views.findById(this, R.id.bot_server_connect);

        connectToServer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverAddress = String.valueOf(serverAddressDeclaration.getText());
                serverDeclarationListener.onConnect(serverAddress);
            }
        });
    }

    public void setServerDeclarationListener(ServerDeclarationListener serverDeclarationListener) {
        this.serverDeclarationListener = serverDeclarationListener;
    }

}
