package com.example.medi_android.ui.profile;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medi_android.ProfileRecyclerViewAdapter;
import com.example.medi_android.R;
import com.example.medi_android.Patient;
import com.example.medi_android.databinding.FragmentProfileBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements ProfileRecyclerViewAdapter.ItemClickListener {

    private FragmentProfileBinding binding;
    private Activity context;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ProfileRecyclerViewAdapter adapter;
    private TextView usernameTextView;
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
        FloatingActionButton fab = context.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        List<String> profileDataTitle = new ArrayList<>();
        List<String> profileDataContent = new ArrayList<>();

        usernameTextView = (TextView) root.findViewById(R.id.username_title);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patientProfile = snapshot.getValue(Patient.class);
                if(patientProfile != null){
                    usernameTextView.setText(patientProfile.getName());

                    profileDataTitle.add("Gender");
                    profileDataTitle.add("Date of Birth (dd/MM/yyyy)");
                    profileDataTitle.add("Age");
                    profileDataTitle.add("Height (cm)");
                    profileDataTitle.add("Weight (kg)");
                    profileDataContent.add(patientProfile.getGender());
                    profileDataContent.add(patientProfile.getDob());
                    profileDataContent.add(patientProfile.getAge());
                    profileDataContent.add(patientProfile.getHeight());
                    profileDataContent.add(patientProfile.getWeight());
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,1);
                    RecyclerView recyclerView = context.findViewById(R.id.rv_profile);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new ProfileRecyclerViewAdapter(context, profileDataTitle, profileDataContent);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}