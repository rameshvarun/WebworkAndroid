package com.varunramesh.webwork;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends SherlockActivity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	
	

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		Bundle b = getIntent().getExtras();
		((TextView) findViewById(R.id.CourseName)).setText(b.getString("com.varunramesh.webwork.coursename"));
		((TextView) findViewById(R.id.CourseSchool)).setText(b.getString("com.varunramesh.webwork.schoolname"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.activity_login, menu);
		return true;

	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		Bundle b = getIntent().getExtras();
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(b.getString("com.varunramesh.webwork.url"));
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

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.

			Document doc = null;
			
			Connection c = Jsoup.connect(params[0]);
			
			c.data("user", mEmailView.getText().toString());
			c.data("passwd", mPasswordView.getText().toString());
			
			
			
			try {
				doc = c.post();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			}


			// TODO: register the new account here.
			
			if(doc.getElementsByClass("ResultsWithError").size() > 0)
			{
				return false;
			}
			else
			{
				return true;
			}
			
			
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			
			
			showProgress(false);
			
			if(success == null)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(mEmailView.getContext());
				builder.setTitle("Connection Error");
				
				builder.setMessage("Could not connect to the WeBWorK server.");
				builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   //finish();
	                   }
	               });
				
				builder.create().show();
				
				return;
			}

			if (success) {
				
				Bundle b = getIntent().getExtras();
				
				//Add course to list of courses
				SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				
				String courses = settings.getString( "courses", "" );
				

				if(courses.compareTo("") == 0)
				{
					courses = b.getString("com.varunramesh.webwork.coursename");
				}
				else
				{
					courses += ";;;";
					courses += b.getString("com.varunramesh.webwork.coursename");
				}
				
				editor.putString("courses", courses);
				
				editor.commit();
				
				//Create preference file for course
				SharedPreferences coursesettings = getSharedPreferences(b.getString("com.varunramesh.webwork.coursename") , 0);
				SharedPreferences.Editor courseeditor = coursesettings.edit();
				
				courseeditor.putString("name", b.getString("com.varunramesh.webwork.coursename"));
				courseeditor.putString("school", b.getString("com.varunramesh.webwork.schoolname"));
				
				courseeditor.putString("user", mEmailView.getText().toString());
				courseeditor.putString("password",  mPasswordView.getText().toString());
				
				courseeditor.putString("url", b.getString("com.varunramesh.webwork.url") );
				
				
				courseeditor.commit();
				
				
				startActivity(new Intent(mEmailView.getContext(), MainActivity.class));
			} else {
				mPasswordView
						.setError("Invalid user ID or password.");
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
