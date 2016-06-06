package com.lethalsys.mimix;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import android.database.Cursor;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;


@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class AllContactsActivity extends Activity {

	//public final static String USR_iD  = "com.lethalsys.mimix.UseriD";
	public final static String USR_DETAIL = "com.lethalsys.mimix.U_DETAIL";
	public final static String GETALL_MESSAGE = "com.lethalsys.mimix.GETALL_MSG";

	
	private String iresponse;
	private String contact;
	private String following;
	//private String Fwhat;
	//private String FAll;
	private String Fusr;
	private String UserID;
	ListView list;
	String cpycontact=null;

	Cursor model=null;
	Database_Two helper=null;
	ContactDatabaseAdapter adapter =null;

	AtomicBoolean isActive=new AtomicBoolean(true);

	JSONArray data = null;
	static Util_Database utilhelper;

	public static Handler allcUiHandler = null;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	Boolean ErrorDlg = true;
	Boolean isLoading = false;
	
	
	 ProgressBar CProgress;
	 
	 LinearLayout emptyview;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_contacts);
		
		//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.fadeactionbarbg));

		//getActionBar().setDisplayHomeAsUpEnabled(true);

		/*Intent intent = getIntent();
		    UserID = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE2);*/


			utilhelper=new Util_Database(this);
			UserID = utilhelper.getUSER_ID();

		helper=new Database_Two(this);

		/*CProgress =(ProgressBar)findViewById(R.id.allcrow_loading);
		CProgress.setVisibility(View.GONE);
		
		CProgresstxt =(TextView)findViewById(R.id.allcrow_loading_txt);
		CProgresstxt.setVisibility(View.GONE);*/

		list=(ListView)findViewById(R.id.showALL_contacts);

		
		  LayoutInflater LInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		   emptyview = (LinearLayout) LInflater.inflate(
					R.layout.lv_set_empty, null, false);
			
		
	       LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
					R.layout.loader_footer, null, false);
			
			CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);
			 
			        
			list.addFooterView(mFooterView);
			
		

		model=helper.getAllALLcontacts();
		startManagingCursor(model);
		adapter=new ContactDatabaseAdapter(model);
		list.setAdapter(adapter);
		list.setOnItemClickListener(onPostClick);
		list.setOnScrollListener(onscroll);



		helper.ClearALL();

		allcUiHandler = new Handler() // Receive messages from service class
		{
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				case 0:
				{

				}
				break;

				case 1:
				{
					reload(); 
				}
				break;

				default:
					break;
				}
			}
		};


	}


	public void onResume()
	{
		super.onResume();
		ErrorDlg = true;
		helper.ClearALL();
		reload();
	}
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}



	public void reload() {

		mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		{

			new MyAsyncTask().execute("Get_ALL");
		}
		else
		{
			//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
			ShowNoNetErrorDialoag();
		}
	}

	
	
	private OnScrollListener onscroll= new OnScrollListener() {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
		if(list.getAdapter().getCount()>0)
		{
			if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1
					&& list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
				if(list.getFirstVisiblePosition() == 0 && (list.getChildCount()==0 || list.getChildAt(0).getTop() == 0))
				{
					
				}else{

					if(isLoading==false)
					{ 
						reload();
					}
				}

			}
		}
		else
		{
			//if(isLoading==false)
			//{ 
			//reload();
			//}
		}
			
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			
		}};
		

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



	@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			//getMenuInflater().inflate(R.menu.contacts, menu);

			return true;
		}




	private AdapterView.OnItemClickListener onPostClick=new
			AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent,
				View view, int position,
				long id) {
			model.moveToPosition(position);
			cpycontact = helper.getALLcontactName(model);
			/*helper.getBody(model);
				helper.getDate(model);*/
			
			if(cpycontact.equals(utilhelper.getUSER()))
			{
				Go_My_Profile();
			}
			else
			{
			    Go_Profile(cpycontact);
			}
				
			
			}
			};
	
	

		    public void Go_My_Profile() {
		    	Intent intent = new Intent(this, MyProfileActivity.class);
		    	//intent.addFlags(Intent.);		    	
		    	startActivity(intent);
		    	}  
						




	public void Go_Profile(String usr) {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(USR_DETAIL, usr);
		startActivity(intent);
	}  




	private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

		@Override
		protected void onPreExecute() {
	    super.onPreExecute();
	    
	    isLoading = true;
	    
		CProgress.setVisibility(View.VISIBLE);
		
		}
		
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0]);
			return null;
		}

		protected void onPostExecute(Double result){


    		if(null!=iresponse)
    		{
    			
    	
			if(iresponse.trim().length()!=0)
			{

					try{
						JSONObject jsonObj = new JSONObject(iresponse.trim());               	
						
						if(jsonObj.isNull("users")==false)
						{
							data = jsonObj.getJSONArray("users");
							
							//helper.ClearALL();
							/*int Incr = utilhelper.getContactPostLoc()+data.length();
	               		    utilhelper.ClearContactPostLoc();
	            		    utilhelper.insertContactPostLoc(Incr);*/
	            		    
	                 	

							for(int i=0;i<data.length();i++)
							{
								JSONObject d = data.getJSONObject(i);
								contact = d.getString("username");
								String gender = d.getString("gender");
								String location = d.getString("location");
								String workplace = d.getString("workplace");
								String userid = d.getString("userid");								following = d.getString("following");
								String detail;
								
								if(workplace.length()!=0)
								{
								   detail = workplace;
								}
								else
								{
								   detail = location;
								}

								helper.insertALL(contact, detail, userid, following);						
							}
							model.requery();

						}
	                	else
	                	{
	                		//Toast.makeText(getApplicationContext(),"isnull" , Toast.LENGTH_LONG).show();
	                		if(jsonObj.isNull("data")==false)
							{
	                		JSONArray data = jsonObj.getJSONArray("data");
	                    	
	                    	
	            			
	                    		JSONObject d = data.getJSONObject(0);
	                    		String msg = d.getString("msg");
	                    		
	                           	if("isFollowed".compareTo(msg)==0)
	                        	{
	                           		JSONArray Fdata = jsonObj.getJSONArray("face");	
	                           			                           		
	                           	  
	                               	JSONObject c = Fdata.getJSONObject(0);
	                               	String uid = c.getString("userid");
	                               	String user = c.getString("username");
	                            	String gender = c.getString("gender");
	                            	String phone = c.getString("phone");
	                            	String email = c.getString("email");
	                            	String location = c.getString("location");
	                            	String workplace = c.getString("workplace");
	                            	String occupation = c.getString("occupation");
	                            	String bio = c.getString("bio");
	                            	String reputation = c.getString("reputation");
	                            	String isVerified = c.getString("isVerified");
	                            	String award = c.getString("award");
	                            	String numfollowers = c.getString("numfollowers");
	                            	String numfollowing = c.getString("numfollowing");
	                            	

	                  		        utilhelper.insertFACE(uid,user, gender, phone, email, location, workplace, occupation, bio, reputation, isVerified, award, numfollowers, numfollowing);            			

	                                 
	                                    
	                                    helper.updateALL("'"+user+"'", "yes");
	                                    model.requery();
	    	                			Toast.makeText(getApplicationContext(),"you are now following "+user , Toast.LENGTH_LONG).show();

	                        		
	                        	}
	                        	
	                           	else if("isUnFollowed".compareTo(msg)==0)
	                        	{
	                           		JSONArray Fdata = jsonObj.getJSONArray("face");	
		                           		
		                           	
		                               	JSONObject c = Fdata.getJSONObject(0);
		                            	String user = c.getString("username");
		                               	String uid = c.getString("userid");
		                               	/*String pimg = c.getString("profilepic");
		                               	String occup = c.getString("occupation");
		                            	String dept = c.getString("department");
		                            	String phn = c.getString("phone");
		                            	String email = c.getString("email");
		                            	String numfollowers = c.getString("numfollowers");
		                            	String numfollowing = c.getString("numfollowing");*/
		                 
		                            utilhelper.DltFACE(uid);	
                                    helper.updateALL("'"+user+"'", "no");
                                    model.requery();
                                    
       	                			Toast.makeText(getApplicationContext(),user+" has been unfollowed" , Toast.LENGTH_LONG).show();

	                        	}
	                           	else
	                           	{
    	                			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

	                           	}
	  
	            	    		
	            	              	    		
	            	    		
	                    	
	                		
	                	}
	                	}

					}catch (JSONException e){

					} 

			
					//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
					//confirmFireMissiles();
				
			}

    		}

    		CProgress.setVisibility(View.GONE);
    		isLoading = false;
    		
    		
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


		protected void onProgressUpdate(Integer... progress){
		}

		protected void postData(String Header) {
			// Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
			
			HttpPost httppost;


			if("FollowORunfollow".compareTo(Header)==0)
			{   
				httppost = new HttpPost(LoginActivity.SERVER+"FollowORunfollow.php");
				
			}



			else
			{
				httppost = new HttpPost(LoginActivity.SERVER+"send_contacts.php");
			   
			}




			try {
				// Add your data

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
				nameValuePairs.add(new BasicNameValuePair("UsrNME", Fusr ));
				nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
				nameValuePairs.add(new BasicNameValuePair("CNAME", cpycontact ));
				if(helper.getLastALLcontacts()!=null)
				{
				nameValuePairs.add(new BasicNameValuePair("PostLoc",helper.getLastALLcontacts()));
				}
				else
				{
			    nameValuePairs.add(new BasicNameValuePair("PostLoc","0"));
				}
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();



				//HttpResponse response;


				// Execute HTTP Post Request

				iresponse = httpclient.execute(httppost, responseHandler);





			} catch (IOException e) {
				if(ErrorDlg == true)
				ShowErrorDialoag();
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
                    	  
                    	   if(isLoading==false)
                  			{ 
                    		   reload();
                  			}
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
    
	
	
    @SuppressLint("ValidFragment")
	public class NoNetErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setMessage(LoginActivity.noneterrormsg)
            builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
                   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	  
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
	




	class ContactDatabaseAdapter extends CursorAdapter {


		ContactDatabaseAdapter(Cursor c) {
			super(AllContactsActivity.this,c);
		}
				
		@Override
		public void bindView(View row, Context ctxt,Cursor c) {
			contactDatabaseHolder holder=(contactDatabaseHolder)row.getTag();
			holder.populateFrom(c, helper);
			
		    /*if(lvboolaray2[c.getPosition()] == false)
		    {		    	
		    	//holder.cfollowbtn.setVisibility(View.GONE);
				//cfollowbtn.setImageResource(R.drawable.ic_untick);
		    	holder.cfollowbtn.setText("Follow");
		    	holder.cfollowbtn.setTag("N");
		    }
		    else
		    {
		    	//holder.cfollowbtn.setVisibility(View.VISIBLE);
				//cfollowbtn.setImageResource(R.drawable.ic_untick);
		    	holder.cfollowbtn.setText("Unfollow");
		    	holder.cfollowbtn.setTag("Y");
		    }*/
			if(helper.getALLFollowing(c).equals("yes")) {
				holder.cfollowbtn.setVisibility(View.VISIBLE);
				holder.cfollowbtn.setTag("Y");
			}

			if(helper.getALLFollowing(c).equals("no")){

				holder.cfollowbtn.setVisibility(View.VISIBLE);
				holder.cfollowbtn.setTag("N");
			}

		}
		@Override
		public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.allcontacts_row, parent, false);
			contactDatabaseHolder holder=new contactDatabaseHolder(row);
			row.setTag(holder);
			
			return(row);
		}

	}    





static class contactDatabaseHolder {
		private TextView cname=null;
		private TextView cdetail=null;
		private NetworkImageView cimage=null;
		//private ImageButton cfollowbtn=null;
		private TextView cfollowbtn=null;
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		contactDatabaseHolder(View row) {

			cfollowbtn=(TextView)row.findViewById(R.id.allcrow_followbtn);
			cname=(TextView)row.findViewById(R.id.allcrow_username);
			cdetail=(TextView)row.findViewById(R.id.allcrow_detail);
			cimage=(NetworkImageView)row.findViewById(R.id.allcrow_image);	

			cimage.setDefaultImageResId(R.drawable.me);
		}


		void populateFrom(Cursor c, Database_Two helper) {
			//cname.setText(helper.getALLcontactName(c));
			cdetail.setText(helper.getMAINcontactDetail(c));

			
			Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
			String atMentionScheme = "profile://";


			TransformFilter transformFilter = new TransformFilter() {
				//skip the first character to filter out '@'
				public String transformUrl(final Matcher match, String url) {
					return match.group(1);
				}
			};

			
			cname.setText("@"+helper.getALLcontactName(c));
			if(helper.getALLcontactName(c).equals(utilhelper.getUSER()))
			{
			Linkify.addLinks(cname, atMentionPattern, "myprofile://", null, transformFilter); 
			}
			else
			{
			Linkify.addLinks(cname, atMentionPattern, atMentionScheme, null, transformFilter); 
			}
			
			stripUnderlines(cname);

	
	
        	/*byte[] pimgdata = Base64.decode(helper.getALLcontactbmp(c), 0);
        	if(pimgdata.length!=0)
        	{
        		cimage.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeByteArray(pimgdata, 0, pimgdata.length),150));
        	}
        	else
        	{
        		cimage.setImageResource(R.drawable.me);
        	}*/
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();

			cimage.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getALLcontactUID(c).toString()+".png", imageLoader);
			

        	
        	
			if(helper.getALLFollowing(c).equals("yes")) {

				cfollowbtn.setText("Unfollow");
			}

			if(helper.getALLFollowing(c).equals("no")){


				cfollowbtn.setText("Follow");
			}

			/*if(helper.getALLFollowing(c).equals("me"))
			{
				//cfollowbtn.setVisibility(View.GONE);
				lvboolaray2[c.getPosition()] = false;
				//cfollowbtn.setImageResource(0);
				cfollowbtn.setLayoutParams(new LinearLayout.LayoutParams(
			    		ViewGroup.LayoutParams.WRAP_CONTENT,
			    		ViewGroup.LayoutParams.WRAP_CONTENT));

			}*/




		}
	} 




	public void FollowORunfollow(View v) {

		final int position = list.getPositionForView(v);
		if (position != ListView.INVALID_POSITION) {
			model.moveToPosition(position);
			cpycontact = helper.getALLcontactName(model);

			//ImageButton cfbtn=(ImageButton)v.findViewById(R.id.allcrow_followbtn);
			TextView cfbtn=(TextView)v.findViewById(R.id.allcrow_followbtn);
			if(cfbtn.getTag()=="Y")
			{
				//cfbtn.setImageResource(R.drawable.ic_tick);
				cfbtn.setText("Follow");
				cfbtn.setTag("N");
			}
			else
			{
				//cfbtn.setImageResource(R.drawable.ic_untick);
				cfbtn.setText("Unfollow");
				cfbtn.setTag("Y");
			}

			/*Drawable fDraw = cfbtn.getBackground();
				Drawable sDraw = getResources().getDrawable(R.drawable.ic_followed);

				if(fDraw.hashCode()==sDraw.hashCode())
				{
					cfbtn.setImageResource(R.drawable.ic_unfollowed);
				}
				else
				{
					cfbtn.setImageResource(R.drawable.ic_followed);
				}*/
			//Toast.makeText(getApplicationContext(),cpycontact , Toast.LENGTH_LONG).show();
			new MyAsyncTask().execute("FollowORunfollow");

		}	


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
