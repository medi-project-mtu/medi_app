package com.example.medi_android.ui.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.medi_android.DashboardDrawer;
import com.example.medi_android.R;
import com.example.medi_android.Support;
import com.example.medi_android.databinding.FragmentSupportBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class SupportFragment extends Fragment {


    Activity context;
    private FragmentSupportBinding binding;
    private EditText supportMsgText;
    private EditText supportSubject;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        binding = FragmentSupportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onStart() {
        super.onStart();

        //set fab visibility off
        com.github.clans.fab.FloatingActionMenu floatingActionMenu = context.findViewById(R.id.fab);
        floatingActionMenu.setVisibility(View.GONE);

        Button submit = context.findViewById(R.id.submit_support_btn);
        supportMsgText = context.findViewById(R.id.supportTextView);
        supportSubject = context.findViewById(R.id.support_subject);
        submit.setOnClickListener(view ->
                FirebaseDatabase.getInstance().getReference("Patient")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("support")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                createSupportTicket();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createSupportTicket() {
        if (checkEmptyField(supportSubject)) return;
        if (checkEmptyField(supportMsgText)) return;

        String subject = supportSubject.getText().toString().trim();
        String supportMsg = supportMsgText.getText().toString().trim();
        Support support = new Support();
        support.setMsg(supportMsg);
        support.setSubject(subject);

        UUID uuid = UUID.randomUUID();

        FirebaseDatabase.getInstance().getReference("Patient")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("support").child(uuid.toString()).setValue(support);

        Toast.makeText(context, "Support Ticket Submitted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(context, DashboardDrawer.class));
    }

    private boolean checkEmptyField(EditText field) {
        if (field.getText().toString().isEmpty()) {
            field.setError("This field is required!");
            field.requestFocus();
            return true;
        }
        return false;
    }

}