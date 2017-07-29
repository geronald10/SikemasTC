package id.ac.its.sikemastc.activity.mahasiswa;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.databinding.ActivityDetailPerkuliahanMahasiswaBinding;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;

public class DetailPerkuliahanMahasiswa extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = DetailPerkuliahanMahasiswa.class.getSimpleName();

    public static final String[] MAIN_LIST_PERKULIAHAN_PROJECTION = {
            SikemasContract.PerkuliahanEntry.KEY_STATUS_DOSEN,
            SikemasContract.PerkuliahanEntry.KEY_STATUS_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_PERTEMUAN_KE,
            SikemasContract.PerkuliahanEntry.KEY_TANGGAL_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_KODE_MK,
            SikemasContract.PerkuliahanEntry.KEY_NAMA_MK,
            SikemasContract.PerkuliahanEntry.KEY_NAMA_RUANGAN,
            SikemasContract.PerkuliahanEntry.KEY_KODE_KELAS,
            SikemasContract.PerkuliahanEntry.KEY_MULAI,
            SikemasContract.PerkuliahanEntry.KEY_SELESAI,
            SikemasContract.PerkuliahanEntry.KEY_KODE_SEMESTER
    };

    public static final String[] MAIN_PERKULIAHAN_DOSEN_PROJECTION = {
            SikemasContract.DosenEntry.KEY_NAMA_DOSEN,
            SikemasContract.DosenEntry.KEY_EMAIL_DOSEN
    };

    public static final int INDEX_STATUS_DOSEN = 0;
    public static final int INDEX_STATUS_PERKULIAHAN = 1;
    public static final int INDEX_PERTEMUAN_KE = 2;
    public static final int INDEX_TANGGAL_PERKULIAHAN = 3;
    public static final int INDEX_KODE_MK = 4;
    public static final int INDEX_NAMA_MK = 5;
    public static final int INDEX_NAMA_RUANGAN = 6;
    public static final int INDEX_KODE_KELAS = 7;
    public static final int INDEX_MULAI = 8;
    public static final int INDEX_SELESAI = 9;
    public static final int INDEX_KODE_SEMESTER = 10;

    public static final int INDEX_NAMA_DOSEN = 0;
    public static final int INDEX_EMAIL_DOSEN = 1;

    private static final int ID_LIST_PERKULIAHAN_MAHASISWA_LOADER = 55;
    private static final int ID_LIST_DOSEN_PERKULIAHAN_MAHASISWA_LOADER = 56;

    private Uri mUri;
    private String idKelas;

    private ActivityDetailPerkuliahanMahasiswaBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_perkuliahan_mahasiswa);

        Intent intent = getIntent();
        idKelas = intent.getStringExtra("id_kelas");

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

        getSupportLoaderManager().initLoader(ID_LIST_PERKULIAHAN_MAHASISWA_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_LIST_DOSEN_PERKULIAHAN_MAHASISWA_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_LIST_PERKULIAHAN_MAHASISWA_LOADER:
                return new CursorLoader(this,
                        mUri,
                        MAIN_LIST_PERKULIAHAN_PROJECTION,
                        null,
                        null,
                        null);
            case ID_LIST_DOSEN_PERKULIAHAN_MAHASISWA_LOADER:
                Uri dosenKelas = SikemasContract.DosenEntry.CONTENT_URI;
                String selection = SikemasContract.DosenEntry.KEY_ID_KELAS_DOSEN + " = ?";
                return new CursorLoader(this,
                        dosenKelas,
                        MAIN_PERKULIAHAN_DOSEN_PROJECTION,
                        selection,
                        new String[]{idKelas},
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ID_LIST_PERKULIAHAN_MAHASISWA_LOADER:
                // 0 belum aktif, 1 aktif, 2 selesai, 3 pending
                data.moveToFirst();
                switch (data.getString(INDEX_STATUS_DOSEN)) {
                    case "0":
                        mDetailBinding.ivStatusDosen.setImageResource(R.color.colorStatusIdle);
                        mDetailBinding.tvStatusDosen.setText(R.string.perkuliahan_belum_aktif);
                        mDetailBinding.tvStatusDosen.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
                        break;
                    case "1":
                        mDetailBinding.ivStatusDosen.setImageResource(R.color.colorSuccessButtonNormal);
                        mDetailBinding.tvStatusDosen.setText(R.string.perkuliahan_aktif);
                        mDetailBinding.tvStatusDosen.setTextColor(ContextCompat.getColor(this, R.color.colorIcons));
                        break;
                    case "2":
                        mDetailBinding.ivStatusDosen.setImageResource(R.color.colorPrimaryDark);
                        mDetailBinding.tvStatusDosen.setText(R.string.perkuliahan_berakhir);
                        mDetailBinding.tvStatusDosen.setTextColor(ContextCompat.getColor(this, R.color.colorIcons));
                        break;
                    case "3":
                        mDetailBinding.ivStatusDosen.setImageResource(R.color.colorDangerButtonNormal);
                        mDetailBinding.tvStatusDosen.setText(R.string.perkuliahan_ditunda);
                        mDetailBinding.tvStatusDosen.setTextColor(ContextCompat.getColor(this, R.color.colorIcons));
                        break;
                }

                if (data.getString(INDEX_STATUS_PERKULIAHAN).equals("0")) {
                    mDetailBinding.ivStatusPerkuliahan.setImageResource(R.color.colorPrimaryDark);
                    mDetailBinding.tvTanggalHariIni.setTextColor(ContextCompat.getColor(this, R.color.colorIcons));
                    mDetailBinding.tvPertemuanKeLabel.setTextColor(ContextCompat.getColor(this, R.color.colorIcons));
                    mDetailBinding.tvPertemuanKe.setTextColor(ContextCompat.getColor(this, R.color.colorIcons));
                } else {
                    mDetailBinding.ivStatusPerkuliahan.setImageResource(R.color.colorAccent);
                    mDetailBinding.tvTanggalHariIni.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
                    mDetailBinding.tvPertemuanKeLabel.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
                    mDetailBinding.tvPertemuanKe.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
                }
                mDetailBinding.tvSemester.setText(data.getString(INDEX_KODE_SEMESTER));
                mDetailBinding.tvTanggalHariIni.setText(SikemasDateUtils.formatDate(
                        data.getString(INDEX_TANGGAL_PERKULIAHAN)));
                mDetailBinding.tvPertemuanKe.setText(data.getString(INDEX_PERTEMUAN_KE));
                mDetailBinding.tvKodeMatakuliah.setText(data.getString(INDEX_KODE_MK));
                mDetailBinding.tvMatakuliah.setText(data.getString(INDEX_NAMA_MK));
                mDetailBinding.tvRuangan.setText(data.getString(INDEX_NAMA_RUANGAN));
                mDetailBinding.tvKelas.setText(data.getString(INDEX_KODE_KELAS));
                mDetailBinding.tvWaktuMulai.setText(SikemasDateUtils.formatTime(data.getString(INDEX_MULAI)));
                mDetailBinding.tvWaktuSelesai.setText(SikemasDateUtils.formatTime(data.getString(INDEX_SELESAI)));
                break;
            case ID_LIST_DOSEN_PERKULIAHAN_MAHASISWA_LOADER:
                data.moveToFirst();
                mDetailBinding.tvNamaDosen.setText(data.getString(INDEX_NAMA_DOSEN));
                mDetailBinding.tvEmailDosen.setText(data.getString(INDEX_EMAIL_DOSEN));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}