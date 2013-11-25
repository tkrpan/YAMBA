package com.tkrpan.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbHelper1 extends SQLiteOpenHelper {

    static final String TAG = "DbHelper";
    static final String DB_NAME = "timeline.db";
    static final int DB_VERSION = 1;
    static final String TABLE = "timline";
    static final String C_ID = BaseColumns._ID;
    static final String C_CREATED_AT = "created_at";
    static final String C_SOURCE = "source";
    static final String C_TEXT = "txt";
    static final String C_USER = "user";
    
    Context context;
    
    public DbHelper1(Context context) {
            super(context, DB_NAME, null, DB_VERSION); //konstante proslijedjujemo superu
            this.context = context; //lokalnu referencu na kontekst
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // poziva se jednom, kada se baza podataka kreira
            // TODO Auto-generated method stub
            String sql = "create table " + TABLE + " (" + C_ID + " int primary key, " +
                                    C_CREATED_AT + " int, " + C_SOURCE + " text, " + C_USER + " text, " +
                                    C_TEXT + " text" + ")"; //stvarni SQL kojeg proslijedjujemo bazi
            db.execSQL(sql);
            
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("drop table if exists " + TABLE); //ispusta staru bazu podataka
            
            onCreate(db); //izrada nove baze
    }

}