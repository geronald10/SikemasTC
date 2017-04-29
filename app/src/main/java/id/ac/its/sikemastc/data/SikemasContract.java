package id.ac.its.sikemastc.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class SikemasContract {

    public static final String CONTENT_AUTHORITY = "id.ac.its.sikemastc";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USER = "user";
    public static final String PATH_JADWAL_MK = "jadwal_mk";

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

    public static final class JadwalMKEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_JADWAL_MK)
                .build();

        public static final String TABLE_NAME = "jadwal_mk";
        public static final String KEY_KODE_SEMESTER = "kode_semester";
        public static final String KEY_KODE_MK = "kode_matakuliah";
        public static final String KEY_NAMA_MK = "nama_kelas";
        public static final String KEY_NAMA_RUANGAN = "nama_ruangan";
        public static final String KEY_HARI = "hari";
        public static final String KEY_MULAI = "mulai";
        public static final String KEY_SELESAI = "selesai";
    }
}
