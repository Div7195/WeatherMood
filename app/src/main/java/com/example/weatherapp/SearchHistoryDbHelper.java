package com.example.weatherapp;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class SearchHistoryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "search_history.db";
    private static final int DATABASE_VERSION = 1;
//    private static final String name = "name";
//    private static final String searchDate = "date";
    public SearchHistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE search_history (_id INTEGER PRIMARY KEY AUTOINCREMENT, search_string TEXT, search_date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS search_history");
        onCreate(db);
    }
}

