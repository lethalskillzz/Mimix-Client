package com.lethalsys.mimix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RatingActivity extends Activity {
	
	public static Handler RateActivityHandler = null;
	
	Util_Database utilhelper=null;
	private String UserID;
	private String PID;
	private RatingBar RateBar;
	private Button rating_btn;
	private ProgressBar rate_progressBar;
	TextView RateCount;
    String response;
    Boolean ErrorDlg = true;
	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	JSONArray data = null;
	String msg;
    String Rating;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);
		
		utilhelper = new Util_Database(this);
		UserID = utilhelper.getUSER_ID();

	
		Intent intent = getIntent();
	    PID = intent.getStringExtra(Post_Fragment.PostID);
	    
	    rating_btn = (Button) findViewById(R.id.rating_btn);
	    rate_progressBar = (ProgressBar) findViewById(R.id.rate_progressBar);
	    rate_progressBar.setVisibility(View.GONE);
	    
	    RateCount=(TextView)findViewById(R.id.rate_count);
	    
	    RateBar=(RatingBar)findViewById(R.id.main_ratingbar);
	    
	    RateBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
	    {
	    	
	    	/*public void OnRatingBarChanged(RatingBar ratingBar, float rating, boolean fromUser){
	    		
	    		RateCount.setText(String.valueOf(rating)+"/5.0");

	    		
	    	}*/

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {

				RateCount.setText(String.valueOf(rating)+"/5.0");
				
			}
	    });
	   
	    
	
	}
	

	public void onResume()
	{
		super.onResume();
		ErrorDlg = true;
	}
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK :
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	
	
	public void Rate(View v)
	{
		AddRate("add_rate");
	}
	
    public void AddRate(final String Header){
    	
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        

    	
        new AsyncTask<String, Integer, Double>() {
	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		
		rating_btn.setVisibility(View.GONE);
		rate_progressBar.setVisibility(View.VISIBLE);
		isFinish(false);
		
	}
	
	@Override
	protected Double doInBackground(String... params) {

        // Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
			
    	HttpPost httppost = new HttpPost(LoginActivity.SERVER+"add_rating.php");
    	Rating =  String.valueOf(RateBar.getRating());
		
    	 
    	try {
    		
    	// Add your data	
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
    	nameValuePairs.add(new BasicNameValuePair("UID",UserID));
    	nameValuePairs.add(new BasicNameValuePair("rate",Rating));
    	nameValuePairs.add(new BasicNameValuePair("pid", PID));

    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	 

    	

    	
    	// Execute HTTP Post Request
    	 response = httpclient.execute(httppost, responseHandler);
    	 


    	 
    
    	} catch (IOException e) {
    		
 
    		if(ErrorDlg == true)
    		ShowErrorDialoag();
    	}
   


    
		return null; 
	}
	
   	protected void onProgressUpdate(Integer... progress){

   	}

	
	protected void onPostExecute(Double result){
		/*if(response.trim().equals("ok"))
		{
			ClosMe();
			sendmsg();
		}
		else
		{
			//ShowTost("Error");
			if(ErrorDlg == true)
	        ShowErrorDialoag();
		}*/
		if(null!=response)
		{
			
		if(response.trim().length()!=0)
		{

			try{


				JSONObject jsonObj = new JSONObject(response.trim());               	
				

				if(jsonObj.isNull("data")==false)
				{

					data = jsonObj.getJSONArray("data");

					JSONObject d = data.getJSONObject(0);
					String msg = d.getString("msg");
					

					if("ok".compareTo(msg)==0)
					{
					
						String ratecount = d.getString("ratecount");
						String finalrating = d.getString("finalrating");
						ClosMe();
						sendmsg(finalrating,ratecount);


					}

					else
					{
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
						
					}

				}
			} catch (JSONException e){

			} 



		}
		}		
			
   		rate_progressBar.setVisibility(View.GONE);
		rating_btn.setVisibility(View.VISIBLE);
		isFinish(true);
		
		//Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

	}
    
    }.execute();
    
        }
        else
        {
        	if(ErrorDlg == true)
        	ShowErrorDialoag();
        }	
    
    }
    
    
    
    @SuppressLint("ValidFragment")
	public class NetworkErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setMessage(LoginActivity.neterrormsg)
            builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
                   .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   AddRate("add_rate");
                       }
                   })
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    
    
	public void ShowErrorDialoag() {
	    DialogFragment newFragment = new NetworkErrorDialogFragment();
	    newFragment.show(getFragmentManager(), "neterror");
	}
    
    
    
    

	public void ShowTost(String txt)
	{
		Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.rating, menu);
		return true;
	}
	
	
    public void sendmsg(String Rating,String Rcount)
    {
      
        RateActivityHandler = new Handler();
     
        if(null != Post_Fragment.mUiHandler)
        {
        	//if(HomeActivity.homeactv == true)
        	//{
            //first build the message and send.
            //put a integer value here and get it from the Activity handler
            //For Example: lets use 0 (msg.what = 0;)
            //for receiving service running status in the activity
            Message msgToActivity = new Message();
            msgToActivity.what = 2;
            msgToActivity.obj = PID+":"+Rating+":"+Rcount;
            Post_Fragment.mUiHandler.sendMessage(msgToActivity);
          
        	//}
        } 
        
        
        
       if(null != MyProfileActivity.myprofUiHandler)
        {
        	//if(MyProfileActivity.myprofactv == true)
        	//{
            //first build the message and send.
            //put a integer value here and get it from the Activity handler
            //For Example: lets use 0 (msg.what = 0;)
            //for receiving service running status in the activity
            Message msgToActivity = new Message();
            msgToActivity.what = 2;
            msgToActivity.obj = PID+":"+Rating+":"+Rcount;
            MyProfileActivity.myprofUiHandler.sendMessage(msgToActivity);
        	//}
        } 
        	
        
        if(null != ProfileActivity.profUiHandler)
        {
        	
        	//if(ProfileActivity.profactv == true)
        	//{
            //first build the message and send.
            //put a integer value here and get it from the Activity handler
            //For Example: lets use 0 (msg.what = 0;)
            //for receiving service running status in the activity
            Message msgToActivity = new Message();
            msgToActivity.what = 2;
            msgToActivity.obj = PID+":"+Rating+":"+Rcount;
            ProfileActivity.profUiHandler.sendMessage(msgToActivity);
        	//}
        } 
        
        if(null != HashActivity.HashUiHandler)
        {

        	//if(DisplayHashActivity.hashactv == true)
        	//{
            //first build the message and send.
            //put a integer value here and get it from the Activity handler
            //For Example: lets use 0 (msg.what = 0;)
            //for receiving service running status in the activity
            Message msgToActivity = new Message();
            msgToActivity.what = 2;
            msgToActivity.obj = PID+":"+Rating+":"+Rcount;
            HashActivity.HashUiHandler.sendMessage(msgToActivity);
        	//}
        } 
    }
	
	
    public void ClosMe() {

    Intent intent = getIntent();
    overridePendingTransition(0, 0);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
}
    
    void isFinish(boolean bl)
    {
    	this.setFinishOnTouchOutside(bl);
    }
	

}
