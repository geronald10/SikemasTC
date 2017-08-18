package id.ac.its.sikemastc.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class SikemasContract {

    public static final String CONTENT_AUTHORITY = "id.ac.its.sikemastc";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String DATABASE_NAME = "sikemas.db";
    public static final String PATH_USER = "user";
    public static final String PATH_PERKULIAHAN = "perkuliahan";
    public static final String PATH_KELAS = "kelas";
    public static final String PATH_KEHADIRAN = "kehadiran";
    public static final String PATH_PESERTA = "peserta";
    public static final String PATH_PERTEMUAN = "pertemuan";
    public static final String PATH_DOSEN = "dosen";

    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER)
                .build();

        public static final String TABLE_NAME = "user";
        public static final String KEY_USER_NAME = "nama";
        public static final String KEY_USER_EMAIL = "email";
        public static final String KEY_USER_ID = "user_id";
        public static final String KEY_USER_ROLE = "role";
        public static final String KEY_KODE_DOSEN = "kode_dosen";
        public static final String KEY_USER_PASSWORD = "password";
    }

    public static final class PerkuliahanEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PERKULIAHAN)
                .build();

        public static final String TABLE_NAME = "perkuliahan";
        public static final String KEY_ID_PERKULIAHAN = "perkuliahan_id";
        public static final String KEY_ID_KELAS = "id_kelas";
        public static final String KEY_STATUS_DOSEN = "status_dosen";
        public static final String KEY_STATUS_PERKULIAHAN = "status_perkuliahan";
        public static final String KEY_PERTEMUAN_KE = "pertemuan";
        public static final String KEY_TANGGAL_PERKULIAHAN = "tanggal";
        public static final String KEY_KODE_SEMESTER = "kode_semester";
        public static final String KEY_KODE_MK = "kode_matakuliah";
        public static final String KEY_NAMA_MK = "nama_kelas";
        public static final String KEY_KODE_KELAS = "kode_kelas";
        public static final String KEY_HARI = "hari";
        public static final String KEY_MULAI = "mulai";
        public static final String KEY_SELESAI = "selesai";
        public static final String KEY_NAMA_RUANGAN = "nama";
        public static final String KEY_JUMLAH_PESERTA = "jumlah_peserta";

        public static Uri buildPerkuliahanUriPerkuliahanId(String perkuliahanId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(perkuliahanId)
                    .build();
        }

        public static Uri buildPerkuliahanUriKelasId(String kelasId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(kelasId)
                    .build();
        }
    }

    public static final class KelasEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_KELAS)
                .build();

        public static final String TABLE_NAME = "kelas";
        public static final String KEY_ID_KELAS = "id";
        public static final String KEY_KODE_SEMESTER = "kode_semester";
        public static final String KEY_KODE_KELAS = "kode_kelas";
        public static final String KEY_KODE_MK = "kode_matakuliah";
        public static final String KEY_NAMA_MK = "nama_kelas";
        public static final String KEY_NAMA_RUANGAN = "nama_ruangan";
        public static final String KEY_HARI = "hari";
        public static final String KEY_MULAI = "mulai";
        public static final String KEY_SELESAI = "selesai";

        public static Uri buildKelasUriKelasId(String kelasId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(kelasId)
                    .build();
        }
    }

    public static final class KehadiranEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_KEHADIRAN)
                .build();

        public static final String TABLE_NAME = "kehadiran";
        public static final String KEY_ID_PERKULIAHAN_MAHASISWA = "id_perkuliahanmahasiswa";
        public static final String KEY_ID_KELAS_MAHASISWA = "id_kelas";
        public static final String KEY_ID_MAHASISWA = "id_mahasiswa";
        public static final String KEY_PERTEMUAN_KE = "pertemuan";
        public static final String KEY_TANGGAL_PERTEMUAN = "tanggal_pertemuan";
        public static final String KEY_NAMA_MAHASISWA = "nama";
        public static final String KEY_NRP_MAHASISWA = "nrp";
        public static final String KEY_WAKTU_CHEKIN = "waktu_checkin";
        public static final String KEY_TEMPAT_CHECKIN = "tempat_checkin";
        public static final String KEY_STATUS_KEHADIRAN = "ket_kehadiran";
        public static final String KEY_KET_KEHADIRAN = "keterangan";

        public static Uri buildKehadiranUriPerkuliahanId(String perkuliahanId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(perkuliahanId)
                    .build();
        }

        public static Uri buildKehadiranUriKelasId(String kelasId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(kelasId)
                    .build();
        }
    }

    public static final class PesertaEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PESERTA)
                .build();

        public static final String TABLE_NAME = "peserta";
        public static final String KEY_ID_KELAS = "id_kelas";
        public static final String KEY_ID_MAHASISWA = "id_mahasiswa";
        public static final String KEY_NRP_MAHASISWA = "nrp";
        public static final String KEY_NAMA_MAHASISWA = "nama";
        public static final String KEY_EMAIL_MAHASISWA = "email";
        public static final String KEY_COUNT_HADIR = "masuk";
        public static final String KEY_COUNT_IJIN = "ijin";
        public static final String KEY_COUNT_SAKIT = "sakit";
        public static final String KEY_COUNT_ABSEN = "absen";
    }

    public static final class PertemuanEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PERTEMUAN)
                .build();

        public static final String TABLE_NAME = "pertemuan";
        public static final String KEY_ID_PERTEMUAN = "id";
        public static final String KEY_ID_KELAS = "id_kelas";
        public static final String KEY_PERTEMUAN_KE = "pertemuan";
        public static final String KEY_STATUS_PERKULIAHAN = "status_perkuliahan";
        public static final String KEY_STATUS_DOSEN = "status_dosen";
        public static final String KEY_BERITA_ACARA = "berita_acara";
        public static final String KEY_TANGGAL = "tanggal";
        public static final String KEY_HARI = "hari";
        public static final String KEY_MULAI = "mulai";
        public static final String KEY_SELESAI = "selesai";

        public static Uri buildPertemuanUriPertemuanId(String pertemuanId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(pertemuanId)
                    .build();
        }
    }

    public static final class DosenEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_DOSEN)
                .build();

        public static final String TABLE_NAME = "dosen";
        public static final String KEY_ID_DOSEN = "id";
        public static final String KEY_ID_KELAS_DOSEN = "id_kelas";
        public static final String KEY_NAMA_DOSEN = "nama";
        public static final String KEY_KODE_DOSEN = "kode_dosen";
        public static final String KEY_EMAIL_DOSEN = "email";

        public static Uri buildDosenUriKelasId(String kelasId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(kelasId)
                    .build();
        }
    }
}
