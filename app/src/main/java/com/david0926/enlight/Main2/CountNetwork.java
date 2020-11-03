package com.david0926.enlight.Main2;

import android.content.res.Resources;
import android.util.Log;

import com.david0926.enlight.R;
import com.david0926.enlight.api.NetworkHelper;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountNetwork {

    private static OnCountSuccessListener onCountSuccessListener;
    private static OnCountFailedListener onCountFailedListener;

    private static Resources mResources;

    public interface OnCountSuccessListener {
        void onCountSuccess(CountBodyData data);
    }

    public interface OnCountFailedListener {
        void onCountFailed(String errorMsg);
    }

    public static void getCount(String token, String date, Resources res, OnCountSuccessListener s, OnCountFailedListener e) {
        onCountSuccessListener = s;
        onCountFailedListener = e;
        mResources = res;

        if (token == null || token.isEmpty() || date == null || date.isEmpty()) {
            onCountFailedListener.onCountFailed(mResources.getString(R.string.error_empty_field));
            return;
        }

        requestCount(token, date);
    }

    private static void requestCount(String token, String date) {
        NetworkHelper
                .getInstance(mResources.getString(R.string.base_url))
                .getCount(token, date)
                .enqueue(new Callback<CountBody>() {
                    @Override
                    public void onResponse(Call<CountBody> call, Response<CountBody> response) {
                        CountBody body;
                        if (response.code() != 200) {
                            try {
                                Gson gson = new Gson();
                                body = gson.fromJson(response.errorBody().string(), CountBody.class);
                                onCountFailedListener.onCountFailed(body.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            body = response.body();
                            onCountSuccessListener.onCountSuccess(body.getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<CountBody> call, Throwable t) {
                        t.printStackTrace();
                        onCountFailedListener.onCountFailed(t.getLocalizedMessage());
                    }
                });
    }


}
