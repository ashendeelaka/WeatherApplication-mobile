package com.example.e2145134_weather_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView latitudeLongitudeTextView;
    private TextView geoAddressTextView;
    private TextView currnetSystemTimeTextView;
    private TextView weatherMainTextView;
    private TextView weatherDescriptionTextView;
    private TextView temperatureTextView;

    private TextView humidityTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        latitudeLongitudeTextView = findViewById(R.id.latitude_longitude);
        geoAddressTextView = findViewById(R.id.geo_address);
        currnetSystemTimeTextView = findViewById(R.id.system_time);


        weatherDescriptionTextView = findViewById(R.id.weather_description);
        temperatureTextView = findViewById(R.id.temperature);
        humidityTextView = findViewById(R.id.humidity);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            getLastLocation();
        }

        displayCurrentTime();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            latitudeLongitudeTextView.setText(String.format(Locale.getDefault(), "Latitude: %.4f, Longitude: %.4f", latitude, longitude));
                            getAddressFromLocation(latitude, longitude);
                            fetchWeatherData(latitude, longitude);
                        }
                    }
                });
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                geoAddressTextView.setText(address.getAddressLine(0));
            } else {
                geoAddressTextView.setText("Address not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            geoAddressTextView.setText("Geocoder service not available");
        }
    }

    private void fetchWeatherData(double latitude, double longitude) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherServiceInterface service = retrofit.create(WeatherServiceInterface.class);
        Call<WeatherFeedback> call = service.getCurrentWeather(latitude, longitude, "6f8fba17399e750022253295bc38b541", "metric");

        call.enqueue(new Callback<WeatherFeedback>() {
            @Override
            public void onResponse(Call<WeatherFeedback> call, Response<WeatherFeedback> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherFeedback weatherFeedback = response.body();

                    if (weatherFeedback.weather != null && !weatherFeedback.weather.isEmpty()) {
                        WeatherFeedback.Weather weather = weatherFeedback.weather.get(0);

                        weatherDescriptionTextView.setText(weather.description);
                    }


                    weatherDescriptionTextView.setText(weatherFeedback.weather.get(0).description);
                    temperatureTextView.setText(String.format(Locale.getDefault(), "Temperature: %.2f°C", weatherFeedback.main.temp));

                    humidityTextView.setText(String.format(Locale.getDefault(), "Humidity: %d%%", weatherFeedback.main.humidity));

                }
            }

            @Override
            public void onFailure(Call<WeatherFeedback> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void displayCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        currnetSystemTimeTextView.setText(currentTime);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}