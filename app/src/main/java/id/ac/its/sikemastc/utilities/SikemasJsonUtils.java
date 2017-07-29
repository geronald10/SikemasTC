package id.ac.its.sikemastc.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.ac.its.sikemastc.data.SikemasContract;

public class SikemasJsonUtils {

    public static ContentValues[] getListKelasDosenContentValuesFromJson(Context context,
        String listKelasDosenJsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(listKelasDosenJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        Log.d("test leng listkelas", String.valueOf(listKelas.length()));
        ContentValues[] kelasDosenContentValues = new ContentValues[listKelas.length()];

        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            String idListJadwal = detailKelas.getString("id");
            String kodeSemester = detailKelas.getString("kode_semester");
            String kodeMk = detailKelas.getString("kode_matakuliah");
            String namaMk = detailKelas.getString("nama_kelas");
            String kelasMk = detailKelas.getString("kode_kelas");
            String ruangMK = detailKelas.getString("nama_ruangan");

//                // handle "peserta kelas"
//                JSONArray listPeserta = detailKelas.getJSONArray("peserta");
//                List<PesertaPerkuliahan> listPesertaKelas = new ArrayList<>();
//                for (int j = 0; j < listPeserta.length(); j++) {
//                    JSONObject detailPeserta = listPeserta.getJSONObject(j);
//                    PesertaPerkuliahan pesertaKelas = new PesertaPerkuliahan(detailPeserta.getString("nrp"),
//                            detailPeserta.getString("nama"));
//                    listPesertaKelas.add(pesertaKelas);
//                }

            // handle "kelaswaktu"
            String hari = null, mulai = null, selesai = null;
            JSONArray listWaktu = detailKelas.getJSONArray("kelaswaktu");
            for (int k = 0; k < listWaktu.length(); k++) {
                JSONObject detailWaktu = listWaktu.getJSONObject(k);
                hari = detailWaktu.getString("hari");
                mulai = detailWaktu.getString("mulai");
                selesai = detailWaktu.getString("selesai");
                if (k + 1 < listWaktu.length()) {
                    hari += "\\n";
                    mulai += "\\n";
                    selesai += "\\n";
                }
            }
//
//                // handle "dosenkelas"
//                JSONArray listDosen = detailKelas.getJSONArray("dosenkelas");
//                List<DosenKelas> listDosenKelas = new ArrayList<>();
//                for (int l = 0; l < listDosen.length(); l++) {
//                    JSONObject detailDosen = listDosen.getJSONObject(l);
//                    DosenKelas dosenKelas = new DosenKelas(detailDosen.getString("id"),
//                            detailDosen.getString("nip"), detailDosen.getString("nama"),
//                            detailDosen.getString("kode_dosen"));
//                    listDosenKelas.add(dosenKelas);
//                }
//                // add to Jadwal Kelas Diampu List
//                JadwalKelas jadwalKelas = new JadwalKelas(idListJadwal, kodeMk, namaMk,
//                        kelasMk, ruangMK, listPesertaKelas, listWaktuKelas, listDosenKelas);
//                jadwalKelasList.add(jadwalKelas);
//                Toast.makeText(mContext, "Berhasil Memuat List Kelas Diampu", Toast.LENGTH_LONG).show();

            ContentValues kelasDosenValues = new ContentValues();
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_ID_KELAS, idListJadwal);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_KODE_SEMESTER, kodeSemester);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_KODE_MK, kodeMk);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_NAMA_MK, namaMk);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_KODE_KELAS, kelasMk);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_NAMA_RUANGAN, ruangMK);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_HARI, hari);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_MULAI, mulai);
            kelasDosenValues.put(SikemasContract.KelasEntry.KEY_SELESAI, selesai);

            kelasDosenContentValues[i] = kelasDosenValues;
        }
        return kelasDosenContentValues;
    }

    public static ContentValues[] getListPerkuliahanDosenContentValuesFromJson(Context context,
        String listPerkuliahanDosenJsonStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(listPerkuliahanDosenJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        Log.d("test leng listkelas", String.valueOf(listKelas.length()));
        ContentValues[] perkuliahanDosenContentValues = new ContentValues[listKelas.length()];

        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            String idListJadwal = detailKelas.getString("id");
            String statusPerkuliahan = detailKelas.getString("status_perkuliahan");
            String idKelas = detailKelas.getString("id_kelas");
            String hariPerkuliahan = detailKelas.getString("hari");
            String tanggalPerkuliahan = detailKelas.getString("tanggal");
            String perkuliahanKe = detailKelas.getString("pertemuan");
            String statusDosen = detailKelas.getString("status_dosen");
            String waktuMulai = detailKelas.getString("mulai");
            String waktuSelesai = detailKelas.getString("selesai");

            JSONObject kelas = detailKelas.getJSONObject("kelas");
            String kodeSemester = kelas.getString("kode_semester");
            String kodeMk = kelas.getString("kode_matakuliah");
            String kelasMk = kelas.getString("kode_kelas");
            String namaMk = kelas.getString("nama_kelas");
            String ruangMK = kelas.getString("nama_ruangan");

            ContentValues perkuliahanDosenValue = new ContentValues();
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN, idListJadwal);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_STATUS_PERKULIAHAN, statusPerkuliahan);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_ID_KELAS, idKelas);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_HARI, hariPerkuliahan);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_TANGGAL_PERKULIAHAN, tanggalPerkuliahan);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_PERTEMUAN_KE, perkuliahanKe);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_STATUS_DOSEN, statusDosen);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_MULAI, waktuMulai);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_SELESAI, waktuSelesai);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_NAMA_MK, namaMk);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_KODE_KELAS, kelasMk);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_NAMA_RUANGAN, ruangMK);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_KODE_SEMESTER, kodeSemester);
            perkuliahanDosenValue.put(SikemasContract.PerkuliahanEntry.KEY_KODE_MK, kodeMk);

            Log.d("idListJadwal", idListJadwal);
            Log.d("hariperkuliahan", hariPerkuliahan);
            Log.d("tanggalperkuliahan", tanggalPerkuliahan);
            Log.d("perkuliahanke", perkuliahanKe);
            Log.d("statusperkuliahan", statusDosen);
            Log.d("mulai", waktuMulai);
            Log.d("selesai", waktuSelesai);
            Log.d("nama", namaMk);
            Log.d("kelas", kelasMk);
            Log.d("ruang", ruangMK);
            Log.d("semester", kodeSemester);
            Log.d("kodeMK", kodeMk);

            perkuliahanDosenContentValues[i] = perkuliahanDosenValue;
        }
        return perkuliahanDosenContentValues;
    }

    public static ContentValues[] getListKehadiranPesertaContentValuesFromJson(Context context,
        String listPerkuliahanDosenJsonStr) throws JSONException {

        int totalPeserta = 0;
        JSONObject jsonObject = new JSONObject(listPerkuliahanDosenJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            JSONArray kehadiran = detailKelas.getJSONArray("kehadiran");
            totalPeserta += kehadiran.length();
        }
        Log.d("test length listkelas", String.valueOf(listKelas.length()));
        ContentValues[] kehadiranPesertaContentValues = new ContentValues[totalPeserta];

        int index = 0;
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            JSONArray kehadiran = detailKelas.getJSONArray("kehadiran");
            String idMahasiswa, nrpMahasiswa, namaMahasiswa, idPerkuliahanMahasiswa, ketKehadiran,
            waktuKehadiran;
            for (int j = 0; j < kehadiran.length(); j++) {
                JSONObject peserta = kehadiran.getJSONObject(j);
                nrpMahasiswa = peserta.getString("nrp");
                namaMahasiswa = peserta.getString("nama");
                JSONObject pivot = peserta.getJSONObject("pivot");
                idPerkuliahanMahasiswa = pivot.getString("id_perkuliahanmahasiswa");
                idMahasiswa = pivot.getString("id_mahasiswa");
                ketKehadiran = pivot.getString("ket_kehadiran");
                waktuKehadiran = pivot.getString("updated_at");
                if (waktuKehadiran != "null")
                    waktuKehadiran = SikemasDateUtils.formatTimeFromTimestamp(waktuKehadiran);

                ContentValues kehadiranPesertaValue = new ContentValues();
                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_NRP_MAHASISWA, nrpMahasiswa);
                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_NAMA_MAHASISWA, namaMahasiswa);
                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA, idPerkuliahanMahasiswa);
                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_ID_MAHASISWA, idMahasiswa);
                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_STATUS_KEHADIRAN, ketKehadiran);
                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_WAKTU_CHEKIN, waktuKehadiran);

                kehadiranPesertaContentValues[index] = kehadiranPesertaValue;
                index++;
            }
        }
        return kehadiranPesertaContentValues;
    }

    public static ContentValues[] getListKelasPertemuanDosenContentValuesFromJson(Context context,
        String listKelasDosenPertemuanJsonStr) throws JSONException {

        int totalPertemuan = 0;
        JSONObject jsonObject = new JSONObject(listKelasDosenPertemuanJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            JSONArray perkuliahanMahasiswa = detailKelas.getJSONArray("perkuliahanmahasiswa");
            for (int j = 0; j < perkuliahanMahasiswa.length(); j++)
                totalPertemuan = listKelas.length() * perkuliahanMahasiswa.length();

        }
        Log.d("totalPertemuan", String.valueOf(totalPertemuan));
        ContentValues[] kelasDosenPertemuanContentValues = new ContentValues[totalPertemuan];

        int index = 0;
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            // handle "perkuliahanmahasiswa"
            JSONArray perkuliahanMahasiswa = detailKelas.getJSONArray("perkuliahanmahasiswa");
            for (int j = 0; j < perkuliahanMahasiswa.length(); j++) {
                JSONObject detailPerkuliahan = perkuliahanMahasiswa.getJSONObject(j);
                String idPertemuan = detailPerkuliahan.getString("id");
                String pertemuanKe = detailPerkuliahan.getString("pertemuan");
                String statusPerkuliahan = detailPerkuliahan.getString("status_perkuliahan");
                String statusDosen = detailPerkuliahan.getString("status_dosen");
                String idKelas = detailPerkuliahan.getString("id_kelas");
                String tanggal = detailPerkuliahan.getString("tanggal");
                String hari = detailPerkuliahan.getString("hari");
                String mulai = detailPerkuliahan.getString("mulai");
                String selesai = detailPerkuliahan.getString("selesai");

                ContentValues kelasDosenPertemuanValues = new ContentValues();
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_ID_PERTEMUAN, idPertemuan);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_ID_KELAS, idKelas);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_PERTEMUAN_KE, pertemuanKe);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_STATUS_PERKULIAHAN, statusPerkuliahan);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_STATUS_DOSEN, statusDosen);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_TANGGAL, tanggal);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_HARI, hari);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_MULAI, mulai);
                kelasDosenPertemuanValues.put(SikemasContract.PertemuanEntry.KEY_SELESAI, selesai);

                kelasDosenPertemuanContentValues[index] = kelasDosenPertemuanValues;
                index++;
            }
        }
        return kelasDosenPertemuanContentValues;
    }

    public static ContentValues[] getListPesertaDosenContentValuesFromJson(Context context,
        String listKelasDosenPesertaJsonStr) throws JSONException {

        int totalPeserta = 0;
        JSONObject jsonObject = new JSONObject(listKelasDosenPesertaJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            JSONArray kelasPeserta = detailKelas.getJSONArray("peserta");
            totalPeserta += kelasPeserta.length();
        }
        Log.d("totalPeserta", String.valueOf(totalPeserta));
        ContentValues[] kelasDosenPesertaContentValues = new ContentValues[totalPeserta];

        int index = 0;
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            // handle "peserta"
            JSONArray kelasPeserta = detailKelas.getJSONArray("peserta");
            for (int j = 0; j < kelasPeserta.length(); j++) {
                JSONObject detailPeserta = kelasPeserta.getJSONObject(j);
                String nrpPeserta = detailPeserta.getString("nrp");
                String namaPeserta = detailPeserta.getString("nama");
                String emailPeserta = detailPeserta.getString("email");

                JSONObject pivot = detailPeserta.getJSONObject("pivot");
                String idKelas = pivot.getString("id_kelas");
                String idPeserta = pivot.getString("id_mahasiswa");

                ContentValues kelasDosenPesertaValues = new ContentValues();
                kelasDosenPesertaValues.put(SikemasContract.PesertaEntry.KEY_ID_KELAS, idKelas);
                kelasDosenPesertaValues.put(SikemasContract.PesertaEntry.KEY_ID_MAHASISWA, idPeserta);
                kelasDosenPesertaValues.put(SikemasContract.PesertaEntry.KEY_NRP_MAHASISWA, nrpPeserta);
                kelasDosenPesertaValues.put(SikemasContract.PesertaEntry.KEY_NAMA_MAHASISWA, namaPeserta);
                kelasDosenPesertaValues.put(SikemasContract.PesertaEntry.KEY_EMAIL_MAHASISWA, emailPeserta);

                kelasDosenPesertaContentValues[index] = kelasDosenPesertaValues;
                index++;
            }
        }
        return kelasDosenPesertaContentValues;
    }

    // Content values for mahasiswa
    public static ContentValues[] getListKelasMahasiswaContentValuesFromJson(Context context,
        String listKelasMahasiswaJsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(listKelasMahasiswaJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        Log.d("test leng listkelas", String.valueOf(listKelas.length()));
        ContentValues[] kelasMahasiswaContentValues = new ContentValues[listKelas.length()];

        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            String idListJadwal = detailKelas.getString("id");
            String kodeSemester = detailKelas.getString("kode_semester");
            String kodeMk = detailKelas.getString("kode_matakuliah");
            String namaMk = detailKelas.getString("nama_kelas");
            String kelasMk = detailKelas.getString("kode_kelas");
            String ruangMK = detailKelas.getString("nama_ruangan");

            // handle "kelaswaktu"
            String hari = null, mulai = null, selesai = null;
            JSONArray listWaktu = detailKelas.getJSONArray("kelaswaktu");
            for (int k = 0; k < listWaktu.length(); k++) {
                JSONObject detailWaktu = listWaktu.getJSONObject(k);
                hari = detailWaktu.getString("hari");
                mulai = detailWaktu.getString("mulai");
                selesai = detailWaktu.getString("selesai");
                if (k + 1 < listWaktu.length()) {
                    hari += "\\n";
                    mulai += "\\n";
                    selesai += "\\n";
                }
            }

            ContentValues kelasMahasiswaValues = new ContentValues();
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_ID_KELAS, idListJadwal);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_KODE_SEMESTER, kodeSemester);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_KODE_MK, kodeMk);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_NAMA_MK, namaMk);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_KODE_KELAS, kelasMk);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_NAMA_RUANGAN, ruangMK);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_HARI, hari);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_MULAI, mulai);
            kelasMahasiswaValues.put(SikemasContract.KelasEntry.KEY_SELESAI, selesai);

            kelasMahasiswaContentValues[i] = kelasMahasiswaValues;
        }
        return kelasMahasiswaContentValues;
    }

    public static ContentValues[] getListKelasMahasiswaDosenContentValuesFromJson(Context context,
        String listKelasMahasiswaDosenJsonStr) throws JSONException {
        int totalDosen = 0;
        JSONObject jsonObject = new JSONObject(listKelasMahasiswaDosenJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            JSONArray listDosenKelas = detailKelas.getJSONArray("dosenkelas");
            for (int j = 0; j < listDosenKelas.length(); j++) {
                totalDosen++;
            }
        }
        Log.d("test leng listkelas", String.valueOf(listKelas.length()));
        ContentValues[] dosenKelasContentValues = new ContentValues[totalDosen];

        int index = 0;
        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            // handle "dosenkelas"
            JSONArray listDosenKelas = detailKelas.getJSONArray("dosenkelas");
            for (int j = 0; j < listDosenKelas.length(); j++) {
                JSONObject detailDosenKelas = listDosenKelas.getJSONObject(j);
                String idDosen = detailDosenKelas.getString("id");
                String nama = detailDosenKelas.getString("nama");
                String email = detailDosenKelas.getString("email");
                String kodeDosen = detailDosenKelas.getString("kode_dosen");

                JSONObject pivot = detailDosenKelas.getJSONObject("pivot");
                String idKelasDosen = pivot.getString("id_kelas");

                ContentValues dosenKelasValues = new ContentValues();
                dosenKelasValues.put(SikemasContract.DosenEntry.KEY_ID_DOSEN, idDosen);
                dosenKelasValues.put(SikemasContract.DosenEntry.KEY_ID_KELAS_DOSEN, idKelasDosen);
                dosenKelasValues.put(SikemasContract.DosenEntry.KEY_KODE_DOSEN, kodeDosen);
                dosenKelasValues.put(SikemasContract.DosenEntry.KEY_NAMA_DOSEN, nama);
                dosenKelasValues.put(SikemasContract.DosenEntry.KEY_EMAIL_DOSEN, email);

                dosenKelasContentValues[index] = dosenKelasValues;
                index++;
            }
        }
        return dosenKelasContentValues;
    }

    public static ContentValues[] getListPerkuliahanMahasiswaContentValuesFromJson(Context context,
        String listPerkuliahanMahasiswaJsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(listPerkuliahanMahasiswaJsonStr);
        JSONArray listKelas = jsonObject.getJSONArray("listkelas");
        Log.d("listKelasMahasiswa", String.valueOf(listKelas.length()));
        ContentValues[] perkuliahanMahasiswaContentValues = new ContentValues[listKelas.length()];

        for (int i = 0; i < listKelas.length(); i++) {
            JSONObject detailKelas = listKelas.getJSONObject(i);
            String idListJadwal = detailKelas.getString("id");
            String idKelas = detailKelas.getString("id_kelas");
            String hariPerkuliahan = detailKelas.getString("hari");
            String tanggalPerkuliahan = detailKelas.getString("tanggal");
            String perkuliahanKe = detailKelas.getString("pertemuan");
            String statusDosen = detailKelas.getString("status_dosen");
            String statusPerkuliahan = detailKelas.getString("status_perkuliahan");
            String waktuMulai = detailKelas.getString("mulai");
            String waktuSelesai = detailKelas.getString("selesai");

            JSONObject kelas = detailKelas.getJSONObject("kelas");
            String kodeSemester = kelas.getString("kode_semester");
            String kodeMk = kelas.getString("kode_matakuliah");
            String kelasMk = kelas.getString("kode_kelas");
            String namaMk = kelas.getString("nama_kelas");
            String ruangMK = kelas.getString("nama_ruangan");

            ContentValues perkuliahanMahasiswaValue = new ContentValues();
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN, idListJadwal);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_ID_KELAS, idKelas);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_HARI, hariPerkuliahan);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_TANGGAL_PERKULIAHAN, tanggalPerkuliahan);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_PERTEMUAN_KE, perkuliahanKe);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_STATUS_DOSEN, statusDosen);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_STATUS_PERKULIAHAN, statusPerkuliahan);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_MULAI, waktuMulai);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_SELESAI, waktuSelesai);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_NAMA_MK, namaMk);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_KODE_KELAS, kelasMk);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_NAMA_RUANGAN, ruangMK);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_KODE_SEMESTER, kodeSemester);
            perkuliahanMahasiswaValue.put(SikemasContract.PerkuliahanEntry.KEY_KODE_MK, kodeMk);

            perkuliahanMahasiswaContentValues[i] = perkuliahanMahasiswaValue;
        }
        return perkuliahanMahasiswaContentValues;
    }

    public static ContentValues[] getRekapKehadiranMahasiswaContentValuesFromJson(Context context,
        String listRekapKehadiranMahasiswaJsonStr) throws JSONException {
            JSONObject jsonObject = new JSONObject(listRekapKehadiranMahasiswaJsonStr);
            JSONArray rekapAbsen = jsonObject.getJSONArray("rekapabsen");
            Log.d("rekapAbsen", String.valueOf(rekapAbsen.length()));
            ContentValues[] rekapAbsenMahasiswaContentValues = new ContentValues[rekapAbsen.length()];

            int index = 0;
            for (int i = 0; i < rekapAbsen.length(); i++) {
                JSONObject absenMahasiswa = rekapAbsen.getJSONObject(i);
                String waktuTimeStamp = absenMahasiswa.getString("updated_at");
                String tanggal = SikemasDateUtils.formatDateFromTimestamp(waktuTimeStamp);
                String waktu = SikemasDateUtils.formatTimeFromTimestamp(waktuTimeStamp);
                String statusHadir = absenMahasiswa.getString("ket_kehadiran");
                String pesanKehadiran = absenMahasiswa.getString("pesan");

                JSONArray perkuliahanMahasiswa = absenMahasiswa.getJSONArray("perkuliahanmahasiswa");
                for (int j = 0; j < perkuliahanMahasiswa.length(); j++) {
                    JSONObject perkuliahan = perkuliahanMahasiswa.getJSONObject(j);
                    String pertemuanKe = perkuliahan.getString("pertemuan");

                    ContentValues rekapKehadiranMahasiswaValue = new ContentValues();
                    rekapKehadiranMahasiswaValue.put(SikemasContract.KehadiranEntry.KEY_TANGGAL_PERTEMUAN, tanggal);
                    rekapKehadiranMahasiswaValue.put(SikemasContract.KehadiranEntry.KEY_WAKTU_CHEKIN, waktu);
                    rekapKehadiranMahasiswaValue.put(SikemasContract.KehadiranEntry.KEY_TEMPAT_CHECKIN, "null");
                    rekapKehadiranMahasiswaValue.put(SikemasContract.KehadiranEntry.KEY_STATUS_KEHADIRAN, statusHadir);
                    rekapKehadiranMahasiswaValue.put(SikemasContract.KehadiranEntry.KEY_PERTEMUAN_KE, pertemuanKe);
                    rekapKehadiranMahasiswaValue.put(SikemasContract.KehadiranEntry.KEY_KET_KEHADIRAN, pesanKehadiran);

                    rekapAbsenMahasiswaContentValues[i] = rekapKehadiranMahasiswaValue;
                }
                index++;
            }
            return rekapAbsenMahasiswaContentValues;
    }
}