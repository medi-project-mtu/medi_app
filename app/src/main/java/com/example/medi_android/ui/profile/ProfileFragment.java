package com.example.medi_android.ui.profile;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medi_android.Patient;
import com.example.medi_android.R;
import com.example.medi_android.RecyclerViewAdapter;
import com.example.medi_android.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Activity context;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private RecyclerViewAdapter adapter;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();

        binding = FragmentProfileBinding.inflate(inflater, container, false);
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
        floatingActionMenu.setVisibility(View.GONE);

        List<String> profileDataTitle = new ArrayList<>();
        List<String> profileDataContent = new ArrayList<>();


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patientProfile = snapshot.getValue(Patient.class);
                if (patientProfile != null) {
                    profileDataTitle.add("Name");
                    profileDataTitle.add("Gender");
                    profileDataTitle.add("Date of Birth (dd/MM/yyyy)");
                    profileDataTitle.add("Age");
                    profileDataTitle.add("Height (cm)");
                    profileDataTitle.add("Weight (kg)");
                    profileDataContent.add(patientProfile.getName());
                    if (patientProfile.getGender().equals("1")) {
                        profileDataContent.add("Male");
                    } else {
                        profileDataContent.add("Female");
                    }
                    profileDataContent.add(patientProfile.getDob());
                    profileDataContent.add(patientProfile.getAge());
                    profileDataContent.add(patientProfile.getHeight());
                    profileDataContent.add(patientProfile.getWeight());
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
                    RecyclerView recyclerView = context.findViewById(R.id.rv_profile);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new RecyclerViewAdapter(context, profileDataTitle, profileDataContent, R.layout.cardview_row);
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