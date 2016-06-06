package com.lethalsys.mimix;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddPhoneContactActivity extends Activity {

	 public static ConnectivityManager mConnectivityManager;
	 public static NetworkInfo mNetworkInfo;

	 Util_Database utilhelper;
	 private String UserID;
	
	 Boolean ErrorDlg = true;
	
	 ProgressDialog pDialog;
	 
     Cursor model=null;
	 Database_One helper=null;
	 AdPhoneListAdapter adapter;
	 String contactx;
	 String usernamex;
	 String iresponse;
	 String iresponse2;
	 
	 JSONArray data = null;
	 
	 ListView list;
		
	 // private LinearLayout Loader;
	 private LinearLayout addphnbuttonwrapper;
	 
     
 	
 	public ProgressBar CProgress;
 	public TextView CProgresstxt;
 	 	
 	LinearLayout emptyview;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_phone_contact);		
		
		getActionBar().setTitle("");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		utilhelper=new Util_Database(this);
		UserID = utilhelper.getUSER_ID();	 
		
		helper=new Database_One(this);
		
		
		list=(ListView)findViewById(R.id.add_phn_list);
		
		//Loader =(LinearLayout)findViewById(R.id.add_phn_loader);
		//Loader.setVisibility(View.GONE);
		
		  LayoutInflater LInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		   emptyview = (LinearLayout) LInflater.inflate(
					R.layout.lv_set_empty, null, false);
			
		
	       LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
					R.layout.addphn_view, null, false);
			
			CProgress  = (ProgressBar) mFooterView.findViewById(R.id.addphn_loading);
			 
			CProgresstxt  = (TextView) mFooterView.findViewById(R.id.addphn_loading_txt);
			
			addphnbuttonwrapper =(LinearLayout)mFooterView.findViewById(R.id.addphnbuttonwrapper);
			        
			list.addFooterView(mFooterView);
			
			
		
		helper.ClearAddPhn();
		
		model=helper.getAllAddPhn();
		startManagingCursor(model);
		adapter=new AdPhoneListAdapter(model);
		list.setAdapter(adapter);
		

   
		contactx = Get_Contacts().toString();
		if(contactx.length()>0)
		{
		get_phn_contacts();
		}
       
	 
		

		
	}
	
	
	
	public JSONObject Get_Contacts()
	{
		JSONArray arr = new JSONArray();
	    
	    JSONObject obj2 = new JSONObject();

		ContentResolver cr = getContentResolver();
	    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
	    if (cur.getCount() > 0) {
	    while (cur.moveToNext()) {
	    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
	    //Query phone here.  Covered next
	    	
	    	
	    		    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
	    			ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"= ?",new String[]{id}, null);
	    			while (pCur.moveToNext()) {
	    			// Do something with phones
	    				String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));

	    				//ShowTost(name);
	    				
	    					//r.setPhone(phone);
	    					//r.setName(name);
	    				
	    				 try{
	    				    	
	    					      JSONObject obj = new JSONObject();
	    					      obj.put("contact",name);
	    					      obj.put("phone",phone); 
	    					      arr.put(obj); 
	    					      obj2.put("contacts",arr);
	    					      obj2.put("uid",UserID);
	    					      obj2.put("header","get_phn");
	    					    
	    					    }catch (JSONException e){
	    					    	 
	    					    }
	    					
	    				
	    				
	    				   
	    			}
	    			pCur.close();
	}
	}
	}
	    
	
	    return obj2;
	}
	
	
	
	
	public JSONObject Add_Contacts()
	{
		Cursor pmodel = helper.getAllSelectedAddPhn();
		JSONArray arr = new JSONArray();	    
	    JSONObject obj2 = new JSONObject();
	    

		while(pmodel.moveToNext())
		{
			
			 try{
			    	
			      JSONObject obj = new JSONObject();
			      obj.put("username",pmodel.getString(1));		      
			      arr.put(obj); 
			      obj2.put("contacts",arr);
			      obj2.put("uid",UserID);
			      obj2.put("header","add_phn");
			    
			    }catch (JSONException e){
			    	 
			    }
		}
		
		// ShowTost(obj2.toString());
		return obj2;
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.add_phone_contact, menu);
		return true;
	}
	
	

	public void get_phn_contacts()
	{
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
       new AsyncTask<String, Integer, Double>() {
    	
	   @Override
	   protected void onPreExecute() {
		 super.onPreExecute();
		
	   /*pDialog = new ProgressDialog(HomeActivity.this);
		pDialog.setMessage("loging out...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();*/
	    //setProgressBarVisibility(true);
		//Loader.setVisibility(View.VISIBLE);
		 list.removeFooterView(emptyview);
			CProgress.setVisibility(View.VISIBLE);
			CProgresstxt.setVisibility(View.VISIBLE);

	}
	
	@Override
	protected Double doInBackground(String... params) {
	
    
		String url = LoginActivity.SERVER+"get_add_phone_contacts.php"; 
		
		String query=contactx;

		try {
			     /*query = String.format("PacketHead=%s&UID=%s&cat=%s", 
			     URLEncoder.encode(Header, "UTF-8"),
			     URLEncoder.encode(UserID, "UTF-8"),
			     URLEncoder.encode(cat.trim(), "UTF-8"));*/
		
		
	    URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true); // Triggers POST.
		//connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(30000);
		//connection.setDoInput(true);
		java.io.OutputStream out=connection.getOutputStream();
		out.write(query.getBytes("UTF-8"));
		
		InputStream response = connection.getInputStream();
		
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
		
		}catch (IOException e) {
			if(ErrorDlg == true)
			ShowNoNetErrorDialoag();
		} 
		
   
	return null;
	}
	
	
	
   	protected void onProgressUpdate(Integer... progress){

   	}

	
	@SuppressWarnings("deprecation")
	protected void onPostExecute(Double result)
	{
	
		if(null != iresponse)
		{
			if(iresponse.trim().length()!=0)
			{
			
			
	  try{
		  
			JSONObject jsonObj = new JSONObject(iresponse.trim());      	
        	
        	if(jsonObj.isNull("matches")==false)
        	{
        	
        	data = jsonObj.getJSONArray("matches");
        		
        	if(data.length()>0)
        	{  
        	
        	helper.ClearAddPhn();
			
        	
            for(int i=0;i<data.length();i++)
            {
            	JSONObject c = data.getJSONObject(i);
            	String username = c.getString("username");
            	String contanct = c.getString("contact");
            	String phone = c.getString("phone");
            	String face = c.getString("face");
            
         	
            	    if(face.length()>0)
            	    {
                    	byte[] imgdata;
						
                    	try {
                    		
					    imgdata = Base64.decode(face, 0);    	
            		    helper.insertAddPhn(contanct,phone,username,imgdata,"N");  
            		    
                    	} catch (IOException e) {
							e.printStackTrace();
						}
                    	
            	    } 
                	else
                	{
                    	
                		helper.insertAddPhn(contanct,phone,username,null,"N"); 
                		
                	}
               	
            
            }
            addphnbuttonwrapper.setVisibility(View.VISIBLE);
			}
        	
        	}
    
            model.requery();
			
		 } catch (JSONException e){
         	
         }
			
		}
		
		             
		}
		//ShowErrorDialoag();
		//ShowTost(iresponse);
		//confirmFireMissiles();
		//Loader.setVisibility(View.GONE);
		CProgress.setVisibility(View.GONE);
		CProgresstxt.setVisibility(View.GONE);
		
		
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
			if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
        }	
	    
	    
	}
	
	


	public void add_phn_contacts(View v)
	{

		usernamex=Add_Contacts().toString();
		if(usernamex.length()>0)
		{
		
		
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
       new AsyncTask<String, Integer, Double>() {
    	
	   @Override
	   protected void onPreExecute() {
		 super.onPreExecute();
		
	    pDialog = new ProgressDialog(AddPhoneContactActivity.this);
		pDialog.setMessage("Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}
	
	@Override
	protected Double doInBackground(String... params) {
	
    
		String url = LoginActivity.SERVER+"get_add_phone_contacts.php"; 
		
	    String query=usernamex;

		try {
	
		
	    URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true); // Triggers POST.
		//connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(30000);
		//connection.setDoInput(true);
		java.io.OutputStream out=connection.getOutputStream();
		out.write(query.getBytes("UTF-8"));
		
		InputStream response = connection.getInputStream();
		
		out.flush();
		out.close();

	
		
		iresponse2 = IOUtils.toString(response);
		
		}catch (IOException e) {
			if(ErrorDlg == true)
			ShowNoNetErrorDialoag();
		} 
		
   
	return null;
	}
	
	
	
   	protected void onProgressUpdate(Integer... progress){

   	}

	
	protected void onPostExecute(Double result)
	{
	
		if(null != iresponse2)
		{
			if(iresponse2.trim().length()!=0)
			{
			
			
	  try{
		  
			JSONObject jsonObj = new JSONObject(iresponse2.trim());      	
        	
        	if(jsonObj.isNull("data")==false)
        	{
        	
        	data = jsonObj.getJSONArray("data");
        		
        	if(data.length()>0)
        	{  
        	
				data = jsonObj.getJSONArray("data");
				JSONObject d = data.getJSONObject(0);
				String msg  = d.getString("msg");
	
         
			}
        	
        	}
        	
 
            
			
		 } catch (JSONException e){
         	
         }
			
		}
		
		             
		}
		//ShowErrorDialoag();
		//ShowTost(iresponse2);
		pDialog.dismiss();
		

		
		
	
	}
    
    }.execute();
    
        }
        else
        {
			if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
        }	
	    
		}
	}
    
	
	public class ContactX{	

		List<String> Phone = new ArrayList<String>();
		List<String> Name = new ArrayList<String>();


		public List<String> getPhone()
		{
			return(Phone);
		}		
		public void setPhone(String iPhone)
		{
			Phone.add(iPhone);
			
		}

		public List<String> getName()
		{
			return(Name);
		}			
		public void setName(String iName)
		{
			Name.add(iName);
		}


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
                	
                		//get_ads("get_ads_cat");
           			
                
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
    newFragment.show(getFragmentManager(), "noneterror");
}







