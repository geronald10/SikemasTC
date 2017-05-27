package id.ac.its.sikemastc.activity.dosen;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kofigyan.stateprogressbar.StateProgressBar;

import id.ac.its.sikemastc.R;

public class PenjadwalanUlangSementara extends AppCompatActivity {

    private String[] progressState = {"Tanggal","Waktu","Ruangan","Konfirmasi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjadwalan_ulang_sementara);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Penjadwalan Ulang");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(10f);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.state_reschedule_progress_bar);
        stateProgressBar.setStateDescriptionData(progressState);
    }
}
