package id.ac.its.sikemastc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SikemasDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sikemas.db";
    private static final int DATABASE_VERSION = 1;

    public SikemasDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqlCreateUserTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SikemasContract.UserEntry.TABLE_NAME);
    }

    private void sqlCreateUserTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + SikemasContract.UserEntry.TABLE_NAME + " (" +
                        SikemasContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SikemasContract.UserEntry.KEY_USER_ID + " TEXT NOT NULL, " +
                        SikemasContract.UserEntry.KEY_USER_NAME + " TEXT NOT NULL, " +
                        SikemasContract.UserEntry.KEY_USER_EMAIL + " TEXT NOT NULL, UNIQUE (" +
                        SikemasContract.UserEntry.KEY_USER_EMAIL + ", " +
                        SikemasContract.UserEntry.KEY_USER_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
    }
}
