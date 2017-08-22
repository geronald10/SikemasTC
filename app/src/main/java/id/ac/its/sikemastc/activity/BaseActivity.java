package id.ac.its.sikemastc.activity;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.HashMap;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.HalamanUtamaDosen;
import id.ac.its.sikemastc.activity.dosen.ListKelasActivity;
import id.ac.its.sikemastc.activity.mahasiswa.HalamanUtamaMahasiswa;
import id.ac.its.sikemastc.activity.mahasiswa.ListKelasMahasiswa;
import id.ac.its.sikemastc.activity.orangtua.HalamanUtamaOrangtua;
import id.ac.its.sikemastc.activity.orangtua.LihatJadwalMahasiswa;
import id.ac.its.sikemastc.data.SikemasSessionManager;

public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mActivityContent;
    public SikemasSessionManager session;
    private SwitchCompat switcher;

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

        int checkedDrawerItemId = intent.getIntExtra("checkedDrawerItemId", 0);
        String title = intent.getStringExtra("title");
        String userNama = userDetail.get(SikemasSessionManager.KEY_USER_NAME);
        String userEmail = userDetail.get(SikemasSessionManager.KEY_USER_EMAIL);
        String userId = userDetail.get(SikemasSessionManager.KEY_USER_ID);
        String userRole = userDetail.get(SikemasSessionManager.KEY_USER_ROLE);
        String userCode = userDetail.get(SikemasSessionManager.KEY_USER_CODE);

        if (checkedDrawerItemId == 0) {
            switch (userRole) {
                case "1":
                    checkedDrawerItemId = R.id.item_home_dosen;
                    break;
                case "4":
                    checkedDrawerItemId = R.id.item_home_mahasiswa;
                    break;
                case "3":
                    checkedDrawerItemId = R.id.item_home_orangtua;
                    break;
            }
        }
        setupToolbarMenu(title);
        setupNavigationDrawerMenu(checkedDrawerItemId, userNama, userEmail, userId, userRole, userCode);
    }

    private void setupToolbarMenu(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_navigation);
        if (title == null)
            mToolbar.setTitle("Beranda");
        else
            mToolbar.setTitle(title);
    }

    private void setupNavigationDrawerMenu(int checkedDrawerItemId, String userNama, String userEmail, String userId, String userRole, String kodeDosen) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View navHeader = navigationView.getHeaderView(0);
        TextView tvName = (TextView) navHeader.findViewById(R.id.tv_nama_user);
        TextView tvUserId = (TextView) navHeader.findViewById(R.id.tv_id_user);
        MaterialLetterIcon icon = (MaterialLetterIcon) navHeader.findViewById(R.id.img_profile);
        tvName.setText(userNama);
        icon.setShapeColor(getResources().getColor(R.color.colorAccent));
        icon.setShapeType(MaterialLetterIcon.Shape.CIRCLE);
        icon.setLetter(kodeDosen);
        icon.setLetterColor(getResources().getColor(R.color.colorIcons));
        icon.setLetterSize(26);
        icon.setLettersNumber(2);
        icon.setInitials(false);
        icon.setInitialsNumber(2);

        switch (userRole) {
            case "1":
                navigationView.inflateMenu(R.menu.menu_item_dosen);
                Menu menuDosen = navigationView.getMenu();
                MenuItem menuItemDosen = menuDosen.findItem(R.id.nav_switch);
                View actionView = MenuItemCompat.getActionView(menuItemDosen);
                tvUserId.setText(userEmail);

                switcher = (SwitchCompat) actionView.findViewById(R.id.switcher);
                switcher.setChecked(true);
                switcher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v, (switcher.isChecked()) ? "is checked!!!" : "not checked!!!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                });
                break;
            case "3":
                navigationView.inflateMenu(R.menu.menu_item_orangtua);
                tvUserId.setText(userId);
                break;
            case "4":
                navigationView.inflateMenu(R.menu.menu_item_mahasiswa);
                Menu menuMhs = navigationView.getMenu();
                MenuItem menuItemMhs = menuMhs.findItem(R.id.nav_switch);
                View actionViewMhs = MenuItemCompat.getActionView(menuItemMhs);
                tvUserId.setText(userId);

                switcher = (SwitchCompat) actionViewMhs.findViewById(R.id.switcher);
                switcher.setChecked(true);
                switcher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v, (switcher.isChecked()) ? "is checked!!!" : "not checked!!!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                });
                break;
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (checkedDrawerItemId == 0)
            navigationView.getMenu().getItem(0).setChecked(false);
        else if(checkedDrawerItemId == 2)
            navigationView.getMenu().getItem(2).setChecked(false);
        else
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

        switch (menuItem.getItemId()) {
            case R.id.item_home_dosen:
                Intent intentToHomeDosen = new Intent(this, HalamanUtamaDosen.class);
                intentToHomeDosen.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToHomeDosen.putExtra("title", menuItem.getTitle());
                startActivity(intentToHomeDosen);
                finish();
                break;
            case R.id.item_home_mahasiswa:
                Intent intentToHomeMahasiswa = new Intent(this, HalamanUtamaMahasiswa.class);
                intentToHomeMahasiswa.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToHomeMahasiswa.putExtra("title", menuItem.getTitle());
                startActivity(intentToHomeMahasiswa);
                finish();
                break;
            case R.id.item_home_orangtua:
                Intent intentToHomeOrangtua = new Intent(this, HalamanUtamaOrangtua.class);
                intentToHomeOrangtua.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToHomeOrangtua.putExtra("title", menuItem.getTitle());
                startActivity(intentToHomeOrangtua);
                finish();
                break;
            case R.id.item_list_kelas_diampu:
                Intent intentToListKelas = new Intent(this, ListKelasActivity.class);
                intentToListKelas.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToListKelas.putExtra("title", menuItem.getTitle());
                startActivity(intentToListKelas);
                finish();
                break;
            case R.id.item_lihat_jadwal_kelas:
                Intent intentToJadwalKelas = new Intent(this, ListKelasMahasiswa.class);
                intentToJadwalKelas.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToJadwalKelas.putExtra("title", menuItem.getTitle());
                startActivity(intentToJadwalKelas);
                finish();
                break;
            case R.id.item_lihat_jadwal_mahasiswa:
                Intent intentToJadwalMhs = new Intent(this, LihatJadwalMahasiswa.class);
                intentToJadwalMhs.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToJadwalMhs.putExtra("title", menuItem.getTitle());
                startActivity(intentToJadwalMhs);
                finish();
                break;
            case R.id.nav_switch:
                switcher.setChecked(!switcher.isChecked());
                Snackbar.make(menuItem.getActionView(), (switcher.isChecked()) ? "is checked" : "not checked",
                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                return true;
            case R.id.item_logout:
                Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
                session.logoutUser();
                finish();
                return true;
        }
        closeDrawer();
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
