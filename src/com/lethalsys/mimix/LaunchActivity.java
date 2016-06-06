package com.lethalsys.mimix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LaunchActivity extends Activity {

    //public final static String SERVER = "http://10.0.2.2/mimix_project/";
    public final static String SERVER = "http://192.168.187.1/mimix_project/";
    //public final static String SERVER = "http://route.pixub.com/";
 
	Util_Database utilhelper=null;
	private ShimmerLayout mShimmerViewContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		
		utilhelper=new Util_Database(this);
			
			 
			if(utilhelper.getUSER_ID()!=null)
			{			
				Go_Home();
				ClosMe();		
			}
			
		    mShimmerViewContainer = (ShimmerLayout) findViewById(R.id.shimmer_view_container);
	       
		    //mShimmerViewContainer.setAngle(ShimmerLayout.MaskAngle.CW_270);
		    
	        /*mShimmerViewContainer.setBaseAlpha(0);
	        mShimmerViewContainer.setDuration(2000);
	        mShimmerViewContainer.setDropoff(0.1f);
	        mShimmerViewContainer.setIntensity(0.35f);
	        mShimmerViewContainer.setMaskShape(ShimmerLayout.MaskShape.RADIAL);*/
	        
		    mShimmerViewContainer.setDuration(3000);
	        mShimmerViewContainer.setBaseAlpha(0.85f);
	        mShimmerViewContainer.setDropoff(0.5f);
	        mShimmerViewContainer.setTilt(0);


		    mShimmerViewContainer.startShimmerAnimation();

			
	}

    
	
    public void startReg(View view) {
    	Intent intent = new Intent(this, RegisterActivity.class);
    	startActivity(intent);
    	} 
    
    
    public void go_login(View view) {
    	Intent intent = new Intent(this, LoginActivity.class);
    	startActivity(intent);
    	}  
    
 
	
    public void Go_Home() {
    	Intent intent = new Intent(this, HomeActivity.class);
    	intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK|*/Intent.FLAG_ACTIVITY_CLEAR_TASK);
    	startActivity(intent);
    	}  
    
    
    
   public void ClosMe() {
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    finish();
    }
    
}
