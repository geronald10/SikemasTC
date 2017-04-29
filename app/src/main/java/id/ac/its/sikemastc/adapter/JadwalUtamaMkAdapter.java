package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.model.JadwalKelas;

public class JadwalUtamaMkAdapter extends RecyclerView.Adapter<JadwalUtamaMkAdapter.JadwalUtamaViewHolder>{

    private static final int VIEW_TYPE_TODAY_JADWAL = 0;
    private static final int VIEW_TYPE_FUTURE_DAY_JADWAL = 1;

    private final Context mContext;

    private boolean mUseTodayLayout;

    private List<JadwalKelas> jadwalMataKuliahHariIni;

    final private JadwalUtamaMkAdapterOnClickHandler mClickHandler;

    public interface JadwalUtamaMkAdapterOnClickHandler {
        void onClick(String idListKelas);
    }

    public JadwalUtamaMkAdapter(Context context, JadwalUtamaMkAdapterOnClickHandler mClickHandler, List<JadwalKelas> jadwalMataKuliahHariIni) {
        this.mClickHandler = mClickHandler;
        this.jadwalMataKuliahHariIni = jadwalMataKuliahHariIni;
        this.mContext = context;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @Override
    public JadwalUtamaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new JadwalUtamaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JadwalUtamaViewHolder jadwalUtamaViewHolder, int position) {
        JadwalKelas currentJadwal = jadwalMataKuliahHariIni.get(position);
        String pertemuanKe = currentJadwal.getPertemuanKe();
        Log.d("pertemuan ke", pertemuanKe);
        String kelas = currentJadwal.getKelasMK();
        String ruangKuliah = currentJadwal.getRuangMK();
        String mataKuliah = currentJadwal.getNamaMK();
        String hariKuliah = null;
        String waktuKuliah = null;
        for(int i=0; i< currentJadwal.getKelasWaktu().size(); i++) {
            hariKuliah = currentJadwal.getKelasWaktu().get(i).getHariKelas();
            waktuKuliah = currentJadwal.getKelasWaktu().get(i).getWaktuMulai() +
                    " - " + currentJadwal.getKelasWaktu().get(i).getWaktuSelesai();
            if (i+1 < currentJadwal.getKelasWaktu().size()) {
                hariKuliah += "\n";
                waktuKuliah += "\n";
            }
        }
        String tanggalPertemuan = hariKuliah + ", " + currentJadwal.getTanggalPertemuan();

        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_TODAY_JADWAL:
                jadwalUtamaViewHolder.tvPertemuanKe.setText(pertemuanKe);
                jadwalUtamaViewHolder.tvKelas.setText(kelas);
                break;
            case VIEW_TYPE_FUTURE_DAY_JADWAL:
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        jadwalUtamaViewHolder.tvTanggalPertemuan.setText(tanggalPertemuan);
        jadwalUtamaViewHolder.tvRuangKuliah.setText(ruangKuliah);
        jadwalUtamaViewHolder.tvWaktuKuliah.setText(waktuKuliah);
        jadwalUtamaViewHolder.tvMataKuliah.setText(mataKuliah);
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
        return jadwalMataKuliahHariIni.size();
    }

    class JadwalUtamaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTanggalPertemuan;
        private TextView tvPertemuanKe;
        private TextView tvKelas;
        private TextView tvRuangKuliah;
        private TextView tvWaktuKuliah;
        private TextView tvMataKuliah;

        JadwalUtamaViewHolder(View itemView) {
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
            mClickHandler.onClick(jadwalMataKuliahHariIni.get(adapterPosition).getIdListJadwalMK());
        }
    }

    public void clear() {
        jadwalMataKuliahHariIni.clear();
        notifyDataSetChanged();
    }
}
