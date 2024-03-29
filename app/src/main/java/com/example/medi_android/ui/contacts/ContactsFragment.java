package com.example.medi_android.ui.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.medi_android.Patient;
import com.example.medi_android.R;
import com.example.medi_android.databinding.FragmentContactsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactsFragment extends Fragment {

    Activity context;
    private FragmentContactsBinding binding;
    private TextView gpName, gpEmail, gpPhoneNr, insName, insEmail, insPhoneNr;
    private DatabaseReference gpReference, userReference, insuranceReference;
    private String userID, gpNr, gpEm, insEm, insNr;
    private FirebaseUser user;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        gpReference = FirebaseDatabase.getInstance().getReference("Gp");
        userReference = FirebaseDatabase.getInstance().getReference("Patient").child(userID);
        insuranceReference = FirebaseDatabase.getInstance().getReference("Insurance");

        return root;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void dial(String phoneNr) {
        startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + phoneNr)));

    }

    public void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        startActivity(intent);
    }

    public void onStart() {
        super.onStart();

        //set fab visibility off
        com.github.clans.fab.FloatingActionMenu floatingActionMenu = context.findViewById(R.id.fab);
        floatingActionMenu.setVisibility(View.GONE);

        gpName = root.findViewById(R.id.contactGpName);
        gpEmail = root.findViewById(R.id.contactGpEmail);
        gpEmail.setOnClickListener(v -> sendEmail(gpEm));

        gpPhoneNr = root.findViewById(R.id.contactGpPhoneNr);
        gpPhoneNr.setOnClickListener(v -> dial(gpNr));

        insName = root.findViewById(R.id.contactInsuranceName);
        insEmail = root.findViewById(R.id.contactInsuranceEmail);
        insPhoneNr = root.findViewById(R.id.contactInsurancePhoneNr);
        insEmail.setOnClickListener(v -> sendEmail(insEm));
        insPhoneNr.setOnClickListener(v -> dial(insNr));

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patientProfile = snapshot.getValue(Patient.class);

                gpReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gpName.setText(snapshot.child(patientProfile.getGpUid()).child("name").getValue(String.class));
                        gpEm = snapshot.child(patientProfile.getGpUid()).child("email").getValue(String.class);
                        gpEmail.setText(gpEm);
                        gpEmail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

                        gpNr = snapshot.child(patientProfile.getGpUid()).child("phone").getValue(String.class);
                        gpPhoneNr.setText(gpNr);
                        gpPhoneNr.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                insuranceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        insName.setText(snapshot.child(patientProfile.getInsuranceId()).child("name").getValue(String.class));
                        insEm = snapshot.child(patientProfile.getInsuranceId()).child("email").getValue(String.class);
                        insEmail.setText(insEm);
                        insEmail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                        insNr = snapshot.child(patientProfile.getInsuranceId()).child("phone").getValue(String.class);
                        insPhoneNr.setText(insNr);
                        insPhoneNr.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}