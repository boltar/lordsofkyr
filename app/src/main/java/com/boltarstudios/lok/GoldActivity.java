package com.boltarstudios.lok;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.cloud.backend.core.CloudBackendFragment;
import com.google.cloud.backend.core.CloudCallbackHandler;
import com.google.cloud.backend.core.CloudEntity;

public class GoldActivity extends Activity {
	
	private Button collectButton;
	private Button cancelButton;
	private TextView tv_cur_gold;
	private TextView tv_cur_pop;
	private TextView tv_gold_update;
	private int currentGold = 0;
	private int newGold = 0;
	private int currentPop = 0;

	private final String TAG = "GoldActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gold);
		
		collectButton = (Button) findViewById(R.id.collect_taxes);
		cancelButton = (Button) findViewById(R.id.cancel_taxes);
		tv_cur_gold = (TextView) findViewById(R.id.tv_cur_gold);
		tv_cur_pop = (TextView) findViewById(R.id.tv_cur_pop);
		
		currentGold = ((LokProperties)getApplication()).getGold();
		tv_cur_gold.setText(currentGold + "");
		
		currentPop = ((LokProperties)getApplication()).getPop();
		tv_cur_pop.setText(currentPop + "");
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		collectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// buy, then display alert
				int newGold = currentGold;
				
				newGold = newGold +  
							(int) (currentPop * 0.1); // 10% tax rate
				updateGold(newGold);
				
			}
		});
		tv_gold_update = (TextView) findViewById(R.id.gold_update_text);
		tv_gold_update.setText("The " + currentPop + " people of your empire can pay you " 
				+ (int)(currentPop * 0.1) + " gold on this glorious turn");
	
		
	}
	
	private void updateGold(final int newTotalGold) {
		CloudCallbackHandler<CloudEntity> handler = 
				new CloudCallbackHandler<CloudEntity>() {
			
			@Override
			public void onComplete(CloudEntity result) {
				Log.d(TAG, "2 <----- finished saving gold: " 
						+ result.getProperties().get(LokProperties.DS_REALM_GOLD));
				finish();
			}

			@Override
			public void onError(IOException exception) {
				Log.d(TAG, "error on saving gold");
				finish();
			}
		};
		
		CloudEntity ce = ((LokProperties)getApplication()).getCe();
		ce.put(LokProperties.DS_REALM_GOLD, newTotalGold);
		CloudBackendFragment cbf = ((LokProperties)getApplication()).getCloudBackendFragment();
		cbf.getCloudBackend().update(ce, handler);

	}
}
