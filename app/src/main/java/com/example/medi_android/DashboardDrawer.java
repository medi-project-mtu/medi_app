package com.example.medi_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
                R.id.nav_home, R.id.nav_profile, R.id.nav_contacts, R.id.nav_ratings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().findItem(R.id.nav_diabetes).setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(this, MediAIDiabetes.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_alzheimers).setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(this, MediAIAlzheimers.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_heartDisease).setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(this, MediAIHeartDisease.class));
            return true;
        });
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
        final View heartDiseaseFormPopUpView = getLayoutInflater().inflate(R.layout.heart_disease_data_form_popup, null);

        Button popUpSave = heartDiseaseFormPopUpView.findViewById(R.id.heart_disease_form_popup_save);
        Button popUpCancel = heartDiseaseFormPopUpView.findViewById(R.id.heart_disease_form_popup_cancel);

        EditText chestPainTypeET = heartDiseaseFormPopUpView.findViewById(R.id.hd_cp_editText);
        EditText rbpET = heartDiseaseFormPopUpView.findViewById(R.id.hd_trestbps_editText);
        EditText serumCholesterolET = heartDiseaseFormPopUpView.findViewById(R.id.hd_chol_editText);
        EditText fastingBSET= heartDiseaseFormPopUpView.findViewById(R.id.hd_fbs_editText);
        EditText restingECGET= heartDiseaseFormPopUpView.findViewById(R.id.hd_restecg_editText);
        EditText maxHeartRateET= heartDiseaseFormPopUpView.findViewById(R.id.hd_thalach_editText);
        EditText anginaET = heartDiseaseFormPopUpView.findViewById(R.id.hd_exang_editText);
        EditText STDepressionET = heartDiseaseFormPopUpView.findViewById(R.id.hd_oldpeak_editText);
        EditText peakExerciseSTET = heartDiseaseFormPopUpView.findViewById(R.id.hd_slope_editText);
        EditText majorVesselsET= heartDiseaseFormPopUpView.findViewById(R.id.hd_ca_editText);
        EditText thalET = heartDiseaseFormPopUpView.findViewById(R.id.hd_thal_editText);

        dialogBuilder.setView(heartDiseaseFormPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        popUpSave.setOnClickListener(view -> {
            if (checkValidity(chestPainTypeET,"1","4")) return;
            if (checkValidity(rbpET,"94","200")) return;
            if (checkValidity(serumCholesterolET,"126","564")) return;
            if (checkValidity(fastingBSET,"0","1")) return;
            if (checkValidity(restingECGET,"0","2")) return;
            if (checkValidity(maxHeartRateET,"71","202")) return;
            if (checkValidity(anginaET,"0","1")) return;
            if (checkValidity(STDepressionET,"0","6.2")) return;
            if (checkValidity(peakExerciseSTET,"1","3")) return;
            if (checkValidity(majorVesselsET,"0","3")) return;
            if (checkEmptyField(thalET)) return;
            if (thalET.getText().toString().matches("3|6|7")) {
                thalET.setError("O!");
                return;
            }
            else {
                thalET.setError("Valid input is 3,6,7");
                thalET.requestFocus();
            }

            HeartDiseaseData heartDiseaseData = new HeartDiseaseData();
            heartDiseaseData.setChestPainType(Float.parseFloat(chestPainTypeET.getText().toString()));
            heartDiseaseData.setRestingBloodPressure(Float.parseFloat(rbpET.getText().toString()));
            heartDiseaseData.setSerumCholesterol(Float.parseFloat(serumCholesterolET.getText().toString()));
            heartDiseaseData.setFastingBloodSugar(Float.parseFloat(fastingBSET.getText().toString()));
            heartDiseaseData.setRestingECG(Float.parseFloat(restingECGET.getText().toString()));
            heartDiseaseData.setMaxHeartRateAchieved(Float.parseFloat(maxHeartRateET.getText().toString()));
            heartDiseaseData.setExerciseInducedAngina(Float.parseFloat(anginaET.getText().toString()));
            heartDiseaseData.setSTDepressionInduced(Float.parseFloat(STDepressionET.getText().toString()));
            heartDiseaseData.setPeakExerciseSTSegment(Float.parseFloat(peakExerciseSTET.getText().toString()));
            heartDiseaseData.setMajorVesselsNumber(Float.parseFloat(majorVesselsET.getText().toString()));
            heartDiseaseData.setThal(Float.parseFloat(thalET.getText().toString()));

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Patient patient = snapshot.getValue(Patient.class);
                    if (patient != null){
                        heartDiseaseData.setAge(Float.parseFloat(patient.getAge()));
                        heartDiseaseData.setGender(Float.parseFloat(patient.getGender()));

                        Intent intent = new Intent(DashboardDrawer.this, MediAIHeartDisease.class);
                        intent.putExtra("inputs", heartDiseaseData);
                        startActivity(intent);

                        FirebaseDatabase.getInstance().getReference("Patient")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("heartDisease")
                                .setValue(heartDiseaseData);

                        Toast.makeText(DashboardDrawer.this, "Heart Disease Data saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });

        popUpCancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void createAlzheimersPopupform() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View alzheimersFormPopUpView = getLayoutInflater().inflate(R.layout.alzheimers_data_form_popup, null);

        Button popUpSave = alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_save);
        Button popUpCancel = alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_cancel);

        EditText eduLevelET = alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_education_level);
        EditText socioStatusET = alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_socioeconomic_status);
        EditText mmseET = alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_mini_mental_state_examination);
        EditText asfET = alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_asf);
        EditText etivET= alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_estimated_total_intracranial_volume);
        EditText nwbvET= alzheimersFormPopUpView.findViewById(R.id.alzheimers_form_popup_normalize_whole_brain_volume);

        dialogBuilder.setView(alzheimersFormPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        popUpSave.setOnClickListener(view -> {
            if (checkValidity(eduLevelET,"1","5")) return;
            if (checkValidity(socioStatusET,"1","5")) return;
            if (checkValidity(mmseET,"14","30")) return;
            if (checkValidity(asfET,"0","2")) return;
            if (checkValidity(etivET,"1123","1992")) return;
            if (checkValidity(nwbvET,"0.64","0.89")) return;

            AlzheimersData alzheimersData = new AlzheimersData();
            alzheimersData.setEducationLevel(Float.parseFloat(eduLevelET.getText().toString()));
            alzheimersData.setSocialEconomicStatus(Float.parseFloat(socioStatusET.getText().toString()));
            alzheimersData.setMiniMentalStateExamination(Float.parseFloat(mmseET.getText().toString()));
            alzheimersData.setAsf(Float.parseFloat(asfET.getText().toString()));
            alzheimersData.setEstimatedTotalIntracranialVolume(Float.parseFloat(etivET.getText().toString()));
            alzheimersData.setNormalizeHoleBrainVolume(Float.parseFloat(nwbvET.getText().toString()));

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Patient patient = snapshot.getValue(Patient.class);
                    if (patient != null){
                        alzheimersData.setAge(Float.parseFloat(patient.getAge()));
                        alzheimersData.setGender(Float.parseFloat(patient.getGender()));

                        Intent intent = new Intent(DashboardDrawer.this, MediAIAlzheimers.class);
                        intent.putExtra("inputs", alzheimersData);
                        startActivity(intent);

                        FirebaseDatabase.getInstance().getReference("Patient")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("alzheimers")
                                .setValue(alzheimersData);

                        Toast.makeText(DashboardDrawer.this, "Alzheimer's Data saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });
        popUpCancel.setOnClickListener(view -> dialog.dismiss());
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
            if(checkValidity(pregnanciesET,"0","17")) return;
            if(checkValidity(glucoseET,"0","199")) return;
            if(checkValidity(bloodPressureET,"0","122")) return;
            if(checkValidity(skinThicknessET,"0","110")) return;
            if(checkValidity(insulinET,"0","744")) return;
            if(checkValidity(bmiET,"0","80.6")) return;
            if(checkValidity(dpfET,"0.078","2.42")) return;

            DiabetesData diabetesData = new DiabetesData();

            diabetesData.setPregnancies(Float.parseFloat(pregnanciesET.getText().toString()));
            diabetesData.setGlucose(Float.parseFloat(glucoseET.getText().toString()));
            diabetesData.setBloodPressure(Float.parseFloat(bloodPressureET.getText().toString()));
            diabetesData.setSkinThickness(Float.parseFloat(skinThicknessET.getText().toString()));
            diabetesData.setInsulin(Float.parseFloat(insulinET.getText().toString()));
            diabetesData.setBmi(Float.parseFloat(bmiET.getText().toString()));
            diabetesData.setDiabetesPedigreeFunction(Float.parseFloat(dpfET.getText().toString()));

            reference.child(userID).child("age").get().addOnCompleteListener(task -> {
                    diabetesData.setAge(Float.parseFloat(task.getResult().getValue().toString()));
                    Intent intent = new Intent(DashboardDrawer.this, MediAIDiabetes.class);
                    intent.putExtra("inputs", diabetesData);
                    startActivity(intent);

                    FirebaseDatabase.getInstance().getReference("Patient")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("diabetes")
                            .setValue(diabetesData);

                    Toast.makeText(DashboardDrawer.this, "Diabetes data saved", Toast.LENGTH_SHORT).show();
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
    private boolean checkValidity(EditText field,String min,String max) {
        if (checkEmptyField(field)) return true;
        float getFloat= Float.parseFloat(field.getText().toString());
        if (getFloat>Float.parseFloat(max) || getFloat<Float.parseFloat(min)) {
            field.setError("Out of range!");
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