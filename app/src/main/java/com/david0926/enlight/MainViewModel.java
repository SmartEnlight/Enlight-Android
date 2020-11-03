package com.david0926.enlight;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.david0926.enlight.Main2.CountBodyData;

public class MainViewModel extends ViewModel {
    public MutableLiveData<String> pw = new MutableLiveData<>("");

    public MutableLiveData<String> wifiSsid = new MutableLiveData<>("");
    public MutableLiveData<String> wifiPw = new MutableLiveData<>("");

    public MutableLiveData<Integer> day = new MutableLiveData<>(0);
    public MutableLiveData<Integer> night = new MutableLiveData<>(0);

    public interface OnScanListener {
        void onScan();
    }

    public OnScanListener onScanListener = () -> {
    };

    public interface OnSendListener {
        void onSend(String msg);
    }

    public OnSendListener onSendListener = msg -> {
    };

    public interface OnDisconnectRequestListener {
        void onDisconnectRequest();
    }

    public OnDisconnectRequestListener onDisconnectRequestListener = () -> {
    };

    public interface OnDisconnectListener {
        void onDisconnect();
    }

    public OnDisconnectListener onDisconnectListener = () -> {
    };

    public interface OnFailedListener {
        void onFailed();
    }

    public OnFailedListener onFailedListener = () -> {
    };


    public interface OnConnectListener {
        void onConnect(String name);
    }

    public OnConnectListener onConnectListener = name -> {
    };

    public interface OnReceiveListener {
        void onReceive(String msg);
    }

    public OnReceiveListener onReceiveListener = msg -> {
    };
}

