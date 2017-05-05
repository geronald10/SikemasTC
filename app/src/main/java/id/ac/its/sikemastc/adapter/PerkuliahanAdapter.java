package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.HalamanUtamaDosen;
import id.ac.its.sikemastc.model.JadwalKelas;

public class PerkuliahanAdapter extends RecyclerView.Adapter<PerkuliahanAdapter.PerkuliahanAdapterViewHolder>{

    private static final int VIEW_TYPE_TODAY_JADWAL = 0;
    private static final int VIEW_TYPE_FUTURE_DAY_JADWAL = 1;

    private final Context mContext;
    private Cursor mCursor;

    private boolean mUseTodayLayout;

    final private PerkuliahanAdapterOnClickHandler mClickHandler;

    public interface PerkuliahanAdapterOnClickHandler {
        void onClick(String idListKelas);
    }

    public PerkuliahanAdapter(Context context, PerkuliahanAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @Override
    public PerkuliahanAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_TODAY_JADWAL:
                layoutId = R.layout.list_item_jadwal_hari_ini;
                break;
            case VIEW_TYPE_FUTURE_DAY_JADWAL:
                layoutId = R.layout.list_item_jadwal_utama;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new PerkuliahanAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PerkuliahanAdapterViewHolder perkuliahanAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String statusPerkuliahan = mCursor.getString(HalamanUtamaDosen.INDEX_STATUS_PERKULIAHAN);
        String perkuliahanKe = mCursor.getString(HalamanUtamaDosen.INDEX_PERTEMUAN_KE);
        String tanggalPerkuliahan = mCursor.getString(HalamanUtamaDosen.INDEX_HARI) + ", " +
                mCursor.getString(HalamanUtamaDosen.INDEX_TANGGAL_PERKULIAHAN);
        String mataKuliah = mCursor.getString(HalamanUtamaDosen.INDEX_NAMA_MK);
        String ruangKuliah = mCursor.getString(HalamanUtamaDosen.INDEX_NAMA_RUANGAN);
        String kodeKkelas = mCursor.getString(HalamanUtamaDosen.INDEX_KODE_KELAS);
        String waktuKuliah = mCursor.getString(HalamanUtamaDosen.INDEX_MULAI) + " - " +
                mCursor.getString(HalamanUtamaDosen.INDEX_SELESAI);

        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_TODAY_JADWAL:
                perkuliahanAdapterViewHolder.tvPertemuanKe.setText(perkuliahanKe);
                perkuliahanAdapterViewHolder.tvKelas.setText(kodeKkelas);
                break;
            case VIEW_TYPE_FUTURE_DAY_JADWAL:
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        perkuliahanAdapterViewHolder.tvTanggalPertemuan.setText(tanggalPerkuliahan);
        perkuliahanAdapterViewHolder.tvRuangKuliah.setText(ruangKuliah);
        perkuliahanAdapterViewHolder.tvWaktuKuliah.setText(waktuKuliah);
        perkuliahanAdapterViewHolder.tvMataKuliah.setText(mataKuliah);
    }

    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY_JADWAL;
        } else {
            return VIEW_TYPE_FUTURE_DAY_JADWAL;
        }
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

    class PerkuliahanAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTanggalPertemuan;
        private TextView tvPertemuanKe;
        private TextView tvKelas;
        private TextView tvRuangKuliah;
        private TextView tvWaktuKuliah;
        private TextView tvMataKuliah;

        PerkuliahanAdapterViewHolder(View itemView) {
            super(itemView);

            tvTanggalPertemuan = (TextView) itemView.findViewById(R.id.tv_tanggal_pertemuan);
            tvPertemuanKe = (TextView) itemView.findViewById(R.id.tv_pertemuan_ke);
            tvKelas = (TextView) itemView.findViewById(R.id.tv_kelas);
            tvRuangKuliah = (TextView) itemView.findViewById(R.id.tv_ruang_kuliah);
            tvWaktuKuliah = (TextView) itemView.findViewById(R.id.tv_waktu_kuliah);
            tvMataKuliah = (TextView) itemView.findViewById(R.id.tv_mata_kuliah);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String IdPerkuliahan = mCursor.getString(HalamanUtamaDosen.INDEX_ID_PERKULIAHAN);
            mClickHandler.onClick(IdPerkuliahan);
        }
    }
}
