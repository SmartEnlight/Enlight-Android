package com.david0926.enlight.Auth;

import android.content.res.Resources;
import android.util.Log;

import com.david0926.enlight.R;
import com.david0926.enlight.api.NetworkHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginNetwork {

    private static OnLoginSuccessListener onLoginSuccessListener;
    private static OnLoginFailedListener onLoginFailedListener;

    private static Resources mResources;

    public interface OnLoginSuccessListener {
        void onLoginSuccess(String token, UserModel user);
    }

    public interface OnLoginFailedListener {
        void onLoginFailed(String errorMsg);
    }

    public static void login(String email, String pw, Resources res, OnLoginSuccessListener s, OnLoginFailedListener e) {
        onLoginSuccessListener = s;
        onLoginFailedListener = e;
        mResources = res;

        if (email == null || pw == null || email.isEmpty() || pw.isEmpty()) {
            onLoginFailedListener.onLoginFailed(mResources.getString(R.string.error_empty_field));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onLoginFailedListener.onLoginFailed(mResources.getString(R.string.error_email_invalid));
            return;
        }

        getToken(s1 -> signIn(email, pw, s1));

    }

    private static void getToken(OnSuccessListener<String> s) {
        FirebaseMessaging
                .getInstance()
                .getToken()
                .addOnSuccessListener(s)
                .addOnFailureListener(e -> onLoginFailedListener.onLoginFailed("토큰을 가져오는 데 실패했습니다."));
    }

    private static void signIn(String email, String pw, String token) {
        NetworkHelper
                .getInstance(mResources.getString(R.string.base_url))
                .signIn(email, pw, token)
                .enqueue(new Callback<LoginBody>() {
                    @Override
                    public void onResponse(Call<LoginBody> call, Response<LoginBody> response) {
                        LoginBody body;
                        if (response.code() != 200) {
                            try {
                                Gson gson = new Gson();
                                body = gson.fromJson(response.errorBody().string(), LoginBody.class);
                                onLoginFailedListener.onLoginFailed(body.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            body = response.body();
                            onLoginSuccessListener.onLoginSuccess(body.getAccessToken(), body.getUserInfo());
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginBody> call, Throwable t) {
                        t.printStackTrace();
                        onLoginFailedListener.onLoginFailed(t.getLocalizedMessage());
                    }
                });
    }


}
