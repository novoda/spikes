package com.amazonaws.cognito.sync.demo;

import android.content.Context;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ToastObserver implements Observer<String> {

    private final Context context;

    public ToastObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    private void showToast(String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    @Override
    public void onError(Throwable throwable) {
        showToast(throwable.getMessage(), Toast.LENGTH_LONG);
    }

    @Override
    public void onComplete() {
    }
}
