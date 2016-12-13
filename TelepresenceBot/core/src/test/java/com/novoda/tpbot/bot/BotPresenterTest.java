package com.novoda.tpbot.bot;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BotPresenterTest {

    private static final Result SUCCESS_RESULT = Result.from("Connection Successful!");
    private static final Result FAILURE_RESULT = Result.from(new Exception("Connection Unsuccessful"));
    private static final Direction DIRECTION = Direction.FORWARD;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    BotTpService tpService;

    @Mock
    BotView botView;

    private BotPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new BotPresenter(tpService, botView);
    }

    @Test
    public void givenSuccessfulConnection_whenStartPresenting_thenBotViewOnConnectIsCalled() {
        when(tpService.connect()).thenReturn(Observable.just(SUCCESS_RESULT));

        presenter.startPresenting();

        verify(botView).onConnect(SUCCESS_RESULT.message().get());
    }

    @Test
    public void givenUnsuccessfulConnection_whenStartPresenting_thenBotViewOnErrorIsCalled() {
        when(tpService.connect()).thenReturn(Observable.just(FAILURE_RESULT));

        presenter.startPresenting();

        verify(botView).onError(FAILURE_RESULT.exception().get().getMessage());
    }

    @Test
    public void givenAlreadyPresenting_whenStopPresentingIsCalled_thenTpServiceDisconnectIsCalled() {
        when(tpService.connect()).thenReturn(Observable.just(SUCCESS_RESULT));
        presenter.startPresenting();

        presenter.stopPresenting();

        verify(tpService).disconnect();
    }

    @Test
    public void givenAlreadyPresenting_whenStopPresentingIsCalled_thenConnectionObservableObserversAreDetached() {
        Observable<Result> observable = Observable.just(SUCCESS_RESULT);
        Observable<Result> spyObservable = Mockito.spy(observable);
        when(tpService.connect()).thenReturn(spyObservable);
        presenter.startPresenting();

        presenter.stopPresenting();

        verify(spyObservable).detachObservers();
    }

    @Test
    public void givenDirection_whenStartListeningForDirectionIsCalled_thenTpServiceMoveInIsCalled() {
        when(tpService.listen()).thenReturn(Observable.just(DIRECTION));

        presenter.startListeningForDirection();

        verify(botView).moveIn(DIRECTION);
    }

    @Test
    public void givenStartedListeningForDirections_whenStopPresentingIsCalled_thenDirectionObservableObserversAreDetached() {
        Observable<Direction> observable = Observable.just(DIRECTION);
        Observable<Direction> spyObservable = Mockito.spy(observable);
        when(tpService.listen()).thenReturn(spyObservable);
        presenter.startListeningForDirection();

        presenter.stopPresenting();

        verify(spyObservable).detachObservers();
    }

}
