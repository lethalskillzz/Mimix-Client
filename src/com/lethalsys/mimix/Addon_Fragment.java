package com.lethalsys.mimix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.lethalsys.mimix.aidl.IBinary;
import com.viewpagerindicator.PageIndicator;

public class Addon_Fragment extends Fragment {
	

    private OpServiceConnection opServiceConnection;
    private IBinary IService;
	
    private PackageBroadcastReceiver packageBroadcastReceiver;
    private IntentFilter packageFilter;
    private ArrayList<HashMap<String,String>> services;
    private ArrayList<String> categories; 
 
    public static final String ACTION_PICK_ADDON = "mimix.intent.action.PICK_ADD_ON";
    static final String KEY_PKG = "pkg";
    static final String KEY_SERVICENAME = "servicename"; 
    static final String KEY_ACTIONS = "actions";
    static final String KEY_CATEGORIES = "categories";
    static final String BUNDLE_EXTRAS_CATEGORY = "category";



	private String airesponse;
	private String UserID;
	
	
	Database_One helper=null;


	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	JSONArray data = null;

	String addon_pkg;

	Boolean ErrorDlg = true;
	Util_Database utilhelper=null;

	public ProgressBar addon_loading;

	StaggeredGridView  gridView;
	Gridadapter Adapter;
	
	
	 
		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View rootView = inflater.inflate(R.layout.fragment_addon, container, false);
	        

			helper=new Database_One(getActivity());
			utilhelper=new Util_Database(getActivity());
			UserID = utilhelper.getUSER_ID();

	        
			packageBroadcastReceiver = new PackageBroadcastReceiver();
			packageFilter = new IntentFilter();
			packageFilter.addAction( Intent.ACTION_PACKAGE_ADDED  );
			packageFilter.addAction( Intent.ACTION_PACKAGE_REPLACED );
			packageFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
			packageFilter.addCategory( Intent.CATEGORY_DEFAULT ); 
			packageFilter.addDataScheme( "package" );
			
			gridView = (StaggeredGridView ) rootView.findViewById(R.id.home_gridView);
			
			gridView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {


					String cat = services.get(position).get(KEY_CATEGORIES);
					String pkg = services.get(position).get(KEY_PKG);

					bindService(cat);

					try{

						Resources rc = getActivity().getPackageManager().getResourcesForApplication(pkg);
						String addon_name=rc.getString(rc.getIdentifier(pkg+":string/addon_name","string", null));

						Intent intent = new Intent();
						intent.setComponent(new ComponentName(pkg,pkg+"."+addon_name));
						startActivity(intent);

					}catch(NameNotFoundException e)
					{      	 
					}


				}
			});
			
			
			
			gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View v,
						int position, long id) {				

					//String cat = services.get(position).get(KEY_CATEGORIES);
					String pkg = services.get(position).get(KEY_PKG);
					addon_pkg = pkg;
					if(helper.get_Addon_isInstalled(pkg).equals("YES"))
					{
						                    
						PopupMenu popupMenu = new PopupMenu(getActivity(), v);				
						popupMenu.setOnMenuItemClickListener((OnMenuItemClickListener) getActivity());				
						popupMenu.inflate(R.menu.i_addon_popup_menu);				
						popupMenu.show();	
						return true;
	 	               
					}
					else
					{
						PopupMenu popupMenu = new PopupMenu(getActivity(), v);				
						popupMenu.setOnMenuItemClickListener((OnMenuItemClickListener) getActivity());				
						popupMenu.inflate(R.menu.addon_popup_menu);				
						popupMenu.show();	
						return true;

					}
				}
			});
			
			addon_loading = (ProgressBar)rootView.findViewById(R.id.addon_loading);

			get_addon_service();
			//Toast.makeText(getActivity(),"AERROORR" , Toast.LENGTH_LONG).show();							

	       
	        return rootView;
	    }
		
		



