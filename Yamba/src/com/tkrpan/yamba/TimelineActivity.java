package com.tkrpan.yamba;

import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineActivity extends Activity {

	DbHelper1 dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //kursor statusa u bazi podataka
	//TextView textTimeline;
	ListView listTimeline;
	TimelineAdapter adapter;
	
	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		//textTimeline = (TextView)findViewById(R.id.textTimeline);
		listTimeline = (ListView)findViewById(R.id.listTimeline);
		dbHelper = new DbHelper1(this);
		db = dbHelper.getReadableDatabase(); //samo èitamo bazu
		
	}

	@Override  
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		db.close(); // zatvara bazu podataka
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		cursor = db.query(DbHelper1.TABLE, null, null, null, null, null, DbHelper1.C_CREATED_AT + " DESC");
		startManagingCursor(cursor); //cursor je iterator
		
		adapter.setViewBinder((android.widget.SimpleCursorAdapter.ViewBinder) VIEW_BINDER);
		
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
		
		/*String user, text, output;
		while (cursor.moveToNext()){ //zaustavlja se kada više nema podataka
			user = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper1.C_USER));
			text = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper1.C_TEXT));
			output = String.format("%s: %s\n", user, text);
			//textTimeline.append(output);
		}*/
	}
	
	static final ViewBinder VIEW_BINDER = new ViewBinder() { //konstanta i implementirana je kao unutarnja klasa jer nece ju nitko drugi koristiti
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex){ //povezuje podatke s prikazom
			
			if(view.getId() != R.id.textCreatedAt)
			return false;
			
			long timestamp = cursor.getLong(columnIndex); //uzima sirovu vrijednost vremenske oznake iz kursora
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
			((TextView)view).setText(relTime); //azurira text u prikazu
			return true; //vraca true pa SimpleCursorAdapter ne obradjuje bindView() na ovom elementu na uobicajen nacin
		}
	};
}
