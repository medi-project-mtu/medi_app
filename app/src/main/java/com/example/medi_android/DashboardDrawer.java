package com.example.medi_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.medi_android.databinding.ActivityDashboardDrawerBinding;
import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DashboardDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private AlertDialog dialog;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.medi_android.databinding.ActivityDashboardDrawerBinding binding = ActivityDashboardDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarDashboardDrawer.toolbar);

        FloatingActionButton diabetesFAB = findViewById(R.id.diabetes_fab);
        FloatingActionButton alzheimersFAB = findViewById(R.id.alzheimers_fab);
        FloatingActionButton heartDiseaseFAB = findViewById(R.id.heart_disease_fab);

        diabetesFAB.setOnClickListener(view -> createDiabetesPopupForm());
        alzheimersFAB.setOnClickListener(view -> createAlzheimersPopupform());
        heartDiseaseFAB.setOnClickListener(view -> createHeartDiseasePopupForm());


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // set user name and email and the nav sider drawer
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Patient");
        userID = user.getUid();
        setUserProfileToNavSideBar(navigationView);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile, R.id.nav_contacts, R.id.nav_ratings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(DashboardDrawer.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to log out?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return true;
        });
    }

    private void setUserProfileToNavSideBar(NavigationView navigationView) {
        View hView = navigationView.getHeaderView(0);
        TextView nav_username = hView.findViewById(R.id.navside_username);
        TextView nav_email = hView.findViewById(R.id.navside_email);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patientProfile = snapshot.getValue(Patient.class);
                if(patientProfile != null){
                    String name = patientProfile.getName();
                    String email = patientProfile.getEmail();

                    nav_username.setText(name);
                    nav_email.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardDrawer.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createHeartDiseasePopupForm() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View formPopUpView = getLayoutInflater().inflate(R.layout.heart_disease_data_form_popup, null);
        Button popUpSave = formPopUpView.findViewById(R.id.heart_disease_form_popup_save);
        Button popUpCancel = formPopUpView.findViewById(R.id.heart_disease_form_popup_cancel);

        popUpSave.setOnClickListener(view -> {
            Toast.makeText(this, "Heart Disease Data saved", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        popUpCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialogBuilder.setView(formPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void createAlzheimersPopupform() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View formPopUpView = getLayoutInflater().inflate(R.layout.alzheimers_data_form_popup, null);
        Button popUpSave = formPopUpView.findViewById(R.id.alzheimers_form_popup_save);
        Button popUpCancel = formPopUpView.findViewById(R.id.alzheimers_form_popup_cancel);

        popUpSave.setOnClickListener(view -> {
            Toast.makeText(this, "Alzheimer's Data saved", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        popUpCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialogBuilder.setView(formPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void createDiabetesPopupForm() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View diabetesFormPopUpView = getLayoutInflater().inflate(R.layout.diabetes_data_form_popup, null);

        Button popUpSave = diabetesFormPopUpView.findViewById(R.id.diabetes_form_popup_save);
        Button popUpCancel = diabetesFormPopUpView.findViewById(R.id.diabetes_form_popup_cancel);

        EditText pregnanciesET = diabetesFormPopUpView.findViewById(R.id.pregnancy_editText);
        EditText glucoseET = diabetesFormPopUpView.findViewById(R.id.glucose_editText);
        EditText bloodPressureET = diabetesFormPopUpView.findViewById(R.id.bloodPressure_editText);
        EditText skinThicknessET = diabetesFormPopUpView.findViewById(R.id.skinThickness_editText);
        EditText insulinET = diabetesFormPopUpView.findViewById(R.id.insulin_editText);
        EditText bmiET = diabetesFormPopUpView.findViewById(R.id.BMI_editText);
        EditText dpfET = diabetesFormPopUpView.findViewById(R.id.diabetesPedigreeFunction_editText);

        dialogBuilder.setView(diabetesFormPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        popUpSave.setOnClickListener(view -> {
            if(checkEmptyField(pregnanciesET)) return;
            if(checkEmptyField(glucoseET)) return;
            if(checkEmptyField(bloodPressureET)) return;
            if(checkEmptyField(skinThicknessET)) return;
            if(checkEmptyField(insulinET)) return;
            if(checkEmptyField(bmiET)) return;
            if(checkEmptyField(dpfET)) return;

            DiabetesData diabetesData = new DiabetesData();

            diabetesData.setPregnancies(Float.parseFloat(pregnanciesET.getText().toString()));
            diabetesData.setGlucose(Float.parseFloat(glucoseET.getText().toString()));
            diabetesData.setBloodPressure(Float.parseFloat(bloodPressureET.getText().toString()));
            diabetesData.setSkinThickness(Float.parseFloat(skinThicknessET.getText().toString()));
            diabetesData.setInsulin(Float.parseFloat(insulinET.getText().toString()));
            diabetesData.setBmi(Float.parseFloat(bmiET.getText().toString()));
            diabetesData.setDiabetesPedigreeFunction(Float.parseFloat(dpfET.getText().toString()));

            FirebaseDatabase.getInstance().getReference("Patient")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("age")
                    .get()
                    .addOnCompleteListener(task -> {
                        diabetesData.setAge(Float.parseFloat(task.getResult().getValue().toString()));
                        Intent intent = new Intent(DashboardDrawer.this, MediAIDiabetes.class);
                        intent.putExtra("inputs", diabetesData);
                        startActivity(intent);

                        FirebaseDatabase.getInstance().getReference("Patient")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("diabetes")
                                .setValue(diabetesData);

                        Toast.makeText(DashboardDrawer.this, "diabetes data saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
        });

        popUpCancel.setOnClickListener(view -> dialog.dismiss());
    }

    private boolean checkEmptyField(EditText field) {
        if (field.getText().toString().isEmpty()) {
            field.setError("This field is required!");
            field.requestFocus();
            return true;
        }
        return false;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}