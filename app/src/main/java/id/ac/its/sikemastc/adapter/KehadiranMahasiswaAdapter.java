package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.mahasiswa.DetailJadwalKelasMahasiswa;
import id.ac.its.sikemastc.model.RekapKehadiran;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;

public class KehadiranMahasiswaAdapter extends RecyclerView.Adapter<KehadiranMahasiswaAdapter.KehadiranMahasiswaViewHolder> {

    private final Context mContext;
    private List<RekapKehadiran> mRekapKehadiranList;

    public KehadiranMahasiswaAdapter(Context context, List<RekapKehadiran> rekapKehadiranList) {
        mContext = context;
        mRekapKehadiranList = rekapKehadiranList;
    }

    @Override
    public KehadiranMahasiswaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.list_item_riwayat_kehadiran_mahasiswa, viewGroup, false);
        view.setFocusable(true);

        return new KehadiranMahasiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KehadiranMahasiswaViewHolder kehadiranMahasiswaViewHolder, int position) {
        RekapKehadiran currentPertemuan = mRekapKehadiranList.get(position);

//        mCursor.moveToFirst();
//
//        String pertemuanKe = mCursor.getString(DetailJadwalKelasMahasiswa.INDEX_PERTEMUAN_KE);
//        String tanggalPertemuan = SikemasDateUtils
//                .formatDate(mCursor.getString(DetailJadwalKelasMahasiswa.INDEX_TANGGAL_PERTEMUAN));
//        String checkinTime = SikemasDateUtils
//                .formatTime(mCursor.getString(DetailJadwalKelasMahasiswa.INDEX_WAKTU_CHECKIN));
//        String checkinPlace = mCursor.getString(DetailJadwalKelasMahasiswa.INDEX_TEMPAT_CHECKIN);
//        String statusKehadiran = mCursor.getString(DetailJadwalKelasMahasiswa.INDEX_STATUS_KEHADIRAN);
//        String ketKehadiran = mCursor.getString(DetailJadwalKelasMahasiswa.INDEX_KET_KEHADIRAN);
        String tanggalKehadiran = currentPertemuan.getTanggalKehadiran();
        String waktuKehadiran = currentPertemuan.getWaktuKehadiran();
        String tempatKehadiran = currentPertemuan.getTempatKehadiran();
        kehadiranMahasiswaViewHolder.tvPertemuan.setText(currentPertemuan.getPertemuanKe());
        kehadiranMahasiswaViewHolder.tvTanggalPertemuan.setText(currentPertemuan.getTanggalKehadiran());

        if (tanggalKehadiran == null) {
            kehadiranMahasiswaViewHolder.tvTanggalPertemuan.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
            kehadiranMahasiswaViewHolder.ivTanggalPertemuan.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
        } else {

            kehadiranMahasiswaViewHolder.tvTanggalPertemuan.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorPrimaryText));
            kehadiranMahasiswaViewHolder.ivTanggalPertemuan.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorPrimaryText));
        }

        if (waktuKehadiran == null) {
            kehadiranMahasiswaViewHolder.tvCheckinTime.setText(R.string.tv_empty_value);
            kehadiranMahasiswaViewHolder.ivCheckinTime.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
            kehadiranMahasiswaViewHolder.tvCheckinTime.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
        } else {
            kehadiranMahasiswaViewHolder.tvCheckinTime.setText(currentPertemuan.getWaktuKehadiran());
            kehadiranMahasiswaViewHolder.ivCheckinTime.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorStatusHadir));
            kehadiranMahasiswaViewHolder.tvCheckinTime.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorStatusHadir));
        }
        if (tempatKehadiran == null) {
            kehadiranMahasiswaViewHolder.tvCheckinPlace.setText(R.string.tv_empty_value);
            kehadiranMahasiswaViewHolder.ivCheckinPlace.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
            kehadiranMahasiswaViewHolder.tvCheckinPlace.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
        } else {
            kehadiranMahasiswaViewHolder.tvCheckinPlace.setText(currentPertemuan.getWaktuKehadiran());
            kehadiranMahasiswaViewHolder.ivCheckinPlace.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorStatusHadir));
            kehadiranMahasiswaViewHolder.tvCheckinPlace.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorStatusHadir));
        }

        if (currentPertemuan.getPesanKehadiran().equals("null")) {
            kehadiranMahasiswaViewHolder.tvKeteranganKehadiran.setText(R.string.tv_empty_value);
            kehadiranMahasiswaViewHolder.tvKeteranganKehadiran.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
            kehadiranMahasiswaViewHolder.ivKeteranganKehadiran.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorStatusIdle));
        }
        else {
            kehadiranMahasiswaViewHolder.tvKeteranganKehadiran.setText(currentPertemuan.getPesanKehadiran());
            kehadiranMahasiswaViewHolder.tvKeteranganKehadiran.setTextColor(ContextCompat
                    .getColor(mContext, R.color.colorPrimaryText));
            kehadiranMahasiswaViewHolder.ivKeteranganKehadiran.setColorFilter(ContextCompat
                    .getColor(mContext, R.color.colorPrimaryText));
        }

        switch (currentPertemuan.getStatusKehadiran()) {
            case "M":
                kehadiranMahasiswaViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusHadir);
                kehadiranMahasiswaViewHolder.tvStatusKehadiran.setText(R.string.tv_status_hadir_label);
                break;
            case "I":
                kehadiranMahasiswaViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusIjin);
                kehadiranMahasiswaViewHolder.tvStatusKehadiran.setText(R.string.tv_status_ijin_label);
                break;
            case "A":
                kehadiranMahasiswaViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusAbsen);
                kehadiranMahasiswaViewHolder.tvStatusKehadiran.setText(R.string.tv_status_absen_label);
                break;
            default:
                kehadiranMahasiswaViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusIdle);
                kehadiranMahasiswaViewHolder.tvStatusKehadiran.setText(R.string.tv_empty_value);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mRekapKehadiranList.size();
    }

    public class KehadiranMahasiswaViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPertemuan;
        private TextView tvTanggalPertemuan;
        private TextView tvCheckinTime;
        private TextView tvCheckinPlace;
        private TextView tvStatusKehadiran;
        private TextView tvKeteranganKehadiran;
        private ImageView ivTanggalPertemuan;
        private ImageView ivKeteranganKehadiran;
        private ImageView ivStatusKehadiran;
        private ImageView ivCheckinTime;
        private ImageView ivCheckinPlace;

        public KehadiranMahasiswaViewHolder(View itemView) {
            super(itemView);

            tvPertemuan = (TextView) itemView.findViewById(R.id.tv_pertemuan_kuliah);
            tvTanggalPertemuan = (TextView) itemView.findViewById(R.id.tv_tanggal_pertemuan);
            tvCheckinTime = (TextView) itemView.findViewById(R.id.tv_check_in);
            tvCheckinPlace = (TextView) itemView.findViewById(R.id.tv_check_in_place);
            tvStatusKehadiran = (TextView) itemView.findViewById(R.id.tv_status_kehadiran);
            tvKeteranganKehadiran = (TextView) itemView.findViewById(R.id.tv_keterangan_status);
            ivTanggalPertemuan = (ImageView) itemView.findViewById(R.id.iv_date);
            ivKeteranganKehadiran = (ImageView) itemView.findViewById(R.id.iv_keterangan_status);
            ivStatusKehadiran = (ImageView) itemView.findViewById(R.id.iv_status_kehadiran);
            ivCheckinTime = (ImageView) itemView.findViewById(R.id.iv_check_in);
            ivCheckinPlace = (ImageView) itemView.findViewById(R.id.iv_check_in_place);
        }
    }

    public void clear() {
        mRekapKehadiranList.clear();
        notifyDataSetChanged();
    }
}
