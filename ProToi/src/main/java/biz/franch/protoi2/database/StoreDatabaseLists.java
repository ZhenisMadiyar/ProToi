package biz.franch.protoi2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StoreDatabaseLists extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "lists.db";
    public static final int DATABASE_VERSION = 5;

    public static String SQL_CREATE_ENTRIES1 =
            "CREATE TABLE estrada(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES1 = "DROP TABLE IF EXISTS estrada";
    public static String SQL_CLEAN_ENTRIES1 = "DELETE from estrada";

    public static String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE vedushii(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES2 = "DROP TABLE IF EXISTS vedushii";
    public static String SQL_CLEAN_ENTRIES2 = "DELETE from vedushii";

    public static String SQL_CREATE_ENTRIES3 =
            "CREATE TABLE shou(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES3 = "DROP TABLE IF EXISTS shou";
    public static String SQL_CLEAN_ENTRIES3 = "DELETE from shou";

    public static String SQL_CREATE_ENTRIES4 =
            "CREATE TABLE dj(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES4 = "DROP TABLE IF EXISTS dj";
    public static String SQL_CLEAN_ENTRIES4 = "DELETE from dj";

    public static String SQL_CREATE_ENTRIES5 =
            "CREATE TABLE zhivye_komandy(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES5 = "DROP TABLE IF EXISTS zhivye_komandy";
    public static String SQL_CLEAN_ENTRIES5 = "DELETE from zhivye_komandy";

    public static String SQL_CREATE_ENTRIES6 =
            "CREATE TABLE limuziny(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES6 = "DROP TABLE IF EXISTS limuziny";
    public static String SQL_CLEAN_ENTRIES6 = "DELETE from limuziny";

    public static String SQL_CREATE_ENTRIES7 =
            "CREATE TABLE keitering(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES7 = "DROP TABLE IF EXISTS keitering";
    public static String SQL_CLEAN_ENTRIES7 = "DELETE from keitering";

    public static String SQL_CREATE_ENTRIES8 =
            "CREATE TABLE restaran(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES8 = "DROP TABLE IF EXISTS restaran";
    public static String SQL_CLEAN_ENTRIES8 = "DELETE from restaran";

    public static String SQL_CREATE_ENTRIES9 =
            "CREATE TABLE dance(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES9 = "DROP TABLE IF EXISTS dance";
    public static String SQL_CLEAN_ENTRIES9 = "DELETE from dance";

    public static String SQL_CREATE_ENTRIES10 =
            "CREATE TABLE photovideo(name VARCHAR(50), desc VARCHAR(50), imageUrl VARCHAR(50), objectId VARCHAR(50));";
    public static String SQL_DELETE_ENTRIES10 = "DROP TABLE IF EXISTS photovideo";
    public static String SQL_CLEAN_ENTRIES10= "DELETE from photovideo";

    public StoreDatabaseLists(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES1);
        db.execSQL(SQL_CREATE_ENTRIES2);
        db.execSQL(SQL_CREATE_ENTRIES3);
        db.execSQL(SQL_CREATE_ENTRIES4);
        db.execSQL(SQL_CREATE_ENTRIES5);
        db.execSQL(SQL_CREATE_ENTRIES6);
        db.execSQL(SQL_CREATE_ENTRIES7);
        db.execSQL(SQL_CREATE_ENTRIES8);
        db.execSQL(SQL_CREATE_ENTRIES9);
        db.execSQL(SQL_CREATE_ENTRIES10);
    }

    public void cleanTable(SQLiteDatabase db) {
        Log.i("info", "table cleaned");
        db.execSQL(SQL_CLEAN_ENTRIES1);
        db.execSQL(SQL_CLEAN_ENTRIES2);
        db.execSQL(SQL_CLEAN_ENTRIES3);
        db.execSQL(SQL_CLEAN_ENTRIES4);
        db.execSQL(SQL_CLEAN_ENTRIES5);
        db.execSQL(SQL_CLEAN_ENTRIES6);
        db.execSQL(SQL_CLEAN_ENTRIES7);
        db.execSQL(SQL_CLEAN_ENTRIES8);
        db.execSQL(SQL_CLEAN_ENTRIES9);
        db.execSQL(SQL_CLEAN_ENTRIES10);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "Обновление базы данных с версии " + oldVersion
                + " до версии " + newVersion + ", которое удалит все старые данные");
        db.execSQL(SQL_DELETE_ENTRIES1);
        db.execSQL(SQL_DELETE_ENTRIES2);
        db.execSQL(SQL_DELETE_ENTRIES3);
        db.execSQL(SQL_DELETE_ENTRIES4);
        db.execSQL(SQL_DELETE_ENTRIES5);
        db.execSQL(SQL_DELETE_ENTRIES6);
        db.execSQL(SQL_DELETE_ENTRIES7);
        db.execSQL(SQL_DELETE_ENTRIES8);
        db.execSQL(SQL_DELETE_ENTRIES9);
        db.execSQL(SQL_DELETE_ENTRIES10);
        onCreate(db);
    }
}