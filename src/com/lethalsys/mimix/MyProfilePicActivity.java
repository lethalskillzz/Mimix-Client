package com.lethalsys.mimix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.edmodo.cropper.CropImageView;
import com.lethalsys.mimix.MyProfileActivity.ProfileAdapter;

public class MyProfilePicActivity extends Activity {

	
	String iresponse;
	String UsrNAME;


	private String UserID;
	byte[] imgdata = null;
	String imgfile;

	// number of images to select
	private static final int PICK_IMAGE = 1;
	

	Util_Database utilhelper;
	ProfileAdapter adapter=null;
	

	JSONArray data = null;

	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	
	private ImageView profpic=null;
	private CropImageView cropImageView;
    private ProgressBar ppic_loading;
    private ImageButton postppic_btn=null;
    private Uri outputFileUri;
	
	Boolean ErrorDlg = true;
	
	private Bitmap MyBMP;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile_pic);
		
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		utilhelper = new Util_Database(this);
		UserID = utilhelper.getUSER_ID();
		
		profpic=(ImageView)findViewById(R.id.disp_myprofpic);
	    profpic.setMinimumHeight(350);
	    profpic.setMinimumWidth(350);
	    profpic.setMaxHeight(550);
	    profpic.setMaxWidth(550);
	    
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);
		postppic_btn = (ImageButton) findViewById(R.id.postppic_btn);
		ppic_loading = (ProgressBar) findViewById(R.id.ppic_loading);
		
		
		
		/*if(utilhelper.getFACEisIMG(UserID).equals("YES"))
		{
			Bitmap bmp = BitmapFactory.decodeByteArray(utilhelper.getFACEPPIC(UserID), 0, utilhelper.getFACEPPIC(UserID).length);			
			profpic.setImageBitmap(bmp);
			
		}
		else
		{
			profpic.setImageResource(R.drawable.me);
		}*/
		
		//Getprofpic();
		new BMPAsyncTask().execute("img_"+UserID+".png");
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

	
	
	public void Go_postppic(View v)
	{
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		cropImageView.getCroppedImage().compress(CompressFormat.PNG, 100, bos);
		imgdata = bos.toByteArray();
		int flag = 0;
		imgfile = Base64.encodeToString(imgdata, flag);
		
		
		
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
	    	new MyAsyncTask().execute("changeprofpic");
        }
        else
        {
        	//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
        	ShowNoNetErrorDialoag();
        }
    	
	}
	
	
	public void Getprofpic()
	{
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
	    	new MyAsyncTask().execute("getmyprofpic");
        }
        else
        {
        	//Toast.makeText(this, "Network Error! No Internet Connection", Toast.LENGTH_LONG).show();
        	ShowNoNetErrorDialoag();
        }
	}
	
	
	public void Go_Changepic(View v)
	{
		openImageIntent();
	}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_pic, menu);
		return true;
	}*/
	
	

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




	
	
	
	
	
	private void openImageIntent() {
	// Determine Uri of camera image to save.
	final File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Mimix"+File.separator+"Images"+File.separator);
	root.mkdirs();
	final String fname = "ppic_mimix"+System.currentTimeMillis()+".jpg";//Utils.getUniqueImageFilename();
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
	        
	    }
	}
	


	/**
	 * The method decodes the image file to avoid out of memory issues. Sets the
	 * selected image in to the ImageView.
	 * 
	 * @param filePath
	 */
	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		Bitmap scaledBitmap = null;
		
        o.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, o);
 
        int actualHeight = o.outHeight;
        int actualWidth = o.outWidth;
 
//      max Height and width values of the compressed image is taken as 816x612
 
        float maxHeight = 616.0f;
        float maxWidth = 412.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
 
//      width and height values are set maintaining the aspect ratio of the image
 
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
 
            }
        }
 
