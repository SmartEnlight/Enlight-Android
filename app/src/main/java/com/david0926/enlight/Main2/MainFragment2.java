package com.david0926.enlight.Main2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.david0926.enlight.MainViewModel;
import com.david0926.enlight.R;
import com.david0926.enlight.databinding.FragmentMain2Binding;
import com.david0926.enlight.util.TokenCache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainFragment2 extends Fragment {

    public static MainFragment2 newInstance() {
        return new MainFragment2();
    }

    private FragmentMain2Binding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main2, container, false);
        binding.setLifecycleOwner(requireActivity());
        binding.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(viewModel);

        binding.btnMain2Share.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Wanna join Enlight? \n" +
                    "https://github.com/roian6/Enlight");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        //TODO: replace with LocalDateTime
        CountNetwork.getCount(TokenCache.getToken(requireContext()), simpleDateFormat.format(new Date()), getResources(),
                data -> {
                    viewModel.day.setValue(data.getMorning());
                    viewModel.night.setValue(data.getNight());
                },
                errorMsg -> Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
