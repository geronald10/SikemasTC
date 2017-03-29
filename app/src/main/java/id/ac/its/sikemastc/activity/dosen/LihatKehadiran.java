package id.ac.its.sikemastc.activity.dosen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import id.ac.its.sikemastc.R;

public class LihatKehadiran extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kehadiran);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Lihat Kehadiran");
        mToolbar.setSubtitle("Update terakhir pukul 12.25");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mToolbar.setElevation(10f);
//        }

        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title =  (String) item.getTitle();
                Toast.makeText(LihatKehadiran.this, title + " Selected !", Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.refresh:
                        break;
                    case R.id.search:
                        break;
                }
                return true;
            }
        });
    }
}
