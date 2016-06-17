package com.novoda.event;

import com.jakewharton.rxrelay.BehaviorRelay;

import rx.Notification;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public final class EventFunctions {

    private EventFunctions() {
        throw new IllegalStateException("NonInstantiableClassException");
    }

    public static <T> boolean isInitialised(BehaviorRelay<Event<T>> subject) {
        return hasData(subject) || isLoading(subject);
    }

    private static <T> boolean hasData(BehaviorRelay<Event<T>> subject) {
        return subject.getValue().data().isPresent();
    }

    private static <T> boolean isLoading(BehaviorRelay<Event<T>> subject) {
        return subject.getValue().state() == Status.LOADING;
    }

    public static <T> Observable.Transformer<T, Event<T>> asEvent(Class<T> clazz) {
        return asEvent();
    }

    public static <T> Observable.Transformer<T, Event<T>> asEvent() {
        return asEvent(Event.<T>loading());
    }

    public static <T> Observable.Transformer<T, Event<T>> asEvent(final Event<T> currentEvent) {
        return new Observable.Transformer<T, Event<T>>() {
            @Override
            public Observable<Event<T>> call(Observable<T> observable) {
                return observable.materialize().compose(EventFunctions.<T>notificationToEvent(currentEvent.asLoading()));
            }
        };
    }

    public static <T> Observable.Transformer<Notification<T>, Event<T>> notificationToEvent(final Event<T> startWith) {
        return new Observable.Transformer<Notification<T>, Event<T>>() {
            @Override
            public Observable<Event<T>> call(Observable<Notification<T>> observable) {
                return observable
                        .scan(
                                startWith,
                                new Func2<Event<T>, Notification<T>, Event<T>>() {
                                    @Override
                                    public Event<T> call(Event<T> event, Notification<T> notification) {
                                        switch (notification.getKind()) {
                                            case OnNext:
                                                return event.asLoadingWithData(notification.getValue());
                                            case OnCompleted:
                                                return event.asIdle();
                                            case OnError:
                                                return event.asError(notification.getThrowable());
                                            default:
                                                throw new IllegalStateException("No case defined for kind " + notification.getKind());
                                        }
                                    }
                                }
                        )
                        .startWith(startWith)
                        .distinctUntilChanged();
            }
        };
    }

    public static <T> Observable.Transformer<Event<T>, T> asData(Class<T> clazz) {
        return asData();
    }

    public static <T> Observable.Transformer<Event<T>, T> asData() {
        return new Observable.Transformer<Event<T>, T>() {
            @Override
            public Observable<T> call(Observable<Event<T>> observable) {
                return observable
                        .filter(EventFunctions.<T>dataPresent())
                        .map(EventFunctions.<T>extractData())
                        .distinctUntilChanged();
            }
        };
    }

    private static <T> Func1<Event<T>, Boolean> dataPresent() {
        return new Func1<Event<T>, Boolean>() {
            @Override
            public Boolean call(Event<T> event) {
                return event.data().isPresent();
            }
        };
    }

    private static <T> Func1<Event<T>, T> extractData() {
        return new Func1<Event<T>, T>() {
            @Override
            public T call(Event<T> event) {
                return event.data().get();
            }
        };
    }
}
