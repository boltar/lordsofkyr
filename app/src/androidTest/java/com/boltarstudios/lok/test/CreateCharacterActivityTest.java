package com.boltarstudios.lok.test;

// Some Standard Imports
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.closeSoftKeyboard;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.util.Log;
import android.view.View;

import com.boltarstudios.lok.LoginActivity;
import com.boltarstudios.lok.R;

//import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
//Some Espresso Related imports

public class CreateCharacterActivityTest extends
		android.test.ActivityInstrumentationTestCase2<LoginActivity> {
	private final String TAG = "lok.test";	
	public CreateCharacterActivityTest() {
		super(LoginActivity.class);
	}

	// The standard JUnit 3 setUp method run for for every test
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getActivity(); // prevent error
						// "No activities found. Did you forget to launch the activity by calling getActivity()"		
	}

	public void testCreateAccount() {
		onView(withId(R.id.sign_in_button_gplus))
		.perform(click());
		
		try {
			int retries = 20;
			while ((retries >= 0) && 
					getActivity().findViewById(R.id.login_status)
					.getVisibility() == View.VISIBLE) {
				Thread.sleep(1000);
				Log.d(TAG, "retries: " + retries);
				retries--;
			}
		} catch (InterruptedException e) {
		    e.printStackTrace();
		    // handle the exception...        
		    // For example consider calling Thread.currentThread().interrupt(); here.
		} // I've spent way too much energy figuring out IdlingResource
				
		onView(withId(R.id.realmName)).perform(typeText("espresso test realm"),
				closeSoftKeyboard());
		try {Thread.sleep(1000);} catch (InterruptedException e){}

		onView(withId(R.id.create_char)).perform(click());

		try {
			//Thread.sleep(1000);
			int retries = 3;
			while (retries >= 0) {
				Thread.sleep(1000);
				Log.d(TAG, "retries: " + retries);
				retries--;
			}
		} catch (InterruptedException e) {
		    e.printStackTrace();
		    // handle the exception...        
		    // For example consider calling Thread.currentThread().interrupt(); here.
		}
		
		onView(withId(R.id.delete_account_button)).perform(click());

	}

	// public void testDeleteAccount() {
	// onView(withId(R.id.delete_account_button))
	// .perform(click());
	// }

}