//      setting inSampleSize value allows to load a scaled down version of the original image
 
        o.inSampleSize = calculateInSampleSize(o, actualWidth, actualHeight);
		
		o.inJustDecodeBounds = false;
	    o.inPurgeable = true;
        o.inInputShareable = true;
        o.inTempStorage = new byte[16 * 1024];
 		
		
	     try {
//	          load the bitmap from its path
	            bmp = BitmapFactory.decodeFile(filePath, o);
	        } catch (OutOfMemoryError exception) {
	            exception.printStackTrace();
	 
	        }
	        try {
	        	scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
	        } catch (OutOfMemoryError exception) {
	            exception.printStackTrace();
	        }
	 
	        float ratioX = actualWidth / (float) o.outWidth;
	        float ratioY = actualHeight / (float) o.outHeight;
	        float middleX = actualWidth / 2.0f;
	        float middleY = actualHeight / 2.0f;
	 
	        Matrix scaleMatrix = new Matrix();
	        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
	 
	        Canvas canvas = new Canvas(scaledBitmap);
	        canvas.setMatrix(scaleMatrix);
	        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
	 
	        
	        ExifInterface exif;
	        try {
	            exif = new ExifInterface(filePath);
	 
	            int orientation = exif.getAttributeInt(
	                    ExifInterface.TAG_ORIENTATION, 0);
	            Log.d("EXIF", "Exif: " + orientation);
	            Matrix matrix = new Matrix();
	            if (orientation == 6) {
	                matrix.postRotate(90);
	                Log.d("EXIF", "Exif: " + orientation);
	            } else if (orientation == 3) {
	                matrix.postRotate(180);
	                Log.d("EXIF", "Exif: " + orientation);
	            } else if (orientation == 8) {
	                matrix.postRotate(270);
	                Log.d("EXIF", "Exif: " + orientation);
	            }
	            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
	            		scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
	                    true);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
		
		profpic.setVisibility(View.GONE);
		cropImageView.setVisibility(View.VISIBLE);
		postppic_btn.setVisibility(View.VISIBLE);
		
		cropImageView.setImageBitmap(scaledBitmap);
		//cropImageView.rotateImage(90);
		cropImageView.setFixedAspectRatio(true);
		cropImageView.setAspectRatio(10,10);
		
		


	/*	// The new size we want to scale to
		final int REQUIRED_SIZE = 128;

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
		
*/



	}
	
	
	
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 2;
	 
	    if (height > reqHeight || width > reqWidth) {
	        final int heightRatio = Math.round((float) height/ (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
	        inSampleSize++;
	    }
	 
	    return inSampleSize;
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

			ppic_loading.setVisibility(View.VISIBLE);
			postppic_btn.setVisibility(View.GONE);
			
		}


		protected void onPostExecute(Double result){

			if(null!=iresponse)
    		{
			if(iresponse.trim().length()!=0)
			{


				try{


					JSONObject jsonObj = new JSONObject(iresponse.trim());               	
					

					if(jsonObj.isNull("data")==false)
					{


						new BMPAsyncTask().execute("img_"+UserID+".png");

                    	
                		//profpic.setVisibility(View.VISIBLE);
                		cropImageView.setVisibility(View.GONE);
                		postppic_btn.setVisibility(View.GONE);
                	
					}
					else
					{
						Toast.makeText(getApplicationContext(),"Failed to upload profile pic", Toast.LENGTH_LONG).show();
					}
				
				
						

					
				} catch (JSONException e){

				} 

				data = null;
				ppic_loading.setVisibility(View.GONE);
			}


    		}

			//Toast.makeText(getApplicationContext(),iresponse.trim() , Toast.LENGTH_LONG).show();
			//confirmFireMissiles();
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

			HttpPost httppost = new HttpPost(LoginActivity.SERVER+"profile_detail.php");

			try {
				// Add your data

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
				nameValuePairs.add(new BasicNameValuePair("UID", UserID ));
				if(Header.compareTo("changeprofpic")==0)
				{
				nameValuePairs.add(new BasicNameValuePair("PROFPIC", imgfile ));
				}
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
 

				// Execute HTTP Post Request
				iresponse = httpclient.execute(httppost, responseHandler);




			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();
				ppic_loading.setVisibility(View.GONE);
				cancel(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ppic_loading.setVisibility(View.GONE);
				if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
			}
			
			
			


		}

	}
	
	

	private class BMPAsyncTask extends AsyncTask<String, Integer, Double>{

		@Override
		protected Double doInBackground(String... params) {

			DownloadBMP(params[0]);

			return null; 
		} 


		@Override  
		protected void onPreExecute() {
			super.onPreExecute();
			
			ppic_loading.setVisibility(View.VISIBLE);
			postppic_btn.setVisibility(View.GONE);

		}


		protected void onPostExecute(Double result){
			if(MyBMP!=null)
			{
			 
			    profpic.setImageBitmap(MyBMP);
			}
			else
			{
				profpic.setImageResource(R.drawable.me);
			}

    		profpic.setVisibility(View.VISIBLE);
    		cropImageView.setVisibility(View.GONE);
    		postppic_btn.setVisibility(View.GONE);
    		ppic_loading.setVisibility(View.GONE);

		}


		protected void onProgressUpdate(Integer... progress){

		}
		
		
		protected void DownloadBMP(String filename)
		{
		 //Bitmap myBitmap=null;
		try {
		  //set the download URL, a url that points to a file on the internet
		  //this is the file to be downloaded
		  String Url = LoginActivity.SERVER+"profile/image/"+filename;
		  URL url = new URL(Url);
		  //create the new connection
		  HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

		  //set up some things on the connection
		  urlConnection.setRequestMethod("GET");
		  urlConnection.setDoOutput(true); 
		  urlConnection.setConnectTimeout(15000);
		  urlConnection.setReadTimeout(30000);
		   //and connect!
		  urlConnection.connect();

		  //this will be used in reading the data from the internet
		  InputStream inputStream = urlConnection.getInputStream();
		  MyBMP = BitmapFactory.decodeStream(inputStream);
		 // Log.i("data:", IOUtils.toString(inputStream));

		 //catch some possible errors...
		 } catch (MalformedURLException e) {
		  e.printStackTrace();
		  Log.i("URL-ERROR:",e.toString());
		 } catch (IOException e) {
			 //myBitmap=null;
		  e.printStackTrace();
		  Log.i("IO-ERROR:",e.toString());
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
                    	   Getprofpic();
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
