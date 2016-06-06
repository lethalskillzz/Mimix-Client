package com.lethalsys.mimix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hall.emojimap.EmojiMapUtil;
import com.rockerhieu.emojicon.EmojiconTextView;

public class Message_Fragment extends Fragment {
	 
	private String UserID;
	
	Util_Database utilhelper;
	
	public final static String MSGUSRNAME = "com.lethalsys.mimix.MESSAGE_USERNAME";
	public final static String USR_DETAIL = "com.lethalsys.mimix.U_DETAIL";

	
	private Set<Long> seenmsg=new HashSet<Long>();
	//private Set<Long> seenmymsg=new HashSet<Long>();
	
	String iresponse;

	String username;
	String msgbody;
	String mid;

	String cpycontact=null;
	String READMID=null;
	
	JSONArray data = null;
	
	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	ListView list;
	
	Cursor model=null;
	Database_Two helper=null;
	ContactDatabaseAdapter adapter =null;
	
	public static Handler msglUiHandler = null;
	
	Boolean ErrorDlg = true;
	public static Boolean msglstactv = true;
	
	
	public ProgressBar CProgress;
	
	LinearLayout emptyview;
	 	
	
	   
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
	        
	        
		    utilhelper = new Util_Database(getActivity());
		    UserID = utilhelper.getUSER_ID();
		    
			
			helper=new Database_Two(getActivity());
			
			
			/*CProgress =(ProgressBar)findViewById(R.id.lvmsg_loading);
			CProgress.setVisibility(View.GONE);
			
			CProgresstxt =(TextView)findViewById(R.id.lvmsg_loading_txt);
			CProgresstxt.setVisibility(View.GONE);*/
			
			
			list=(ListView)rootView.findViewById(R.id.lvmsg_list);
			
			  LayoutInflater LInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			   emptyview = (LinearLayout) LInflater.inflate(
						R.layout.lv_set_empty, null, false);
				
			

