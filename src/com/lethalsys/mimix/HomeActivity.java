package com.lethalsys.mimix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.lethalsys.mimix.SimpleGestureFilter.SimpleGestureListener;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.PageIndicator;




@SuppressLint({ "HandlerLeak", "NewApi" })
public class HomeActivity extends FragmentActivity  implements SimpleGestureListener {

	
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ImageButton mSearchView;
	
	//public final static String USR_DETAIL = "com.lethalsys.mimix.U_DETAIL";
	//public final static String GETALL_MESSAGE = "com.lethalsys.mimix.GETALL_MSG";

	private static String BG_NAME = null;

	private SimpleGestureFilter detector;

	private String iresponse;
	private String UserID;
	private String oresponse;
	
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	NetworkImageView mainmenu_pimg;
	TextView mainmenu_pname;

	private ProgressDialog pDialog;

	private SlideHolder mSlideHolder;
	
	Database_One helper=null;

	Database_Two Rhelper=null;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	JSONArray data = null;

	String im_tabslct;

	Boolean ErrorDlg = true;
	Util_Database utilhelper=null;

	public static Boolean homeactv = true;
    View adhoc_actionbar;
	PageIndicator mIndicator;

	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		adhoc_actionbar =  (View) findViewById(R.id.adhoc_actionbar);
	
        mSearchView = (ImageButton) findViewById(R.id.mainmenu_search);

        
        detector = new SimpleGestureFilter(this,this);

