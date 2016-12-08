package com.novoda.tpbot.bot;

import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BotPresenterTest {

    private static final Result SUCCESS_RESULT = Result.from("Connection Successful!");
    private static final Result FAILURE_RESULT = Result.from(new Exception("Connection Unsuccessful"));

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    BotTpService tpService;

    @Mock
    BotView botView;

    @Test
    public void givenSuccessfulConnection_whenStartPresenting_thenBotViewOnConnectIsCalled() {
        when(tpService.connect()).thenReturn(Observable.just(SUCCESS_RESULT));

        BotPresenter presenter = new BotPresenter(tpService, botView);
        presenter.startPresenting();

        verify(botView).onConnect(SUCCESS_RESULT.message().get());
    }

    @Test
    public void givenUnsuccessfulConnection_whenStartPresenting_thenBotViewOnErrorIsCalled() {
        when(tpService.connect()).thenReturn(Observable.just(FAILURE_RESULT));

        BotPresenter presenter = new BotPresenter(tpService, botView);
        presenter.startPresenting();

        verify(botView).onError(FAILURE_RESULT.exception().get().getMessage());
    }

    @Test
    public void givenAlreadyPresenting_whenStopPresentingIsCalled_thenTpServiceDisconnectIsCalled() {
        BotPresenter presenter = givenAlreadyPresenting();

        presenter.stopPresenting();

        verify(tpService).disconnect();
    }

    private BotPresenter givenAlreadyPresenting() {
        when(tpService.connect()).thenReturn(Observable.just(SUCCESS_RESULT));

        BotPresenter presenter = new BotPresenter(tpService, botView);
        presenter.startPresenting();

        return presenter;
    }

}
