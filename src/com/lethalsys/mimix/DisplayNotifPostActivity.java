package com.lethalsys.mimix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.lethalsys.mimix.HomeActivity.FireMissilesDialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayNotifPostActivity extends Activity {

	private TextView uname=null;
	private TextView post_body=null;
	private TextView timestamp=null;
	private ImageView image=null;
	private ImageView pimage=null;
	private RatingBar RateBar=null;	
	public ProgressBar CProgress;

	String PID=null;
	
	String piresponse;
	Boolean ErrorDlg = true;
	
	static Util_Database utilhelper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_notif_post);
		
		utilhelper = new Util_Database(this);
		
		Intent intent = getIntent();
		PID = intent.getStringExtra(Notification_Fragment.NPOST_ID);

		
		uname=(TextView)findViewById(R.id.dispN_row_username);
		post_body=(TextView)findViewById(R.id.dispN_row_body);
		timestamp=(TextView)findViewById(R.id.dispN_row_date_time);
		image=(ImageView)findViewById(R.id.dispN_row_image);
		pimage=(ImageView)findViewById(R.id.dispN_row_postimage);
		RateBar=(RatingBar)findViewById(R.id.dispN_ratingbar);
		//CProgress=(ProgressBar)findViewById(R.id.dispN_progressbar);
		
		new PostsAyncTask().execute();
		
	}
	
	
	@Override
	public void onResume() {
	super.onResume();
	ErrorDlg = true;
	}
	
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.display_notif_post, menu);
		return true;
	}
	
	
	private class PostsAyncTask extends AsyncTask<String, Integer, Double>{

		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData();
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			//CProgress.setVisibility(View.VISIBLE);
			}


		protected void onPostExecute(Double result){

			if(null!=piresponse)
    		{
			if(piresponse.trim().length()!=0)
			{
				
				try{

					JSONObject jsonObj = new JSONObject(piresponse.trim());               	
					
					
					if(jsonObj.isNull("notif_posts")==false)
					{
						
						    JSONArray data = jsonObj.getJSONArray("notif_posts");
							JSONObject c = data.getJSONObject(0);
							
							String usrname = c.getString("username");
		                	String userid = c.getString("userid");
							String stamp = c.getString("stamp");
							String pid = c.getString("pid");
							String body = c.getString("body");
							String face = c.getString("face");
		                	String pimg = c.getString("pimg");
		                	String IMG = c.getString("IMG");
		                	String finalrating  = c.getString("finalrating");
		                	double rating = c.getDouble("rating");
		                	int ratecount = c.getInt("ratecount");
		                	
		                    //double realrating = rating / ratecount;
		                    						                   
		                    
		                	if(face.length()>0)
		                	{
		                        	byte[] imgdata = Base64.decode(face, 0);
		            				image.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length),150));
		                	}
		                	else
		                	{
		                		    image.setImageResource(R.drawable.me);
		                	}
		                    
		            		if(body.length()<1)
		            		{
		            			post_body.setVisibility(View.GONE);
		            		}
		            		else
		            		{
		            			Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
		            			String atMentionScheme = "profile://";
		            			
		            			Pattern HashPattern = Pattern.compile("#([A-Za-z0-9_]+)");
		            			String HashScheme = "hashtag://";

		            			TransformFilter transformFilter = new TransformFilter() {
		            			        //skip the first character to filter out '@'
		            			        public String transformUrl(final Matcher match, String url) {
		            			                return match.group(1);
		            			        }
		            			};
		            			
								
								uname.setText("@"+usrname);						
								if(usrname.equals(utilhelper.getUSER()))
								{
								Linkify.addLinks(uname, atMentionPattern, "myprofile://", null, transformFilter); 
								}
								else
								{
								Linkify.addLinks(uname, atMentionPattern, atMentionScheme, null, transformFilter); 
								}
								
								stripUnderlines(uname);
		
		            			
		            			post_body.setText(body);

		            			Linkify.addLinks(post_body, Linkify.ALL);
		            			Linkify.addLinks(post_body, atMentionPattern, atMentionScheme, null, transformFilter); 
		            			Linkify.addLinks(post_body, HashPattern, HashScheme, null, transformFilter); 
		                    
		            		}
		            		
		            		timestamp.setText(stamp);

		                    
	                     	if(IMG.compareTo("YES")==0)
	                    	{
	                     		pimage.setVisibility(View.VISIBLE);
	                     		byte[] imgdata = Base64.decode(pimg, 0);
	            				pimage.setImageBitmap(BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length));
	            		    }
	            			else
	            			{
	            		    	pimage.setVisibility(View.GONE);
	            			}
	                     	
	                	    RateBar.setRating(Float.parseFloat(finalrating));

					}

				} catch (JSONException e){

				} 

			}


    		}
			
			//CProgress.setVisibility(View.GONE);

			//Toast.makeText(getApplicationContext(),piresponse.trim() , Toast.LENGTH_LONG).show();
		    //confirmFireMissiles();


		}

		protected void onProgressUpdate(Integer... progress){

		}

		protected void postData() {
			// Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
			
			HttpPost httppost = new HttpPost(LoginActivity.SERVER+"get_notified.php");

			String Header = "get_notif_post";

			try {
				// Add your data 

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
				nameValuePairs.add(new BasicNameValuePair("PID", PID ));
				//nameValuePairs.add(new BasicNameValuePair("UNAME", UsrNAME ));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();


				//HttpResponse response;


				// Execute HTTP Post Request
				piresponse = httpclient.execute(httppost, responseHandler);




		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
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
                    	  new PostsAyncTask().execute();
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
	

	

	@SuppressLint("ValidFragment")
	public class FireMissilesDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(piresponse.trim())
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


	public void confirmFireMissiles() {
		DialogFragment newFragment = new FireMissilesDialogFragment();
		newFragment.show(getFragmentManager(), "missiles");
	}


	
//////////////////////////////////////////////////////////////////////////////////////////

private static void stripUnderlines(TextView textView) {
Spannable s = (Spannable)textView.getText();
URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
for (URLSpan span: spans) {
int start = s.getSpanStart(span);
int end = s.getSpanEnd(span);
s.removeSpan(span);
span = new URLSpanNoUnderline(span.getURL());
s.setSpan(span, start, end, 0);
}
textView.setText(s);
}



}
