package com.david0926.enlight.Auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.enlight.R;
import com.david0926.enlight.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gun0912.tedkeyboardobserver.TedKeyboardObserver;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        //finish activity, when back button pressed
        binding.toolbarRegi.setNavigationOnClickListener(view -> finish());

        //scroll to bottom when keyboard up
        new TedKeyboardObserver(this).listen(isShow -> {
            binding.scrollRegi.smoothScrollTo(0, binding.scrollRegi.getBottom());
        });

        //sign up button clicked
        binding.btnRegiSignup.setOnClickListener(view -> {

            binding.setOnProgress(true);
            hideKeyboard(this);

            if (TextUtils.isEmpty(binding.getName()) || TextUtils.isEmpty(binding.getEmail())
                    || TextUtils.isEmpty(binding.getPw()) || TextUtils.isEmpty(binding.getPwcheck())) //empty field
                showErrorMsg("Please fill all required fields.");

            else if (!isValidEmail(binding.getEmail())) //invalid email
                showErrorMsg("Please enter a valid email address.");

            else if (!isValidPw(binding.getPw())) //invalid password
                showErrorMsg("Please enter a valid password. (6~24 letters, 0-9 + A-z)");

            else if (!binding.getPw().equals(binding.getPwcheck())) //password confirm failed
                showErrorMsg("Please enter same password in both fields.");

            else //confirm success
                createAccount(binding.getName(), binding.getEmail(), binding.getPw());

        });

    }

    private void createAccount(String name, String email, String pw) {
        RegisterNetwork.register(email, pw, name, getResources(), this::finishSignUp, this::showErrorMsg);
    }

    private void finishSignUp() {
        sendBroadcast(new Intent("finish_signup"));

        binding.animatorRegi.showNext();
        binding.lottieRegiFinish.playAnimation();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }, binding.lottieRegiFinish.getDuration() + 1000);
    }

    private void showErrorMsg(String msg) {
        binding.setOnProgress(false);
        binding.txtRegiError.setVisibility(View.VISIBLE);
        binding.txtRegiError.setText(msg);
        binding.txtRegiError.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    private boolean isValidEmail(String target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidPw(String target) {
        //6~24 letters, 0~9 + A-z
        Pattern p = Pattern.compile("(^.*(?=.{6,24})(?=.*[0-9])(?=.*[A-z]).*$)");
        Matcher m = p.matcher(target);
        //except korean letters
        return m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    private void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_down_before, R.anim.slide_down);
    }

}
