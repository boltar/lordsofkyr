package com.boltarstudios.lok;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekbarFragment extends Fragment {

	private final String TAG = "SeekbarFragment";
	private TextView title;
	private TextView count;
	private TextView max;
	private SeekBar sb;
	private onProgressSelectedListener mCallback;

	
	public SeekbarFragment() {
		Log.d(TAG, "SeekbarFragment created!");
	}

	public interface onProgressSelectedListener {
		public void onProgressSelected(SeekBar seekbar, int position);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_seekbar, container,
				false);
		title = (TextView)rootView.findViewById(R.id.tv_sb_title);
		count = (TextView)rootView.findViewById(R.id.tv_sb_counts);
		max = (TextView)rootView.findViewById(R.id.tv_sb_max);
		sb = (SeekBar)rootView.findViewById(R.id.any_seekbar);
		
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       

		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {      
		        // TODO Auto-generated method stub      
		    }       

		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {     
		        // TODO Auto-generated method stub      
		    }       

		    @Override       
		    public void onProgressChanged(SeekBar seekBar, int progress, 
		    		boolean fromUser) {     
		        // TODO Auto-generated method stub      

		        setCount(progress + "");
		        mCallback.onProgressSelected(seekBar, progress);
		    }  
		});
				
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (onProgressSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onProgressSelectedListener");
        }
	}
	
	public void setTitle(String text) {
		title.setText(text);
	}
	
	public void setCount(String text) {
		count.setText(text);
	}
	
	public void setMax(int maxVal) {
		max.setText("" + maxVal);
		sb.setMax(maxVal);
		
	}
	
	public SeekBar getSeekbar() {
		return sb;
	}	
}
