package com.tkrpan.yamba;

import java.util.List;

import com.tkrpan.yamba.StatusData.DbHelper;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {

	Twitter twitter; //sada su dio zajedni�kog objekta a ne vi�e StatusActivity-a
	SharedPreferences prefs;
	private boolean serviceRunning; // flag koji �e govoriti izvr�ava li se Servis ili ne
	//oznaka je privatna u ovoj klasi pa ju se izvana ne mo�e mijenjati
	
	private StatusData statusData;
	
	@Override
	public void onCreate() { //poziva se kada je aplikacija prvi put izra�ena
		//Kreira se svaki put kad god je prvi put potreban neki od njenih dijelova, kao �to su Activity ili Service
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);//postavlja postavke
		//svaka aplikacija ima vlastite postavke koje su dostupne svim komponentama tog konteksta aplikacije.
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		this.statusData = new StatusData(this);
	}

	@Override
	public void onTerminate() {
		// Podlo�ak za �i��enje koji �e se dogoditi svaki put kada se apliakcija bude trebala ugasiti
		super.onTerminate();
	}
	//Premje�ten iz StatusActivity-a jer �e ga koristiti drugi dijelovi aplikacije. 
	// Syncronized zna�i da se unutar takve metode mo�e u jednom trenutku nalaziti samo 
	//jedna nit. To je va�no jer tu metodu mogu koristiti razli�ite niti koje mogu postojati u aplikaciji
	public synchronized Twitter getTwitter(){ 

		if(this.twitter==null){//ako twitter nije definiran pravimo ga
			String username, password, apiRoot;
			username = this.prefs.getString("username", ""); //prvi parametar je klju�
			password = this.prefs.getString("password", ""); //drugi parametar je default vrijednost
			apiRoot = this.prefs.getString("apiRoot", "http://yamba.marakana.com/api");
			
			if(!TextUtils.isEmpty(username)&& !TextUtils.isEmpty(password) && !TextUtils.isEmpty(apiRoot)){
				//spaja se sa twitter.com
				this.twitter = new Twitter(username, password);
				this.twitter.setAPIRootUrl(apiRoot);	
			}
		}
		return this.twitter;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		this.twitter = null;// poni�tava twitter objekt da bi ga getTwitter() pravio kada bude potreban.
	}
	
	public boolean isServiceRunning(){// javna metoda koja �e provjeravati status oznake
		return serviceRunning;
	}
	
	public void setServiceRunning(boolean serviceRunning){ //ova metoda �e postavljati status oznake
		this.serviceRunning = serviceRunning;
	}
	
	public StatusData getStatusData(){ //objekt je dostupan ostatku aplikacije samo kroz ovu metodu
		if(statusData == null){
			statusData = new StatusData(this);
		}
		return statusData;
	}
	
	public SharedPreferences getPrefs() {
		    return prefs;
		  }
	
	//SPaja se s mre�om i smje�ta najnovije statuse u bazu
	//Vra�a broj novih statusa
	
	public synchronized int fetchStatusUpdate (){
		Twitter twitter = this.getTwitter();
		if(twitter==null){
			return 0;
		}
		try {
			List<Status> statusUpdate = twitter.getFriendsTimeline();
			long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
			int count = 0;
			
			ContentValues values = new ContentValues();
			
			for(Twitter.Status status : statusUpdate){
				
				values.put(StatusData.C_ID, status.getId());
				long createdAt =status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT, createdAt);
				//values.put(dbHelper.C_SOURCE, status.source);
				values.put(StatusData.C_TEXT, status.getText());
				values.put(StatusData.C_USER, status.getUser().getName());
				
				this.getStatusData().insertOrIgnore(values);
				
				if(latestStatusCreatedAtTime < createdAt){
					count ++;
				}
			}
			return count;
		} catch (RuntimeException e) {
			return 0;
		}
	}
}