public void get_addon_service() {
services = new ArrayList<HashMap<String,String>>();
categories = new ArrayList<String>();
PackageManager packageManager = getActivity().getPackageManager();
Intent baseIntent = new Intent( ACTION_PICK_ADDON );
baseIntent.setFlags( Intent.FLAG_DEBUG_LOG_RESOLUTION );
List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,
PackageManager.GET_RESOLVED_FILTER );
//Log.d( LOG_TAG, "fillPluginList: "+list );
for( int i = 0 ; i < list.size() ; ++i ) {
ResolveInfo info = list.get( i );
ServiceInfo sinfo = info.serviceInfo;
IntentFilter filter = info.filter;
//Log.d( LOG_TAG, "fillPluginList: i: "+i+"; sinfo: "+sinfo+";filter: "+filter );
if( sinfo != null ) {
HashMap<String,String> item = new HashMap<String,String>();
item.put( KEY_PKG, sinfo.packageName );
//item.put( KEY_SERVICENAME, sinfo.name );
String firstCategory = null;
if( filter != null ) {
StringBuilder actions = new StringBuilder();
for( Iterator<String> actionIterator = filter.actionsIterator() ; actionIterator.hasNext() ; ) {
	String action = actionIterator.next();
	if( actions.length() > 0 )
		actions.append( "," );
	actions.append( action );
}
StringBuilder categories = new StringBuilder();
for( Iterator<String> categoryIterator = filter.categoriesIterator() ;
		categoryIterator.hasNext() ; ) {
	String category = categoryIterator.next();
	if( firstCategory == null )
		firstCategory = category;
	if( categories.length() > 0 )
		categories.append( "," );
	categories.append( category );
}
//item.put( KEY_ACTIONS,new String( actions ) );
item.put( KEY_CATEGORIES,new String ( categories ) );
} else {
//item.put( KEY_ACTIONS,"<null>" );
item.put( KEY_CATEGORIES,"<null>" );
}
if( firstCategory == null )
firstCategory = "";
categories.add( firstCategory );
services.add( item );
}
}
//Log.d( LOG_TAG, "services: "+services );
//Log.d( LOG_TAG, "categories: "+categories );
SetGrid();
}



class PackageBroadcastReceiver extends BroadcastReceiver {
//private static final String LOG_TAG = "PackageBroadcastReceiver";
public void onReceive(Context context, Intent intent) {
//Log.d( LOG_TAG, "onReceive: "+intent );
services.clear();
}
}



private void bindService(String cat) {

opServiceConnection = new OpServiceConnection();
Intent i = new Intent( ACTION_PICK_ADDON );
i.addCategory( cat );
getActivity().bindService( i, opServiceConnection, Context.BIND_AUTO_CREATE);

}

private void releaseService() {
getActivity().unbindService( opServiceConnection );	  
opServiceConnection = null;
}




class OpServiceConnection implements ServiceConnection {

public void onServiceConnected(ComponentName className, 
IBinder boundService ) {
IService = IBinary.Stub.asInterface((IBinder)boundService);
//Log.d( LOG_TAG,"onServiceConnected" );    	
//ShowTost("ServiceConnected");
try {
IService.data( LoginActivity.SERVER, UserID, utilhelper.getUSER() );
} catch (RemoteException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
}	        	
}

public void onServiceDisconnected(ComponentName className) {
IService = null;
//Log.d( LOG_TAG,"onServiceDisconnected" );	        	
//ShowTost("ServiceDisconnected");
}
};




public void SetGrid()
{ 
Resources rc;
String addon_name;
Bitmap bitmap;
ByteArrayOutputStream stream;
byte[] addon_img;

JSONArray arr = new JSONArray();	    
JSONObject obj2 = new JSONObject();

helper.Delete_Addon("'YES'"); 

for(int i=0;i<services.size();i++)
{
String pkg = services.get(i).get(KEY_PKG);
String cat = services.get(i).get(KEY_CATEGORIES);

try {
rc = getActivity().getPackageManager().getResourcesForApplication(pkg);

addon_name = rc.getString(rc.getIdentifier(pkg+":string/addon_name","string", null));

bitmap = ((BitmapDrawable)rc.getDrawable(rc.getIdentifier(pkg+":drawable/ic_addon","drawable", null))).getBitmap();
stream = new ByteArrayOutputStream();
bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
addon_img = stream.toByteArray();


helper.insertAddon("YES", cat, pkg, addon_name, addon_img, null);			

JSONObject obj = new JSONObject();
obj.put("i_addon_name",addon_name);
arr.put(obj); 
obj2.put("i_addons",arr);
obj2.put("uid",UserID);
obj2.put("header","get_addons");

bitmap = null;
stream = null;
addon_img = null;

} catch (NameNotFoundException e) {
// TODO Auto-generated catch block
e.printStackTrace();	    
}catch (JSONException e){

}

}


get_addons(obj2.toString());
services.clear();


Cursor i_addon_model = helper.getAll_I_Addon();

if (i_addon_model != null)
{		
while(i_addon_model.moveToNext())
{

HashMap<String,String> item = new HashMap<String,String>();
item.put(KEY_CATEGORIES,i_addon_model.getString(1));
item.put(KEY_PKG,i_addon_model.getString(2));
services.add( item );

}
}


Cursor addon_model = helper.getAllAddon();

if (addon_model != null)
{
while(addon_model.moveToNext())
{

HashMap<String,String> item = new HashMap<String,String>();
item.put(KEY_CATEGORIES,addon_model.getString(1));
item.put(KEY_PKG,addon_model.getString(2));

services.add( item );

}
}



Adapter = new Gridadapter(getActivity(), services);
gridView.setAdapter(Adapter);
}


