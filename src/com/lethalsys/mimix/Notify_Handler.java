package com.lethalsys.mimix;


import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Notify_Handler extends IntentService {

    public static final int IMNOTIFICATION_ID=1337;

    public static String notif_type = null;	
    
	
	 String type;
     String title;
     String body;
     private Handler handler;
     
     public static Handler IMRefHandler = null;
     
     Ringtone r;
     String messageType;
     
     Vibrator vibrator;
     
     PendingIntent pi;

    public Notify_Handler() {
        super("Notify_Handler");
    }

    @Override   
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
        
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);

    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
      
        messageType = gcm.getMessageType(intent);

        type = extras.getString("type");
        title = extras.getString("title");
        body = extras.getString("body"); 
        
        sendRefmsg();
        Notify();
        
        long pattern[]={0,200,200,400};
        vibrator.vibrate(pattern,-1);
        //vibrator.cancel();        
        r.play();   
        
        //showToast();
       
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
    
    
    @SuppressLint("NewApi")
    private void showNotification() {
    
    	if(type.equals("NOTIFY_IM"))
    	{
    	Intent i=new Intent(this, HomeActivity.class);
    	notif_type="im";
    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
    	Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	pi=PendingIntent.getActivity(this, 0,i,0);
    	}
    	
    	else if(type.equals("NOTIFY_NEW_FOLLOW"))
    	{
    	Intent i=new Intent(this, HomeActivity.class);
    	notif_type="nf";
    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
    	Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	pi=PendingIntent.getActivity(this, 0,i,0);
    	}
 
    	else if(type.equals("NOTIFY_POST_MENTION"))
    	{
    	Intent i=new Intent(this, HomeActivity.class);
    	notif_type="pm";
    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
    	Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	pi=PendingIntent.getActivity(this, 0,i,0);
    	}
    	
    	
    	
    	
    	final NotificationManager mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	Notification note=new Notification.Builder(this)
    	
    	.setContentTitle(title)
    	.setContentText(body)
    	//.setSubText(String.valueOf(i)+" new messages")
    	.setSmallIcon(R.drawable.ic_notify)
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

    public void Notify() {
    	
    	if(Message_Fragment.msglstactv == false)
    	{
         	if(MessageActivity.msgactv == false)
         	{
         	 showNotification();
         	}
    	}	
          
    }


    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),messageType , Toast.LENGTH_LONG).show();
            }
         });

    }
    
    
    
    public void sendRefmsg()
    {
      
    	IMRefHandler = new Handler();
     
         if(null != Message_Fragment.msglUiHandler)
         {
            Message msgToActivity = new Message();
            msgToActivity.what = 1;
            Message_Fragment.msglUiHandler.sendMessage(msgToActivity);
         } 
         
         
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
    }
}