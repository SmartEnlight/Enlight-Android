package com.david0926.enlight.Auth;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.enlight.MainActivity;
import com.david0926.enlight.R;
import com.david0926.enlight.databinding.ActivityLoginBinding;
import com.david0926.enlight.util.TokenCache;
import com.david0926.enlight.util.UserCache;
import com.google.firebase.auth.FirebaseAuth;

import gun0912.tedkeyboardobserver.TedKeyboardObserver;

public class LoginActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //scroll to bottom when keyboard up
        new TedKeyboardObserver(this).listen(isShow -> {
            binding.scrollLogin.smoothScrollTo(0, binding.scrollLogin.getBottom());
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        //sign in button clicked
        binding.btnLoginSignin.setOnClickListener(view -> {
            binding.setOnProgress(true);
            hideKeyboard(this);

            String id = binding.getEmail(), pw = binding.getPw();
            LoginNetwork.login(id, pw, getResources(), (token, user) -> {
                runOnUiThread(() -> {
                    TokenCache.setToken(this, token);
                    UserCache.setUser(this, user);
                    finishSignIn();
                });
            }, this::showErrorMsg);

        });

        //sign up button clicked
        binding.btnLoginRegi.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_up, R.anim.slide_up_before);
        });

        //finish when sign up success
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("finish_signup")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_signup"));
    }

    private void finishSignIn() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_down_before, R.anim.slide_down);
        finish();
    }

    private void showErrorMsg(String msg) {
        binding.setOnProgress(false);
        binding.txtLoginError.setVisibility(View.VISIBLE);
        binding.txtLoginError.setText(msg);
        binding.txtLoginError.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    private void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
