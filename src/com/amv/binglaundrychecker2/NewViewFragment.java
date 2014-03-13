package com.amv.binglaundrychecker2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class NewViewFragment extends ViewFragment {
	int selected;
	private TextView time;

	private NewViewGraph[] graphs;

	private int heightInDp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.blding_list, container, false);
		initializeTextViews(myView);
		return myView;
	}

	public void onStart() {
		super.onStart();
		viewChangeListener.update();
		heightInDp = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
						.getDisplayMetrics());

		AdView adView = (AdView) getView().findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"76BEA164B057BB9C09EA516A71AEC324").build();
		adView.loadAd(adRequest);
	}

	private void initializeTextViews(View view) {
		graphs = new NewViewGraph[2];

		graphs[0] = new NewViewGraph(getActivity());
		graphs[1] = new NewViewGraph(getActivity());

		graphs[0].name = (TextView) view.findViewById(R.id.nameA);

		graphs[0].washerAvail = (TextView) view.findViewById(R.id.washerAvailA);
		graphs[0].washerComplete = (TextView) view
				.findViewById(R.id.washerCompleteA);
		graphs[0].washerInUse = (TextView) view.findViewById(R.id.washerInUseA);
		graphs[0].washers = (TableRow) view.findViewById(R.id.washersA);

		graphs[0].dryerAvail = (TextView) view.findViewById(R.id.dryerAvailA);
		graphs[0].dryerComplete = (TextView) view
				.findViewById(R.id.dryerCompleteA);
		graphs[0].dryerInUse = (TextView) view.findViewById(R.id.dryerInUseA);
		graphs[0].dryers = (TableRow) view.findViewById(R.id.dryersA);

		graphs[0].line = (TableRow) view.findViewById(R.id.lineA);
		graphs[0].tableLayout = (TableLayout) view
				.findViewById(R.id.tableLayoutA);

		graphs[1].name = (TextView) view.findViewById(R.id.nameB);

		graphs[1].washerAvail = (TextView) view.findViewById(R.id.washerAvailB);
		graphs[1].washerComplete = (TextView) view
				.findViewById(R.id.washerCompleteB);
		graphs[1].washerInUse = (TextView) view.findViewById(R.id.washerInUseB);
		graphs[1].washers = (TableRow) view.findViewById(R.id.washersB);

		graphs[1].dryerAvail = (TextView) view.findViewById(R.id.dryerAvailB);
		graphs[1].dryerComplete = (TextView) view
				.findViewById(R.id.dryerCompleteB);
		graphs[1].dryerInUse = (TextView) view.findViewById(R.id.dryerInUseB);
		graphs[1].dryers = (TableRow) view.findViewById(R.id.dryersB);

		graphs[1].line = (TableRow) view.findViewById(R.id.lineB);
		graphs[1].tableLayout = (TableLayout) view
				.findViewById(R.id.tableLayoutB);

		graphs[0].setClickListeners();
		graphs[1].setClickListeners();

		graphs[0].setGraphInvisible();
		graphs[1].setGraphInvisible();

		time = (TextView) view.findViewById(R.id.time);
	}

	public void update(JSONArray json, String timeString) {
		if (json == null) {
			return;
		}
		for (int i = 0; i < json.length(); i++) {
			JSONObject object;
			try {
				object = json.getJSONObject(i);
				graphs[i].setGraphVisible();
				graphs[i].setValues(object, heightInDp);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			time.setText(timeString);
		}

		if (json.length() == 1) {
			graphs[1].setGraphInvisible();
		}
	}

}