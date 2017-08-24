package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.PertemuanKelasFragment;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;

public class PertemuanAdapter extends RecyclerView.Adapter<PertemuanAdapter.PertemuanAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    // handle clicks on items
    final private PertemuanAdapterOnClickHandler mClickHandler;

    // interface that receives onClick message
    public interface PertemuanAdapterOnClickHandler {
        void onClick(String itemId, String idPertemuan, String idKelas, String pertemuanKe);
    }

    public PertemuanAdapter(@NonNull Context context, PertemuanAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public PertemuanAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.list_item_pertemuan, viewGroup, false);
        view.setFocusable(true);

        return new PertemuanAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PertemuanAdapterViewHolder pertemuanAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        Log.d("id Pertemuan", mCursor.getString(PertemuanKelasFragment.INDEX_ID_PERTEMUAN));
        String statusDosen = mCursor.getString(PertemuanKelasFragment.INDEX_STATUS_DOSEN);
        String pertemuanKe = mCursor.getString(PertemuanKelasFragment.INDEX_PERTEMUAN_KE);
        String tanggal = SikemasDateUtils.formatDate(mCursor.getString(PertemuanKelasFragment.INDEX_TANGGAL));
        String mulai = SikemasDateUtils.formatTime(mCursor.getString(PertemuanKelasFragment.INDEX_MULAI));
        String selesai = SikemasDateUtils.formatTime(mCursor.getString(PertemuanKelasFragment.INDEX_SELESAI));
        String waktuPertemuan = "mulai: " + mulai + "\n" + "selesai: " + selesai;
        String beritaAcara = mCursor.getString(PertemuanKelasFragment.INDEX_BERITA_ACARA);

        switch (statusDosen) {
            case "1":
                pertemuanAdapterViewHolder.ivStatusLabel.setImageResource(R.color.colorStatusHadir);
                pertemuanAdapterViewHolder.ivStatusKehadiran1.setImageResource(R.color.colorStatusHadir);
                pertemuanAdapterViewHolder.tvStatusPertemuan.setText(R.string.status_aktif);
                pertemuanAdapterViewHolder.clPenjadwalanUlang.setVisibility(View.GONE);
                pertemuanAdapterViewHolder.clBeritaAcara.setVisibility(View.GONE);
                pertemuanAdapterViewHolder.clBeritaAcaraContent.setVisibility(View.GONE);
                break;
            case "2":
                pertemuanAdapterViewHolder.ivStatusLabel.setImageResource(R.color.colorStatusAbsen);
                pertemuanAdapterViewHolder.ivStatusKehadiran1.setImageResource(R.color.colorStatusAbsen);
                pertemuanAdapterViewHolder.tvStatusPertemuan.setText(R.string.status_selesai);
                pertemuanAdapterViewHolder.clPenjadwalanUlang.setVisibility(View.GONE);
                if (beritaAcara.equals("null")) {
                    pertemuanAdapterViewHolder.clBeritaAcara.setVisibility(View.VISIBLE);
                    pertemuanAdapterViewHolder.clBeritaAcaraContent.setVisibility(View.GONE);
                }
                else {
                    pertemuanAdapterViewHolder.clBeritaAcaraContent.setVisibility(View.VISIBLE);
                    pertemuanAdapterViewHolder.tvBeritaAcara.setText(beritaAcara);
                    pertemuanAdapterViewHolder.clBeritaAcara.setVisibility(View.GONE);
                }
                break;
            default:
                pertemuanAdapterViewHolder.ivStatusLabel.setImageResource(R.color.colorStatusIdle);
                pertemuanAdapterViewHolder.ivStatusKehadiran1.setImageResource(R.color.colorStatusIdle);
                pertemuanAdapterViewHolder.tvStatusPertemuan.setText(R.string.status_belum_aktif);
                pertemuanAdapterViewHolder.clPenjadwalanUlang.setVisibility(View.VISIBLE);
                pertemuanAdapterViewHolder.clBeritaAcara.setVisibility(View.GONE);
                pertemuanAdapterViewHolder.clBeritaAcaraContent.setVisibility(View.GONE);
                break;
        }
        pertemuanAdapterViewHolder.tvPertemuanKe.setText(pertemuanKe);
        pertemuanAdapterViewHolder.tvTanggalPertemuan.setText(String.valueOf(tanggal));
        pertemuanAdapterViewHolder.tvWaktuPertemuan.setText(waktuPertemuan);
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

    class PertemuanAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ConstraintLayout clPenjadwalanUlang;
        private ConstraintLayout clBeritaAcara;
        private ConstraintLayout clBeritaAcaraContent;
        private TextView tvPertemuanKe;
        private TextView tvTanggalPertemuan;
        private TextView tvWaktuPertemuan;
        private TextView tvStatusPertemuan;
        private TextView tvBeritaAcara;
        private ImageView ivStatusKehadiran1;
        private ImageView ivStatusLabel;
        private Button btnSementara;
        private Button btnTambahBerita;
        private Button btnUbahBerita;

        public PertemuanAdapterViewHolder(View itemView) {
            super(itemView);
            tvPertemuanKe = (TextView) itemView.findViewById(R.id.tv_pertemuan_kuliah);
            tvTanggalPertemuan = (TextView) itemView.findViewById(R.id.tv_tanggal_pertemuan);
            tvWaktuPertemuan = (TextView) itemView.findViewById(R.id.tv_waktu_pertemuan);
            tvStatusPertemuan = (TextView) itemView.findViewById(R.id.tv_status_pertemuan);
            ivStatusKehadiran1 = (ImageView) itemView.findViewById(R.id.iv_status_kehadiran);
            ivStatusLabel = (ImageView) itemView.findViewById(R.id.iv_label);
            btnSementara = (Button) itemView.findViewById(R.id.btn_ganti_jadwal_sementara);
            btnTambahBerita = (Button) itemView.findViewById(R.id.btn_tambah_berita_acara);
            clPenjadwalanUlang = (ConstraintLayout) itemView.findViewById(R.id.cl_penjadwalan_ulang);
            clBeritaAcara = (ConstraintLayout) itemView.findViewById(R.id.cl_tambah_berita_acara);
            clBeritaAcaraContent = (ConstraintLayout) itemView.findViewById(R.id.cl_berita_acara);
            tvBeritaAcara = (TextView) itemView.findViewById(R.id.tv_berita_acara_content);
            btnUbahBerita = (Button) itemView.findViewById(R.id.btn_ubah_berita_acara);

            btnSementara.setOnClickListener(this);
            btnTambahBerita.setOnClickListener(this);
            btnUbahBerita.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String idPertemuan = mCursor.getString(PertemuanKelasFragment.INDEX_ID_PERTEMUAN);
            String idKelas = mCursor.getString(PertemuanKelasFragment.INDEX_ID_KELAS);
            String pertemuanKe = mCursor.getString(PertemuanKelasFragment.INDEX_PERTEMUAN_KE);
            switch (v.getId()) {
                case R.id.btn_ganti_jadwal_sementara:
                    mClickHandler.onClick("1", idPertemuan, null, pertemuanKe);
                    break;
                case R.id.btn_tambah_berita_acara:
                    mClickHandler.onClick("3", idPertemuan, null, pertemuanKe);
                    break;
                case R.id.btn_ubah_berita_acara:
                    mClickHandler.onClick("4", idPertemuan, null, pertemuanKe);
                    break;
                default:
                    mClickHandler.onClick("0", idPertemuan, idKelas, pertemuanKe);
                    break;
            }
        }
    }
}