class AdPhoneListAdapter extends CursorAdapter {

@SuppressWarnings("deprecation")
AdPhoneListAdapter(Cursor c) {
super(AddPhoneContactActivity.this,c);
}
@Override
public void bindView(View row, Context ctxt,Cursor c) {
AdPhoneListHolder holder=(AdPhoneListHolder)row.getTag();
holder.populateFrom(c, helper);

/*if(lvboolaray8[c.getPosition()] == false)
{		    	
	holder.addphnchk.setChecked(false);
}
else
{
	holder.addphnchk.setChecked(true);
}*/

if(helper.getAddPhnIsAdd(c).equals("Y")) {
	
	holder.addphnchk.setChecked(true);
	
	}

else if(helper.getAddPhnIsAdd(c).equals("N")){

	holder.addphnchk.setChecked(false);

   }

  
}
@Override
public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
LayoutInflater inflater=getLayoutInflater();
View row=inflater.inflate(R.layout.addphn_row, parent, false);
AdPhoneListHolder holder=new AdPhoneListHolder(row);
row.setTag(holder);
return(row);
}

}    





static class AdPhoneListHolder {
private ImageView  addphnimg=null;
private TextView addphncontact=null;
private TextView  addphnuser=null;
private CheckBox addphnchk=null;
//private View row=null;
AdPhoneListHolder(View row) {
//this.row=row;

 addphnimg=(ImageView)row.findViewById(R.id.addphnrow_img);
 addphncontact=(TextView)row.findViewById(R.id.addphnrow_contact);
 addphnuser=(TextView)row.findViewById(R.id.addphnrow_user);
 addphnchk=(CheckBox)row.findViewById(R.id.addphnrow_chk);


}


void populateFrom(Cursor c, Database_One helper) {
	
	addphncontact.setText(helper.getAddPhnContact(c));
	
    
    Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
    String atMentionScheme = "profile://";


    TransformFilter transformFilter = new TransformFilter() {
    //skip the first character to filter out '@'
    public String transformUrl(final Matcher match, String url) {
    return match.group(1);
    }
    };

    addphnuser.setText("@"+helper.getAddPhnUser(c));  

    Linkify.addLinks(addphnuser, Linkify.ALL);
    Linkify.addLinks(addphnuser, atMentionPattern, atMentionScheme, null, transformFilter); 

	
	
if(helper.getAddPhnImg(helper.getAddPhnUser(c))!=null)
{
	addphnimg.setImageBitmap(BitmapFactory.decodeByteArray(helper.getAddPhnImg(helper.getAddPhnUser(c)), 0, helper.getAddPhnImg(helper.getAddPhnUser(c)).length));
}
else
{
	addphnimg.setImageResource(R.drawable.me);//ic_dummy_img);
}



if(helper.getAddPhnIsAdd(c).equals("Y")) {

	
	addphnchk.setTag("Y");
}

else if(helper.getAddPhnIsAdd(c).equals("N")){


	addphnchk.setTag("N");
}



}
} 






public void Select_Phone_Contact(View v) {

	final int position = list.getPositionForView(v);
	if (position != ListView.INVALID_POSITION) {
		model.moveToPosition(position);
		String usr = helper.getAddPhnUser(model);

		CheckBox addphn_chk=(CheckBox)v.findViewById(R.id.addphnrow_chk);

		if(addphn_chk.getTag()=="Y")
		{
			helper.updateAddPhn(usr,"N");
			addphn_chk.setTag("N");
			//ShowTost(usr+" N");
		}
		else
		{
			helper.updateAddPhn(usr,"Y");
			addphn_chk.setTag("Y");
			//ShowTost(usr+" Y");
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
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
    
    
    
}
