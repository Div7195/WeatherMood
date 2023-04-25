package com.example.weatherapp;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView currentTemp, humid, precip, tomorrowTemp, dayAfterTemp, maxMinTemp, city, conditionView;
    ImageView imageToday, imageTomorrow, imageDayAfter, backgroundImage;
    ProgressBar loadingView;
    ImageView refreshButtonView;
    FusedLocationProviderClient fusedLocationProviderClient;
    MediaPlayer mp;
    String playSoundGlobal;
    private final static int REQUEST_CODE = 100;
    private final String apiUrl = "https://api.weatherapi.com/v1/forecast.json";
    private final String apiKey = "c818478c5cea4ddab5545407232504";
//     private final String apiKey = "9cabe1f6815145c98a2142339231304";
    private int resID1 = 0;
    private int resID2 = 0;
    private int resID3 = 0;
    private int resID4 = 0;
    private boolean backPressedOnce = false;
    String msgCityName, msgTemp, msgCondition, msgHumidty, msgPrecipitation, msgMaxTemp, msgMinTemp;
    String tommTemp, tommTempF, maxTommTemp, minTommTemp, maxTommTempF, minTommTempF, tommWindSpeed, tommHumidty, tommSunrise, tommPrecipIn, tommPrecipMm, tommRainChance, cityPass;
    String afterTemp, afterTempF, maxAfterTemp, minAfterTemp, maxAfterTempF, minAfterTempF, AfterWindSpeed, AfterHumidty, AfterSunrise, AfterPrecipIn, AfterPrecipMm, AfterRainChance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            ((Window) window).addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backPressedOnce = false;
        currentTemp = findViewById(R.id.textView_current_temp);
        humid = findViewById(R.id.textView_humidity);
        precip = findViewById(R.id.textView_precipitation);
        city = findViewById(R.id.textView_cityname);
        tomorrowTemp = findViewById(R.id.textView_tommorrow_temp);
        dayAfterTemp = findViewById(R.id.textView_dayAfter_temp);
        maxMinTemp = findViewById(R.id.textView_max_min_temp);
        imageToday = findViewById(R.id.imageView_current);
        imageTomorrow = findViewById(R.id.imageView_tomorrow);
        imageDayAfter = findViewById(R.id.imageView_dayAfter);
        conditionView = findViewById(R.id.condition);
        backgroundImage = findViewById(R.id.backImage);
        loadingView = findViewById(R.id.loadingProgressBar1);
        refreshButtonView = findViewById(R.id.refreshButton);
        loadingView.setVisibility(View.VISIBLE);
        final TypedArray imgs1 = getResources().obtainTypedArray(R.array.apptour1);
        final TypedArray imgs2 = getResources().obtainTypedArray(R.array.apptour2);
        final TypedArray imgs3 = getResources().obtainTypedArray(R.array.apptour3);
        final TypedArray imgs4 = getResources().obtainTypedArray(R.array.apptour4);

        final Random rand = new Random();
        final int rndInt1 = rand.nextInt(imgs1.length());
        resID1 = imgs1.getResourceId(rndInt1, 0);
        final int rndInt2 = rand.nextInt(imgs1.length());
        resID2 = imgs2.getResourceId(rndInt2, 0);
        final int rndInt3 = rand.nextInt(imgs1.length());
        resID3 = imgs3.getResourceId(rndInt3, 0);
        final int rndInt4 = rand.nextInt(imgs1.length());
        resID4 = imgs4.getResourceId(rndInt4, 0);
