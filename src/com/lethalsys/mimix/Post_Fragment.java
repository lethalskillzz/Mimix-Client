package com.lethalsys.mimix;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;

public class Post_Fragment extends Fragment{
	 
	public final static String USR_DETAIL = "com.lethalsys.mimix.U_DETAIL";


	public final static String Disp_Uname = "com.lethalsys.mimix.Disp_Uname";
	public final static String Disp_Pbody = "com.lethalsys.mimix.Disp_Pbody";
	public final static String Disp_Tstamp = "com.lethalsys.mimix.Disp_Tstamp";
	public final static String Disp_Rating = "com.lethalsys.mimix.Disp_Rating";
	public final static String PostID = "com.lethalsys.mimix.PID";
	public final static String Disp_Pimage = "com.lethalsys.mimix.Disp_Pimage";
	

	public static Handler mUiHandler = null;

	private String iresponse;
	private String UserID;
	private String sresponse;
	private String dresponse;

	ImageView mainmenu_pimg;
	TextView mainmenu_pname;

	private ProgressDialog pDialog;

	private LinearLayout mFooterView;


	ListView list;


	Cursor model=null;
	PostDatabaseAdapter adapter = null;
	Database_One helper=null;

	Database_Two Rhelper=null;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	JSONArray data = null;

	StringBox r;
	String URL;

	String duname;
	String dbody;
	String dstamp;
	String dimg;

	String postID;
	String addon_pkg;

	String sharepid;
	String sharebody;
	String sharerating;			
	byte[] shareimgdata;


	AtomicBoolean isActive=new AtomicBoolean(true);
	int progress=0;	

	String im_tabslct;

	InputStream response; 
	InputStream rresponse; 

	Boolean ErrorDlg = true;
	Util_Database utilhelper=null;


	public ProgressBar CProgress;
	public ProgressBar addon_loading;
	Boolean isLoading = false;

	Boolean isDONE = false;
	Boolean isSwipe = false;
	public static Boolean homeactv = true;

	LinearLayout emptyview;
	


		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
	        
	        helper=new Database_One(getActivity());
			utilhelper=new Util_Database(getActivity());
			UserID = utilhelper.getUSER_ID();
			
			

			list=(ListView)rootView.findViewById(R.id.show_posts);
			

			//LayoutInflater LInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			emptyview = (LinearLayout) inflater.inflate(R.layout.lv_set_empty, null, false);
 


			LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			mFooterView = (LinearLayout) mInflater.inflate(
					R.layout.loader_footer, null, false);
			

			CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);
			//CProgress.setVisibility(View.GONE);

			list.addFooterView(mFooterView);
			list.setFooterDividersEnabled(false);

			model=helper.getAll();
			getActivity().startManagingCursor(model);
			adapter=new PostDatabaseAdapter(model);
			list.setAdapter(adapter);

			list.setOnItemClickListener(onPostClick);
			list.setOnScrollListener(onscroll);
			
			
			// Set a listener to be invoked when the list should be refreshed.
			((PullAndLoadListView) list)
					.setOnRefreshListener(new OnRefreshListener() {

						public void onRefresh() {
							// Do work to refresh the list here.
							Get_Post("refresh");
						}
					});


			if(isDONE==false)
			{ 
				Get_Post("refresh");
				isDONE=true;
			}
			

			
			
			mUiHandler = new Handler() // Receive messages from service class
			{
				public void handleMessage(Message msg)
				{
					switch(msg.what)
					{
					case 0:
					{
						model.requery();
					}
					break;

					case 1:
					{
						Get_Post("refresh");
					}
					break;

					case 2:
					{
						String coll;
						String frag[];

						coll = (String)msg.obj;
						frag = coll.trim().split(":");

						helper.updateRate(frag[0],frag[1],frag[2]);
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

						helper.updateComment(frag[0],frag[1]);
						model.requery();

					}
					break;

					default:
						break;
					}
				}
			};
	       
	        return rootView;
	    }
		
		OnClickListener clickListener = new OnClickListener() {
		    @Override
		    public void onClick(final View v) {
		        switch(v.getId()) {
	               case R.id.row_popup:
	                	Pop_up(v);
	                  break;
	                case R.id.rate_clk:
	                	OpenRateDlg(v);
	                  break;
	                case R.id.comment_clk:
	                	OpenCommentDlg(v);
		                  break;
	                case R.id.share_clk:
	                	Share(v);
		                  break;
	                default:
	                  break;
		        }
		    }
		};
		
	

		
		public void Get_Post(String header)
		{
			mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

		


		private OnScrollListener onscroll = new OnScrollListener() {

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
								Get_Post("get_post");
							}

						}
					}
				}
				else
				{
					if(isDONE==false)
					{ 
						Get_Post("refresh");
						isDONE=true;
					}
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

					
						
						if(helper.getType(model).equals("YES"))
						{

							if(helper.get_isExpanded(model).equals("NO"))
							{
								PostDatabaseHolder holder = (PostDatabaseHolder)view.getTag();
								holder.pimage.setAdjustViewBounds(true);
								helper.update_isExpanded(helper.getPID(model), "'"+"YES"+"'");
							}
							else
							{
								PostDatabaseHolder holder = (PostDatabaseHolder)view.getTag();
								holder.pimage.setAdjustViewBounds(false);
								holder.pimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
								helper.update_isExpanded(helper.getPID(model), "'"+"NO"+"'");
							}
							
						}
						



				}
			};

			
			void Gomyprof()
			{

				Intent MyProfintent = new Intent(getActivity(), MyProfileActivity.class);
				startActivity(MyProfintent);

			} 

			
			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
			public void Pop_up(View v)
			{
				final int position = list.getPositionForView(v);
				if (position != ListView.INVALID_POSITION) {
					model.moveToPosition(position-1);

					postID = helper.getPID(model);

					if(helper.getUID(model).equals(UserID))
					{
						PopupMenu popupMenu = new PopupMenu(getActivity(), v );				
						popupMenu.setOnMenuItemClickListener(onMenuClick);				
						popupMenu.inflate(R.menu.row_mypopup_menu);				
						popupMenu.show();
					}
					else
					{
						PopupMenu popupMenu = new PopupMenu(getActivity(),v );				
						popupMenu.setOnMenuItemClickListener(onMenuClick);				
						popupMenu.inflate(R.menu.row_popup_menu);				
						popupMenu.show();	
					}

				  }
				
			}


		OnMenuItemClickListener onMenuClick = new OnMenuItemClickListener() {
			
	        @Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {

				case R.id.row_popup_delete:
				{
					Delete_post(postID);				
				}
				return true;

				case R.id.row_popup_report:
				{
					Toast.makeText(getActivity(), "Report = "+postID, Toast.LENGTH_SHORT).show();
				}
				return true;

				case R.id.row_popup_cancel:
				{

				}
				return true;
				
				case R.id.i_addon_popup_update:			
				{
	              //ShowTost("yeayea!!");
				}
				return true;
				
				case R.id.i_addon_popup_delete:			
				{
	   
		            Uri packageUri = Uri.parse("package:"+addon_pkg);
		            Intent uninstallIntent =
		            new Intent(Intent.ACTION_DELETE , packageUri);
		            startActivity(uninstallIntent);
					
				}
				return true;
				
				case R.id.i_addon_popup_cancel:			
				{

				}
				return true;
				
				
				case R.id.addon_popup_install:			
				{

				}
				return true;
				
				case R.id.addon_popup_cancel:			
				{

				}
				return true;
		

		
				default:		
					return false;


				}
			}

		};
			


			public void Go_Profile(String usr) {
				Intent intent = new Intent(getActivity(), ProfileActivity.class);
				intent.putExtra(USR_DETAIL, usr);
				startActivity(intent);

			}  



			public void ClickName(View v) {

				final int position = list.getPositionForView(v);
				if (position != ListView.INVALID_POSITION) {
					model.moveToPosition(position-1);
					String Name = helper.getName(model);
					if(Name.equals(utilhelper.getUSER()))
					{

						Gomyprof();

					}else{

						Go_Profile(Name);

					}

				}	


			}	 




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

					list.removeFooterView(emptyview);
					CProgress.setVisibility(View.VISIBLE);

					//mReload.setVisibility(View.GONE);
					//mReload_prog.setVisibility(View.VISIBLE);

					//setProgressBarIndeterminateVisibility(true);


				}


				protected void onPostExecute(Double result){

					((PullAndLoadListView) list).onRefreshComplete();
					
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

							    /*JSONArray edata = jsonObj.getJSONArray("extra");
	                            JSONObject e = edata.getJSONObject(0);
	                            String postloc = e.getString("postloc");
	                            utilhelper.ClearPostLoc();
	                            utilhelper.insertPostLoc(postloc);*/

									data = jsonObj.getJSONArray("posts");

									if(data.length()>0)
									{
										if(jsonObj.isNull("extra")==false)
										{
											helper.Clear();
											/*utilhelper.ClearPostLoc();
											utilhelper.insertPostLoc(data.length());

											//ShowTost(String.valueOf(data.length()));
										}
										else
										{
											int Incr = utilhelper.getPostLoc()+data.length();
											utilhelper.ClearPostLoc();
											utilhelper.insertPostLoc(Incr);*/
										}


										//ShowTost(String.valueOf(Incr));


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
											String isLike = c.getString("isLike");
											int likecount = c.getInt("likecount");
											int commentcount = c.getInt("commentcount");


		
											if(IMG.compareTo("NO")==0)
											{

												helper.insert(pid,userid,usrname,body,null,stamp,"NO",
														isLike,String.valueOf(likecount),String.valueOf(commentcount));            			
											} 
											else
											{
												byte[] imgdata = Base64.decode(pimg, 0);
												helper.insert(pid,userid,usrname,body,imgdata,stamp,"YES",
														isLike,String.valueOf(likecount),String.valueOf(commentcount));
											}
										}

									}

									else if(data.length()<1)
									{
										if(jsonObj.isNull("extra")==false)
										{
											//SetEmpty();
										}
									}

								}
								model.requery();

							} catch (JSONException e){

							}


						}


						//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
						//confirmFireMissiles();
					}

					//isActive.set(false);

					//dismiss_pbar();
					isLoading = false;
					CProgress.setVisibility(View.GONE);

					//mReload.setVisibility(View.VISIBLE);
					//mReload_prog.setVisibility(View.GONE);

					//setProgressBarIndeterminateVisibility(false);


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


				protected void onProgressUpdate(Integer... progress){
					//pb.setProgress(progress[0]);
					//pBar.setProgress(5);

					//setProgress(200);



				}

				protected void postData(String Header) {



					String url = LoginActivity.SERVER+"add_get_post.php"; 
					String query=null;

					try {

						if(Header.compareTo("refresh")==0)
						{

							query = String.format("PacketHead=%s&UID=%s", 
									URLEncoder.encode(Header, "UTF-8"), 
									URLEncoder.encode(UserID, "UTF-8"));
						}

						else
						{


							query = String.format("PacketHead=%s&UID=%s&PostLoc=%s", 
									URLEncoder.encode(Header, "UTF-8"), 
									URLEncoder.encode(UserID, "UTF-8"),
									URLEncoder.encode(String.valueOf(helper.getLastPost()), "UTF-8"));
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



    



			public void Delete_post(final String PostID)
			{
				mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

				if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
				{



					new AsyncTask<String, Integer, Double>() {
						@Override
						protected void onPreExecute() {
							super.onPreExecute();

							pDialog = new ProgressDialog(getActivity());
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
												Toast.makeText(getActivity().getApplicationContext(),"Post deleted" , Toast.LENGTH_LONG).show();
												helper.Delete(PostID);
												model.requery();

											}

											else
											{
												pDialog.dismiss();
												Toast.makeText(getActivity().getApplicationContext(),"Post could not be deleted" , Toast.LENGTH_LONG).show();
											}

										}
									} catch (JSONException e){

									} 



								}
							}		


							pDialog.dismiss();
							//Toast.makeText(getApplicationContext(),dresponse , Toast.LENGTH_LONG).show();							


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




			public void OpenRateDlg(View v)
			{
				final int position = list.getPositionForView(v);
				if (position != ListView.INVALID_POSITION) {
					model.moveToPosition(position-1);
					String pid = helper.getPID(model);

					Intent intent = new Intent(getActivity(), RatingActivity.class);
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
					String pid = helper.getPID(model);

					Intent intent = new Intent(getActivity(), CommentActivity.class);
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
					sharepid = helper.getPID(model);
					sharebody = helper.getBody(model);
					//sharerating = helper.getRating(model);			
					shareimgdata = helper.getBmp(model);

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
					builder.setView(getActivity().getLayoutInflater().inflate(R.layout.share_post_alert, null))
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
				newFragment.show(getActivity().getFragmentManager(), "Share");
			}


			public void Share_post(final String Header){

				mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

				if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
				{


					new AsyncTask<String, Integer, Double>() {
						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							/*pDialog = new ProgressDialog(HomeActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();	*/
							//setProgressBarIndeterminateVisibility(true);
							//mReload.setVisibility(View.GONE);
							//mReload_prog.setVisibility(View.VISIBLE);

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

								Toast.makeText(getActivity().getApplicationContext(),"Post could not be shared" , Toast.LENGTH_LONG).show();

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

											Toast.makeText(getActivity().getApplicationContext(),"Post shared" , Toast.LENGTH_LONG).show();	
											//if(isLoading==false)
											//{ 
											Get_Post("refresh");
											//}    

										}

										else
										{
											Toast.makeText(getActivity().getApplicationContext(),"Post could not be shared" , Toast.LENGTH_LONG).show();
										}

									}




								} catch (JSONException e){

								}

							}



							//pDialog.dismiss();
							//setProgressBarIndeterminateVisibility(false);
							//mReload.setVisibility(View.VISIBLE);
							//mReload_prog.setVisibility(View.GONE);



						}

					}.execute();

				}
				else
				{
					//pDialog.dismiss();	
					/*if(ErrorDlg == true);
				ShowNoNetErrorDialoag();*/
					//setProgressBarIndeterminateVisibility(false);
					//mReload.setVisibility(View.VISIBLE);
					//mReload_prog.setVisibility(View.GONE);

				}	

			} 

			
			



			@SuppressLint("ValidFragment")
			public class InitNetworkErrorDialogFragment extends DialogFragment {
				@Override
				public Dialog onCreateDialog(Bundle savedInstanceState) {
					// Use the Builder class for convenient dialog construction
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					//builder.setMessage(LoginActivity.neterrormsg)
					builder.setView(getActivity().getLayoutInflater().inflate(R.layout.network_error_alert, null))
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Get_Post("refresh");
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
				newFragment.show(getActivity().getFragmentManager(), "neterror");
			}




			@SuppressLint("ValidFragment")
			public class NoNetErrorDialogFragment extends DialogFragment {
				@Override
				public Dialog onCreateDialog(Bundle savedInstanceState) {
					// Use the Builder class for convenient dialog construction
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					//builder.setMessage(LoginActivity.noneterrormsg)
					builder.setView(getActivity().getLayoutInflater().inflate(R.layout.network_error_alert, null))
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if(isLoading==false)
							{ 
								Get_Post("get_post"); 
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
				newFragment.show(getActivity().getFragmentManager(), "noneterror");
			}






			class PostDatabaseAdapter extends CursorAdapter {

				@SuppressWarnings("deprecation")
				PostDatabaseAdapter(Cursor c) {
					super(getActivity(),c);
				}

				@Override
				public void bindView(View row, Context ctxt,Cursor c) {
					PostDatabaseHolder holder=(PostDatabaseHolder)row.getTag();
					holder.populateFrom(c, helper, utilhelper);
					/*if(lvboolaray[c.getPosition()] == false)
					{		    	
						holder.post_body.setVisibility(View.GONE);
					}
					else
					{
						holder.post_body.setVisibility(View.VISIBLE);
					}*/
					if(helper.getBody(c).length()!=0)
					{
						holder.post_body.setVisibility(View.VISIBLE);
					}
					else
					{
						holder.post_body.setVisibility(View.GONE);
					}
					
					
					
				if(helper.getType(c).equals("YES"))
				{
					holder.pimage.setVisibility(View.VISIBLE);
					
					if(helper.get_isExpanded(c).equals("NO"))
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
					LayoutInflater inflater=getActivity().getLayoutInflater();
					View row=inflater.inflate(R.layout.main_row, parent, false);
					PostDatabaseHolder holder=new PostDatabaseHolder(row);
					row.setTag(holder);     
					return(row);              
				}

			}    





	 class PostDatabaseHolder {
				 private TextView uname=null;
				 private TextView post_body=null;
				 private TextView date_time=null;
				 private NetworkImageView image=null;
				 private ImageView pimage=null;
				 private RatingBar RateBar;
				 Button ratebtn;
				 Button sharebtn;
				 Button commentbtn;
				 ImageButton rowpopup;
				 ImageLoader imageLoader = AppController.getInstance().getImageLoader();
				 Util_Database utilhelper2;
				 PostDatabaseHolder(View row) {

					uname=(TextView)row.findViewById(R.id.row_username);
					post_body=(TextView)row.findViewById(R.id.row_body);
					date_time=(TextView)row.findViewById(R.id.row_date_time);
					image=(NetworkImageView)row.findViewById(R.id.row_image);
					pimage=(ImageView)row.findViewById(R.id.row_postimage);
					RateBar=(RatingBar)row.findViewById(R.id.row_ratingbar);
					ratebtn=(Button)row.findViewById(R.id.rate_clk);
					sharebtn=(Button)row.findViewById(R.id.share_clk);
					commentbtn=(Button)row.findViewById(R.id.comment_clk);
					rowpopup=(ImageButton)row.findViewById(R.id.row_popup);
					//image.setErrorImageResId(R.drawable.me);
					image.setDefaultImageResId(R.drawable.me);
					
					ratebtn.setOnClickListener(clickListener);
					commentbtn.setOnClickListener(clickListener);
					sharebtn.setOnClickListener(clickListener);
					rowpopup.setOnClickListener(clickListener);

					
				}


				void populateFrom(Cursor c, Database_One helper, Util_Database utilhelper) {

					if (imageLoader == null)
						imageLoader = AppController.getInstance().getImageLoader();

					image.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getUID(c).toString()+".png", imageLoader);
					

	 
					/*Pattern tagMatcher = Pattern.compile("[#]+[A-Za-z0-9-_]+\\b");
		            String newActivityURL = "HashTag://";
		            post_body.setText(helper.getBody(c));
		            Linkify.addLinks(post_body, tagMatcher, newActivityURL);


		        	Pattern tagMatcher1 = Pattern.compile("[@]+[A-Za-z0-9-_]+\\b");	
		            String newActivityURL1 = "profile://";
		            post_body.setText(helper.getBody(c));   
		            Linkify.addLinks(post_body, tagMatcher1, newActivityURL1);	*/

					
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

					

					uname.setText("@"+helper.getName(c));						
					if(helper.getName(c).equals(utilhelper.getUSER()))
					{
						Linkify.addLinks(uname, atMentionPattern, "myprofile://", null, transformFilter); 
					}
					else
					{
						Linkify.addLinks(uname, atMentionPattern, atMentionScheme, null, transformFilter); 
					}
					
					stripUnderlines(uname);
					
					

					date_time.setText(helper.getDate(c));

					if(helper.getBody(c).length()<1)
					{
						post_body.setText(null);
						/*post_body.setLayoutParams(new LinearLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));*/
						//post_body.setVisibility(View.GONE);
					}			
					else
					{
					
						post_body.setText(helper.getBody(c));

						Linkify.addLinks(post_body, Linkify.ALL);
						Linkify.addLinks(post_body, atMentionPattern, atMentionScheme, null, transformFilter); 
					 	Linkify.addLinks(post_body, HashPattern, HashScheme, null, transformFilter);
						//stripUnderlines(post_body);
						
						
					}	



					if(helper.getType(c).compareTo("NO")==0)
					{
						pimage.setImageBitmap(null);
						/*pimage.setLayoutParams(new LinearLayout.LayoutParams(
					    		ViewGroup.LayoutParams.WRAP_CONTENT,
					    		ViewGroup.LayoutParams.WRAP_CONTENT));*/
					}

					else //if(helper.getType(c).compareTo("Post")==0)
					{

						pimage.setImageBitmap(BitmapFactory.decodeByteArray(helper.getBmp(c), 0, helper.getBmp(c).length));
						//pimage.setLayoutParams(new LinearLayout.LayoutParams(90,90));	
						/*pimage.setLayoutParams(new LinearLayout.LayoutParams(
					    		ViewGroup.LayoutParams.WRAP_CONTENT,
					    		ViewGroup.LayoutParams.WRAP_CONTENT));*/
					}

					

					//RateBar.setRating(Float.parseFloat(helper.getRating(c)));

					//ratebtn.setVisibility(View.VISIBLE);
					//sharebtn.setVisibility(View.VISIBLE);
					ratebtn.setText("("+helper.getLikeCount(c)+")");
					commentbtn.setText("("+helper.getCommentCount(c)+")");

				}
			}   

	

	public static boolean checkNetworkConnection(Context context)
	{
		final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi =connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		final android.net.NetworkInfo mobile =connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if(wifi.isAvailable()||mobile.isAvailable())
			return true;
		else
			return false;
	}    


	
	

	@SuppressLint("ValidFragment")
	public class DeletePostErrorDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			//builder.setMessage(LoginActivity.neterrormsg)
			builder.setView(getActivity().getLayoutInflater().inflate(R.layout.network_error_alert, null))
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
		newFragment.show(getActivity().getFragmentManager(), "logouterror");
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