package com.gertherb.home;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.gertherb.R;
import com.gertherb.authentication.BetterAccountManager;
import com.gertherb.authentication.Token;
import com.novoda.notils.logger.simple.Log;

import rx.Observer;

public class HomeActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = new TextView(this);
        setContentView(textView);
        if(savedInstanceState == null) {
            tryLogin();
        }
    }

    private void tryLogin() {
        BetterAccountManager accountManager = new BetterAccountManager(this, getString(R.string.account_type), getString(R.string.token_type), null);
        accountManager.getSessionKeyOrAddAccount(null).subscribe(new Observer<Token>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("Error while fetching token", e);
                textView.setText(e.getLocalizedMessage());
            }

            @Override
            public void onNext(Token token) {
                Log.d("Got token " + token);
                textView.setText(token.getAuthToken());
            }
        });
    }
}
