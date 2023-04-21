package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    ProgressBar loadingViewSearch;
    String [] name = {};
    ArrayAdapter<String> arrayAdapter;
    MediaPlayer mp;
    String playSoundGlobal;
    private final String apiUrl = "https://api.weatherapi.com/v1/forecast.json";
    private final String apiKey = "9cabe1f6815145c98a2142339231304";
//    ################################################################################

    public boolean ifItemClicked = false;
    String topHistoryItem = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String soundPref = pref.getString("selectedSoundPref", "Yes");
        playSoundGlobal = soundPref;
        listView = findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,name);
        listView.setAdapter(arrayAdapter);
        loadingViewSearch = findViewById(R.id.loadingProgressBar1);
        loadingViewSearch.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchHistoryDbHelper dbHelper = new SearchHistoryDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT search_string, search_date FROM search_history ORDER BY _id DESC", null);

// Create a list of SearchString objects from the cursor
        List<SearchString> searchStrings = new ArrayList<>();
        while (cursor.moveToNext()) {
            String searchString = cursor.getString(0);
            String searchDate = cursor.getString(1);
            SearchString searchStringObject = new SearchString(searchString, searchDate);
            searchStrings.add(searchStringObject);
        }
        String[] searchArray = new String[searchStrings.size()];

        for (int i = 0; i < searchStrings.size(); i++) {
            searchArray[i] = searchStrings.get(i).getSearchString();
        }


        arrayAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1,searchArray);
        listView.setAdapter(arrayAdapter);


        getMenuInflater().inflate(R.menu.menu1,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Type location");


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                ifItemClicked = true;
                searchView.setQuery(selectedItem, true);
                listView.setVisibility(View.GONE);
                loadingViewSearch.setVisibility(View.VISIBLE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                loadingViewSearch.setVisibility(View.VISIBLE);
                String tempUrl = "";
                String city = s;
                if(city.equals("")){
                    Toast.makeText(SearchActivity.this, "City field cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    tempUrl = apiUrl + "?key=" + apiKey + "&q=" +city + "&days="+ "3" + "&aqi=" +"yes" + "&alerts=" + "yes";
                }
                StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        String output = "";
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject jsonObjectLocation = jsonResponse.getJSONObject("location");
                            String cityName = jsonObjectLocation.getString("name");
                            String countryName = jsonObjectLocation.getString("country");
                            String localTime = jsonObjectLocation.getString("localtime");
                            int localTimeInt=0;
                            Log.d("localtime", String.valueOf(String.valueOf(localTime.charAt(11)) + String.valueOf(localTime.charAt(12))));
                            if(String.valueOf(localTime.charAt(12)).equals(":")){
                                localTime = String.valueOf(localTime.charAt(11));
                            }else{
                                localTime = String.valueOf(localTime.charAt(11)) + String.valueOf(localTime.charAt(12));
                            }
                            Log.d("debuggg", localTime);
                            localTimeInt = Integer.valueOf(localTime);
                            JSONObject jsonObjectCurrent = jsonResponse.getJSONObject("current");
                            double temp_celcius = jsonObjectCurrent.getDouble("temp_c");
                            double temp_farhen = jsonObjectCurrent.getDouble("temp_f");
                            double windSpeed = jsonObjectCurrent.getDouble("wind_kph");
                            double precipitation = jsonObjectCurrent.getDouble("precip_in");
                            double precipitation_mm = jsonObjectCurrent.getDouble("precip_mm");
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
                            JSONObject jsonForecastDayDetailObjectSunrise1 = jsonForecastObject1.getJSONObject("astro");
                            String sunriseTomm= jsonForecastDayDetailObjectSunrise1.getString("sunrise");
                            JSONObject jsonForecastDayDetailObject1 = jsonForecastObject1.getJSONObject("day");
                            double firstDayTemp_c = jsonForecastDayDetailObject1.getDouble("avgtemp_c");
                            double firstDayTemp_f = jsonForecastDayDetailObject1.getDouble("avgtemp_f");

//                            ########################################################################
                            double max_c_Tom = jsonForecastDayDetailObject1.getDouble("maxtemp_c");
                            double min_c_Tom = jsonForecastDayDetailObject1.getDouble("mintemp_c");
                            double max_f_Tom = jsonForecastDayDetailObject1.getDouble("maxtemp_f");
                            double min_f_Tom = jsonForecastDayDetailObject1.getDouble("mintemp_f");
                            double humidityTomm = jsonForecastDayDetailObject1.getDouble("avghumidity");
                            double windspeedTomm = jsonForecastDayDetailObject1.getDouble("maxwind_mph");
                            double precipTommIn = jsonForecastDayDetailObject1.getDouble("totalprecip_in");
                            double precipTommMm = jsonForecastDayDetailObject1.getDouble("totalprecip_mm");
                            double rainChanceTomm = jsonForecastDayDetailObject1.getDouble("daily_will_it_rain");



//                            ##########################################################################


                            JSONObject jsonObjectCondition1 = jsonForecastDayDetailObject1.getJSONObject("condition");
                            String condition1 = jsonObjectCondition1.getString("text");

                            JSONObject jsonForecastObject2 = jsonForecastArray.getJSONObject(2);
                            JSONObject jsonForecastDayDetailObjectSunrise2 = jsonForecastObject1.getJSONObject("astro");
                            String sunriseDayAfter= jsonForecastDayDetailObjectSunrise2.getString("sunrise");

                            JSONObject jsonForecastDayDetailObject2 = jsonForecastObject2.getJSONObject("day");
                            double secondDayTemp_c = jsonForecastDayDetailObject2.getDouble("avgtemp_c");
                            double secondDayTemp_f = jsonForecastDayDetailObject2.getDouble("avgtemp_f");
//                            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                            double max_c_After = jsonForecastDayDetailObject2.getDouble("maxtemp_c");
                            double min_c_After = jsonForecastDayDetailObject2.getDouble("mintemp_c");
                            double max_f_After = jsonForecastDayDetailObject2.getDouble("maxtemp_f");
                            double min_f_After = jsonForecastDayDetailObject2.getDouble("mintemp_f");
                            double humidityAfter = jsonForecastDayDetailObject2.getDouble("avghumidity");
                            double windspeedAfter = jsonForecastDayDetailObject2.getDouble("maxwind_mph");
                            double precipAfterIn = jsonForecastDayDetailObject2.getDouble("totalprecip_in");
                            double precipAfterMm = jsonForecastDayDetailObject2.getDouble("totalprecip_mm");
                            double rainChanceAfter = jsonForecastDayDetailObject2.getDouble("daily_will_it_rain");




//                            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                            JSONObject jsonObjectCondition2 = jsonForecastDayDetailObject2.getJSONObject("condition");
                            String condition2 = jsonObjectCondition2.getString("text");
//                            Toast.makeText(SearchActivity.this, cityName+countryName+" "+String.valueOf(firstDayTemp_c)+" "+String.valueOf(secondDayTemp_c),Toast.LENGTH_SHORT).show();

                            if(condition0.equals("Mist")){
                                playSound(R.raw.cloudysound);
                            } else if (condition0.contains("rain") || condition0.contains("Rain")) {
                                playSound(R.raw.thunder);
                            } else if (condition0.contains("cloud") || condition0.contains("Cloud")) {
                                playSound(R.raw.cloudysound);
                            } else if (condition0.equals("Clear")) {
                                playSound(R.raw.clearsound);
                            }else if(condition0.equals("Overcast")){
                                playSound(R.raw.mistsound);
                            }else{
                                playSound(R.raw.sunnysound);
                            }

                            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                            Bundle extras = new Bundle();
                            extras.putDouble("EXTRA_CURRENTTEMP", temp_celcius);
                            extras.putDouble("EXTRA_CURRENTTEMPF", temp_farhen);
                            extras.putDouble("EXTRA_MAXTEMP", maxTemp_c);
                            extras.putDouble("EXTRA_MAXTEMPF", maxTemp_f);
                            extras.putDouble("EXTRA_MINTEMP", minTemp_c);
                            extras.putDouble("EXTRA_MINTEMPF", minTemp_f);
                            extras.putInt("EXTRA_HUMIDITY", humidity);
                            extras.putDouble("EXTRA_PRECIP", precipitation);
                            extras.putDouble("EXTRA_PRECIPMM", precipitation_mm);
                            extras.putDouble("EXTRA_TOMORROWTEMP", firstDayTemp_c);
                            extras.putDouble("EXTRA_TOMORROWTEMPF", firstDayTemp_f);
                            extras.putDouble("EXTRA_DAYAFTERTEMP", secondDayTemp_c);
                            extras.putDouble("EXTRA_DAYAFTERTEMPF", secondDayTemp_f);
                            extras.putString("EXTRA_CITY", cityName);
                            extras.putString("EXTRA_COUNTRY", countryName);
                            extras.putString("EXTRA_CONDITION0", condition0);
                            extras.putString("EXTRA_CONDITION1", condition1);
                            extras.putString("EXTRA_CONDITION2", condition2);
                            extras.putInt("EXTRA_TIME", localTimeInt);


                            extras.putString("WEXTRA_TOMTEMP"  , String.valueOf(firstDayTemp_c));
                            extras.putString("WEXTRA_TOMTEMPF"   , String.valueOf(firstDayTemp_f));
                            extras.putString("WEXTRA_MAXTOMTEMP"   , String.valueOf(max_c_Tom));
                            extras.putString("WEXTRA_MAXTOMTEMPF"   , String.valueOf(max_f_Tom));
                            extras.putString("WEXTRA_MINTOMTEMP"   , String.valueOf(min_c_Tom));
                            extras.putString("WEXTRA_MINTOMTEMPF"  , String.valueOf(min_f_Tom));
                            extras.putString("WEXTRA_HUMIDITYTOM"  , String.valueOf(humidityTomm));
                            extras.putString("WEXTRA_PRECIPTOM"  , String.valueOf(precipTommIn));
                            extras.putString("WEXTRA_PRECIPTOMMM"  , String.valueOf(precipTommMm));
                            extras.putString("WEXTRA_WINDSPEEDTOM"  , String.valueOf(windspeedTomm));
                            extras.putString("WEXTRA_RAINCHANCETOM"  , String.valueOf(rainChanceTomm));
                            extras.putString("WEXTRA_SUNRISETOM"  , sunriseTomm);

                            extras.putString("WEXTRA_AFTTEMP" , String.valueOf(secondDayTemp_c));
                            extras.putString("WEXTRA_AFTTEMPF"  , String.valueOf(secondDayTemp_f));
                            extras.putString("WEXTRA_MAXAFTTEMP"  , String.valueOf(max_c_After));
                            extras.putString("WEXTRA_MAXAFTEMPF"  , String.valueOf(max_f_After));
                            extras.putString("WEXTRA_MINAFTTEMP"  , String.valueOf(min_c_After));
                            extras.putString("WEXTRA_MINAFTTEMPF"  , String.valueOf(min_f_After));
                            extras.putString("WEXTRA_HUMIDITYAFT"  , String.valueOf(humidityAfter));
                            extras.putString("WEXTRA_PRECIPAFT"  , String.valueOf(precipAfterIn));
                            extras.putString("WEXTRA_PRECIPAFTMM"  , String.valueOf(precipAfterMm));
                            extras.putString("WEXTRA_WINDSPEEDAFT"  , String.valueOf(windspeedAfter));
                            extras.putString("WEXTRA_RAINCHANCEAFT"  , String.valueOf(rainChanceAfter));
                            extras.putString("WEXTRA_SUNRISEAFT"  , sunriseDayAfter);



//                            Log.d("THIS IS ANOTHER ACITIVIT","i got there and its feeling insane");
                            loadingViewSearch.setVisibility(View.GONE);
                            intent.putExtras(extras);

                            setResult(123, intent);
                            finish();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No matching location found", Toast.LENGTH_SHORT).show();
                        loadingViewSearch.setVisibility(View.GONE);
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

                if(!ifItemClicked){
                    if(!s.equals(topHistoryItem)) {
                        String searchDate = DateFormat.getDateTimeInstance().format(new Date());
                        // Insert the new row into the database
                        SearchHistoryDbHelper dbHelper = new SearchHistoryDbHelper(SearchActivity.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("search_string", s);
                        values.put("search_date", searchDate);
                        db.insert("search_history", null, values);
                    }
                }
                // Get the current date/time
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                if(arrayAdapter.isEmpty()){
                    return false;
                }
                topHistoryItem = arrayAdapter.getItem(0);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void playSound(int soundId) {
        if (playSoundGlobal.equals("Yes")) {
            try {
                if (mp != null) {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = null;
                        mp = MediaPlayer.create(SearchActivity.this, soundId);
                    }
                } else {
                    mp = MediaPlayer.create(SearchActivity.this, soundId);
                }
                mp.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mp.stop();

                    }
                }, 6000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}