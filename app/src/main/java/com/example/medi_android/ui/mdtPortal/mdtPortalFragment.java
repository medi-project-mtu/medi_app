package com.example.medi_android.ui.mdtPortal;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.medi_android.R;
import com.example.medi_android.databinding.FragmentMdtPortalBinding;

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

    private FragmentMdtPortalBinding binding;
    private View root;
    Activity context;

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

        //set fab visibility off
        com.github.clans.fab.FloatingActionMenu floatingActionMenu = context.findViewById(R.id.fab);
        floatingActionMenu.setVisibility(View.GONE);

        final Web3j client = Web3j.build(new HttpService("https://ropsten.infura.io/v3/c4db87b143374a1093e3499dd15e795a"));
        final String walletAddress = "0x09F2278E1f81b481afFB813Ec07356B5C83003d3";
        final String pk = "f809526301afddd0ce9ae1f5034397aae964a099a923acb8885588ea2cfac821";
        Credentials credentials = Credentials.create(pk);

        String contractAddress = "0x96603d01f1717c7692c2d244fc045f0398239102";
        ERC20 token = ERC20.load(contractAddress, client, credentials, new DefaultGasProvider());

        try {
            BigInteger balance = token.balanceOf(walletAddress).sendAsync().get(10, TimeUnit.SECONDS);
            BigDecimal scaledBalance = new BigDecimal(balance)
                    .divide(new BigDecimal(1000000000000000000L), 18, RoundingMode.HALF_UP);

            TextView balanceTV = root.findViewById(R.id.balance);
            TextView walletTV = root.findViewById(R.id.wallet);
            walletTV.setText("Wallet Address: " + walletAddress);
            balanceTV.setText("Balance: " + scaledBalance.toString());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }
}
