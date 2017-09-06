package com.novoda.tpbot.controls;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.LastServerPreferences;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServiceDeclarationListener;

public class ServerDeclarationView extends LinearLayout {

    private ServiceDeclarationListener serviceDeclarationListener = ServiceDeclarationListener.NO_OP;

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
                String serverAddress = serverAddressDeclaration.getText().toString();
                serviceDeclarationListener.onServiceConnected(serverAddress);
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        LastServerPreferences lastServerPreferences = new LastServerPreferences(sharedPreferences);
        if (lastServerPreferences.containsLastConnectedServer()) {
            serverAddressDeclaration.setText(lastServerPreferences.getLastConnectedServer());
        }
    }

    public void setServiceDeclarationListener(ServiceDeclarationListener serviceDeclarationListener) {
        this.serviceDeclarationListener = serviceDeclarationListener;
    }

}
