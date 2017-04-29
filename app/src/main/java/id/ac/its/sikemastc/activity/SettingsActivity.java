package id.ac.its.sikemastc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import id.ac.its.sikemastc.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String
            KEY_PREF_NOTIFICATION_SWITCH = "notification_switch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}