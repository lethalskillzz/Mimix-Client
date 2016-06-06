package com.lethalsys.mimix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
//import android.util.Xml;
//import android.opengl.Visibility;
//import android.widget.TextView;

public class RegisterActivity extends Activity implements OnClickListener{

	//private EditText value;
	private EditText Phonevalue;
	private EditText Emailvalue;
	private EditText Usernamevalue;
	private EditText Passwordvalue;
	private EditText Comfirmvalue;
	private Spinner Locationvalue;
	private Spinner Gendervalue;
	
	private Button btn;
	//private ProgressBar pb;
	private ProgressDialog pDialog;

	
	HttpResponse response;
	private String iresponse;
	//private String text;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	JSONArray data = null;
	String msg;

	Boolean ErrorDlg = true;
	 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		
		Phonevalue=(EditText)findViewById(R.id.editPhone);
		Emailvalue=(EditText)findViewById(R.id.editEmail);
		Usernamevalue=(EditText)findViewById(R.id.editUsername);
		Passwordvalue=(EditText)findViewById(R.id.editPassword);
		Comfirmvalue=(EditText)findViewById(R.id.editComfirm);
		Locationvalue=(Spinner)findViewById(R.id.editLocation);
		Gendervalue=(Spinner)findViewById(R.id.editGender);
		btn=(Button)findViewById(R.id.button1);
		//pb=(ProgressBar)findViewById(R.id.progressBar1);
		//pb.setVisibility(View.GONE);
		btn.setOnClickListener(this);
		
		String _no = null;
		_no = Get_Phone_no();
		if(_no!=null)
		{
			Phonevalue.setText(_no);
		}
		
		

       Spinner spinner = (Spinner) findViewById(R.id.editGender);
       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Gender_array, android.R.layout.simple_spinner_item);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinner.setPrompt("Gender...");
       spinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter,R.layout.gender_spinner_row_nothing_selected,
        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional 
       this));

       Spinner spinner2 = (Spinner) findViewById(R.id.editLocation);
       ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.Location_array, android.R.layout.simple_spinner_item);
       adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinner2.setPrompt("Location...");
       spinner2.setAdapter(new NothingSelectedSpinnerAdapter(adapter2,R.layout.location_spinner_row_nothing_selected,
        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional 
       this));

		
	}

	/*@Override
public boolean onCreateOptionsMenu(Menu menu) {
getMenuInflater().inflate(R.menu.main, menu);
return true;
}*/

	public void onClick(View v) {
		DoReG();
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
	
	
	public void DoReG() {
		if(Phonevalue.getText().toString().length()<1){
			// out of range
			Toast.makeText(this, "please enter Phone number", Toast.LENGTH_LONG).show();
		}
		if(Emailvalue.getText().toString().length()<1){
			// out of range
			Toast.makeText(this, "please enter email address", Toast.LENGTH_LONG).show();
		}
		if(Usernamevalue.getText().toString().length()<1){
			Toast.makeText(this, "please enter Username", Toast.LENGTH_LONG).show();
		}
		if(Passwordvalue.getText().toString().length()<1){
			Toast.makeText(this, "please enter Password", Toast.LENGTH_LONG).show();
		}else{
			//pb.setVisibility(View.VISIBLE);
			
			if(Comfirmvalue.getText().toString().length()<1){
				Toast.makeText(this, "please comfirm Password", Toast.LENGTH_LONG).show();
			}else{
				
			
				if(Passwordvalue.getText().toString().equals(Comfirmvalue.getText().toString()))
				{
					mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
					mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

					if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
					{

						new MyAsyncTask().execute("REG");

					}
					else
					{
						//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
						ShowNoNetErrorDialoag();
					}	


				}else{
					Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show();

				}
			}
			
		}

		
		
	}
	
	
	public void Goto_Login() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}  



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


			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}



		protected void onPostExecute(Double result){
			//pb.setVisibility(View.GONE);
			//Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
			//iresponse = Xml.parse(getResponseBody(response), new CustomXmlParser()); /*response.getStatusLine().getReasonPhrase();response.toString();*/

			//text = iresponse.trim();
			if(null!=iresponse)
    		{
				
			if(iresponse.trim().length()!=0)
			{

				try{


					JSONObject jsonObj = new JSONObject(iresponse.trim());               	
					

					if(jsonObj.isNull("data")==false)
					{

						data = jsonObj.getJSONArray("data");

						JSONObject d = data.getJSONObject(0);
						msg = d.getString("msg");


						if("Registration successful!".compareTo(msg)==0)
						{
							pDialog.setMessage("Registration complete");
							Toast.makeText(getApplicationContext(),msg , Toast.LENGTH_LONG).show();
							Goto_Login();
							ClosMe();

						}
						else
						{
							Toast.makeText(getApplicationContext(),msg , Toast.LENGTH_LONG).show();
							pDialog.dismiss();
						}

					}
				} catch (JSONException e){

				} 



			}
    		} 
			
			Toast.makeText(getApplicationContext(),iresponse , Toast.LENGTH_LONG).show();
		}



		protected void onProgressUpdate(Integer... progress){
			//pb.setProgress(progress[0]);
		}

		protected void postData(String valueSend) {
			// Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,LoginActivity.conntimeout);
		    HttpConnectionParams.setSoTimeout(httpparameters,LoginActivity.socktimeout);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
 
			HttpPost httppost = new HttpPost(/*"http://10.0.2.2/Twitter-Engine/*/LoginActivity.SERVER+"registerA.php");
			//HttpPost httppost = new HttpPost("http://route.pixub.com/registerA.php");

			//String Header = "REG";

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				//nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
				nameValuePairs.add(new BasicNameValuePair("email", Emailvalue.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("username", Usernamevalue.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("password", Passwordvalue.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("phone", Phonevalue.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("gender", String.valueOf(Gendervalue.getSelectedItem())));	
				nameValuePairs.add(new BasicNameValuePair("location", String.valueOf(Locationvalue.getSelectedItem())));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
  
 
				// Execute HTTP Post Request

				iresponse = httpclient.execute(httppost, responseHandler);

				/*response = httpclient.execute(httppost);
 InputStream is = response.getEntity().getContent();
 BufferedInputStream bis = new BufferedInputStream(is);
 ByteArrayBuffer baf = new ByteArrayBuffer(20);
 int current = 0;
 while ((current = bis.read()) != 1)
 {
	 baf.append((byte)current);

 }

 text = new String(baf.toByteArray());*/









			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();
				pDialog.dismiss();
				cancel(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
	    		pDialog.dismiss();
				if(ErrorDlg == true)
				ShowNoNetErrorDialoag();			}

			//Toast.makeText(getApplicationContext(), response.toString() , Toast.LENGTH_LONG).show();

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
                    	   DoReG();
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
	

	
    public String Get_Phone_no()
	{
    	
	TelephonyManager telemamanger = (TelephonyManager) getSystemService
	(Context.TELEPHONY_SERVICE);
	//String getSimSerialNumber = telemamanger.getSimSerialNumber();
	String SimNumber = telemamanger.getLine1Number();

	
	return SimNumber;
		
	}

	public void ClosMe() {

		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
	}

}