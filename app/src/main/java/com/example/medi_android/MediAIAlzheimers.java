package com.example.medi_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MediAIAlzheimers extends AppCompatActivity {

    private Button alzheimersAIBtn;
    private TextView alzheimersResultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medi_aialzheimers);
        AlzheimersData inputs = (AlzheimersData) getIntent().getSerializableExtra("inputs");

        Toolbar toolbar = findViewById(R.id.diabetesToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);


        alzheimersResultTV = findViewById(R.id.alzheimers_result);
        alzheimersAIBtn = findViewById(R.id.alzheimers_ai_btn);
        alzheimersAIBtn.setOnClickListener(view -> alzheimersResultTV.setText(inputs.toString()));
    }
}