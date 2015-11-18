package com.boltarstudios.lok;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.cloud.backend.core.CloudEntity;

public class PlayersListFragment extends Fragment {
	public static final String ARG_PLAYER_A = "PLAYER_A";
	public static final String ARG_PLAYER_B = "PLAYER_B";

	public PlayersListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_players_list, container, false);
		TableLayout t = (TableLayout) rootView.findViewById(R.id.players_table);
		//TableLayout tl = new TableLayout(getActivity());
		TableRow tr;
		TextView tv;
		
		List<CloudEntity> list = ((LokProperties) getActivity().getApplication()).getPlayersList();
		
    	for (CloudEntity e: list) {
    		tv = new TextView(getActivity());
    		tr = new TableRow(getActivity());
    		String text = (String) e.getProperties().get("Realm_Name") + " | " + 
    				(String) e.getProperties().get("Realm_Race") + " | Population: " + 
    				e.getProperties().get("Realm_Pop") + " | Farms " + 
    				e.getProperties().get("Realm_Farms");
    		tv.setText(text);
    		tr.addView(tv);
    		t.addView(tr);
    	}
					
		return rootView;
	}
}