//        loadingView.setVisibility(View.GONE);
//        setTheme(R.style.LightTheme);
        refreshButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);

            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d("oncreatedebug", "onCreate: ");

        getLastLocation();
        imageTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation blinkanimation= new AlphaAnimation(1, 1/4); // Change alpha from fully visible to invisible
                blinkanimation.setDuration(300); // duration - half a second
                blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                blinkanimation.setRepeatCount(3); // Repeat animation infinitely
                blinkanimation.setRepeatMode(Animation.REVERSE);
                imageTomorrow.startAnimation(blinkanimation);
                Intent intentDetail = new Intent(MainActivity.this, DetailedForecast.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("EXTRA_DAY" , "Tomorrow");
                mBundle.putString("EXTRA_CITY" ,cityPass);
                mBundle.putString("EXTRA_TOMTEMP" , String.valueOf(tommTemp));
                mBundle.putString("EXTRA_TOMTEMPF"  , String.valueOf(tommTempF));
                mBundle.putString("EXTRA_MAXTOMTEMP"  , String.valueOf(maxTommTemp));
                mBundle.putString("EXTRA_MAXTOMTEMPF"  , String.valueOf(maxTommTempF));
                mBundle.putString("EXTRA_MINTOMTEMP"  , String.valueOf(minTommTemp));
                mBundle.putString("EXTRA_MINTOMTEMPF"  , String.valueOf(minTommTempF));
                mBundle.putString("EXTRA_HUMIDITYTOM"  , String.valueOf(tommHumidty));
                mBundle.putString("EXTRA_PRECIPTOM"  , String.valueOf(tommPrecipIn));
                mBundle.putString("EXTRA_PRECIPTOMMM"  , String.valueOf(tommPrecipMm));
                mBundle.putString("EXTRA_WINDSPEEDTOM"  , String.valueOf(tommWindSpeed));
                mBundle.putString("EXTRA_RAINCHANCETOM"  , String.valueOf(tommRainChance));
                mBundle.putString("EXTRA_SUNRISETOM"  , tommSunrise);
                intentDetail.putExtras(mBundle);
                startActivity(intentDetail);
            }
        });

        imageDayAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation blinkanimation= new AlphaAnimation(1, 1/4); // Change alpha from fully visible to invisible
                blinkanimation.setDuration(300); // duration - half a second
                blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                blinkanimation.setRepeatCount(3); // Repeat animation infinitely
                blinkanimation.setRepeatMode(Animation.REVERSE);
                imageDayAfter.startAnimation(blinkanimation);
                Intent intentDetail = new Intent(MainActivity.this, DetailedForecast.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("EXTRA_DAY" , "Day After Tomorrow");
                mBundle.putString("EXTRA_CITY" ,cityPass);
                mBundle.putString("EXTRA_AFTTEMP" , String.valueOf(afterTemp));
                mBundle.putString("EXTRA_AFTTEMPF"  , String.valueOf(afterTempF));
                mBundle.putString("EXTRA_MAXAFTTEMP"  , String.valueOf(maxAfterTemp));
                mBundle.putString("EXTRA_MAXAFTEMPF"  , String.valueOf(maxAfterTempF));
                mBundle.putString("EXTRA_MINAFTTEMP"  , String.valueOf(minAfterTemp));
                mBundle.putString("EXTRA_MINAFTTEMPF"  , String.valueOf(minAfterTempF));
                mBundle.putString("EXTRA_HUMIDITYAFT"  , String.valueOf(AfterHumidty));
                mBundle.putString("EXTRA_PRECIPAFT"  , String.valueOf(AfterPrecipIn));
                mBundle.putString("EXTRA_PRECIPAFTMM"  , String.valueOf(AfterPrecipMm));
                mBundle.putString("EXTRA_WINDSPEEDAFT"  , String.valueOf(AfterWindSpeed));
                mBundle.putString("EXTRA_RAINCHANCEAFT"  , String.valueOf(AfterRainChance));
                mBundle.putString("EXTRA_SUNRISEAFT"  , AfterSunrise);
                intentDetail.putExtras(mBundle);
                startActivity(intentDetail);

            }
        });


    }

    private void getLastLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

