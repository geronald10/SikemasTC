package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.mahasiswa.ListPerkuliahanFragment;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;

public class PerkuliahanMahasiswaAdapter extends RecyclerView.Adapter<PerkuliahanMahasiswaAdapter.PerkuliahanMahasiswaViewHolder>{

    private final Context mContext;
    private Cursor mCursor;

    final private PerkuliahanMahasiswaAdapterOnClickHandler mClickHandler;

    public interface PerkuliahanMahasiswaAdapterOnClickHandler {
        void onClick(String idKelas, String idPerkuliahan);
    }

    public PerkuliahanMahasiswaAdapter(Context context, PerkuliahanMahasiswaAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public void onBindViewHolder(PerkuliahanMahasiswaViewHolder perkuliahanHolder, int position) {
        mCursor.moveToPosition(position);

        String statusDosen = mCursor.getString(ListPerkuliahanFragment.INDEX_STATUS_DOSEN);
        String perkuliahanKe = mCursor.getString(ListPerkuliahanFragment.INDEX_PERTEMUAN_KE);
        String tanggalPerkuliahan = SikemasDateUtils.formatDate(mCursor.getString(ListPerkuliahanFragment.INDEX_TANGGAL_PERKULIAHAN));
        String mataKuliah = mCursor.getString(ListPerkuliahanFragment.INDEX_NAMA_MK);
        String ruangKuliah = mCursor.getString(ListPerkuliahanFragment.INDEX_NAMA_RUANGAN);
        String kodeKelas = mCursor.getString(ListPerkuliahanFragment.INDEX_KODE_KELAS);
        String waktuMulai = SikemasDateUtils.formatTime(mCursor.getString(ListPerkuliahanFragment.INDEX_MULAI));
        String waktuSelesai = SikemasDateUtils.formatTime(mCursor.getString(ListPerkuliahanFragment.INDEX_SELESAI));

        perkuliahanHolder.tvTanggalPertemuan.setText(tanggalPerkuliahan);
        perkuliahanHolder.tvRuangKuliah.setText(ruangKuliah);
        perkuliahanHolder.tvWaktuMulai.setText(waktuMulai);
        perkuliahanHolder.tvWaktuSelesai.setText(waktuSelesai);
        perkuliahanHolder.tvMataKuliah.setText(mataKuliah);
    }

    @Override
    public PerkuliahanMahasiswaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_jadwal_perkuliahan_mahasiswa, viewGroup, false);
        view.setFocusable(true);
        return new PerkuliahanMahasiswaViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class PerkuliahanMahasiswaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTanggalPertemuan;
        private TextView tvRuangKuliah;
        private TextView tvWaktuMulai;
        private TextView tvWaktuSelesai;
        private TextView tvMataKuliah;

        public PerkuliahanMahasiswaViewHolder(View itemView) {
            super(itemView);

            tvTanggalPertemuan = (TextView) itemView.findViewById(R.id.tv_tanggal_pertemuan);
            tvRuangKuliah = (TextView) itemView.findViewById(R.id.tv_ruang);
            tvWaktuMulai = (TextView) itemView.findViewById(R.id.tv_waktu_mulai);
            tvWaktuSelesai = (TextView) itemView.findViewById(R.id.tv_waktu_selesai);
            tvMataKuliah = (TextView) itemView.findViewById(R.id.tv_mata_kuliah);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String idKelas = mCursor.getString(ListPerkuliahanFragment.INDEX_ID_KELAS);
            String idPerkuliahan = mCursor.getString(ListPerkuliahanFragment.INDEX_ID_PERKULIAHAN);
            mClickHandler.onClick(idKelas, idPerkuliahan);
        }
    }
}
