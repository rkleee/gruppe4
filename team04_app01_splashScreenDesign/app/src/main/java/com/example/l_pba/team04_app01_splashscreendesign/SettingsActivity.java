/**
 * @author group04
 * @version 1.0
 * SettingsActivity, where you can change the settings
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Android Imports
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

//the SettingsActivity class
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

        @Override
        /**
         * @param savedInstanceState Bundle
         */
        public void onCreate(Bundle savedInstanceState) {
            //Android Shit
            super.onCreate(savedInstanceState);

            //add preferences to vision
            addPreferencesFromResource(R.xml.preferences);

            //added keys to Listener
            Preference languagePref = findPreference(getString(R.string.preference_language_key));
            Preference stylePref = findPreference(getString(R.string.preference_style_key));
            Preference pingPref = findPreference(getString(R.string.preference_ping_key));

            languagePref.setOnPreferenceChangeListener(this);
            stylePref.setOnPreferenceChangeListener(this);
            pingPref.setOnPreferenceChangeListener(this);

            // call onPreferenceChange instantly with the values, which are saved in SharedPref.
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            String savedLanguage = sharedPrefs.getString(languagePref.getKey(), "");
            String savedStyle = sharedPrefs.getString(stylePref.getKey(), "");
            String savedPing = sharedPrefs.getString(pingPref.getKey(), "");

            onPreferenceChange(languagePref, savedLanguage);
            onPreferenceChange(stylePref, savedStyle);
            onPreferenceChange(pingPref, savedPing);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            //display the pref summary
            preference.setSummary(value.toString());

            return true;
        }

    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent backIntent = new Intent(SettingsActivity.this,HomeActivity.class);
                startActivity(backIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        },50);
    }
}