//                    Toast.makeText(MainActivity.this, "fdsfsad", Toast.LENGTH_SHORT).show();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            String tempUrl = "";
                            String currentLocation = addresses.get(0).getLocality() + " " + addresses.get(0).getCountryName();
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("currentLocation", currentLocation);
                            editor.apply();
                            String defaultLocation = pref.getString("defaultEnteredLocation", "");
                            String tempUnitPref = pref.getString("selectedTempUnitName", "C");
                            String precipUnitPref = pref.getString("selectedPrecipUnitName", "Inches(in)");
                            String soundPref = pref.getString("selectedSoundPref", "Yes");
                            playSoundGlobal = soundPref;
                            Log.d("locERR", currentLocation);
                            if (currentLocation.equals("")) {
                                Toast.makeText(MainActivity.this, "City field cannot be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                if (defaultLocation.equals("")) {
                                    Log.d("network problem", currentLocation);
                                    tempUrl = apiUrl + "?key=" + apiKey + "&q=" + currentLocation + "&days=" + "3" + "&aqi=" + "yes" + "&alerts=" + "yes";
                                } else {
                                    Log.d("network pro", defaultLocation);
                                    tempUrl = apiUrl + "?key=" + apiKey + "&q=" + defaultLocation + "&days=" + "3" + "&aqi=" + "yes" + "&alerts=" + "yes";
                                }

                            }


                            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    loadingView.setVisibility(View.GONE);
                                    Log.d("response", response);
                                    String output = "";
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        JSONObject jsonObjectLocation = jsonResponse.getJSONObject("location");
                                        String cityName = jsonObjectLocation.getString("name");
                                        String countryName = jsonObjectLocation.getString("country");
                                        String localTime = jsonObjectLocation.getString("localtime");
                                        int localTimeInt = 0;
                                        Log.d("localtime", String.valueOf(String.valueOf(localTime.charAt(11)) + String.valueOf(localTime.charAt(12))));
                                        if (String.valueOf(localTime.charAt(12)).equals(":")) {
                                            localTime = String.valueOf(localTime.charAt(11));
                                        } else {
                                            localTime = String.valueOf(localTime.charAt(11)) + String.valueOf(localTime.charAt(12));
                                        }
                                        localTimeInt = Integer.valueOf(localTime);
                                        JSONObject jsonObjectCurrent = jsonResponse.getJSONObject("current");
                                        double temp_celcius = jsonObjectCurrent.getDouble("temp_c");
                                        double temp_farhen = jsonObjectCurrent.getDouble("temp_f");
                                        double windSpeed = jsonObjectCurrent.getDouble("wind_kph");
                                        double precipitation = jsonObjectCurrent.getDouble("precip_in");
                                        int humidity = jsonObjectCurrent.getInt("humidity");

                                        JSONObject jsonObjectCondition0 = jsonObjectCurrent.getJSONObject("condition");
                                        String condition0 = jsonObjectCondition0.getString("text");

                                        JSONObject jsonObjectForecast = jsonResponse.getJSONObject("forecast");
                                        JSONArray jsonForecastArray = jsonObjectForecast.getJSONArray("forecastday");

                                        JSONObject jsonForecastObject0 = jsonForecastArray.getJSONObject(0);
                                        JSONObject jsonForecastDayDetailObject0 = jsonForecastObject0.getJSONObject("day");

                                        double maxTemp_c = jsonForecastDayDetailObject0.getDouble("maxtemp_c");
                                        double maxTemp_f = jsonForecastDayDetailObject0.getDouble("maxtemp_f");
                                        double minTemp_c = jsonForecastDayDetailObject0.getDouble("mintemp_c");
                                        double minTemp_f = jsonForecastDayDetailObject0.getDouble("mintemp_f");

                                        JSONObject jsonForecastObject1 = jsonForecastArray.getJSONObject(1);
                                        JSONObject jsonForecastDayDetailObject1 = jsonForecastObject1.getJSONObject("day");
                                        JSONObject jsonForecastDayDetailObjectSunrise1 = jsonForecastObject1.getJSONObject("astro");
                                        String sunriseTomm = jsonForecastDayDetailObjectSunrise1.getString("sunrise");
                                        double firstDayTemp_c = jsonForecastDayDetailObject1.getDouble("avgtemp_c");
                                        double firstDayTemp_f = jsonForecastDayDetailObject1.getDouble("avgtemp_f");
//                            ###############################################################################################
                                        double max_c_Tom = jsonForecastDayDetailObject1.getDouble("maxtemp_c");
                                        double min_c_Tom = jsonForecastDayDetailObject1.getDouble("mintemp_c");
                                        double max_f_Tom = jsonForecastDayDetailObject1.getDouble("maxtemp_f");
                                        double min_f_Tom = jsonForecastDayDetailObject1.getDouble("mintemp_f");
                                        double humidityTomm = jsonForecastDayDetailObject1.getDouble("avghumidity");
                                        double windspeedTomm = jsonForecastDayDetailObject1.getDouble("maxwind_mph");
                                        double precipTommIn = jsonForecastDayDetailObject1.getDouble("totalprecip_in");
                                        double precipTommMm = jsonForecastDayDetailObject1.getDouble("totalprecip_mm");
                                        double rainChanceTomm = jsonForecastDayDetailObject1.getDouble("daily_will_it_rain");
