package id.ac.its.sikemastc;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.HashMap;

import id.ac.its.sikemastc.activity.dosen.HalamanUtamaDosen;
import id.ac.its.sikemastc.activity.dosen.ListKelasDiampu;
import id.ac.its.sikemastc.activity.dosen.PenjadwalanUlang;
import id.ac.its.sikemastc.data.SikemasSessionManager;

public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mActivityContent;
    public SikemasSessionManager session;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        session = new SikemasSessionManager(this);
        session.checkLogin();

        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        mActivityContent = (FrameLayout) mDrawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, mActivityContent, true);
        super.setContentView(mDrawerLayout);
        Intent intent = getIntent();
        HashMap<String, String> userDetail = session.getUserDetails();

        String title = intent.getStringExtra("title");
        int checkedDrawerItemId = intent.getIntExtra("checkedDrawerItemId", R.id.item_home_dosen);

        setupToolbarMenu(title);
        setupNavigationDrawerMenu(checkedDrawerItemId);
    }

    private void setupToolbarMenu(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (title == null)
            mToolbar.setTitle("Home");
        else
            mToolbar.setTitle(title);
    }

    private void setupNavigationDrawerMenu(int checkedDrawerItemId) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.inflateMenu(R.menu.menu_item_mahasiswa);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView.setCheckedItem(checkedDrawerItemId);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToogle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToogle);
        drawerToogle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String itemName = (String) menuItem.getTitle();
        Toast.makeText(BaseActivity.this, itemName + " Clicked", Toast.LENGTH_SHORT).show();

        closeDrawer();

        switch (menuItem.getItemId()) {
            case R.id.item_home_dosen:
                Intent intentToHome = new Intent(this, HalamanUtamaDosen.class);
                intentToHome.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToHome.putExtra("title", menuItem.getTitle());
                startActivity(intentToHome);
                break;
            case R.id.item_penjadwalan_ulang:
                Intent intentToPenjadwalan = new Intent(this, PenjadwalanUlang.class);
                intentToPenjadwalan.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToPenjadwalan.putExtra("title", menuItem.getTitle());
                startActivity(intentToPenjadwalan);
                break;
            case R.id.item_list_kelas_diampu:
                Intent intentToListKelas = new Intent(this, ListKelasDiampu.class);
                intentToListKelas.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToListKelas.putExtra("title", menuItem.getTitle());
                startActivity(intentToListKelas);
                break;
            case R.id.item_settings:
                break;
            case R.id.item_logout:
                Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
                session.logoutUser();
                return true;
        }
        return true;
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
    }
}
