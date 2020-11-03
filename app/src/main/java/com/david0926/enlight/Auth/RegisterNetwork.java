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

public class RegisterNetwork {

    private static OnRegisterSuccessListener onRegisterSuccessListener;
    private static OnRegisterFailedListener onRegisterFailedListener;

    private static Resources mResources;

    public interface OnRegisterSuccessListener {
        void onRegisterSuccess();
    }

    public interface OnRegisterFailedListener {
        void onRegisterFailed(String errorMsg);
    }

    public static void register(String email, String pw, String name, Resources res, OnRegisterSuccessListener s, OnRegisterFailedListener e) {
        onRegisterSuccessListener = s;
        onRegisterFailedListener = e;
        mResources = res;

        if (email == null || pw == null || email.isEmpty() || pw.isEmpty()) {
            onRegisterFailedListener.onRegisterFailed(mResources.getString(R.string.error_empty_field));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onRegisterFailedListener.onRegisterFailed(mResources.getString(R.string.error_email_invalid));
            return;
        }

        getToken(s1 -> signIn(email, pw, name, s1));

    }

    private static void getToken(OnSuccessListener<String> s) {
        FirebaseMessaging
                .getInstance()
                .getToken()
                .addOnSuccessListener(s)
                .addOnFailureListener(e -> onRegisterFailedListener.onRegisterFailed("토큰을 가져오는 데 실패했습니다."));
    }

    private static void signIn(String email, String pw, String name, String token) {
        NetworkHelper
                .getInstance(mResources.getString(R.string.base_url))
                .signUp(email, pw, name, token)
                .enqueue(new Callback<RegisterBody>() {
                    @Override
                    public void onResponse(Call<RegisterBody> call, Response<RegisterBody> response) {
                        RegisterBody body;
                        if (response.code() != 200) {
                            try {
                                Gson gson = new Gson();
                                body = gson.fromJson(response.errorBody().string(), RegisterBody.class);
                                onRegisterFailedListener.onRegisterFailed(body.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            body = response.body();
                            onRegisterSuccessListener.onRegisterSuccess();
                        }


                    }

                    @Override
                    public void onFailure(Call<RegisterBody> call, Throwable t) {
                        t.printStackTrace();
                        onRegisterFailedListener.onRegisterFailed(t.getLocalizedMessage());
                    }
                });
    }


}
