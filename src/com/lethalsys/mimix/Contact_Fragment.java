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
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class Contact_Fragment extends Fragment {
 
	//public final static String USR_iD  = "com.lethalsys.mimix.UseriD";
	public final static String USR_DETAIL = "com.lethalsys.mimix.U_DETAIL";
	public final static String GETALL_MESSAGE = "com.lethalsys.mimix.GETALL_MSG";


	private String iresponse;
	private String contact;
	private String following;
	//private String Fwhat;
	//private String FAll;
	private String UserID;
	ListView list;
	ListView trendlist;
	String cpycontact=null;

	Cursor model=null;
	Database_Two helper=null;
	ContactDatabaseAdapter adapter =null;
	
	
	Cursor trendmodel=null;
    TrendHashDatabaseAdapter trendadapter =null;

	AtomicBoolean isActive=new AtomicBoolean(true);

	JSONArray data = null;
	static Util_Database utilhelper;

	public static Handler allcUiHandler = null;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	Boolean ErrorDlg = true;
	
	
	 public ProgressBar CProgress=null;
	 //public TextView CProgresstxt=null;
	 
	 LinearLayout emptyview;
	 private Button load_more;
   
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
         
        
		utilhelper=new Util_Database(getActivity());
		UserID = utilhelper.getUSER_ID();

		helper=new Database_Two(getActivity());

		/*CProgress =(ProgressBar)findViewById(R.id.maincrow_loading);
		//CProgress.setVisibility(View.GONE);
		
		CProgresstxt =(TextView)findViewById(R.id.maincrow_loading_txt);*/
		//CProgresstxt.setVisibility(View.GONE);

		list=(ListView)rootView.findViewById(R.id.showMAIN_contacts);
			
		  LayoutInflater LInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		   emptyview = (LinearLayout) LInflater.inflate(
					R.layout.lv_set_empty, null, false);
			
		
	       LayoutInflater hInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        LinearLayout hFooterView = (LinearLayout) hInflater.inflate(
					R.layout.trending_view, null, false);
	        
	        trendlist=(ListView) hFooterView.findViewById(R.id.trendHash_list);
	        
		      /* LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
						R.layout.loader_footer, null, false);
				
				CProgress  = (ProgressBar) mFooterView.findViewById(R.id.mainrow_loading);
				//CProgress.setVisibility(View.GONE);
				 
				CProgresstxt  = (TextView) mFooterView.findViewById(R.id.mainrow_loading_txt);			
			
	        //trendlist.addFooterView(mFooterView);*/
			load_more = (Button) hFooterView.findViewById(R.id.maincrow_load_more);
			load_more.setOnClickListener(clickListener);
			
			CProgress  = (ProgressBar) hFooterView.findViewById(R.id.maincrow_loading);
			 
			//CProgresstxt  = (TextView) hFooterView.findViewById(R.id.maincrow_loading_txt);
			        
			list.addFooterView(hFooterView);
		
		//helper.ClearMAIN();

		model=helper.getMAINcontacts();
		getActivity().startManagingCursor(model);
		adapter=new ContactDatabaseAdapter(model);
		list.setAdapter(adapter);
		list.setOnItemClickListener(onContactClick);

		trendmodel=helper.getAllTrendingHash();
		getActivity().startManagingCursor(trendmodel);
		trendadapter=new TrendHashDatabaseAdapter(trendmodel);
		trendlist.setAdapter(trendadapter);
		trendlist.setOnItemClickListener(onTrendClick);
		
		trendlist.setDivider(null);

		


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
		
	

        
        return rootView;
    }
	
	
	
	public void onResume()
	{
		super.onResume();
		ErrorDlg = true;

		reload();
		
	}
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}

	
	OnClickListener clickListener = new OnClickListener() {
	    @Override
	    public void onClick(final View v) {
	        switch(v.getId()) {
               case R.id.maincrow_load_more:
            	   GO_ALL(v);
            	  break;
                default:
                  break;
	        }
	    }
	};
	


	

	public void reload() {

		mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		{

			new MyAsyncTask().execute("Get_MAIN");
		}
		else
		{
			//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
			ShowErrorDialoag();
		}
	}


	public void GO_ALL(View v)
	{

		Intent intent = new Intent(getActivity(), AllContactsActivity.class);
		startActivity(intent);
	}
	
	public void confirmFireMissiles() {
		DialogFragment newFragment = new FireMissilesDialogFragment();
		newFragment.show(getActivity().getFragmentManager(), "missiles");
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





	private AdapterView.OnItemClickListener onContactClick=new
			AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent,
				View view, int position,
				long id) {
			model.moveToPosition(position);
			cpycontact = helper.getMAINcontactName(model);
			
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
	
			
			
			
			private AdapterView.OnItemClickListener onTrendClick=new
					AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					trendmodel.moveToPosition(position);
					//String HashTag = helper.getTrendingHash(trendmodel);
										
					
					}
					};
			
			
			
	

		    public void Go_My_Profile() {
		    	Intent intent = new Intent(getActivity(), MyProfileActivity.class);
		    	//intent.addFlags(Intent.);		    	
		    	startActivity(intent);
		    	}  
						




	public void Go_Profile(String usr) {
		Intent intent = new Intent(getActivity(), ProfileActivity.class);
		intent.putExtra(USR_DETAIL, usr);
		startActivity(intent);
	}  




	private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

		@Override
		protected void onPreExecute() {
	    super.onPreExecute();
	    
		CProgress.setVisibility(View.VISIBLE);
		//CProgresstxt.setVisibility(View.VISIBLE);
		
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
							
							JSONArray data2 = jsonObj.getJSONArray("trendhash");
							
							helper.ClearTrendingHash();
							
							for(int i=0;i<data2.length();i++)
							{
							JSONObject t = data2.getJSONObject(i);
							String hash = t.getString("hash");
							String count = t.getString("count");
							
							helper.insertTrendingHash(hash,count);

							}
							trendmodel.requery();
							
							helper.ClearMAIN();

							for(int i=0;i<data.length();i++)
							{
								JSONObject d = data.getJSONObject(i);
								contact = d.getString("username");
								String gender = d.getString("gender");
								String location = d.getString("location");
								String workplace = d.getString("workplace");
								String userid = d.getString("userid");
								following = d.getString("following");
								String detail;
								
								if(workplace.length()!=0)
								{
								   detail = workplace;
								}
								else
								{
								   detail = location;
								}

								helper.insertMAIN(contact, detail, userid, following);						
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
                           		//Toast.makeText(getApplicationContext(),msg , Toast.LENGTH_LONG).show();

	                    		
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
	                            	
              		                utilhelper.insertFACE(uid, user, gender, phone, email, location, workplace, occupation, bio, reputation, isVerified, award, numfollowers, numfollowing);            			

	                                    helper.updateMAIN(user, "yes");
	                                    model.requery();
	                                    
	    	                			Toast.makeText(getActivity(),"you are now following "+user , Toast.LENGTH_LONG).show();

	                        		
	                        	}
	                        	
	                           	else if("isUnFollowed".compareTo(msg)==0)
	                        	{
	                           		JSONArray Fdata = jsonObj.getJSONArray("face");	
		                           		
		                           	
		                               	JSONObject c = Fdata.getJSONObject(0);
		                            	String user = c.getString("username");
		                               	String uid = c.getString("userid");
		                               
		                            	
		                           	utilhelper.DltFACE(uid);
                                    helper.updateMAIN(user, "no");
                                    model.requery();
                                    
       	                			Toast.makeText(getActivity(),user+" has been unfollowed" , Toast.LENGTH_LONG).show();

	                        	}
	                           	else
	                           	{
    	                			Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

	                           	}
	  
	            	    		
	            	    		
	                    	
	                		
	                	}
	                	}

					}catch (JSONException e){

					} 

			 
					//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
				    // confirmFireMissiles();
				
			}

    		}

    		CProgress.setVisibility(View.GONE);
    		//CProgresstxt.setVisibility(View.GONE);
    		
    		
    		if(adapter.isEmpty())
    		{
    			list.removeHeaderView(emptyview);
    		    list.addHeaderView(emptyview);
    		}
    		else
    		{
    			list.removeHeaderView(emptyview);
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
				nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
				nameValuePairs.add(new BasicNameValuePair("CNAME", cpycontact ));
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
            builder.setView(getActivity().getLayoutInflater().inflate(R.layout.network_error_alert, null))
                   .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   reload();
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
	    newFragment.show(getActivity().getFragmentManager(), "neterror");
	}
    
	
	
  



	class ContactDatabaseAdapter extends CursorAdapter {


		ContactDatabaseAdapter(Cursor c) {
			super(getActivity(),c);
		}
				
		@Override
		public void bindView(View row, Context ctxt,Cursor c) {
			contactDatabaseHolder holder=(contactDatabaseHolder)row.getTag();
			holder.populateFrom(c, helper);
			
		   /* if(lvboolaray5[c.getPosition()] == false)
		    {		    	
		    	//holder.cfollowbtn.setVisibility(View.GONE);
		    	holder.cfollowbtn.setText("Follow");
		    	holder.cfollowbtn.setTag("N");
		    }
		    else
		    {
		    	//holder.cfollowbtn.setVisibility(View.VISIBLE);
		    	holder.cfollowbtn.setText("Unfollow");
		    	holder.cfollowbtn.setTag("Y");
		    }*/
			
			if(helper.getMAINFollowing(c).equals("yes")) {
				holder.cfollowbtn.setVisibility(View.VISIBLE);
				holder.cfollowbtn.setTag("Y");
			}

			if(helper.getMAINFollowing(c).equals("no")){

				holder.cfollowbtn.setVisibility(View.VISIBLE);
				holder.cfollowbtn.setTag("N");
			}

			
		}
		@Override
		public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.maincontacts_row, parent, false);
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

			//cfollowbtn=(ImageButton)row.findViewById(R.id.maincrow_followbtn);
			cfollowbtn=(TextView)row.findViewById(R.id.maincrow_followbtn);
			cname=(TextView)row.findViewById(R.id.maincrow_username);
			cdetail=(TextView)row.findViewById(R.id.maincrow_detail);
			cimage=(NetworkImageView)row.findViewById(R.id.maincrow_image);
			
			cimage.setDefaultImageResId(R.drawable.me);

		}


		void populateFrom(Cursor c, Database_Two helper) {
			//cname.setText(helper.getMAINcontactName(c));
			cdetail.setText(helper.getMAINcontactDetail(c));
			
			Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
			String atMentionScheme = "profile://";

		
			TransformFilter transformFilter = new TransformFilter() {
				//skip the first character to filter out '@'
				public String transformUrl(final Matcher match, String url) {
					return match.group(1);
				}
			};

			
			cname.setText("@"+helper.getMAINcontactName(c));
			if(helper.getMAINcontactName(c).equals(utilhelper.getUSER()))
			{
			Linkify.addLinks(cname, atMentionPattern, "myprofile://", null, transformFilter); 
			}
			else
			{
			Linkify.addLinks(cname, atMentionPattern, atMentionScheme, null, transformFilter); 
			}

			stripUnderlines(cname);

	
        	/*byte[] pimgdata = Base64.decode(helper.getMAINcontactbmp(c), 0);
        	if(pimgdata.length!=0)
        	{
        		cimage.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeByteArray(pimgdata, 0, pimgdata.length), 150));
        		//cimage.setImageBitmap(BitmapFactory.decodeByteArray(pimgdata, 0, pimgdata.length));
        	}
        	else
        	{
        		cimage.setImageResource(R.drawable.me);
        	}*/
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();

			cimage.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getMAINcontactUID(c).toString()+".png", imageLoader);

        	
        	
			if(helper.getMAINFollowing(c).equals("yes")) {

				cfollowbtn.setText("Unfollow");
			}

			if(helper.getMAINFollowing(c).equals("no")){

				cfollowbtn.setText("Follow");
				
			}

			/*if(helper.getMAINFollowing(c).equals("me"))
			{
				//cfollowbtn.setVisibility(View.GONE);
				lvboolaray5[c.getPosition()] = false;
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
			cpycontact = helper.getMAINcontactName(model);

			//ImageButton cfbtn=(ImageButton)v.findViewById(R.id.maincrow_followbtn);
			 TextView cfbtn=(TextView)v.findViewById(R.id.maincrow_followbtn);

			if(cfbtn.getTag()=="Y")
			{
				//cfbtn.setImageResource(R.drawable.follow_txt);
				cfbtn.setText("Follow");
				cfbtn.setTag("N");
				
			}
			else
			{
				//cfbtn.setImageResource(R.drawable.unfollow_txt);
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



	class TrendHashDatabaseAdapter extends CursorAdapter {


		TrendHashDatabaseAdapter(Cursor c) {
			super(getActivity(),c);
		}
				
		@Override
		public void bindView(View row, Context ctxt,Cursor c) {
			TrendHashDatabaseHolder holder=(TrendHashDatabaseHolder)row.getTag();
			holder.populateFrom(c, helper);
		
		}
		@Override
		public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.trendhash_row, parent, false);
			TrendHashDatabaseHolder holder=new TrendHashDatabaseHolder(row);
			row.setTag(holder);
			
			return(row);
		}

	}    


	
	static class TrendHashDatabaseHolder {
		private TextView Trend =null;
		private TextView count =null;
		//private View row=null;
		TrendHashDatabaseHolder(View row) {
			//this.row=row;

			Trend=(TextView)row.findViewById(R.id.trendrow_hash);
			count=(TextView)row.findViewById(R.id.trendrow_count);

		}


		void populateFrom(Cursor c, Database_Two helper) {
		
            
Pattern atMentionPattern = Pattern.compile("#([A-Za-z0-9_]+)");
String atMentionScheme = "hashtag://";


TransformFilter transformFilter = new TransformFilter() {
//skip the first character to filter out '@'
public String transformUrl(final Matcher match, String url) {
return match.group(1);
}
};

Trend.setText(helper.getTrendingHash(c));
count.setText(helper.getTrendingCount(c)+" people talking about this");

Linkify.addLinks(Trend, Linkify.ALL);
Linkify.addLinks(Trend, atMentionPattern, atMentionScheme, null, transformFilter); 

stripUnderlines(Trend);

}
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