//                            ####################################################################################################

                                        JSONObject jsonObjectCondition1 = jsonForecastDayDetailObject1.getJSONObject("condition");
                                        String condition1 = jsonObjectCondition1.getString("text");

                                        JSONObject jsonForecastObject2 = jsonForecastArray.getJSONObject(2);
                                        JSONObject jsonForecastDayDetailObject2 = jsonForecastObject2.getJSONObject("day");
                                        JSONObject jsonForecastDayDetailObjectSunrise2 = jsonForecastObject2.getJSONObject("astro");
                                        String sunriseDayAfter= jsonForecastDayDetailObjectSunrise2.getString("sunrise");
                                        double secondDayTemp_c = jsonForecastDayDetailObject2.getDouble("avgtemp_c");
                                        double secondDayTemp_f = jsonForecastDayDetailObject2.getDouble("avgtemp_f");
//                                        ###############################################################################################
                                        double max_c_After = jsonForecastDayDetailObject2.getDouble("maxtemp_c");
                                        double min_c_After = jsonForecastDayDetailObject2.getDouble("mintemp_c");
                                        double max_f_After = jsonForecastDayDetailObject2.getDouble("maxtemp_f");
                                        double min_f_After = jsonForecastDayDetailObject2.getDouble("mintemp_f");
                                        double humidityAfter = jsonForecastDayDetailObject2.getDouble("avghumidity");
                                        double windspeedAfter = jsonForecastDayDetailObject2.getDouble("maxwind_mph");
                                        double precipAfterIn = jsonForecastDayDetailObject2.getDouble("totalprecip_in");
                                        double precipAfterMm = jsonForecastDayDetailObject2.getDouble("totalprecip_mm");
                                        double rainChanceAfter = jsonForecastDayDetailObject2.getDouble("daily_will_it_rain");
