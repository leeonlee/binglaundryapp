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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class List extends Activity implements OnRefreshListener {
	private ProgressDialog progDialog;
	private String building;
	int selected;
	boolean hasNext;
	private TextView time, nameA;

	private Button washerAvailA, washerCompleteA, washerInUseA, dryerAvailA,
			dryerCompleteA, dryerInUseA;

	private TableRow washersA, dryersA, lineA;

	boolean switcher;
	int numComplete;
	private String statusURL = "http://binglaundry.herokuapp.com/status/";
	private String communityURL = "http://binglaundry.herokuapp.com/communities";
	private String buildingURL = "http://binglaundry.herokuapp.com/buildings/";
	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blding_list);

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).listener(this)
				.allChildrenArePullable().setup(mPullToRefreshLayout);

		// load saved configurations
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		// if building pref is not there then set building to null
		building = prefs.getString("building", null);
		if (building == null) {
			getActionBar().setTitle("Laundry Status");
		} else {
			getActionBar().setTitle(building);
			getActionBar().setSubtitle("Laundry Status");
		}
		initializeTextViews();
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
		case com.amv.binglaundrychecker2.R.id.action_change:
			setCommunity();
			break;
		}
		return true;
	}

	private void initializeTextViews() {
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-BoldCondensed.ttf");
		Typeface tf2 = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Medium.ttf");
		nameA = (TextView) findViewById(R.id.nameA);
		nameA.setTypeface(tf);

		washerAvailA = (Button) findViewById(R.id.washerAvailA);
		washerAvailA.setTypeface(tf);
		washerCompleteA = (Button) findViewById(R.id.washerCompleteA);
		washerCompleteA.setTypeface(tf);
		washerInUseA = (Button) findViewById(R.id.washerInUseA);
		washerInUseA.setTypeface(tf2);
		washersA = (TableRow) findViewById(R.id.washersA);

		dryerAvailA = (Button) findViewById(R.id.dryerAvailA);
		dryerAvailA.setTypeface(tf);
		dryerCompleteA = (Button) findViewById(R.id.dryerCompleteA);
		dryerCompleteA.setTypeface(tf);
		dryerInUseA = (Button) findViewById(R.id.dryerInUseA);
		dryerInUseA.setTypeface(tf2);
		dryersA = (TableRow) findViewById(R.id.dryersA);

		lineA = (TableRow) findViewById(R.id.lineA);

		time = (TextView) findViewById(R.id.time);
	}

	private void getStatus(String building) {
		new CallAPI().execute(statusURL + building, "status");
	}

	private void setCommunity() {
		new CallAPI().execute(communityURL, "community");
	}

	private void setBuilding(String community) {
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

		protected void onPreExecute() {
			progDialog = new ProgressDialog(List.this);
			progDialog.setMessage("Loading..");
			progDialog.show();
		}

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
			progDialog.dismiss();
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
		builder.setCancelable(false);
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
	}

	public void postStatusCall(JSONArray json) {
		for (int i = 0; i < 1/* json.length() */; i++) {
			lineA.setVisibility(View.VISIBLE);
			JSONObject object;
			try {
				object = json.getJSONObject(i);
				nameA.setText(object.getString("name"));

				int avail = object.getInt("washerAvail");
				int total = object.getInt("washerTotal");
				int inUse = object.getInt("washerInUse");
				int complete = object.getInt("washerComplete");

				float weightPerMachine;

				if (total > 0) {
					washersA.setVisibility(View.VISIBLE);
					washerAvailA.setText(Integer.toString(avail));
					washerInUseA.setText(Integer.toString(inUse));
					washerCompleteA.setText(Integer.toString(complete));

					weightPerMachine = 96 / total;

					washerAvailA
							.setLayoutParams(new TableRow.LayoutParams(0,
									LayoutParams.WRAP_CONTENT, weightPerMachine
											* avail));
					washerInUseA
							.setLayoutParams(new TableRow.LayoutParams(0,
									LayoutParams.WRAP_CONTENT, weightPerMachine
											* inUse));
					washerCompleteA.setLayoutParams(new TableRow.LayoutParams(
							0, LayoutParams.WRAP_CONTENT, weightPerMachine
									* complete));
				} else {
					washersA.setVisibility(View.INVISIBLE);
					lineA.setVisibility(View.INVISIBLE);
				}

				avail = object.getInt("dryerAvail");
				total = object.getInt("dryerTotal");
				inUse = object.getInt("dryerInUse");
				complete = object.getInt("dryerComplete");

				if (total > 0) {
					dryersA.setVisibility(View.VISIBLE);
					dryerAvailA.setText(Integer.toString(avail));
					dryerInUseA.setText(Integer.toString(inUse));
					dryerCompleteA.setText(Integer.toString(complete));

					weightPerMachine = 96 / total;
					dryerAvailA
							.setLayoutParams(new TableRow.LayoutParams(0,
									LayoutParams.WRAP_CONTENT, weightPerMachine
											* avail));
					dryerInUseA
							.setLayoutParams(new TableRow.LayoutParams(0,
									LayoutParams.WRAP_CONTENT, weightPerMachine
											* inUse));
					dryerCompleteA.setLayoutParams(new TableRow.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, weightPerMachine
									* complete));
				} else {
					dryersA.setVisibility(View.INVISIBLE);
					lineA.setVisibility(View.INVISIBLE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			setTime();
		}
	}

	private void setTime() {
		Time now = new Time();
		now.setToNow();
		String hours = now.format("%l:%M");
		time.setText("Status as of" + hours);
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
		builder.setCancelable(false);
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
					building = buildings[selected].replace(" ", "_");
					new CallAPI().execute(statusURL + building, "status");
					dialog.dismiss();
				}
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onRefreshStarted(View view) {
		getStatus(building);
	}
}