package rapid.responce.RUCU;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class SettingActivity2 extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Switch switchEnglish;
    private Switch switchKiswahili;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_two);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        switchEnglish = findViewById(R.id.switchEnglish);
        switchKiswahili = findViewById(R.id.switchKiswahili);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(SettingActivity2.this, garageHome.class));
                        return true;

                    case R.id.garage:
                        startActivity(new Intent(SettingActivity2.this, myGarage.class));
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(SettingActivity2.this, SettingActivity2.class));
                        return true;
                }
                return false;
            }
        });

        // Retrieve the currently selected language from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedLanguage = preferences.getString("language", "");

        // Set the appropriate switch based on the selected language
        switch (selectedLanguage) {
            case "en":
                switchEnglish.setChecked(true);
                switchKiswahili.setChecked(false);
                break;
            case "sw":
                switchEnglish.setChecked(false);
                switchKiswahili.setChecked(true);
                break;
            default:
                switchEnglish.setChecked(true);
                switchKiswahili.setChecked(false);
                break;
        }

        // Set listeners for the language switches
        switchEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchKiswahili.setChecked(false);
                    setLocale("en");
                }
            }
        });

        switchKiswahili.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchEnglish.setChecked(false);
                    setLocale("sw");
                }
            }
        });
    }

    private void setLocale(String languageCode) {
        // Update the language in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        // Change the locale for the entire app
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart the app to apply the language change to all activities
        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
