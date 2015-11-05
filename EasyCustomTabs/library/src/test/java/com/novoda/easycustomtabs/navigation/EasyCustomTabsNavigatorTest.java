package com.novoda.easycustomtabs.navigation;

import android.app.Activity;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import com.novoda.easycustomtabs.EasyCustomTabs;
import com.novoda.easycustomtabs.connection.Connection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class EasyCustomTabsNavigatorTest {

    private static final Uri ANY_URL = Uri.EMPTY;
    private static final CustomTabsIntent ANY_INTENT = new CustomTabsIntent.Builder().build();

    @Mock
    private Connection mockConnection;
    @Mock
    private IntentCustomizer mockIntentCustomizer;
    @Mock
    private NavigationFallback mockNavigationFallback;
    @Mock
    private Activity mockActivity;
    @Mock
    private EasyCustomTabsIntentBuilder mockEasyCustomTabsIntentBuilder;

    private WebNavigator webNavigator;

    @Before
    public void setUp() {
        initMocks(this);

        EasyCustomTabs.initialize(Robolectric.application);
        when(mockIntentCustomizer.onCustomiseIntent(any(EasyCustomTabsIntentBuilder.class))).thenReturn(mockEasyCustomTabsIntentBuilder);
        when(mockEasyCustomTabsIntentBuilder.createIntent()).thenReturn(ANY_INTENT);

        webNavigator = new EasyCustomTabsWebNavigator(mockConnection);
    }

    @Test
    public void navigateToWillFallbackIfHasFallbackAndNotConnected() {
        when(mockConnection.isConnected()).thenReturn(false);
        webNavigator.withFallback(mockNavigationFallback);

        webNavigator.navigateTo(ANY_URL, mockActivity);

        verify(mockNavigationFallback).onFallbackNavigateTo(ANY_URL);
    }

    @Test
    public void navigateToDoesNothingIfHasNotFallbackAndNotConnected() {
        when(mockConnection.isConnected()).thenReturn(false);

        webNavigator.navigateTo(ANY_URL, mockActivity);

        verifyZeroInteractions(mockNavigationFallback);
    }

    @Test
    public void intentBuilderIsCustomizedIfConnectedAndHasCustomizer() {
        when(mockConnection.isConnected()).thenReturn(true);
        webNavigator.withIntentCustomizer(mockIntentCustomizer);

        webNavigator.navigateTo(ANY_URL, mockActivity);

        verify(mockIntentCustomizer).onCustomiseIntent(any(EasyCustomTabsIntentBuilder.class));
    }

}
