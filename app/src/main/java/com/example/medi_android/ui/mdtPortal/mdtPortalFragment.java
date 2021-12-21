package com.example.medi_android.ui.mdtPortal;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.medi_android.DashboardDrawer;
import com.example.medi_android.HeartDiseaseData;
import com.example.medi_android.MediAIHeartDisease;
import com.example.medi_android.Patient;
import com.example.medi_android.R;
import com.example.medi_android.databinding.FragmentMdtPortalBinding;
import com.example.medi_android.ui.profile.ProfileFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class mdtPortalFragment extends Fragment {

    private AlertDialog dialog;
    private FragmentMdtPortalBinding binding;
    private View root;
    private Activity context;
    private DatabaseReference reference;
    private String userID;
    private String walletAddress,pk;
    private EditText walletAddressEt,pkEt;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        binding = FragmentMdtPortalBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onStart() {
        super.onStart();
        createWalletPopUp();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Patient");
        assert user != null;
        userID = user.getUid();
        //set fab visibility off
        com.github.clans.fab.FloatingActionMenu floatingActionMenu = context.findViewById(R.id.fab);
        floatingActionMenu.setVisibility(View.GONE);
    }

    private void viewAccount(){
        final Web3j client = Web3j.build(new HttpService("https://ropsten.infura.io/v3/c4db87b143374a1093e3499dd15e795a"));
        walletAddress = walletAddressEt.getText().toString();
        pk = pkEt.getText().toString();
        Credentials credentials = Credentials.create(pk);

        String contractAddress = "0x96603d01f1717c7692c2d244fc045f0398239102";
        ERC20 token = ERC20.load(contractAddress, client, credentials, new DefaultGasProvider());

        try {
            BigInteger balance = token.balanceOf(walletAddress).sendAsync().get(10, TimeUnit.SECONDS);
            BigDecimal scaledBalance = new BigDecimal(balance)
                    .divide(new BigDecimal(1000000000000000000L), 18, RoundingMode.HALF_UP);

            TextView balanceTV = root.findViewById(R.id.balance);
            TextView walletTV = root.findViewById(R.id.wallet);
            walletTV.setText("Wallet Address:\n" + walletAddress);
            balanceTV.setText("Balance:\n" + scaledBalance.toString());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
    private void createWalletPopUp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        final View mdtFormPopUpView = getLayoutInflater().inflate(R.layout.mdt_form_popup, null);

        walletAddressEt = mdtFormPopUpView.findViewById(R.id.mdt_form_wallet_adress);
        pkEt = mdtFormPopUpView.findViewById(R.id.mdt_form_wallet_password);

        Button popUpSave = mdtFormPopUpView.findViewById(R.id.mdt_form_save_btn);
        Button popUpCancel = mdtFormPopUpView.findViewById(R.id.mdt_form_cancel_btn);
        dialogBuilder.setView(mdtFormPopUpView);
        dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        popUpSave.setOnClickListener(view -> {
            if (checkEmptyField(walletAddressEt)) return;
            if (checkEmptyField(pkEt)) return;



            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Patient patient = snapshot.getValue(Patient.class);
                    if (patient != null){
                        FirebaseDatabase.getInstance().getReference("Patient")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("walletAddress")
                                .setValue(walletAddressEt.getText().toString());

                        Toast.makeText(context, "Wallet Data Saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            viewAccount();
        });

        popUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DashboardDrawer.class));
            }
        });
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