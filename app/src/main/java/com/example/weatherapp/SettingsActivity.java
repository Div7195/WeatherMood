package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private EditText defaultLocation;
    private RadioGroup radioTemperatureGroup, radioPrecipitationGroup, radioSoundsGroup;
    private RadioButton radioTempButton, radioPrecipitationButton, radioSoundButton;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        defaultLocation = findViewById(R.id.defaultLocation);
        radioTemperatureGroup = findViewById(R.id.radioTemperature);
        radioPrecipitationGroup = findViewById(R.id.radioPrecipitation);
        radioSoundsGroup = findViewById(R.id.radioSounds);
//        radioThemeGroup = findViewById(R.id.radioTheme);
        btn = findViewById(R.id.save_button);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String defaultLocationValue = pref.getString("defaultEnteredLocation", "");
        defaultLocation.setText(defaultLocationValue);
        radioTempButton = (RadioButton)findViewById(pref.getInt("selectedTempRadioId", 0));
        if(radioTempButton != null){
            radioTempButton.setChecked(true);
        }

        radioPrecipitationButton = (RadioButton)findViewById(pref.getInt("selectedPrecipRadioId", 0));
        if(radioPrecipitationButton != null){
            radioPrecipitationButton.setChecked(true);
        }
        radioSoundButton = (RadioButton)findViewById(pref.getInt("selectedSoundRadioId", 0));
        if(radioSoundButton != null){
            radioSoundButton.setChecked(true);
        }

//        radioThemeButton = (RadioButton)findViewById(pref.getInt("selectedPrecipRadioId", 0));
//        if(radioThemeButton != null){
//            radioThemeButton.setChecked(true);
//        }
        addListenerOnButton();

    }

    public void addListenerOnButton(){

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = defaultLocation.getText().toString();
                int selectedIdTemp = radioTemperatureGroup.getCheckedRadioButtonId();
                int selectedIdPrecipitation = radioPrecipitationGroup.getCheckedRadioButtonId();
                int selectedIdSound = radioSoundsGroup.getCheckedRadioButtonId();
//                int selectedIdTheme = radioThemeGroup.getCheckedRadioButtonId();
                radioTempButton = (RadioButton) findViewById(selectedIdTemp);
                radioPrecipitationButton = (RadioButton) findViewById(selectedIdPrecipitation);
                radioSoundButton = (RadioButton) findViewById(selectedIdSound);
                SharedPreferences pref = getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                String currentLocation = pref.getString("currentLocation", "");
                if(location.equals("")){
                    editor.putString("defaultEnteredLocation", currentLocation);
                    editor.apply();
                }
                else{
                    editor.putString("defaultEnteredLocation", location);
                    editor.apply();
                }
                editor.putBoolean("selectedTempUnitPreference", radioTempButton.isChecked());
                editor.putBoolean("selectedPreciptUnitPreference", radioPrecipitationButton.isChecked());
                editor.putBoolean("selectedSoundPreference", radioSoundButton.isChecked());
//                editor.putBoolean("selectedThemePreference", radioThemeButton.isChecked());
                editor.putInt("selectedTempRadioId",selectedIdTemp);
                editor.putInt("selectedPrecipRadioId",selectedIdPrecipitation);
                editor.putInt("selectedSoundRadioId", selectedIdSound);
//                editor.putInt("selectedThemeRadioId",selectedIdTheme);
                editor.putString("selectedTempUnitName", radioTempButton.getText().toString());
                editor.putString("selectedPrecipUnitName", radioPrecipitationButton.getText().toString());
                editor.putString("selectedSoundPref", radioSoundButton.getText().toString());
//                editor.putString("selectedThemeName", radioThemeButton.getText().toString());
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Saved Changes!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}