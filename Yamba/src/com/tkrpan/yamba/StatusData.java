package com.tkrpan.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class StatusData {
	
	static final int VERSION = 1;
	static final String DATABASE = "timline.db";
	static final String TABLE = "timline";
	static final String C_ID = "_id";
	static final String C_CREATED_AT = "created_at";
	static final String C_USER = "user";
	static final String C_TEXT = "txt";
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	
	private static final String[] MAX_CREATED_AT_COLUMNS = {"max(" + StatusData.C_CREATED_AT + ")" };
	
	private static final String[] DB_TEXT_COLUMNS = {C_TEXT};
	
	//Implementacije DbHelper
	
	class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) { 
			super(context, DATABASE, null, VERSION); //konstante proslijeðujemo superu
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) { // poziva se jednom, kada se baza podataka kreira
			String sql = "create table " + TABLE + " (" + C_ID + " int primary key, " + 
						C_CREATED_AT + " int, " + C_USER + " text, " +
						C_TEXT + " text" + ")"; //stvarni SQL kojeg proslijeðujemo bazi
			db.execSQL(sql);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table " + TABLE); //ispušta staru bazu podataka
			
			this.onCreate(db); //izrada nove baze
		}
	}
	
	private final DbHelper dbHelper; //privatna i konaèna referenca na DbHelper
	
	public StatusData(Context context){
		this.dbHelper = new DbHelper(context);
	}
	
	public void close(){
		this.dbHelper.close();
	}
	
	public void insertOrIgnore(ContentValues values){
		SQLiteDatabase db = this.dbHelper.getWritableDatabase(); //otvaramo bazu neposredno prije upisivanja
		try {
			db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		} finally {
			db.close(); // zatvara bez obzira na to dali je nešto prošlo krivim putem
		}
	}
	
	public Cursor getStatusUpdate (){//vraæa sve statuse u bazi pocevsi od najnovijeg
		
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	public long getLatestStatusCreatedAtTime (){//vraæa vremensku oznaku najnovijeg statusa u bazi
		
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		
		try {
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS, null, null, null, null, null);
			
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			} finally {
				cursor.close();
			}
			
		} finally {
			db.close();
		}
	}
	
	public String getStatusTextById(long id){ // zadani identifikator getStatusTextById() vraca stvarni text tog statusa
		
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		
		try {
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id, null, null, null, null);
			
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
			
		} finally {
			db.close();
		}
	}
}
