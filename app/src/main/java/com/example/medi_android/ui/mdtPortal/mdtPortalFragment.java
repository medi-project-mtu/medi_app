package com.example.medi_android.ui.mdtPortal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.medi_android.DashboardDrawer;
import com.example.medi_android.Patient;
import com.example.medi_android.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.example.medi_android.databinding.FragmentMdtPortalBinding;
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
import org.web3j.tx.gas.StaticGasProvider;

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
    private EditText walletAddressEt,pkEt, receipientAddET, amountET;
    private Button topUp, pay;
    private Web3j client;
    private String contractAddress, mainAccAddress, mainAccPK;
    private ERC20 bankToken, token;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        binding = FragmentMdtPortalBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        topUp = root.findViewById(R.id.mdt_topup);
        pay = root.findViewById(R.id.pay_btn);
        contractAddress = "0x96603d01f1717c7692c2d244fc045f0398239102";
        mainAccAddress = "0x09F2278E1f81b481afFB813Ec07356B5C83003d3";
        mainAccPK = "f809526301afddd0ce9ae1f5034397aae964a099a923acb8885588ea2cfac821";

        createWalletPopUp();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Patient");
        client = Web3j.build(new HttpService("https://ropsten.infura.io/v3/c4db87b143374a1093e3499dd15e795a"));

        assert user != null;
        userID = user.getUid();

        //set fab visibility off
        com.github.clans.fab.FloatingActionMenu floatingActionMenu = context.findViewById(R.id.fab);
        floatingActionMenu.setVisibility(View.GONE);
    }

    private void viewAccount(){
        receipientAddET = root.findViewById(R.id.receipient_wallet_add);
        amountET = root.findViewById(R.id.mdt_amount);

        walletAddress = walletAddressEt.getText().toString();
        pk = pkEt.getText().toString();
        Credentials credentials = Credentials.create(pk);

        // personal token access
        token = ERC20.load(contractAddress, client, credentials, new DefaultGasProvider());
        StaticGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(4_100_000_000L), BigInteger.valueOf(100_000));
        token.setGasProvider(gasProvider);

        // main bank token access
        Credentials mainCredentials = Credentials.create(mainAccPK);
        bankToken = ERC20.load(contractAddress, client, mainCredentials, new DefaultGasProvider());
        bankToken.setGasProvider(gasProvider);

        // print patient wallet and balance info
        updateBalanceView(token);
        TextView walletTV = root.findViewById(R.id.personal_wallet_add);
        walletTV.setText("Wallet Address:\n" + walletAddress);
        // top up 10MDT
        topUp.setOnClickListener(view -> {
            try {
                TransactionReceipt receipt = bankToken.transfer(walletAddress, new BigInteger("10000000000000000000")).sendAsync().get();
                showTXSuccessDialog(view, receipt);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        pay.setOnClickListener(view -> {
            if (checkEmptyField(receipientAddET)) return;
            if (checkEmptyField(amountET)) return;
            String receipientAddress = receipientAddET.getText().toString().trim();

            try {
                TransactionReceipt receipt = token.transfer(
                        receipientAddress,
                        new BigInteger(amountET.getText().toString().trim())
                                .multiply(new BigInteger("1000000000000000000")))
                        .sendAsync().get();
                showTXSuccessDialog(view, receipt);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void showTXSuccessDialog(View view, TransactionReceipt receipt){
        System.out.println("Transaction hash: "+receipt.getTransactionHash());
        updateBalanceView(token);
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    // view tx to etherscan
                    Uri link = Uri.parse("https://ropsten.etherscan.io/tx/" + receipt.getTransactionHash());
                    Intent intent = new Intent(Intent.ACTION_VIEW, link);
                    if(intent.resolveActivity(context.getPackageManager()) != null){
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Can't go to etherscan", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Transcation Completed, view on Etherscan.io?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void updateBalanceView(ERC20 token){
        try {
            BigInteger balance = token.balanceOf(walletAddress).sendAsync().get(10, TimeUnit.SECONDS);
            BigDecimal scaledBalance = new BigDecimal(balance)
                    .divide(new BigDecimal(1000000000000000000L), 18, RoundingMode.HALF_UP);

            TextView balanceTV = root.findViewById(R.id.personal_mdt_balance);
            balanceTV.setText("Balance:\n" + scaledBalance.toString());
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
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
                        Toast.makeText(context, "Welcome to MDT Portal", Toast.LENGTH_SHORT).show();
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
