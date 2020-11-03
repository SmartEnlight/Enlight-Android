package com.david0926.enlight.Main3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.david0926.enlight.Auth.LoginActivity;
import com.david0926.enlight.Auth.UserModel;
import com.david0926.enlight.R;
import com.david0926.enlight.databinding.FragmentMain3Binding;
import com.david0926.enlight.util.UserCache;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class MainFragment3 extends Fragment {

    public static MainFragment3 newInstance() {
        return new MainFragment3();
    }

    private Context mContext;
    private FragmentMain3Binding binding;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main3, container, false);

        binding.btnMain3Profile.setOnClickListener(view -> {
            UserModel model = UserCache.getUser(mContext);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("My Profile").setMessage("Username: "+model.getUsername()
            +"\nEmail: "+model.getEmail());
            builder.setPositiveButton("Confirm", (dialogInterface, i) -> {}).show();
        });

        binding.btnMain3Share.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Wanna join Enlight? \n" +
                    "https://github.com/roian6/Enlight");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

        binding.btnMain3Logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(mContext, LoginActivity.class));

            Activity activity = getActivity();
            if (activity == null) return;
            activity.finish();
        });
        return binding.getRoot();
    }
}
