package com.boltarstudios.lok;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.backend.core.CloudBackendFragment;
import com.google.cloud.backend.core.CloudCallbackHandler;
import com.google.cloud.backend.core.CloudEntity;

public class MainFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	private final String TAG = "MainFragment";

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		rootView.findViewById(R.id.farms_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity(),
								FarmsActivity.class);
						intent.putExtra("FARMS", ((LokProperties) getActivity()
								.getApplication()).getFarms());
						intent.putExtra("GOLD", ((LokProperties) getActivity()
								.getApplication()).getGold());
						startActivity(intent);
					}
				});
		rootView.findViewById(R.id.collect_taxes_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity(),
								GoldActivity.class);
						intent.putExtra("GOLD", ((LokProperties) getActivity()
								.getApplication()).getGold());
						intent.putExtra("POP", ((LokProperties) getActivity()
								.getApplication()).getPop());
						startActivity(intent);
					}
				});
		
		rootView.findViewById(R.id.military_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity(), 
								ArmyActivity.class);
						intent.putExtra("GOLD", ((LokProperties) getActivity()
								.getApplication()).getGold());
						startActivity(intent);
					}
				});
		rootView.findViewById(R.id.attack_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						CharSequence text = "Ain't nobody got time for attax";
						Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT)
								.show();
					}
				});
		rootView.findViewById(R.id.delete_account_button).setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						deleteAccount();
					}
				});

		return rootView;
	}
	
	private void deleteAccount() {
		CloudCallbackHandler<Void> handler = 
				new CloudCallbackHandler<Void>() {
			
			@Override
			public void onComplete(Void v) {
				Log.d(TAG, "deleteAccount onComplete");
				getActivity().finish();
			}

			@Override
			public void onError(IOException exception) {
				Log.d(TAG, "deleteAccount onError:" + exception);
				getActivity().finish();
			}
			
		};


		CloudEntity ce = ((LokProperties) getActivity()
				.getApplication()).getCe();
		CloudBackendFragment cbf = ((LokProperties) getActivity()
				.getApplication()).getCloudBackendFragment();
		cbf.getCloudBackend().delete(ce, handler);

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "MainFragment onResume");
		updateStatsTable(getView());

	}

	public void updateStatsTable(View rootView) {
		TableLayout t = (TableLayout) rootView.findViewById(R.id.stats_table);
		TableRow tr = new TableRow(getActivity());
		TextView tv = new TextView(getActivity());
		t.removeAllViews();
		LokProperties lok = (LokProperties)getActivity().getApplication();

		tv.setText(lok.getName());
		tr.addView(tv);
		t.addView(tr);

		tv = new TextView(getActivity());
		tr = new TableRow(getActivity());
		tv.setText("Race: "
				+ lok.getRace());
		tr.addView(tv);
		t.addView(tr);

		tv = new TextView(getActivity());
		tr = new TableRow(getActivity());
		tv.setText("Population: \t"
				+ lok.getPop());
		tr.addView(tv);
		t.addView(tr);

		tv = new TextView(getActivity());
		tr = new TableRow(getActivity());
		tv.setText("Gold: \t"
				+ lok.getGold());
		tr.addView(tv);
		t.addView(tr);

		tv = new TextView(getActivity());
		tr = new TableRow(getActivity());
		tv.setText("Farms: \t"
				+ lok.getFarms());
		tr.addView(tv);
		t.addView(tr);

		tv = new TextView(getActivity());
		tr = new TableRow(getActivity());
		tv.setText("Archers: " + lok.getArchers() + " | " +
				"Swordsmen: " + lok.getSwordsmen() + " | " +
				"Dragons: " + lok.getDragons());
		tr.addView(tv);
		t.addView(tr);
		
	}
}
