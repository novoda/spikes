package com.novoda.voiceshutters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private static final List<AuthUI.IdpConfig> PROVIDERS = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Log.d("TUT", "NEW START");
    }

    public void onSelectSignIn(View v) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(PROVIDERS)
                        .build(),
                RC_SIGN_IN
        );
    }

    public void onSelectSignOut(View v) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> Log.d("TUT", "signed out"));
    }

    public void onSelectClient(View v) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // TODO what is a unique identifier we can save
                // that will be useful on all platforms
                // and is the same even if we sign in twice by accident

                String uid = user.getUid();
                Log.d("TUT", "signed in " + uid);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> userData = new HashMap<>();
                userData.put("platform", "Android"); // just some junk so we create the user

                db.collection("users").document(uid)
                        .set(userData, SetOptions.merge())
                        .addOnSuccessListener(
                                aVoid ->
                                        Log.d("TUT", "DocumentSnapshot added with ID: " + uid))
                        .addOnFailureListener(
                                e ->
                                        Log.w("TUT", "Error adding document", e));

            } else {
                if (response == null) {
                    Log.e("TUT", "sign in failed " + response.getErrorCode());
                } else {
                    Log.e("TUT", "sign in failed, and no response (cancelled by user)");
                }
            }
        }
    }

}
