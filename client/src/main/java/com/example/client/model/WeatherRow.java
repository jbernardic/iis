package com.example.client.model;

/** One row of the gRPC weather result (Part 4). */
public class WeatherRow {

    private final String city;
    private final String temperature;
    private final String humidity;
    private final String pressure;
    private final String wind;
    private final String weather;

    public WeatherRow(String city, String temperature, String humidity, String pressure,
                      String wind, String weather) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.wind = wind;
        this.weather = weather;
    }

    public String getCity() {
        return city;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public String getWind() {
        return wind;
    }

    public String getWeather() {
        return weather;
    }
}
