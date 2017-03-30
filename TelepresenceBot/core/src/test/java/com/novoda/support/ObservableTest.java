package com.novoda.support;

import com.novoda.tpbot.Result;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ObservableTest {

    private static final Result EXPECTED_RESULT = Result.from("result");

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Observer<Result> observer;
    @Mock
    private Observer<Result> additionalObserver;

    @Test
    public void givenAnObservable_whenAttachingAnObserver_thenObserverIsNotifiedOfEmissions() {
        Observable.just(EXPECTED_RESULT)
                .attach(observer)
                .start();

        verify(observer).update(EXPECTED_RESULT);
    }

    @Test
    public void givenAnObservable_withMultipleObservers_whenDetachingAllObservers_thenObserversAreNotNotifiedOfEmissions() {
        Observable<Result> observable = givenObservableWithMultipleObservers();

        observable.detachObservers();
        observable.start();

        verifyZeroInteractions(observer);
        verifyZeroInteractions(additionalObserver);
    }

    @Test
    public void givenAnObservable_withMultipleObservers_whenDetachingAnObserver_thenRemainingObserverAreNotifiedOfEmissions() {
        Observable<Result> observable = givenObservableWithMultipleObservers();

        observable.detach(additionalObserver);
        observable.start();

        verify(observer).update(EXPECTED_RESULT);
        verifyZeroInteractions(additionalObserver);
    }

    private Observable<Result> givenObservableWithMultipleObservers() {
        return Observable.just(EXPECTED_RESULT)
                .attach(observer)
                .attach(additionalObserver);
    }

}
