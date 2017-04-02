package id.ac.its.sikemastc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.ac.its.sikemastc.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
