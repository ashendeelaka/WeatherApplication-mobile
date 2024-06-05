package com.example.e2145134_weather_app;

import java.util.List;

public class WeatherFeedback {
    public Coord coord;
    public List<Weather> weather;
    public Main main;
    public int visibility;
    public Wind wind;
    public Rain rain;
    public Clouds clouds;
    public Sys sys;

    public class Coord {
        public double lon;
        public double lat;
    }

    public class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public class Main {
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int pressure;
        public int humidity;
        public int sea_level;
        public int grnd_level;
    }

    public class Wind {
        public double speed;
        public int deg;
        public double gust;
    }

    public class Rain {
        public double _1h;
    }

    public class Clouds {
        public int all;
    }

    public class Sys {
        public int type;
        public int id;
        public String country;
        public long sunrise;
        public long sunset;
    }
}