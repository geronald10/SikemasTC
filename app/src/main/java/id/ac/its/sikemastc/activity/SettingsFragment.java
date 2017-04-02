package id.ac.its.sikemastc.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;

import id.ac.its.sikemastc.R;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    //    private void setPreferenceSummary(Preference preference, Object value) {
//        String stringValue = value.toString();
//
//        if (preference instanceof ListPreference) {
//            // For list preferences, look up the correct display value in
//            // the preference's 'entries' list (since they have separate labels/values).
//            ListPreference listPreference = (ListPreference) preference;
//            int prefIndex = listPreference.findIndexOfValue(stringValue);
//            if (prefIndex >= 0) {
//                preference.setSummary(listPreference.getEntries()[prefIndex]);
//            }
//        } else {
//            // For other preferences, set the summary to the value's simple string representation.
//            preference.setSummary(stringValue);
//        }
//    }

//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        addPreferencesFromResource(R.xml.preferences);
//        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//        PreferenceScreen prefScreen = getPreferenceScreen();
//        int count = prefScreen.getPreferenceCount();
//        for (int i = 0; i < count; i++) {
//            Preference p = prefScreen.getPreference(i);
//            if (!(p instanceof CheckBoxPreference)) {
//                String value = sharedPreferences.getString(p.getKey(), "");
//                setPreferenceSummary(p, value);
//            }
//        }
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
}
