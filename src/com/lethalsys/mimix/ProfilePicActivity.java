package com.lethalsys.mimix;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.lethalsys.mimix.MyProfileActivity.ProfileAdapter;

public class ProfilePicActivity extends Activity {

	
	String iresponse;
	String UsrNAME;


    String UserID;
	byte[] imgdata = null;
	String imgfile;

	// number of images to select
	//private static final int PICK_IMAGE = 1;
	
    private ProgressBar ppic_loading;

	Util_Database utilhelper;
	ProfileAdapter adapter=null;

	JSONArray data = null;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	private ImageView profpic=null;
	
	Boolean ErrorDlg = true;
	
	private Bitmap MyBMP;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_pic);
		

		utilhelper = new Util_Database(this);
		UserID = utilhelper.getUSER_ID();

	
		Intent intent = getIntent();
		UsrNAME = intent.getStringExtra(ProfileActivity.PPUsrNAME);
		
		ppic_loading = (ProgressBar) findViewById(R.id.ppic_loading);
		profpic=(ImageView)findViewById(R.id.disp_profpic);
	    profpic.setMinimumHeight(350);
	    profpic.setMinimumWidth(350);
	    profpic.setMaxHeight(550);
	    profpic.setMaxWidth(550);
		
		new BMPAsyncTask().execute("img_"+utilhelper.getFACEUID(UsrNAME)+".png");

	  /*if(utilhelper.getFACEUID(UsrNAME)!=null)
	  {
		if(utilhelper.getFACEisIMG(utilhelper.getFACEUID(UsrNAME))!=null)
		{
		if(utilhelper.getFACEisIMG(utilhelper.getFACEUID(UsrNAME)).equals("YES"))
		{
			profpic.setImageBitmap(BitmapFactory.decodeByteArray(utilhelper.getFACEPPIC(utilhelper.getFACEUID(UsrNAME)), 0, utilhelper.getFACEPPIC(utilhelper.getFACEUID(UsrNAME)).length));
		}
		else
		{
			profpic.setImageResource(R.drawable.me);
		}
		
		}

	  }
		else
		{
			profpic.setImageResource(R.drawable.me);
		}*/
		
		//Getprofpic();
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
	
	public void Getprofpic()
	{
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
	    	new MyAsyncTask().execute("getprofpic");
        }
        else
        {
        	//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
        	ShowNoNetErrorDialoag();
        }
	}
	
	
	/*@O
	 *verride
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_pic, menu);
		return true;
	}*/
	
	

	public void confirmFireMissiles() {
		DialogFragment newFragment = new FireMissilesDialogFragment();
		newFragment.show(getFragmentManager(), "missiles");
	}


	@SuppressLint("ValidFragment")
	public class FireMissilesDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(iresponse.trim())
			.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// FIRE ZE MISSILES!
				}
			});
			/*.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });*/
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}




	
	
	
	

    	
	
	
	
	
	
	


	private class MyAsyncTask extends AsyncTask<String, Integer, Double>{

		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0]);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			ppic_loading.setVisibility(View.VISIBLE);
		}


		protected void onPostExecute(Double result){

			if(null!=iresponse)
    		{
			if(iresponse.trim().length()!=0)
			{


				try{


					JSONObject jsonObj = new JSONObject(iresponse.trim());               	
					

					if(jsonObj.isNull("data")==false)
					{
						data = jsonObj.getJSONArray("data");

						JSONObject d = data.getJSONObject(0);
						String img  = d.getString("img");
						
                    	int flag = 0;
                    	byte[] imgdata = Base64.decode(img, flag);
                    	
                    	if(imgdata.length!=0)
                    	{
                    		profpic.setImageBitmap(BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length));	
                    	}
                    	else
                    	{
                    		profpic.setImageResource(R.drawable.me);
                    	}
                    	
                    	
						 
					}
				
				
						

					
				} catch (JSONException e){

				} 

				data = null;
				ppic_loading.setVisibility(View.GONE);
			}


    		}

			//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
			//confirmFireMissiles();


		}


		protected void onProgressUpdate(Integer... progress){
			//pb.setProgress(progress[0]);
		}

		protected void postData(String Header) {
			// Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);

			HttpPost httppost = new HttpPost(LoginActivity.SERVER+"profile_detail.php");

			try {
				// Add your data

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
				nameValuePairs.add(new BasicNameValuePair("UNAME", UsrNAME ));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
 

				// Execute HTTP Post Request
				iresponse = httpclient.execute(httppost, responseHandler);




			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();
				ppic_loading.setVisibility(View.GONE);
				cancel(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ppic_loading.setVisibility(View.GONE);
				if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
			}
			
			

		}

	}

	
	
	
	
	

	private class BMPAsyncTask extends AsyncTask<String, Integer, Double>{

		@Override
		protected Double doInBackground(String... params) {

			DownloadBMP(params[0]);

			return null; 
		} 


		@Override  
		protected void onPreExecute() {
			super.onPreExecute();
			
			ppic_loading.setVisibility(View.VISIBLE);

		}


		protected void onPostExecute(Double result){
			if(MyBMP!=null)
			{
			    profpic.setImageBitmap(MyBMP);
			    
			}
			else
			{
				profpic.setImageResource(R.drawable.me);
			}

    		profpic.setVisibility(View.VISIBLE);
    		ppic_loading.setVisibility(View.GONE);

		}


		protected void onProgressUpdate(Integer... progress){

		}
		
		
		protected void DownloadBMP(String filename)
		{
		 //Bitmap myBitmap=null;
		try {
		  //set the download URL, a url that points to a file on the internet
		  //this is the file to be downloaded
		  String Url = LoginActivity.SERVER+"profile/image/"+filename;
		  URL url = new URL(Url);
		  //create the new connection
		  HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

		  //set up some things on the connection
		  urlConnection.setRequestMethod("GET");
		  urlConnection.setDoOutput(true); 
		  urlConnection.setConnectTimeout(15000);
		  urlConnection.setReadTimeout(30000);
		   //and connect!
		  urlConnection.connect();

		  //this will be used in reading the data from the internet
		  InputStream inputStream = urlConnection.getInputStream();
		  MyBMP = BitmapFactory.decodeStream(inputStream);
		 // Log.i("data:", IOUtils.toString(inputStream));

		 //catch some possible errors...
		 } catch (MalformedURLException e) {
		  e.printStackTrace();
		  Log.i("URL-ERROR:",e.toString());
		 } catch (IOException e) {
			 //myBitmap=null;
		  e.printStackTrace();
		  Log.i("IO-ERROR:",e.toString());
		 }

		 
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
                    	   Getprofpic();
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
    
    
	
	public void ShowNoNetErrorDialoag() {
	    DialogFragment newFragment = new NetworkErrorDialogFragment();
	    newFragment.show(getFragmentManager(), "noneterror");
	}
	

	

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            
            case android.R.id.home:
            	Go_Home();
            	return true;

 

            default:
                return super.onOptionsItemSelected(item);
        }
    } 
    
    
    public void Go_Home() {
    	Intent intent = new Intent(this, HomeActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    	}  
    



}
