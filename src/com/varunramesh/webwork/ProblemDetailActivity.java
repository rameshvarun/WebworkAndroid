package com.varunramesh.webwork;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * An activity representing a single Problem detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link ProblemListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ProblemDetailFragment}.
 */
public class ProblemDetailActivity extends SherlockFragmentActivity {
	
	public ProblemDetailFragment fragment;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem_detail);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		//if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ProblemDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(ProblemDetailFragment.ARG_ITEM_ID));
			fragment = new ProblemDetailFragment(false);
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.problem_detail_container, fragment).commit();
		//}
		
		/*Button submit = (Button)findViewById(R.id.submitanswers);
		Button preview = (Button)findViewById(R.id.previewanswers);
		
		submit.setVisibility(View.GONE);
		preview.setVisibility(View.GONE);*/
		
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_problem, menu);
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
			//NavUtils.navigateUpTo(this, new Intent(this,
			//		ProblemListActivity.class));
			
			finish();
			
			return true;
			
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			break;
			
		case R.id.preview:
			
						
			fragment.preview();
			break;
			
		case R.id.submit:
			
			fragment.submit();
			break;
			
		case R.id.gotourl:
			fragment.gotourl();
			break;
			
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		
		}
		return super.onOptionsItemSelected(item);
	}
}