//                            ####################################################################################################
                                        JSONObject jsonObjectCondition2 = jsonForecastDayDetailObject2.getJSONObject("condition");
                                        String condition2 = jsonObjectCondition2.getString("text");
                                        loadingView.setVisibility(View.GONE);
                                        if (tempUnitPref.equals("F")) {
                                            currentTemp.setText(String.valueOf(temp_farhen) + "°F");
                                            tomorrowTemp.setText(String.valueOf(firstDayTemp_f) + "°F");
                                            dayAfterTemp.setText(String.valueOf(secondDayTemp_f) + "°F");
                                            maxMinTemp.setText("Max " + String.valueOf(maxTemp_f) + "°F↑ • Min " + String.valueOf(minTemp_f) + "°F↓");
                                        } else {
                                            currentTemp.setText(String.valueOf(temp_celcius) + "°C");
                                            tomorrowTemp.setText(String.valueOf(firstDayTemp_c) + "°C");
                                            dayAfterTemp.setText(String.valueOf(secondDayTemp_c) + "°C");
                                            maxMinTemp.setText("Max " + String.valueOf(maxTemp_c) + "°C↑ • Min " + String.valueOf(minTemp_c) + "°C↓");
                                        }
                                        if (precipUnitPref.equals("Millimeters(mm)")) {
                                            precip.setText("Precipitation: " + String.valueOf(precipitation) + "mm");

                                        } else {
                                            precip.setText("Precipitation: " + String.valueOf(precipitation) + "in");
                                        }
                                        humid.setText("Humidity: " + String.valueOf(humidity) + "%");
                                        city.setText(cityName + ", " + countryName);
                                        if(condition0.equals("Patchy light rain with thunder")){
                                            conditionView.setText("Thunder");
                                        }else {
                                            conditionView.setText(condition0);
                                        }
                                        if (condition0.equals("Mist")) {
                                            imageToday.setImageResource(R.drawable.mist);
                                            playSound(R.raw.cloudysound);

                                        } else if (condition0.contains("rain") || condition0.contains("Rain")) {
                                            imageToday.setImageResource(R.drawable.rain);
                                            playSound(R.raw.thunder);

                                        } else if (condition0.contains("cloud") || condition0.contains("Cloud")  ) {
                                            conditionView.setText("Cloudy");
                                            imageToday.setImageResource(R.drawable.cloud);
                                            playSound(R.raw.cloudysound);
                                        } else if (condition0.equals("Clear")) {
                                            imageToday.setImageResource(R.drawable.clear);
                                            playSound(R.raw.clearsound);
                                        } else if (condition0.equals("Overcast")) {
                                            imageToday.setImageResource(R.drawable.overcast);
                                            playSound(R.raw.mistsound);
                                        } else {
                                            imageToday.setImageResource(R.drawable.sunny);
                                            playSound(R.raw.sunnysound);
                                        }
                                        if (condition1.equals("Mist")) {
                                            imageTomorrow.setImageResource(R.drawable.mist);
                                        } else if (condition1.contains("rain") || condition1.contains("Rain")) {
                                            imageTomorrow.setImageResource(R.drawable.rain);
                                        } else if (condition1.equals("Clear")) {
                                            imageTomorrow.setImageResource(R.drawable.clear);
                                        } else if (condition1.contains("cloud") || condition1.contains("Cloud") ) {
                                            imageTomorrow.setImageResource(R.drawable.cloud);
                                            playSound(R.raw.thunder);
                                        }else if (condition1.equals("Overcast")) {
                                            imageTomorrow.setImageResource(R.drawable.overcast);
                                        } else {
                                            imageTomorrow.setImageResource(R.drawable.sunny);
                                        }
                                        if (condition2.equals("Mist")) {
                                            imageDayAfter.setImageResource(R.drawable.mist);
//                                            imageDayAfter.setImageResource(R.drawable.rain);
                                        } else if (condition2.equals("Clear")) {
                                            imageDayAfter.setImageResource(R.drawable.clear);
                                        } else if (condition2.contains("cloud") || condition2.contains("Cloud") ) {
                                            imageDayAfter.setImageResource(R.drawable.cloud);
                                        }else if (condition2.equals("Overcast")) {
                                            imageDayAfter.setImageResource(R.drawable.overcast);
                                        } else {
                                            imageDayAfter.setImageResource(R.drawable.sunny);
                                        }
                                        //*******************
                                        if (localTimeInt >= 5 && localTimeInt < 9) {
                                            backgroundImage.setImageResource(resID1);
                                        } else if (localTimeInt >= 9 && localTimeInt < 16) {
                                            backgroundImage.setImageResource(resID2);
                                        } else if (localTimeInt >= 16 && localTimeInt < 19) {
                                            backgroundImage.setImageResource(resID3);
                                        } else if ((localTimeInt >= 19 && localTimeInt <= 23) || localTimeInt < 5) {
                                            backgroundImage.setImageResource(resID4);
                                        }
                                        cityPass = cityName;

                                        tommTemp = String.valueOf(firstDayTemp_c);
                                        tommTempF = String.valueOf(firstDayTemp_f);
                                        maxTommTemp= String.valueOf(max_c_Tom);
                                        maxTommTempF = String.valueOf(max_f_Tom);
                                        minTommTemp = String.valueOf(min_c_Tom);
                                        minTommTempF = String.valueOf(min_f_Tom);
                                        tommHumidty = String.valueOf(humidityTomm);
                                        tommWindSpeed = String.valueOf(windspeedTomm);
                                        tommPrecipIn = String.valueOf(precipTommIn);
                                        tommPrecipMm = String.valueOf(precipTommMm);
                                        tommSunrise = sunriseTomm;
                                        tommRainChance = String.valueOf(rainChanceTomm);

                                        afterTemp = String.valueOf(secondDayTemp_c);
                                        afterTempF = String.valueOf(secondDayTemp_f);
                                        maxAfterTemp = String.valueOf(max_c_After);
                                        maxAfterTempF = String.valueOf(max_f_After);
                                        minAfterTemp = String.valueOf(min_c_After);
                                        minAfterTempF = String.valueOf(min_f_After);
                                        AfterHumidty = String.valueOf(humidityAfter);
                                        AfterPrecipIn = String.valueOf(precipAfterIn);
                                        AfterPrecipMm = String.valueOf(precipAfterMm);
                                        AfterWindSpeed = String.valueOf(windspeedAfter);
                                        AfterSunrise = sunriseDayAfter;
                                        AfterRainChance = String.valueOf(rainChanceAfter);

                                        msgCityName = String.valueOf(cityName + ", "+countryName);
                                        msgCondition = String.valueOf(condition0);
                                        msgHumidty = String.valueOf(humidity);
                                        msgPrecipitation = String.valueOf(precipitation);
                                        msgMaxTemp = String.valueOf(maxTemp_c);
                                        msgMinTemp = String.valueOf(minTemp_c);
                                        msgTemp = String.valueOf(temp_celcius);



                                        //*******************

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "No matching default location found", Toast.LENGTH_SHORT).show();
                                    loadingView.setVisibility(View.GONE);
                                }
                            });
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        Toast.makeText(MainActivity.this, "Location is turned off. Turn on location and refresh/restart app", Toast.LENGTH_LONG).show();
                        LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

