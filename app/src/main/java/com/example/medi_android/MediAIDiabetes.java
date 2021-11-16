package com.example.medi_android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MediAIDiabetes extends AppCompatActivity {

    private TextView diabetesResultTV;
    private String url;
    private DatabaseReference reference;
    private String userID;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medi_aidiabetes);

        reference = FirebaseDatabase.getInstance().getReference("Patient");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        DiabetesData inputs = (DiabetesData) getIntent().getSerializableExtra("inputs");

        Toolbar toolbar = findViewById(R.id.diabetesToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        diabetesResultTV = findViewById(R.id.diabetes_result);
        Button diabetesAIBtn = findViewById(R.id.diabetes_ai_btn);
        diabetesAIBtn.setOnClickListener(view -> {
            url = "https://mtu-medi-ai.herokuapp.com/predictDiabetes";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String diagnosis = jsonObject.getString("Diagnosis");
                        diabetesResultTV.setText(diagnosis);
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
//                    if (inputs == null){
//                        reference.child(userID).child("diabetes").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                DiabetesData diabetesData = snapshot.getValue(DiabetesData.class);
//                                if (diabetesData != null){
//                                    params.put("pregnancies", Float.toString(diabetesData.getPregnancies()));
//                                    params.put("glucose", Float.toString(diabetesData.getGlucose()));
//                                    params.put("bp", Float.toString(diabetesData.getBloodPressure()));
//                                    params.put("skinThickness", Float.toString(diabetesData.getSkinThickness()));
//                                    params.put("insulin", Float.toString(diabetesData.getInsulin()));
//                                    params.put("bmi", Float.toString(diabetesData.getBmi()));
//                                    params.put("dpf", Float.toString(diabetesData.getDiabetesPedigreeFunction()));
//                                    params.put("age", Float.toString(diabetesData.getAge()));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//                    } else {
                    params.put("pregnancies", Float.toString(inputs.getPregnancies()));
                    params.put("glucose", Float.toString(inputs.getGlucose()));
                    params.put("bp", Float.toString(inputs.getBloodPressure()));
                    params.put("skinThickness", Float.toString(inputs.getSkinThickness()));
                    params.put("insulin", Float.toString(inputs.getInsulin()));
                    params.put("bmi", Float.toString(inputs.getBmi()));
                    params.put("dpf", Float.toString(inputs.getDiabetesPedigreeFunction()));
                    params.put("age", Float.toString(inputs.getAge()));
//                    }
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(MediAIDiabetes.this);
            queue.add(request);
        });
    }
}