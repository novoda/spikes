package com.gertherb.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BetterAccountManager {

    private final AccountManager accountManager;
    private final Activity activity;
    private final String accountType;
    private final String tokenType;
    private final String[] requiredFeatures;

    public BetterAccountManager(Activity activity, String accountType, String tokenType, String[] requiredFeatures) {
        this.activity = activity;
        this.accountType = accountType;
        this.tokenType = tokenType;
        this.requiredFeatures = requiredFeatures;
        this.accountManager = AccountManager.get(activity.getApplicationContext());
    }

    public Observable<Token> addAccountAndGetToken(final Bundle options) {
        return withDefaultSchedulers(addAccount(options).flatMap(new Func1<String, Observable<? extends Token>>() {
            @Override
            public Observable<? extends Token> call(String s) {
                return getSessionKey(options);
            }
        }));
    }

    public Observable<Token> getSessionKeyOrAddAccount(Bundle options) {
        return withDefaultSchedulers(getSessionKey(options).onErrorFlatMap(addNewAccount(options)));
    }

    private Observable<Token> getSessionKey(final Bundle options) {
        return Observable.create(new Observable.OnSubscribe<Token>() {
            @Override
            public void call(Subscriber<? super Token> subscriber) {
                Account[] accounts = accountManager.getAccountsByType(accountType);
                if (accounts.length == 0) {
                    subscriber.onError(new NoAccountException());
                    return;
                }

                Bundle result;
                try {
                    result = getAuthToken(accounts[0], options);
                } catch (AuthenticatorException e) {
                    subscriber.onError(e);
                    return;
                } catch (OperationCanceledException e) {
                    subscriber.onError(e);
                    return;
                } catch (IOException e) {
                    subscriber.onError(e);
                    return;
                }

                subscriber.onNext(new Token(accounts[0].name, result.getString(AccountManager.KEY_AUTHTOKEN)));
                subscriber.onCompleted();
            }
        });
    }

    private Bundle getAuthToken(Account account, Bundle options) throws AuthenticatorException, OperationCanceledException, IOException {
        return accountManager.getAuthToken(account, tokenType, options, activity, null, null).getResult();
    }

    private Func1<OnErrorThrowable, Observable<? extends Token>> addNewAccount(final Bundle options) {
        return new Func1<OnErrorThrowable, Observable<? extends Token>>() {
            @Override
            public Observable<? extends Token> call(OnErrorThrowable onErrorThrowable) {
                if (onErrorThrowable.getCause() instanceof NoAccountException) {
                    return addAccountAndGetToken(options);
                }
                return Observable.error(onErrorThrowable.getCause());
            }
        };
    }

    private Observable<String> addAccount(final Bundle options) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Bundle result;
                try {
                    result = accountManager.addAccount(accountType, tokenType, requiredFeatures, options, activity, null, null).getResult();
                } catch (OperationCanceledException e) {
                    subscriber.onError(e);
                    return;
                } catch (IOException e) {
                    subscriber.onError(e);
                    return;
                } catch (AuthenticatorException e) {
                    subscriber.onError(e);
                    return;
                }

                subscriber.onNext(result.getString(AccountManager.KEY_ACCOUNT_NAME));
                subscriber.onCompleted();
            }
        });
    }

    private <T> Observable<T> withDefaultSchedulers(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread());
    }

}
