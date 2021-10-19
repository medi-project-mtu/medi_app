package com.example.medi_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    private Button logout,medical_history,medi_ai_interface,payment,support,rating;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        medical_history = (Button)findViewById(R.id.medical_history);
        medical_history.setOnClickListener(this);

        rating = (Button) findViewById(R.id.rating_button);
        rating.setOnClickListener(this);

        medi_ai_interface = (Button)findViewById(R.id.medi_ai_interface);
        medi_ai_interface.setOnClickListener(this);

        payment = (Button)findViewById(R.id.payment);
        payment.setOnClickListener(this);

        support = (Button)findViewById(R.id.support);
        support.setOnClickListener(this);

        logout = (Button) findViewById(R.id.log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(Dashboard.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView emailTextView = (TextView) findViewById(R.id.dashboard_email);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    String email = userProfile.email;

                    emailTextView.setText("Welcome " + email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.medical_history:
                startActivity(new Intent(this, MedicalHistory.class));
                break;
            case R.id.medi_ai_interface:
                startActivity(new Intent(this, MediAiInterface.class));
                break;
            case R.id.payment:
                startActivity(new Intent(this, Payment.class));
                break;
            case R.id.support:
                startActivity(new Intent(this, Support.class));
                break;
            case R.id.rating_button:
                startActivity(new Intent(this, Ratings.class));
                break;
        }
    }
}