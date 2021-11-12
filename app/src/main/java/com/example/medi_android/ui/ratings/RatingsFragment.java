package com.example.medi_android.ui.ratings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.medi_android.DashboardDrawer;
import com.example.medi_android.R;
import com.example.medi_android.Review;
import com.example.medi_android.databinding.FragmentRatingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingsFragment extends Fragment {


    private FragmentRatingsBinding binding;
    Activity context;
    private EditText ratingText;
    RatingBar ratingBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();

        binding = FragmentRatingsBinding.inflate(inflater, container, false);


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

        Button submit = context.findViewById(R.id.submit_review_btn);
        ratingBar= context.findViewById(R.id.ratingBar);
        ratingText = context.findViewById(R.id.ratingTextView);
        submit.setOnClickListener(view ->
                FirebaseDatabase.getInstance().getReference("Patient")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("review")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            createReview();
                        } else {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            createReview();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("You already have left a review. Do you wish to update it ?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }));

    }

    public void createReview() {
        Review review = new Review(String.valueOf(ratingBar.getRating()), ratingText.getText().toString());
        FirebaseDatabase.getInstance().getReference("Patient")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("review").setValue(review);
        Toast.makeText(context, "Review Submitted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(context, DashboardDrawer.class));
    }

}