package com.lethalsys.mimix;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
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
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;

@SuppressLint("NewApi")
public class HashActivity extends Activity implements OnMenuItemClickListener {

	public final static String Disp_Uname = "com.lethalsys.mimix.Disp_Uname";
	public final static String Disp_Pbody = "com.lethalsys.mimix.Disp_Pbody";
	public final static String Disp_Tstamp = "com.lethalsys.mimix.Disp_Tstamp";
	public final static String Disp_Rating = "com.lethalsys.mimix.Disp_Rating";
	public final static String Disp_Pimage = "com.lethalsys.mimix.Disp_Pimage";
	public final static String PostID = "com.lethalsys.mimix.PID";
	public final static String USR_DETAIL = "com.lethalsys.mimix.U_DETAIL";


	
	//private LinearLayout Loader;

	ListView list;
	
	static Util_Database utilhelper=null;
	
	public static Handler HashUiHandler = null;
	
	Cursor model=null;
	Database_Two helper=null;	
	HashDatabaseAdapter adapter=null;

	private String UserID;
	String iresponse;
	InputStream response; 
	private String sresponse;
	private String dresponse;

	
	String hashtag;
	
	Boolean ErrorDlg = true;

	Boolean isLoading = false;
    
	private ProgressDialog pDialog;
    
    String postID;

	
	String sharepid;
	String sharebody;
	String sharerating;			
    byte[] shareimgdata;
    
    
	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	JSONArray data = null;

	public ProgressBar CProgress;
	
	LinearLayout emptyview;
	
