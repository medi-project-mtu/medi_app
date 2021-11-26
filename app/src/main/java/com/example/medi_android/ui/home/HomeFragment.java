package com.example.medi_android.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medi_android.AlzheimersData;
import com.example.medi_android.MediAIAlzheimers;
import com.example.medi_android.MediAIDiabetes;
import com.example.medi_android.MediAIHeartDisease;
import com.example.medi_android.Patient;
import com.example.medi_android.R;
import com.example.medi_android.RecyclerViewAdapter;
import com.example.medi_android.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Activity context;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private RecyclerViewAdapter adapter;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Patient");
        userID = user.getUid();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onStart() {
        super.onStart();

        //set fab visibility on
        com.github.clans.fab.FloatingActionMenu floatingActionMenu = context.findViewById(R.id.fab);
        floatingActionMenu.setVisibility(View.VISIBLE);

        List<String> profileDataTitle = new ArrayList<>();
        List<String> profileDataContent = new ArrayList<>();
        TextView homeNameTitle = root.findViewById(R.id.home_name_title);


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patientProfile = snapshot.getValue(Patient.class);
                if(patientProfile != null){
                    homeNameTitle.setText(patientProfile.getName());

                    profileDataTitle.add("Diabetes Risk");
                    profileDataTitle.add("Alzheimer's Risk");
                    profileDataTitle.add("Heart Disease Risk");

                    profileDataContent.add(snapshot.child("diabetes").child("diagnosis").getValue().toString());
                    profileDataContent.add(snapshot.child("alzheimers").child("diagnosis").getValue().toString());
                    profileDataContent.add(snapshot.child("heartDisease").child("diagnosis").getValue().toString());
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,1);
                    RecyclerView recyclerView = context.findViewById(R.id.rv_home);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new RecyclerViewAdapter(context, profileDataTitle, profileDataContent);
                    adapter.setClickListener((view, position) -> {
                        switch (position){
                            case 0:
                                startActivity(new Intent(context, MediAIDiabetes.class));
                                break;
                            case 1:
                                startActivity(new Intent(context, MediAIAlzheimers.class));
                                break;
                            case 2:
                                startActivity(new Intent(context, MediAIHeartDisease.class));
                                break;
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
