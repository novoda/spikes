package com.novoda.frankboylan.meetingseating;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteDelete;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteUpdate;
import com.novoda.frankboylan.meetingseating.network.ConnectionStatus;
import com.novoda.frankboylan.meetingseating.seats.SeatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth auth;
    TextView tvEmail, tvPassword;
    String snackbarShown = "";
    CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Clear any cached data
        SQLiteDelete sqliteDelete = new SQLiteDelete(this);
        sqliteDelete.clearSeatCache();
        sqliteDelete.close();

        SQLiteUpdate sqliteUpdate = new SQLiteUpdate(this);
        sqliteUpdate.setMetaCacheToInactive();

        tvEmail = findViewById(R.id.tv_login_email);
        tvPassword = findViewById(R.id.tv_login_password);
        cbRememberMe = findViewById(R.id.cb_remember_me);

        SharedPreferences loginPrefs = getSharedPreferences("saveLogin", MODE_PRIVATE);

        if (loginPrefs.getBoolean("saveLogin", false)) {
            tvEmail.setText(loginPrefs.getString("email", ""));
            tvPassword.setText(loginPrefs.getString("password", ""));
            cbRememberMe.setChecked(true);
        }

        if (getIntent().getExtras() != null) {
            tvEmail.setText(getIntent().getExtras().getString("email"));
        }

        auth = FirebaseAuth.getInstance();
        auth.signOut();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkNetworkState();
                handler.postDelayed(this, 4000);
            }
        }, 300);
    }

    private void checkNetworkState() {
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnSignup = findViewById(R.id.btn_signup);

        if (isNetworkAvailable()) {
            btnLogin.setEnabled(true);
            btnSignup.setEnabled(true);

            if (!snackbarShown.equals("connected")) {
                Snackbar.make(findViewById(R.id.cl_login_activity), "Connected", Snackbar.LENGTH_SHORT).show();
                snackbarShown = "connected";
            }

        } else {
            btnLogin.setEnabled(false);
            btnSignup.setEnabled(false);

            if (!snackbarShown.equals("offline")) {
                Snackbar.make(findViewById(R.id.cl_login_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Offline Mode", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handlerOfflineLogin(v);
                            }
                        })
                        .show();
                snackbarShown = "offline";
            }
        }
    }

    private boolean isNetworkAvailable() {
        return ConnectionStatus.hasActiveInternetConnection();
    }

    /**
     * Button Handler method - Login
     */
    public void handlerLogin(View v) {
        final String email = tvEmail.getText().toString();
        final String pass = tvPassword.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            makeToast("Missing fields!");
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences loginPrefs = getSharedPreferences("saveLogin", MODE_PRIVATE);
                            SharedPreferences.Editor loginPrefsEditor = loginPrefs.edit();
                            if (cbRememberMe.isChecked()) { // Save user data to SharedPreferences
                                loginPrefsEditor.putBoolean("saveLogin", true);
                                loginPrefsEditor.putString("email", email);
                                loginPrefsEditor.putString("password", pass);
                                loginPrefsEditor.commit();
                            } else { // Clear any stored SharedPreferences
                                loginPrefsEditor.clear();
                                loginPrefsEditor.commit();
                            }
                            startActivity(new Intent(LoginActivity.this, SeatActivity.class));
                            finish();
                            return;
                        }
                        makeToast("Email & Password combo not recognised!");
                    }
                });

    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Button Handler method - Create Account
     */
    public void handlerCreateAccount(View v) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Button Handler method - Offline Mode
     */
    public void handlerOfflineLogin(View v) {
        Intent intent = new Intent(this, SeatActivity.class);
        startActivity(intent);
        finish();
    }

}
