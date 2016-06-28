package net.bonysoft.magicmirror.modules.weather;

import android.content.Context;
import android.support.annotation.NonNull;

import com.novoda.notils.logger.simple.Log;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.exception.WeatherProviderInstantiationException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.DayForecast;
import com.survivingwithandroid.weather.lib.model.Weather;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.concurrent.TimeUnit;

import net.bonysoft.magicmirror.BuildConfig;
import net.bonysoft.magicmirror.modules.DashboardModule;

public class WeatherModule implements DashboardModule {

    private static final long MIN_UPDATE_DELAY = TimeUnit.MINUTES.toMillis(10);
    private static final String API_KEY = BuildConfig.OPENWEATHERMAP_API_KEY;

    private static final double LIVERPOOL_LONGITUDE = -2.9982376;
    private static final double LIVERPOOL_LATITUDE = 53.4097205;

    private final WeatherClient client;
    private final WeatherListener weatherListener;
    private long lastUpdate = 0;

    public static DashboardModule newInstance(Context context, WeatherListener weatherListener) {
        WeatherClient.ClientBuilder builder = new WeatherClient.ClientBuilder();
        WeatherConfig config = buildWeatherConfig();

        try {
            WeatherClient client = builder.attach(context)
                    .provider(new OpenweathermapProviderType())
                    .httpClient(com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient.class)
                    .config(config)
                    .build();

            return new WeatherModule(client, weatherListener);

        } catch (WeatherProviderInstantiationException e) {
            Log.e(e, "Error creating weather client");
            return new DisabledWeatherModule(weatherListener);
        }
    }

    @NonNull
    private static WeatherConfig buildWeatherConfig() {
        WeatherConfig config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        config.lang = "en";
        config.maxResult = 5;
        config.numDays = 6;
        config.ApiKey = API_KEY;
        return config;
    }

    public WeatherModule(WeatherClient client, WeatherListener weatherListener) {
        this.client = client;
        this.weatherListener = weatherListener;
    }

    @Override
    public void update() {
        if (tooCloseToLastUpdate()) {
            return;
        }
        lastUpdate = System.currentTimeMillis();

        // TODO: get current devicePosition
        double longitude = LIVERPOOL_LONGITUDE;
        double latitude = LIVERPOOL_LATITUDE;
        WeatherRequest request = new WeatherRequest(longitude, latitude);

        client.getCurrentCondition(request, new CurrentWeatherListener(weatherListener));
        client.getForecastWeather(request, new TodayForecastListener(weatherListener));
    }

    private boolean tooCloseToLastUpdate() {
        long now = System.currentTimeMillis();
        return now - lastUpdate < MIN_UPDATE_DELAY;
    }

    private static class CurrentWeatherListener implements WeatherClient.WeatherEventListener {

        private final WeatherListener weatherListener;

        private CurrentWeatherListener(WeatherListener weatherListener) {
            this.weatherListener = weatherListener;
        }

        @Override
        public void onWeatherRetrieved(CurrentWeather weather) {
            Weather.Temperature temperature = weather.weather.temperature;
            WeatherInfo currentWeather = new WeatherInfo(
                    temperature.getTemp(),
                    temperature.getMinTemp(),
                    temperature.getMaxTemp(),
                    weather.weather.currentCondition.getCondition(),
                    weather.weather.currentCondition.getIcon());
            weatherListener.onCurrentWeatherFetched(currentWeather);
        }

        @Override
        public void onWeatherError(WeatherLibException e) {
            Log.e(e, "Error while fetching weather conditions");
            weatherListener.onCurrentWeatherUnavailable();
        }

        @Override
        public void onConnectionError(Throwable e) {
            Log.e(e, "Connection error while fetching weather conditions");
            weatherListener.onCurrentWeatherUnavailable();
        }
    }

    private static class TodayForecastListener implements WeatherClient.ForecastWeatherEventListener {

        private final WeatherListener weatherListener;

        private TodayForecastListener(WeatherListener weatherListener) {
            this.weatherListener = weatherListener;
        }

        @Override
        public void onWeatherRetrieved(WeatherForecast forecast) {
            DayForecast todayForecast = forecast.getForecast(0);

            WeatherInfo todayWeather = new WeatherInfo(
                    todayForecast.forecastTemp.day,
                    todayForecast.forecastTemp.min,
                    todayForecast.forecastTemp.max,
                    todayForecast.weather.currentCondition.getCondition(),
                    todayForecast.weather.currentCondition.getIcon()
            );
            weatherListener.onTodayForecastFetched(todayWeather);
        }

        @Override
        public void onWeatherError(WeatherLibException e) {
            Log.e(e, "Error while fetching weather forecast");
            weatherListener.onTodayForecastUnavailable();
        }

        @Override
        public void onConnectionError(Throwable e) {
            Log.e(e, "Error while fetching weather forecast");
            weatherListener.onTodayForecastUnavailable();
        }
    }

    @Override
    public void stop() {
        // no-op
    }

    public interface WeatherListener {

        void onCurrentWeatherFetched(WeatherInfo currentInfo);

        void onTodayForecastFetched(WeatherInfo todayInfo);

        void onCurrentWeatherUnavailable();

        void onTodayForecastUnavailable();

    }

}
