package com.david0926.enlight;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
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

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private BluetoothSPP bt;

    private ActivityMainBinding binding;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        binding.bottomMain.setOnNavigationItemSelectedListener(item -> {
            viewModel.onReceiveListener = msg -> {
            };
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

        bt.setOnDataReceivedListener((data, message) -> viewModel.onReceiveListener.onReceive(message));

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(MainActivity.this, "연결에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                viewModel.onConnectListener.onConnect(name);
                bt.send("A", false);
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(MainActivity.this, "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                viewModel.onDisconnectListener.onDisconnect();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(MainActivity.this, "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                viewModel.onFailedListener.onFailed();
            }
        });

        viewModel.onScanListener = () -> {
            startActivityForResult(new Intent(MainActivity.this, DeviceList.class),
                    BluetoothState.REQUEST_CONNECT_DEVICE);
        };

        viewModel.onSendListener = msg -> bt.send(msg, false);

        viewModel.onDisconnectRequestListener = () -> {
            if (bt != null) bt.disconnect();
        };
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
    }
}