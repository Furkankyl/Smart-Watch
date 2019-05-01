package com.reeder.smartwatch.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reeder.smartwatch.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private static String TAG = "ResetPasswordActivity";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText editTextEmail;
    Button buttonResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();
        buttonResetPassword = (Button) findViewById(R.id.emailResetPasswordButton);
        editTextEmail = (EditText) findViewById(R.id.email);
        if (getIntent().getStringExtra("email") != null && !getIntent().getStringExtra("email").isEmpty()) {
            editTextEmail.setText(getIntent().getStringExtra("email"));
            buttonResetPassword.setEnabled(true);
            buttonResetPassword.getBackground().setAlpha(255);
        }else{
            buttonResetPassword.setEnabled(false);
            buttonResetPassword.getBackground().setAlpha(50);
        }

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: " + charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: " + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editTextEmail.getText().toString();
                TextInputLayout emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailTextInputLayout);
                if (email.isEmpty()) {
                    emailTextInputLayout.setError("Boş bırakılamaz!");
                    buttonResetPassword.setEnabled(false);
                    buttonResetPassword.getBackground().setAlpha(50);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    buttonResetPassword.setEnabled(false);
                    buttonResetPassword.getBackground().setAlpha(50);
                    emailTextInputLayout.setError("Geçerli bir mail adresi girin!");
                } else {
                    buttonResetPassword.setEnabled(true);
                    buttonResetPassword.getBackground().setAlpha(255);
                    emailTextInputLayout.setError("");
                }
            }
        });


        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                sendRequest(email);
            }
        });
    }

    public void sendRequest(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Mail gönderildi", Toast.LENGTH_SHORT).show();
                    buttonResetPassword.setEnabled(false);
                    buttonResetPassword.getBackground().setAlpha(50);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Mail gönderildi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
