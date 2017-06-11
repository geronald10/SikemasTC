package id.ac.its.sikemastc.activity.dosen;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.databinding.ActivityDetailPerkuliahanBinding;

public class DetailPerkuliahan extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = DetailPerkuliahan.class.getSimpleName();

    public static final String[] MAIN_DETAIL_PERKULIAHAN_DOSEN_DETAIL_PROJECTION = {
            SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_STATUS_DOSEN,
            SikemasContract.PerkuliahanEntry.KEY_PERTEMUAN_KE,
            SikemasContract.PerkuliahanEntry.KEY_TANGGAL_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_KODE_SEMESTER,
            SikemasContract.PerkuliahanEntry.KEY_NAMA_MK,
            SikemasContract.PerkuliahanEntry.KEY_KODE_MK,
            SikemasContract.PerkuliahanEntry.KEY_NAMA_RUANGAN,
            SikemasContract.PerkuliahanEntry.KEY_KODE_KELAS,
            SikemasContract.PerkuliahanEntry.KEY_HARI,
            SikemasContract.PerkuliahanEntry.KEY_MULAI,
            SikemasContract.PerkuliahanEntry.KEY_SELESAI
    };

    public static final int INDEX_ID_PERKULIAHAN = 0;
    public static final int INDEX_STATUS_DOSEN = 1;
    public static final int INDEX_PERTEMUAN_KE = 2;
    public static final int INDEX_TANGGAL_PERKULIAHAN = 3;
    public static final int INDEX_KODE_SEMESTER = 4;
    public static final int INDEX_NAMA_MK = 5;
    public static final int INDEX_KODE_MK = 6;
    public static final int INDEX_NAMA_RUANGAN = 7;
    public static final int INDEX_KODE_KELAS = 8;
    public static final int INDEX_HARI = 9;
    public static final int INDEX_MULAI = 10;
    public static final int INDEX_SELESAI = 11;

    private static final int ID_DETAIL_PERKULIAHAN_DOSEN_LOADER = 350;

    private Uri mUri;

    private ActivityDetailPerkuliahanBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_perkuliahan);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Detail Perkuliahan");
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

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailPerkuliahanActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_PERKULIAHAN_DOSEN_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_DETAIL_PERKULIAHAN_DOSEN_LOADER:
                return new CursorLoader(this,
                        mUri,
                        MAIN_DETAIL_PERKULIAHAN_DOSEN_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        Button btnLihatKehadiran = (Button)findViewById(R.id.btn_lihat_kehadiran);

        String tanggalPertemuan = data.getString(INDEX_HARI) + ", " +
                data.getString(INDEX_TANGGAL_PERKULIAHAN);
        String waktu = data.getString(INDEX_MULAI) + "-" +
                data.getString(INDEX_SELESAI);
        final String idPerkuliahan = data.getString(INDEX_ID_PERKULIAHAN);
        String statusDosen = data.getString(INDEX_STATUS_DOSEN);
//        String jumlahPeserta =

        mDetailBinding.primaryInfo.tvTanggalPertemuan.setText(tanggalPertemuan);
        mDetailBinding.primaryInfo.tvPertemuanKe.setText(data.getString(INDEX_PERTEMUAN_KE));
        mDetailBinding.primaryInfo.tvKodeMataKuliah.setText(data.getString(INDEX_KODE_MK));
        mDetailBinding.primaryInfo.tvMataKuliah.setText(data.getString(INDEX_NAMA_MK));
        mDetailBinding.primaryInfo.tvKelas.setText(data.getString(INDEX_KODE_KELAS));
        mDetailBinding.primaryInfo.tvSemester.setText(data.getString(INDEX_KODE_SEMESTER));
        mDetailBinding.detailPeserta.tvRuangKuliah.setText(data.getString(INDEX_NAMA_RUANGAN));
        mDetailBinding.detailPeserta.tvWaktuKuliah.setText(waktu);
//        mDetailBinding.detailPeserta.tvKapasitas.setText(jumlahPeserta);

        btnLihatKehadiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToLihatKehadiran = new Intent(DetailPerkuliahan.this, LihatKehadiran.class);
                Uri uriKehadiranClicked = SikemasContract.KehadiranEntry.buildKehadiranUriPerkuliahanId(idPerkuliahan);
                intentToLihatKehadiran.putExtra("id_perkuliahan", idPerkuliahan);
                intentToLihatKehadiran.setData(uriKehadiranClicked);
                startActivity(intentToLihatKehadiran);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
