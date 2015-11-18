
package com.boltarstudios.lok;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.cloud.backend.core.CloudBackendFragment;
import com.google.cloud.backend.core.CloudCallbackHandler;
import com.google.cloud.backend.core.CloudEntity;

public class ArmyActivity extends Activity 
	implements SeekbarFragment.onProgressSelectedListener{
	
	private final int ARCHER_COST = 10;
	private final int SWORDSMEN_COST = 10;
	private final int DRAGON_COST = 50;
	private final String TAG = "ArmyActivity";
	private int goldOwned; 
	private TextView goldOwnedTV, goldSpentTV;
	private int archerCost = 0;
	private int swordsmenCost = 0;
	private int dragonCost = 0;
	SeekbarFragment sb1, sb2, sb3;
	private Button buyButton;
	private Button cancelButton;
	AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_army);
		LokProperties lok = (LokProperties)getApplication();
		goldOwned = lok.getGold();
		
		sb1 = (SeekbarFragment) getFragmentManager().findFragmentById(R.id.army1);
		sb1.setTitle("Archers");
		sb1.setCount("0");
		sb1.setMax(Math.min(goldOwned / ARCHER_COST, lok.MAX_ARMY_PURCHASABLE_PER_TURN));
		sb2 = (SeekbarFragment) getFragmentManager().findFragmentById(R.id.army2);
		sb2.setTitle("Swordsmen");
		sb2.setCount("0");
		sb2.setMax(Math.min(goldOwned / SWORDSMEN_COST, lok.MAX_ARMY_PURCHASABLE_PER_TURN));
		sb3 = (SeekbarFragment) getFragmentManager().findFragmentById(R.id.army3);
		sb3.setTitle("Dragons");
		sb3.setCount("0");
		sb3.setMax(Math.min(goldOwned / DRAGON_COST, lok.MAX_ARMY_PURCHASABLE_PER_TURN));
		
		buyButton = (Button)findViewById(R.id.buy_army);
		cancelButton = (Button)findViewById(R.id.cancel_military);
		
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
				if (archerCost + swordsmenCost + dragonCost > goldOwned) {
					builder.show();
				} else {
					updateArmy(archerCost, swordsmenCost, dragonCost);
				}
			}
		});
		
		goldOwnedTV = (TextView)findViewById(R.id.gold_owned_tv);
		goldSpentTV = (TextView)findViewById(R.id.farm_gold_spent_tv);
		
		goldOwnedTV.setText("Your gold reserves: " + goldOwned);
		goldSpentTV.setText("Total Cost: 0");
		
		builder = new AlertDialog.Builder(this);
	     builder.setTitle("Not enough gold!")
	    .setMessage("You're too poor.")
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     })
	    .setIcon(R.drawable.gold);

	}

    public void onProgressSelected(SeekBar sb, int position) {
    	Log.d(TAG, "onProgressSelected: " + position);
    	
    	if (sb == sb1.getSeekbar()) {
    		archerCost = ARCHER_COST * position;
    	} else if (sb == sb2.getSeekbar()) {
    		swordsmenCost = SWORDSMEN_COST * position;
    	} else if (sb == sb3.getSeekbar()) {
    		dragonCost = DRAGON_COST * position;
    	}

    	try {
    		goldSpentTV.setText("Total Cost: " + (archerCost + swordsmenCost + dragonCost));
    	} catch (NullPointerException e) {
    		Log.d(TAG, "goldSpentTV not created yet... NO BIG DEAL");
    	}
    }
    
	private void updateArmy(int archerCost, int swordsmenCost, int dragonCost) {
		CloudCallbackHandler<CloudEntity> handler = 
				new CloudCallbackHandler<CloudEntity>() {
			
			@Override
			public void onComplete(CloudEntity result) {
				Log.d(TAG, "2 <----- finished saving army: " 
						+ result.getProperties().get(LokProperties.DS_ARCHERS) + " " 
						+ result.getProperties().get(LokProperties.DS_SWORDSMEN) + " " 
						+ result.getProperties().get(LokProperties.DS_DRAGONS));
				
				
				finish();
			}

			@Override
			public void onError(IOException exception) {
				Log.d(TAG, "error on saving farm realm");
				finish();
			}
		};
		
		LokProperties lok = ((LokProperties)getApplication());
		
		CloudEntity ce = lok.getCe();
		ce.put(LokProperties.DS_ARCHERS, archerCost / ARCHER_COST + lok.getArchers());
		ce.put(LokProperties.DS_SWORDSMEN, swordsmenCost / SWORDSMEN_COST + lok.getSwordsmen());
		ce.put(LokProperties.DS_DRAGONS, dragonCost / DRAGON_COST + lok.getDragons());
		ce.put(LokProperties.DS_REALM_GOLD, goldOwned - (archerCost + swordsmenCost + dragonCost));
		
		CloudBackendFragment cbf = lok.getCloudBackendFragment();
		cbf.getCloudBackend().update(ce, handler);

	}

    
	
}
