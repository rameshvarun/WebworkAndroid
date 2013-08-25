package com.varunramesh.webwork;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import com.varunramesh.webwork.MainActivity.AddCourseListener;



import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;

import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.content.DialogInterface;

public class FindServer extends SherlockActivity
{
	
	public static String SERVERLISTURL = "https://dl.dropbox.com/u/2727127/servers.html";
	
	class School
	{
		public String name;
		public String city;
		public String state;
		public String country;
		
		public String url;
		
		public School(String n, String c, String s, String cou, String u)
		{
			name = n;
			city = c;
			state = s;
			country = cou;
			url = u;
		}
	}
	
	HashMap<View, School> schools;
	
	
	class RequestTask extends AsyncTask<String, String, Document>
	{

		
		class SchoolListener implements OnClickListener
		{
			Context owner;
			
			public SchoolListener(Context own)
			{
				owner = own;
			}

			@Override
			public void onClick(View arg0) {
				
				Intent i = new Intent(owner, CourseSelect.class);
				
				School s = schools.get(arg0);
				
				i.putExtra("com.varunramesh.webwork.schoolname", s.name);
				i.putExtra("com.varunramesh.webwork.url", s.url);
				
				startActivity(i);
				
			}
		}
		
		

		@Override
		protected Document doInBackground(String... url)
		{
			Document doc = null;
			
			SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
			
			//First try to connect to server
			if(settings.getBoolean("useexternalserver", true))
			{
				try {
					doc = Jsoup.connect(SERVERLISTURL).get();
					
					//Save for offline access later
					FileOutputStream fos = openFileOutput("servers.html", Context.MODE_PRIVATE);
					fos.write(doc.html().getBytes());
					
					Log.v("Save for offline", getFilesDir().toString());
					fos.close();
					
					return doc;
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//Then try to use a cached server list from a prior download
			try
			{
				BufferedReader br = new BufferedReader(  new InputStreamReader( openFileInput( "servers.html" )  ) );
				
				String line = "";
				StringBuilder code = new StringBuilder();
				while((line=br.readLine()) != null)
				{
					code.append(line);
					code.append("\n");
				}
				
				doc = Jsoup.parse(code.toString());
				
				return doc;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			//Then try to use the default server list packaged with the application
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("html/servers.html")));
				
				String line = "";
				StringBuilder code = new StringBuilder();
				while((line=br.readLine()) != null)
				{
					code.append(line);
					code.append("\n");
				}
				
				doc = Jsoup.parse(code.toString());
				
				//doc = Jsoup.connect("http://forms.maa.org/r/webwork/ww_table.aspx").get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return doc;
		}
		
	    @Override
	    protected void onPostExecute(Document doc) {
	        super.onPostExecute(doc);
	        
	        schools = new HashMap<View, School>();
	        
	        findViewById(R.id.progressBar1).setVisibility(View.GONE);
	        
	        findViewById(R.id.textView3).setVisibility(View.GONE);
	        findViewById(R.id.textView4).setVisibility(View.GONE);
	        
	        
	        LinearLayout schoolslayout = (LinearLayout)findViewById(R.id.schoolslayout);
	        
	        SchoolListener l = new SchoolListener(schoolslayout.getContext());
	        
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
	        			School s = new School(row.getElementsByTag("td").get(0).ownText(),
	        					row.getElementsByTag("td").get(1).ownText(),
	        					row.getElementsByTag("td").get(2).ownText(),
	        					row.getElementsByTag("td").get(3).ownText(),
	        					row.getElementsByTag("td").get(4).getElementsByTag("a").get(0).attr("href")
	        					);
	        			
	        			//txt.append( row.getElementsByTag("td").get(0).ownText() );
	        			
	        			LinearLayout h = new LinearLayout(schoolslayout.getContext());
	        			h.setOrientation(LinearLayout.HORIZONTAL);
	        			
	        			LinearLayout v = new LinearLayout(schoolslayout.getContext());
	        			v.setOrientation(LinearLayout.VERTICAL);
	        			
	        			
	        			TextView t1 = new TextView(schoolslayout.getContext());
	        			t1.setText(s.name);
	        			t1.setTextSize(20);
	        			v.addView(t1);
	        			
	        			TextView t2 = new TextView(schoolslayout.getContext());
	        			t2.setText(s.city + ", " + s.state + " - " + s.country);
	        			t2.setTextSize(14);
	        			v.addView(t2);
	        			
	        			v.setPadding(10, 10, 10, 10);
	        			
	        			h.setClickable(true);
	        			h.setOnClickListener(l);
	        			
	        			h.setBackgroundResource(R.drawable.clickable);
	        			
	        			h.addView(v);
	        			
	        			schoolslayout.addView(h);
	        			
	        			View view = new View(schoolslayout.getContext());
	        			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
	        			view.setBackgroundColor(Color.LTGRAY);
	        			
	        			schoolslayout.addView(view);
	        			
	        			schools.put(h, s);
	        			
	        			
	        		}
	        		
	        	}

	        	//Empty item to pad out list to ensure that all schools are visible
    			LinearLayout h = new LinearLayout(schoolslayout.getContext());
    			h.setOrientation(LinearLayout.HORIZONTAL);
    			LinearLayout v = new LinearLayout(schoolslayout.getContext());
    			v.setOrientation(LinearLayout.VERTICAL);
    			TextView t1 = new TextView(schoolslayout.getContext());
    			t1.setText("");
    			t1.setTextSize(20);
    			v.addView(t1);
    			TextView t2 = new TextView(schoolslayout.getContext());
    			t2.setText("");
    			t2.setTextSize(14);
    			v.addView(t2);
    			v.setPadding(10, 10, 10, 10);
    			h.addView(v);
    			schoolslayout.addView(h);
		        
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        	
	        	//txt.setText(e.toString());
	        }
	        
	        //Do anything with response..
	    }
		
	}
	
	class SearchListener implements SearchView.OnQueryTextListener
	{

		public SearchListener()
		{
			
		}
		
		public void find(String text)
		{
			if(schools != null)
			{

				for(Entry<View, School> s : schools.entrySet())
				{
					if(s.getValue().name.toLowerCase().startsWith(text.toLowerCase()))
					{
						ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
						
						scroll.scrollTo(0, s.getKey().getTop());
						
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
			
			builder.setTitle("Manually Enter School Server");
			
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
			t2.setText("Server URL (Should start with 'https://' or 'http://' and end with '/webwork2/'):");
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
		        	   if( !(surl.getText().toString().endsWith("webwork2") || surl.getText().toString().endsWith("webwork2/") ) )
		        	   {
		        		   surl.setError("The url must end in /webwork2/");
		        		   return;
		        	   }
		        	   
		        	   Intent i = new Intent(owner, CourseSelect.class);
		        	   
		        	   i.putExtra("com.varunramesh.webwork.schoolname", sname.getText().toString());
		        	   i.putExtra("com.varunramesh.webwork.url", surl.getText().toString());
		        	   
		        	   startActivity(i);
					
				}
			};
			
			b.setOnClickListener(golisten);
		        
			/*builder.setPositiveButton("Go", 
		       });*/
			
			AlertDialog dialog = builder.create();
			dialog.show();
			
			//
			//
			
			//
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_server);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		

		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
		findViewById(R.id.manualentry).setOnClickListener(new ManualListener(this));
		
		new RequestTask().execute("http://forms.maa.org/r/webwork/ww_table.aspx");
		
	}
	

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_find_server, menu);
		
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
			//NavUtils.navigateUpFromSameTask(this);
			
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

}
