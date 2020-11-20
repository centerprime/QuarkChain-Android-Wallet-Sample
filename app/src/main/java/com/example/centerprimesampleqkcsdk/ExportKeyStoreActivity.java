package com.example.centerprimesampleqkcsdk;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.centerprime.quarkchainsdk.QKCManager;
import com.example.centerprimesampleqkcsdk.databinding.ActivityExportKeystoreBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ExportKeyStoreActivity extends AppCompatActivity {
    ActivityExportKeystoreBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_export_keystore);

        /**
         * Using this getKeyStore function user can get keyStore of provided walletAddress.
         *
         * @params WalletAddress, Context
         *
         * @return keyStore
         */

        QKCManager qkcManager = QKCManager.getInstance();
        qkcManager.init("http://jrpc.mainnet.quarkchain.io:38391");

        binding.button.setOnClickListener(v -> {
            String walletAddress = "";
            if (!TextUtils.isEmpty(binding.address.getText().toString())) {
                walletAddress = binding.address.getText().toString();
            } else {
                Toast.makeText(this, "Enter wallet address", Toast.LENGTH_SHORT).show();
            }

            qkcManager.getKeyStore(walletAddress, this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(keystore -> {

                        binding.keystoreT.setVisibility(View.VISIBLE);
                        binding.copy.setVisibility(View.VISIBLE);
                        binding.keystoreT.setText(keystore);
                        hideKeyboard(this);

                    }, error -> {
                        Toast.makeText(this, "Please insert valid wallet address", Toast.LENGTH_SHORT).show();
                    });
        });

        binding.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", binding.keystoreT.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}