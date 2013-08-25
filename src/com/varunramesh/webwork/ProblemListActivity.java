package com.varunramesh.webwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import com.varunramesh.webwork.CourseView.RequestTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;


import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * An activity representing a list of Problems. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ProblemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ProblemListFragment} and the item details (if present) is a
 * {@link ProblemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ProblemListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ProblemListActivity extends SherlockFragmentActivity implements
		ProblemListFragment.Callbacks {
	
	//Global string in which to store the url of the problem set (Once it has been calculated)
	//This is so that teh Go To Webpage menu item works
	public static String gotourl = "";
	
	
	public static String pdfurl = "";
	
	public static String emailurl = "";
	
	public static boolean haveWhiteSpace(final String ss){
		if(ss != null){
		    for(int i = 0; i < ss.length(); i++){
		        if(Character.isWhitespace(ss.charAt(i))){
		            return true;
		        }
		    }
		}
		return false;

		}

	class RequestTask extends AsyncTask<String, String, Document>
	{
		
		boolean online;

		/**
		 * Get the current problem set. Shared connection ProblemContent.c.
		 * 
		 * params[0] is the name of the course that this problem set is a part of
		 * params[1] is the url of the problem set
		 * params[2] is the name of the problem set
		 */
		@Override
		protected Document doInBackground(String... params)
		{
			//Read stored data for teh course
			SharedPreferences coursesettings = getSharedPreferences(params[0] , 0);
			
			Document doc = null;
			
			//Get course url
			ProblemContent.c = Jsoup.connect(coursesettings.getString("url", ""));
			
			//Get login credentials for course
			ProblemContent.c.data("user", coursesettings.getString("user", "") );
			ProblemContent.c.data("passwd", coursesettings.getString("password", "") );
			
			
			
			try {
				
				//Login to course
				Document doc1 = ProblemContent.c.post();
				
				//Get Cookies (To Find Session ID)
				//Map<String, String> cookies = response.cookies();
				
				//Log.v("ProblemSetLoading", cookies.keySet().toString());
				//Log.v("ProblemSetLoading", cookies.values().toString());
				
				String key = doc1.getElementsByAttributeValue("name", "key").get(0).attr("value");
				
				String href = doc1.getElementsMatchingOwnText(params[2]).attr("href");
				
				Log.v("href", href);
				Log.v("params[2]", params[2]);
				
				String coursename = coursesettings.getString("name", "");
				
				if( !href.contains(params[2]) )
				{
					Log.v("ProblemListActivity", "The course name is not in the URL.");
					href = href.substring( href.indexOf( coursename ) + coursename.length()  );
					
				}
				else
				{
					Log.v("ProblemListActivity", "The course name is in the URL.");
					href = href.substring(href.indexOf(params[2]));
					
				}
				
				Log.v("href",href );
				
				String seturl = coursesettings.getString("url", "") + href;
				
				Log.v("seturl",seturl );
				
				Log.v("Elements containing url", String.valueOf( doc1.getElementsMatchingOwnText(params[2]) ) );
				
				//Get page of problem set
				Connection c = Jsoup.connect( seturl );
				gotourl = seturl;
				
				
				ProblemContent.u = seturl;
				
				
				Log.v("ProblemSetLoading", key);
				Log.v("ProblemSetLoading", seturl);
				
				//for (Map.Entry<String, String> cookie : cookies.entrySet()) {
				//     c.cookie(cookie.getKey(), cookie.getValue());     
				// }
				
				//c.data("key", key);
				
				
				
				doc = c.get();
				online = true;
				
		        if(doc.getElementsContainingOwnText("Download a hardcopy").size() > 0)
		        {
		        	String pdf = doc.getElementsContainingOwnText("Download a hardcopy").get(0).attr("href");
		        	
		        	pdfurl = coursesettings.getString("url", "") + pdf.substring( pdf.indexOf("hardcopy") );
		        	
		        }
		        
		        if(doc.getElementsByAttributeValue("name", "user").size() > 0 && doc.getElementsByAttributeValue("name", "effectiveUser").size() > 0)
		        {
			        String user = doc.getElementsByAttributeValue("name", "user").get(0).attr("value");
			        String effectiveUser = doc.getElementsByAttributeValue("name", "effectiveUser").get(0).attr("value");
			        
			        emailurl = coursesettings.getString("url", "") + "feedback/?effectiveUser=" + effectiveUser + "&user=" + user + "&key=" + key;
		        }
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try
				{
					Bundle b = getIntent().getExtras();
					BufferedReader br = new BufferedReader(  new InputStreamReader( openFileInput(
							b.getString("com.varunramesh.webwork.coursename") +
							b.getString("com.varunramesh.webwork.setname")
							)  ) );
					
					String line = "";
					StringBuilder code = new StringBuilder();
					while((line=br.readLine()) != null)
					{
						code.append(line);
						code.append("\n");
					}
					
					doc = Jsoup.parse(code.toString());
					
					ProblemContent.u = doc.getElementsByTag("appofflinedata").attr("url");
					
					online = false;
					
					return doc;
				}
				catch(Exception e1)
				{
					
				}
				
				return null;
			}
			
			return doc;
		}
		
	    @Override
	    protected void onPostExecute(Document doc) {
	        super.onPostExecute(doc);

	        
	        ProblemContent.ITEM_MAP.clear();
	        ProblemContent.ITEMS.clear();
	        
	        Bundle b = getIntent().getExtras();
	        getSupportActionBar().setTitle(b.getString("com.varunramesh.webwork.setname"));
	        
	        if(doc == null)
	        {
				AlertDialog.Builder builder = new AlertDialog.Builder( ProblemContent.a );
				builder.setTitle("Connection Error");
				
				builder.setMessage("Could not connect to the WeBWorK server.");
				builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   finish();
	                   }
	               });
				
				builder.create().show();
				
				return;
	        }
	        else
	        {
		        //Store offline copy
		        try {
					FileOutputStream fos = openFileOutput(b.getString("com.varunramesh.webwork.coursename") + b.getString("com.varunramesh.webwork.setname"), Context.MODE_PRIVATE);
					
					doc.appendElement("appofflinedata").attr("url", ProblemContent.u);
					
					
					fos.write(doc.html().getBytes());
					Log.v("Save for offline", getFilesDir().toString());
					fos.close();
					
					
					
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        
	        Log.v("ProblemList", String.valueOf( doc.getElementsByTag("input").size() ) );
	        
	        if(online == false)
	        {
	        	Toast toast = Toast.makeText(ProblemContent.a, "You are offline. Problems are read-only without connection.", Toast.LENGTH_LONG);
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
	        			
	        			String name = row.getElementsByTag("td").get(0).getElementsByTag("a").get(0).ownText();
	        			String url = row.getElementsByTag("td").get(0).getElementsByTag("a").get(0).attr("href");
	        			
	        			String attempts = row.getElementsByTag("td").get(1).ownText();
	        			String remaining = row.getElementsByTag("td").get(2).ownText();
	        			String worth = row.getElementsByTag("td").get(3).ownText();
	        			String score = row.getElementsByTag("td").get(4).ownText();
	        			
	        			String href = ProblemContent.u.substring(0, ProblemContent.u.indexOf("/webwork2/")) + url;
	    				
	    				
	        			
	        			ProblemContent.addItem( new ProblemContent.Problem(name, name, href, attempts, remaining, worth, score) );
	        			
	        			
	        			
	        		}
	        	}
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        
	        ProblemListFragment frag = ((ProblemListFragment) getSupportFragmentManager().findFragmentById(R.id.problem_list));
	        
			int layout = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) ?
		            android.R.layout.simple_list_item_activated_1 :
		            R.layout.simple_list_item_checked;

			
	        
			// TODO: replace with a real list adapter.
			ArrayAdapter<ProblemContent.Problem> adapter = new ArrayAdapter<ProblemContent.Problem>(ProblemContent.a,
					layout,
					 ProblemContent.ITEMS);
	        
	        frag.setListAdapter(adapter);
	        
	    }
		
	}

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem_list);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		

		ProblemContent.ITEM_MAP.clear();
        ProblemContent.ITEMS.clear();

		if (findViewById(R.id.problem_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ProblemListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.problem_list))
					.setActivateOnItemClick(true);
			
			
		}


		Bundle b = getIntent().getExtras();
		
		ProblemContent.a = this;
		
		new RequestTask().execute(
				b.getString("com.varunramesh.webwork.coursename"),
				b.getString("com.varunramesh.webwork.url"),
				b.getString("com.varunramesh.webwork.setname")
				);
		
		// TODO: If exposing deep links into your app, handle intents here.
		
		ProblemContent.listactivity = this;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_problem_list, menu);
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
			
		case R.id.menu_pdf:
			Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfurl));
			startActivity(browserIntent2);
			break;
			
		case R.id.menu_emailinstructor:
			Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(emailurl));
			startActivity(browserIntent3);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link ProblemListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ProblemDetailFragment.ARG_ITEM_ID, id);
			ProblemDetailFragment fragment = new ProblemDetailFragment(true);
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.problem_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ProblemDetailActivity.class);
			detailIntent.putExtra(ProblemDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
