package com.lethalsys.mimix;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class LoginActivity extends Activity implements OnClickListener{
	 
	public final static String EXTRA_MESSAGE = "com.lethalsys.mimix.MESSAGE";
	
    //public final static String SERVER = "http://10.0.2.2/mimix_project/";
    public final static String SERVER = "http://192.168.56.1/mimix_project/";
    //public final static String SERVER = "http://route.pixub.com/";
    
    public static final String SETTING_PREFERENCES = "SettingPrefs" ;
    
	private static String BG_PATH = "/data/data/com.lethalsys.mimix/backgrounds/";
	private static String BG_NAME = null;

		
	String GCM_PROJECT_NUMBER = "98391239133";
	
	public final static int conntimeout = 15000;
	public final static int socktimeout = 30000;
	
	//public final static String neterrormsg = "IOException... \nAn error occured while connecting to the internet.";
	//public final static String noneterrormsg = "IOException... \nAn error occured while connecting to the internet.";

	SharedPreferences sharedpreferences;
	
	private ProgressDialog pDialog;
	
	private EditText Usernamevalue;
	private EditText Passwordvalue;
	private Button btn;  
	//private ProgressBar pb;
	private String iresponse;
	//private String[] Data;
	//private ImageView  main_image;
	
	
	
	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	public GoogleCloudMessaging gcm;
	
	JSONArray data = null;
	String msg; 
	String userid;
   //String regid;
	String user_name;
	String pass_word;

	StringBox r;
	String URL;
	
	Boolean ErrorDlg = true;
	String regid;
    int tryagain=0;
    
	Util_Database utilhelper=null;
	String fresponse;
	InputStream Fresponse;
	JSONArray Fdata = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);
	
	//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.fadeactionbarbg));
	//getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
	sharedpreferences = getSharedPreferences(SETTING_PREFERENCES, Context.MODE_PRIVATE);

	
	utilhelper=new Util_Database(this);
		
