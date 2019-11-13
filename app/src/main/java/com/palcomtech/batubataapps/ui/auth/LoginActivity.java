package com.palcomtech.batubataapps.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.ui.dashboard.DashboardActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String LOGOUT = "logout";
    public static final int RESULT_LOGOUT = 911;

    @BindView(R.id.tlEmail) TextInputLayout txtEmail;
    @BindView(R.id.tlPassword) TextInputLayout txtPass;
    @BindView(R.id.email) TextInputEditText edtEmail;
    @BindView(R.id.password) TextInputEditText edtPass;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;
    private String email, password;
    private View focusView = null;
    private FirebaseAuth mAuth;

    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        initAuth();

        edtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void initAuth() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, DashboardActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    private boolean validate() {
        boolean valid = true;

        email = Objects.requireNonNull(txtEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(txtPass.getEditText()).getText().toString();

        if (TextUtils.isEmpty(password)) {
            txtPass.setError(getString(R.string.error_field_required));
            focusView = txtPass;
            valid = false;
        } else if (!isPasswordValid(password)) {
            txtPass.setError(getString(R.string.error_invalid_password));
            focusView = txtPass;
            valid = false;
        } else {
            txtPass.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.error_field_required));
            focusView = txtEmail;
            valid = false;
        } else if (!isEmailValid(email)) {
            txtEmail.setError(getString(R.string.error_invalid_email));
            focusView = txtEmail;
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        return valid;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    @OnClick(R.id.btnSignIn) void attemptLogin() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) {
            return;
        }

        email = Objects.requireNonNull(txtEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(txtPass.getEditText()).getText().toString();

        barUtils.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            edtEmail.setText(null);
                            edtPass.setText(null);
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_login_success), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        } else {
                            snackbarUtils.snackBarLong(getString(R.string.msg_login_failed));
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                txtEmail.setError(getString(R.string.error_credentials_email));
                                txtEmail.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException invalidPassword) {
                                txtPass.setError(getString(R.string.error_incorrect_password));
                                txtPass.requestFocus();
                            } catch (FirebaseAuthException auth) {
                                Toast.makeText(getApplicationContext(), auth.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                snackbarUtils.snackbarShort(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        barUtils.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

