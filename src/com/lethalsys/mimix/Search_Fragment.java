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

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;

public class Search_Fragment extends Fragment  implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{
	 
	private SearchView mSearchView;
	private ListView search_listview;
	private LinearLayout emptyview;
	private ProgressBar CProgress;
	private static final String BUCKET = "members-network";
	
	private String UserID;
	Cursor model=null;
	private String iresponse;
	Database_Two helper=null;
	SearchDatabaseAdapter adapter =null;
	static Util_Database utilhelper;


	
		@SuppressWarnings("deprecation")
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
				 
	        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
	        
			utilhelper=new Util_Database(getActivity());
			UserID = utilhelper.getUSER_ID();
		   
			helper=new Database_Two(getActivity());
		
			mSearchView = (SearchView)rootView.findViewById(R.id.mainmenu_search_editor);
			setupSearchView();
        	
			search_listview = (ListView)rootView.findViewById(R.id.mainmenu_search_listview);
		    LayoutInflater LInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			emptyview = (LinearLayout) LInflater.inflate(
						R.layout.lv_set_empty, null, false);
			   
			   
		    LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		    LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
						R.layout.loader_footer, null, false);
				
		    CProgress  = (ProgressBar)mFooterView.findViewById(R.id.row_loading);
				 
			
		    search_listview.addFooterView(mFooterView);

			
			
			model=helper.getAllSearch();
			getActivity().startManagingCursor(model);
			adapter=new SearchDatabaseAdapter(model);
			search_listview.setAdapter(adapter);
			//search_listview.setOnItemClickListener(onPostClick);
			
			((LoadMoreListView) search_listview)
			.setOnLoadMoreListener(new OnLoadMoreListener() {
				public void onLoadMore() {
					
				}
			});
					
	         
	        return rootView;
	    }
		
		
		
		
		public void onResume()
		{
			super.onResume();
			
		}
		
		
		@Override
		public void onPause() {
		super.onPause();
		
		}
		
		
	    private void setupSearchView() {
	        mSearchView.setIconifiedByDefault(true);	        
	        mSearchView.setOnQueryTextListener(this);
	        mSearchView.setOnCloseListener(this);
	        mSearchView.setSubmitButtonEnabled(false);
	        //mSearchView.setQueryHint("Search Here");
	        mSearchView.setIconified(false);
	        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
	        TextView textView = (TextView) mSearchView.findViewById(id);
	        textView.setTextColor(Color.WHITE);
	        

	    }
	 
	    public boolean onQueryTextChange(String newText) {
	    	helper.ClearSearch();
	        if (TextUtils.isEmpty(newText)) {
	        	search_listview.setVisibility(View.GONE);
	        } else {
	        	search_listview.setVisibility(View.VISIBLE);
	        	search_listview.removeHeaderView(emptyview);
	        	new MyAsyncTask().execute(newText);

	        }
	        return true;
	    }
	 
	    public boolean onQueryTextSubmit(String query) {
	        return false;
	    }

	
	    public boolean onClose() {
	    	getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
	    	return false;
	    }

			public void hide_search(View v)
			{

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
						    helper.ClearSearch();
						    
							try{
								
								JSONObject jsonObj = new JSONObject(iresponse.trim());               	
								
								if(jsonObj.isNull("user_result")==false)
								{
									JSONArray data = jsonObj.getJSONArray("user_result");
									
									for(int i=0;i<data.length();i++)
									{
										JSONObject d = data.getJSONObject(i);
										String userid = d.getString("userid");
										String user = d.getString("username");
										String location = d.getString("location");
										String workplace = d.getString("workplace");
										String detail;
										
										if(workplace.length()!=0)
										{
										   detail = workplace;
										}
										else
										{
										   detail = location;
										}

										helper.insertSearch(userid, user, detail);						
									}
								}
				                else
				                {
				                		
				                }
				  
									
								if(jsonObj.isNull("hash_result")==false)
								{
										
										JSONArray data2 = jsonObj.getJSONArray("hash_result");
										
										
										
										for(int i=0;i<data2.length();i++)
										{
										JSONObject t = data2.getJSONObject(i);
										String hash = t.getString("hash");
										String count = t.getString("count");
										String detail = count+" people are talking about this";
										
										helper.insertSearch(null, hash, detail);	

										}

								}
			                	else
			                	{
			                		
			                    }
			                        	
								model.requery();

							}catch (JSONException e){

							} 

					 
							//Toast.makeText(getActivity(),iresponse.trim() , Toast.LENGTH_LONG).show();
						    // confirmFireMissiles();						
					}

		    		}

		    		CProgress.setVisibility(View.GONE);
		    		
		    		
		    		if(adapter.isEmpty())
		    		{
		    			search_listview.removeHeaderView(emptyview);
		    			search_listview.addHeaderView(emptyview);
		    		}
		    		else
		    		{
		    			search_listview.removeHeaderView(emptyview);
		    		}
		    				    		
				}


				protected void onProgressUpdate(Integer... progress){
				}

				protected void postData(String Query) {
					// Create a new HttpClient and Post Header
					HttpParams httpparameters = new BasicHttpParams();
				    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
				    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
				    
					DefaultHttpClient httpclient = new DefaultHttpClient();
					httpclient.setParams(httpparameters);
					
					HttpPost httppost;
					httppost = new HttpPost(LoginActivity.SERVER+"search.php");
					String Header = "search";
					
					try {
						// Add your data

						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
						nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
						nameValuePairs.add(new BasicNameValuePair("query",Query ));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						ResponseHandler<String> responseHandler = new BasicResponseHandler();


						//HttpResponse response;


						// Execute HTTP Post Request

						iresponse = httpclient.execute(httppost, responseHandler);





					} catch (IOException e) {
						//if(ErrorDlg == true)
						//ShowErrorDialoag();
					} 
					


				}

			}



			
			
			
			
		    class SearchDatabaseAdapter extends CursorAdapter {


		    SearchDatabaseAdapter(Cursor c) {
			super(getActivity(),c);
			}
			@Override
			public void bindView(View row, Context ctxt,Cursor c) {
			searchDatabaseHolder holder=(searchDatabaseHolder)row.getTag();
			holder.populateFrom(c, helper);
			
			
			 if(helper.getSearchUID(helper.getSearchTitle(c)) != null) {
				holder.image.setVisibility(View.VISIBLE);
			 }			  
			 else
			 { 
				holder.image.setVisibility(View.GONE);
			 }

			}
			
			
			@Override
			public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.search_row, parent, false);
			searchDatabaseHolder holder=new searchDatabaseHolder(row);
			row.setTag(holder);		
			return(row);
			}
		
		}    





	static class searchDatabaseHolder {
		private TextView title=null;
		private TextView detail=null;
		private NetworkImageView image=null;
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		searchDatabaseHolder(View row) {
		
	    detail=(TextView)row.findViewById(R.id.result_detail);
	    title=(TextView)row.findViewById(R.id.result_title);
	    image=(NetworkImageView)row.findViewById(R.id.result_image);
	    
	    image.setDefaultImageResId(R.drawable.me);
	    
		}
		
		
		void populateFrom(Cursor c, Database_Two helper) {
			detail.setText(helper.getSearchDetail(c));
			
			Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
			String atMentionScheme = "profile://";

			Pattern hashPattern = Pattern.compile("#([A-Za-z0-9_]+)");
			String hashScheme = "hashtag://";

			TransformFilter transformFilter = new TransformFilter() {
				//skip the first character to filter out '@'
				public String transformUrl(final Matcher match, String url) {
					return match.group(1);
				}
			};

			
			title.setText(helper.getSearchTitle(c));
			if(helper.getSearchTitle(c).contains("@"))
		    {
			   if(helper.getSearchTitle(c).equals(utilhelper.getUSER()))
			   {
			    Linkify.addLinks(title, atMentionPattern, "myprofile://", null, transformFilter); 
			   }
			   else
			   {
			     Linkify.addLinks(title, atMentionPattern, atMentionScheme, null, transformFilter); 
			   }
			   
		    }
			else
			{
			     Linkify.addLinks(title, hashPattern, hashScheme, null, transformFilter); 
			}
			
		    stripUnderlines(title);
		    
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();

		    
			if(helper.getSearchUID(helper.getSearchTitle(c)) != null)
		    {
				image.setImageUrl(LaunchActivity.SERVER+"profile/image/"+"img_"+helper.getSearchUID(c).toString()+".png", imageLoader);
		    }
	    	 
		   
			
		    
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