package com.reeder.smartwatch.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reeder.smartwatch.Activities.MainActivity;
import com.reeder.smartwatch.Activities.UpdateUserInfoActivity;
import com.reeder.smartwatch.R;
import com.reeder.smartwatch.Activities.ResetPasswordActivity;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginEmailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginEmailFragment extends Fragment {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private static String TAG = "LoginActivity";
    private Button buttonSignIn;
    private CheckBox checkBoxRememberMe;
    FirebaseAuth auth;
    FirebaseUser user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginEmailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginEmailFragment newInstance(String param1, String param2) {
        LoginEmailFragment fragment = new LoginEmailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login_email, container, false);;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editTextEmail = (EditText) view.findViewById(R.id.email);
        editTextPassword = (EditText) view.findViewById(R.id.password);
        buttonSignIn = (Button) view.findViewById(R.id.email_sign_in_button);
        checkBoxRememberMe = (CheckBox) view.findViewById(R.id.checkboxRememberMe);
        TextView textViewRememberPassword = (TextView) view.findViewById(R.id.textViewRememberPassword);

        getUserEmail();
        buttonSignIn.setEnabled(false);
        buttonSignIn.getBackground().setAlpha(50);
        //showDialog();
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
                TextInputLayout emailTextInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
                if (email.isEmpty()) {
                    emailTextInputLayout.setError("Boş bırakılamaz!");
                    buttonSignIn.setEnabled(false);
                    buttonSignIn.getBackground().setAlpha(50);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    buttonSignIn.setEnabled(false);
                    buttonSignIn.getBackground().setAlpha(50);
                    emailTextInputLayout.setError("Geçerli bir mail adresi girin!");
                } else {
                    if (validate()) {
                        buttonSignIn.setEnabled(true);
                        buttonSignIn.getBackground().setAlpha(255);
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
                TextInputLayout passwordTextInputLayout = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);
                if (password.isEmpty()) {
                    buttonSignIn.setEnabled(false);
                    buttonSignIn.getBackground().setAlpha(50);
                    passwordTextInputLayout.setError("Boş bırakılamaz!");
                } else if (password.length() < 6) {
                    buttonSignIn.setEnabled(false);
                    buttonSignIn.getBackground().setAlpha(50);
                    passwordTextInputLayout.setError("6 Karakterden uzun olmalı!");
                } else if (password.length() > 32) {
                    buttonSignIn.setEnabled(false);
                    buttonSignIn.getBackground().setAlpha(50);
                    passwordTextInputLayout.setError("32 Karakterden kısa olmalı!");
                } else {
                    if (validate()) {
                        buttonSignIn.setEnabled(true);
                        buttonSignIn.getBackground().setAlpha(255);
                    }
                    passwordTextInputLayout.setError("");
                }
            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                signIn(email, password);
            }
        });

        textViewRememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                intent.putExtra("email",editTextEmail.getText().toString());
                startActivity(intent);
            }
        });
        return view;
    }

    public void signIn(String email, String password) {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setContentView(R.layout.login_dialog_layout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        final TextView textViewDialogMessage = (TextView) dialog.findViewById(R.id.dialogMessage);
        final TextView textViewResult = (TextView) dialog.findViewById(R.id.dialogResult);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.dialogProgress);

        progressBar.setVisibility(View.VISIBLE);
        final ImageView dialogImage = (ImageView) dialog.findViewById(R.id.dialogImageView);
        dialog.show();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = auth.getCurrentUser();
                    progressBar.setVisibility(View.INVISIBLE);
                    dialogImage.setVisibility(View.VISIBLE);
                    textViewDialogMessage.setText("Hoş geldin: " + user.getDisplayName());
                    if (checkBoxRememberMe.isChecked()) {
                        saveUserEmail();
                    } else {
                        deleteUserEmail();
                    }
                    textViewResult.setVisibility(View.VISIBLE);
                    textViewResult.setText("Başarılı");
                    dialogImage.setImageResource(R.drawable.accept_icon);
                    Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_animation);
                    dialogImage.startAnimation(slideUp);
                    textViewResult.startAnimation(slideUp);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkUserInfo();
                        }
                    }, 1000);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    dialogImage.setVisibility(View.VISIBLE);
                    textViewDialogMessage.setText("Giriş Başarısız");
                    textViewResult.setVisibility(View.VISIBLE);
                    textViewResult.setText("Hata:" + task.getException().getLocalizedMessage());
                    dialogImage.setImageResource(R.drawable.deny_icon);
                    Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_animation);
                    dialogImage.startAnimation(slideUp);
                    textViewResult.startAnimation(slideUp);
                }
            }
        });
    }

    private void checkUserInfo() {
        FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getData() != null){
                        Toast.makeText(getContext(), "Okey"+task.getResult(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: "+task.getResult().getData());
                        startActivity(new Intent(getContext(),MainActivity.class));
                    }else{
                        //Kayıt tamamlanmamış
                        Log.d(TAG, "onCompletes: "+task.getResult().getData());
                        startActivity(new Intent(getActivity(), UpdateUserInfoActivity.class));
                    }
                }else {
                    Toast.makeText(getContext(), "Hata:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean validate() {
        boolean valid = true;
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();


        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
        } else if (password.isEmpty() || password.length() < 5 || password.length() > 32) {
            valid = false;
        }

        return valid;
    }

    public void saveUserEmail() {
        SharedPreferences.Editor editor =
                getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE).edit();
        editor.putString("email", editTextEmail.getText().toString());
        editor.apply();
    }

    public void deleteUserEmail() {
        SharedPreferences.Editor editor =
                getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE).edit();
        editor.remove("email");
        editor.apply();
    }

    public void getUserEmail() {
        SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);
        String email = prefs.getString("email", "");
        if (email != null) {
            editTextEmail.setText(email);
            if (email.length() > 0) {
                checkBoxRememberMe.setChecked(true);
            }
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
