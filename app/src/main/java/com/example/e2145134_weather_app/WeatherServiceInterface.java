package com.example.e2145134_weather_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherServiceInterface {
    @GET("weather")
    Call<WeatherFeedback> getCurrentWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String appid, @Query("units") String units);
}

