package id.ac.its.sikemastc.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class SikemasProvider extends ContentProvider {

    public static final int CODE_KELAS = 100;
    public static final int CODE_KELAS_WITH_ID = 101;
    public static final int CODE_PERKULIAHAN = 200;
    public static final int CODE_PERKULIAHAN_WITH_ID = 201;
    public static final int CODE_KEHADIRAN = 300;
    public static final int CODE_KEHADIRAN_WITH_ID = 301;
    public static final int CODE_PESERTA = 400;
    public static final int CODE_PESERTA_WITH_ID = 401;
    public static final int CODE_USER = 500;
    public static final int CODE_PERTEMUAN = 600;
    public static final int CODE_PERTEMUAN_WITH_ID = 601;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SikemasDBHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SikemasContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SikemasContract.PATH_USER, CODE_USER);
        matcher.addURI(authority, SikemasContract.PATH_KELAS, CODE_KELAS);
        matcher.addURI(authority, SikemasContract.PATH_KELAS + "/#", CODE_KELAS_WITH_ID);
        matcher.addURI(authority, SikemasContract.PATH_PERKULIAHAN, CODE_PERKULIAHAN);
        matcher.addURI(authority, SikemasContract.PATH_PERKULIAHAN + "/#", CODE_PERKULIAHAN_WITH_ID);
        matcher.addURI(authority, SikemasContract.PATH_KEHADIRAN, CODE_KEHADIRAN);
        matcher.addURI(authority, SikemasContract.PATH_KEHADIRAN + "/#", CODE_KEHADIRAN_WITH_ID);
        matcher.addURI(authority, SikemasContract.PATH_PESERTA, CODE_PESERTA);
        matcher.addURI(authority, SikemasContract.PATH_PESERTA + "/#", CODE_PESERTA_WITH_ID);
        matcher.addURI(authority, SikemasContract.PATH_PERTEMUAN, CODE_PERTEMUAN);
        matcher.addURI(authority, SikemasContract.PATH_PERTEMUAN + "/#", CODE_PERTEMUAN_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SikemasDBHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_KELAS:
                db.beginTransaction();
                int kelasInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SikemasContract.KelasEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            kelasInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (kelasInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return kelasInserted;

            case CODE_PERTEMUAN:
                db.beginTransaction();
                int pertemuanInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SikemasContract.PertemuanEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            pertemuanInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (pertemuanInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return pertemuanInserted;

            case CODE_PERKULIAHAN:
                db.beginTransaction();
                int perkuliahanInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SikemasContract.PerkuliahanEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            perkuliahanInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (perkuliahanInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return perkuliahanInserted;

            case CODE_KEHADIRAN:
                db.beginTransaction();
                int kehadiranInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SikemasContract.KehadiranEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            kehadiranInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (kehadiranInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return kehadiranInserted;

            case CODE_PESERTA:
                db.beginTransaction();
                int pesertaInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SikemasContract.PesertaEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            pesertaInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (pesertaInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return pesertaInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_KELAS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.KelasEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_KELAS_WITH_ID:
                String idKelas = uri.getLastPathSegment();
                String[] selectionArgumentsKelas = new String[]{idKelas};
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.KelasEntry.TABLE_NAME,
                        projection,
                        SikemasContract.KelasEntry.KEY_ID_KELAS + " = ? ",
                        selectionArgumentsKelas,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_PERTEMUAN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.PertemuanEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_PERKULIAHAN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.PerkuliahanEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_PERKULIAHAN_WITH_ID:
                String idPerkuliahan = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{idPerkuliahan};
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.PerkuliahanEntry.TABLE_NAME,
                        projection,
                        SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_KEHADIRAN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.KehadiranEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_KEHADIRAN_WITH_ID:
                String idPerkuliahanPeserta = uri.getLastPathSegment();
                String[] selectionArgumentsKehadiran = new String[]{idPerkuliahanPeserta};
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.KehadiranEntry.TABLE_NAME,
                        projection,
                        SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA + " = ? ",
                        selectionArgumentsKehadiran,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_PESERTA:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SikemasContract.PesertaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("This app doesn't immplement getType method in Sikemas.");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "This app doesn't immplement insert in Sikemas. Use bulkInsert instead");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_USER:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        SikemasContract.UserEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_KELAS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        SikemasContract.KelasEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_PERTEMUAN:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        SikemasContract.PertemuanEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_PERKULIAHAN:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        SikemasContract.PerkuliahanEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_KEHADIRAN:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        SikemasContract.KehadiranEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_PESERTA:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        SikemasContract.PesertaEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            mOpenHelper.getWritableDatabase().close();
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("This app doesn't immplement update in Sikemas");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
