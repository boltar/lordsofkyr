package com.boltarstudios.lok;

import java.io.IOException;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.backend.core.CloudBackendFragment;
import com.google.cloud.backend.core.CloudBackendFragment.OnListener;
import com.google.cloud.backend.core.CloudCallbackHandler;
import com.google.cloud.backend.core.CloudEntity;
import com.google.cloud.backend.core.CloudQuery.Order;
import com.google.cloud.backend.core.CloudQuery.Scope;
import com.google.cloud.backend.core.Consts;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements OnListener {
	
	private static final String TAG = "LoginActivity";

    private static final String PROCESSING_FRAGMENT_TAG = "BACKEND_FRAGMENT";
    
	private static final String LOK_KIND = "LordsOfKyr";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private FragmentManager mFragmentManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button_gplus).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		findViewById(R.id.prefs_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						CharSequence text = "Ain't nobody got time for prefs!";
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
					}
				});
		mFragmentManager = getFragmentManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
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

		boolean cancel = false;
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			Log.d(TAG, "User canceled... ?!?!");
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
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
     * Method called via OnListener in {@link CloudBackendFragment}.
     */
    @Override
    public void onCreateFinished() {
        //listPosts();
    	Log.d(TAG, "1 <----- onCreateFinished from new CloudBackendFragment");
    	String accountName = ((LokProperties)getApplication())
    			.getCloudBackendFragment()
    			.getCloudBackend()
				.getSharedPreferences()
				.getString(Consts.PREF_KEY_ACCOUNT_NAME, "");

    	checkRealmExists(accountName);
    	Log.d(TAG, "onCreateFinished (finished signing in with Google+)");
    }

    /**
     * Method called via OnListener in {@link CloudBackendFragment}.
     */
    @Override
    public void onBroadcastMessageReceived(List<CloudEntity> l) {
//        for (CloudEntity e : l) {
//            String message = (String) e.get(BROADCAST_PROP_MESSAGE);
//            int duration = Integer.parseInt((String) e.get(BROADCAST_PROP_DURATION));
//            Toast.makeText(this, message, duration).show();
//            Log.i(Consts.TAG, "A message was recieved with content: " + message);
//        }
    }

    private void initiateFragments() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // Check to see if we have retained the fragment which handles
        // asynchronous backend calls
        CloudBackendFragment processingFragment = (CloudBackendFragment) mFragmentManager.
                findFragmentByTag(PROCESSING_FRAGMENT_TAG);
        
        // If not retained (or first time running), create a new one
        if (processingFragment == null) {
        	Log.d(TAG, "1 -----> new CloudBackendFragment"); 
            processingFragment = new CloudBackendFragment();
            processingFragment.setRetainInstance(true);
            fragmentTransaction.add(processingFragment, PROCESSING_FRAGMENT_TAG);
        }
        
        // save to global Application object
        ((LokProperties)getApplication()).setCloudBackendFragment(processingFragment);
        
        fragmentTransaction.commit();
    }
    
    public CloudEntity getPlayerEntity(List<CloudEntity> list, String accountName)
    {
    	for (CloudEntity e: list) {
    		if ((e.getCreatedBy() != null) && e.getCreatedBy().equals(accountName)) {
    			return e;
    		}
    	}
    	return null;
    }
    
    /**
     * 
     * @param e holds our player information
     * @param list holds everyone
     */
    public void startMainActivity(CloudEntity e, List<CloudEntity> list)
    {
    	String realmName = (String) e.getProperties().get("Realm_Name");
    	String realmRace = (String) e.getProperties().get("Realm_Race");
    	
    	((LokProperties)getApplication()).setCe(e); // save ourself to global
    	
    	int realmPop, realmGold, realmFarms;
    	try {
			realmPop = ((LokProperties)getApplication()).getCloudProp(e, LokProperties.DS_REALM_POP);
			realmGold = ((LokProperties)getApplication()).getCloudProp(e, LokProperties.DS_REALM_GOLD);    		
			realmFarms = ((LokProperties)getApplication()).getCloudProp(e, LokProperties.DS_REALM_FARMS);
    	} catch (NullPointerException npe) {
    		Log.e(TAG, "NPE: " + npe.getMessage());
    		realmPop = realmGold = realmFarms = 0;
    	}
    	
    	Log.d(TAG, "gold " + realmGold + ", pop " + realmPop + ", farms " + realmFarms);
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.putExtra("EXTRA_REALM_NAME", realmName);
		intent.putExtra("EXTRA_REALM_RACE", realmRace);
		intent.putExtra("EXTRA_REALM_POP", realmPop);
		intent.putExtra("EXTRA_REALM_GOLD", realmGold);
		intent.putExtra("EXTRA_REALM_FARMS", realmFarms);
		startActivity(intent);
    }
    public void startNewCharActivity() {
		Intent intent = new Intent(LoginActivity.this,
				CreateCharacterActivity.class);
		startActivity(intent);
    }
	/**
	 * Retrieves the list of all posts from the backend and updates the UI. For
	 * demonstration in this sample, the query that is executed is:
	 * "SELECT * FROM LoK ORDER BY _createdAt DESC LIMIT 50" This query will be
	 * re-executed when matching entity is updated.
	 */
	private void checkRealmExists(final String accountName) {
		// create a response handler that will receive the result or an error
		
		CloudCallbackHandler<List<CloudEntity>> handler = 
				new CloudCallbackHandler<List<CloudEntity>>() {
			
			@Override
			public void onComplete(List<CloudEntity> results) {
				Log.d(TAG, "2 <----- finished query, size:" + results.size());
				// I have the list of players -- store this in global
				((LokProperties)getApplication()).setPlayersList(results);
				
				CloudEntity e = getPlayerEntity(results, accountName);
				if (e != null) {
					Log.d(TAG, "Realm Farms instance of " + 
							e.getProperties().get(LokProperties.DS_REALM_FARMS)
							.getClass().getName());
					startMainActivity(e, results);
				} else { 
					Log.d(TAG, "realm not found for user " + accountName);
					startNewCharActivity();
				}
				showProgress(false);
				finish();
			}

			@Override
			public void onError(IOException exception) {
				Log.d(TAG, "error on checking realm");
				handleEndpointException(exception);
				// clear out any wakelocks???
			}
		};

		// execute the query with the handler
		Log.d(TAG, "2 -----> starting query for realmExists");
		CloudBackendFragment cbf = ((LokProperties)getApplication()).getCloudBackendFragment();
		cbf.getCloudBackend().listByKind(LOK_KIND,
				CloudEntity.PROP_OWNER, Order.DESC, 10, Scope.FUTURE_AND_PAST,
				handler);

	}
	
	private void handleEndpointException(IOException e) {
		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		Log.d(TAG, e.toString());

		// mSendBtn.setEnabled(true);
	}
    
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			initiateFragments();

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			//showProgress(false);

			if (success) {
				//finish();
				Log.d(TAG, "onPostExecute finished!");
			} else {
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	public String testMe() {
		return "Hello!";		
	}
}
