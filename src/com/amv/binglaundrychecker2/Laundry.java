package com.amv.binglaundrychecker2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Laundry extends Activity implements OnRefreshListener {
	private ProgressDialog progDialog;
	private String building;
	int selected;
	private TextView time;

	private Graph[] graphs;

	private String statusURL = "http://binglaundry.herokuapp.com/status/";
	private String communityURL = "http://binglaundry.herokuapp.com/communities";
	private String buildingURL = "http://binglaundry.herokuapp.com/buildings/";

	private PullToRefreshLayout mPullToRefreshLayout;

	private int heightInDp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blding_list);

		// set up pull to refresh
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).listener(this)
				.allChildrenArePullable().setup(mPullToRefreshLayout);

		initializeTextViews();

		// load saved configurations
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		// if building pref is not there then set building to null
		building = prefs.getString("building", null);
		if (building == null) {
			getActionBar().setTitle("Laundry Status");
			setCommunity();
		} else {
			getActionBar().setTitle(building.replace("_", " "));
			getActionBar().setSubtitle("Laundry Status");
			getStatus(building);
		}

		heightInDp = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
						.getDisplayMetrics());

		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"76BEA164B057BB9C09EA516A71AEC324").build();
		adView.loadAd(adRequest);
	}

	// Method that calls on pull to refresh
	@Override
	public void onRefreshStarted(View view) {
		getStatus(building);
	}

	private void initializeTextViews() {
		graphs = new Graph[2];

		graphs[0] = new Graph(getApplicationContext());
		graphs[1] = new Graph(getApplicationContext());
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-BoldCondensed.ttf");
		Typeface tf2 = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Medium.ttf");

		graphs[0].name = (TextView) findViewById(R.id.nameA);
		graphs[0].name.setTypeface(tf);

		graphs[0].washerAvail = (TextView) findViewById(R.id.washerAvailA);
		graphs[0].washerAvail.setTypeface(tf);
		graphs[0].washerComplete = (TextView) findViewById(R.id.washerCompleteA);
		graphs[0].washerComplete.setTypeface(tf);
		graphs[0].washerInUse = (TextView) findViewById(R.id.washerInUseA);
		graphs[0].washerInUse.setTypeface(tf2);
		graphs[0].washers = (TableRow) findViewById(R.id.washersA);

		graphs[0].dryerAvail = (TextView) findViewById(R.id.dryerAvailA);
		graphs[0].dryerAvail.setTypeface(tf);
		graphs[0].dryerComplete = (TextView) findViewById(R.id.dryerCompleteA);
		graphs[0].dryerComplete.setTypeface(tf);
		graphs[0].dryerInUse = (TextView) findViewById(R.id.dryerInUseA);
		graphs[0].dryerInUse.setTypeface(tf2);
		graphs[0].dryers = (TableRow) findViewById(R.id.dryersA);

		graphs[0].line = (TableRow) findViewById(R.id.lineA);
		graphs[0].tableLayout = (TableLayout) findViewById(R.id.tableLayoutA);

		graphs[1].name = (TextView) findViewById(R.id.nameB);
		graphs[1].name.setTypeface(tf);

		graphs[1].washerAvail = (TextView) findViewById(R.id.washerAvailB);
		graphs[1].washerAvail.setTypeface(tf);
		graphs[1].washerComplete = (TextView) findViewById(R.id.washerCompleteB);
		graphs[1].washerComplete.setTypeface(tf);
		graphs[1].washerInUse = (TextView) findViewById(R.id.washerInUseB);
		graphs[1].washerInUse.setTypeface(tf2);
		graphs[1].washers = (TableRow) findViewById(R.id.washersB);

		graphs[1].dryerAvail = (TextView) findViewById(R.id.dryerAvailB);
		graphs[1].dryerAvail.setTypeface(tf);
		graphs[1].dryerComplete = (TextView) findViewById(R.id.dryerCompleteB);
		graphs[1].dryerComplete.setTypeface(tf);
		graphs[1].dryerInUse = (TextView) findViewById(R.id.dryerInUseB);
		graphs[1].dryerInUse.setTypeface(tf2);
		graphs[1].dryers = (TableRow) findViewById(R.id.dryersB);

		graphs[1].line = (TableRow) findViewById(R.id.lineB);
		graphs[1].tableLayout = (TableLayout) findViewById(R.id.tableLayoutB);

		graphs[0].setClickListeners();
		graphs[1].setClickListeners();

		graphs[0].setGraphInvisible();
		graphs[1].setGraphInvisible();

		time = (TextView) findViewById(R.id.time);
	}

	private void getStatus(String building) {
		mPullToRefreshLayout.setRefreshing(true);
		new CallAPI().execute(statusURL + building, "status");
	}

	private void setCommunity() {
		callProgDialog();
		new CallAPI().execute(communityURL, "community");
	}

	private void setBuilding(String community) {
		callProgDialog();
		new CallAPI().execute(buildingURL + community, "building");
	}

	/*
	 * Convert JSON string into string
	 */
	private static String convertInputStream(InputStream in) throws IOException {
		int bytesRead;
		byte[] contents = new byte[1024];
		String string = null;
		while ((bytesRead = in.read(contents)) != -1) {
			string = new String(contents, 0, bytesRead);
		}
		return string;
	}

	// The three types are used for- params, progress, result
	private class CallAPI extends AsyncTask<String, String, String[]> {

		/*
		 * Get JSON response from url supplied
		 */
		@Override
		protected String[] doInBackground(String... params) {
			String urlString = params[0];

			String[] result = { "", params[1] };
			InputStream in = null;
			URL url = null;
			HttpURLConnection urlConnection = null;

			try {
				url = new URL(urlString);

				urlConnection = (HttpURLConnection) url.openConnection();

				in = new BufferedInputStream(urlConnection.getInputStream());

				result[0] = convertInputStream(in);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				urlConnection.disconnect();
			}

			return result;
		}

		protected void onPostExecute(String[] result) {
			JSONArray json = null;

			try {
				json = new JSONArray(result[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (result[1] == "community")
				postCommunityCall(json);
			else if (result[1] == "status")
				postStatusCall(json);
			else if (result[1] == "building") {
				postBuildingCall(json);
			}

			if (mPullToRefreshLayout.isRefreshing()) {
				mPullToRefreshLayout.setRefreshComplete();
			}
		}
	}

	private void postCommunityCall(final JSONArray json) {
		final String[] communities = new String[json.length()];
		for (int i = 0; i < json.length(); i++) {
			try {
				communities[i] = json.getJSONObject(i).getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Community");
		builder.setCancelable(true);
		selected = -1;
		builder.setSingleChoiceItems(communities, selected,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selected = which;
					}
				});
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (selected != -1) {
					String community = communities[selected].replace(" ", "_");
					setBuilding(community);
					dialog.dismiss();
				}
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		progDialog.dismiss();
	}

	public void postStatusCall(JSONArray json) {
		for (int i = 0; i < json.length(); i++) {
			JSONObject object;
			try {
				object = json.getJSONObject(i);
				graphs[i].setGraphVisible();
				graphs[i].setValues(object, heightInDp);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			setTime();
		}

		if (json.length() == 1) {
			graphs[1].setGraphInvisible();
		}
	}

	private void setTime() {
		Time now = new Time();
		now.setToNow();
		String hours = now.format("%l:%M");
		time.setText("Status as of " + hours);
	}

	private void postBuildingCall(JSONArray json) {
		final String[] buildings = new String[json.length()];
		for (int i = 0; i < json.length(); i++) {
			try {
				buildings[i] = json.getJSONObject(i).getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Building");
		builder.setCancelable(true);
		selected = -1;
		builder.setSingleChoiceItems(buildings, selected,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selected = which;
					}
				});
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (selected != -1) {
					// Save the building to settings
					SharedPreferences.Editor editor = getPreferences(
							MODE_PRIVATE).edit();
					editor.putString("building", building);
					editor.commit();

					building = buildings[selected].replace(" ", "_");
					getStatus(building);

					getActionBar().setTitle(building);
					getActionBar().setSubtitle("Laundry Status");

					dialog.dismiss();
				}
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		progDialog.dismiss();
	}

	private void callProgDialog() {
		progDialog = new ProgressDialog(Laundry.this);
		progDialog.setMessage("Loading..");
		progDialog.show();
	}

	// save the setup
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putString("building", building);
		editor.commit();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_change:
			setCommunity();
			break;
		}
		
		return true;
	}
}