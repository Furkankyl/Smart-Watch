package com.reeder.smartwatch.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.reeder.smartwatch.R;

public class RegisterActivity extends AppCompatActivity {
    private static String TAG = "RegisterActivity";
    private EditText editTextDisplayName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRepeatedPassword;
    private Button buttonRegister;
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonRegister = (Button) findViewById(R.id.email_register_button);
        editTextRepeatedPassword = (EditText) findViewById(R.id.passwordRepeated);
        editTextDisplayName = (EditText) findViewById(R.id.displayName);
        buttonRegister.setEnabled(false);
        buttonRegister.getBackground().setAlpha(50);
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
                    buttonRegister.setEnabled(false);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    buttonRegister.setEnabled(false);
                    emailTextInputLayout.setError("Geçerli bir mail adresi girin!");
                } else {
                    if(validate()){
                        buttonRegister.setEnabled(true);
                        buttonRegister.getBackground().setAlpha(255);
                    }
                    emailTextInputLayout.setError("");
                }
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
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
                String password = editTextPassword.getText().toString();
                TextInputLayout passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
                if (password.isEmpty()) {
                    buttonRegister.setEnabled(false);
                    passwordTextInputLayout.setError("Boş bırakılamaz!");
                } else if (password.length() < 6) {
                    buttonRegister.setEnabled(false);
                    passwordTextInputLayout.setError("6 Karakterden uzun olmalı!");
                } else if (password.length() > 32) {
                    buttonRegister.setEnabled(false);
                    passwordTextInputLayout.setError("32 Karakterden kısa olmalı!");
                } else {
                    if(validate()){
                        buttonRegister.setEnabled(true);
                        buttonRegister.getBackground().setAlpha(255);
                    }
                    passwordTextInputLayout.setError("");
                }
            }
        });
        editTextRepeatedPassword.addTextChangedListener(new TextWatcher() {
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
                String password = editTextRepeatedPassword.getText().toString();
                TextInputLayout repeatedPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordRepeatedTextInputLayout);
                if (password.isEmpty()) {
                    buttonRegister.setEnabled(false);
                    repeatedPasswordTextInputLayout.setError("Boş bırakılamaz!");
                } else if (password.length() < 6) {
                    buttonRegister.setEnabled(false);
                    repeatedPasswordTextInputLayout.setError("6 Karakterden uzun olmalı!");
                } else if (password.length() > 32) {
                    buttonRegister.setEnabled(false);
                    repeatedPasswordTextInputLayout.setError("32 Karakterden kısa olmalı!");
                }else if (!editTextPassword.getText().toString().equals(password)) {
                    buttonRegister.setEnabled(false);
                    repeatedPasswordTextInputLayout.setError("Parolalar uyuşmuyor!");
                } else {
                    if(validate()){
                        buttonRegister.setEnabled(true);
                        buttonRegister.getBackground().setAlpha(255);
                    }
                    repeatedPasswordTextInputLayout.setError("");
                }
            }
        });
        editTextDisplayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String displayName = editTextDisplayName.getText().toString();
                TextInputLayout displayNameTextInputLayout = (TextInputLayout) findViewById(R.id.displayNameTextInputLayout);
                if (displayName.isEmpty()) {
                    buttonRegister.setEnabled(false);
                    displayNameTextInputLayout.setError("Boş bırakılamaz!");
                } else if (displayName.length() < 6) {
                    buttonRegister.setEnabled(false);
                    displayNameTextInputLayout.setError("6 Karakterden uzun olmalı!");
                } else {
                    if(validate()){
                        buttonRegister.setEnabled(true);
                        buttonRegister.getBackground().setAlpha(255);
                    }
                    displayNameTextInputLayout.setError("");
                }
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String displayName = editTextDisplayName.getText().toString();
                register(email,password,displayName);
            }
        });
    }

    private void register(String email, String password, final String displayName) {
        Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.register_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.dialogProgress);
        progressBar.setVisibility(View.VISIBLE);
        final ImageView dialogImage = (ImageView) dialog.findViewById(R.id.dialogImageView);
        final TextView textViewDialogMessage = (TextView) dialog.findViewById(R.id.dialogMessage);
        final TextView textViewResult = (TextView) dialog.findViewById(R.id.dialogResult);
        dialog.show();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                dialogImage.setVisibility(View.VISIBLE);

                Animation slideUp = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_in_animation);
                dialogImage.startAnimation(slideUp);
            }
        }, 3000);

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = auth.getCurrentUser();
                    progressBar.setVisibility(View.INVISIBLE);
                    dialogImage.setVisibility(View.VISIBLE);
                    textViewDialogMessage.setText("İşlem tamamlanıyor...");
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName).build();
                    user.updateProfile(profileUpdates);
                    textViewDialogMessage.setText(displayName+"  Kaydınız tamamlandı.");
                    textViewResult.setVisibility(View.VISIBLE);
                    textViewResult.setText("Başarılı");
                    dialogImage.setImageResource(R.drawable.accept_icon);
                    Animation slideUp = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_in_animation);
                    dialogImage.startAnimation(slideUp);
                    textViewResult.startAnimation(slideUp);
                        saveUserEmail();
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            startActivity(new Intent(RegisterActivity.this,UpdateUserInfoActivity.class));
                            RegisterActivity.this.finish();
                        }
                    }, 1000);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    dialogImage.setVisibility(View.VISIBLE);
                    textViewDialogMessage.setText("Giriş Başarısız");
                    textViewResult.setVisibility(View.VISIBLE);
                    textViewResult.setText("Hata:"+task.getException().getLocalizedMessage());
                    dialogImage.setImageResource(R.drawable.deny_icon);
                    Animation slideUp = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_in_animation);
                    dialogImage.startAnimation(slideUp);
                    textViewResult.startAnimation(slideUp);
                }
            }
        });
    }
    public void saveUserEmail(){
        SharedPreferences.Editor editor =
                getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
        editor.putString("email", editTextEmail.getText().toString());
        editor.apply();
    }
    private boolean validate(){
        boolean valid = true;
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String displayName = editTextDisplayName.getText().toString();
        String repeatedPassword = editTextRepeatedPassword.getText().toString();

        if(email.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            valid = false;
        }
        else if(password.isEmpty() || password.length() < 5 || password.length() > 32 ){
            valid = false;
        }else if(!password.equals(repeatedPassword) ){
            valid = false;
        }else if(displayName.isEmpty()){
            valid = false;
        }

        return valid;
    }
}