public void get_addons(final String addonx)
{
mConnectivityManager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
{

new AsyncTask<String, Integer, Double>() {

@Override
protected void onPreExecute() {
super.onPreExecute();

addon_loading.setVisibility(View.VISIBLE);

}

@Override
protected Double doInBackground(String... params) {


String url = LoginActivity.SERVER+"get_addons.php"; 

String query=addonx;

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

airesponse = IOUtils.toString(response);

}catch (IOException e) {
//if(ErrorDlg == true)
//ShowNoNetErrorDialoag();
} 


return null;
}



protected void onProgressUpdate(Integer... progress){

}


protected void onPostExecute(Double result)
{

if(null != airesponse)
{
if(airesponse.trim().length()!=0)
{


try{

JSONObject jsonObj = new JSONObject(airesponse.trim());      	

if(jsonObj.isNull("addons")==false)
{

data = jsonObj.getJSONArray("addons");

if(data.length()>0)
{  

helper.Delete_Addon("'NO'");


for(int i=0;i<data.length();i++)
{
JSONObject c = data.getJSONObject(i);
String category = c.getString("category");
String addon_package = c.getString("addon_package");
String addon_name = c.getString("addon_name");
String img = c.getString("img");
String url = c.getString("url");
     	
byte[] imgdata;
	
imgdata = Base64.decode(img, 0);    	
helper.insertAddon("NO", category, addon_package, addon_name, imgdata, url);	                    		            	     	                		               	

}

}

}


} catch (JSONException e){

}

}

 
}
//ShowTost(airesponse);
//confirmFireMissiles();
addon_loading.setVisibility(View.GONE);




}

}.execute();

}
else
{
//if(ErrorDlg == true)
//ShowNoNetErrorDialoag();
}	


}



private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();


public class Gridadapter extends BaseAdapter {
private Context context;
//private final String[] activity;
private final ArrayList<HashMap<String,String>> activity;

public Gridadapter(Context context, ArrayList<HashMap<String,String>> activity) {
this.context = context;
this.activity = activity;

}

public View getView(int position, View convertView, ViewGroup parent) {

LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

View gridView;
ViewHolder vh;

if (convertView == null) {



gridView = new View(context);
gridView = inflater.inflate(R.layout.home_grid, null);

LinearLayout addon_bg = (LinearLayout) gridView .findViewById(R.id.home_addon_bg);
ImageView addon_img = (ImageView) gridView .findViewById(R.id.home_addon_item);
TextView textView = (TextView) gridView.findViewById(R.id.home_addon_label);


String pkg = services.get(position).get(KEY_PKG);
try{

if(helper.get_Addon_isInstalled(pkg).equals("YES"))
{
Resources rc = getActivity().getPackageManager().getResourcesForApplication(pkg);
addon_bg.setBackgroundDrawable(rc.getDrawable(rc.getIdentifier(pkg+":drawable/bg_addon","drawable", null)));
//addon_img.setImageDrawable(rc.getDrawable(rc.getIdentifier(pkg+":drawable/ic_addon","drawable", null)));
//textView.setText(rc.getString(rc.getIdentifier(pkg+":string/addon_name","string", null)));
}
else
{
	addon_bg.setBackgroundColor(Color.parseColor("#616f69"));
	addon_bg.setAlpha((float) 0.8);
}

Bitmap bm = BitmapFactory.decodeByteArray(helper.get_Addon_img(pkg), 0, helper.get_Addon_img(pkg).length);
addon_img.setImageBitmap(bm);
textView.setText(helper.get_Addon_txt(pkg));
}catch(NameNotFoundException e)
{      	 
}


vh = new ViewHolder();
vh.imgView = (DynamicHeightImageView) gridView.findViewById(R.id.home_addon_item);
gridView.setTag(vh);


} else {
gridView = (View) convertView;
vh = (ViewHolder) gridView.getTag();
}

double positionHeight = getPositionRatio(position);
vh.imgView.setHeightRatio(positionHeight);

return gridView;
}

@Override
public int getCount() {
return activity.size();
}

@Override
public Object getItem(int position) {
return null;
}


@Override
public long getItemId(int position) {
return 0;
}

}


private final Random mRandom = new Random();	


static class ViewHolder {
DynamicHeightImageView imgView;
}


private double getPositionRatio(final int position) {
double ratio = sPositionHeightRatios.get(position, 0.0);
//if not yet done generate and stash the columns height
//in our real world scenario this will be determined by
//some match based on the known height and width of the image
//and maybe a helpful way to get the column height!
if (ratio == 0) {
ratio = getRandomHeightRatio();
sPositionHeightRatios.append(position, ratio);
}
return ratio;
}


private double getRandomHeightRatio() {
return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
//the width


}

//////////////////////////////////////////////////////////////////////////////////////////

    
}