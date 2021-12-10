package com.example.medi_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class MediAIDiabetes extends AppCompatActivity {

    private TextView diabetesRiskTV;
    private String url;
    private DatabaseReference reference;
    private RecyclerViewAdapter adapter;
    private String userID;
    private FirebaseUser user;
    private DiabetesData diabetesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medi_aidiabetes);

        reference = FirebaseDatabase.getInstance().getReference("Patient");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        List<String> diabestesDataTitles = new ArrayList<>();
        List<String> diabetesDataContent = new ArrayList<>();

        DiabetesData inputs = (DiabetesData) getIntent().getSerializableExtra("inputs");

        Toolbar toolbar = findViewById(R.id.diabetesToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        diabetesRiskTV = findViewById(R.id.diabetesRiskTV);

        reference.child(userID).child("diabetes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diabetesData = snapshot.getValue(DiabetesData.class);
                if (diabetesData != null){
                    diabestesDataTitles.add("Pregnancies");
                    diabestesDataTitles.add("Glucose");
                    diabestesDataTitles.add("Blood Pressure");
                    diabestesDataTitles.add("Skin Thickness");
                    diabestesDataTitles.add("Insulin");
                    diabestesDataTitles.add("BMI");
                    diabestesDataTitles.add("DPF");
                    diabestesDataTitles.add("Age");
                    diabetesDataContent.add(Float.toString(diabetesData.getPregnancies()));
                    diabetesDataContent.add(Float.toString(diabetesData.getGlucose()));
                    diabetesDataContent.add(Float.toString(diabetesData.getBloodPressure()));
                    diabetesDataContent.add(Float.toString(diabetesData.getSkinThickness()));
                    diabetesDataContent.add(Float.toString(diabetesData.getInsulin()));
                    diabetesDataContent.add(Float.toString(diabetesData.getBmi()));
                    diabetesDataContent.add(Float.toString(diabetesData.getDiabetesPedigreeFunction()));
                    diabetesDataContent.add(Float.toString(diabetesData.getAge()));
                    diabetesRiskTV.setText(String.format("Risk: %s", diabetesData.getDiagnosis()));
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MediAIDiabetes.this, 2);
                    RecyclerView recyclerView = findViewById(R.id.rv_diabetes);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new RecyclerViewAdapter(MediAIDiabetes.this, diabestesDataTitles, diabetesDataContent, R.layout.cardview_row);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // user enters data first time, load diagnosis immediately
        if (inputs != null){
            diabetesRiskTV.setText(R.string.loadingRisk);
            getDiagnosis(inputs);
        }
    }

    private void getDiagnosis(DiabetesData data) {
        url = "https://mtu-medi-ai.herokuapp.com/predictDiabetes";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String diagnosis = jsonObject.getString("Diagnosis");
                    diabetesRiskTV.setText(String.format("Risk: %s", diagnosis));
                    reference.child(userID).child("diabetes").child("diagnosis").setValue(diagnosis);
                    Toast.makeText(MediAIDiabetes.this, "Diabetes Risk: "+diagnosis, Toast.LENGTH_SHORT).show();
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
                params.put("pregnancies", Float.toString(data.getPregnancies()));
                params.put("glucose", Float.toString(data.getGlucose()));
                params.put("bp", Float.toString(data.getBloodPressure()));
                params.put("skinThickness", Float.toString(data.getSkinThickness()));
                params.put("insulin", Float.toString(data.getInsulin()));
                params.put("bmi", Float.toString(data.getBmi()));
                params.put("dpf", Float.toString(data.getDiabetesPedigreeFunction()));
                params.put("age", Float.toString(data.getAge()));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MediAIDiabetes.this);
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.predict_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.predict_menu:
                if(diabetesData != null){
                    getDiagnosis(diabetesData);
                } else {
                    Toast.makeText(MediAIDiabetes.this, "No Data to predict", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.model_accuracy:
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Model Info");
                builder.setMessage("Model Algorithm: SVM\nModel Accuracy: 88.25%").setPositiveButton("Ok", dialogClickListener).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}