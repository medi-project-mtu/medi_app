package com.example.medi_android;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Register extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private EditText editEmail, editPassword;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private final static int PROVIDER_GOOGLE = 1;
    private final static int PROVIDER_EMAIL= 0;

    private AlertDialog dialog;
    private EditText popup_name, popup_height, popup_weight, popup_dob;
    private Spinner spinner_gender, spinner_gp, spinner_insurance;

    private Patient patient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        TextView loginRedirect = findViewById(R.id.log_in_text);
        loginRedirect.setOnClickListener(this);

        Button registerButton = findViewById(R.id.sign_up_button);
        registerButton.setOnClickListener(this);

        SignInButton googleRegisterButton = findViewById(R.id.google_sign_up_button);
        googleRegisterButton.setOnClickListener(this);

        editEmail = findViewById(R.id.sign_up_email);
        editPassword = findViewById(R.id.sign_up_pw);

        initGoogleSignIn();
        if(getIntent().getBooleanExtra("Profile Setup", false)){
            registerGoogle();
        }
//        initFBSignIn();
    }

//    private void initFBSignIn(){
//        mCallbackManager = CallbackManager.Factory.create();
//        LoginButton facebookRegisterButton = findViewById(R.id.facebook_sign_up_button);
//        facebookRegisterButton.setReadPermissions("email", "public_profile");
//        facebookRegisterButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onError(@NonNull FacebookException e) {
//                Toast.makeText(Register.this, "facebook:on-error", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancel() {
//                Toast.makeText(Register.this, "facebook: on-cancel", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//        });
//    }

    private void initGoogleSignIn(){
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
        patient = new Patient();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Register.this, task -> {
                    if (task.isSuccessful()){
                        startActivity(new Intent(Register.this, DashboardDrawer.class));
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //here i check if this email is new or not
                //if the email has no signInMethods, it is new, else it exists
                mAuth.fetchSignInMethodsForEmail(account.getEmail()).addOnCompleteListener(task1 -> {
                    if(task1.getResult().getSignInMethods().size() == 0){
                        createPopUpForm(account, null, null, PROVIDER_GOOGLE);
                    } else {
                        firebaseAuthWithGoogle(account);
                    }
                }).addOnFailureListener(e -> Toast.makeText(Register.this, "Something went wrong with google sign-in", Toast.LENGTH_SHORT).show());
            } catch (ApiException e) {
                Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        String email = mAuth.getCurrentUser().getEmail();
                        Patient patient = new Patient(email);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(patient);
                        startActivity(new Intent(Register.this, DashboardDrawer.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View formPopUpView = getLayoutInflater().inflate(R.layout.form_popup, null);

        popup_name = formPopUpView.findViewById(R.id.form_popup_name);
        popup_dob = formPopUpView.findViewById(R.id.form_popup_dob);
        popup_height = formPopUpView.findViewById(R.id.form_popup_height);
        popup_weight = formPopUpView.findViewById(R.id.form_popup_weight);

        Button popUpSave = formPopUpView.findViewById(R.id.form_popup_save);
        Button popUpCancel = formPopUpView.findViewById(R.id.form_popup_cancel);

        spinner_gender = formPopUpView.findViewById(R.id.spinner_gender);
        spinner_gp = formPopUpView.findViewById(R.id.spinner_gp);
        spinner_insurance = formPopUpView.findViewById(R.id.spinner_insurance);

        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);
        spinner_gender.setOnItemSelectedListener(this);

        List<GP> gpList = getGPList();
        ArrayAdapter<GP> adapter_gp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gpList);
        adapter_gp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gp.setAdapter(adapter_gp);
        spinner_gp.setOnItemSelectedListener(this);

        List<Insurance> insurancesList = getInsuranceList();
        ArrayAdapter<Insurance> adapter_insurance = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, insurancesList);
        adapter_insurance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_insurance.setAdapter(adapter_insurance);
        spinner_insurance.setOnItemSelectedListener(this);


        dialogBuilder.setView(formPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        popup_dob.setOnClickListener(view -> showDatePickerDialog());

        popUpSave.setOnClickListener(view -> {
            String profileName = popup_name.getText().toString().trim();
            String profileDOB = popup_dob.getText().toString().trim();
            String profileHeight = popup_height.getText().toString().trim();
            String profileWeight = popup_weight.getText().toString().trim();

            if(checkEmptyField(popup_name)) return;
            if(checkEmptyField(popup_dob)) return;
            if(checkEmptyField(popup_height)) return;
            if(checkEmptyField(popup_weight)) return;
            if (spinner_gender.getSelectedItem().toString().equals("Select gender")) return;
            if (spinner_gp.getSelectedItem().toString().equals("Select a GP")) return;
            if (spinner_insurance.getSelectedItem().toString().equals("Select an Insurance")) return;

            if (provider == PROVIDER_EMAIL) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                patient.setName(profileName);
                                patient.setDob(profileDOB);
                                patient.setHeight(profileHeight);
                                patient.setWeight(profileWeight);
                                patient.setEmail(email);

                                FirebaseDatabase.getInstance().getReference("Patient")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            user.sendEmailVerification();
                                            Toast.makeText(Register.this, "Registration successful, check email to verify account", Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(Register.this, MainActivity.class));
                                        } else {
                                            Log.w(TAG, "Google SignIn Error", task.getException());
                                        }
                                    }
                                });
                            } else {
                                Log.w(TAG, "Google SignIn Error", task.getException());
                            }
                        });
            } else if (provider == PROVIDER_GOOGLE) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                patient.setName(profileName);
                                patient.setDob(profileDOB);
                                patient.setHeight(profileHeight);
                                patient.setWeight(profileWeight);
                                patient.setEmail(account.getEmail());

                                FirebaseDatabase.getInstance().getReference("Patient")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(patient);

                            } else {
                                Toast.makeText(Register.this, "Cannot login via Google", Toast.LENGTH_LONG).show();
                            }
                            startActivity(new Intent(Register.this, DashboardDrawer.class));
                        });
            }
        });

        popUpCancel.setOnClickListener(view -> {
            //define cancel btn
            dialog.dismiss();
        });
    }

    private boolean checkEmptyField(EditText field) {
        if (field.getText().toString().trim().isEmpty()) {
            field.setError("This field is required!");
            field.requestFocus();
            return true;
        }
        return false;
    }

    private List<Insurance> getInsuranceList() {
        List<Insurance> insuranceList = new ArrayList<>();

        Insurance placeholder = new Insurance();
        placeholder.setName("Select an Insurance");
        insuranceList.add(placeholder);

        FirebaseDatabase.getInstance().getReference("Insurance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot insuranceSnapshot: snapshot.getChildren()){
                    Insurance insurance = insuranceSnapshot.getValue(Insurance.class);
                    assert insurance != null;
                    insurance.setId(insuranceSnapshot.getKey());
                    insuranceList.add(insurance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return insuranceList;
    }

    private List<GP> getGPList() {
        List<GP> gpList = new ArrayList<>();

        GP placeholder = new GP();
        placeholder.setName("Select a GP");
        gpList.add(placeholder);

        FirebaseDatabase.getInstance().getReference("Gp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot gpSnapshot: snapshot.getChildren()){
                    GP gp = gpSnapshot.getValue(GP.class);
                    assert gp != null;
                    gp.setUid(gpSnapshot.getKey());
                    gpList.add(gp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return gpList;
    }

    private void registerUser(){
        patient = new Patient(); //initialize new patient
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
        }).addOnFailureListener(e -> Toast.makeText(Register.this, "Something went wrong with google sign-in", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_gender:
                String gender = parent.getItemAtPosition(position).toString();
                if(gender.equals("Select gender")){
                    TextView errorTextGender = (TextView)spinner_gender.getSelectedView();
                    errorTextGender.setError("Select gender");
                    errorTextGender.setTextColor(Color.GRAY);
                    return;
                }
                patient.setGender(gender);
                break;

            case R.id.spinner_gp:
                GP gp = (GP) parent.getItemAtPosition(position);
                if (gp.getName().equals("Select a GP")){
                    TextView errorTextGP = (TextView)spinner_gp.getSelectedView();
                    errorTextGP.setError("Select a GP");
                    errorTextGP.setTextColor(Color.GRAY);
                    return;
                }
                patient.setGpUid(gp.getUid());
                break;

            case R.id.spinner_insurance:
                Insurance insurance = (Insurance) parent.getItemAtPosition(position);
                if(insurance.getName().equals("Select an Insurance")){
                    TextView errorTextInsurance = (TextView) spinner_insurance.getSelectedView();
                    errorTextInsurance.setError("Select an Insurance");
                    errorTextInsurance.setTextColor(Color.GRAY);
                    return;
                }
                patient.setInsuranceId(insurance.getId());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}