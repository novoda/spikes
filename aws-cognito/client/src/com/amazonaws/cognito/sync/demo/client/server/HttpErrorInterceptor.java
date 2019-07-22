package com.amazonaws.cognito.sync.demo.client.server;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.Response;

public class HttpErrorInterceptor implements Function<Response, ObservableSource<Response>> {

    private final Function<Response, ? extends Throwable> converter;

    public HttpErrorInterceptor(Function<Response, ? extends Throwable> converter) {
        this.converter = converter;
    }

    @Override
    public ObservableSource<Response> apply(@NonNull final Response response) throws Exception {
        return Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                if (response.isSuccessful()) {
                    emitter.onNext(response);
                    emitter.onComplete();
                } else {
                    emitter.onError(converter.apply(response));
                }
            }
        });
    }

}
