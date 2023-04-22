package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class DetailedForecast extends AppCompatActivity {
    TextView avgTempView, maxMinView, humidityView, precipView, windSpeedView, rainChanceView, sunriseView, cityNameView, forecastDayView;
    String avgTemp , maxTemp, minTemp, humidity, precip, rainChance, sunrise, cityName, windspeed, forecastDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_forecast);
        avgTempView = findViewById(R.id.averageTempView);
        maxMinView = findViewById(R.id.maxMinView);
        humidityView = findViewById(R.id.humidityView);
        precipView = findViewById(R.id.precipView);
        rainChanceView = findViewById(R.id.rainChanceView);
        sunriseView = findViewById(R.id.sunriseView);
        cityNameView = findViewById(R.id.cityNameView);
        forecastDayView= findViewById(R.id.forecastDayView);
        windSpeedView = findViewById(R.id.windSpeedView);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String tempUnitPref = pref.getString("selectedTempUnitName", "C");
        String precipUnitPref = pref.getString("selectedPrecipUnitName", "Inches(in)");
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        forecastDay = b.getString("EXTRA_DAY");
        if(forecastDay.equals("Tomorrow")){
            cityName = b.getString("EXTRA_CITY");
            if(tempUnitPref.equals("C")){
                avgTemp = b.getString("EXTRA_TOMTEMP");
                maxTemp = b.getString("EXTRA_MAXTOMTEMP");
                minTemp = b.getString("EXTRA_MINTOMTEMP");
                avgTempView.setText(avgTemp + "°C");
                maxMinView.setText(maxTemp + "°C↑/" + minTemp + "°C↓");
            }else{
                avgTemp = b.getString("EXTRA_TOMTEMPF");
                maxTemp = b.getString("EXTRA_MAXTOMTEMPF");
                minTemp = b.getString("EXTRA_MINTOMTEMPF");
                avgTempView.setText(avgTemp + "°F");
                maxMinView.setText(maxTemp + "°F↑/" + minTemp + "°F↓");
            }
            if(precipUnitPref.equals("Inches(in)")){
                precip = b.getString("EXTRA_PRECIPTOM");
                precipView.setText(precip + "in");
            }else{
                precip = b.getString("EXTRA_PRECIPTOMMM");
                precipView.setText(precip + "mm");
            }
            humidity = b.getString("EXTRA_HUMIDITYTOM");
            windspeed = b.getString("EXTRA_WINDSPEEDTOM");
            rainChance = b.getString("EXTRA_RAINCHANCETOM");
            sunrise = b.getString("EXTRA_SUNRISETOM");

        }
        else{
            cityName = b.getString("EXTRA_CITY");
            if(tempUnitPref.equals("C")){
                avgTemp = b.getString("EXTRA_AFTTEMP");
                maxTemp = b.getString("EXTRA_MAXAFTTEMP");
                minTemp = b.getString("EXTRA_MINAFTTEMP");
                avgTempView.setText(avgTemp + "°C");
                maxMinView.setText(maxTemp + "°C↑/" + minTemp + "°C↓");
            }else{
                avgTemp = b.getString("EXTRA_AFTTEMPF");
                maxTemp = b.getString("EXTRA_MAXAFTEMPF");
                minTemp = b.getString("EXTRA_MINAFTTEMPF");
                avgTempView.setText(avgTemp + "°F");
                maxMinView.setText(maxTemp + "°F↑/" + minTemp + "°F↓");
            }
            if(precipUnitPref.equals("Inches(in)")){
                precip = b.getString("EXTRA_PRECIPAFT");
                precipView.setText(precip + "in");
            }else{
                precip = b.getString("EXTRA_PRECIPAFTMM");
                precipView.setText(precip + "mm");
            }
            humidity = b.getString("EXTRA_HUMIDITYAFT");
            windspeed = b.getString("EXTRA_WINDSPEEDAFT");
            rainChance = b.getString("EXTRA_RAINCHANCEAFT");
            sunrise = b.getString("EXTRA_SUNRISEAFT");
        }
        cityNameView.setText(cityName);
        forecastDayView.setText(forecastDay);
        humidityView.setText(humidity +"%");
        windSpeedView.setText(windspeed + "mph");
        rainChanceView.setText(rainChance);
        sunriseView.setText(sunrise);



    }
}