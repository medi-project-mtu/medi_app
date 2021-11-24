package com.example.medi_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediAIHeartDisease extends AppCompatActivity {

    private TextView heartDiseaseRiskTV;
    private String url;
    private DatabaseReference reference;
    private RecyclerViewAdapter adapter;
    private String userID;
    private FirebaseUser user;
    private HeartDiseaseData heartDiseaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medi_aiheart_disease);

        reference = FirebaseDatabase.getInstance().getReference("Patient");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        List<String> heartDiseaseDataTitles = new ArrayList<>();
        List<String> heartDiseaseDataContent = new ArrayList<>();

        HeartDiseaseData inputs = (HeartDiseaseData) getIntent().getSerializableExtra("inputs");

        Toolbar toolbar = findViewById(R.id.heartDiseaseToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        heartDiseaseRiskTV = findViewById(R.id.heartDiseaseRiskTV);
        Button heartDiseaseAIBtn = findViewById(R.id.heartDisease_ai_btn);

        reference.child(userID).child("heartDisease").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                heartDiseaseData = snapshot.getValue(HeartDiseaseData.class);
                if (heartDiseaseData != null) {
                    heartDiseaseDataTitles.add("Chest Pain Type");
                    heartDiseaseDataTitles.add("Resting Blood Pressure");
                    heartDiseaseDataTitles.add("Serum Cholesterol");
                    heartDiseaseDataTitles.add("Fasting Blood Sugar");
                    heartDiseaseDataTitles.add("Resting ECG");
                    heartDiseaseDataTitles.add("Max Heart Rate");
                    heartDiseaseDataTitles.add("Angina");
                    heartDiseaseDataTitles.add("ST Depression");
                    heartDiseaseDataTitles.add("Peak Exercise ST");
                    heartDiseaseDataTitles.add("Major Vessels number");
                    heartDiseaseDataTitles.add("Thal");
                    heartDiseaseDataTitles.add("Age");
                    heartDiseaseDataTitles.add("Gender");
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getChestPainType()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getRestingBloodPressure()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getSerumCholesterol()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getFastingBloodSugar()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getRestingECG()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getMaxHeartRateAchieved()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getExerciseInducedAngina()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getSTDepressionInduced()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getPeakExerciseSTSegment()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getMajorVesselsNumber()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getThal()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getAge()));
                    heartDiseaseDataContent.add(Float.toString(heartDiseaseData.getGender()));


                    heartDiseaseRiskTV.setText(String.format("Risk: %s", heartDiseaseData.getDiagnosis()));
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MediAIHeartDisease.this, 2);
                    RecyclerView recyclerView = findViewById(R.id.rv_heartDisease);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new RecyclerViewAdapter(MediAIHeartDisease.this, heartDiseaseDataTitles, heartDiseaseDataContent);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // user enters data first time, load diagnosis immediately
        if (inputs != null){
            heartDiseaseRiskTV.setText(R.string.loadingRisk);
            getDiagnosis(inputs);
        }

        // user clicks on predict btn, reload diagnosis
        heartDiseaseAIBtn.setOnClickListener(view -> {
            if(heartDiseaseData != null){
                getDiagnosis(heartDiseaseData);
            } else {
                Toast.makeText(MediAIHeartDisease.this, "No Data to predict", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDiagnosis(HeartDiseaseData data) {
        url = "https://mtu-medi-ai.herokuapp.com/predictHeartDisease";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String diagnosis = jsonObject.getString("Diagnosis");
                    if(diagnosis.equals("0")){
                        diagnosis = "Absent";
                    } else {
                        diagnosis = "Present";
                    }
                    heartDiseaseRiskTV.setText(String.format("Risk: %s", diagnosis));
                    reference.child(userID).child("heartDisease").child("diagnosis").setValue(diagnosis);
                    Toast.makeText(MediAIHeartDisease.this, "Heart Disease Risk: "+diagnosis, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Cannot get JSON", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("chestPainType", Float.toString(data.getChestPainType()));
                params.put("rbp", Float.toString(data.getRestingBloodPressure()));
                params.put("serumChol", Float.toString(data.getSerumCholesterol()));
                params.put("fbs", Float.toString(data.getFastingBloodSugar()));
                params.put("restingECG", Float.toString(data.getRestingECG()));
                params.put("maxHeartRate", Float.toString(data.getMaxHeartRateAchieved()));
                params.put("exerciseInducedAngina", Float.toString(data.getExerciseInducedAngina()));
                params.put("stDepression", Float.toString(data.getSTDepressionInduced()));
                params.put("peakExerciseSTSegment", Float.toString(data.getPeakExerciseSTSegment()));
                params.put("majorVessels", Float.toString(data.getMajorVesselsNumber()));
                params.put("thal", Float.toString(data.getThal()));
                params.put("age", Float.toString(data.getAge()));
                params.put("sex", Float.toString(data.getGender()));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MediAIHeartDisease.this);
        queue.add(request);
    }
}