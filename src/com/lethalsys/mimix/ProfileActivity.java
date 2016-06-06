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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ProfileActivity extends Activity implements OnMenuItemClickListener {

	public final static String F_MSG = "com.lethalsys.mimix.FMESSAGE";
	public final static String F_USR = "com.lethalsys.mimix.FUSER";
	public final static String T_DETAIL = "com.lethalsys.mimix.TARGETDETAIL";
	public final static String PostID = "com.lethalsys.mimix.PID";
	public final static String USR_DETAIL = "com.lethalsys.mimix.U_DETAIL";
	public final static String PPUsrNAME="";

	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	private NetworkImageView prof_image=null;
	//private ImageView prof_option=null;
	//private ImageView prof_follow=null;
	//private ImageView prof_msg=null;
	private ImageButton prof_msg=null;
	private TextView prof_name=null;
	private TextView prof_mail=null;
	private TextView prof_phn=null;
	private TextView prof_occup_workplace=null;
	private TextView location=null;
	private TextView prof_status=null;
	private TextView prof_followbtn=null;
	private LinearLayout prof_dtail=null;
	
	private Button followers=null;
	private Button following=null;
	String iresponse;
	String piresponse;
	String UsrNAME=null;
	String IUsrNAME=null;
	String IUsrNAME2=null;

	String postID;
	
	public static Handler profUiHandler = null;
	
	Util_Database utilhelper;
	
    Boolean isLoading = false;
	
	private String UserID;
	
	private String isTogether;

	ProfileAdapter adapter=null;
	Cursor model=null;
	Database_One helper=null;

	//JSONArray data = null;
	//JSONArray data2 = null;


	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	
	Boolean ErrorDlg = true;
	
	ListView list;
	
	public static Boolean profactv = true;
	
	LinearLayout emptyview;
	private LinearLayout mFooterView;
	
	public ProgressBar CProgress;

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_profile);
		
		//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.fadeactionbarbg));

		//this.setProgressBarIndeterminate(true);  
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		utilhelper = new Util_Database(this);
		UserID = utilhelper.getUSER_ID();

		helper=new Database_One(this);
		
		
		Uri data = getIntent().getData();
		if(data != null){
		    String uri = data.toString();
		   // String username = uri.substring(uri.indexOf("@")+1);
	        String username=uri.toString().split("/")[2];

		    
		    if(username!=null)
		    {
		    	if(username.length()<=15)
		    	{
		    		UsrNAME = username;
		    	}
		    	
		    }
		    //Toast.makeText(getApplicationContext(),username , Toast.LENGTH_LONG).show();
		}
		else
		{
		
		Intent intent2 = getIntent();
		IUsrNAME = intent2.getStringExtra(MyContactsActivity.MYUSR_DETAIL);

		if(IUsrNAME!=null)
		{
			UsrNAME = IUsrNAME;
			//Toast.makeText(getApplicationContext(),UsrNAME2 , Toast.LENGTH_LONG).show();
		}



		Intent intent = getIntent();
		IUsrNAME2 = intent.getStringExtra(ContactsActivity.USR_DETAIL);

		if(IUsrNAME2!=null)
		{
			UsrNAME = IUsrNAME2;
			//Toast.makeText(getApplicationContext(),UsrNAME2 , Toast.LENGTH_LONG).show();
		}
		
		
		Intent intent3 = getIntent();
		String IUsrNAME3 = intent3.getStringExtra(Notification_Fragment.NUSR_DETAIL);

		if(IUsrNAME3!=null)
		{
			UsrNAME = IUsrNAME3;
			//Toast.makeText(getApplicationContext(),IUsrNAME3 , Toast.LENGTH_LONG).show();
		}
		
		
		Intent intent4 = getIntent();
		String IUsrNAME4 = intent4.getStringExtra(MessageActivity.USR_DETAIL);

		if(IUsrNAME4!=null)
		{
			UsrNAME = IUsrNAME4;
		}
		
		
		
		}
		
		setTitle(UsrNAME);
		
		
        LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout mProfilView = (LinearLayout) mInflater.inflate(
				R.layout.profile_view, null, false);
		
		//CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading); 
		//CProgresstxt  = (TextView) mFooterView.findViewById(R.id.row_loading_txt);


		prof_image=(NetworkImageView)mProfilView.findViewById(R.id.profile_image);	
		prof_image.setDefaultImageResId(R.drawable.me);

		prof_msg=(ImageButton)mProfilView.findViewById(R.id.profile_msg);
		//prof_msg=(ImageView)findViewById(R.id.profile_msg);	
		//prof_msg.setImageResource(R.drawable.ic_msg);
		prof_msg.setVisibility(View.INVISIBLE);
		
		prof_followbtn=(TextView)mProfilView.findViewById(R.id.prof_followbtn);
		prof_followbtn.setVisibility(View.GONE);
		
		//prof_option=(ImageView)findViewById(R.id.profile_option);	
		//prof_option.setImageResource(R.drawable.ic_profile_option);

		//prof_follow=(ImageView)findViewById(R.id.profile_follow);
		//prof_follow.setImageResource(R.drawable.ic_follow);

		prof_name=(TextView)mProfilView.findViewById(R.id.profile_name);
		prof_mail=(TextView)mProfilView.findViewById(R.id.profile_mail);
		prof_phn=(TextView)mProfilView.findViewById(R.id.profile_phone);
		prof_occup_workplace=(TextView)mProfilView.findViewById(R.id.profile_occup_workplace);
		location=(TextView)mProfilView.findViewById(R.id.profile_location);
		prof_status=(TextView)mProfilView.findViewById(R.id.profile_status);
	    prof_dtail=(LinearLayout)mProfilView.findViewById(R.id.profile_detail);


		
		RatingBar ratingBar = (RatingBar)mProfilView.findViewById(R.id.prof_ratingbar);
		LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
		stars.getDrawable(2).setColorFilter(Color.parseColor("#68dc2c"), PorterDuff.Mode.SRC_ATOP);

		/*LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
		        stars.getDrawable(2).setColorFilter(getResources().getColor
		(R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
		        stars.getDrawable(1).setColorFilter(getResources().getColor
		(R.color.starPartiallySelected), PorterDuff.Mode.SRC_ATOP);
		        stars.getDrawable(0).setColorFilter(getResources().getColor
		(R.color.starNotSelected), PorterDuff.Mode.SRC_ATOP);*/
		

        
		list=(ListView)findViewById(R.id.showprofile_posts);
		
		
		LayoutInflater LInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		emptyview = (LinearLayout) LInflater.inflate(
				R.layout.lv_set_empty, null, false);



		LayoutInflater fInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mFooterView = (LinearLayout) fInflater.inflate(
				R.layout.loader_footer, null, false);

		CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);
		//CProgress.setVisibility(View.GONE);


		list.addFooterView(mFooterView);
		list.setFooterDividersEnabled(false);		        
		list.addHeaderView(mProfilView);
		
		helper.ClearProfpost();
		
		model=helper.getAllProfpost();
		startManagingCursor(model);
		adapter=new ProfileAdapter(model);
		list.setAdapter(adapter);
		
		list.setOnScrollListener(onscroll);
		list.setOnItemClickListener(onPostClick);
 
 
		followers = (Button)mProfilView. findViewById(R.id.follower_clk);

		following = (Button)mProfilView. findViewById(R.id.following_clk);



		/*mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		{

			new MyAsyncTask().execute("REG");
		}
		else
		{
			Toast.makeText(this, "Network Error: No Internet Connection", Toast.LENGTH_LONG).show();
		}*/	
		
		
	    profUiHandler = new Handler() // Receive messages from service class
        {
            public void handleMessage(Message msg)
            {
                 
            	switch(msg.what)
                {
                      case 0:
                      {
                    	 //model.requery();
                      }
                      break;
                      
                      case 1:
                      {
                    	  rload();	                          
                      }
                      break;
                      
                      
                      case 2:
                      {
                    	  String coll;
                    	  String frag[];
                    	  
                      	  coll = (String)msg.obj;
                    	  frag = coll.trim().split(":");
                    	  
                    	  helper.updateProfpostRate(frag[0],frag[1],frag[2]);
                    	  model.requery();
                    	  
                  		
                    	  //ShowTost(helper.testgetRate(frag[0]));
                      }
                      break;
                      
   	    		   
   	    		   case 3:
   	    		   {
   	    			   String coll;
   	    			   String frag[];

   	    			   coll = (String)msg.obj;
   	    			   frag = coll.trim().split(":");

   	    			   helper.updateProfpostComment(frag[0],frag[1]);
   	    			   model.requery();

   	    		   }
   	    		   break;

 
                    default:
                    break;
                }
            }
        };
	
        QuerryPROF();
        

	}
	
	
	void set_pBar(Boolean bl)
	{
	  setProgressBarIndeterminateVisibility(bl);  
	}
	
	@Override
	public void onResume() {
	super.onResume();
	
	profactv = true;
	ErrorDlg = true;
	//helper.ClearProfpost();
	rload();
	}
	
	
	@Override
	public void onPause() {
	super.onPause();
	
	profactv = false;
	ErrorDlg = false;
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
						rload();
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
		

		
		
		
		private AdapterView.OnItemClickListener onPostClick=new
				AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
				View view, int position,
				long id) {
				model.moveToPosition(position-1);
				
				if(helper.getProfpostType(model).equals("YES"))
				{

					if(helper.getProfpost_isExpanded(model).equals("NO"))
					{
						ProfileHolder holder = (ProfileHolder)view.getTag();
						holder.pimage.setAdjustViewBounds(true);
						helper.updateProfpost_isExpanded(helper.getProfpostPID(model), "'"+"YES"+"'");
					}
					else
					{
						ProfileHolder holder = (ProfileHolder)view.getTag();
						holder.pimage.setAdjustViewBounds(false);
						holder.pimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
						helper.updateProfpost_isExpanded(helper.getProfpostPID(model), "'"+"NO"+"'");
					}
					
				}
				


				
				}
				};
				
			
		
	
	
	
	public void rload() {
		
		mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		{

			new MyAsyncTask().execute("get_detail");
			new PostsAyncTask().execute();

		}
		else
		{
			//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
			ShowNoNetErrorDialoag();
		}			
	}

	public void GoIMessanger(View v) {
		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(T_DETAIL, UsrNAME);
		startActivity(intent);
	}

	public void GoContacts1(View v) {
		Intent intentContacts = new Intent().setClass(this, ContactsActivity.class)
				.putExtra(F_MSG, "followers").putExtra(F_USR, prof_name.getText().toString());
		if(F_USR.length() != 0)
		{
		startActivity(intentContacts);
		}
	}



	public void GoContacts2(View v) {
		Intent intentContacts = new Intent().setClass(this, ContactsActivity.class)
				.putExtra(F_MSG, "following").putExtra(F_USR, prof_name.getText().toString());
		if(F_USR.length() != 0)
		{
		startActivity(intentContacts);
		}
	}


    
	public void Go_profilepic(View v)
	{
		Intent intentProfpic = new Intent().setClass(this, ProfilePicActivity.class)
				.putExtra(PPUsrNAME, UsrNAME);
		startActivity(intentProfpic);
	}
	
	
	
	public void ClickName(View v) {

		final int position = list.getPositionForView(v);
		if (position != ListView.INVALID_POSITION) {
			
		model.moveToPosition(position-1);
		String Name = helper.getProfpostName(model);

        if(Name.equals(utilhelper.getUSER()))
        {
        	
        	Gomyprof();
        	
        }else{
        	
	     Go_Profile(Name);
        //ShowTost(Name);
	     
        }

		}	

	}	
	
	

	void Gomyprof()
    {
		
    	Intent MyProfintent = new Intent(this, MyProfileActivity.class);
    	startActivity(MyProfintent);
    
    }


    public void Go_Profile(String usr) {
    	Intent intent = new Intent(this, ProfileActivity.class);
    	intent.putExtra(USR_DETAIL, usr);
    	startActivity(intent);
    	
    	}  
	
    
    
    
    @SuppressLint("NewApi")
	public void Pop_up(View v)
    {
 		final int position = list.getPositionForView(v);
 		if (position != ListView.INVALID_POSITION) {
 			
		model.moveToPosition(position-1);
		postID = helper.getProfpostPID(model);

 		
         if(helper.getProfpostUID(model).equals(UserID))
         {
     		PopupMenu popupMenu = new PopupMenu(ProfileActivity.this,v );				
     		popupMenu.setOnMenuItemClickListener(ProfileActivity.this);				
     		popupMenu.inflate(R.menu.row_mypopup_menu);				
     		popupMenu.show();
         }
         else
         {
     		PopupMenu popupMenu = new PopupMenu(ProfileActivity.this,v );				
     		popupMenu.setOnMenuItemClickListener(ProfileActivity.this);				
     		popupMenu.inflate(R.menu.row_popup_menu);				
     		popupMenu.show();	
         }

     	
 		}
 		
 		
    }
 	
    

 public boolean onMenuItemClick(MenuItem item) {
 switch (item.getItemId()) {

 case R.id.row_popup_delete:
 {
 Toast.makeText(this, "Delete = "+postID, Toast.LENGTH_SHORT).show();
 }
 return true;

 case R.id.row_popup_report:
 {
 Toast.makeText(this, "Report = "+postID, Toast.LENGTH_SHORT).show();
 }
 return true;

 case R.id.row_popup_cancel:
 {

 }
 return true;

 default:		
 return false;


 }
 }
     

 	
    
    
    
	
	

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





	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}*/



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

			set_pBar(true);
		}


		protected void onPostExecute(Double result){

			if(null!=iresponse)
    		{
			if(iresponse.trim().length()!=0)
			{


				try{

					JSONObject jsonObj = new JSONObject(iresponse.trim());               	
					
					
					if(jsonObj.isNull("detail")==false)
					{
						
						
						JSONArray data1 = jsonObj.getJSONArray("extra");
						JSONObject b = data1.getJSONObject(0);
					    isTogether = b.getString("isTogether");
						
						JSONArray data2 = jsonObj.getJSONArray("detail");
						JSONObject c = data2.getJSONObject(0);
					    
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
                    	String status = c.getString("status");
                    	String numfollowers = c.getString("numfollowers");
                    	String numfollowing = c.getString("numfollowing");
                  	
                        
          				prof_name.setText(UsrNAME);
        				prof_mail.setText(email);
        				
        				prof_phn.setText(phone);
        				prof_occup_workplace.setText(occupation+" at "+workplace);
        				
        				
        				if(status.equals("offline"))
        				{        					
        					prof_status.setTextColor(Color.parseColor("#b0b0b0"));
        				}
        				else
        				{
        					prof_status.setTextColor(Color.parseColor("#68dc2c"));
        				}
        				prof_status.setText(status);
        							
        				
    		            utilhelper.updateFACE(user, gender, phone, email, location, workplace, occupation, bio, reputation, isVerified, award, numfollowers, numfollowing);

        				
        				if (imageLoader == null)
        					imageLoader = AppController.getInstance().getImageLoader();

        				prof_image.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+uid+".png", imageLoader);


        				
        				followers.setText(numfollowers);
        				followers.append(" followers");
        				following.setText(numfollowing);
        				following.append(" following");
        				
        				if(isTogether.equals("true"))
        				{
        					prof_msg.setVisibility(View.VISIBLE);
        			 	}
        				
        				
        				prof_followbtn.setVisibility(View.VISIBLE);
        				
        				if(utilhelper.getFACEUID(user)==null)
        				{
        				    prof_followbtn.setText("Follow");
        				    prof_followbtn.setTag("N");
        				}
        				else
        				{
        					prof_followbtn.setText("Unfollow");
        					prof_followbtn.setTag("Y");
        				}



					}
					else
					{
						

                		if(jsonObj.isNull("data")==false)
						{

                		JSONArray data = jsonObj.getJSONArray("data");
                    	
                    	//if(data.length()!=0)
            			//{
            			
                    		JSONObject d = data.getJSONObject(0);
                    		String msg = d.getString("msg");
                    		
                           	if("isFollowed".compareTo(msg)==0)
                        	{
                           		JSONArray Fdata = jsonObj.getJSONArray("face");	
                           			                           		
                           	  // for(int i=0;i<Fdata.length();i++)
                              // {
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
                               
                                                   
                                    
    	                			Toast.makeText(getApplicationContext(),"you are now following "+user , Toast.LENGTH_LONG).show();
                        		
                        	}
                        	
                           	else if("isUnFollowed".compareTo(msg)==0)
                        	{
                           		JSONArray Fdata = jsonObj.getJSONArray("face");	
	                           		
	                           	
	                               	JSONObject c = Fdata.getJSONObject(0);
	                            	String user = c.getString("username");
	                               	String uid = c.getString("userid");
                            	
	                           	utilhelper.DltFACE(uid); 
	                			Toast.makeText(getApplicationContext(),user+" has been unfollowed" , Toast.LENGTH_LONG).show();

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

			//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
		    //confirmFireMissiles();
			set_pBar(false);


		}

		protected void onProgressUpdate(Integer... progress){
			//pb.setProgress(progress[0]);
		}

		protected void postData(String Header) {
			// Create a new HttpClient 
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

		    httppost = new HttpPost(LoginActivity.SERVER+"profile_detail.php");

    	}			//String Header = "get_detail";

			try {
				// Add your data 

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    	if("FollowORunfollow".compareTo(Header)==0)
		    	{
					nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
		    		nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
		    		nameValuePairs.add(new BasicNameValuePair("CNAME", UsrNAME ));
		    	}
		    	else
		    	{
				nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
				nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
				nameValuePairs.add(new BasicNameValuePair("UNAME", UsrNAME ));
		    	}

		    	
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();


				//HttpResponse response;


				// Execute HTTP Post Request
				iresponse = httpclient.execute(httppost, responseHandler);




		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
			}


		}

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

			 isLoading = true;
			
			CProgress.setVisibility(View.VISIBLE);
			}


		@SuppressWarnings("deprecation")
		protected void onPostExecute(Double result){

			if(null!=piresponse)
    		{
			if(piresponse.trim().length()!=0)
			{


				try{

					JSONObject jsonObj = new JSONObject(piresponse.trim());               	
					
					
					if(jsonObj.isNull("posts")==false)
					{


						//helper.ClearProfpost();						


						JSONArray data = jsonObj.getJSONArray("posts");
						for(int i=0;i<data.length();i++)
						{

							JSONObject c = data.getJSONObject(i);
							String usrname = c.getString("username");
							String stamp = c.getString("stamp");
							String userid = c.getString("uid");
							String body = c.getString("body");
							String pimg = c.getString("pimg");
							String pid = c.getString("pid");
							String IMG = c.getString("IMG");
							String finalrating  = c.getString("finalrating");
							double rating = c.getDouble("rating");
							int ratecount = c.getInt("ratecount");
							int commentcount = c.getInt("commentcount");

							//double realrating = rating / ratecount;

							if(IMG.compareTo("NO")==0)
							{

								helper.insertProfpost(pid,userid,usrname,body,null,stamp,"NO",
										finalrating,String.valueOf(ratecount),String.valueOf(commentcount));            			
							} 
							else
							{
								byte[] imgdata = Base64.decode(pimg, 0);
								helper.insertProfpost(pid,userid,usrname,body,imgdata,stamp,"YES",
										finalrating,String.valueOf(ratecount),String.valueOf(commentcount));
							}

						}
						
						model.requery();
					}
					
				} catch (JSONException e){

				} 

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


			//Toast.makeText(getApplicationContext(),piresponse.trim() , Toast.LENGTH_LONG).show();
		    //confirmFireMissiles();



		}

		protected void onProgressUpdate(Integer... progress){
			//pb.setProgress(progress[0]);
		}

		protected void postData() {
			// Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
			
			HttpPost httppost = new HttpPost(LoginActivity.SERVER+"profile_detail.php");

			String Header = "profile_posts";

			try {
				// Add your data 

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
				nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
				nameValuePairs.add(new BasicNameValuePair("UNAME", UsrNAME ));
				if(helper.getLastProfpost()!=null)
				{
				nameValuePairs.add(new BasicNameValuePair("PostLoc", helper.getLastProfpost()));
				}
				else
				{
				nameValuePairs.add(new BasicNameValuePair("PostLoc","0"));
				}
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


    
	
	
	
	
	
	void QuerryPROF()
	{

		if(utilhelper.getFACEUID(UsrNAME)!=null)
		{
			
	     	if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();

			prof_image.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+utilhelper.getFACEUID(UsrNAME)+".png", imageLoader);


		prof_name.setText(utilhelper.getFACENAME(utilhelper.getFACEUID(UsrNAME)));
		prof_mail.setText(utilhelper.getFACEEMAIL(utilhelper.getFACEUID(UsrNAME)));
		prof_phn.setText(utilhelper.getFACEPHONE(utilhelper.getFACEUID(UsrNAME)));
		prof_occup_workplace.setText(utilhelper.getFACEOCCUPATION(utilhelper.getFACEUID(UsrNAME)));
		prof_occup_workplace.append(" at "+utilhelper.getFACEWORKPLACE(utilhelper.getFACEUID(UsrNAME)));
		
		location.setText(utilhelper.getFACELOCATION(utilhelper.getFACEUID(UsrNAME)));
		
		followers.setText(utilhelper.getFACENUMFOLLOWER(utilhelper.getFACEUID(UsrNAME)));
		followers.append(" followers");
		following.setText(utilhelper.getFACENUMFOLLOWING(utilhelper.getFACEUID(UsrNAME)));
		following.append(" following");
		}

	}
  
    

    public void OpenCommentDlg(View v)
    {
   	 final int position = list.getPositionForView(v);
     if (position != ListView.INVALID_POSITION) {
    	 
 		model.moveToPosition(position-1);
 		String pid = helper.getProfpostPID(model);
    	 
    	Intent intent = new Intent(this, CommentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(PostID, pid);
    	startActivity(intent);
    	
     }
    }

	
	   
    public void OpenRateDlg(View v)
    {
   	 final int position = list.getPositionForView(v);
     if (position != ListView.INVALID_POSITION) {
			
		model.moveToPosition(position-1);
		String pid = helper.getProfpostPID(model);
		
    	Intent intent = new Intent(this, RatingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(PostID, pid);
    	startActivity(intent);
    	
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
                    		   rload();
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
    
    

    
	public void ShowNoNetErrorDialoag() {
	    DialogFragment newFragment = new NetworkErrorDialogFragment();
	    newFragment.show(getFragmentManager(), "noneterror");
	}
	

   


	class ProfileAdapter extends CursorAdapter {

		@SuppressWarnings("deprecation")
		ProfileAdapter(Cursor c) {
			super(ProfileActivity.this,c);
		}

		@Override
		public void bindView(View row, Context ctxt,Cursor c) {
			ProfileHolder holder=(ProfileHolder)row.getTag();
			holder.populateFrom(c, helper, utilhelper);
/*			if(lvboolaray[c.getPosition()] == false)
			{		    	
				holder.post_body.setVisibility(View.GONE);
			}
			else
			{
				holder.post_body.setVisibility(View.VISIBLE);
			}*/
			if(helper.getProfpostBody(c).length()!=0)
			{
				holder.post_body.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.post_body.setVisibility(View.GONE);
			}
			
			
		    
			
			if(helper.getProfpostType(c).equals("YES"))
			{
				holder.pimage.setVisibility(View.VISIBLE);
				
				if(helper.getProfpost_isExpanded(c).equals("NO"))
				{
					holder.pimage.setAdjustViewBounds(false);
					holder.pimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
				}
				else
				{
					holder.pimage.setAdjustViewBounds(true);
				}
				
			}
			else
			{
				holder.pimage.setVisibility(View.GONE);
			}

			    
			
		}                    
		@Override  
		public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.prof_row, parent, false);
			ProfileHolder holder=new ProfileHolder(row);
			row.setTag(holder);     
			return(row);              
		}

	}    



	static class ProfileHolder {
		private TextView uname=null;
		private TextView post_body=null;
		private TextView date_time=null;
		private NetworkImageView image=null;
		private ImageView pimage=null;
		private RatingBar RateBar;
		Button ratebtn;
		Button sharebtn;
		Button commentbtn;
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		ProfileHolder(View row) {
		
		uname=(TextView)row.findViewById(R.id.prow_username);
		post_body=(TextView)row.findViewById(R.id.prow_body);
		date_time=(TextView)row.findViewById(R.id.prow_date_time);
		image=(NetworkImageView)row.findViewById(R.id.prow_image);
		pimage=(ImageView)row.findViewById(R.id.prow_postimage);
		RateBar=(RatingBar)row.findViewById(R.id.prow_ratingbar);
		ratebtn=(Button)row.findViewById(R.id.profrate_clk);
		sharebtn=(Button)row.findViewById(R.id.profshare_clk);
		commentbtn=(Button)row.findViewById(R.id.profcomment_clk);

		//image.setErrorImageResId(R.drawable.me);
		image.setDefaultImageResId(R.drawable.me);
		}

		void populateFrom(Cursor c, Database_One helper, Util_Database utilhelper) {
			
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();

			image.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getProfpostUID(c).toString()+".png", imageLoader);		
			
			
			date_time.setText(helper.getProfpostDate(c));
			
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
			
			
			uname.setText("@"+helper.getProfpostName(c));
			if(helper.getProfpostName(c).equals(utilhelper.getUSER()))
			{
			Linkify.addLinks(uname, atMentionPattern, "myprofile://", null, transformFilter); 
			}
			else
			{
			Linkify.addLinks(uname, atMentionPattern, atMentionScheme, null, transformFilter); 
			}

			
			stripUnderlines(uname);
	
			
			
			if(helper.getProfpostBody(c)!=null)
			{
				post_body.setText(helper.getProfpostBody(c));
				
				Linkify.addLinks(post_body, Linkify.ALL);
				Linkify.addLinks(post_body, atMentionPattern, atMentionScheme, null, transformFilter); 
				Linkify.addLinks(post_body, HashPattern, HashScheme, null, transformFilter); 

			}
			else
			{
				post_body.setText(null);
				post_body.setVisibility(View.GONE);
				
			}
			
			
					
			if(helper.getProfpostType(c).compareTo("YES")==0)
			{
				pimage.setImageBitmap(BitmapFactory.decodeByteArray(helper.getProfpostBmp(c), 0, helper.getProfpostBmp(c).length));
			}
			else
			{
				pimage.setVisibility(View.GONE);
			}
			
					
		
			
			RateBar.setRating(Float.parseFloat(helper.getProfpostRating(c)));
			
			ratebtn.setText("rate("+helper.getProfpostRateCount(c)+")");
			commentbtn.setText("comment("+helper.getProfpostCommentCount(c)+")");

            
		}
		
		
	}
		
	
	
	
	
	
	public void FollowORunfollow(View v) {
		
								
				if(prof_followbtn.getTag()=="Y")
				{
					prof_followbtn.setText("Follow");
					prof_followbtn.setTag("N");
				}
				else
				{
					prof_followbtn.setText("Unfollow");
					prof_followbtn.setTag("Y");
				}
				
			new MyAsyncTask().execute("FollowORunfollow");
	     	


	}	


	
	
	
	
	
	
		

    public void ShowTost(String txt)
    {
    	Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
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
    


    

///////////////////////////////////////////////////////////////////////////////////////////////////	    

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

