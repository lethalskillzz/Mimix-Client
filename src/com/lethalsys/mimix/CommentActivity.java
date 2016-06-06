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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CommentActivity extends Activity {

	public static Handler CommentActivityHandler = null;
	
	ListView list;
	
	private String UserID;
	Util_Database utilhelper;

	private ProgressBar CProgress;
	
	private String PID;
	
	private EditText comment_edttxt;
	private Button comment_btn;
	private ProgressBar comment_progressBar;
	//private TextView comment_count;
	
	LinearLayout emptyview;
	
    String response;
    Boolean ErrorDlg = true;
	
	String msg;
	String Cpycmnt;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	JSONArray data = null;
	
	List<StringBox> model=new ArrayList<StringBox>();
	CommentAdapter adapter=null;
	
	private String isTogether;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
				
		utilhelper = new Util_Database(this);
		UserID = utilhelper.getUSER_ID();
		
		Intent intent = getIntent();
	    PID = intent.getStringExtra(Post_Fragment.PostID);
	    
	    comment_edttxt = (EditText) findViewById(R.id.comment_edttxt);
	    comment_btn = (Button) findViewById(R.id.comment_btn);
	    comment_progressBar = (ProgressBar) findViewById(R.id.comment_progressBar);
	    
	    //comment_count=(TextView)findViewById(R.id.comment_count);
	
		
		list=(ListView)findViewById(R.id.listview_comments);
		
		  LayoutInflater LInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		   emptyview = (LinearLayout) LInflater.inflate(
					R.layout.lv_set_empty, null, false);
			
		
		
        LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
				R.layout.loader_footer, null, false);
		
		CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);		 
		
		list.addFooterView(mFooterView);
		adapter=new CommentAdapter();
		list.setAdapter(adapter);


	}
	
	

	public void onResume()
	{
		super.onResume();
		ErrorDlg = true;
		add_get_comment("get_comment");
	}
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}
	
	
	
	public void Clck_post(View v)
	{
		if(comment_edttxt.getText().toString().length()<1){
			//Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show();

		}
		else
		{
		Cpycmnt = comment_edttxt.getText().toString();

		comment_edttxt.setText("");
		
		add_get_comment("add_comment");
		}
		
	}
	
    public void add_get_comment(final String Header){
    	
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        

    	
        new AsyncTask<String, Integer, Double>() {
	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		
		if(Header.equals("get_comment"))
		{
			CProgress.setVisibility(View.VISIBLE);

		}
		else
		{
			comment_btn.setVisibility(View.GONE);
			comment_progressBar.setVisibility(View.VISIBLE);
			isFinish(false);
		}
	}
	
	@Override
	protected Double doInBackground(String... params) {

        // Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
			
    	HttpPost httppost = new HttpPost(LoginActivity.SERVER+"add_get_comment.php");
		
    	 
    	try {
    		
       if(Header.equals("get_comment"))
       {	
    	// Add your data	
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
    	nameValuePairs.add(new BasicNameValuePair("UID",UserID));
    	nameValuePairs.add(new BasicNameValuePair("pid", PID));      
    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
       }
       else
       {
       	// Add your data	
       	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
       	nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
       	nameValuePairs.add(new BasicNameValuePair("UID",UserID));
       	nameValuePairs.add(new BasicNameValuePair("comment",Cpycmnt));
       	nameValuePairs.add(new BasicNameValuePair("pid", PID));      
       	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
       }
       
       
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
			  				
			  if(Header.equals("get_comment"))
			  {
					JSONArray zdata = jsonObj.getJSONArray("extra");
					JSONObject e = zdata.getJSONObject(0);
					String commentcount  = e.getString("commentcount");
					sendmsg(commentcount);

				  
				if(jsonObj.isNull("comments")==false)
				{
                    
					adapter.clear();
					
					JSONArray data = jsonObj.getJSONArray("comments");
					for(int i=0;i<data.length();i++)
					{
						JSONObject c = data.getJSONObject(i);
						String usrname = c.getString("username");
						String stamp = c.getString("stamp");
						String comment = c.getString("comment");
	                	String userid = c.getString("userid");
	                	
	                	StringBox r=new StringBox();
	                	
       					r.setcommentusr(usrname);
						r.setcomment(comment);
						r.setcommentuid(userid);
				        
					r.setcommentdate(stamp);
						
						adapter.add(r);
						

			    		
					}

	                 
					}
				

			  }
			  else
			  {
              	
					if(jsonObj.isNull("data")==false)
					{
						data = jsonObj.getJSONArray("data");

						JSONObject d = data.getJSONObject(0);
						String msg  = d.getString("msg");
						
						if("ok".compareTo(msg)==0)
						{
							
							//ClosMe();
							/*String commentcount  = d.getString("commentcount");
							sendmsg(commentcount);*/
							add_get_comment("get_comment");


						}

						else
						{
							Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
							
						}

						
					}
				
			  }
			
			} catch (JSONException e){

			} 



		}
		}		
	    
	if(Header.equals("get_comment"))
    {
		CProgress.setVisibility(View.GONE);
    }
	else
	{
   		comment_progressBar.setVisibility(View.GONE);
		comment_btn.setVisibility(View.VISIBLE);
		isFinish(true);
	}
		//Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
		//confirmFireMissiles();
	
	if(adapter.isEmpty())
	{
		list.removeFooterView(emptyview);
	    list.addFooterView(emptyview);
	}
	else
	{
		list.removeFooterView(emptyview);
	}
	

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
	public class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(response.trim())
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
                    	  // Add_get_comment("");
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
    
    
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}
	
	
	
	
	

	class CommentAdapter extends ArrayAdapter<StringBox> {
		CommentAdapter() {
			super(CommentActivity.this, R.layout.comment_row, model);
		}
		public View getView(int position, View convertView,
				ViewGroup parent) {
			View row=convertView;
			CommentHolder holder=null;
			if (row==null) {
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.comment_row, parent, false);
				holder=new CommentHolder(row);
				row.setTag(holder);
			}
			else {
				holder=(CommentHolder)row.getTag();
			}
			holder.populateFrom(model.get(position), utilhelper);
			return(row);
		}
	}
	
	
	
	