	public static Boolean hashactv = true;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hash);
		
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.fadeactionbarbg));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		utilhelper=new Util_Database(this);
		UserID = utilhelper.getUSER_ID();
		
		Uri data = getIntent().getData();
		if(data != null){
		    String uri = data.toString();
		    String Tag =uri.toString().split("/")[2];// uri.substring(uri.indexOf("#")+1);
		    hashtag = "#"+Tag;
		}
		 
		setTitle(hashtag);
		
		//Toast.makeText(getApplicationContext(),hashtag, Toast.LENGTH_LONG).show();
		
			/*Loader =(LinearLayout)findViewById(R.id.hash_loader);
			Loader.setVisibility(View.GONE);*/

		    
		    list=(ListView)findViewById(R.id.show_hash);  
		    
			  LayoutInflater LInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			   emptyview = (LinearLayout) LInflater.inflate(
						R.layout.lv_set_empty, null, false);
				
		    
			
	        LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
					R.layout.loader_footer, null, false);
			
			CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);
			//CProgress.setVisibility(View.GONE);
			 			        
			list.addFooterView(mFooterView);
			list.setFooterDividersEnabled(false);
		    
		    
		    helper=new Database_Two(this);
		    
			helper.ClearHash();
			
		    model=helper.getAllHash();
			startManagingCursor(model);
			adapter=new HashDatabaseAdapter(model);
			list.setAdapter(adapter);
			
			list.setOnScrollListener(onscroll);
			list.setOnItemClickListener(onHashClick);
			
			
			
			// Set a listener to be invoked when the list should be refreshed.
			((PullAndLoadListView) list)
					.setOnRefreshListener(new OnRefreshListener() {

						public void onRefresh() {
							// Do work to refresh the list here.
							 Get_Hash("refresh");	 
						}
					});



			
			
			Get_Hash("refresh");
			
		    HashUiHandler = new Handler() // Receive messages from service class
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
	                    	  Get_Hash("refresh");	                          
	                      }
	                      break;
	                      
	                      
	                      case 2:
	                      {
	                    	  String coll;
	                    	  String frag[];
	                    	  
	                      	  coll = (String)msg.obj;
	                    	  frag = coll.trim().split(":");
	                    	  
	                    	  helper.updateHashRate(frag[0],frag[1],frag[2]);
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

	   	    			   helper.updateHashComment(frag[0],frag[1]);
	   	    			   model.requery();

	   	    		   }
	   	    		   break;

	 
	                    default:
	                    break;
	                }
	            }
	        };
			
			
			
			
			
	
	}
	
	

	@Override
	public void onResume() {
	super.onResume();
	hashactv = true;
	ErrorDlg = true;
	//Get_Hash("refresh");
	//Disp_Pimage = null;
	}
	
	@Override
	public void onPause() {
	super.onPause();
	hashactv = false;
	ErrorDlg = false;
	}
	
	
	
		public void Get_Hash(String header)
		{
		    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		    
		    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		    {
		    	
		    	new MyAsyncTask().execute(header);
	        }
	        else
	        {
	        	if(header.compareTo("get_post")==0)
	        	{
	        		ShowNoNetErrorDialoag();
	        	}
	        	else
	        	{
	        		ShowInitErrorDialoag();
	        	}
	        		
	        	
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
		                  Get_Hash("get_hash");
						}
					}

				}
			}
			else
			{
				//if(isLoading==false)
				//{ 
					Get_Hash("refresh");
				//}
			}
				
				
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}};
			
			
			
			private AdapterView.OnItemClickListener onHashClick=new
					AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent,
					View view, int position,
					long id) {
					model.moveToPosition(position-1);
					
					if(helper.getHashType(model).equals("YES"))
					{

						if(helper.getHash_isExpanded(model).equals("NO"))
						{
							HashDatabaseHolder holder = (HashDatabaseHolder)view.getTag();
							holder.pimage.setAdjustViewBounds(true);
							helper.updateHash_isExpanded(helper.getHashPID(model), "'"+"YES"+"'");
						}
						else
						{
							HashDatabaseHolder holder = (HashDatabaseHolder)view.getTag();
							holder.pimage.setAdjustViewBounds(false);
							holder.pimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
							helper.updateHash_isExpanded(helper.getHashPID(model), "'"+"NO"+"'");
						}
						
					}
					


					
					}
					};
					
					
					
		
		
	    
	    
	    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
	   	 
	    	@Override
	    	protected Double doInBackground(String... params) {

	    		postData(params[0]);
	    	  	
	    	return null;
	    	}
	    	 
	    	
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
							isLoading = true;
					//Loader.setVisibility(View.VISIBLE);
					
					CProgress.setVisibility(View.VISIBLE);
				     
				
	        }
	    	
	    	
	    	@SuppressWarnings("deprecation")
			protected void onPostExecute(Double result){
	    		
	    		if(null!=iresponse)
	    		{
	    			//ShowTost(iresponse);
	    			//ShowErrorDialoag();
	    			
	            if(iresponse.trim().length()!=0)
	            {
	          
	            	
	            
	            try{
	            	JSONObject jsonObj = new JSONObject(iresponse.trim());
	            	

	            	
	            	if(jsonObj.isNull("posts")==false)
	    			{
	            		
	             
	            	
	               
	            	data = jsonObj.getJSONArray("posts");
	            
	            	 if(data.length()>0)
	                 {
	                 	if(jsonObj.isNull("extra")==false)
	                	{
	                 		helper.ClearHash();
	               		    /*utilhelper.ClearHashPostLoc();
	            		    utilhelper.insertHashPostLoc(data.length());
	            		    
	            		    //ShowTost(String.valueOf(data.length()));
	                	}
	                 	else
	                 	{
	                 		 int Incr = utilhelper.getHashPostLoc()+data.length();
	                		 utilhelper.ClearHashPostLoc();
	                		 utilhelper.insertHashPostLoc(Incr);*/
	                 	}
	                 	
	            		 
	            		 //ShowTost(String.valueOf(Incr));
	                 }
	    			
	            
	                for(int i=0;i<data.length();i++)
	                {
	                	JSONObject c = data.getJSONObject(i);
	                	String usrname = c.getString("username");
	                	String stamp = c.getString("stamp");
	                	String userid = c.getString("userid");
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
	                		    helper.insertHash(pid,userid,usrname,body,null,stamp,"NO",
	                		    		finalrating,String.valueOf(ratecount),String.valueOf(commentcount));            			
	                	    } 
	                    	else
	                    	{	                        	
	                        	byte[] imgdata = Base64.decode(pimg, 0);	                    		
	                    		helper.insertHash(pid,userid,usrname,body,imgdata,stamp,"YES",
	                    				finalrating,String.valueOf(ratecount),String.valueOf(commentcount));	                    		
	                    	}
	                   	
	                }
	                
	                
	                
	    			}
	            	
	        		if(data.length()<1)
	        		{
	        			
	        		    //helper.insert(null,null,"Mimix","You have No post update yet. \nMake a post or follow people you know to see their post update.",null,null,"NO",null);       		
	        		}
	                model.requery();
	            	 
	            } catch (JSONException e){
	            	
	            }
	            	
	    	
	           }
	    	
	    	
	    		//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
	    		//confirmFireMissiles();
	    		}


	    		isLoading = false;
	    	    //Loader.setVisibility(View.GONE);
	    		CProgress.setVisibility(View.GONE);	    		
	    		
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

	    		
	    		String url = LoginActivity.SERVER+"get_hash.php"; 
	    		String query=null;

	    		try {
	    			
	    			if(Header.compareTo("refresh")==0)
	    			{
	    				
	    				
	    				 query = String.format("PacketHead=%s&UID=%s&hash=%s", 
							     URLEncoder.encode(Header, "UTF-8"), 
							     URLEncoder.encode(UserID, "UTF-8"),
							     URLEncoder.encode(hashtag, "UTF-8"));
	    			}
	     			
	    			else
	    			{
					    
	    			 
	    			query = String.format("PacketHead=%s&UID=%s&hash=%s&PostLoc=%s", 
					     URLEncoder.encode(Header, "UTF-8"), 
					     URLEncoder.encode(UserID, "UTF-8"),
			 		     URLEncoder.encode(hashtag, "UTF-8"),
					     URLEncoder.encode(helper.getLastHash(), "UTF-8"));
	    			}    
	    			  
					    
				
	    		
	    		/*URLConnection connection = new URL(url + "?" + query).openConnection();
	    		connection.setRequestProperty("Accept-Charset", "UTF-8");
	    		InputStream response = connection.getInputStream()*/
	    		   

			    URLConnection connection = new URL(url).openConnection();
	    		connection.setDoOutput(true); // Triggers POST.
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

	    		/*String mResponse=null; 
	    		
	    		BufferedReader br = new BufferedReader(new InputStreamReader(response));
	    		StringBuilder sb = new StringBuilder();
	    		while((mResponse = br.readLine())!=null)
	    		{
	    			sb.append(mResponse);
	    		}
	    		br.close();
	    		
	    		response.close();
	    		
	    		iresponse = sb.toString();
	    		sb.setLength(0);*/
	    		
	    		/*ByteArrayOutputStream oas = new ByteArrayOutputStream();
	    		copyStream(response,oas); 
	    		
	    		iresponse = oas.toString();
	    		oas.close();
	    		oas = null;*/
	    		
	    		iresponse = IOUtils.toString(response);
	    	
				} catch (IOException e) {
					if(ErrorDlg == true)
					{
						if(Header.compareTo("refresh")==0)
						{
							ShowInitErrorDialoag();
						}
						else
						{
							ShowNoNetErrorDialoag();
						}
						
						 ErrorDlg = false;
					}
					
				} 
	    		
	    		
	    	}


	    	}
	    

	    

	    public void Go_Profile(String usr) {
	    	Intent intent = new Intent(this, ProfileActivity.class);
	    	intent.putExtra(USR_DETAIL, usr);
	    	startActivity(intent);
	    	
	    	}  
	    
	 

		void Gomyprof()
	    {
			
	    	Intent MyProfintent = new Intent(this, MyProfileActivity.class);
	    	startActivity(MyProfintent);
	    	
	 
	    }
	    
	    
		public void ClickName(View v) {

			final int position = list.getPositionForView(v);
			if (position != ListView.INVALID_POSITION) {
				model.moveToPosition(position-1);
				String Name = helper.getHashName(model);
	        if(Name.equals(utilhelper.getUSER()))
	        {
	        	
	        	Gomyprof();
	        	
	        }else{
	        	
		     Go_Profile(Name);
		     
	        }

			}	


		}	

		
		
		

		public void Delete_post(final String PostID)
		{
			mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

			if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
			{



				new AsyncTask<String, Integer, Double>() {
					@Override
					protected void onPreExecute() {
						super.onPreExecute();

						pDialog = new ProgressDialog(HashActivity.this);
						pDialog.setMessage("Deleting post...");
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

						httppost = new HttpPost(LoginActivity.SERVER+"add_get_post.php");

						try {

							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("PacketHead","delete_post" ));
							nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
							nameValuePairs.add(new BasicNameValuePair("PID",  PostID));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							ResponseHandler<String> responseHandler = new BasicResponseHandler();


							// Execute HTTP Post Request
							dresponse = httpclient.execute(httppost, responseHandler);





						} catch (IOException e) {
							pDialog.dismiss();
							if(ErrorDlg == true)
								ShowDeletePostErrorDialoag();		
						}


						return null;
					}

					protected void onProgressUpdate(Integer... progress){

					}


					protected void onPostExecute(Double result){


						if(null!=dresponse)
						{

							if(dresponse.trim().length()!=0)
							{

								try{


									JSONObject jsonObj = new JSONObject(dresponse.trim());               	


									if(jsonObj.isNull("data")==false)
									{

										data = jsonObj.getJSONArray("data");

										JSONObject d = data.getJSONObject(0);
										String msg = d.getString("msg");


										if("deleted".compareTo(msg)==0)
										{

											pDialog.dismiss();
											Toast.makeText(getApplicationContext(),"Post deleted" , Toast.LENGTH_LONG).show();
                                            helper.DeleteHash(PostID);
                                            model.requery();

										}

										else
										{
											pDialog.dismiss();
											Toast.makeText(getApplicationContext(),"Post could not be deleted" , Toast.LENGTH_LONG).show();
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
					ShowDeletePostErrorDialoag();
			}	
		}


		
		
		
		
		
		
		
	    public void Pop_up(View v)
	    {
	    	
			final int position = list.getPositionForView(v);
			if (position != ListView.INVALID_POSITION) {
				model.moveToPosition(position-1);

	         postID = helper.getHashPID(model);
	        
	         if(helper.getHashUID(model).equals(UserID))
	         {
	     		PopupMenu popupMenu = new PopupMenu(HashActivity.this,v );				
	     		popupMenu.setOnMenuItemClickListener(HashActivity.this);				
	     		popupMenu.inflate(R.menu.row_mypopup_menu);				
	     		popupMenu.show();
	         }
	         else
	         {
	     		PopupMenu popupMenu = new PopupMenu(HashActivity.this,v );				
	     		popupMenu.setOnMenuItemClickListener(HashActivity.this);				
	     		popupMenu.inflate(R.menu.row_popup_menu);				
	     		popupMenu.show();	
	         }

	     	
	 		}
	 		
	 		
	    }
	 	
	    

	 public boolean onMenuItemClick(MenuItem item) {
	 switch (item.getItemId()) {

	 case R.id.row_popup_delete:
	 {
		 Delete_post(postID);
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
	     

	 	

	    
	
	    
	    public void OpenRateDlg(View v)
	    {
	   	 final int position = list.getPositionForView(v);
	     if (position != ListView.INVALID_POSITION) {
				model.moveToPosition(position-1);
				String pid = helper.getHashPID(model);
				
	    	Intent intent = new Intent(this, RatingActivity.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        intent.putExtra(PostID, pid);
	    	startActivity(intent);
	    	
	     }
	    }
	
	    
	    
	    
	    public void OpenCommentDlg(View v)
	    {
	   	 final int position = list.getPositionForView(v);
	     if (position != ListView.INVALID_POSITION) {
				model.moveToPosition(position-1);
				String pid = helper.getHashPID(model);
				
	    	Intent intent = new Intent(this, CommentActivity.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        intent.putExtra(PostID, pid);
	    	startActivity(intent);
	    	
	     }
	    }
	    
	    
	    
	    
	    
	    
	    public void Share(View v)
	    {
	   	 final int position = list.getPositionForView(v);
	     if (position != ListView.INVALID_POSITION) {
				model.moveToPosition(position-1);
				sharepid = helper.getHashPID(model);
				sharebody = helper.getHashBody(model);
				sharerating = helper.getHashRating(model);			
			    shareimgdata = helper.getHashBmp(model);

			    ShareDialog();
	 
	    	
	     }
	    }
	    
	    
	    
	    
	    
	    
	    @SuppressLint("ValidFragment")
		public class ShareDialogFragment extends DialogFragment {
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            // Use the Builder class for convenient dialog construction
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            //builder.setMessage("Do you want to share this post to your followers?")
	            builder.setView(getLayoutInflater().inflate(R.layout.share_post_alert, null))
	                   .setPositiveButton("Share", new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	               		   
	                    	if(shareimgdata!=null)
	            		    {
	            		    	Share_post("add_post_img");
	            		    }
	            		    else
	            		    {
	            		    	Share_post("add_post");
	            		    }
	                    	
	                       }
	                   })
	                    .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
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
	    
	    
		public void ShareDialog() {
		    DialogFragment newFragment = new ShareDialogFragment();
		    newFragment.show(getFragmentManager(), "Share");
		}
	    
	    
	    public void Share_post(final String Header){
	    	
		    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
		    {
	        
	    	
	    new AsyncTask<String, Integer, Double>() {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*pDialog = new ProgressDialog(DisplayHashActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();	*/
		}
		
		@Override
		protected Double doInBackground(String... params) {

			
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
			
		HttpPost httppost = new HttpPost(LoginActivity.SERVER+"add_get_post.php");
		
		 
		try {
			
		// Add your data	
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
		nameValuePairs.add(new BasicNameValuePair("UID",UserID));
		nameValuePairs.add(new BasicNameValuePair("post_body", sharebody));
		if(Header.compareTo("add_post_img")==0)
		{	
		nameValuePairs.add(new BasicNameValuePair("post_img",Base64.encodeToString(shareimgdata, 0)));
		}
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		 

		
		// Execute HTTP Post Request
		sresponse = httpclient.execute(httppost, responseHandler);
		 

		
		} catch (IOException e) {
		
			Toast.makeText(getApplicationContext(),"Post could not be shared" , Toast.LENGTH_LONG).show();

		}
		

	    
			return null;
		}
		
	   	protected void onProgressUpdate(Integer... progress){

	   	}

		
		protected void onPostExecute(Double result){

			if(sresponse.trim() != null)
			{
				
		  try{
			  
				JSONObject jsonObj = new JSONObject(sresponse.trim());      	
	        	
	        	
				if(jsonObj.isNull("data")==false)
				{
					JSONArray data = jsonObj.getJSONArray("data");

					JSONObject d = data.getJSONObject(0);
					String msg = d.getString("msg");


					if("Post added".compareTo(msg)==0)
					{
						
						Toast.makeText(getApplicationContext(),"Post shared" , Toast.LENGTH_LONG).show();	
		            	//if(isLoading==false)
		    			//{ 
		            		//Get_Post("refresh");
		    			//}    

					}

					else
					{
						Toast.makeText(getApplicationContext(),"Post could not be shared" , Toast.LENGTH_LONG).show();
					}
	            
				}
	    		
	        	
	          
				
			 } catch (JSONException e){
	         	
	         }
				
			}
	   
			
		
			 //pDialog.dismiss();	

			
		}
	    
	    }.execute();
	    
	        }
	        else
	        {
	        	//pDialog.dismiss();	
				/*if(ErrorDlg == true);
				ShowNoNetErrorDialoag();*/
	        }	
	    
	    } 
	    
	    
	    
	    
	 
	    

		@SuppressLint("ValidFragment")
		public class DeletePostErrorDialogFragment extends DialogFragment {
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// Use the Builder class for convenient dialog construction
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//builder.setMessage(LoginActivity.neterrormsg)
				builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
				.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Delete_post(postID);
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


		public void ShowDeletePostErrorDialoag() {
			DialogFragment newFragment = new DeletePostErrorDialogFragment();
			newFragment.show(getFragmentManager(), "logouterror");
		}


		
	    
	    
	    
	
	
    @SuppressLint("ValidFragment")
	public class InitNetworkErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setMessage(LoginActivity.neterrormsg)
            builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
                   .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	 Get_Hash("refresh");
                    	 ErrorDlg = true;
                       }
                   })
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                    	   ErrorDlg = true;
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    
    
	public void ShowInitErrorDialoag() {
	    DialogFragment newFragment = new InitNetworkErrorDialogFragment();
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
                   .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	if(isLoading==false)
               			{ 
                       	 Get_Hash("get_hash");
               			}
                    	 ErrorDlg = true;
                       }
                   })
                   
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                    	   ErrorDlg = true;
                       }
                   });
                  
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }  
    
    
	public void ShowNoNetErrorDialoag() {
	    DialogFragment newFragment = new NoNetErrorDialogFragment();
	    newFragment.show(getFragmentManager(), "noneterror");
	}
	

	
	
	
	class HashDatabaseAdapter extends CursorAdapter {
        
		@SuppressWarnings("deprecation")
		HashDatabaseAdapter(Cursor c) {
		super(HashActivity.this,c);
		}
	    
	    @Override
		public void bindView(View row, Context ctxt,Cursor c) {
	    HashDatabaseHolder holder=(HashDatabaseHolder)row.getTag();
	    holder.populateFrom(c, helper, utilhelper);
	    if(helper.getHashBody(c).length()!=0)	   
	    {		    	
	    	holder.post_body.setVisibility(View.VISIBLE);
	    }
	    else
	    {
	    	holder.post_body.setVisibility(View.GONE);
	    }

		 
	    
		
	if(helper.getHashType(c).equals("YES"))
	{
		holder.pimage.setVisibility(View.VISIBLE);
		
		if(helper.getHash_isExpanded(c).equals("NO"))
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
		View row=inflater.inflate(R.layout.hash_row, parent, false);
		HashDatabaseHolder holder=new HashDatabaseHolder(row);
		row.setTag(holder);     
		return(row);              
		}
	
	}    



	
	
	
	
	
	
	 static class HashDatabaseHolder {
			private TextView uname=null;
			private TextView post_body=null;
			private TextView date_time=null;
			private NetworkImageView image=null;
			private ImageView pimage=null;
			private RatingBar RateBar;
			Button ratebtn;
			Button commentbtn;
			Button sharebtn;
			ImageLoader imageLoader = AppController.getInstance().getImageLoader();
			Util_Database utilhelper2;
			HashDatabaseHolder(View row) {
			
			uname=(TextView)row.findViewById(R.id.hashrow_username);
			post_body=(TextView)row.findViewById(R.id.hashrow_body);
			date_time=(TextView)row.findViewById(R.id.hashrow_date_time);
			image=(NetworkImageView)row.findViewById(R.id.hashrow_image);
			pimage=(ImageView)row.findViewById(R.id.hashrow_postimage);
			RateBar=(RatingBar)row.findViewById(R.id.hashrow_ratingbar);
		    ratebtn=(Button)row.findViewById(R.id.hashrate_clk);
		    sharebtn=(Button)row.findViewById(R.id.hashshare_clk);
		    commentbtn=(Button)row.findViewById(R.id.hashcomment_clk);
		    
			//image.setErrorImageResId(R.drawable.me);
			image.setDefaultImageResId(R.drawable.me);
			}
			      
			
			void populateFrom(Cursor c, Database_Two helper, Util_Database utilhelper) {
				
				if (imageLoader == null)
					imageLoader = AppController.getInstance().getImageLoader();

				image.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getHashUID(c).toString()+".png", imageLoader);


				//uname.setText(helper.getHashName(c));
				
				date_time.setText(helper.getHashDate(c));
									
				if(helper.getHashBody(c).length()<1)
				{
					post_body.setText(null);
					/*post_body.setLayoutParams(new LinearLayout.LayoutParams(
				    		ViewGroup.LayoutParams.WRAP_CONTENT,
				    		ViewGroup.LayoutParams.WRAP_CONTENT));*/
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
					
					post_body.setText(helper.getHashBody(c));
					
					uname.setText("@"+helper.getHashName(c));
					if(helper.getHashName(c).equals(utilhelper.getUSER()))
					{
					Linkify.addLinks(uname, atMentionPattern, "myprofile://", null, transformFilter); 
					}
					else
					{
					Linkify.addLinks(uname, atMentionPattern, atMentionScheme, null, transformFilter); 
					}

					stripUnderlines(uname);


					Linkify.addLinks(post_body, Linkify.ALL);
					Linkify.addLinks(post_body, atMentionPattern, atMentionScheme, null, transformFilter); 
					Linkify.addLinks(post_body, HashPattern, HashScheme, null, transformFilter); 
	            
	          
				}	
					
				
				


				   
				    if(helper.getHashType(c).compareTo("NO")==0)
				    {
				    	pimage.setImageBitmap(null);
					    /*pimage.setLayoutParams(new LinearLayout.LayoutParams(
					    		ViewGroup.LayoutParams.WRAP_CONTENT,
					    		ViewGroup.LayoutParams.WRAP_CONTENT));*/
			        }
			    
			        else 
			        {
					
			    	    pimage.setImageBitmap(BitmapFactory.decodeByteArray(helper.getHashBmp(c), 0, helper.getHashBmp(c).length));
					    //pimage.setLayoutParams(new LinearLayout.LayoutParams(90,90));	
					    /*pimage.setLayoutParams(new LinearLayout.LayoutParams(
					    		ViewGroup.LayoutParams.WRAP_CONTENT,
					    		ViewGroup.LayoutParams.WRAP_CONTENT));*/
			        }
				    
				    
				    
				    RateBar.setRating(Float.parseFloat(helper.getHashRating(c)));
				    
				    //ratebtn.setVisibility(View.VISIBLE);
					//sharebtn.setVisibility(View.VISIBLE);
				    
					ratebtn.setText("rate("+helper.getHashRateCount(c)+")");
					commentbtn.setText("comment("+helper.getHashCommentCount(c)+")");

				    
				
			   
			   
			}
			}   
	    
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.display_hash_main, menu);
		return true;
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
