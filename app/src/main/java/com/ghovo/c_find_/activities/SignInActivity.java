package com.ghovo.c_find_.activities;



import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_USERS;
import static com.ghovo.c_find_.utilities.Constants.KEY_EMAIL;
import static com.ghovo.c_find_.utilities.Constants.KEY_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_IS_SIGNED_IN;
import static com.ghovo.c_find_.utilities.Constants.KEY_LATITUDE;
import static com.ghovo.c_find_.utilities.Constants.KEY_LONGITUDE;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_NAME;
import static com.ghovo.c_find_.utilities.Constants.KEY_VERIFIED;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.databinding.ActivitySignInBinding;
import com.ghovo.c_find_.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private PreferenceManager preferenceManager;
    private ActivitySignInBinding activitySignInBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());

        changeStatusBarColor();

        setContentView(activitySignInBinding.getRoot());
        setListeners();
        autoLogIn();

    }

    private void setListeners() {

        activitySignInBinding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        activitySignInBinding.buttonSignIn.setOnClickListener(v -> {

            if (isValidSignInDetails()) {

                if (firebaseUser != null) {

                    firebaseUser.reload();

                }

                if (isInsertedEmailSaved(activitySignInBinding.inputEmail.getText().toString())) {

                    isVerified();

                } else {

                    signIn();

                }

            }

        });

        activitySignInBinding.inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (Patterns.EMAIL_ADDRESS.matcher(charSequence.toString()).matches()) {

                    activitySignInBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activitySignInBinding.textEmail.setText("");

                } else {

                    activitySignInBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activitySignInBinding.textEmail.setText(R.string.unknown_email);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        activitySignInBinding.inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 6) {

                    activitySignInBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activitySignInBinding.textPassword.setText(R.string.poor_password);

                } else {

                    activitySignInBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activitySignInBinding.textPassword.setText("");

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

    }

    private void signIn() {

        loading(true);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(activitySignInBinding.inputEmail.getText().toString(),
                        activitySignInBinding.inputPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    loading(false);


                    if (task.isSuccessful()) {

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (firebaseUser != null) {

                            if (firebaseUser.isEmailVerified()) {

                                firestore.collection(KEY_COLLECTION_USERS)
                                        .whereEqualTo(KEY_EMAIL, activitySignInBinding.inputEmail.getText().toString())
                                        .get()
                                        .addOnCompleteListener(v -> {

                                            if (v.isSuccessful() && v.getResult() != null && v.getResult().getDocuments().size() > 0) {

                                                DocumentSnapshot documentSnapshot = v.getResult().getDocuments().get(0);
                                                preferenceManager.putString(KEY_USER_ID, documentSnapshot.getId());
                                                preferenceManager.putString(KEY_USER_NAME, documentSnapshot.getString(KEY_USER_NAME));
                                                preferenceManager.putString(KEY_IMAGE, documentSnapshot.getString(KEY_IMAGE));
                                                preferenceManager.putString(KEY_EMAIL, documentSnapshot.getString(KEY_EMAIL));

                                                preferenceManager.putBoolean(KEY_VERIFIED, true);
                                                preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true);
                                                Intent bobo = getIntent();
                                                String lat = bobo.getStringExtra(KEY_LATITUDE);
                                                String lng = bobo.getStringExtra(KEY_LONGITUDE);
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.putExtra(KEY_LATITUDE, lat);
                                                intent.putExtra(KEY_LONGITUDE,lng);
                                                startActivity(intent);
                                                finish();

                                            } else {

                                                loading(false);
                                                showToast("This account isn't sign up");

                                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                                if (firebaseUser != null) {
                                                    firebaseUser.delete();
                                                }

                                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                                finish();

                                            }
                                        });

                            }

                        }

                    } else if (Objects.equals(
                            Objects.requireNonNull(task.getException()).getClass(),
                            FirebaseAuthInvalidUserException.class)) {

                        inputErrorVisualisation(1);
                        showToast("Invalid Email");

                    } else if (Objects.equals(
                            task.getException().getClass(),
                            FirebaseAuthInvalidCredentialsException.class)) {

                        inputErrorVisualisation(2);
                        showToast("Incorrect Password");

                    } else {

                        showToast("Doesn't Sign in");

                    }
                });
    }

    private void loading(Boolean isLoading) {

        if (isLoading) {

            activitySignInBinding.buttonSignIn.setVisibility(View.INVISIBLE);
            activitySignInBinding.progressBar.setVisibility(View.VISIBLE);

        } else {

            activitySignInBinding.buttonSignIn.setVisibility(View.VISIBLE);
            activitySignInBinding.progressBar.setVisibility(View.INVISIBLE);

        }

    }

    private Boolean isValidSignInDetails() {

        if (activitySignInBinding.inputEmail.getText().toString().trim().isEmpty()) {

            showToast("Enter Email");
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(activitySignInBinding.inputEmail.getText().toString()).matches()) {

            showToast("Enter Email");
            return false;

        } else if (activitySignInBinding.inputPassword.getText().toString().trim().isEmpty()) {

            showToast("Enter Password");
            return false;

        } else if (activitySignInBinding.inputPassword.getText().toString().trim().getBytes().length < 6) {

            showToast("Password is short");
            return false;

        } else {

            return true;

        }

    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void isVerified() {

        if (firebaseUser != null) {

            firebaseUser.reload();

            try {

                TimeUnit.MILLISECONDS.sleep(50);

                firebaseUser.reload();

                if (firebaseUser.isEmailVerified()) {

                    signIn();

                } else {

                    showToast("Verify Account");
                    firebaseUser.sendEmailVerification();

                }

            } catch (InterruptedException e) {

                throw new RuntimeException(e);

            }

        } else {

            signIn();

        }

    }

    private boolean isInsertedEmailSaved(String email) {

        return email.equals(preferenceManager.getString(KEY_EMAIL));

    }

    private void autoLogIn() {

        if (firebaseUser != null) {
            firebaseUser.reload();

            try {

                firebaseUser.reload();

                TimeUnit.MILLISECONDS.sleep(50);

                firebaseUser.reload();

                if (firebaseUser.isEmailVerified()) {

                    if (preferenceManager.getBoolean(KEY_IS_SIGNED_IN) && preferenceManager.getBoolean(KEY_VERIFIED)) {

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    }

                } else {

                    showToast("Verify your account");
                    firebaseUser.sendEmailVerification();

                }

            } catch (InterruptedException e) {

                throw new RuntimeException(e);

            }

        }

    }

    private void changeStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white, getTheme()));
    }

    private void inputErrorVisualisation(int code) {

        if (code == 1) {

            activitySignInBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_incorrect_input
            ));

            activitySignInBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_correct_input
            ));

            activitySignInBinding.textPassword.setText("");
            activitySignInBinding.textEmail.setText(R.string.unknown_email);

        } else if (code == 2) {

            activitySignInBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_correct_input
            ));

            activitySignInBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_incorrect_input
            ));

            activitySignInBinding.textEmail.setText("");
            activitySignInBinding.textPassword.setText(R.string.wrong_password);

        }

    }

}