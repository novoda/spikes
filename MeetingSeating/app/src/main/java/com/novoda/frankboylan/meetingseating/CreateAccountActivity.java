package com.novoda.frankboylan.meetingseating;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.frankboylan.meetingseating.network.ConnectionStatus;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    EditText newEmailPrefix, newPassword, newFirstname, newSurname;
    Spinner newEmailPostfix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        auth = FirebaseAuth.getInstance();

        newEmailPrefix = findViewById(R.id.et_new_email);
        newPassword = findViewById(R.id.et_new_password);
        newFirstname = findViewById(R.id.et_new_firstname);
        newSurname = findViewById(R.id.et_new_surname);
        newEmailPostfix = findViewById(R.id.spn_email_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                                                             R.array.end_of_email_options, R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newEmailPostfix.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void handlerCreateAccountSubmit(View v) {
        if (!ConnectionStatus.hasActiveInternetConnection()) {
            makeToast("You're not connected to the Internet!");
            return;
        }
        if (!formsAreValid()) {
            return;
        }

        final String concatEmail = newEmailPrefix.getText().toString() + newEmailPostfix.getSelectedItem().toString();
        auth.createUserWithEmailAndPassword(concatEmail, newPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            makeToast("Account Created!");
                            
                            DatabaseReference fb = FirebaseDatabase.getInstance().getReference();

                            fb.child("users").child(auth.getUid()).child("email").setValue(concatEmail);
                            fb.child("users").child(auth.getUid()).child("firstname").setValue(newFirstname.getText().toString());
                            fb.child("users").child(auth.getUid()).child("surname").setValue(newSurname.getText().toString());

                            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                            intent.putExtra("email", concatEmail);
                            startActivity(intent);
                            finish();
                        } else {
                            // Account Creation failed!
                            Toast.makeText(CreateAccountActivity.this, "Account creation failed:  " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public boolean formsAreValid() {
        String email = newEmailPrefix.getText().toString();
        String password = newPassword.getText().toString();
        String firstname = newFirstname.getText().toString();
        String surname = newSurname.getText().toString();

        if (firstname.length() < 2) {
            makeToast("Please enter a longer Firstname!");
            return false;
        }
        if (!firstname.matches("[a-zA-Z]+")) {
            makeToast("Ensure there is only letters in your Firstname!");
            return false;
        }

        if (surname.length() < 2) {
            makeToast("Please enter a longer Surname!");
            return false;
        }
        if (!surname.matches("[a-zA-Z]+")) {
            makeToast("Ensure there is only letters in your Surname!");
            return false;
        }

        if (email.length() < 2) {
            makeToast("Email must be longer than 2 characters!");
            return false;
        }
        if (!email.matches("^[a-zA-Z0-9]*$")) {
            makeToast("Email must contain only letters & numbers!");
            return false;
        }

        if (password.length() < 6) {
            makeToast("Password must be longer than 6 characters!");
            return false;
        }

        // User data passes all validation checks.
        return true;
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
