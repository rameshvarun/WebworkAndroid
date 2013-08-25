package com.varunramesh.webwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.varunramesh.webwork.CourseSelect.RequestTask;
import com.varunramesh.webwork.CourseSelect.RequestTask.CourseListener;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CourseView extends SherlockActivity {
	
	//Global string in which to store the url of the problem set (Once it has been calculated)
	//This is so that teh Go To Webpage menu item works
	public static String gotourl = "";
	
	
		
	class RequestTask extends AsyncTask<String, String, Document>
	{
		boolean online;
		
		class ProblemListener implements OnClickListener
		{
			Context owner;
			
			public ProblemListener(Context own)
			{
				owner = own;
			}

			@Override
			public void onClick(View arg0) {
				
				Intent i = new Intent(owner, ProblemListActivity.class);
				
				Bundle b = getIntent().getExtras();
				i.putExtra("com.varunramesh.webwork.coursename", b.getString("com.varunramesh.webwork.coursename"));
				
				i.putExtra("com.varunramesh.webwork.url", urls.get(arg0) );
				
				i.putExtra("com.varunramesh.webwork.setname", setnames.get(arg0) );
				
				startActivity(i);
				
			}
		}
		
		String courseurl;
		
		@Override
		protected Document doInBackground(String... params)
		{
			SharedPreferences coursesettings = getSharedPreferences(params[0] , 0);
			
			Document doc = null;
			
			courseurl = coursesettings.getString("url", "");
			
			gotourl = courseurl;
			
			Connection c = Jsoup.connect(courseurl);
			
			
			c.data("user", coursesettings.getString("user", "") );
			c.data("passwd", coursesettings.getString("password", "") );
			
			
			Log.v("Test", courseurl);
			
			
			try {
				doc = c.post();
				
				online = true;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try
				{
					Bundle b = getIntent().getExtras();
					BufferedReader br = new BufferedReader(  new InputStreamReader( openFileInput(b.getString("com.varunramesh.webwork.coursename"))  ) );
					
					String line = "";
					StringBuilder code = new StringBuilder();
					while((line=br.readLine()) != null)
					{
						code.append(line);
						code.append("\n");
					}
					
					doc = Jsoup.parse(code.toString());
					
					online = false;
					
					return doc;
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
				

				
				return null;
			}
			
			return doc;
		}
		
		HashMap<View, String> urls;
		HashMap<View, String> setnames;
		
	    @Override
	    protected void onPostExecute(Document doc) {
	        super.onPostExecute(doc);
	        
	        if(doc == null)
	        {
				AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.problemsets).getContext());
				builder.setTitle("Connection Error");
				
				builder.setMessage("Could not connect to the WeBWorK server.");
				builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   finish();
	                   }
	               });
				
				builder.create().show();
	        }
	        else
	        {
	        
		        //Store offline copy
		        Bundle b = getIntent().getExtras();
		        try {
					FileOutputStream fos = openFileOutput(b.getString("com.varunramesh.webwork.coursename"), Context.MODE_PRIVATE);
					
					fos.write(doc.html().getBytes());
					
					Log.v("Save for offline", getFilesDir().toString());
					
					fos.close();
					
					
					
					
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        

	        
	        
	        urls = new HashMap<View, String>();
	        setnames = new HashMap<View, String>();
	        
	        findViewById(R.id.progressBar1).setVisibility(View.GONE);
	        
	        LinearLayout setlayout = (LinearLayout)findViewById(R.id.problemsets);
	        
	        ProblemListener l = new ProblemListener(setlayout.getContext());
	        
	        
	        if(online == false)
	        {
	        	Toast toast = Toast.makeText(setlayout.getContext(), "You are offline. Problems are read-only without connection.", Toast.LENGTH_LONG);
	        	toast.show();
	        }
	        
	        
	        try
	        {
	        	boolean first = false;
	        	for(Element row : doc.getElementsByTag("tr"))
	        	{
	        		if(first == false)
	        		{
	        			first = true;
	        		}	
	        		else
	        		{
	        			Elements td = row.getElementsByTag("td");
	        			
	        			String name, url, status;
	        			
	        			/* Different versions of WeBWork Organize their tables differently
	        			 * Version 2.4.9 - uses two columns, first one has radio button AND link to course, second has status
	        			 * Other version - uses three colums - radio button, link, status
	        			 */
	        			if(td.size() < 3)
	        			{
	        				Log.v("Debug", "Less than 3 columns");
	        				
	        				name = td.get(0).getElementsByTag("a").get(0).ownText();
		        			url = td.get(0).getElementsByTag("a").get(0).attr("href");
		        			status = td.get(1).ownText().toString();
	        			}
	        			else
	        			{
	        				Log.v("Debug", "3 or greater columns");
		        			
		        			name = td.get(1).getElementsByTag("a").get(0).ownText();
		        			url = td.get(1).getElementsByTag("a").get(0).attr("href");
		        			status = td.get(2).ownText().toString();
	        			}
	        			
	        			LinearLayout h = new LinearLayout(setlayout.getContext());
	        			h.setOrientation(LinearLayout.HORIZONTAL);
	        			
	        			LinearLayout v = new LinearLayout(setlayout.getContext());
	        			v.setOrientation(LinearLayout.VERTICAL);
	        			
	        			
	        			TextView t1 = new TextView(setlayout.getContext());
	        			t1.setText(name);
	        			t1.setTextSize(20);
	        			v.addView(t1);
	        			
	        			
	        			
	        			TextView t2 = new TextView(setlayout.getContext());
	        			t2.setText(status);
	        			t2.setTextSize(14);
	        			v.addView(t2);
	        			
	        			v.setPadding(10, 10, 10, 10);
	        			
	        			h.setClickable(true);
	        			
	        			h.setBackgroundResource(R.drawable.clickable);
	        			
	        			urls.put(h, courseurl + url.replace("/webwork2/", ""));
	        			setnames.put(h, name);
	        			
	        			h.addView(v);
	        			
	        			h.setOnClickListener(l);
	        			
	        			if(status.contains("closed"))
	        			{
	        				//t1.setTextColor(Color.LTGRAY);
	        				//t2.setTextColor(Color.LTGRAY);
	        			}
	        			
	        			setlayout.addView(h);
	        			
	        			View view = new View(setlayout.getContext());
	        			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
	        			view.setBackgroundColor(Color.LTGRAY);
	        			
	        			setlayout.addView(view);
	        			
	        			
	        		}
	        	}
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        
	    }
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_view);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle b = getIntent().getExtras();
		
		getSupportActionBar().setTitle(b.getString("com.varunramesh.webwork.coursename"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_course_view, menu);
		return true;
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
		
		Bundle b = getIntent().getExtras();
		
		((TextView)findViewById(R.id.courseviewname)).setText(b.getString("com.varunramesh.webwork.coursename"));
		
		new RequestTask().execute(b.getString("com.varunramesh.webwork.coursename"));
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
		case R.id.gotourl:

			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gotourl));
			
			startActivity(browserIntent);
			break;
			
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
