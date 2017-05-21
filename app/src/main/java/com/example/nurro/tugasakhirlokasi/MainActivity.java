package com.example.nurro.tugasakhirlokasi;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzip.runOnUiThread;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String activeFragment;
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_location) {
            if (activeFragment != null && activeFragment.equals("API")) {
                GoogleAPIFragment APIFragment = (GoogleAPIFragment) this.fragment;
                APIFragment.disableGPS();

                activeFragment = "GPS";
                this.fragment = new GPSFragment();
                createFragment();
            }
            else if (activeFragment == null || activeFragment.equals("LIST")) {
                activeFragment = "GPS";
                this.fragment = new GPSFragment();
                createFragment();
            }
        }
        else if (id == R.id.nav_google_location) {
            if (activeFragment != null && activeFragment.equals("GPS")) {
                GPSFragment GPSFragment = (GPSFragment)this.fragment;
                GPSFragment.disableGPS();

                activeFragment = "API";
                this.fragment = new GoogleAPIFragment();
                createFragment();
            }
            else if (activeFragment == null  || activeFragment.equals("LIST")) {
                activeFragment = "API";
                this.fragment = new GoogleAPIFragment();
                createFragment();
            }
        }

        else if (id == R.id.nav_list_places) {
            if (activeFragment != null && activeFragment.equals("GPS")) {
                GPSFragment GPSFragment = (GPSFragment)this.fragment;
                GPSFragment.disableGPS();
            }
            else if (activeFragment != null && activeFragment.equals("API")) {
                GoogleAPIFragment APIFragment = (GoogleAPIFragment) this.fragment;
                APIFragment.disableGPS();
            }
            activeFragment = "LIST";
            this.fragment = new PlacesFragment();
            createFragment();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_main, this.fragment, activeFragment)
                .setTransition(fragmentManager.beginTransaction().TRANSIT_FRAGMENT_FADE)
                .commit();
    }

}
