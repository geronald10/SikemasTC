package id.ac.its.sikemastc.activity.dosen;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.PertemuanAdapter;
import id.ac.its.sikemastc.data.SikemasContract;

public class PertemuanKelasFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PertemuanAdapter.PertemuanAdapterOnClickHandler {

    private final String TAG = PertemuanKelasFragment.class.getSimpleName();

    public static final String[] MAIN_LIST_PERTEMUAN_PROJECTION = {
            SikemasContract.PertemuanEntry.KEY_ID_PERTEMUAN,
            SikemasContract.PertemuanEntry.KEY_ID_KELAS,
            SikemasContract.PertemuanEntry.KEY_STATUS_DOSEN,
            SikemasContract.PertemuanEntry.KEY_STATUS_PERKULIAHAN,
            SikemasContract.PertemuanEntry.KEY_PERTEMUAN_KE,
            SikemasContract.PertemuanEntry.KEY_HARI,
            SikemasContract.PertemuanEntry.KEY_TANGGAL,
            SikemasContract.PertemuanEntry.KEY_MULAI,
            SikemasContract.PertemuanEntry.KEY_SELESAI,
            SikemasContract.PertemuanEntry.KEY_BERITA_ACARA
    };

    public static final int INDEX_ID_PERTEMUAN = 0;
    public static final int INDEX_ID_KELAS = 1;
    public static final int INDEX_STATUS_DOSEN = 2;
    public static final int INDEX_STATUS_PERKULIAHAN = 3;
    public static final int INDEX_PERTEMUAN_KE = 4;
    public static final int INDEX_HARI = 5;
    public static final int INDEX_TANGGAL = 6;
    public static final int INDEX_MULAI = 7;
    public static final int INDEX_SELESAI = 8;
    public static final int INDEX_BERITA_ACARA = 9;

    private static final int ID_LIST_PERTEMUAN_LOADER = 70;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private PertemuanAdapter mPertemuanAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private String bundleIdKelas;
    private String bundleInfoKelas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pertemuan_kelas, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            bundleIdKelas = bundle.getString("id_kelas");
            bundleInfoKelas = bundle.getString("info_kelas");
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_pertemuan);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPertemuanAdapter = new PertemuanAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mPertemuanAdapter);

        showLoading();

        getLoaderManager().initLoader(ID_LIST_PERTEMUAN_LOADER, null, this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_LIST_PERTEMUAN_LOADER:
                Uri pertemuanQueryUri = SikemasContract.PertemuanEntry.CONTENT_URI;
                String sortOrder = "CAST (" + SikemasContract.PertemuanEntry.KEY_PERTEMUAN_KE + " AS REAL) ASC";
                String selection = SikemasContract.PertemuanEntry.KEY_ID_KELAS + " = ? ";
                String selectionArgs = bundleIdKelas;

                return new CursorLoader(getActivity(),
                        pertemuanQueryUri,
                        MAIN_LIST_PERTEMUAN_PROJECTION,
                        selection,
                        new String[]{selectionArgs},
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPertemuanAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0)
            showPertemuanDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPertemuanAdapter.swapCursor(null);
    }

    private void showPertemuanDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String itemId, String idPertemuan, String idKelas, String pertemuanKe) {
        switch (itemId) {
            case "1":
                showAlertDialog(itemId, idPertemuan, pertemuanKe, bundleInfoKelas);
                break;
            case "3":
                showAlertDialog(itemId, idPertemuan, pertemuanKe, bundleInfoKelas);
                break;
            case "4":
                showAlertDialog(itemId, idPertemuan, pertemuanKe, bundleInfoKelas);
                break;
            default:
                Intent intentToDetailPertemuanKelas = new Intent(getActivity(), DetailPertemuanKelas.class);
                intentToDetailPertemuanKelas.putExtra("id_pertemuan", idPertemuan);
                intentToDetailPertemuanKelas.putExtra("id_kelas", idKelas);
                intentToDetailPertemuanKelas.putExtra("pertemuan_ke", pertemuanKe);
                startActivity(intentToDetailPertemuanKelas);
                break;
        }
    }

    private void intentToJadwalSementaraActivity(String idPerkuliahan) {
        Intent intentToJadwalSementara = new Intent(getActivity(), PenjadwalanUlang.class);
        intentToJadwalSementara.putExtra("id_perkuliahan", idPerkuliahan);
        startActivity(intentToJadwalSementara);
    }

    private void intentToBeritaAcaraActivity(String idPerkuliahan) {
        Intent intentToTambahBerita = new Intent(getActivity(), TambahBeritaAcaraActivity.class);
        intentToTambahBerita.putExtra("id_perkuliahan", idPerkuliahan);
        startActivity(intentToTambahBerita);
    }

    private void showAlertDialog(String btnClicked, final String idToPenjadwalan, String pertemuanKe, String infoKelas) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        switch (btnClicked) {
            case "1":
                builder.setTitle("Peringatan")
                        .setMessage("Apakah Anda yakin mengubah jadwal pertemuan ke -" + pertemuanKe + " untuk mata kuliah " + infoKelas + "?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                intentToJadwalSementaraActivity(idToPenjadwalan);
                            }
                        });
                break;
            case "3":
                builder.setTitle("Tambah Berita Acara")
                        .setMessage("Tambahkan berita acara untuk jadwal pertemuan ke -" + pertemuanKe + " pada mata kuliah " + infoKelas + "?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                intentToBeritaAcaraActivity(idToPenjadwalan);
                            }
                        });
                break;
            case "4":
                builder.setTitle("Ubah Berita Acara")
                        .setMessage("Ubah berita acara untuk jadwal pertemuan ke -" + pertemuanKe + " pada mata kuliah " + infoKelas + "?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                intentToBeritaAcaraActivity(idToPenjadwalan);
                            }
                        });
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}