		       LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
						R.layout.loader_footer, null, false);
				
				CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);
				 				        
				list.addFooterView(mFooterView);
				
			
			//helper.ClearMsgList();
			
			model=helper.getMsgList();
			getActivity().startManagingCursor(model);
			adapter=new ContactDatabaseAdapter(model);
			list.setAdapter(adapter);
			list.setOnItemClickListener(onListClick);
			
			//Toast.makeText(getActivity(),"MERROORR" , Toast.LENGTH_LONG).show();							

			
	        msglUiHandler = new Handler() // Receive messages from service class
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
	                    	  Get_IMList(); 
	                      }
	                      break;
	 
	                    default:
	                    break;
	                }
	            }
	        };
		    
			clearNotification();

	         
	        return rootView;
	    }
		
		
		
		
		public void onResume()
		{
			super.onResume();
			ErrorDlg = true;
			msglstactv =  true;
		
			Get_IMList();
			clearNotification();
			
		}
		
		
		@Override
		public void onPause() {
		super.onPause();
		ErrorDlg = false;
		msglstactv =  false;
		}
		
		
		
		public void Get_IMList()
		{
		    mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		    {
	            //helper.ClearMsgList();
		    	new MyAsyncTask().execute("get_msglist"); 	
	        }
	        else
	        {
	        	//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
	        	ShowNoNetErrorDialoag();
	        }	
		}
		
		
		private AdapterView.OnItemClickListener onListClick=new
				AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
				View view, int position,
				long id) {
				model.moveToPosition(position);
				cpycontact = helper.getMsgListUsername(model);
				READMID = helper.getMsgListMID(model);
				//Toast.makeText(getApplicationContext(), READMID, Toast.LENGTH_LONG).show();
				//MSG_READ();
				helper.updatemsglist(READMID);
				//ClosMe();
				Go_Msg(cpycontact);
				
				
			    
				
				}
				};
				
				
				public void MSG_READ()
				{
					mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

				    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
				    {
			        
				    	new MyAsyncTask().execute("msg_read"); 	
			        }
			        else
			        {
			        	//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
			        	ShowNoNetErrorDialoag();
			        }	
						
				}
				
				
				
			    public void Go_Msg(String usr) {
			    	Intent intent = new Intent(getActivity(), MessageActivity.class);
			    	//intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			    	intent.putExtra(MSGUSRNAME, usr);
			    	startActivity(intent);
			    	}  
				
		
		
		private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
			
			
			@Override
			protected void onPreExecute() {
		    super.onPreExecute();
		    
		    list.removeFooterView(emptyview);
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
		        	
		        	
		        	
		        	if(jsonObj.isNull("msgs")==false)
		        	{
		        	
		        	data = jsonObj.getJSONArray("msgs");
		        	 if(data.length()>0)
		             {	  
		        		 
							int Incr = utilhelper.getMsgListPostLoc()+data.length();
							utilhelper.ClearMsgListPostLoc();
							utilhelper.insertMsgListPostLoc(Incr);
			 
		        		 
		        	 helper.ClearMsgList();
		        	 seenmsg.clear();
		        	//seenmymsg.clear();
	    			
		        	
		            for(int i=0;i<data.length();i++)
		            {
		            	JSONObject c = data.getJSONObject(i);
		            	
		            	String stamp = c.getString("stamp");
		            	String userid = c.getString("userid");
		            	String targetid = c.getString("targetid");
		            	String status = c.getString("status");
		            	username =  c.getString("username");
		            	msgbody = EmojiMapUtil.replaceCheatSheetEmojis(c.getString("msgbody"));
		                mid = c.getString("mid");
		                
		                if(userid.compareTo(UserID)==0)
		                {
		                	if (!seenmsg.contains(Long.parseLong(targetid))) {            		
		                	seenmsg.add(Long.parseLong(targetid));
		                	helper.insertMsgList(userid,username,msgbody,"read",mid,stamp);
		                	}
		                }
		                
		                else
		                {
		            	
		            	if (!seenmsg.contains(Long.parseLong(userid))/*||!seenmsg.contains(Long.parseLong(targetid))*/) {
		            		// found a new one!
		            		seenmsg.add(Long.parseLong(userid));
		                
		            	    helper.insertMsgList(userid,username,msgbody,status,mid,stamp);
		            	} 
		                }
		            	
		                /*if("unread".compareTo(status)==0)
		                {
		                	
		                }*/
		               //new MyAsyncTask().execute("msg_seen");
		         
		            }
		            model.requery();
		            	
	    			} 
		        	 
		        	}
		        } catch (JSONException e){
		        	
		        }

			
			
			
		        }
			
	    		}
				//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
				//confirmFireMissiles();

	    		CProgress.setVisibility(View.GONE);
	    		
	    		if(adapter.isEmpty())
	    		{
	    			//list.removeFooterView(emptyview);
	    		    list.addFooterView(emptyview);
	    		}
	    		else
	    		{
	    			list.removeFooterView(emptyview);
	    		}
	    		

			}
			
			
			protected void onProgressUpdate(Integer... progress)
			{
				
			}
			 
			protected void postData(String Header) {
			// Create a new HttpClient and Post Header
				HttpParams httpparameters = new BasicHttpParams();
			    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
			    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
			    
				DefaultHttpClient httpclient = new DefaultHttpClient();
				httpclient.setParams(httpparameters);
				
				HttpPost httppost;

			   httppost = new HttpPost(/*"http://10.0.2.2/Twitter-Engine/*/LoginActivity.SERVER+"imessanger.php");
			   //httppost = new HttpPost("http://route.pixub.com/imessanger.php");
			
			
			try {
				
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
			nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
			if(Header.compareTo("msg_read")==0)
			{
			 nameValuePairs.add(new BasicNameValuePair("READMID", READMID ));
			}
			else
			{
			    if(helper.getLastMsgList()!=null)
				{
				    nameValuePairs.add(new BasicNameValuePair("position",String.valueOf(utilhelper.getMsgListPostLoc())));	
					// nameValuePairs.add(new BasicNameValuePair("position","27"));	
				}
				else
				{
					   nameValuePairs.add(new BasicNameValuePair("position","0"));	
				}
			}
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			 
			
			// Execute HTTP Post Request
			 iresponse = httpclient.execute(httppost, responseHandler);
			 


			 
			} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
	    		Toast.makeText(getActivity().getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();
	    		cancel(true);
			} catch (IOException e) {
			// TODO Auto-generated catch block
				if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
			}


			}
			 
			}


		
		

	    public void Go_Profile(String usr) {
	    	Intent intent = new Intent(getActivity(), ProfileActivity.class);
	    	intent.putExtra(USR_DETAIL, usr);
	    	startActivity(intent);
	    	
	    	}  
	    
	 

		void Gomyprof()
	    {
			
	    	Intent MyProfintent = new Intent(getActivity(), MyProfileActivity.class);
	    	startActivity(MyProfintent);
	    	
	 
	    }
	    
	    
		public void ClickName(View v) {

			final int position = list.getPositionForView(v);
			if (position != ListView.INVALID_POSITION) {
				model.moveToPosition(position);
				String Name = helper.getMsgListUsername(model);
	        if(Name.equals(utilhelper.getUSER()))
	        {
	        	
	        	Gomyprof();
	        	
	        }else{
	        	
		     Go_Profile(Name);
		     
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
	                    	   Get_IMList();
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
		    newFragment.show(getActivity().getFragmentManager(), "noneterror");
		}
		

		
		
		
	    class ContactDatabaseAdapter extends CursorAdapter {

		ContactDatabaseAdapter(Cursor c) {
		super(getActivity(),c);
		}
		@Override
		public void bindView(View row, Context ctxt,Cursor c) {
	    contactDatabaseHolder holder=(contactDatabaseHolder)row.getTag();
		holder.populateFrom(c, helper, utilhelper);
		
	    /*if(lvboolaray4[c.getPosition()] == false)
	    {		    	
	    	holder.ureadimg.setVisibility(View.GONE);
	    }
	    else
	    {
	    	holder.ureadimg.setVisibility(View.VISIBLE);
	    } */
	    
		if(helper.getMsgListStatus(c).equals("unread")) {
			
			holder.ureadimg.setVisibility(View.VISIBLE);
		}		
		else
		{
			holder.ureadimg.setVisibility(View.GONE);

		}
	    
	    
		}
		@Override
		public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
		LayoutInflater inflater=getActivity().getLayoutInflater();
		View row=inflater.inflate(R.layout.msg_row, parent, false);
		contactDatabaseHolder holder=new contactDatabaseHolder(row);
		row.setTag(holder);
		return(row);
		}

	}    





	static class contactDatabaseHolder {
	private TextView msguname=null;
	private EmojiconTextView msgbody=null;
	private TextView msgdate=null;
	private ImageView ureadimg=null;
	private NetworkImageView profimg=null;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	contactDatabaseHolder(View row) {

	msguname=(TextView)row.findViewById(R.id.msgrow_username);
	msgbody=(EmojiconTextView)row.findViewById(R.id.msgrow_body);
	msgdate=(TextView)row.findViewById(R.id.msgrow_date_time);
	ureadimg=(ImageView)row.findViewById(R.id.msgrow_unread);
	profimg=(NetworkImageView)row.findViewById(R.id.msgrow_profimg);
	
	//profimg.setErrorImageResId(R.drawable.me);
	profimg.setDefaultImageResId(R.drawable.me);

	}


	void populateFrom(Cursor c, Database_Two helper, Util_Database utilhelper) {
		//msguname.setText(helper.getMsgListUsername(c));
		msgbody.setText(helper.getMsgListBody(c));
		msgbody.setUseSystemDefault(false);
		msgdate.setText(helper.getMsgListSTAMP(c));
		
		Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
		String atMentionScheme = "profile://";


		TransformFilter transformFilter = new TransformFilter() {
			//skip the first character to filter out '@'
			public String transformUrl(final Matcher match, String url) {
				return match.group(1);
			}
		};

		
		msguname.setText("@"+helper.getMsgListUsername(c));
		if(helper.getMsgListUsername(c).equals(utilhelper.getUSER()))
		{
		Linkify.addLinks(msguname, atMentionPattern, "myprofile://", null, transformFilter); 
		}
		else
		{
		Linkify.addLinks(msguname, atMentionPattern, atMentionScheme, null, transformFilter); 
		}
		

	   stripUnderlines(msguname);



		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		profimg.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getMsgListUID(c).toString()+".png", imageLoader);


		  /*if(utilhelper.getisIMG_MSGLST(helper.getMsgListUsername(c))!=null)
		  {
			  
			if(utilhelper.getisIMG_MSGLST(helper.getMsgListUsername(c).toString()).equals("YES"))
			{
				profimg.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeByteArray(utilhelper.getPPIC_MSGLST(helper.getMsgListUsername(c).toString()), 0, utilhelper.getPPIC_MSGLST(helper.getMsgListUsername(c).toString()).length), 150));
				//profimg.setImageBitmap(BitmapFactory.decodeByteArray(utilhelper.getPPIC_MSGLST(helper.getMsgListUsername(c).toString()), 0, utilhelper.getPPIC_MSGLST(helper.getMsgListUsername(c).toString()).length));

		    }
			else
			{

				profimg.setImageResource(R.drawable.me);	
			}
			
		  }
		  else
		  {
			  profimg.setImageResource(R.drawable.me);
		  }*/
			
		
		if(helper.getMsgListStatus(c).equals("unread")) {
			
			ureadimg.setImageResource(R.drawable.ic_unread_msg);
			//ureadimg.setVisibility(View.VISIBLE);
		}
		
		
		else
		{
			//ureadimg.setVisibility(View.GONE);
		}
	    
	    
	    
	}
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



	private void clearNotification() {
	NotificationManager mgr=
	(NotificationManager)getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
	mgr.cancel(Notify_Handler.IMNOTIFICATION_ID);
	}


	public void ClosMe() {

	Intent intent = getActivity().getIntent();
	getActivity().overridePendingTransition(0, 0);
	intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	getActivity().finish();
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