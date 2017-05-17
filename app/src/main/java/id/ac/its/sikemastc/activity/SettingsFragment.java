package id.ac.its.sikemastc.activity;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import id.ac.its.sikemastc.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey);
    }
}
