package com.varunramesh.webwork;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import com.varunramesh.webwork.FindServer.ManualListener;
import com.varunramesh.webwork.FindServer.RequestTask;
import com.varunramesh.webwork.FindServer.School;
import com.varunramesh.webwork.FindServer.SearchListener;
import com.varunramesh.webwork.FindServer.RequestTask.SchoolListener;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.support.v4.app.NavUtils;

public class CourseSelect extends SherlockActivity {
	
	HashMap<View, String[]> courses;
	
	class RequestTask extends AsyncTask<String, String, Document>
	{
		
		class CourseListener implements OnClickListener
		{
			Context owner;
			
			public CourseListener(Context own)
			{
				owner = own;
			}

			@Override
			public void onClick(View arg0) {
				
				Intent i = new Intent(owner, LoginActivity.class);
				
				
				String[] info = courses.get(arg0);
				
				Bundle b = getIntent().getExtras();
				
				i.putExtra("com.varunramesh.webwork.schoolname", ((TextView)findViewById(R.id.schoolname)).getText());
				i.putExtra("com.varunramesh.webwork.coursename", info[0]);
				i.putExtra("com.varunramesh.webwork.url", b.getString("com.varunramesh.webwork.url") + info[1].replace("/webwork2/", ""));
				
				startActivity(i);
				
			}
		}

		@Override
		protected Document doInBackground(String... url)
		{
			Document doc = null;
			
			try {
				doc = Jsoup.connect(url[0]).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			}
			
			return doc;
		}
		
	    @Override
	    protected void onPostExecute(Document doc) {
	        super.onPostExecute(doc);
	        
	        
	        if(doc == null)
	        {
				AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.progressBar1).getContext());
				builder.setTitle("Connection Error");
				
