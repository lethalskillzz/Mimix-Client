package com.lethalsys.mimix;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddNewContactActivity extends Activity {
	
	
	private ArrayList<String> Item = new ArrayList<String>();
	AddNewAdapter adapter=null;
	private ListView list;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_contact);

		
		Item.add("Username");
		Item.add("Address Book");
		Item.add("Phone Number");
		Item.add("Email Address");

		
		list=(ListView)findViewById(R.id.add_newcontact_list);
	
		adapter=new AddNewAdapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(onItemClick);
		
		


	}

	
	
	public void GoPhoneBook() {

		Intent intent = new Intent(this, AddPhoneContactActivity.class);
		startActivity(intent);
	}	
	
	

	private AdapterView.OnItemClickListener onItemClick=new
			AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
			View view, int position,
			long id) {
				
			String item = Item.get(position);
			
	        if(item.equals("Username"))
	        {
	        	
	        }
	        	
	        else if(item.equals("Address Book"))
	        {
	        	GoPhoneBook();
	        }
	        
	        else if(item.equals("Phone Number"))
	        {
	        	
	        }
	        else{
	        	
		    
	        }


			
			}
			};
			
	
	

	class AddNewAdapter extends ArrayAdapter<String> {
		AddNewAdapter() {
			super(AddNewContactActivity.this, R.layout.addnew_row, Item);
		}
		public View getView(int position, View convertView,
				ViewGroup parent) {
			View row=convertView;
			AddNewHolder holder=null;
			if (row==null) {
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.addnew_row, parent, false);
				holder=new AddNewHolder(row);
				row.setTag(holder);
			}
			else {
				holder=(AddNewHolder)row.getTag();
			}
			holder.populateFrom(Item.get(position));
			return(row);
		}
	}
	
	static class AddNewHolder {
		private TextView txt=null;
		private ImageView img=null;
		//private View row=null;
		AddNewHolder(View row) {
		//this.row=row;
		
		txt = (TextView)row.findViewById(R.id.addnew_txt);
		img = (ImageView)row.findViewById(R.id.addnew_img);

		}

		void populateFrom(String r) {
			
			if(r.equals("Username"))
			{
				img.setImageResource(R.drawable.ic_username);

			}
			else if(r.equals("Address Book"))
			{
				img.setImageResource(R.drawable.ic_phonebook);
			}
			else if(r.equals("Phone Number"))
			{
				img.setImageResource(R.drawable.ic_mobile);
			}
			else
			{
				img.setImageResource(R.drawable.ic_email);
			}
			txt.setText(r);
			

			
            
		}
		
		
	}
		
		

	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.add_new_contact, menu);
		return true;
	}

}