//
                    }
                }

            });
//            fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    askPermission();
//                }
//            });
        }else{
            askPermission();

        }
    }


    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else{
                Toast.makeText(this, "Location Permission Required", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    double currenttemp,currenttempf,maxtemp, maxtempf, mintemp, mintempf, precipin,precipmm, tomorrowtemp, tomorrowtempf, dayaftertemp, dayaftertempf;
                    int humidity, time;
                    String cityName, countryName, conditionday0, conditionday1, conditionday2;
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                    String soundPref = pref.getString("selectedSoundPref", "Yes");
                    playSoundGlobal = soundPref;
//                    String defaultLocation = pref.getString("defaultEnteredLocation", "");
                    String tempUnitPref = pref.getString("selectedTempUnitName", "C");
                    String precipUnitPref = pref.getString("selectedPrecipUnitName", "Inches(in)");

                    if (result.getResultCode() == 123) {
                        Bundle extras = result.getData().getExtras();

                        if (extras == null) {
                            currenttemp = 0;
                            currenttempf = 0;
                            mintemp = 0;
                            maxtemp = 0;
                            mintempf = 0;
                            maxtempf = 0;
                            humidity = 0;
                            precipin = 0;
                            precipmm = 0;
                            tomorrowtemp = 0;
                            tomorrowtempf = 0;
                            dayaftertemp = 0;
                            dayaftertempf = 0;
                            time = 0;
                        } else {

                            currenttemp = extras.getDouble("EXTRA_CURRENTTEMP");
                            currenttempf = extras.getDouble("EXTRA_CURRENTTEMPF");
                            maxtemp = extras.getDouble("EXTRA_MAXTEMP");
                            maxtempf = extras.getDouble("EXTRA_MAXTEMPF");
                            mintemp = extras.getDouble("EXTRA_MINTEMP");
                            mintempf = extras.getDouble("EXTRA_MINTEMPF");
//                        Log.d("NOT WORKINGFDFDSF","fuc");
                            humidity = extras.getInt("EXTRA_HUMIDITY");
                            precipin = extras.getDouble("EXTRA_PRECIP");
                            precipmm = extras.getDouble("EXTRA_PRECIPMM");
                            tomorrowtemp = extras.getDouble("EXTRA_TOMORROWTEMP");
                            tomorrowtempf = extras.getDouble("EXTRA_TOMORROWTEMPF");
                            dayaftertemp = extras.getDouble("EXTRA_DAYAFTERTEMP");
                            dayaftertempf = extras.getDouble("EXTRA_DAYAFTERTEMPF");
                            cityName = extras.getString("EXTRA_CITY");
                            countryName = extras.getString("EXTRA_COUNTRY");
                            conditionday0 = extras.getString("EXTRA_CONDITION0");
                            conditionday1 = extras.getString("EXTRA_CONDITION1");
                            conditionday2 = extras.getString("EXTRA_CONDITION2");
                            time = extras.getInt("EXTRA_TIME");
//                            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                            cityPass = extras.getString("EXTRA_CITY");
                            tommTemp = extras.getString("WEXTRA_TOMTEMP");
                            tommTempF = extras.getString("WEXTRA_TOMTEMPF");
                            maxTommTemp= extras.getString("WEXTRA_MAXTOMTEMP");
                            maxTommTempF = extras.getString("WEXTRA_MAXTOMTEMPF");
                            minTommTemp = extras.getString("WEXTRA_MINTOMTEMP");
                            minTommTempF = extras.getString("WEXTRA_MINTOMTEMPF");
                            tommHumidty = extras.getString("WEXTRA_HUMIDITYTOM");
                            tommWindSpeed = extras.getString("WEXTRA_WINDSPEEDTOM");
                            tommPrecipIn = extras.getString("WEXTRA_PRECIPTOM");
                            tommPrecipMm = extras.getString("WEXTRA_PRECIPTOMMM");
                            tommSunrise = extras.getString("WEXTRA_SUNRISETOM");
                            tommRainChance = extras.getString("WEXTRA_RAINCHANCETOM");

                            afterTemp = extras.getString("WEXTRA_AFTTEMP");
                            afterTempF = extras.getString("WEXTRA_AFTTEMPF");
                            maxAfterTemp = extras.getString("WEXTRA_MAXAFTTEMP");
                            maxAfterTempF = extras.getString("WEXTRA_MAXAFTEMPF");
                            minAfterTemp = extras.getString("WEXTRA_MINAFTTEMP");
                            minAfterTempF = extras.getString("WEXTRA_MINAFTTEMPF");
                            AfterHumidty = extras.getString("WEXTRA_HUMIDITYAFT");
                            AfterPrecipIn = extras.getString("WEXTRA_PRECIPAFT");
                            AfterPrecipMm = extras.getString("WEXTRA_PRECIPAFTMM");
                            AfterWindSpeed = extras.getString("WEXTRA_WINDSPEEDAFT");
                            AfterSunrise = extras.getString("WEXTRA_SUNRISEAFT");
                            AfterRainChance = extras.getString("WEXTRA_RAINCHANCEAFT");

                            msgCityName = String.valueOf(cityName + ", "+countryName);
                            msgCondition = String.valueOf(conditionday0);
                            msgHumidty = String.valueOf(humidity);
                            msgPrecipitation = String.valueOf(precipin);
                            msgMaxTemp = String.valueOf(maxtemp);
                            msgMinTemp = String.valueOf(mintemp);
                            msgTemp = String.valueOf(currenttemp);
//                            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$




                            Log.d("CHECK EXTRAS", extras.toString());
//                            setContentView(R.layout.activity_main);
                            currentTemp = findViewById(R.id.textView_current_temp);
                            humid = findViewById(R.id.textView_humidity);
                            precip = findViewById(R.id.textView_precipitation);
                            city = findViewById(R.id.textView_cityname);
                            tomorrowTemp = findViewById(R.id.textView_tommorrow_temp);
                            dayAfterTemp = findViewById(R.id.textView_dayAfter_temp);

                            if(tempUnitPref.equals("F") ){
                                currentTemp.setText(String.valueOf(currenttempf) + "°F");
                                tomorrowTemp.setText(String.valueOf(tomorrowtempf)+"°F");
                                dayAfterTemp.setText(String.valueOf(dayaftertempf)+"°F");
                                maxMinTemp.setText("Max "+String.valueOf(maxtempf)+"°F↑ • Min "+String.valueOf(mintempf)+"°F↓");
                            }
                            else{
                                currentTemp.setText(String.valueOf(currenttemp) + "°C");
                                tomorrowTemp.setText(String.valueOf(tomorrowtemp)+"°C");
                                dayAfterTemp.setText(String.valueOf(dayaftertemp)+"°C");
                                maxMinTemp.setText("Max "+String.valueOf(maxtemp)+"°C↑ • Max "+String.valueOf(mintemp)+"°C↓");
                            }
                            if(precipUnitPref.equals("Millimeters(mm)")){
                                precip.setText("Precipitation: " + String.valueOf(precipmm) + "mm");

                            }
                            else{
                                precip.setText("Precipitation: " + String.valueOf(precipin) + "in");
                            }
                            humid.setText("Humidity: " + String.valueOf(humidity) + "%");
                            city.setText(cityName+", "+countryName);
                            if(conditionday0.equals("Patchy light rain with thunder")){
                                conditionView.setText("Thunder");
                            }else {
                                conditionView.setText(conditionday0);
                            }
                            if(conditionday0.equals("Mist")){
                                imageToday.setImageResource(R.drawable.mist);
                                playSound(R.raw.cloudysound);
                            } else if (conditionday0.contains("rain") || conditionday0.contains("Rain")) {
                                imageToday.setImageResource(R.drawable.rain);
                                playSound(R.raw.thunder);
                            }
                            else if (conditionday0.equals("Clear")) {
                                imageToday.setImageResource(R.drawable.clear);
                                playSound(R.raw.clearsound);
                            }
                            else if (conditionday0.contains("cloud") || conditionday0.contains("Cloud") ) {
                                imageToday.setImageResource(R.drawable.cloud);
                                conditionView.setText("Cloudy");
                                playSound(R.raw.cloudysound);
                            } else if (conditionday0.equals("Overcast")) {
                                imageToday.setImageResource(R.drawable.overcast);
                                playSound(R.raw.mistsound);
                            }else{
                                imageToday.setImageResource(R.drawable.sunny);
                                playSound(R.raw.sunnysound);
                            }
                            if(conditionday1.equals("Mist")){
                                imageTomorrow.setImageResource(R.drawable.mist);
                            } else if (conditionday1.contains("rain") || conditionday1.contains("Rain")) {
                                imageTomorrow.setImageResource(R.drawable.rain);
                            }
                            else if (conditionday1.equals("Clear")) {
                                imageTomorrow.setImageResource(R.drawable.clear);
                            }
                            else if (conditionday1.contains("cloud") || conditionday1.contains("Cloud") ) {
                                imageTomorrow.setImageResource(R.drawable.cloud);
                            }else if (conditionday1.equals("Overcast")) {
                                imageTomorrow.setImageResource(R.drawable.overcast);
                            }else{
                                imageTomorrow.setImageResource(R.drawable.sunny);
                            }
                            if(conditionday2.equals("Mist")){
                                imageDayAfter.setImageResource(R.drawable.mist);
                            } else if (conditionday2.contains("rain") || conditionday2.contains("Rain")) {
                                imageDayAfter.setImageResource(R.drawable.rain);
                            }
                            else if (conditionday2.equals("Clear")) {
                                imageDayAfter.setImageResource(R.drawable.clear);
                            }
                            else if (conditionday2.contains("cloud") || conditionday2.contains("Cloud") ) {
                                imageDayAfter.setImageResource(R.drawable.cloud);
                            }else if (conditionday2.equals("Overcast")) {
                                imageDayAfter.setImageResource(R.drawable.overcast);
                            }else{
                                imageDayAfter.setImageResource(R.drawable.sunny);
                            }
                            if(time >=5 && time < 9){
                                backgroundImage.setImageResource(resID1);
                            } else if (time >=9 && time < 16){
                                backgroundImage.setImageResource(resID2);
                            }
                            else if (time >=16 && time < 19){
                                backgroundImage.setImageResource(resID3);
                            }
                            else if ((time >= 19 && time <= 23) || time < 5){
                                backgroundImage.setImageResource(resID4);
                            }

                        }
                    }
                }
            });
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return true;
    }
    private void playSound(int soundId) {
        if (playSoundGlobal.equals("Yes")) {
            try {
                if (mp != null) {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = null;
                        mp = MediaPlayer.create(MainActivity.this, soundId);
                    }
                } else {
                    mp = MediaPlayer.create(MainActivity.this, soundId);
                }
                mp.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mp.stop();

                    }
                }, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void stopSound() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
    }
    public void onBackPressed() {
        if (isTaskRoot()) {
            if (backPressedOnce) {
                super.onBackPressed();
                stopSound();
                return;
            }
            backPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedOnce = false;
                }
            }, 2000);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            if (backPressedOnce) {
                super.onBackPressed();
                return;
            }


        }
    }
    protected void sendSMS(String msgBody) {
        Log.i("Send SMS", "");


        try {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("smsto:"));
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address"  , "");
            smsIntent.putExtra("sms_body"  , msgBody);
            startActivity(smsIntent);


            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
//                Toast.makeText(this, "Search is pressed", Toast.LENGTH_SHORT).show();
                Intent intentSearch = new Intent(this , SearchActivity.class);
//                startActivityForResult(intentSearch, REQUEST_CODE);
//                startActivity(intentSearch);
//                startActivityForResult(intentSearch, 1);
                activityResultLaunch.launch(intentSearch);

                break;

            case R.id.setting:
//                Toast.makeText(this, "Setting is pressed", Toast.LENGTH_SHORT).show();
                Intent intentSetting = new Intent(this , SettingsActivity.class);
                startActivity(intentSetting);
                break;
            case R.id.share:
                String msgBody = ( "Today's Weather\nCity: "+msgCityName+"\nTemperture: "+msgTemp+"°C\nMax/Min Temp: "+msgMaxTemp+"°C/"+msgMinTemp+"°C\nCondition: "+msgCondition+"\nHumidity: "+msgHumidty+"%\nPrecipitation: "+msgPrecipitation+"in");
                sendSMS(msgBody);
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
