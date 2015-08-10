package biz.franch.protoi2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StoreDatabaseInformation extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "information.db";
    public static final int DATABASE_VERSION = 4;
    public static String SQL_CREATE_ENTRIES =
            "CREATE TABLE information (" +
                    "id int(150), tag int(150), name VARCHAR(50), imageUrl VARCHAR(50), videoUrl VARCHAR(50)," +
                    " desc VARCHAR(50), color VARCHAR(50));";

    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS information";
    public static String SQL_CLEAN_ENTRIES = "DELETE from information";

    public StoreDatabaseInformation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void cleanTable(SQLiteDatabase db) {
        Log.i("info", "table information");
        db.execSQL(SQL_CLEAN_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "Обновление базы данных с версии " + oldVersion
                + " до версии " + newVersion + ", которое удалит все старые данные");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}