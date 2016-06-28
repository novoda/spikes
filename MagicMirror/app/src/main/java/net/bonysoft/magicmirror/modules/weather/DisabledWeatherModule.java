package net.bonysoft.magicmirror.modules.weather;

import net.bonysoft.magicmirror.modules.DashboardModule;

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
