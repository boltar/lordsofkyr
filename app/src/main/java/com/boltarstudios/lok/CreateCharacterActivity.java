package com.boltarstudios.lok;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.cloud.backend.core.CloudBackendFragment;
import com.google.cloud.backend.core.CloudBackendFragment.OnListener;
import com.google.cloud.backend.core.CloudCallbackHandler;
import com.google.cloud.backend.core.CloudEntity;

public class CreateCharacterActivity extends Activity implements OnListener {

	public static final String TAG = "TC";
	private static final String PROCESSING_FRAGMENT_TAG = "BACKEND_FRAGMENT";
	
	private static final String DB_TAG = "LOK_DB";
	private static final String LOK_KIND = "LordsOfKyr";
	private static boolean realmExists;

	private EditText mRealmEditText;
	private Spinner mRealmRaceSpinner;
	private Spinner mRealmStartingGiftsSpinner;

	private FragmentManager mFragmentManager;
	private CloudBackendFragment mProcessingFragment;
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_create_character);

		mRealmEditText = (EditText) findViewById(R.id.realmName);

		mRealmRaceSpinner = (Spinner) findViewById(R.id.race_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.races_array,
						android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mRealmRaceSpinner.setAdapter(adapter);

		mRealmStartingGiftsSpinner = (Spinner) findViewById(R.id.starting_gift_spinner);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
				this, R.array.starting_gifts_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mRealmStartingGiftsSpinner.setAdapter(adapter2);

		//initiateFragments();

		Button createCharButton = (Button) findViewById(R.id.create_char);
		
		createCharButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String realmName = mRealmEditText.getText().toString();
				
				if ((realmName.length() > 20) || (realmName.length() == 0)) {
					new AlertDialog.Builder(context).
					setTitle("Invalid Realm Name").
					setMessage("Realm name is too long or too weird").
					setNeutralButton("OK", null).
					show();
					return;
				}
				String realmRace = (String) mRealmRaceSpinner.getSelectedItem();
				Log.d(TAG, "create realm pressed with realm name: " + realmName);
				createRealm(v, realmName, realmRace);

			}
		});
	}

	public void createRealm(View v, String realmName, String realmRace) {

		// create a CloudEntity with the new post
		CloudEntity newRealm = new CloudEntity(LOK_KIND);
		newRealm.put(LokProperties.DS_REALM_NAME, realmName);
		newRealm.put(LokProperties.DS_REALM_RACE, realmRace);
		newRealm.put(LokProperties.DS_REALM_POP, new BigDecimal(1000));
		newRealm.put(LokProperties.DS_REALM_GOLD, new BigDecimal(50));
		newRealm.put(LokProperties.DS_REALM_FARMS, new BigDecimal(10));

		// create a response handler that will receive the result or an error
		CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
			@Override
			public void onComplete(final CloudEntity result) {
				/*
				 * mPosts.add(0, result); updateGuestbookView();
				 * mMessageTxt.setText(""); mMessageTxt.setEnabled(true);
				 * mSendBtn.setEnabled(true);
				 */
				Log.d(DB_TAG, "createRealm insert complete!");
				// also save the entity to global -- be sure to save the result
				// not newRealm, so we can use this to save it back on updates
				((LokProperties)getApplication()).setCe(result);
				
				Intent intent = new Intent(CreateCharacterActivity.this,
						MainActivity.class);
				startActivity(intent);
				//finish();
			}

			@Override
			public void onError(final IOException exception) {
				handleEndpointException(exception);
			}
		};

		// execute the insertion with the handler
		((LokProperties)getApplication())
			.getCloudBackendFragment()
			.getCloudBackend()
			.insert(newRealm, handler);
		
	}

	/**
	 * Method called via OnListener in {@link CloudBackendFragment}.
	 */
	@Override
	public void onCreateFinished() {
		// listPosts();
		Log.d(TAG, "onCreateFinished()");
	}

	/**
	 * Method called via OnListener in {@link CloudBackendFragment}.
	 */
	@Override
	public void onBroadcastMessageReceived(List<CloudEntity> l) {
		for (CloudEntity e : l) {
			// String message = (String) e.get(BROADCAST_PROP_MESSAGE);
			// int duration = Integer.parseInt((String)
			// e.get(BROADCAST_PROP_DURATION));
			// Toast.makeText(this, message, duration).show();
			// Log.i(Consts.TAG, "A message was recieved with content: " +
			// message);
		}
	}

	private void handleEndpointException(IOException e) {
		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		Log.d(TAG, e.toString());

		// mSendBtn.setEnabled(true);
	}
}
