package com.novoda.wallpaper.droidcon;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.novoda.wallpaper.droidcon.TimeOfDay.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeOfDayAssetRefresherTest {

    private static final Context ANY_CONTEXT = null;
    private static final Intent ANY_INTENT = null;

    private TimeOfDayAssetRefresher refresher;

    @Mock
    private LondonParallaxWallpaperRenderer mockRender;
    @Mock
    private TimeOfDayCalculator mockTimeCalculator;

    @Before
    public void setUp() {
        refresher = new TimeOfDayAssetRefresher(mockRender, mockTimeCalculator);
    }

    @Test
    public void givenItsDawn_whenReceivingBroadcast_thenDawnAssetsAreLoaded() {
        givenItIs(DAWN);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender).loadAssetsFor(DAWN);
    }

    @Test
    public void givenItsDawn_whenReceivingMultipleBroadcasts_thenDawnAssetsAreLoadedOnlyOnce() {
        givenItIs(DAWN);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);
        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender, times(1)).loadAssetsFor(DAWN);
    }

    @Test
    public void givenItsDay_whenReceivingBroadcast_thenDayAssetsAreLoaded() {
        givenItIs(DAY);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender).loadAssetsFor(DAY);
    }

    @Test
    public void givenItsDay_whenReceivingMultipleBroadcasts_thenDayAssetsAreLoadedOnlyOnce() {
        givenItIs(DAY);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);
        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender, times(1)).loadAssetsFor(DAY);
    }

    @Test
    public void givenItsDusk_whenReceivingBroadcast_thenDuskAssetsAreLoaded() {
        givenItIs(DUSK);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender).loadAssetsFor(DUSK);
    }

    @Test
    public void givenItsDusk_whenReceivingMultipleBroadcasts_thenDuskAssetsAreLoadedOnlyOnce() {
        givenItIs(DUSK);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);
        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender, times(1)).loadAssetsFor(DUSK);
    }

    @Test
    public void givenItsNight_whenReceivingBroadcast_thenNightAssetsAreLoaded() {
        givenItIs(NIGHT);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender).loadAssetsFor(NIGHT);
    }

    @Test
    public void givenItsNight_whenReceivingMultipleBroadcasts_thenNightAssetsAreLoadedOnlyOnce() {
        givenItIs(NIGHT);

        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);
        refresher.onReceive(ANY_CONTEXT, ANY_INTENT);

        verify(mockRender, times(1)).loadAssetsFor(NIGHT);
    }

    private void givenItIs(TimeOfDay timeOfDay) {
        when(mockTimeCalculator.currentTimeOfDay()).thenReturn(timeOfDay);
    }
}
