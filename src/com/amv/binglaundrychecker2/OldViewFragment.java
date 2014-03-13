package com.amv.binglaundrychecker2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OldViewFragment extends ViewFragment {
	private TextView time;

	private OldViewGraph[] graphs;

	public void onStart() {
		super.onStart();
		viewChangeListener.update();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.old_view_fragment, container,
				false);
		initializeTextViews(myView);
		return myView;
	}

	private void initializeTextViews(View v) {
		graphs = new OldViewGraph[2];
		graphs[0] = new OldViewGraph();
		graphs[1] = new OldViewGraph();

		graphs[0].name = (TextView) v.findViewById(R.id.building);
		graphs[0].statusWash = (TextView) v.findViewById(R.id.statusA);
		graphs[0].statusDry = (TextView) v.findViewById(R.id.statusB);
		graphs[1].name = (TextView) v.findViewById(R.id.buildingB);
		graphs[1].statusWash = (TextView) v.findViewById(R.id.status1);
		graphs[1].statusDry = (TextView) v.findViewById(R.id.status2);
		time = (TextView) v.findViewById(R.id.time);
	}

	@Override
	public void update(JSONArray json, String timeString) {
		if (json == null) {
			return;
		}
		for (int i = 0; i < json.length(); i++) {
			JSONObject object;
			try {
				object = json.getJSONObject(i);
				graphs[i].setValues(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			time.setText(timeString);
		}
	}

}
