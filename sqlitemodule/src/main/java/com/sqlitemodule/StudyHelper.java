package com.sqlitemodule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wenming on 2016/6/25.
 */
public class StudyHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "countBook.db";

    public static final String TABLE_NAME = "countBook";

    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_SQL = "create table "+TABLE_NAME+" (id integer PRIMARY KEY AUTOINCREMENT," +
            "data varchar(20) not null," +
            "income integer not null," +
            "cost integer not null);";

    public StudyHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public StudyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
