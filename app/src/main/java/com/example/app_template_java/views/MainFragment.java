package com.example.app_template_java.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_template_java.core.MainFragmentViewModel;
import com.example.app_template_java.core.user.User;
import com.example.app_template_java.databinding.FrgMainBinding;
import com.example.app_template_java.injects.base.BaseFragment;
import com.example.app_template_java.injects.base.BaseViewModel.LoadingStatus;
import com.example.app_template_java.views.utils.AnimationUtils;

import org.androidannotations.annotations.EFragment;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class MainFragment extends BaseFragment {

    protected FrgMainBinding binding;

    protected MainFragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FrgMainBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);

        initObservers();

        viewModel.getUsers();

        return binding.getRoot();
    }

    @Override
    public void initObservers() {

        viewModel.usersLiveData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                String usersCount = String.format("%s users", users.size());
                binding.usersCountTxtView.setText(usersCount);

                int randomIndex = (int) (Math.random() * users.size());
                User user = users.get(randomIndex);
                binding.usernameTxtView.setText(user.username);

                startAnimations();
            }
        });

        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), new Observer<LoadingStatus>() {
            @Override
            public void onChanged(LoadingStatus status) {
                int visiblity = status == LoadingStatus.LOADING ? View.VISIBLE : View.GONE;
                binding.loader.setVisibility(visiblity);
            }
        });
    }

    public void startAnimations() {
        new AnimationUtils.Builder()
                .setObjects(Arrays.asList(binding.usersCountTxtView, binding.usernameTxtView))
                .setAnimateAlphaIn(true)
                .setTranslationYBegin(-400f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDelay(300)
                .start();
    }
}
