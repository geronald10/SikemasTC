package id.ac.its.sikemastc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.ac.its.sikemastc.data.SikemasContract.UserEntry;
import id.ac.its.sikemastc.data.SikemasContract.KelasEntry;
import id.ac.its.sikemastc.data.SikemasContract.PerkuliahanEntry;
import id.ac.its.sikemastc.data.SikemasContract.KehadiranEntry;
import id.ac.its.sikemastc.data.SikemasContract.PesertaEntry;
import id.ac.its.sikemastc.data.SikemasContract.PertemuanEntry;
import id.ac.its.sikemastc.data.SikemasContract.DosenEntry;


public class SikemasDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = SikemasContract.DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    public SikemasDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqlCreateUserTable(sqLiteDatabase);
        sqlCreateKelasTable(sqLiteDatabase);
        sqlCreatePerkuliahanTable(sqLiteDatabase);
        sqlCreateKehadiranTable(sqLiteDatabase);
        sqlCreatePesertaTable(sqLiteDatabase);
        sqlCreatePertemuanTable(sqLiteDatabase);
        sqlCreateDosenTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KelasEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PerkuliahanEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KehadiranEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PesertaEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PertemuanEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DosenEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private void sqlCreateUserTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                        UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        UserEntry.KEY_USER_ID + " TEXT NOT NULL, " +
                        UserEntry.KEY_USER_NAME + " TEXT NOT NULL, " +
                        UserEntry.KEY_USER_EMAIL + " TEXT NOT NULL, UNIQUE (" +
                        UserEntry.KEY_USER_EMAIL + ", " +
                        UserEntry.KEY_USER_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
    }

    private void sqlCreateKelasTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_KELAS_TABLE =
                "CREATE TABLE " + SikemasContract.KelasEntry.TABLE_NAME + " (" +
                        KelasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KelasEntry.KEY_ID_KELAS + " TEXT NOT NULL, " +
                        KelasEntry.KEY_KODE_SEMESTER + " TEXT NOT NULL, " +
                        KelasEntry.KEY_KODE_KELAS + " TEXT NOT NULL, " +
                        KelasEntry.KEY_NAMA_MK  + " TEXT NOT NULL, " +
                        KelasEntry.KEY_KODE_MK + " TEXT NOT NULL, " +
                        KelasEntry.KEY_NAMA_RUANGAN + " TEXT NOT NULL, " +
                        KelasEntry.KEY_HARI + " TEXT NOT NULL, " +
                        KelasEntry.KEY_MULAI + " TEXT NOT NULL, " +
                        KelasEntry.KEY_SELESAI + " TEXT NOT NULL ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_KELAS_TABLE);
    }

    private void sqlCreatePerkuliahanTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PERKULIAHAN_TABLE =
                "CREATE TABLE " + SikemasContract.PerkuliahanEntry.TABLE_NAME + " (" +
                        PerkuliahanEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PerkuliahanEntry.KEY_ID_PERKULIAHAN + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_ID_KELAS + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_STATUS_DOSEN + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_STATUS_PERKULIAHAN + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_PERTEMUAN_KE + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_TANGGAL_PERKULIAHAN + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_KODE_SEMESTER + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_NAMA_MK  + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_KODE_MK + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_NAMA_RUANGAN + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_KODE_KELAS + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_HARI + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_MULAI + " TEXT NOT NULL, " +
                        PerkuliahanEntry.KEY_SELESAI + " TEXT NOT NULL ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_PERKULIAHAN_TABLE);
    }

    private void sqlCreateKehadiranTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_KEHADIRAN_TABLE =
                "CREATE TABLE " + KehadiranEntry.TABLE_NAME + " (" +
                        KehadiranEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA + " TEXT, " +
                        KehadiranEntry.KEY_ID_KELAS_MAHASISWA + " TEXT, " +
                        KehadiranEntry.KEY_ID_MAHASISWA + " TEXT, " +
                        KehadiranEntry.KEY_PERTEMUAN_KE + " TEXT, " +
                        KehadiranEntry.KEY_TANGGAL_PERTEMUAN + " TEXT, " +
                        KehadiranEntry.KEY_NRP_MAHASISWA + " TEXT, " +
                        KehadiranEntry.KEY_NAMA_MAHASISWA + " TEXT, " +
                        KehadiranEntry.KEY_STATUS_KEHADIRAN + " TEXT, " +
                        KehadiranEntry.KEY_WAKTU_CHEKIN + " TEXT, " +
                        KehadiranEntry.KEY_TEMPAT_CHECKIN + " TEXT, " +
                        KehadiranEntry.KEY_KET_KEHADIRAN + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_KEHADIRAN_TABLE);
    }

    private void sqlCreatePesertaTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PESERTA_TABLE =
                "CREATE TABLE "+ PesertaEntry.TABLE_NAME + " (" +
                        PesertaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PesertaEntry.KEY_ID_KELAS + " TEXT NOT NULL, " +
                        PesertaEntry.KEY_ID_MAHASISWA + " TEXT NOT NULL, " +
                        PesertaEntry.KEY_NRP_MAHASISWA + " TEXT NOT NULL, " +
                        PesertaEntry.KEY_NAMA_MAHASISWA + " TEXT NOT NULL, " +
                        PesertaEntry.KEY_EMAIL_MAHASISWA + " TEXT NOT NULL ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_PESERTA_TABLE);
    }

    private void sqlCreatePertemuanTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PERTEMUAN_TABLE =
                "CREATE TABLE " + PertemuanEntry.TABLE_NAME + " (" +
                        PertemuanEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PertemuanEntry.KEY_ID_PERTEMUAN + " TEXT NOT NULL, " +
                        PertemuanEntry.KEY_ID_KELAS + " TEXT NOT NULL, " +
                        PertemuanEntry.KEY_PERTEMUAN_KE + " TEXT NOT NULL, " +
                        PertemuanEntry.KEY_STATUS_PERKULIAHAN + " TEXT NOT NULL, " +
                        PertemuanEntry.KEY_STATUS_DOSEN + " TEXT NOT NULL, " +
                        PertemuanEntry.KEY_TANGGAL + " TEXT, " +
                        PertemuanEntry.KEY_HARI + " TEXT NOT NULL, " +
                        PertemuanEntry.KEY_MULAI + " TEXT NOT NULL, " +
                        PertemuanEntry.KEY_SELESAI + " TEXT NOT NULL ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_PERTEMUAN_TABLE);
    }

    private void sqlCreateDosenTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_DOSEN_TABLE =
                "CREATE TABLE " + DosenEntry.TABLE_NAME + " (" +
                        DosenEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DosenEntry.KEY_ID_DOSEN + " TEXT NOT NULL, " +
                        DosenEntry.KEY_ID_KELAS_DOSEN + " TEXT NOT NULL, " +
                        DosenEntry.KEY_KODE_DOSEN + " TEXT NOT NULL, " +
                        DosenEntry.KEY_NAMA_DOSEN + " TEXT NOT NULL, " +
                        DosenEntry.KEY_EMAIL_DOSEN + " TEXT NOT NULL ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_DOSEN_TABLE);
    }
}
