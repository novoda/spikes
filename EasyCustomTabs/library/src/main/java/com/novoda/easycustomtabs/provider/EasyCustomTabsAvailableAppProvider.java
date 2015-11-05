package com.novoda.easycustomtabs.provider;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EasyCustomTabsAvailableAppProvider implements AvailableAppProvider {

    private final Observable<String> observable;
    private final BestPackageFinder bestPackageFinder;

    EasyCustomTabsAvailableAppProvider(Observable<String> observable, BestPackageFinder bestPackageFinder) {
        this.observable = observable;
        this.bestPackageFinder = bestPackageFinder;
    }

    public static EasyCustomTabsAvailableAppProvider newInstance() {
        BestPackageFinder bestPackageFinder = BestPackageFinder.newInstance();
        return new EasyCustomTabsAvailableAppProvider(createObservable(), bestPackageFinder);
    }

    private static Observable<String> createObservable() {
        return Observable.<String>empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    @WorkerThread
    public void findBestPackage(@NonNull PackageFoundCallback packageFoundCallback) {
        observable.mergeWith(findBestPackageObservable())
                .subscribe(findBestPackageSubscriber(packageFoundCallback));
    }

    private Observable<String> findBestPackageObservable() {
        return Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        String packageName = bestPackageFinder.findBestPackage();
                        subscriber.onNext(packageName);
                        subscriber.onCompleted();
                    }
                }
        );
    }

    private Subscriber<String> findBestPackageSubscriber(final PackageFoundCallback packageFoundCallback) {
        return new Subscriber<String>() {
            @Override
            public void onCompleted() {
                //no-op
            }

            @Override
            public void onError(Throwable e) {
                packageFoundCallback.onPackageNotFound();
            }

            @Override
            public void onNext(String packageName) {
                if (TextUtils.isEmpty(packageName)) {
                    packageFoundCallback.onPackageNotFound();
                }

                packageFoundCallback.onPackageFound(packageName);
            }
        };
    }

    public interface PackageFoundCallback {
        void onPackageFound(String packageName);

        void onPackageNotFound();
    }

}
