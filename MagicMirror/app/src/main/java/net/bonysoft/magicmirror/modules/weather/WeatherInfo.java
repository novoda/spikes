package net.bonysoft.magicmirror.modules.weather;

public class WeatherInfo {

    private final float temperature;
    private final float minTemperature;
    private final float maxTemperature;
    private final String condition;
    private final String iconId;

    public WeatherInfo(float temperature, float minTemperature, float maxTemperature, String condition, String iconId) {
        this.temperature = temperature;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.condition = condition;
        this.iconId = iconId;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public String getCondition() {
        return condition;
    }

    public String getIconId() {
        return iconId;
    }
}
