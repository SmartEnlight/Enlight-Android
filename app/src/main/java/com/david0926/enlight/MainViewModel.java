package com.david0926.enlight;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public MutableLiveData<String> pw = new MutableLiveData<>("");
}
