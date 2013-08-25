package com.varunramesh.webwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A fragment representing a single Problem detail screen. This fragment is
 * either contained in a {@link ProblemListActivity} in two-pane mode (on
 * tablets) or a {@link ProblemDetailActivity} on handsets.
 */
public class ProblemDetailFragment extends SherlockFragment {
	
	boolean tablet;
	
	public class WebAppInterface
	{
	    Context mContext;

	    /** Instantiate the interface and set the context */
	    WebAppInterface(Context c) {
	        mContext = c;
	    }

	    /** Show a toast from the web page */
	    @JavascriptInterface
	    public void setData(String key, String value) {
	    	
	    	ProblemContent.Pair p = new ProblemContent.Pair();
	    	p.first = key;
	    	p.second = value;
	    	
	        ProblemContent.DATA.add(p);
	        
	        Log.v(key, ProblemContent.DATA.toString());
	    }
	    
	    @JavascriptInterface
	    public void complete() {
	    	
	    	
	    	
	        ProblemContent.complete = true;
	    }
	}
	
	class RequestTask extends AsyncTask<String, String, Document>
	{

		String problemurl;
		
		boolean online;

		@Override
		protected Document doInBackground(String... params)
		{
			problemurl = params[0];
			
			if(params[1].compareTo("get") == 0)
			{
			
				Document doc = null;
				
				try {
					
					
					doc = Jsoup.connect(params[0]).get();
					online = true;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					try
					{
						Bundle b = getActivity().getIntent().getExtras();
						BufferedReader br = new BufferedReader(  new InputStreamReader( getActivity().openFileInput(
								String.valueOf(problemurl.hashCode())
								)  ) );
						
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
			else
			{
				
				while(ProblemContent.complete == false)
				{
					
				}
				
				Document doc = null;
				
				try {
					
					
					Connection c = Jsoup.connect(params[0]);
					
					c.data(params[1], params[2]);
					
					Log.v("Data", ProblemContent.DATA.toString());

					for(ProblemContent.Pair p : ProblemContent.DATA)
					{
						c.data(p.first, p.second);
					}
					
					doc = c.post();
					
					online = true;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					

					
					return null;
				}
				
				return doc;
			}
		}
		
		
		
	    @Override
	    protected void onPostExecute(Document doc) {
	        super.onPostExecute(doc);
	        
	        if(doc == null)
	        {
	        	
	        }
	        else
	        {
		        //Store offline copy
		        try {
					FileOutputStream fos = getActivity().openFileOutput( String.valueOf(problemurl.hashCode()), Context.MODE_PRIVATE);

					fos.write(doc.html().getBytes());
					Log.v("Save for offline", getActivity().getFilesDir().toString());
					fos.close();
					
					
					
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        
	        if(online == false)
	        {
	        	Toast toast = Toast.makeText(ProblemContent.a, "You are offline. Problems are read-only without connection.", Toast.LENGTH_LONG);
	        	toast.show();
	        }
	        
	        
	        try
	        {
	        	//Element body = doc.getElementsByClass("Body").get(0);
	        	
	        	Element body = doc.getElementById("content");
	        	//body.attr("style", "max-width: 500px;");
	        	
	        	//Rempve extraneous elements
	        	HTMLUtils.removeId(body, "site-navigation");
	        	HTMLUtils.removeId(body, "footer");
	        	
	        	HTMLUtils.removeAllClass(body, "Message");
	        	HTMLUtils.removeAllClass(body, "for-broken-browsers");
	        	HTMLUtils.removeAllClass(body, "problemFooter");

	        	HTMLUtils.removeAllClass(body, "Nav");
	        	
	        	HTMLUtils.removeTag(body, "hr");
	        	HTMLUtils.removeTag(body, "span");
	        	
	        	/*if(body.getElementsByAttributeValue("name", "feedbackForm").size() > 0)
	        	{
	        		body.getElementsByAttributeValue("name", "feedbackForm").get(0).remove();
	        	}*/
	        	HTMLUtils.removeAttributeValue(body, "name", "feedbackForm");
	        	
	        	/*if(body.getElementsByAttributeValue("name", "previewAnswers").size() > 0)
	        	{
	        		body.getElementsByAttributeValue("name", "previewAnswers").get(0).remove();
	        	}*/
	        	HTMLUtils.removeAttributeValue(body, "name", "previewAnswers");
	        	
	        	if(body.getElementsByAttributeValue("name", "checkAnswers").size() > 0)
	        	{
	        		body.getElementsByAttributeValue("name", "checkAnswers").get(0).remove();
	        		isOpen = true;
	        	}
	        	
	        	if(body.getElementsByAttributeValue("name", "submitAnswers").size() > 0)
	        	{
	        		body.getElementsByAttributeValue("name", "submitAnswers").get(0).remove();
	        		isOpen = false;
	        	}
	        	
	        	HTMLUtils.removeTag(body, "form", 1);
	        	
	        	String script = "<script>" + ProblemContent.readRawTextFile(ProblemContent.webview.getContext(), R.raw.script) + "</script>";
	        	
	        	String html = "<html><body>";
	        	
	        	html += script;
	        	
	        	html += body.html();
	        	
	        	html += "</body></html>";
	        	
	        	ProblemContent.webview.loadDataWithBaseURL(mItem.url, html, "text/html", null, null);
	        	
	        	
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        
	        rootView.findViewById(R.id.progressBar1).setVisibility(View.GONE);
	        
	    }
			
	}
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private ProblemContent.Problem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	
	public ProblemDetailFragment() {
		tablet = false;
	}
	
	public ProblemDetailFragment(boolean isTablet) {
		tablet = isTablet;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = ProblemContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}
	
	public void preview()
	{
		rootView.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
		
		ProblemContent.complete = false;
		
		ProblemContent.DATA.clear();
		ProblemContent.webview.loadUrl("javascript:getData()");
		
		new RequestTask().execute(mItem.url, "previewAnswers", "Preview Answers");
	}
	
	boolean isOpen;
	
	public void submit()
	{
		rootView.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
		
		ProblemContent.complete = false;
		
		ProblemContent.DATA.clear();
		ProblemContent.webview.loadUrl("javascript:getData()");
		
		
		if(isOpen == false)
		{
			new RequestTask().execute(mItem.url, "submitAnswers", "Submit Answers");
		}
		else
		{
			new RequestTask().execute(mItem.url, "checkAnswers", "Check Answers");
		}
	}
	
	public void gotourl()
	{
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mItem.url));
		
		startActivity(browserIntent);
	}

	class SubmitListener implements OnClickListener
	{
		Context owner;
		int type;
		
		public SubmitListener(Context own, int t)
		{
			owner = own;
			type = t;
		}

		@Override
		public void onClick(View arg0) {
			
			if(type == 0)
			{
				preview();
			}
			else
			{
				submit();
			}
			
		}
	}
	
	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_problem_detail,
				container, false);
		
		

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			this.getSherlockActivity().getSupportActionBar().setTitle(mItem.name);
			
			ProblemContent.webview = (WebView) rootView.findViewById(R.id.problem_webview);
			ProblemContent.webview.getSettings().setJavaScriptEnabled(true);
			ProblemContent.webview.addJavascriptInterface(new WebAppInterface( ProblemContent.webview.getContext() ), "Android");
			
			rootView.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
			new RequestTask().execute(mItem.url, "get");
			
			ProblemContent.webview.setOnTouchListener(new OnSwipeTouchListener(ProblemContent.webview) {
			    public void onSwipeTop() {
			    	
			    	
			    }
			    public void onSwipeRight() {
			    	if(!tablet)
			    	{
				    	getActivity().finish();
			    	}
			    	
			    	int i = ProblemContent.ITEMS.indexOf(mItem);
			    	
			    	if(i > 0)
			    	{
			    		ProblemContent.listactivity.onItemSelected(ProblemContent.ITEMS.get(i - 1).id);
			    	}
			    }
			    public void onSwipeLeft() {
			    	if(!tablet)
			    	{
			    		getActivity().finish();
			    	}
			    	
			    	int i = ProblemContent.ITEMS.indexOf(mItem);

			    	
			    	if(i + 1 < ProblemContent.ITEMS.size())
			    	{
			    		ProblemContent.listactivity.onItemSelected(ProblemContent.ITEMS.get(i + 1).id);
			    	}
			    }
			    public void onSwipeBottom() {

			    }
			});
			
			
		}
		
		try
		{
			Button submit = (Button)rootView.findViewById(R.id.submitanswers);
			Button preview = (Button)rootView.findViewById(R.id.previewanswers);
			
			
			if(tablet)
			{
				
				
				preview.setOnClickListener(new SubmitListener(rootView.getContext(), 0));
				submit.setOnClickListener(new SubmitListener(rootView.getContext(), 1));
			}
			else
			{
				preview.setVisibility(View.GONE);
				submit.setVisibility(View.GONE);
				rootView.findViewById(R.id.tabletbottom).setVisibility(View.GONE);
			}
		}
		catch(Exception e)
		{
			
		}

		return rootView;
	}
}
