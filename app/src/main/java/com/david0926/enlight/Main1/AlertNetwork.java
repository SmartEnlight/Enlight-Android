package com.david0926.enlight.Main1;

import android.content.Intent;
import android.content.res.Resources;

import com.david0926.enlight.R;
import com.david0926.enlight.api.NetworkHelper;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertNetwork {

    private static OnAlertSuccessListener onAlertSuccessListener;
    private static OnAlertFailedListener onAlertFailedListener;

    private static Resources mResources;

    public interface OnAlertSuccessListener {
        void onAlertSuccess(AlertBody body);
    }

    public interface OnAlertFailedListener {
        void onAlertFailed(String errorMsg);
    }

    public static void sendAlert(String token, String date, Integer db, Resources res, OnAlertSuccessListener s, OnAlertFailedListener e) {
        onAlertSuccessListener = s;
        onAlertFailedListener = e;
        mResources = res;

        if (token == null || token.isEmpty() || date == null || date.isEmpty()) {
            onAlertFailedListener.onAlertFailed(mResources.getString(R.string.error_empty_field));
            return;
        }

        requestAlert(token, date, db);
    }

    private static void requestAlert(String token, String date, Integer db) {
        NetworkHelper
                .getInstance(mResources.getString(R.string.base_url))
                .sendAlert(token, date, db)
                .enqueue(new Callback<AlertBody>() {
                    @Override
                    public void onResponse(Call<AlertBody> call, Response<AlertBody> response) {
                        AlertBody body;
                        if (response.code() != 200) {
                            try {
                                Gson gson = new Gson();
                                body = gson.fromJson(response.errorBody().string(), AlertBody.class);
                                onAlertFailedListener.onAlertFailed(body.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            body = response.body();
                            onAlertSuccessListener.onAlertSuccess(body);
                        }
                    }

                    @Override
                    public void onFailure(Call<AlertBody> call, Throwable t) {
                        t.printStackTrace();
                        onAlertFailedListener.onAlertFailed(t.getLocalizedMessage());
                    }
                });
    }


}
