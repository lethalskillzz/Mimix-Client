package com.lethalsys.mimix;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

	    public class TabsPagerAdapter extends FragmentPagerAdapter  implements IconPagerAdapter {

	    	protected static final String[] CONTENT = new String[] { "tab0", "tab1", "tab2", "tab3","tab4", };
	    	
	    	 protected static final int[] ICONS = new int[] {
	                R.drawable.post_tabicon,
	                R.drawable.notification_tabicon,
	                R.drawable.contact_tabicon,
	                R.drawable.message_tabicon,
	                R.drawable.addon_tabicon
	        };

	        private int mCount = 5;

	        public TabsPagerAdapter(FragmentManager fm) {
	            super(fm);
	        }

	    	public Fragment getItem(int position) {
	    		switch (position) {

	    			
	    		case 0:
	    			Post_Fragment post_fragment = new Post_Fragment();
	    			return post_fragment;

	    			
	    		case 1:
	    			Notification_Fragment notification_fragment = new Notification_Fragment();
	    			return notification_fragment;

	    			
	    		case 2:
	    			Contact_Fragment contact_fragment = new Contact_Fragment();
	    			return contact_fragment;

	    		case 3:
	    			Message_Fragment message_fragment = new Message_Fragment();
	    			return message_fragment;

	    		case 4:
	    			Addon_Fragment addon_fragment = new Addon_Fragment();
	    			return addon_fragment;
	    			
	    		}
	    		return null; 
	    	}

	        @Override
	        public int getCount() {
	            return mCount;
	        }


	        @Override
	        public CharSequence getPageTitle(int position) {
	          return CONTENT[position % CONTENT.length];
	        }

	        
	        @Override
	        public int getIconResId(int index) {
	          return ICONS[index % ICONS.length];
	        }

	        public void setCount(int count) {
	            if (count > 0 && count <= 10) {
	                mCount = count;
	                notifyDataSetChanged();
	            }
	        }
	     
	    }
	    
