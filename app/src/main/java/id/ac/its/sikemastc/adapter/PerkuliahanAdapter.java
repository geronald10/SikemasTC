package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.HalamanUtamaDosen;

public class PerkuliahanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TODAY_JADWAL = 0;
    private static final int VIEW_TYPE_FUTURE_DAY_JADWAL = 1;

    private final Context mContext;
    private Cursor mCursor;

    private boolean mUseTodayLayout;

    final private PerkuliahanAdapterOnClickHandler mClickHandler;

    public interface PerkuliahanAdapterOnClickHandler {
        void onClick(int itemId, String idListKelas, String mataKuliah, String kodeKelas);
    }

    public PerkuliahanAdapter(Context context, PerkuliahanAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_TODAY_JADWAL:
                View view0 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_item_jadwal_hari_ini, viewGroup, false);
                view0.setFocusable(true);
                return new PerkuliahanAdapterViewHolderPrimary(view0);
            case VIEW_TYPE_FUTURE_DAY_JADWAL:
                View view1 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_item_jadwal_utama, viewGroup, false);
                view1.setFocusable(true);
                return new PerkuliahanAdapterViewHolderSecondary(view1);
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String statusDosen = mCursor.getString(HalamanUtamaDosen.INDEX_STATUS_DOSEN);
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
                PerkuliahanAdapterViewHolderPrimary viewholder0 = (PerkuliahanAdapterViewHolderPrimary) holder;
                viewholder0.tvPertemuanKe.setText(perkuliahanKe);
                viewholder0.tvKelas.setText(kodeKkelas);
                viewholder0.tvTanggalPertemuan.setText(tanggalPerkuliahan);
                viewholder0.tvRuangKuliah.setText(ruangKuliah);
                viewholder0.tvWaktuKuliah.setText(waktuKuliah);
                viewholder0.tvMataKuliah.setText(mataKuliah);
                switch (statusDosen) {
                    case "0":
                        viewholder0.btnBatalkanPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnAkhiriPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnStatusPerkuliahanBerakhir.setVisibility(View.GONE);
                        viewholder0.btnAktifkanPerkuliahan.setVisibility(View.VISIBLE);
                        break;
                    case "1":
                        viewholder0.btnStatusPerkuliahanBerakhir.setVisibility(View.GONE);
                        viewholder0.btnAktifkanPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnAkhiriPerkuliahan.setVisibility(View.VISIBLE);
                        viewholder0.btnBatalkanPerkuliahan.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        viewholder0.btnBatalkanPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnAkhiriPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnAktifkanPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnStatusPerkuliahanBerakhir.setVisibility(View.VISIBLE);
                        break;
                    case "3":
                        viewholder0.btnBatalkanPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnAkhiriPerkuliahan.setVisibility(View.GONE);
                        viewholder0.btnStatusPerkuliahanBerakhir.setVisibility(View.GONE);
                        viewholder0.btnAktifkanPerkuliahan.setVisibility(View.VISIBLE);
                        break;
                }
                break;

            case VIEW_TYPE_FUTURE_DAY_JADWAL:
                PerkuliahanAdapterViewHolderSecondary viewholder1 = (PerkuliahanAdapterViewHolderSecondary) holder;
                viewholder1.tvTanggalPertemuan.setText(tanggalPerkuliahan);
                viewholder1.tvRuangKuliah.setText(ruangKuliah);
                viewholder1.tvWaktuKuliah.setText(waktuKuliah);
                viewholder1.tvMataKuliah.setText(mataKuliah);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
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

    class PerkuliahanAdapterViewHolderSecondary extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTanggalPertemuan;
        private TextView tvRuangKuliah;
        private TextView tvWaktuKuliah;
        private TextView tvMataKuliah;

        PerkuliahanAdapterViewHolderSecondary(View itemView) {
            super(itemView);

            tvTanggalPertemuan = (TextView) itemView.findViewById(R.id.tv_tanggal_pertemuan);
            tvRuangKuliah = (TextView) itemView.findViewById(R.id.tv_ruang_kuliah);
            tvWaktuKuliah = (TextView) itemView.findViewById(R.id.tv_waktu_kuliah);
            tvMataKuliah = (TextView) itemView.findViewById(R.id.tv_mata_kuliah);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String idPerkuliahan = mCursor.getString(HalamanUtamaDosen.INDEX_ID_PERKULIAHAN);
            String mataKuliah = mCursor.getString(HalamanUtamaDosen.INDEX_NAMA_MK);
            String kodeKelas = mCursor.getString(HalamanUtamaDosen.INDEX_KODE_KELAS);
            mClickHandler.onClick(0, idPerkuliahan, mataKuliah, kodeKelas);
        }
    }

    class PerkuliahanAdapterViewHolderPrimary extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTanggalPertemuan;
        private TextView tvPertemuanKe;
        private TextView tvKelas;
        private TextView tvRuangKuliah;
        private TextView tvWaktuKuliah;
        private TextView tvMataKuliah;
        private Button btnAktifkanPerkuliahan;
        private Button btnBatalkanPerkuliahan;
        private Button btnAkhiriPerkuliahan;
        private Button btnStatusPerkuliahanBerakhir;

        public PerkuliahanAdapterViewHolderPrimary(View itemView) {
            super(itemView);

            tvTanggalPertemuan = (TextView) itemView.findViewById(R.id.tv_tanggal_pertemuan);
            tvPertemuanKe = (TextView) itemView.findViewById(R.id.tv_pertemuan_ke);
            tvKelas = (TextView) itemView.findViewById(R.id.tv_kelas);
            tvRuangKuliah = (TextView) itemView.findViewById(R.id.tv_ruang_kuliah);
            tvWaktuKuliah = (TextView) itemView.findViewById(R.id.tv_waktu_kuliah);
            tvMataKuliah = (TextView) itemView.findViewById(R.id.tv_mata_kuliah);
            btnAktifkanPerkuliahan = (Button) itemView.findViewById(R.id.btn_aktifkan_kelas);
            btnBatalkanPerkuliahan = (Button) itemView.findViewById(R.id.btn_nonaktifkan_kelas);
            btnAkhiriPerkuliahan = (Button) itemView.findViewById(R.id.btn_akhiri_kelas);
            btnStatusPerkuliahanBerakhir = (Button) itemView.findViewById(R.id.btn_status_perkuliahan);

            btnAktifkanPerkuliahan.setOnClickListener(this);
            btnBatalkanPerkuliahan.setOnClickListener(this);
            btnAkhiriPerkuliahan.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String idPerkuliahan = mCursor.getString(HalamanUtamaDosen.INDEX_ID_PERKULIAHAN);
            String mataKuliah = mCursor.getString(HalamanUtamaDosen.INDEX_NAMA_MK);
            String kodeKelas = mCursor.getString(HalamanUtamaDosen.INDEX_KODE_KELAS);
            switch (v.getId()) {
                case R.id.btn_aktifkan_kelas:
                    Log.d("Masuk Aktifkan kelas", String.valueOf(v.getId()));
                    mClickHandler.onClick(1, idPerkuliahan, mataKuliah, kodeKelas);
                    break;
                case R.id.btn_akhiri_kelas:
                    mClickHandler.onClick(2, idPerkuliahan, mataKuliah, kodeKelas);
                    break;
                case R.id.btn_nonaktifkan_kelas:
                    mClickHandler.onClick(3, idPerkuliahan, mataKuliah, kodeKelas);
                    break;
                default:
                    mClickHandler.onClick(0, idPerkuliahan, null, null);
                    break;
            }
        }
    }
}
