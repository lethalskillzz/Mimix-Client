package com.lethalsys.mimix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hall.emojimap.EmojiMapUtil;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

@SuppressLint({ "HandlerLeak", "NewApi" })
@SuppressWarnings("deprecation")
public class MessageActivity  extends FragmentActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener  {

	public final static String USR_DETAIL = "com.lethalsys.mimix.TARGETDETAIL";

	private Set<Long> seenmsg=new HashSet<Long>();
	AtomicBoolean isnewmsg=new AtomicBoolean(false);	

	//private com.lethalsys.http_req_test.message_array_adapter adapter;
	private ListView lv;
	EmojiconEditText msgedit;
	private ImageButton msgsend;



	private String iresponse;
	private String UserID;
	private String TargetName=null;
	private String Cpymsg;


	public static Handler msgUiHandler = null;

	Cursor model=null;
	PostDatabaseAdapter adapter =null;
	Database_One helper=null;

	JSONArray data = null;

	public static Boolean msgactv = true;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	Util_Database utilhelper;


	List<xenv> model2=new ArrayList<xenv>();
	//postadapter adapter2=null;

	ProgressBar AB_Loading;

	public ImageButton showemoji;
	public ImageButton hideemoji;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);	
				
		Intent intent = getIntent();
		TargetName = intent.getStringExtra(ProfileActivity.T_DETAIL);
		
		NetworkImageView AB_Image;
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();

		
		if(TargetName==null)
		{
			TargetName = intent.getStringExtra(Message_Fragment.MSGUSRNAME);
		}		
		
		utilhelper=new Util_Database(this);
		UserID = utilhelper.getUSER_ID();
		
		AB_Image = (NetworkImageView)findViewById(R.id.msg_action_bar_image);
		AB_Image.setDefaultImageResId(R.drawable.me);
		
		  /*if(utilhelper.getisIMG_MSGLST(TargetName).equals("YES"))
		  {	  
			
			  AB_Image.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeByteArray(utilhelper.getPPIC_MSGLST(TargetName), 0, utilhelper.getPPIC_MSGLST(TargetName).length),150));
			
		  }
		  else
		  {
			  AB_Image.setImageResource(R.drawable.me);
		  }*/
		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		AB_Image.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+utilhelper.getFACEUID(TargetName)+".png", imageLoader);

		
		  TextView AB_Title = (TextView)findViewById(R.id.msg_action_bar_username); 
		  AB_Title.setText(TargetName);

		  AB_Loading = (ProgressBar)findViewById(R.id.msg_action_bar_loading); 

		  showemoji = (ImageButton)findViewById(R.id.emojibtn); 
		  hideemoji = (ImageButton)findViewById(R.id.hideemojibtn); 

		  


		helper=new Database_One(this);


		lv = (ListView) findViewById(R.id.msglistView);
		lv.setDivider(null);

		msgsend=(ImageButton)findViewById(R.id.msgsendbtn);
		//msgsend.setEnabled(false);
		msgsend.setVisibility(View.GONE);

		//helper.ClearMsg();
		//Toast.makeText(getApplicationContext(),helper.getLastMsg(UserID, utilhelper.getFACEUID(TargetName)), Toast.LENGTH_LONG).show();
		
		
		mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		{
			new MyAsyncTask().execute("get_msg");	    	
		}
		else
		{
			//Toast.makeText(this, "Network Error: No Internet Connection", Toast.LENGTH_LONG).show();
			ShowNoNetErrorDialoag();
		}	



		model=helper.getAllMsg(UserID, utilhelper.getFACEUID(TargetName));
		startManagingCursor(model);
		adapter=new PostDatabaseAdapter(model);
		lv.setAdapter(adapter);

		//adapter2 = new postadapter();

		//adapter = new message_array_adapter(getApplicationContext(), R.layout.message_bubble);
		msgedit = (EmojiconEditText) findViewById(R.id.msgeditText);

		msgedit.addTextChangedListener(watch);
		
		msgedit.setUseSystemDefault(false);
		setEmojiconFragment(false);

		/*msgedit.setOnKeyListener(new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) { 
			// If the event is a key-down event on the "enter" button
			if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
				// Perform action on key press
				//adapter.add(new OneMsg(false, msgedit.getText().toString()));
				Cpymsg = msgedit.getText().toString();
				msgedit.setText("");

				Send_IMessage(v);


				return true;
			}
			return false;
		}
	});*/



		msgUiHandler = new Handler() // Receive messages from service class
		{
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				case 0:
				{
					// model.requery();
					//Toast.makeText(getApplicationContext(), "msg rcvd", Toast.LENGTH_LONG).show();
				}
				break;

				case 1:
				{
					Get_IMessage(); 
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
		msgactv =  true;
	    AB_Loading.setVisibility(View.VISIBLE);

	}


	@Override
	public void onPause() {
		super.onPause();
		msgactv =  false;
	}
	
	
	
	
	
    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(msgedit, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(msgedit);
    }
	
	
	public void Show_Emoji(View v)
	{
		FrameLayout emojicons = (FrameLayout) findViewById(R.id.emojicons);
		emojicons.setVisibility(View.VISIBLE);
		showemoji.setVisibility(View.GONE);
		hideemoji.setVisibility(View.VISIBLE);
    }


	
	public void Hide_Emoji(View v)
	{
		FrameLayout emojicons = (FrameLayout) findViewById(R.id.emojicons);
		emojicons.setVisibility(View.GONE);
		showemoji.setVisibility(View.VISIBLE);
		hideemoji.setVisibility(View.GONE);
    }


	public void Get_IMessage()
	{
		//Cpymsg = msgedit.getText().toString();
		//msgedit.setText("");

		mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		{
			new MyAsyncTask().execute("get_msg");     	
		}
		else
		{
			//Toast.makeText(this, "Network Error: No Internet Connection", Toast.LENGTH_LONG).show();
			ShowNoNetErrorDialoag();
		}	
	}


	public void Send_IMessage(View v)
	{
		//Toast.makeText(this, msgedit.getText().toString(), Toast.LENGTH_LONG).show();
		
		//ShowTost(EmojiMapUtil.replaceUnicodeEmojis(msgedit.getText().toString()));
		
		Hide_Emoji(null);
		if(msgedit.getText().toString().length()<1){
			//Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show();
			
		}
		else
		{
			Cpymsg = EmojiMapUtil.replaceUnicodeEmojis(msgedit.getText().toString());

			msgedit.setText("");

			/*lv.setAdapter(adapter2);

			xenv r=new xenv();

			r.setmsg(Cpymsg);

			adapter2.add(r);

		    lv.setAdapter(adapter);*/

			/**xenv r=new xenv();
			Cursor xodel = helper.getAllMsgX();
			while(xodel.moveToNext())
			{
				r.setWho(xodel.getString(1));
				r.setmsg(xodel.getString(2));
				r.setdate(xodel.getString(3));
			}

			helper.ClearMsg();
			helper.insertMsg(Cpymsg,"just now","me");	
			for(int i=0;i<r.getWho().size();i++)
			{	 
				helper.insertMsg(r.getmsg().get(i),r.getdate().get(i),r.getWho().get(i));					 
			}

			model.requery();*/



			mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

			if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
			{

				new MyAsyncTask().execute("add_msg");    	
			}
			else
			{
				//Toast.makeText(this, "Network Error: No Internet Connection", Toast.LENGTH_LONG).show();
				ShowNoNetErrorDialoag();
			}
		}
	}


	
	public void GoProf(View v)
    {
		
    	Intent Profintent = new Intent(this, ProfileActivity.class);
    	Profintent.putExtra(USR_DETAIL, TargetName);
    	startActivity(Profintent);	
    	
    }
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}*/
	
	
	
	
  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
    
            case android.R.id.home:
            	GoProf(); 
            	return true;
            	
    
                
            default:
                return super.onOptionsItemSelected(item);
        }
    } */

	
	



	TextWatcher watch = new TextWatcher(){
		@Override
		public void afterTextChanged(Editable arg0) {

		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}
		@Override
		public void onTextChanged(CharSequence s, int a, int b, int c) {
			if(s.length() > 0 && s.toString().trim().length() >= 0 )
			{
				msgsend.setVisibility(View.VISIBLE);
			}
			else
			{
				msgsend.setVisibility(View.GONE);
			}

		}};



		private class MyAsyncTask extends AsyncTask<String, Integer, Double>{

			@Override
			protected Double doInBackground(String... params) {
				postData(params[0]);
				return null;
			}


			@Override
			protected void onPreExecute() {
				super.onPreExecute();


				/* pDialog = new ProgressDialog(HomeActivity.this);
    			pDialog.setMessage("loding posts...");
    			pDialog.setIndeterminate(false);
    			pDialog.setCancelable(true);
    			pDialog.show();*/
			}


			protected void onPostExecute(Double result){
				if(null!=iresponse)
				{

					if(iresponse.trim().length()!=0)
					{
						/*if("Post added".compareTo(iresponse.trim())==0)
						{
							Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
						}

						else if("Error: Unable to add post".compareTo(iresponse.trim())==0)
						{
							Toast.makeText(getApplicationContext(),iresponse.trim(), Toast.LENGTH_LONG).show();
						}
						else*/

							try{
								JSONObject jsonObj = new JSONObject(iresponse.trim());

								if(jsonObj.isNull("extra")==false)
								{
									JSONArray edata=jsonObj.getJSONArray("extra");
									JSONObject d = edata.getJSONObject(0);
									String isTogether = d.getString("isTogether");

									if(isTogether.equals("true"))
									{

										data = jsonObj.getJSONArray("msgs");
										//helper.ClearMsg();

										for(int i=0;i<data.length();i++)
										{
											JSONObject c = data.getJSONObject(i);
											String stamp = c.getString("stamp");
											String userid = c.getString("userid");
											String targetid = c.getString("targetid");
											String msgbody = EmojiMapUtil.replaceCheatSheetEmojis(c.getString("msgbody"));
											int mid = c.getInt("mid");
											String status = c.getString("status");


											if (!seenmsg.contains(mid)) {            		
												seenmsg.add((long)mid);

												if(isnewmsg.get() == false)
												{
													isnewmsg.set(true);
												}
											}

											if(UserID.compareTo(userid)==0)
											{
												helper.insertMsg(mid,userid,targetid,msgbody,stamp,status,"me");
											}
											else
											{
												helper.insertMsg(mid,userid,targetid,msgbody,stamp,status,"friend");
											}

										}

										if(isnewmsg.get() != false)
										{
											model.requery();
										}

									}
								}

							} catch (JSONException e){

							}
						//seenmsg.clear();
						isnewmsg.set(false);
						//showNotification();
						//pDialog.dismiss();
						//confirmFireMissiles();
					}

				}

				 AB_Loading.setVisibility(View.GONE);
				 //Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
				 //confirmFireMissiles();
				
				 //ShowTost(helper.getLastMsg(UserID, utilhelper.getFACEUID(TargetName)));



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

				HttpPost httppost = new HttpPost(LoginActivity.SERVER+"imessanger.php");


				try { 
					// Add your data

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("PacketHead",Header));
					nameValuePairs.add(new BasicNameValuePair("UID", UserID));
					nameValuePairs.add(new BasicNameValuePair("Target_Name",  TargetName));
					if(Header.compareTo("add_msg")==0)
					{
						nameValuePairs.add(new BasicNameValuePair("msg_body",Cpymsg));
						Cpymsg = null;
					}
					//new PostDatabase(MessageActivity.this).getLastMsg(UserID,TargetName);
				    if(helper.getLastMsg(UserID,TargetName)!=null)
					{
					    nameValuePairs.add(new BasicNameValuePair("position",helper.getLastMsg(UserID, utilhelper.getFACEUID(TargetName))));	
						// nameValuePairs.add(new BasicNameValuePair("position","27"));	
					}
					else
					{
						   nameValuePairs.add(new BasicNameValuePair("position","0"));	
					}
						   
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
			
					//HttpResponse response; 

					// Execute HTTP Post Request
					iresponse = httpclient.execute(httppost, responseHandler);

				} catch (ClientProtocolException e) {

				} catch (IOException e) {

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
						// FIRE ZE MISSILES!
					}
				})
				.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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





		class PostDatabaseAdapter extends CursorAdapter {


			PostDatabaseAdapter(Cursor c) {
				super(MessageActivity.this,c);
			}
			@Override
			public void bindView(View row, Context ctxt,Cursor c) {
				PostDatabaseHolder holder=(PostDatabaseHolder)row.getTag();
				holder.populateFrom(c, helper);

				/*if(msglvboolaray2[c.getPosition()] == true)
				{
					holder.sent.setVisibility(View.GONE);
					holder.sending.setVisibility(View.GONE);
				}
				else
				{

					if(msglvboolaray[c.getPosition()] == true)
					{		    	
						holder.sent.setVisibility(View.GONE);
						holder.sending.setVisibility(View.VISIBLE);

					}
					else
					{		    	
						holder.sent.setVisibility(View.VISIBLE);
						holder.sending.setVisibility(View.GONE);		    
					}

				}*/
				
				if("friend".compareTo(helper.getWho(c))==0) 
				{
					holder.sent.setVisibility(View.GONE);
					holder.sending.setVisibility(View.GONE);
				}
				else
				{ 
					if("just now".compareTo(helper.getMsgDate(c))==0) 
					{
						holder.sent.setVisibility(View.GONE);
						holder.sending.setVisibility(View.VISIBLE);
					}
					else
					{					
						holder.sent.setVisibility(View.VISIBLE);
						holder.sending.setVisibility(View.GONE);		    
					}
				}


			}
			@Override
			public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
				LayoutInflater inflater=getLayoutInflater();
				View row=inflater.inflate(R.layout.message_bubble, parent, false);
				PostDatabaseHolder holder=new PostDatabaseHolder(row);
				row.setTag(holder);
				return(row);
			}

		}    





		static class PostDatabaseHolder {
			private EmojiconTextView msg_body=null;
			private ImageView sent=null;
			private ProgressBar sending=null;
			private TextView date_time=null;
			//private View row=null;
			private LinearLayout mainwrap;
			private LinearLayout wrapper;
			PostDatabaseHolder(View row) {
				//this.row=row;

				sent = (ImageView)row.findViewById(R.id.txt_sent);
				msg_body=(EmojiconTextView)row.findViewById(R.id.txt_msg);
				msg_body.setUseSystemDefault(false);
				sending =(ProgressBar)row.findViewById(R.id.txt_loading);
				date_time=(TextView)row.findViewById(R.id.txt_date);
				mainwrap =  (LinearLayout) row.findViewById(R.id.mainwrap);
				wrapper =  (LinearLayout) row.findViewById(R.id.wrapper);
			}



			void populateFrom(Cursor c, Database_One helper) {
				msg_body.setText(helper.getMsgBody(c));
				
				date_time.setText(helper.getMsgDate(c));
				
				if("friend".compareTo(helper.getWho(c))==0) 
				{
					//msg_body.setBackgroundResource(R.drawable.bubble_yellow);
					mainwrap.setGravity(Gravity.LEFT);
					wrapper.setBackgroundResource(R.drawable.bubble_yellow);
				}
				else
				{
					//msg_body.setBackgroundResource(R.drawable.bubble_green);
					mainwrap.setGravity(Gravity.RIGHT);
					wrapper.setBackgroundResource(R.drawable.bubble_green);

 
					if("just now".compareTo(helper.getMsgDate(c))==0) 
					{
					}
					else
					{					
					}
				}


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




		public class xenv{

			/* String Who ="";
		    String msg ="";

		    public String getmsg() {
			return(msg);
			}
			public void setmsg(String msg) {
			this.msg=msg;
			}


		    public String getWho() {
			return(Who);
			}
			public void setWho(String Who) {
			this.Who=Who;
			}*/

			List<String> Who = new ArrayList<String>();
			List<String> msg = new ArrayList<String>();
			List<String> date = new ArrayList<String>();


			public List<String> getWho()
			{
				return(Who);
			}		
			public void setWho(String iWho)
			{
				Who.add(iWho);
			}

			public List<String> getmsg()
			{
				return(msg);
			}			
			public void setmsg(String imsg)
			{
				msg.add(imsg);
			}

			public List<String> getdate()
			{
				return(date);
			}			
			public void setdate(String idate)
			{
				date.add(idate);
			}

		}



		/*class postadapter extends ArrayAdapter<xenv> {
		postadapter() {
			super(MessageActivity.this, R.layout.message_bubble, model2);
		}
		public View getView(int position, View convertView,
				ViewGroup parent) {
			View row=convertView;
			PostHolder holder=null;
			if (row==null) {
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.message_bubble, parent, false);
				holder=new PostHolder(row);
				row.setTag(holder);
			}
			else {
				holder=(PostHolder)row.getTag();
			}
			holder.populateFrom(model2.get(position));
			return(row);
		}
	}

	static class PostHolder {
		private TextView msg_body=null;
		//private TextView date_time=null;
		//private View row=null;
		private LinearLayout wrapper;

		PostHolder(View row) {
			//this.row=row;
			msg_body=(TextView)row.findViewById(R.id.txt_msg);
			//date_time=(TextView)row.findViewById(R.id.row_date_time);
			wrapper =  (LinearLayout) row.findViewById(R.id.wrapper);
		}

		void populateFrom(xenv r) {

			    msg_body.setText(r.getmsg());

				msg_body.setBackgroundResource(R.drawable.bubble_green);
				wrapper.setGravity(Gravity.RIGHT);

		}
	}*/



		public void CloseContact() {

			Toast.makeText(getApplicationContext(),"closed" , Toast.LENGTH_LONG).show();
		}






		public void ShowTost(String txt)
		{
			Toast.makeText(getApplicationContext(),txt , Toast.LENGTH_LONG).show();
			
		}




}