/*	try {
		copyBackGrounds();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	
	 
	if(utilhelper.getUSER_ID()!=null)
	{
		
		Go_Home();
		ClosMe();		
		//Toast.makeText(this, "not_empty", Toast.LENGTH_LONG).show();
	}
	


	//setTitle("Log in");
	
	Usernamevalue=(EditText)findViewById(R.id.user_edttxt);
	Passwordvalue=(EditText)findViewById(R.id.pwd_edttxt);
	btn=(Button)findViewById(R.id.login_button);
	//pb=(ProgressBar)findViewById(R.id.progressBar);
	//pb.setVisibility(View.GONE);
	btn.setOnClickListener(this);
	
	/*btn.setOnTouchListener(new OnTouchListener () {
        @Override
        public boolean onTouch(View v,MotionEvent motionEvent) {
        int rawX = (int) motionEvent.getX();
        int rawY = (int) motionEvent.getY();
        
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
        TranslateAnimation anim = new TranslateAnimation(rawX, rawY, 0, 0);
        anim.setDuration(500L);
        btn.startAnimation(anim);
        }
        else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
        	
        	if(Usernamevalue.getText().toString().length()<1){
        		//Toast.makeText(this, "please enter username", Toast.LENGTH_LONG).show();
        	}
        	if(Passwordvalue.getText().toString().length()<1){
        		//Toast.makeText(this, "please enter password", Toast.LENGTH_LONG).show();
        	}else{
        		
        		pDialog = new ProgressDialog(LoginActivity.this);
        		pDialog.setMessage("Initializing...");
        		pDialog.setIndeterminate(false);
        		pDialog.setCancelable(false);
        		pDialog.show();
        		
        	 getRegId();
        	}
        }
         return true;
        }});*/
	
	//main_image=(ImageView)findViewById(R.id.main_img);	
	//main_image.setImageResource(R.drawable.ic_main);
	
	
	
	
	}
	
	
	public void onResume()
	{
		super.onResume();
		ErrorDlg = true;
	}
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}
	
	
    public void onClick(View v) {

    	if(Usernamevalue.getText().toString().length()<1){
    		Toast.makeText(this, "please enter username", Toast.LENGTH_LONG).show();
    	}
    	if(Passwordvalue.getText().toString().length()<1){
    		Toast.makeText(this, "please enter password", Toast.LENGTH_LONG).show();
    	}else{
    		
    		pDialog = new ProgressDialog(LoginActivity.this);
    		pDialog.setMessage("Initializing...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(false);
    		pDialog.show();
    		
    	 getRegId();
    	
    	}
    
     }
	
	
	public void AUTH() {
		

    	//pb.setVisibility(View.VISIBLE);
        
    	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

    	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
    	    {
            
            	 new MyAsyncTask().execute();
            }
            else
            {
            	//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
            	ShowNoNetErrorDialoag();
            }
         
    	
    	
    
		
	}
	
	
 
	

    
    public void Go_Home() {
    	Intent intent = new Intent(this, HomeActivity.class);
    	intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK|*/Intent.FLAG_ACTIVITY_CLEAR_TASK);
    	startActivity(intent);
    	}  
    

    public void getRegId(){
    	
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
	    	
    	
    new AsyncTask<String, Integer, Double>() {
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
    

	}
	
	@Override
	protected Double doInBackground(String... params) {
        
        /*try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
           
            regid = gcm.register(GCM_PROJECT_NUMBER);
            
            

        } catch (IOException ex) {
        	regid = "Error";

        }*/
		 
		regid = "APA91bFqnQzp0z5IpXWdth1lagGQZw1PTbdBAD13c-UQ0T76BBYVsFrY96MA4SFduBW9RzDguLaad-7l4QWluQcP6zSoX1HSUaAzQYSmI93....";
        
    
		return null;
	}
	
   	protected void onProgressUpdate(Integer... progress){

   	}

	
	protected void onPostExecute(Double result){
	if(regid.trim().length()!=0)
	{
		if(regid.equals("Error"))
		{
			if(tryagain==5)
			{
    	
			if(ErrorDlg == true)
			ShowNoNetErrorDialoag();
			tryagain=0;
			}
			else
	 		{
				getRegId();
				tryagain++;
			}
			
		}
		else
		{
			AUTH();
		}
		 
	}
	else
	{
		getRegId();
	}
	
	
	}
    
    }.execute();
    
        }
        else
        {
        	ShowNoNetErrorDialoag();
        }	
    
    }
    
   
    
    
    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
    	 
 
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Logging in...");

		}
    	
    	@Override
    	protected Double doInBackground(String... params) {
    	// TODO Auto-generated method stub
    	postData();
    	return null;
    	}
    	 
    	protected void onPostExecute(Double result){
    	//pb.setVisibility(View.GONE);
    	
    	//Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
    	//iresponse = Xml.parse(getResponseBody(response), new CustomXmlParser()); //response.getStatusLine().getReasonPhrase();response.toString();
    	if(null!=iresponse)
    	{
    		
        if(iresponse.trim().length()!=0)
        {

        	//Toast.makeText(getApplicationContext(), iresponse.trim(), Toast.LENGTH_LONG).show();
    	
    		try{
    			
    			
    			JSONObject jsonObj = new JSONObject(iresponse.trim()); 
    			

    			
    			
    			
    			if(jsonObj.isNull("data")==false)
    			{
    			
    			data = jsonObj.getJSONArray("data");
    			
    			JSONObject d = data.getJSONObject(0);
    		    msg = d.getString("msg");
    		     
    		    
            	if("LOGGED IN!".compareTo(msg)==0)
            	{
            		userid = d.getString("uid");
            		user_name = d.getString("user_name");
            		pass_word = d.getString("pass_word");
            		String _regid = d.getString("gcm_regid");
            		
            		utilhelper.ClearUSER();		
            	    utilhelper.insertUSER(userid,_regid,user_name,pass_word);
            	    
            	    //Toast.makeText(getApplicationContext(), utilhelper.getUSER_GCM_ID(), Toast.LENGTH_LONG).show();
            	    
            	    pDialog.setMessage("login successful");
            	    //pDialog.dismiss();
            	    
            	    Get_Faces();
            		
            	}
            	else
            	{
            		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            		pDialog.dismiss();
            	}

    			}
    		    
    		} catch (JSONException e){
    			
    		} 
    	
    		
        	
            }
    	}
    	
    	}
    	
    	
    	protected void onProgressUpdate(Integer... progress){
    	//pb.setProgress(progress[0]);
    	}

    	protected void postData() {
    		
    	//URL = SERVER+"loginA.php";	
    	// Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);    	
        
        HttpPost httppost = new HttpPost(SERVER+"loginA.php");
    	
    	
    	//HttpPost httppost = new HttpPost(URL);
    	//String Header = "REG";
    	 
    	try {
    		
    	
    	// Add your data
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	//nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
    	nameValuePairs.add(new BasicNameValuePair("username", Usernamevalue.getText().toString()));
    	nameValuePairs.add(new BasicNameValuePair("password", Passwordvalue.getText().toString()));
    	nameValuePairs.add(new BasicNameValuePair("gcm_regid", regid));
    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	 

    	//HttpResponse response;

    	
    	// Execute HTTP Post Request
    	 iresponse = httpclient.execute(httppost, responseHandler);
    	 

    	  
    	 
    	} catch (ClientProtocolException e) {
    	// TODO Auto-generated catch block
		
    	} catch (IOException e) {
    	// TODO Auto-generated catch block
    		//pDialog.dismiss();
			if(ErrorDlg == true)
			ShowNoNetErrorDialoag();
    	}
    	
    	catch (Exception e) {
        	// TODO Auto-generated catch block
        		//pDialog.dismiss();	
        		//new MyAsyncTask().cancel(true);
        		//Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();
    	}
    	
    	

    	}
    	 
    	}
    
    
    
    
    public void Get_Faces(){
    	
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
    	
    new AsyncTask<String, Integer, Double>() {
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog.setMessage("Loading contacts...");
		 
		

	}
	
	@Override
	protected Double doInBackground(String... params) {

		String url = LoginActivity.SERVER+"get_faces.php"; 
		
		String query=null;

		try {
			     query = String.format("PacketHead=%s&UID=%s", 
			     URLEncoder.encode("get_faces", "UTF-8"),
			     URLEncoder.encode(utilhelper.getUSER_ID(), "UTF-8"));
		
		
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
		
		Fresponse = connection.getInputStream();
		
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
		
		fresponse = IOUtils.toString(Fresponse);
	
		
		
		} catch (IOException e) {
			//pDialog.dismiss();
			if(ErrorDlg == true);
			ShowNoNetErrorDialoag();
		} 
		
		
    
		return null;
	}
	
   	protected void onProgressUpdate(Integer... progress){

   	}

	
	protected void onPostExecute(Double result){

		if(fresponse.trim() != null)
		{
			
	  try{
		  
			JSONObject jsonObj = new JSONObject(fresponse.trim());      	
			
        	if(jsonObj.isNull("faces")==false)
        	{
        	 
        	 Fdata = jsonObj.getJSONArray("faces");	
        	 utilhelper.Clear();
			
            for(int i=0;i<Fdata.length();i++)
            {
            	JSONObject c = Fdata.getJSONObject(i);
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

            
            }
           
            
			}
    		
        	
          
			
		 } catch (JSONException e){
         	
         }
			
		}
   
		
         Go_Home();
 		 ClosMe();
 		 pDialog.dismiss();
		
	}
    
    }.execute();
    
        }
        else
        {
        	//pDialog.dismiss();	
			if(ErrorDlg == true);
			ShowNoNetErrorDialoag();
        }	
    

    }
        
    
    
    
    @SuppressLint("ValidFragment")
	public class NetworkErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setMessage(neterrormsg)
            builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
                   .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	  //getRegId();
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
	    newFragment.show(getFragmentManager(), "noneterror");
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
                    	
                   		pDialog = new ProgressDialog(LoginActivity.this);
                		pDialog.setMessage("Initializing...");
                		pDialog.setIndeterminate(false);
                		pDialog.setCancelable(false);
                		pDialog.show();
                	
                    	   getRegId();
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
	    pDialog.dismiss();
	}
	
	

	
    
    
    /**
     * Checks if the device has Internet connection.
     * 
     * @return <code>true</code> if the phone is connected to the Internet.
     */
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
    
    
      
    public void ClosMe() {

    //Intent intent = getIntent();
    //overridePendingTransition(0, 0);
    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    finish();
}
    
    public void ShowTost(String txt)
    {
    	Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }
        
        
    
