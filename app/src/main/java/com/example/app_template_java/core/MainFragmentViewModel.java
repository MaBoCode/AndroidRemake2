package com.example.app_template_java.core;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.app_template_java.core.user.User;
import com.example.app_template_java.core.user.UserService;
import com.example.app_template_java.injects.base.BaseViewModel;
import com.example.app_template_java.utils.Logs;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragmentViewModel extends BaseViewModel {

    protected UserService userService;

    protected MutableLiveData<List<User>> _usersLiveData = new MutableLiveData<>();
    public LiveData<List<User>> usersLiveData = _usersLiveData;

    @ViewModelInject
    public MainFragmentViewModel(UserService userService, @Assisted SavedStateHandle savedStateHandle) {
        this.userService = userService;
        this.savedStateHandle = savedStateHandle;
    }

    public void getUsers() {
        userService.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        _loadingLiveData.postValue(LoadingStatus.LOADING);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Throwable {
                        _loadingLiveData.postValue(LoadingStatus.NOT_LOADING);
                    }
                })
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Throwable {
                        _usersLiveData.postValue(users);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Logs.error(this, throwable.getMessage());
                    }
                });
    }

}