				builder.setMessage("Could not connect to the WeBWorK server.");
				builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   finish();
	                   }
	               });
				
				builder.create().show();
	        }
	        
	        courses = new HashMap<View, String[]>();
	        
	        findViewById(R.id.progressBar1).setVisibility(View.GONE);
	        
	        LinearLayout courselayout = (LinearLayout)findViewById(R.id.courseslayout);
	        
	       	CourseListener l = new CourseListener(courselayout.getContext());
	        
	        try
	        {
	        	//If they use the more standard <li> tag system
	        	if( doc.getElementsByTag("li").size() > 1 )
	        	{
		        	for(Element row : doc.getElementsByTag("li"))
		        	{
		        		if(row.getElementsByTag("a").size() > 0)
		        		{
	
			        		String name = row.getElementsByTag("a").get(0).ownText();
			        		String url = row.getElementsByTag("a").get(0).attr("href");
			        		
			        		if(name.compareTo( "Courses" ) == 0)
			        		{
			        			break;
			        		}
			        		
		        			LinearLayout h = new LinearLayout( courselayout.getContext());
		        			h.setOrientation(LinearLayout.HORIZONTAL);
		        			
		        			LinearLayout v = new LinearLayout( courselayout.getContext());
		        			v.setOrientation(LinearLayout.VERTICAL);
		        			
		        			
		        			TextView t1 = new TextView( courselayout.getContext());
		        			t1.setText(name);
		        			t1.setTextSize(20);
		        			v.addView(t1);
		        			
		        			v.setPadding(15, 15, 15, 15);
		        			
		        			h.setClickable(true);
		        			h.setOnClickListener(l);
		        			
		        			String[] info = {name, url};
		        			courses.put(h, info);
		        			
		        			h.setBackgroundResource(R.drawable.clickable);
		        			
		        			h.addView(v);
		        			
		        			courselayout.addView(h);
		        			
		        			View view = new View( courselayout.getContext());
		        			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
		        			view.setBackgroundColor(Color.LTGRAY);
		        			
		        			courselayout.addView(view);
		        		}
		        	}
	        	}
	        	
	        	//If they use a table based two-column categorize system, like James Madison University
	        	//https://webwork.cit.jmu.edu/webwork2/courses/
	        	//If they use the more standard <li> tag system
	        	if( doc.getElementsByTag("tr").size() > 1 )
	        	{
		        	for(Element row : doc.getElementsByTag("tr"))
		        	{
		        		if(row.getElementsByTag("a").size() > 0)
		        		{
	
			        		String name = row.getElementsByTag("a").get(0).ownText();
			        		String url = row.getElementsByTag("a").get(0).attr("href");
			        		
	        				String otherinfo = "";
	        				
	        				for(Element td : row.getElementsByTag("td"))
	        				{
	        					otherinfo += td.ownText();
	        				}
	        				
			        		
			        		if(name.compareTo( "Courses" ) == 0)
			        		{
			        			break;
			        		}
			        		
		        			LinearLayout h = new LinearLayout( courselayout.getContext());
		        			h.setOrientation(LinearLayout.HORIZONTAL);
		        			
		        			LinearLayout v = new LinearLayout( courselayout.getContext());
		        			v.setOrientation(LinearLayout.VERTICAL);
		        			
		        			
		        			TextView t1 = new TextView( courselayout.getContext());
		        			t1.setText(name);
		        			t1.setTextSize(20);
		        			v.addView(t1);
		        			
		        			TextView t2 = new TextView( courselayout.getContext());
		        			t2.setText(otherinfo);
		        			t2.setTextSize(14);
		        			v.addView(t2);
		        			
		        			v.setPadding(15, 15, 15, 15);
		        			
		        			h.setClickable(true);
		        			h.setOnClickListener(l);
		        			
		        			String[] info = {name, url};
		        			courses.put(h, info);
		        			
		        			h.setBackgroundResource(R.drawable.clickable);
		        			
		        			h.addView(v);
		        			
		        			courselayout.addView(h);
		        			
		        			View view = new View( courselayout.getContext());
		        			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
		        			view.setBackgroundColor(Color.LTGRAY);
		        			
		        			courselayout.addView(view);
		        		}
		        		else
		        		{
		        			if(row.getElementsByTag("b").size() > 0 && row.getElementsByTag("b").get(0).ownText().trim().length() > 0)
		        			{
		        				String name = row.getElementsByTag("b").get(0).ownText();
		        				

		        				LinearLayout h = new LinearLayout( courselayout.getContext());
			        			h.setOrientation(LinearLayout.HORIZONTAL);
			        			
			        			LinearLayout v = new LinearLayout( courselayout.getContext());
			        			v.setOrientation(LinearLayout.VERTICAL);
			        			
			        			
			        			TextView t1 = new TextView( courselayout.getContext());
			        			t1.setText(name);
			        			t1.setTextSize(30);
			        			

			        			
			        			v.addView(t1);
			        			
			        			v.setPadding(15, 15, 15, 15);

			        			h.addView(v);
			        			
			        			courselayout.addView(h);
			        			
			        			View view = new View( courselayout.getContext());
			        			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
			        			view.setBackgroundColor(Color.LTGRAY);
			        			
			        			courselayout.addView(view);
		        				
		        			}
		        		}
		        	}
	        	}
	        	
		        
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        
	        
	        //Pad out list to make sure all courses are visible
        	//Empty item to pad out list
			LinearLayout h = new LinearLayout(courselayout.getContext());
			h.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout v = new LinearLayout(courselayout.getContext());
			v.setOrientation(LinearLayout.VERTICAL);
			TextView t1 = new TextView(courselayout.getContext());
			t1.setText("");
			t1.setTextSize(20);
			v.addView(t1);
			TextView t2 = new TextView(courselayout.getContext());
			t2.setText("");
			t2.setTextSize(14);
			v.addView(t2);
			v.setPadding(10, 10, 10, 10);
			h.addView(v);
			courselayout.addView(h);
	    }
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_select);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class SearchListener implements SearchView.OnQueryTextListener
	{

		public SearchListener()
		{
			
		}
		
		public void find(String text)
		{
			if(courses != null)
			{
				for(Entry<View, String[]> c : courses.entrySet())
				{
					if(c.getValue()[0].toLowerCase().startsWith(text.toLowerCase()))
					{
						ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
						
						scroll.scrollTo(0, c.getKey().getTop());
						
						break;
					}
				}
			}
		}
		
		@Override
		public boolean onQueryTextChange(String arg0) {
			// TODO Auto-generated method stub
			
			find(arg0);
			
			return false;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			// TODO Auto-generated method stub
			
			find(query);
			
			return false;
		}

		
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_course_select, menu);
		
		if(Build.VERSION.SDK_INT > 10)
		{
			SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
			searchView.setOnQueryTextListener(new SearchListener());
		}
		else
		{
			menu.findItem(R.id.menu_search).setVisible(false);
		}
		
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		
		((TextView)findViewById(R.id.schoolname)).setText(b.getString("com.varunramesh.webwork.schoolname"));
		
		findViewById(R.id.manualentry).setOnClickListener(new ManualListener(this));
		
		new RequestTask().execute(b.getString("com.varunramesh.webwork.url"));
		
		
		
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
			
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			break;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class ManualListener implements OnClickListener
	{
		Context owner;
		
		public ManualListener(Context own)
		{
			owner = own;
		}
		
		EditText sname;
		EditText surl;

		@Override
		public void onClick(View arg0) {
			
			

			AlertDialog.Builder builder = new AlertDialog.Builder(owner);
			
			builder.setTitle("Manually Enter Course Login Page");
			
			LinearLayout v = new LinearLayout(owner);
			v.setOrientation(LinearLayout.VERTICAL);
			
			
			
			TextView t1 = new TextView(owner);
			t1.setText("School Name:");
			t1.setTextSize(20);
			t1.setPadding(10, 10, 10, 10);
			v.addView(t1);
			
			sname = new EditText(owner);
			v.addView(sname);
			
			
			TextView t2 = new TextView(owner);
			t2.setText("Course Login Page URL (Should start with 'https://' or 'http://' and end with '/webwork2/course_name/'):");
			t2.setTextSize(20);
			t2.setPadding(10, 10, 10, 10);
			v.addView(t2);
			
			surl = new EditText(owner);
			v.addView(surl);
			
			
			builder.setView(v);
			
			Button b = new Button(owner);
			b.setText("Go");
			v.addView(b);
			
			OnClickListener golisten = new OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Intent i = new Intent(owner, CourseSelect.class);
		        	   
		        	   i.putExtra("com.varunramesh.webwork.schoolname", sname.getText().toString());
		        	   i.putExtra("com.varunramesh.webwork.url", surl.getText().toString());
		        	   
		        	   if(sname.getText().toString().trim().length() == 0)
		        	   {
		        		   sname.setError("This field is required");
		        		   return;
		        	   }
		        	   if(surl.getText().toString().trim().length() == 0)
		        	   {
		        		   sname.setError("This field is required");
		        		   return;
		        	   }
		        	   
		        	   startActivity(i);
		           }

				@Override
				public void onClick(View arg0) {

		        	   
		        	   if(sname.getText().toString().trim().length() == 0)
		        	   {
		        		   sname.setError("This field is required");
		        		   return;
		        	   }
		        	   if(surl.getText().toString().trim().length() == 0)
		        	   {
		        		   surl.setError("This field is required");
		        		   return;
		        	   }
		        	   
						Intent i = new Intent(owner, LoginActivity.class);
												
						Bundle b = getIntent().getExtras();
						
						i.putExtra("com.varunramesh.webwork.schoolname", sname.getText().toString());
						i.putExtra("com.varunramesh.webwork.coursename", surl.getText().toString().substring(surl.getText().toString().indexOf("webwork2")  + "webwork2".length()).replace("/", "")  );
						i.putExtra("com.varunramesh.webwork.url", surl.getText().toString());
						
						startActivity(i);
					
				}
			};
			
			b.setOnClickListener(golisten);

			AlertDialog dialog = builder.create();
			dialog.show();
			
		}
	}

}
