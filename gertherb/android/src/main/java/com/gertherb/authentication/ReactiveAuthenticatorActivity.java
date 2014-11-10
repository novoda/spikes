package com.gertherb.authentication;

import android.accounts.AccountManager;
import android.os.Bundle;

import com.gertherb.R;
import com.novoda.notils.logger.simple.Log;
import com.novoda.rx.core.ObservableVault;
import com.novoda.rx.core.ObserverFactory;
import com.novoda.rx.core.ResumableReference;
import com.novoda.rx.core.ResumableSubscriber;
import com.novoda.rx.core.observer.ResumableObserver;
import com.novoda.rx.exception.CancelledException;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class ReactiveAuthenticatorActivity<T> extends BetterAuthenticatorActivity implements ObserverFactory, ResumableReference {

    private ResumableSubscriber resumableSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resumableSubscriber = new ResumableSubscriber(this, this, getObservebleVault());
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLoginProcess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumableSubscriber.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumableSubscriber.pause();
    }

    protected abstract ObservableVault getObservebleVault();

    protected abstract Observable<T> getCredentialsObservable();

    protected abstract Observable<Token> getTokenObservable(T credentials);

    private void initLoginProcess() {
        getCredentialsObservable().subscribe(new Observer<T>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Error retrieving user credentials", e);
            }

            @Override
            public void onNext(T credentials) {
                resumableSubscriber.subscribe(getTokenObservable(credentials)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()), new TokenObserver());
            }
        });
    }

    protected void handleError(Throwable throwable) {
        setErrorResultAuthenticator(AccountManager.ERROR_CODE_REMOTE_EXCEPTION, throwable);
    }

    @Override
    public ResumableObserver createObserver(int code) {
        switch (code) {
            case TokenObserver.ID:
                return new TokenObserver();
            default:
                throw new IllegalArgumentException("No case defined for id " + code);
        }
    }

    private Func1<T, Observable<? extends Token>> login() {
        return new Func1<T, Observable<? extends Token>>() {
            @Override
            public Observable<? extends Token> call(T credentials) {
                return getTokenObservable(credentials);
            }
        };
    }

    private class TokenObserver implements ResumableObserver<Token> {

        private static final int ID = R.id.token_observer;

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable throwable) {
            if (throwable instanceof CancelledException) {
                setCancelResultAuthenticator(throwable);
                return;
            }
            handleError(throwable);
        }

        @Override
        public void onNext(Token token) {
            setSuccessResponse(token);
        }
    }
}
