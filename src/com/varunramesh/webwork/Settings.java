package com.varunramesh.webwork;

import java.util.HashMap;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class Settings extends SherlockActivity {
	
	class ClearListener implements OnClickListener
	{
		Activity owner;
		public ClearListener(Activity own)
		{
			owner = own;
		}

		@Override
		public void onClick(View arg0) {
			
			SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
			String[] courses = settings.getString( "courses", "" ).split(";;;");
			
			if(settings.getString( "courses", "" ).compareTo("") != 0)
			{
				for(String course : courses)
				{
					SharedPreferences coursesettings = getSharedPreferences(course , 0);
					coursesettings.edit().clear().commit();
					
					
				}
			}
			
			settings.edit().clear().commit();
			
			startActivity(new Intent(owner, MainActivity.class));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public int dp(float dps)
	{
		final float scale = this.getResources().getDisplayMetrics().density;
		int pixels =  (int) (dps * scale + 0.5f);
		return pixels;
	}
	
	public HashMap<View, String> courseMap;
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
		courseMap = new HashMap<View, String>();
		
		((Button)findViewById(R.id.clearstoreddata)).setOnClickListener(new ClearListener(this));
		
		
		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
		
		CheckBox useexternalserver = (CheckBox)findViewById(R.id.externalserver);
		useexternalserver.setChecked(settings.getBoolean("useexternalserver", true));
		
		CompoundButton.OnCheckedChangeListener  externalserverchange = new CompoundButton.OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				
				editor.putBoolean("useexternalserver", arg1);
				editor.commit();
				
			}
			
		};
		
		useexternalserver.setOnCheckedChangeListener(externalserverchange);
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.settingslayout);
		
		String[] courses = settings.getString( "courses", "" ).split(";;;");
		if(settings.getString( "courses", "" ).compareTo("") != 0)
		{
			for(String course : courses)
			{
				TextView name = new TextView(this);
				name.setText(course);
				name.setTextSize( 20 );
				
				name.setPadding( dp(20) , dp(5) , dp(0), dp(5) );
				name.setTextAppearance(this, android.R.attr.textAppearanceMedium);
				
				layout.addView(name);
				
				
				
				Button b = new Button(this);
				b.setText("Delete This Course");
				
				courseMap.put(b, course);
				
				b.setOnClickListener( new OnClickListener()
				{

					@Override
					public void onClick(View arg0) {

						SharedPreferences coursesettings = getSharedPreferences(courseMap.get(arg0), 0);
						coursesettings.edit().clear().commit();
						
						
						SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						
						String newcourses = settings.getString( "courses", "" );
						
						newcourses = newcourses.replace(courseMap.get(arg0) + ";;;" , "");
						newcourses = newcourses.replace(courseMap.get(arg0) , "");
						
						editor.putString("courses", newcourses);
						editor.commit();
						
						startActivity(new Intent(arg0.getContext(), MainActivity.class));
					}
					
				});

				
				layout.addView(b);

			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			finish();
			return true;
			
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
