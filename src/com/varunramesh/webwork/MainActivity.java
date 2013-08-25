package com.varunramesh.webwork;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.varunramesh.webwork.FindServer.RequestTask.SchoolListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends SherlockActivity {
	
	public static final String PREFS_NAME = "WEBWORKPREFS";

	class AddCourseListener implements OnClickListener
	{
		Activity owner;
		public AddCourseListener(Activity own)
		{
			owner = own;
		}

		@Override
		public void onClick(View arg0) {
			startActivity(new Intent(owner, FindServer.class));
			
		}
	}
	
	class CourseListener implements OnClickListener
	{
		Context owner;
		
		public CourseListener(Context own)
		{
			owner = own;
		}

		@Override
		public void onClick(View arg0) {
			
			Intent i = new Intent(owner, CourseView.class);
			
			i.putExtra("com.varunramesh.webwork.coursename", courseMap.get(arg0));
			
			startActivity(i);
			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
	}
	
	HashMap<View, String> courseMap;
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
		courseMap = new HashMap<View, String>();
		
		CourseListener l = new CourseListener(this);
		
		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
		String[] courses = settings.getString( "courses", "" ).split(";;;");
		
		Log.v("MainACtivity", String.valueOf( courses.length ) );
		
		LinearLayout courselist = (LinearLayout)findViewById(R.id.maincourseslist);
		
		if(settings.getString( "courses", "" ).compareTo("") != 0)
		{
			findViewById(R.id.youhaventadded).setVisibility(View.GONE);
			findViewById(R.id.addcourse).setVisibility(View.GONE);
			
			for(String course : courses)
			{
				SharedPreferences coursesettings = getSharedPreferences(course , 0);
				
				LinearLayout h = new LinearLayout( courselist.getContext());
				h.setOrientation(LinearLayout.HORIZONTAL);
				
				LinearLayout v = new LinearLayout( courselist.getContext());
				v.setOrientation(LinearLayout.VERTICAL);
				
				TextView t1 = new TextView( courselist.getContext());
				t1.setText(coursesettings.getString("name", ""));
				t1.setTextSize(20);
				v.addView(t1);
				
				TextView t2 = new TextView(courselist.getContext());
				t2.setText(coursesettings.getString("school", ""));
				t2.setTextSize(14);
				v.addView(t2);
				
				v.setPadding(15, 15, 15, 15);
				
				h.setClickable(true);
				
				courseMap.put(h, course);
				
				h.setOnClickListener(l);
				
				h.setBackgroundResource(R.drawable.clickable);
				
				h.addView(v);
				
				courselist.addView(h);
				
				View view = new View( courselist.getContext());
				view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
				view.setBackgroundColor(Color.LTGRAY);
				
				courselist.addView(view);
			}
			
		}
		

		

		/*Intent i = new Intent(this, CourseSelect.class);
		i.putExtra("com.varunramesh.webwork.schoolname", "Hamilton High School");
		i.putExtra("com.varunramesh.webwork.url", "http://webwork.tuhsd.k12.az.us/webwork2/");
		startActivity(i);*/
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		
		
		((Button)findViewById(R.id.addcourse)).setOnClickListener(new AddCourseListener(this));
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addcourse:
			startActivity(new Intent(this, FindServer.class));
			
			return true;
			
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			break;
			
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