static class CommentHolder {
		private TextView uname=null;
		private TextView comment=null;
		private TextView date_time=null;
		private NetworkImageView pimg=null;
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		CommentHolder(View row) {
		
		uname=(TextView)row.findViewById(R.id.commentrow_username);
		comment=(TextView)row.findViewById(R.id.commentrow_comment);
		date_time=(TextView)row.findViewById(R.id.commentrow_date_time);
		pimg=(NetworkImageView)row.findViewById(R.id.commentrow_image);
		
		pimg.setDefaultImageResId(R.drawable.me);
		}

		void populateFrom(StringBox r, Util_Database utilhelper) {
			//uname.setText(r.getcommentusr());
			//post_body.setText(r.getBody());
			//date_time.setText(r.);
			
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
			
			comment.setText(r.getcomment());
			
			uname.setText("@"+r.getcommentusr());
			Linkify.addLinks(uname, atMentionPattern, atMentionScheme, null, transformFilter);
			
			
			if(r.getcommentusr().equals(utilhelper.getUSER()))
			{
			Linkify.addLinks(uname, atMentionPattern, "myprofile://", null, transformFilter); 
			}
			else
			{
			Linkify.addLinks(uname, atMentionPattern, atMentionScheme, null, transformFilter); 
			}
			
			stripUnderlines(uname);

	
			
			

			Linkify.addLinks(comment, Linkify.ALL);
			Linkify.addLinks(comment, atMentionPattern, atMentionScheme, null, transformFilter); 
			Linkify.addLinks(comment, HashPattern, HashScheme, null, transformFilter); 
			
			/*if(r.getcommentpimg()!=null)
			{
				pimg.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeByteArray(r.getcommentpimg(), 0, r.getcommentpimg().length),150));
			}
			else
			{
				pimg.setImageResource(R.drawable.me);
			}*/
			
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();

			pimg.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+r.getcommentuid()+".png", imageLoader);

			
		
			
	/*  if(utilhelper.getFACEIMG(r.getUid())!=null)
	  {
							  
			if(utilhelper.getFACEIMG(r.getUid()).equals("YES"))
			{
				Bitmap bitmap = BitmapFactory.decodeByteArray(utilhelper.getFACEPPIC(r.getUid()), 0, utilhelper.getFACEPPIC(r.getUid()).length);
				image.setImageBitmap(bitmap);
			}
			else
			{
				image.setImageResource(R.drawable.me);
			}
	  }
		else
		{
			image.setImageResource(R.drawable.me);
		}*/

				
			
			date_time.setText(r.getcommentdate());
			
            
		}
		
		
	}
		
		



public void sendmsg(String Ccount)
{
  
    CommentActivityHandler = new Handler();
 
    if(null != Post_Fragment.mUiHandler)
    {
    	//if(HomeActivity.homeactv == true)
    	//{
        //first build the message and send.
        //put a integer value here and get it from the Activity handler
        //For Example: lets use 0 (msg.what = 0;)
        //for receiving service running status in the activity
        Message msgToActivity = new Message();
        msgToActivity.what = 3;
        msgToActivity.obj = PID+":"+Ccount;
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
        msgToActivity.what = 3;
        msgToActivity.obj = PID+":"+Ccount;
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
        msgToActivity.what = 3;
        msgToActivity.obj = PID+":"+Ccount;
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
        msgToActivity.what = 3;
        msgToActivity.obj = PID+":"+Ccount;
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
