package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.model.PerkuliahanMahasiswa;

public class PerkuliahanAktifMahasiswaAdapter extends RecyclerView.Adapter<PerkuliahanAktifMahasiswaAdapter.PerkuliahanAktifMahasiswaViewHolder> {

    private final Context mContext;
    private List<PerkuliahanMahasiswa> perkuliahanAktifMahasiswaList;

    // handle clicks on items
    final private PerkuliahanAktifMahasiswaOnClickHandler mClickHandler;

    // interface that receives onClick message
    public interface  PerkuliahanAktifMahasiswaOnClickHandler {
        void onClick(int buttonId, String idPerkuliahan);
    }

    public PerkuliahanAktifMahasiswaAdapter(Context context, List<PerkuliahanMahasiswa> perkuliahanMahasiswaList,
                                            PerkuliahanAktifMahasiswaOnClickHandler clickHandler) {
        mContext = context;
        perkuliahanAktifMahasiswaList = perkuliahanMahasiswaList;
        mClickHandler = clickHandler;
    }

    @Override
    public PerkuliahanAktifMahasiswaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_current_class,
                viewGroup, false);
        view.setFocusable(true);

        return new PerkuliahanAktifMahasiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PerkuliahanAktifMahasiswaViewHolder holder, int position) {
        PerkuliahanMahasiswa currentPerkuliahan = perkuliahanAktifMahasiswaList.get(position);

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
        return perkuliahanAktifMahasiswaList.size();
    }

    public class PerkuliahanAktifMahasiswaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvPertemuanKe;
        private TextView tvKelas;
        private TextView tvRuangKuliah;
        private TextView tvWaktuMulaiKuliah;
        private TextView tvWaktuSelesaiKuliah;
        private TextView tvMataKuliah;
        private TextView tvKodeMataKuliah;
        private Button btnTandaTangan;
        private Button btnPencocokanWajah;

        public PerkuliahanAktifMahasiswaViewHolder(View itemView) {
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
                    mClickHandler.onClick(R.id.btn_verifikasi_tandatangan, perkuliahanAktifMahasiswaList.get(adapterPosition).getIdPerkuliahan());
                    break;
                case R.id.btn_verifikasi_wajah:
                    mClickHandler.onClick(R.id.btn_verifikasi_wajah, perkuliahanAktifMahasiswaList.get(adapterPosition).getIdPerkuliahan());
                    break;
                default:
                    break;
            }
        }
    }

    public void clear() {
        perkuliahanAktifMahasiswaList.clear();
        notifyDataSetChanged();
    }
}
