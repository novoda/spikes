package com.amazonaws.cognito.sync.demo.client;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AndroidExecutionStrategy<T> implements ObservableTransformer<T, T> {

    @Override
    public ObservableSource<T> apply(Observable<T> observable) {
        return observable.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
