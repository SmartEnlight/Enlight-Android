package com.david0926.enlight;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.david0926.enlight.Main1.MainFragment1;
import com.david0926.enlight.Main2.MainFragment2;
import com.david0926.enlight.Main3.MainFragment3;
import com.david0926.enlight.databinding.ActivityMainBinding;
import com.david0926.enlight.util.SerialRules;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiverScan, broadcastReceiverSend, broadcastReceiverDisconnect;
    private BluetoothSPP bt;

    private ActivityMainBinding binding;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        binding.bottomMain.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_1:
                    if (bt.getConnectedDeviceName() != null) {
                        switchFragment(MainFragment1.newInstanceConnected(bt.getConnectedDeviceName()));
                    } else switchFragment(MainFragment1.newInstance());
                    break;
                case R.id.action_2:
                    switchFragment(MainFragment2.newInstance());
                    break;
                case R.id.action_3:
                    switchFragment(MainFragment3.newInstance());
                    break;
            }
            return true;
        });
        switchFragment(MainFragment1.newInstance());

        bt = new BluetoothSPP(this);
        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(this, "블루투스 사용이 불가능합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }


        bt.setOnDataReceivedListener((data, message) -> {
            if (message.charAt(0) == SerialRules.CONNECT_SUCCESS) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                String ssid = info.getSSID();

                String wifiCode = "B:" + ssid + "," + viewModel.pw.getValue();
                bt.send(wifiCode, false);
            }

            Log.d("debug", "bluetooth: " + message);
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                //Toast.makeText(MainActivity.this, "연결에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("main_connected");
                intent.putExtra("name", name);
                sendBroadcast(intent);

                bt.send("A", false);
            }

            public void onDeviceDisconnected() { //연결해제
                //Toast.makeText(MainActivity.this, "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent("main_disconnected"));
            }

            public void onDeviceConnectionFailed() { //연결실패
                //Toast.makeText(MainActivity.this, "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent("main_failed"));
            }
        });

        broadcastReceiverScan = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("main1_scan_device")) {
                    startActivityForResult(new Intent(MainActivity.this, DeviceList.class),
                            BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        };
        registerReceiver(broadcastReceiverScan, new IntentFilter("main1_scan_device"));

        broadcastReceiverSend = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("main1_send_message")) {
                    bt.send(intent.getStringExtra("message"), false);
                }
            }
        };
        registerReceiver(broadcastReceiverSend, new IntentFilter("main1_send_message"));

        broadcastReceiverDisconnect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("main1_disconnect")) {
                    if (bt != null) bt.disconnect();
                }

            }
        };
        registerReceiver(broadcastReceiverDisconnect, new IntentFilter("main1_disconnect"));

    }

    void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) bt.connect(data);
            else sendBroadcast(new Intent("main_failed"));
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(this, "블루투스가 활성화되지 않았습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
        unregisterReceiver(broadcastReceiverScan);
        unregisterReceiver(broadcastReceiverSend);
        unregisterReceiver(broadcastReceiverDisconnect);
    }
}