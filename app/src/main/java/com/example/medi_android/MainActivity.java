package com.example.medi_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView signup, forgotPW;
    private EditText editEmail, editPassword;
    private Button loginButton;
    private ImageButton googleSignInButton;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

//  start the app with the user logged in, if they haven't logged out the last session
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(MainActivity.this, Dashboard.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signup = (TextView) findViewById(R.id.sign_up_redirect);
        signup.setOnClickListener(this);

        forgotPW = (TextView) findViewById(R.id.forgot_pw);
        forgotPW.setOnClickListener(this);

        loginButton = (Button)findViewById(R.id.log_in_button);
        loginButton.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.log_in_email);
        editPassword = (EditText) findViewById(R.id.log_in_pw);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);

        googleSignInButton = (ImageButton) findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        requestGoogleSignIn();
    }

    private void requestGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                            startActivity(new Intent(MainActivity.this, Dashboard.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Cannot login via Google", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_up_redirect:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.log_in_button:
                loginUser();
                break;
            case R.id.forgot_pw:
                startActivity(new Intent(this, ResetPassword.class));
                break;
            case R.id.google_sign_in_button:
                signInGoogle();
                break;
        }
    }

    private void loginUser(){
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty()){
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
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        //redirect to user's medi dashboard
                        startActivity(new Intent(MainActivity.this, Dashboard.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Account not verified, verification link resent to email", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Failed to login! Check your credentials!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}