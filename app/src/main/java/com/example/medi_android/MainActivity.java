package com.example.medi_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView signup, forgotPW;
    private EditText editEmail, editPassword;
    private Button loginButton;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

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

        mAuth = FirebaseAuth.getInstance();
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