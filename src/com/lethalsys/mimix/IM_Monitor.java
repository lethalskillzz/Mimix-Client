package com.lethalsys.mimix;


import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;


@SuppressLint("NewApi")
public class IM_Monitor extends Service {
	
//public static String im_monitor = "com.lethalsys.mimix.im_monitor";	
	
AtomicBoolean isActive=new AtomicBoolean(true);	

Vibrator vibrator;



static int IM_POLL_PERIOD = 5000;
static int MSG_POLL_PERIOD = 5000;
public static Handler   IMMonitorHandler    = null;

//private Set<Long> seenMsg=new HashSet<Long>();

public static final int IMNOTIFICATION_ID=13372;

String iresponse=null;
String UserID;

String username;
String msgbody;
String mid;
int i;

Util_Database utilhelper;

Database_One helper=null;

JSONArray data = null;

public static ConnectivityManager mConnectivityManager;
public static NetworkInfo mNetworkInfo;
Ringtone r;

@Override
public void onCreate() {
super.onCreate();

utilhelper=new Util_Database(this);
UserID = utilhelper.getUSER_ID();

helper=new Database_One(this);

vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);



//Toast.makeText(this, "Congrats! IMService Created", Toast.LENGTH_LONG).show();
new Thread(threadBody).start();	

//showNotification();

}




@Override
public IBinder onBind(Intent intent) {
	return(null);
}


@Override
public void onDestroy() {
super.onDestroy();

isActive.set(false);
}


private Runnable threadBody=new Runnable() {
public void run() {
while (isActive.get()) {


if(null != MessageActivity.msgUiHandler)
{
	if( MessageActivity.msgactv != false)
	{
	//SystemClock.sleep(IM_POLL_PERIOD);
    Message msgToActivity = new Message();
    msgToActivity.what = 1;
    MessageActivity.msgUiHandler.sendMessage(msgToActivity);
	}
}

	
//pollIM();
SystemClock.sleep(MSG_POLL_PERIOD);


}
}
};


/*private void pollIM() {
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
	    	new MyAsyncTask().execute("get_msgmonitor");
        }
        else
        {
        	
        }	

}


private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
	@Override
	protected Double doInBackground(String... params) {
	// TODO Auto-generated method stub
	postData(params[0]);
	return null;
	}
	 
	protected void onPostExecute(Double result){

		//Toast.makeText(getApplicationContext(), iresponse.trim(), Toast.LENGTH_LONG).show();
		if(null!=iresponse)
		{
        if(iresponse.trim().length()!=0)
        {
        	//helper.Clear();
        
        try{
        	JSONObject jsonObj = new JSONObject(iresponse.trim());
        	
        	data = jsonObj.getJSONArray("msgs");
        	
        	if(data!=null)
			{
			
        	
            for(i=0;i<data.length();i++)
            {
            	JSONObject c = data.getJSONObject(i);
            	
            	//String stamp = c.getString("stamp");
            	//String userid = c.getString("userid");
            	username =  c.getString("username");
            	msgbody = c.getString("msgbody");
                //mid = c.getString("mid");
            	
                long pattern[]={0,200,200,400};
                vibrator.vibrate(pattern,-1);
                //vibrator.cancel();
                inotify();
                r.play();
             
                
        		//model.requery();
            }
			}
            
        } catch (JSONException e){
        	
        }

	
	
        }
        
		}
	
		//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();

	
	}
	
	
	protected void onProgressUpdate(Integer... progress)
	{
		
	}
	 
	protected void postData(String Header) {
	// Create a new HttpClient and Post Header
		HttpParams httpparameters = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
	    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
	    
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.setParams(httpparameters);
		
		HttpPost httppost;

	   httppost = new HttpPost(LoginActivity.SERVER+"imessanger.php");
	   
	
	try {
		
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
	nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	ResponseHandler<String> responseHandler = new BasicResponseHandler();
	 
	
	// Execute HTTP Post Request
	 iresponse = httpclient.execute(httppost, responseHandler);
	 


	 
	} catch (ClientProtocolException e) {
	// TODO Auto-generated catch block
		cancel(true);
	} catch (IOException e) {
	// TODO Auto-generated catch block
	}


	}
	 
	}


  private void showNotification() {
	final NotificationManager mgr=
	(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	Notification note=new Notification(R.drawable.ic_launcher,
	"New Message!",
	System.currentTimeMillis());
	Intent i=new Intent(this, MessageListActivity.class);
	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
	Intent.FLAG_ACTIVITY_SINGLE_TOP);
	PendingIntent pi=PendingIntent.getActivity(this, 0,
	i,
	0);
	note.setLatestEventInfo(this, username,
    msgbody,
	pi);

	mgr.notify(IMNOTIFICATION_ID, note);
	}*/




@SuppressLint("NewApi")
private void showNotification() {
	Intent i=new Intent(this, HomeActivity.class);
	//im_monitor="im_m";
	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
	Intent.FLAG_ACTIVITY_SINGLE_TOP);
	PendingIntent pi=PendingIntent.getActivity(this, 0,i,0);
	

	
	final NotificationManager mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	Notification note=new Notification.Builder(this)
	
	.setContentTitle(username)
	.setContentText(msgbody)
	//.setSubText(String.valueOf(i)+" new messages")
	.setSmallIcon(R.drawable.ic_launcher)
	.setContentIntent(pi)
	.setAutoCancel(true)
	//.setSound(sounduri)
	/*.addAction(R.drawable.ic_launcher, "title", pi)*/.build();
	
	//(R.drawable.ic_launcher,"New Message!",System.currentTimeMillis());

	
/*	note.setLatestEventInfo(this, username,
    msgbody,
	pi);*/

	mgr.notify(IMNOTIFICATION_ID, note);
	}



public void inotify() {
	
	if(Message_Fragment.msglstactv == false)
	{
     	if(MessageActivity.msgactv == false)
     	{
     	 showNotification();
     	}
	}	
      
}


public void sendIMmsg()
{
  
    IMMonitorHandler = new Handler();

if(MessageActivity.msgactv==true) 
{
     if(null != Post_Fragment.mUiHandler)
    {
        //first build the message and send.
        //put a integer value here and get it from the Activity handler
        //For Example: lets use 0 (msg.what = 0;)
        //for receiving service running status in the activity
        Message msgToActivity = new Message();
        msgToActivity.what = 0;
        Post_Fragment.mUiHandler.sendMessage(msgToActivity);
    } 
}
}





}