        viewPager = (ViewPager) findViewById(R.id.home_pager);       
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
       
      
        mIndicator = (IconPageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        

       


		helper=new Database_One(this);
		Rhelper=new Database_Two(this);
		utilhelper=new Util_Database(this);
		UserID = utilhelper.getUSER_ID();

		
	try {
		copyBackGrounds();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	


		mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);


		View toggleView = findViewById(R.id.main_option);
		
		mainmenu_pimg = (NetworkImageView) findViewById(R.id.mainmenu_profimg);
		mainmenu_pimg.setDefaultImageResId(R.drawable.me);

		mainmenu_pname = (TextView) findViewById(R.id.mainmenu_profname);
		toggleView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			
				
				if(mSlideHolder.isOpened()) {
					mSlideHolder.close();
				} else {
					SetMenuDetail();
					mSlideHolder.open();
				}			
				
				
			}
		});
		
		

		/*stopService(new Intent(this, IM_Monitor.class));
	       startService(new Intent(this, IM_Monitor.class));*/  


	}
	
	



	public void SetMenuDetail()
	{
		//if(utilhelper.getPROF_isIMG()!=null)
		//{
		//	if(utilhelper.getPROF_isIMG().equals("YES"))
		//	{
		///		Bitmap bmp = BitmapFactory.decodeByteArray(utilhelper.getPROF_IMG(), 0, utilhelper.getPROF_IMG().length);			
		//		mainmenu_pimg.setImageBitmap(MLRoundedImageView.getCroppedBitmap(bmp, 150));
		//prof_image.setImageBitmap(BitmapFactory.decodeByteArray(utilhelper.getPROF_IMG(), 0, utilhelper.getPROF_IMG().length));
		//	}
		//}

		mainmenu_pname.setText(utilhelper.getFACENAME(UserID));
		/*if(utilhelper.getFACEisIMG(UserID).equals("YES"))
		{
			Bitmap bmp = BitmapFactory.decodeByteArray(utilhelper.getFACEPPIC(UserID), 0, utilhelper.getFACEPPIC(UserID).length);
			mainmenu_pimg.setImageBitmap(RoundedImageView.getCroppedBitmap(bmp, 150));
			//prof_image.setImageBitmap(BitmapFactory.decodeByteArray(utilhelper.getPROF_IMG(), 0, utilhelper.getPROF_IMG().length));
		}
		else
		{
			//mainmenu_pimg.setImageResource(R.drawable.me);
			Bitmap bmp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.me);
			mainmenu_pimg.setImageBitmap(RoundedImageView.getCroppedBitmap(bmp, 150));

		}*/
		
		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		mainmenu_pimg.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+UserID+".png", imageLoader);


	}



	
	public void show_search(View v)
	{

        if (findViewById(R.id.home_view) != null) {

            Search_Fragment search_fragment = new Search_Fragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.home_view, search_fragment).commit();
        }
		
	}

	


	public void GoMyProfile(View v)
	{

		Intent MyProfintent = new Intent(this, MyProfileActivity.class);
		startActivity(MyProfintent);

	}



	@Override
	public void onResume() {
		super.onResume();
		homeactv = true;
		ErrorDlg = true;


		im_tabslct = Notify_Handler.notif_type; 

		if(im_tabslct!=null)
		{
			if(im_tabslct.compareTo("im")==0)
			{
				mIndicator.setCurrentItem(3);
			}
			/*else if(im_tabslct.compareTo("nf")==0)
			{
				host.setCurrentTab(1);
			}*/
			else
			{
				mIndicator.setCurrentItem(1);
			}

		}

		Notify_Handler.notif_type = null;
		MessageActivity.msgactv = false;

	}

	@Override
	public void onPause() {
		super.onPause();
		homeactv = false;
		ErrorDlg = false;
	}


	/*@Override
	public void onContentChanged(){
    super.onContentChanged();

    LayoutInflater LInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    LinearLayout Lview = (LinearLayout) LInflater.inflate(
			R.layout.lv_set_empty, null, false);


    //View empty = (LinearLayout)mFooterView.findViewById(R.id.set_empty_txt);    
	//list.setEmptyView(Lview);


	}*/



	
	



	public void Go_LogAwt(View v) {

		log_out();

	} 
	
	
	public void Go_EditProfile(View v) {

		Intent intent = new Intent(this, EditProfileActivity.class);
		startActivity(intent);

	} 




		



		
		

		public void Click_Writepost(View v) 
		{
			Intent Postintent = new Intent(this, PostActivity.class);//.putExtra(EXTRA_MESSAGE2, UserID);
			startActivity(Postintent);
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


		public void confirmFireMissiles() {
			DialogFragment newFragment = new FireMissilesDialogFragment();
			newFragment.show(getFragmentManager(), "missiles");
		}



		public void log_out()
		{
			mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

			if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
			{



				new AsyncTask<String, Integer, Double>() {
					@Override
					protected void onPreExecute() {
						super.onPreExecute();

						pDialog = new ProgressDialog(HomeActivity.this);
						pDialog.setMessage("Logging out...");
						pDialog.setIndeterminate(false);
						pDialog.setCancelable(false);
						pDialog.show();
					}

					@Override
					protected Double doInBackground(String... params) {

						// Create a new HttpClient and Post Header
						HttpParams httpparameters = new BasicHttpParams();
						HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
						HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);

						DefaultHttpClient httpclient = new DefaultHttpClient();
						httpclient.setParams(httpparameters);

						HttpPost httppost;

						httppost = new HttpPost(LoginActivity.SERVER+"logout.php");

						try {

							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("PacketHead","logout" ));
							nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							ResponseHandler<String> responseHandler = new BasicResponseHandler();


							// Execute HTTP Post Request
							oresponse = httpclient.execute(httppost, responseHandler);





						} catch (IOException e) {
							pDialog.dismiss();
							if(ErrorDlg == true)
								ShowLogOutErrorDialoag();		
						}


						return null;
					}

					protected void onProgressUpdate(Integer... progress){

					}


					protected void onPostExecute(Double result){

						if(null!=oresponse)
						{

							if(oresponse.trim().length()!=0)
							{

								try{


									JSONObject jsonObj = new JSONObject(oresponse.trim());               	


									if(jsonObj.isNull("data")==false)
									{

										data = jsonObj.getJSONArray("data");

										JSONObject d = data.getJSONObject(0);
										String msg = d.getString("msg");


										if("logged out".compareTo(msg)==0)
										{

											pDialog.dismiss();

											utilhelper.ClearUSER();
											helper.Clear();
											helper.ClearMsg();
											Rhelper.Clear();
											Rhelper.ClearALL();
											Rhelper.ClearMsgList();
											Rhelper.ClearNotifications();
											utilhelper.Clear();
											Rhelper.ClearMAIN();
											Rhelper.ClearHash();
											Rhelper.ClearTrendingHash();
											utilhelper.ClearMsgListPostLoc();
											helper.ClearProfpost();
											helper.ClearAddon();

											finalize_logout();

										}

										else
										{
											pDialog.dismiss();
											Toast.makeText(getApplicationContext(),"Logout failed, try again later" , Toast.LENGTH_LONG).show();
											//Toast.makeText(getApplicationContext(),oresponse , Toast.LENGTH_LONG).show();							
										}

									}
								} catch (JSONException e){

								} 



							}
						}		


						pDialog.dismiss();


					}

				}.execute();

			}
			else
			{
				pDialog.dismiss();
				if(ErrorDlg == true)
					ShowLogOutErrorDialoag();
			}	
		}


		public void finalize_logout()
		{
			stopService(new Intent(this, IM_Monitor.class));
			stopService(new Intent(this, Notify_Handler.class));
			Intent awtintent = new Intent(this, LaunchActivity.class);
			awtintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(awtintent);

			/*Intent intent = getIntent();
    		overridePendingTransition(0, 0);
    		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);*/
			finish();
		}





		@SuppressLint("ValidFragment")
		public class LogOutErrorDialogFragment extends DialogFragment {
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// Use the Builder class for convenient dialog construction
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//builder.setMessage(LoginActivity.neterrormsg)
				builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
				.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						log_out();
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


		public void ShowLogOutErrorDialoag() {
			DialogFragment newFragment = new LogOutErrorDialogFragment();
			newFragment.show(getFragmentManager(), "logouterror");
		}



		public void ShowTost(String txt)
		{
			Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
		}




		@Override
		public boolean dispatchTouchEvent(MotionEvent me){
			// Call onTouchEvent of SimpleGestureFilter class
			this.detector.onTouchEvent(me);
			return super.dispatchTouchEvent(me);
		}
		@Override
		public void onSwipe(int direction) {

			if(mSlideHolder.isOpened()) {
				mSlideHolder.close();
			} 


			switch (direction) {

			case SimpleGestureFilter.SWIPE_RIGHT : 
			{
				
			}
			break;

			case SimpleGestureFilter.SWIPE_LEFT : 
			{
				
			}
			break;

			case SimpleGestureFilter.SWIPE_DOWN :  
				adhoc_actionbar.setVisibility(View.VISIBLE);
				break;

			case SimpleGestureFilter.SWIPE_UP :    
				adhoc_actionbar.setVisibility(View.GONE);
				break;

			}

		}

		@Override
		public void onDoubleTap() {
			/*mSlideHolder.toggle();
			SetMenuDetail();
			*/
		}



 
	    
	     
		private void copyBackGrounds() throws IOException{
			
			final File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Mimix"+File.separator+"Backgrounds"+File.separator);
			root.mkdirs();


			for(int i=1;i<2;i++)
			{ 

			BG_NAME	= "bg_"+i+".jpg";
				
			InputStream myInput = this.getAssets().open(BG_NAME);

			// Path to the just created empty db
			String outFileName = root.toString() +"/"+ BG_NAME;

			//Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			//transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}

			//Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
			}

		}

}
