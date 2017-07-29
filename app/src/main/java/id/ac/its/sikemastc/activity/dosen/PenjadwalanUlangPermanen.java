package id.ac.its.sikemastc.activity.dosen;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import id.ac.its.sikemastc.R;

public class PenjadwalanUlangPermanen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjadwalan_ulang_permanen);

        Intent intentExtra = getIntent();
        String idKelas = intentExtra.getStringExtra("id_kelas");

        TextView tvIdKelas = (TextView) findViewById(R.id.tv_id_kelas);
        tvIdKelas.setText(idKelas);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Penjadwalan Ulang Permanen");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(10f);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
