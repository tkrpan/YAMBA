package com.tkrpan.yamba;

import java.util.List;

import com.tkrpan.yamba.StatusData.DbHelper;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	
	public static final String NEW_STATUS_INTENT = "com.marakana.yamba.NEW_STATUS";
	public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";
	private static final String TAG = "UpdaterService";
	static final int DELAY = 60000; //minuta
	private boolean runFlag = false;
	private Updater updater;
	
	//private YambaApplication yamba;
	//DbHelper dbHelper;
	//SQLiteDatabase db;

	@Override
	//koristi se u vezanim uslugama da vrati stvarnu implementaciju neèega što se zove  poveznik engl. binder. Za sada vracamo samo nulu
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	//Dobro mjesto za oavljanje posla koji se treba obaviti samo jednom za vrijeme trajanja Servisa
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater(); // posebna nit koja komunicira preko mreže i preuzima statuse. Potrebno ju je napraviti samo jedanput.
		
//		this.yamba = (YambaApplication)getApplication(); //referenca na objekt YambaAplication korištenjem metode getApplication()
//		dbHelper = new DbHelper(this); // Service je podklasa konteksta
	}
	
	@Override
	//poziva se svaki puta kada Servis primi namjeru startService()
	//može primiti više zahtjeva da bude pokrenuta i svaki že izazvati onStartCommand()
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!runFlag){
			this.runFlag = true; // postavlja zastavicu da je pokrenuta nit Updater
			this.updater.start();
			((YambaApplication)super.getApplication()).setServiceRunning(true); //ne kontam zašto nije stavio runFlag

			//this.yamba.setServiceRunning(true);
			
			Log.d(TAG, "onStarted");
		}
		return START_STICKY;
	}

	@Override
	//poziva se neposredno prije unuštavanja Servisa, kroz zahtjev stopService()
	//Dobro mjesto za pospremanje stvari koje su inicijalizirane u onCreate()
	public void onDestroy() {
		super.onDestroy();
		
		this.runFlag = false; //nit se više ne izvršava
		this.updater.interrupt(); //interupt() prekida nit
		this.updater = null; //procesu prikupljanja otpada olakšavamo posao
		((YambaApplication) super.getApplication()).setServiceRunning(false);
		
		Log.d(TAG, "onDestroy");
	}
	
	//nit koja preuzima statuse sa mrežne usluge
	private class Updater extends Thread { //klasa updater je nit pa ju proširujemo
		
		public Updater(){ //imenovanje niti
			super("UpdaterService-Updater");
		}
	
		public void run(){ // Java nit mora pružati metodu run(). U njoj se obavlja sav posao
			
			UpdaterService updaterService = UpdaterService.this; //pravimo referencu na uslugu
			
			while(updaterService.runFlag==true){//petlja se izvršava sve dok se Servis ne zaustavi
				
				Log.d(TAG, "Updater run");	
				
					try {
						YambaApplication yamba = (YambaApplication)updaterService.getApplication();
						int newUpdates = yamba.fetchStatusUpdate();
						
//						timeline = yamba.getTwitter().getFriendsTimeline();
//						//Poziva se getTwiter() u YambaAplication
//						//zatim se uzima objekt Twitter i zatim na njemu Timline
//						//ova metoda može trajati i smještena je u posebnu nit
//					} catch (TwitterException e1) {
//						Log.d(TAG, "Failed to connect to twitter service", e1);
//						e1.printStackTrace();
//					} 
//					
//					db = DbHelper.getWritableDatabase(); // na ovoj liniji otici ce u dbHelper i izraditi datoteku baze podataka
//					
//					ContentValues values = new ContentValues(); //jednostavna strukutra ime - vrijednost koja preslikava imena tablica baze podataka njihovim odgovarajuæim vrijednostima
//					//prolazi kroz sustav i ispisuje ih
//						
//						try {
//							//umeæemo vriejdnost sadržaja u bazu preko poziva insert() na objektu SQLiteDatabase
//							db.insertOrThrow(DbHelper.TABLE, null, values); // poušava spremititi podatke u bazu ali ne uspjeva
//							
//							Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
//							//ispisujemo statuse u LogCat
//						} catch (SQLException e) {
//							// ignorira iznimku
//						}
//					}
//					
//					db.close(); // da ne bi drugi Activity pokuašvao pisati ili èitati
					
					
					Thread.sleep(DELAY); //prekida izvršavanje niti Updater na odreðeni broj milisekundi
				
				} catch (InterruptedException e) {
					//kada signaliziramo interrupt() niti koja se izvršava, to æe izazvati Exeption u ovoj
					//metodi. Zato postavljamo rinFlag na false kako se nit ne bi pokušala ponovno izvršavati 
					//sve dok je se ponovno ne pokrene
					updaterService.runFlag = false;
				} 
			}
		}
	}
}
