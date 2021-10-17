package com.example.medi_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private FirebaseAuth mAuth;
    private EditText editEmail, editPassword;
    private Button registerButton;
    private SignInButton googleRegisterButton;
    private LoginButton facebookRegisterButton;
    private CallbackManager mCallbackManager;
    private TextView loginRedirect;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private ProgressBar formProgressBar;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText popup_firstName, popup_lastname, popup_dob, popup_phone;
    private Button popUpSave, popUpCancel, datePickerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        loginRedirect = (TextView) findViewById(R.id.log_in_text);
        loginRedirect.setOnClickListener(this);

        registerButton = (Button) findViewById(R.id.sign_up_button);
        registerButton.setOnClickListener(this);

        googleRegisterButton = (SignInButton) findViewById(R.id.google_sign_up_button);
        googleRegisterButton.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.sign_up_email);
        editPassword = (EditText) findViewById(R.id.sign_up_pw);

        requestGoogleSignIn();
        mCallbackManager = CallbackManager.Factory.create();
        facebookRegisterButton = findViewById(R.id.facebook_sign_up_button);
        facebookRegisterButton.setReadPermissions("email", "public_profile");
        facebookRegisterButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onError(@NonNull FacebookException e) {
                Toast.makeText(Register.this, "facebook:on-error", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(Register.this, "facebook: on-cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
        });
    }

    private void requestGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.log_in_text:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.sign_up_button:
                registerUser();
                break;
            case R.id.google_sign_up_button:
                registerGoogle();
                break;
        }
    }
    private void registerGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            String email = mAuth.getCurrentUser().getEmail();
                            User user = new User(email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user);
                            startActivity(new Intent(Register.this, Dashboard.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String email = account.getEmail();

                            User user = new User(email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user);

                            startActivity(new Intent(Register.this, Dashboard.class));
                        } else {
                            Toast.makeText(Register.this, "Cannot login via Google", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        month += 1;
        String date = dayOfMonth + "/" + month + "/" + year;
        popup_dob.setText(date);
    }

    public void createFormPopUp(String email, String password){
        dialogBuilder = new AlertDialog.Builder(this);
        final View formPopUpView = getLayoutInflater().inflate(R.layout.form_popup, null);
        popup_firstName = (EditText) formPopUpView.findViewById(R.id.form_popup_fname);
        popup_lastname = (EditText) formPopUpView.findViewById(R.id.form_popup_lname);
        popup_dob = (EditText) formPopUpView.findViewById(R.id.form_popup_dob);
        popup_phone = (EditText) formPopUpView.findViewById(R.id.form_popup_phone);

        popUpSave = (Button) formPopUpView.findViewById(R.id.form_popup_save);
        popUpCancel = (Button) formPopUpView.findViewById(R.id.form_popup_cancel);
        datePickerButton = (Button) formPopUpView.findViewById(R.id.date_picker_button);

        formProgressBar = (ProgressBar) formPopUpView.findViewById(R.id.form_progressBar);

        dialogBuilder.setView(formPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        popUpSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formProgressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String fname = popup_firstName.getText().toString().trim();
                                    String lname = popup_lastname.getText().toString().trim();
                                    String dob = popup_dob.getText().toString().trim();
                                    String phone = popup_phone.getText().toString().trim();
                                    User user = new User(email, fname, lname, dob, phone);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                user.sendEmailVerification();
                                                Toast.makeText(Register.this, "Registration successful, check email to verify account", Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(Register.this, MainActivity.class));
                                                formProgressBar.setVisibility(View.GONE);
                                            } else {
                                                Toast.makeText(Register.this, "Failed to register user!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(Register.this, "Failed to register user! firebase prob?", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        popUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //define cancel btn
                dialog.dismiss();
            }
        });
    }

    private void registerUser(){
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(email.isEmpty()){
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Invalid email format!");
            editEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editPassword.setError("Password is required!");
            editPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            editPassword.setError("Password length should be at least 6 characters!!!");
            editPassword.requestFocus();
            return;
        }

        createFormPopUp(email, password);
    }

}