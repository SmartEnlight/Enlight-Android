package com.david0926.enlight.Main1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.david0926.enlight.MainViewModel;
import com.david0926.enlight.R;
import com.david0926.enlight.databinding.FragmentMain1Binding;

import org.jetbrains.annotations.NotNull;

public class MainFragment1 extends Fragment {

    public static MainFragment1 newInstance() {
        return new MainFragment1();
    }

    public static MainFragment1 newInstanceConnected(String name) {
        return new MainFragment1(name);
    }

    private String name;

    private BroadcastReceiver broadcastReceiverConnect;
    private BroadcastReceiver broadcastReceiverDisConnect;
    private BroadcastReceiver broadcastReceiverFailed;

    private Context mContext;
    private FragmentMain1Binding binding;

    private MainViewModel viewModel;

    private MainFragment1(String name) {
        this.name = name;
    }

    private MainFragment1() {
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
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
        binding.setTime("0");
        binding.setOnProgress(false);
        binding.setIsAlert(false);
        binding.setText("");
        binding.setPw("");

        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(viewModel);

        binding.btnMain1Send.setOnClickListener(v -> sendMessage(binding.getText()));

        binding.btnMain1Scan.setOnClickListener(view -> {
            if (!binding.getIsConnected()) {
                mContext.sendBroadcast(new Intent("main1_scan_device"));
                binding.setOnProgress(true);
            } else {
                mContext.sendBroadcast(new Intent("main1_disconnect"));
                binding.setIsConnected(false);
            }
        });

        broadcastReceiverConnect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("main_connected")) {
                    binding.setOnProgress(false);
                    binding.setIsConnected(true);
                    binding.setName(intent.getStringExtra("name"));
                }
            }
        };
        mContext.registerReceiver(broadcastReceiverConnect, new IntentFilter("main_connected"));

        broadcastReceiverDisConnect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("main_disconnected")) {
                    binding.setOnProgress(false);
                    binding.setIsConnected(false);
                }
            }
        };
        mContext.registerReceiver(broadcastReceiverDisConnect, new IntentFilter("main_disconnected"));

        broadcastReceiverFailed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("main_failed")) {
                    binding.setOnProgress(false);
                }
            }
        };
        mContext.registerReceiver(broadcastReceiverFailed, new IntentFilter("main_failed"));

        return binding.getRoot();
    }

    private void sendMessage(String msg) {
        Intent intent = new Intent("main1_send_message");
        intent.putExtra("message", msg);
        mContext.sendBroadcast(intent);
        //binding.setText("");
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(broadcastReceiverConnect);
        mContext.unregisterReceiver(broadcastReceiverFailed);
        mContext.unregisterReceiver(broadcastReceiverDisConnect);
        super.onDestroy();
    }
}
