package com.novoda.easycustomtabs.connection;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EasyCustomTabsConnectionTest {

    @Mock
    private Binder mockBinder;
    @Mock
    private Activity mockActivity;
    @Mock
    private ConnectedClient mockConnectedClient;

    private EasyCustomTabsConnection easyCustomTabsConnection;

    @Before
    public void setUp() {
        initMocks(this);

        easyCustomTabsConnection = new EasyCustomTabsConnection(mockBinder);
    }

    @Test
    public void connectToBindsActivityToService() {
        easyCustomTabsConnection.connectTo(mockActivity);

        verify(mockBinder).bindCustomTabsServiceTo(mockActivity);
    }

    @Test
    public void disconnectFromUnbindsActivityFromService() {
        easyCustomTabsConnection.disconnectFrom(mockActivity);

        verify(mockBinder).unbindCustomTabsService(mockActivity);
    }

    @Test
    public void warmsUpConnectedClientOnServiceConnected() {
        givenAConnectedClient();

        easyCustomTabsConnection.onServiceConnected(mockConnectedClient);

        verify(mockConnectedClient).warmup();
    }

    @Test
    public void doesNotWarmUpDisconnectedClientOnServiceConnected() {
        givenADisconnectedClient();

        easyCustomTabsConnection.onServiceConnected(mockConnectedClient);

        verify(mockConnectedClient, never()).warmup();
    }

    @Test
    public void createsNewSessionWhenClientIsStillConnected() {
        givenAConnectedClient();

        easyCustomTabsConnection.onServiceConnected(mockConnectedClient);
        easyCustomTabsConnection.newSession();

        verify(mockConnectedClient).newSession();
    }

    @Test
    public void doesNotCreateNewSessionWhenClientIsDisconnected() {
        givenADisconnectedClient();

        easyCustomTabsConnection.onServiceConnected(mockConnectedClient);
        assertThat(easyCustomTabsConnection.newSession()).isNull();

        verify(mockConnectedClient, never()).newSession();
    }

    @Test
    public void disconnectsConnectedClientOnServiceDisconnected() {
        givenAConnectedClient();

        easyCustomTabsConnection.onServiceConnected(mockConnectedClient);
        easyCustomTabsConnection.onServiceDisconnected();

        verify(mockConnectedClient).disconnect();
    }

    @Test
    public void doesNotDisconnectDisconnectedConnectedClientOnServiceDisconnected() {
        givenADisconnectedClient();

        easyCustomTabsConnection.onServiceConnected(mockConnectedClient);
        easyCustomTabsConnection.onServiceDisconnected();

        verify(mockConnectedClient, never()).disconnect();
    }

    private void givenAConnectedClient() {
        when(mockConnectedClient.stillConnected()).thenReturn(true);
    }

    private void givenADisconnectedClient() {
        when(mockConnectedClient.stillConnected()).thenReturn(false);
    }

}
