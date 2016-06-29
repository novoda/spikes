package com.novoda.magicmirror.modules.weather;

import com.novoda.magicmirror.modules.DashboardModule;

class DisabledWeatherModule implements DashboardModule {

    private final WeatherModule.WeatherListener weatherListener;

    DisabledWeatherModule(WeatherModule.WeatherListener weatherListener) {
        this.weatherListener = weatherListener;
    }

    @Override
    public void update() {
        weatherListener.onCurrentWeatherUnavailable();
    }

    @Override
    public void stop() {
        // no-op
    }

}
