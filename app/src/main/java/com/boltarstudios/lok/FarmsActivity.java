package com.boltarstudios.lok;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.cloud.backend.core.CloudBackendFragment;
import com.google.cloud.backend.core.CloudCallbackHandler;
import com.google.cloud.backend.core.CloudEntity;

public class FarmsActivity extends Activity implements SeekbarFragment.onProgressSelectedListener {
	
	private Button buyButton;
	private Button cancelButton;
	private final int FARM_PRICE = 10;
	private final int MAX_PURCHASE = 10;
	private final String TAG = "FarmsActivity";
	private int farmsToBuy = 0;
	private int farmsOwned = 0;
	private TextView goldOwnedTV, goldSpentTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_farms);
		
		buyButton = (Button) findViewById(R.id.buy_army);
		cancelButton = (Button) findViewById(R.id.cancel_military);
		SeekbarFragment sb1 = (SeekbarFragment) getFragmentManager().
				findFragmentById(R.id.farms_fragment);
		sb1.setTitle("Farms to buy");
		sb1.setCount("0");
		
		final int goldOwned = ((LokProperties)getApplication()).getGold();
		farmsOwned = ((LokProperties)getApplication()).getFarms();
		int max_farms = Math.min(goldOwned / FARM_PRICE, MAX_PURCHASE); 
		sb1.setMax(max_farms);
		final SeekBar sb = sb1.getSeekbar();
						
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		buyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// buy, then display alert
				farmsToBuy = sb.getProgress();
				updateFarms(farmsToBuy + farmsOwned, 
						goldOwned - farmsToBuy * FARM_PRICE); // no + operator
			}
		});
		
		goldOwnedTV = (TextView)findViewById(R.id.farm_gold_owned_tv);
		goldSpentTV = (TextView)findViewById(R.id.farm_gold_spent_tv);
		
		goldOwnedTV.setText("Your gold reserves: " + goldOwned);
		goldSpentTV.setText("Total Cost: 0");
	}
	
    public void onProgressSelected(SeekBar sb, int position) {
    	Log.d(TAG, "onProgressSelected: " + position);
    	
    	try {
    		goldSpentTV.setText("Total Cost: " + position * FARM_PRICE);
    	} catch (NullPointerException e) {
    		Log.d(TAG, "goldSpentTV not created yet... NO BIG DEAL");
    	}
    }
    
	
	private void updateFarms(final int newTotalFarms, final int newTotalGold) {
		CloudCallbackHandler<CloudEntity> handler = 
				new CloudCallbackHandler<CloudEntity>() {
			
			@Override
			public void onComplete(CloudEntity result) {
				Log.d(TAG, "2 <----- finished saving farms: " 
						+ result.getProperties().get(LokProperties.DS_REALM_FARMS));
				farmsOwned = newTotalFarms;
				finish();
			}

			@Override
			public void onError(IOException exception) {
				Log.d(TAG, "error on saving farm realm");
				finish();
			}
		};
		
		CloudEntity ce = ((LokProperties)getApplication()).getCe();
		ce.put(LokProperties.DS_REALM_FARMS, newTotalFarms);
		ce.put(LokProperties.DS_REALM_GOLD, newTotalGold);
		CloudBackendFragment cbf = ((LokProperties)getApplication()).getCloudBackendFragment();
		cbf.getCloudBackend().update(ce, handler);

	}


}
