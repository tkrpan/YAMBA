package com.tkrpan.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher {
	
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		
		//Pronalazi prikaze
		
		editText = (EditText)findViewById(R.id.editText);
		updateButton = (Button)findViewById(R.id.buttonUpdate);
		textCount = (TextView)findViewById(R.id.textCount);
		
		textCount.setText(Integer.toString(140));
		textCount.setTextColor(Color.GREEN); //Color je Android klasa
		updateButton.setOnClickListener(this); // obavještava this tj. StatusActivity kada je pritisnut
		
		editText.addTextChangedListener(this);
		
		twitter = new Twitter("student", "password");
		twitter.setAPIRootUrl("http://yamba.marakana.com/api");
	}
	
	//Asinkrono objavljivanje
			class PostToTwitter extends AsyncTask<String, Integer, String> {
				//Poziva se za pokretanje pozadinske aktivnosti
				@Override
				protected String doInBackground(String... statuses) { //... oznaèavaju da se radi o Strings
					
					try {
						Twitter.Status status = twitter.updateStatus(statuses[0]);
						return status.text;
					} catch (TwitterException e) {
						e.printStackTrace();
						return "Failed to post";
					}
				}

				//Poziva se kada postoji status koji treba ažurirati
				@Override
				protected void onProgressUpdate(Integer... values) {
					// TODO Auto-generated method stub
					super.onProgressUpdate(values);
					//ovdje se ne koristi
				}
				
				//Poziva se kada je pozadinska aktivnost završila
				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
				}
			}
	//poziva se kada korisnik prvi puta pritisne gumb izbornika
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu); //inflate-amo izbornik iz XML resursa
		return true;
	}
	//poziva se kada je pritisnuta opcija izbornika
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			Intent itemPrefsIntent = new Intent(this, PrefsActivity.class);
			startActivity(itemPrefsIntent);
			break;

		default:
			break;
		}
		return true;
	}



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String status = editText.getText().toString();
		new PostToTwitter().execute(status);
	}

	@Override
	public void afterTextChanged(Editable statusText) {
		// TODO Auto-generated method stub
		int count = 140-statusText.length();
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN);
		
		if(count<20){
			textCount.setTextColor(Color.YELLOW);
		}
		if(count<0){
			textCount.setTextColor(Color.RED);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}
