package com.lethalsys.mimix;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
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
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class Notification_Fragment extends Fragment {
	 
	
	public final static String NUSR_DETAIL = "com.lethalsys.mimix.U_DETAIL";
	public final static String NPOST_ID = "com.lethalsys.mimix.U_DETAIL";

	private String UserID;
	
	Util_Database utilhelper;
	
	InputStream response;
	String iresponse;
	
	Cursor model=null;
	Database_Two helper=null;
	NotifyDatabaseAdapter adapter =null;
	
	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	ListView list;
	
	Boolean ErrorDlg = true;
	
	public ProgressBar CProgress;
	
	LinearLayout emptyview;
	
	
	
	
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) { 
			
	        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
	        
	        
			utilhelper = new Util_Database(getActivity());
			UserID = utilhelper.getUSER_ID();

			
			helper=new Database_Two(getActivity());
		    

			
			list=(ListView)rootView.findViewById(R.id.lvnot_list);
			
			  LayoutInflater LInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			   emptyview = (LinearLayout) LInflater.inflate(
						R.layout.lv_set_empty, null, false);
				
			
	        LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
					R.layout.loader_footer, null, false);
			
			CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);
			//CProgress.setVisibility(View.GONE);
			 			        
			list.addFooterView(mFooterView);
			
			model=helper.getNotify();
			getActivity().startManagingCursor(model);
			adapter=new NotifyDatabaseAdapter(model);
			list.setAdapter(adapter);
			
			list.setOnItemClickListener(onNotClick);
			
     
			//Toast.makeText(getActivity(),"NERROORR" , Toast.LENGTH_LONG).show();							

	       
	        return rootView;
	    }
		

		
		
		public void onResume()
		{
			super.onResume();
			ErrorDlg = true;
			
			getNotifications();
			//Toast.makeText(getApplicationContext(),String.valueOf(utilhelper.getNotifPostLoc()) , Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onPause() {
		super.onPause();
		ErrorDlg = false;

		}
		
		
		
		private AdapterView.OnItemClickListener onNotClick=new
				AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
				View view, int position,
				long id) {
				model.moveToPosition(position);
				
				String dname = helper.getNotifyName(model);
				String dtype = helper.getNotifyType(model);
				String dbody = helper.getNotifyBody(model);
				String dstamp = helper.getNotifyDate(model);
				
				if(dtype.equals("NOTIFY_NEW_FOLLOW"))
				{
					if(dname.equals(utilhelper.getUSER()))
					{
						Go_My_Profile();
					}
					else
					{
					    Go_Profile(dname);
					}				
				}
				else if(dtype.equals("NOTIFY_POST_MENTION"))
				{
					String ddata = helper.getNotifyData(model);
					Go_Post(ddata);
				}
			
				
		

				
				}
				};
				
				
			    public void Go_My_Profile() {
			    	Intent intent = new Intent(getActivity(), MyProfileActivity.class);
			    	//intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);		    	
			    	startActivity(intent);
			    	}  
							
				
			    public void Go_Profile(String usr) {
			    	Intent intent = new Intent(getActivity(), ProfileActivity.class);
			    	//intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			    	intent.putExtra(NUSR_DETAIL, usr);
			    	startActivity(intent);
			    	}  
				
		
			    public void Go_Post(String pid) {
			    	Intent intent = new Intent(getActivity(), DisplayNotifPostActivity.class);
			    	//intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			    	intent.putExtra(NPOST_ID, pid);
			    	startActivity(intent);
			    	}  
					
			    	
		
		
		
	    public void getNotifications(){
	    	

	    	
		    mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		    {
		    	
	    	
	    new AsyncTask<String, Integer, Double>() {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			list.removeFooterView(emptyview);
			CProgress.setVisibility(View.VISIBLE);

		}
		
		@Override
		protected Double doInBackground(String... params) {
			
			String url = LoginActivity.SERVER+"get_notified.php"; 
			String query=null;

			try {
				     
					    if(helper.getLastNotify()!=null)
						{
			 			     query = String.format("PacketHead=%s&UID=%s&position=%s", 
			 					     URLEncoder.encode("getNotifications", "UTF-8"), 
			 					     URLEncoder.encode(UserID, "UTF-8"),
			 					     URLEncoder.encode(helper.getLastNotify(), "UTF-8"));	 		 
						}
						else
						{
			 			     query = String.format("PacketHead=%s&UID=%s&position=%s", 
			 					     URLEncoder.encode("getNotifications", "UTF-8"), 
			 					     URLEncoder.encode(UserID, "UTF-8"),
			 					     URLEncoder.encode("0", "UTF-8"));		 		 
						}
		
			   

		    URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true); 
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(30000);
			//connection.setDoInput(true);
			java.io.OutputStream out=connection.getOutputStream();
			out.write(query.getBytes("UTF-8"));
			
			response = connection.getInputStream();
			
			out.flush();
			out.close();

			iresponse = IOUtils.toString(response);
		
			} catch (IOException e) {
				if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
			} 
	     

			return null;
		}
		
	   	protected void onProgressUpdate(Integer... progress){

	   	}

		
		@SuppressWarnings("deprecation")
		protected void onPostExecute(Double result){
		
	        if(null!=iresponse)
			{
			
				
	        if(iresponse.trim().length()!=0)
	        {
	      
	        try{
	        	JSONObject jsonObj = new JSONObject(iresponse.trim());
	        	
	        	if(jsonObj.isNull("notifs")==false)
				{
	        		 		
	           
	        	 JSONArray data = jsonObj.getJSONArray("notifs");
	        
	        	 if(data.length()>0)
	             {
	        		 
	        		// helper.ClearNotifications();
	        		 
						/*int Incr = utilhelper.getNotifPostLoc()+data.length();
						utilhelper.ClearNotifPostLoc();
						utilhelper.insertNotifPostLoc(Incr);*/		
	        
	            for(int i=0;i<data.length();i++)
	            {
	            	JSONObject c = data.getJSONObject(i);
	            	String nid = c.getString("nid");
	            	String type = c.getString("type");
	            	String body = c.getString("body");
	            	String ndata = c.getString("ndata");
	            	String stamp = c.getString("stamp");
	            	String uid = c.getString("uid");
	            	String username = c.getString("username");
	            	
	                		
	               helper.insertNotifications(nid,type,ndata,body,stamp,uid,username);
	            
	                		
	            }	
	               	
	                 	
				}
	        	
	    		
	            model.requery();
	        }
	        	 
	        } catch (JSONException e){
	        	
	        }
	        	
		
	       }
		
		
			//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
			//confirmFireMissiles();
			}


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
	    
	    }.execute();
	    
	        }
	        else
	        {
	        	ShowNoNetErrorDialoag();
	        }	
	    
	    }
	    
		
		
		
		
		
		
	    @SuppressLint("ValidFragment")
		public class NoNetErrorDialogFragment extends DialogFragment {
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            // Use the Builder class for convenient dialog construction
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            //builder.setMessage(LoginActivity.neterrormsg)
	            builder.setView(getActivity().getLayoutInflater().inflate(R.layout.network_error_alert, null))
	                   .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                    	 
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
		    DialogFragment newFragment = new NoNetErrorDialogFragment();
		    newFragment.show(getActivity().getFragmentManager(), "noneterror");
		}
		

		
		
		
	    class NotifyDatabaseAdapter extends CursorAdapter {

	    @SuppressWarnings("deprecation")
		NotifyDatabaseAdapter(Cursor c) {
		super(getActivity(),c);
		}
		@Override
		public void bindView(View row, Context ctxt,Cursor c) {
			 NotifyDatabaseHolder holder=( NotifyDatabaseHolder)row.getTag();
		holder.populateFrom(c, helper);
	   /* if(lvboolaray1[c.getPosition()] == false)
	    {		    	
	    	holder.cfollowbtn.setVisibility(View.GONE);
	    }
	    else
	    {
	    	holder.cfollowbtn.setVisibility(View.VISIBLE);
	    }*/
	    

		}
		@Override
		public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
		LayoutInflater inflater=getActivity().getLayoutInflater();
		View row=inflater.inflate(R.layout.notify_row, parent, false);
		 NotifyDatabaseHolder holder=new  NotifyDatabaseHolder(row);
		row.setTag(holder);		
		return(row);
		}

	}    


	static class NotifyDatabaseHolder {
	private NetworkImageView nimage=null;
	private TextView body=null;
	private TextView date=null;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	NotifyDatabaseHolder
	(View row) {

	nimage=(NetworkImageView)row.findViewById(R.id.notifrow_img);
	body=(TextView)row.findViewById(R.id.notifrow_body);
	date=(TextView)row.findViewById(R.id.notifrow_date);
	
	//nimage.setErrorImageResId(R.drawable.me);
	nimage.setDefaultImageResId(R.drawable.me);


	}


	void populateFrom(Cursor c, Database_Two helper) {
		
		/*Pattern tagMatcher1 = Pattern.compile("[@]+[A-Za-z0-9-_]+\\b");	
	    String newActivityURL = "profile://";
	    body.setText(helper.getNotifyBody(c));   
	    Linkify.addLinks(body, tagMatcher1, newActivityURL);*/	
	       
	                                                          
		Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
		String atMentionScheme = "profile://";
		 

		TransformFilter transformFilter = new TransformFilter() {
		        //skip the first character to filter out '@'
		        public String transformUrl(final Matcher match, String url) {
		                return match.group(1);
		        }
		};
		 
	    body.setText(helper.getNotifyBody(c));   

		Linkify.addLinks(body, Linkify.ALL);
		Linkify.addLinks(body, atMentionPattern, atMentionScheme, null, transformFilter); 

		stripUnderlines(body);
		
		date.setText(helper.getNotifyDate(c));

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		nimage.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getNotifyUID(c).toString()+".png", imageLoader);	
		
	    
	}
	} 

		
		
		
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