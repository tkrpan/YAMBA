package com.tkrpan.yamba;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {
	//niz stringova koji zadaje stupce u krusoru s kojima se povezujemo
	static final String [] FROM = {DbHelper1.C_CREATED_AT, DbHelper1.C_USER, DbHelper1.C_TEXT };
	static final int [] TO = {R.id.textCreatedAt, R.id.textUser, R.id.textText};
	
	public TimelineAdapter(Context context, Cursor c) {
		
		super(context, R.layout.row, c, FROM, TO);
	}
	
	public void bindView(View row, Context context, Cursor cursor){ //Poziva se za svaki red
		super.bindView(row, context, cursor);
		
		//ruèno povezivanje vremenske oznake s prikazom
		long timestamp = cursor.getLong(cursor.getColumnIndex(DbHelper1.C_CREATED_AT));
		TextView textCreatedAt = (TextView)row.findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}
}
