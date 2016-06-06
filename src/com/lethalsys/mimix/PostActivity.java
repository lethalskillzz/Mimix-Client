package com.lethalsys.mimix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PostActivity extends Activity implements OnClickListener {

	 Util_Database utilhelper;
	
	private EditText Postvalue;
	String iresponse;
	private Button btn;
	String UserID;
	
	private TextView count;
	private ImageButton image;
	private Bitmap bitmap;
	byte[] data;
	String file=null;

	// number of images to select
	private static final int PICK_IMAGE = 1;
	private Uri outputFileUri;

	
	
	public static Handler PostActivityHandler = null;
	//private ProgressDialog pDialog;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	JSONArray Jdata = null;
	String msg;
	
	private ProgressBar post_progressBar;
	
	Boolean ErrorDlg = true;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		
		utilhelper = new Util_Database(this);
		UserID = utilhelper.getUSER_ID();

		
	    Postvalue=(EditText)findViewById(R.id.post_edttxt);
	    Postvalue.addTextChangedListener(watch);
	    
	    count = (TextView) findViewById(R.id.char_count);
	    
	    btn=(Button)findViewById(R.id.Post_button);
		btn.setOnClickListener(this);
		
		image = (ImageButton) findViewById(R.id.post_img);
		image.setOnLongClickListener(onLongClick);
			
		/*Intent intent = getIntent();
	    UserID = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE2);*/
	    
		
		
		//Toast.makeText(this,UserID , Toast.LENGTH_LONG).show();
	    post_progressBar = (ProgressBar) findViewById(R.id.post_progressBar);
	    post_progressBar.setVisibility(View.GONE);
	    

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
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK :
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post, menu);
		return true;
	}*/
	
	public void MakePost() {

    	if(Postvalue.getText().toString().length()<1 && file==null ){
    		//Toast.makeText(this, "Post can not be empty", Toast.LENGTH_LONG).show();
    	}else{

    	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

    	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
    	    {
                if(file!=null)
                {
                	new MyAsyncTask().execute("add_post_img");
                }
                else
                {
    	    	new MyAsyncTask().execute("add_post");
                }
            }
            else
            {
            	//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
            	ShowNoNetErrorDialoag();
            }	
    	
    	}
    		
	}
	
	
    public void onClick(View v) {
    	MakePost();
    }
    
    
	
    public void get_img(View v) {
    
    	//selectImageFromGallery();
    	openImageIntent(); 
    }
    /*public void GoPost() {
    	if(Postvalue.getText().toString().length()<1){
    		Toast.makeText(this, "post cannot be empty", Toast.LENGTH_LONG).show();
    	}else{

    	new MyAsyncTask().execute("REG");	
    }
    	
    }*/
    public void GoHome() {
	Intent Profintent = new Intent(this, HomeActivity.class);
	startActivity(Profintent);
    }
    
    
    
    
     private  View.OnLongClickListener onLongClick = new View.OnLongClickListener() {
    	
		@Override
		public boolean onLongClick(View v) {
			file = null;
			image.setImageResource(R.drawable.ic_camera);
			return false;
		}
	};
    
    
	   TextWatcher watch = new TextWatcher(){
		   @Override
		   public void afterTextChanged(Editable arg0) {
		     // TODO Auto-generated method stub
			  
		   }
		   @Override
		   public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		       int arg3) {
		     // TODO Auto-generated method stub
			   
		   }
		   @Override
		   public void onTextChanged(CharSequence s, int a, int b, int c) {
		     // TODO Auto-generated method stub
			   if(s.length() > 0)
			   {
				   count.setText(String.valueOf(s.length())+"/140");
				  // msgsend.setVisibility(View.VISIBLE);
			   }
			   else
			   {
				   count.setText("0/140");
				   //msgsend.setVisibility(View.GONE);
			   }
		  
		   }};
	
    
    
    
	/**
	 * Opens dialog picker, so the user can select image from the gallery. The
	 * result is returned in the method <code>onActivityResult()</code>
	 */
	public void selectImageFromGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				PICK_IMAGE);
	}
	

	
	private void openImageIntent() {
	// Determine Uri of camera image to save.
	final File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Mimix"+File.separator+"Images"+File.separator);
	root.mkdirs();
	final String fname = "img_mimix"+System.currentTimeMillis()+".jpg";//Utils.getUniqueImageFilename();
	final File sdImageMainDirectory = new File(root, fname);
	outputFileUri = Uri.fromFile(sdImageMainDirectory);
	    // Camera.
	    final List<Intent> cameraIntents = new ArrayList<Intent>();
	    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    final PackageManager packageManager = getPackageManager();
	    final List<ResolveInfo> listCam = packageManager.queryIntentActivities
	(captureIntent, 0);
	    for(ResolveInfo res : listCam) {
	        final String packageName = res.activityInfo.packageName;
	        final Intent intent = new Intent(captureIntent);
	        intent.setComponent(new ComponentName
	(res.activityInfo.packageName, res.activityInfo.name));
	        intent.setPackage(packageName);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        cameraIntents.add(intent);
	    }
	    // Filesystem.
	    final Intent galleryIntent = new Intent();
	    galleryIntent.setType("image/*");
	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
	    // Chooser of filesystem options.
	    final Intent chooserIntent = Intent.createChooser(galleryIntent, "SelectSource");
	    // Add the camera options.
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
	cameraIntents.toArray(new Parcelable[]{}));
	    startActivityForResult(chooserIntent, PICK_IMAGE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    if(resultCode == RESULT_OK)
	    {
	        if(requestCode == PICK_IMAGE)
	        {
	            final boolean isCamera;
	            if(data == null)
	            {
	                isCamera = true;
	            }
	            else
	            {
	                final String action = data.getAction();
	                if(action == null)
	                {
	                    isCamera = false;
	                }
	                else
	                {
	                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	                }
	            }
	            Uri selectedImageUri;
	            if(isCamera)
	            {
	                selectedImageUri = outputFileUri;	
	                decodeFile(selectedImageUri.getPath());
	            }
	            else
	            {
	                selectedImageUri = data == null ? null : data.getData();


		            String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImageUri,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					decodeFile(picturePath);
	            }

	           
	        }
	        
	        Toast.makeText(getApplicationContext(),"Long press to remove" , Toast.LENGTH_LONG).show();
	    }
	}
	

	/**
	 * Retrives the result returned from selecting image, by invoking the method
	 * <code>selectImageFromGallery()</code>
	 */
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			decodeFile(picturePath);

		}
	}*/

	/**
	 * The method decodes the image file to avoid out of memory issues. Sets the
	 * selected image in to the ImageView.
	 * 
	 * @param filePath
	 */
	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 512;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		image.setImageBitmap(bitmap);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, bos);
		data = bos.toByteArray();
		
		file = Base64.encodeToString(data, 0);//encodeBytes(data);
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
			
			/*pDialog = new ProgressDialog(PostActivity.this);
			pDialog.setMessage("adding post...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();*/
			btn.setVisibility(View.GONE);
			post_progressBar.setVisibility(View.VISIBLE);
			isFinish(false);
			
			
		}
    	 
    	protected void onPostExecute(Double result){
    		if(null!=iresponse)
    		{
            if(iresponse.trim().length()!=0)
            {    		
            	/*//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
            	sendmsg();
            	post_progressBar.setVisibility(View.GONE);
            	ClosMe();
            	 //GoHome();
    
           	    pDialog.setMessage("post added");	    
        	    pDialog.dismiss();*/
    			try{


    				JSONObject jsonObj = new JSONObject(iresponse.trim());               	
    				

    				if(jsonObj.isNull("data")==false)
    				{
    					Jdata = jsonObj.getJSONArray("data");

    					JSONObject d = Jdata.getJSONObject(0);
    					String msg = d.getString("msg");


    					if("Post added".compareTo(msg)==0)
    					{
    						
    		            	sendmsg();
    		            	post_progressBar.setVisibility(View.GONE);
    		            	ClosMe(); 
    		            	


    					}

    					else
    					{
    						Toast.makeText(getApplicationContext(),"Post was not added" , Toast.LENGTH_LONG).show();
    					}

    				}
    			} catch (JSONException e){

    			} 

    			

            }
    		}
			//Toast.makeText(getApplicationContext(),iresponse.trim(), Toast.LENGTH_LONG).show();
    		//confirmFireMissiles();
    		post_progressBar.setVisibility(View.GONE);
    		btn.setVisibility(View.VISIBLE);
    		isFinish(true);
    	}
    	
    	
    	
    	protected void postData(String Header) {
    	// Create a new HttpClient and Post Header
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
    	nameValuePairs.add(new BasicNameValuePair("post_body", Postvalue.getText().toString()));
    	if(Header.compareTo("add_post_img")==0)
    	{	
    	nameValuePairs.add(new BasicNameValuePair("post_img",file));
    	}
    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	 

    	//HttpResponse response;

    	
    	// Execute HTTP Post Request
    	 iresponse = httpclient.execute(httppost, responseHandler);
    	 

    	
    	} catch (IOException e) {
    	
    		
			if(ErrorDlg == true)
			ShowNoNetErrorDialoag();
    	}


    	}
    	 
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
                    	   MakePost();
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
	

	
	
	
    
    public void sendmsg()
    {
      
        PostActivityHandler = new Handler();
     
        if(null != Post_Fragment.mUiHandler)
        {
            //first build the message and send.
            //put a integer value here and get it from the Activity handler
            //For Example: lets use 0 (msg.what = 0;)
            //for receiving service running status in the activity
            Message msgToActivity = new Message();
            msgToActivity.what = 1;
            Post_Fragment.mUiHandler.sendMessage(msgToActivity);
        } 
    }
    
    
    
    public void ClosMe() {

    Intent intent = getIntent();
    overridePendingTransition(0, 0);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
}
	
    
    void isFinish(boolean bl)
    {
    	this.setFinishOnTouchOutside(bl);
    }
	
	

}