/*void Get_phone_contacts() 
{
    
 Cursor c1;
 // list Columns to retive , pass null to get all the columns
 String col[]={ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME};
 c1 = getContentResolver().query
 (ContactsContract.Contacts.CONTENT_URI, col, null, null,
 ContactsContract.Contacts.DISPLAY_NAME);
 String personName = null, number = "";
 if(c1==null)
 return;
 // Fetch the Corresponding Phone Number of Person Name
 try
 {
 if(c1.getCount() > 0)
 {
 while(c1.moveToNext())
 {
 String id = c1.getString(c1.getColumnIndex(Contacts._ID)
 );
 personName = c1.getString(c1.getColumnIndex
 (Contacts.DISPLAY_NAME));
 if(id==null||personName==null)
 continue;
 Cursor cur = mContext.getContentResolver().query
 (CommonDataKinds.Phone.CONTENT_URI, null,
 CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
 if(cur==null)
 continue;
 number = "";
 while(cur.moveToNext())
 {
 number = cur.getString(cur.getColumnIndex
 (CommonDataKinds.Phone.NUMBER));
 }
 cur.close();
 }
 }
 }
 catch(Exception e)
 {
 }
 finally
 {
 c1.close();
 }
 
}*/
    
    
	private void copyBackGrounds() throws IOException{
		
		final File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Mimix"+File.separator+File.separator+"Backgrounds"+File.separator);
		root.mkdirs();


		for(int i=0;i<21;i++)
		{

		BG_NAME	= "bg_"+i+".jpg";
			
		InputStream myInput = this.getAssets().open(BG_NAME);

		// Path to the just created empty db
		String outFileName = root.toString() + BG_NAME;

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


