package com.example.medi_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MediAIAlzheimers extends AppCompatActivity {

    private TextView alzheimersRiskTV;
    private String url;
    private DatabaseReference reference;
    private RecyclerViewAdapter adapter;
    private String userID;
    private FirebaseUser user;
    private AlzheimersData alzheimersData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medi_aialzheimers);

        reference = FirebaseDatabase.getInstance().getReference("Patient");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        List<String> alzheimersDataTitles = new ArrayList<>();
        List<String> alzheimersDataContent = new ArrayList<>();

        AlzheimersData inputs = (AlzheimersData) getIntent().getSerializableExtra("inputs");

        Toolbar toolbar = findViewById(R.id.alzheimersToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        alzheimersRiskTV = findViewById(R.id.alzheimersRiskTV);
        Button alzheimersAIBtn = findViewById(R.id.alzheimers_ai_btn);

        reference.child(userID).child("alzheimers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alzheimersData = snapshot.getValue(AlzheimersData.class);
                if (alzheimersData != null){
                    alzheimersDataTitles.add("Education Level");
                    alzheimersDataTitles.add("Socioeconomic Status");
                    alzheimersDataTitles.add("Mini Mental State Exam");
                    alzheimersDataTitles.add("ASF");
                    alzheimersDataTitles.add("ETIV");
                    alzheimersDataTitles.add("NWBV");
                    alzheimersDataTitles.add("Age");
                    alzheimersDataTitles.add("Gender");
                    alzheimersDataContent.add(Float.toString(alzheimersData.getEducationLevel()));
                    alzheimersDataContent.add(Float.toString(alzheimersData.getSocialEconomicStatus()));
                    alzheimersDataContent.add(Float.toString(alzheimersData.getMiniMentalStateExamination()));
                    alzheimersDataContent.add(Float.toString(alzheimersData.getAsf()));
                    alzheimersDataContent.add(Float.toString(alzheimersData.getEstimatedTotalIntracranialVolume()));
                    alzheimersDataContent.add(Float.toString(alzheimersData.getNormalizeHoleBrainVolume()));
                    alzheimersDataContent.add(Float.toString(alzheimersData.getAge()));
                    alzheimersDataContent.add(Float.toString(alzheimersData.getGender()));
                    alzheimersRiskTV.setText(String.format("Risk: %s", alzheimersData.getDiagnosis()));
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MediAIAlzheimers.this, 2);
                    RecyclerView recyclerView = findViewById(R.id.rv_alzheimers);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new RecyclerViewAdapter(MediAIAlzheimers.this, alzheimersDataTitles, alzheimersDataContent);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // user enters data first time, load diagnosis immediately
        if (inputs != null){
            alzheimersRiskTV.setText(R.string.loadingRisk);
            getDiagnosis(inputs);
        }

        // user clicks on predict btn, reload diagnosis
        alzheimersAIBtn.setOnClickListener(view -> {
            if(alzheimersData!= null){
                getDiagnosis(alzheimersData);
            } else {
                Toast.makeText(MediAIAlzheimers.this, "No Data to predict", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDiagnosis(AlzheimersData data) {
        url = "https://mtu-medi-ai.herokuapp.com/predictAlzheimers";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String diagnosis = jsonObject.getString("Diagnosis");
                    alzheimersRiskTV.setText(String.format("Risk: %s", diagnosis));
                    reference.child(userID).child("alzheimers").child("diagnosis").setValue(diagnosis);
                    Toast.makeText(MediAIAlzheimers.this, "Alzheimer's Risk: "+diagnosis, Toast.LENGTH_SHORT).show();
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
//                params.put("dominantHand", Float.toString(data.getDominantHand()));
                params.put("educ", Float.toString(data.getEducationLevel()));
                params.put("ses", Float.toString(data.getSocialEconomicStatus()));
                params.put("mmse", Float.toString(data.getMiniMentalStateExamination()));
                params.put("asf", Float.toString(data.getAsf()));
                params.put("etiv", Float.toString(data.getEstimatedTotalIntracranialVolume()));
                params.put("nwbv", Float.toString(data.getNormalizeHoleBrainVolume()));
                params.put("mf", Float.toString(data.getGender()));
                params.put("age", Float.toString(data.getAge()));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MediAIAlzheimers.this);
        queue.add(request);
    }
}