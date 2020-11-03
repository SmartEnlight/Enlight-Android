package com.david0926.enlight.Main1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.david0926.enlight.MainViewModel;
import com.david0926.enlight.R;
import com.david0926.enlight.databinding.FragmentMain1Binding;
import com.david0926.enlight.util.TokenCache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainFragment1 extends Fragment {

    public static MainFragment1 newInstance() {
        return new MainFragment1();
    }

    public static MainFragment1 newInstanceConnected(String name) {
        return new MainFragment1(name);
    }

    private String name;
    private FragmentMain1Binding binding;

    private MainViewModel viewModel;

    private MainFragment1(String name) {
        this.name = name;
    }

    private MainFragment1() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main1, container, false);
        if (name != null) {
            binding.setName(name.trim());
            binding.setIsConnected(true);
        } else {
            binding.setName("Device Name");
            binding.setIsConnected(false);
        }
        binding.setOnProgress(false);

        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(viewModel);

        binding.btnMain1Scan.setOnClickListener(view -> {
            if (!binding.getIsConnected()) {
                viewModel.onScanListener.onScan();
                binding.setOnProgress(true);
            } else {
                viewModel.onDisconnectRequestListener.onDisconnectRequest();
            }
        });

        binding.imageView4.setOnClickListener(v -> viewModel.onSendListener.onSend("test"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd", Locale.getDefault());

        viewModel.onReceiveListener = msg -> {
            Log.d("baam", "onCreate: " + msg);
            if (msg.length() == 6 && msg.charAt(0) == '[' && msg.charAt(msg.length() - 1) == ']') {
                int db = Integer.parseInt(msg.substring(1, 5));
                binding.setDb(db);
                AlertNetwork.sendAlert(TokenCache.getToken(requireContext()), simpleDateFormat.format(new Date()),
                        db, getResources(), body -> {

                        }, errorMsg -> Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show());
            }
        };

        viewModel.onConnectListener = name -> {
            binding.setOnProgress(false);
            binding.setIsConnected(true);
            binding.setName(name);
        };

        viewModel.onDisconnectListener = () -> {
            binding.setOnProgress(false);
            binding.setIsConnected(false);
        };

        viewModel.onFailedListener = () -> binding.setOnProgress(false);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
