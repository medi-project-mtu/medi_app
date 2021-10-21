package com.example.medi_android;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private EditText editEmail, editPassword;
    private Button registerButton;
    private SignInButton googleRegisterButton;
    private LoginButton facebookRegisterButton;
    private CallbackManager mCallbackManager;
    private TextView loginRedirect;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private final static int PROVIDER_GOOGLE = 1;
    private final static int PROVIDER_EMAIL= 0;
    private final static int POPUP_SETUP = 555;

    private ProgressBar formProgressBar;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText popup_name, popup_height, popup_weight, popup_dob;
    private Spinner spinner_gender;
    private Button popUpSave, popUpCancel;

    private User user;


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
        if(getIntent().getBooleanExtra("Profile Setup", false)){
            registerGoogle();
        }
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
        user = new User();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(Register.this, Dashboard.class));
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //here i check if this email is new or not
                //if the email has no signInMethods, it is new, else it exists
                mAuth.fetchSignInMethodsForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.getResult().getSignInMethods().size() == 0){
                            createPopUpForm(account, null, null, PROVIDER_GOOGLE);
                        } else {
                            firebaseAuthWithGoogle(account);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Something went wrong with google sign-in", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,this,
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

    public void createPopUpForm(GoogleSignInAccount account, String email, String password, int provider){
        dialogBuilder = new AlertDialog.Builder(this);
        final View formPopUpView = getLayoutInflater().inflate(R.layout.form_popup, null);

        popup_name = (EditText) formPopUpView.findViewById(R.id.form_popup_name);
        popup_dob = (EditText) formPopUpView.findViewById(R.id.form_popup_dob);
        popup_height = (EditText) formPopUpView.findViewById(R.id.form_popup_height);
        popup_weight = (EditText) formPopUpView.findViewById(R.id.form_popup_weight);

        popUpSave = (Button) formPopUpView.findViewById(R.id.form_popup_save);
        popUpCancel = (Button) formPopUpView.findViewById(R.id.form_popup_cancel);

        spinner_gender = (Spinner) formPopUpView.findViewById(R.id.spinner_gender);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter);
        spinner_gender.setOnItemSelectedListener(this);

        formProgressBar = (ProgressBar) formPopUpView.findViewById(R.id.form_progressBar);

        dialogBuilder.setView(formPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        popup_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        popUpSave.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String profileName = popup_name.getText().toString().trim();
                 String profileDOB = popup_dob.getText().toString().trim();
                 String profileHeight = popup_height.getText().toString().trim();
                 String profileWeight = popup_weight.getText().toString().trim();

                 if (profileName.isEmpty()) {
                     popup_name.setError("Name is required!");
                     popup_name.requestFocus();
                     return;
                 }
                 if (profileDOB.isEmpty()) {
                     popup_dob.setError("Date of Birth is required!");
                     popup_dob.requestFocus();
                     return;
                 }
                 if (profileHeight.isEmpty()) {
                     popup_height.setError("Height is required!");
                     popup_height.requestFocus();
                     return;
                 }
                 if (profileWeight.isEmpty()) {
                     popup_weight.setError("Weight is required!");
                     popup_weight.requestFocus();
                     return;
                 }
                 formProgressBar.setVisibility(View.VISIBLE);
                 if (provider == PROVIDER_EMAIL) {
                     mAuth.createUserWithEmailAndPassword(email, password)
                             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if (task.isSuccessful()) {
                                         user.setName(profileName);
                                         user.setDob(profileDOB);
                                         user.setHeight(profileHeight);
                                         user.setWeight(profileWeight);
                                         user.setEmail(email);

                                         FirebaseDatabase.getInstance().getReference("Patient")
                                                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                 .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()) {
                                                     FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                     user.sendEmailVerification();
                                                     Toast.makeText(Register.this, "Registration successful, check email to verify account", Toast.LENGTH_LONG).show();
                                                     FirebaseAuth.getInstance().signOut();
                                                     startActivity(new Intent(Register.this, MainActivity.class));
                                                     formProgressBar.setVisibility(View.GONE);
                                                 } else {
                                                     Log.w(TAG, "Google SignIn Error", task.getException());
                                                 }
                                             }
                                         });
                                     } else {
                                         Log.w(TAG, "Google SignIn Error", task.getException());
                                     }
                                 }
                             });
                 } else if (provider == PROVIDER_GOOGLE) {
                     AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                     mAuth.signInWithCredential(credential)
                             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if (task.isSuccessful()) {
                                         user.setName(profileName);
                                         user.setDob(profileDOB);
                                         user.setHeight(profileHeight);
                                         user.setWeight(profileWeight);
                                         user.setEmail(account.getEmail());

                                         FirebaseDatabase.getInstance().getReference("Patient")
                                                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                 .setValue(user);

                                     } else {
                                         Toast.makeText(Register.this, "Cannot login via Google", Toast.LENGTH_LONG).show();
                                     }
                                     startActivity(new Intent(Register.this, Dashboard.class));
                                     formProgressBar.setVisibility(View.GONE);
                                 }
                             });
                 }
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
        user = new User(); //initialize new user
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

        //here i check if this email is new or not
        //if the email has no signInMethods, it is new, else it exists
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.getResult().getSignInMethods().size() == 0){
                    createPopUpForm(null, email, password, PROVIDER_EMAIL);
                } else {
                    Toast.makeText(Register.this, "User email already exist", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Something went wrong with google sign-in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner_gender){
            String gender = parent.getItemAtPosition(position).toString();
            user.setGender(gender);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        user.setGender("Prefer not to Say");
    }
}