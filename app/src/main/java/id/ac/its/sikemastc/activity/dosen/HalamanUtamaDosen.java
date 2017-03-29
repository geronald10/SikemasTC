package id.ac.its.sikemastc.activity.dosen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import id.ac.its.sikemastc.BaseActivity;
import id.ac.its.sikemastc.R;

public class HalamanUtamaDosen extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = ListKelasDiampu.class.getSimpleName();

    private Context mContext;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private Button btnAktif;
    private Button btnLihatKehadiran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_dosen);
        mContext = this;

        btnAktif = (Button) findViewById(R.id.btn_aktifkan_kelas);
        btnLihatKehadiran = (Button) findViewById(R.id.btn_lihat_kehadiran);

        btnAktif.setOnClickListener(operate);
        btnLihatKehadiran.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_aktifkan_kelas:
                    break;
                case R.id.btn_lihat_kehadiran:
                    Intent intentToLihatKehadiran = new Intent(mContext, LihatKehadiran.class);
                    startActivity(intentToLihatKehadiran);
                    break;
            }
        }
    };
}
