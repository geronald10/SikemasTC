package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.model.Perkuliahan;

public class PerkuliahanAktifAdapter extends RecyclerView.Adapter<PerkuliahanAktifAdapter.PerkuliahanAktifViewHolder> {

    private final Context mContext;
    private List<Perkuliahan> perkuliahanAktifList;

    // handle clicks on items
    final private PerkuliahanAktifOnClickHandler mClickHandler;

    // interface that receives onClick message
    public interface PerkuliahanAktifOnClickHandler {
        void onClick(int buttonId, String idPerkuliahan);
    }

    public PerkuliahanAktifAdapter(Context context, List<Perkuliahan> perkuliahanList,
                                   PerkuliahanAktifOnClickHandler clickHandler) {
        mContext = context;
        perkuliahanAktifList = perkuliahanList;
        mClickHandler = clickHandler;
    }

    @Override
    public PerkuliahanAktifViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_current_class,
                viewGroup, false);
        view.setFocusable(true);

        return new PerkuliahanAktifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PerkuliahanAktifViewHolder holder, int position) {
        Perkuliahan currentPerkuliahan = perkuliahanAktifList.get(position);

        holder.tvPertemuanKe.setText(currentPerkuliahan.getPertemuanKe());
        holder.tvKelas.setText(currentPerkuliahan.getKelasMk());
        holder.tvRuangKuliah.setText(currentPerkuliahan.getRuangMk());
        holder.tvWaktuMulaiKuliah.setText(currentPerkuliahan.getWaktuMulai());
        holder.tvWaktuSelesaiKuliah.setText(currentPerkuliahan.getWaktuSelesai());
        holder.tvMataKuliah.setText(currentPerkuliahan.getNamaMk());
        holder.tvKodeMataKuliah.setText(currentPerkuliahan.getKodeMk());
    }

    @Override
    public int getItemCount() {
        return perkuliahanAktifList.size();
    }

    public class PerkuliahanAktifViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvPertemuanKe;
        private TextView tvKelas;
        private TextView tvRuangKuliah;
        private TextView tvWaktuMulaiKuliah;
        private TextView tvWaktuSelesaiKuliah;
        private TextView tvMataKuliah;
        private TextView tvKodeMataKuliah;
        private Button btnTandaTangan;
        private Button btnPencocokanWajah;

        public PerkuliahanAktifViewHolder(View itemView) {
            super(itemView);

            tvPertemuanKe = (TextView) itemView.findViewById(R.id.tv_pertemuan_ke);
            tvKelas = (TextView) itemView.findViewById(R.id.tv_kelas);
            tvRuangKuliah = (TextView) itemView.findViewById(R.id.tv_ruang);
            tvWaktuMulaiKuliah = (TextView) itemView.findViewById(R.id.tv_waktu_mulai);
            tvWaktuSelesaiKuliah = (TextView) itemView.findViewById(R.id.tv_waktu_selesai);
            tvKodeMataKuliah = (TextView) itemView.findViewById(R.id.tv_kode_mata_kuliah);
            tvMataKuliah = (TextView) itemView.findViewById(R.id.tv_mata_kuliah);
            btnTandaTangan = (Button) itemView.findViewById(R.id.btn_verifikasi_tandatangan);
            btnPencocokanWajah = (Button) itemView.findViewById(R.id.btn_verifikasi_wajah);

            btnTandaTangan.setOnClickListener(this);
            btnPencocokanWajah.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            switch (v.getId()) {
                case R.id.btn_verifikasi_tandatangan:
                    mClickHandler.onClick(R.id.btn_verifikasi_tandatangan,
                            perkuliahanAktifList.get(adapterPosition).getIdPerkuliahan());
                    break;
                case R.id.btn_verifikasi_wajah:
                    mClickHandler.onClick(R.id.btn_verifikasi_wajah,
                            perkuliahanAktifList.get(adapterPosition).getIdPerkuliahan());
                    break;
                default:
                    break;
            }
        }
    }

    public void clear() {
        perkuliahanAktifList.clear();
        notifyDataSetChanged();
    }
}