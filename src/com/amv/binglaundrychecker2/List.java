package com.amv.binglaundrychecker2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class List extends Activity {
	private ProgressDialog progDialog;
	private String title, washers, dryers, url;
	private String[] buildings;
	private String building, first, next;
	int selected;
	boolean hasNext;
	private WebView webView;
	private TextView buildingName, statusA, statusB, buildingNameB, status1,
			status2, time;
	boolean switcher;
	int numComplete;
	private TableLayout table;
	private String statusURL = "http://binglaundry.herokuapp.com/status/";
	private String communityURL = "http://binglaundry.herokuapp.com/communities";
	private String buildingURL = "http://binglaundry.herokuapp.com/buildings/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blding_list);
		// load the setup
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		// if building pref is not there then set building to null
		building = prefs.getString("building", null);
		// url of esuds to scrape data from
		table = (TableLayout) findViewById(R.id.tableLayout);
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

	private void initializeTextViews() {
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-BoldCondensed.ttf");
		Typeface tf2 = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Medium.ttf");
		buildingName = (TextView) findViewById(R.id.building);
		buildingName.setTypeface(tf);
		// statusA = A side of first building
		statusA = (TextView) findViewById(R.id.statusA);
		statusA.setTypeface(tf2);
		// statusB = B side of first building
		statusB = (TextView) findViewById(R.id.statusB);
		statusB.setTypeface(tf2);
		// building name of second building
		buildingNameB = (TextView) findViewById(R.id.buildingB);
		buildingNameB.setTypeface(tf);
		// status 1 = A side of second building
		status1 = (TextView) findViewById(R.id.status1);
		status1.setTypeface(tf2);
		// status 2 = B side of second building
		status2 = (TextView) findViewById(R.id.status2);
		status2.setTypeface(tf2);
		time = (TextView) findViewById(R.id.time);
		table.setLongClickable(true);
		table.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// new CallAPI().execute(apiURL + "Lehman", "status");
				new CallAPI().execute(communityURL, "community");
				return true;
			}
		});
	}

	private void getStatus(String building) {
		// setting values to reset to while it loads
		buildingName.setText("");
		statusA.setText("");
		statusB.setText("");
		buildingNameB.setText("");
		status1.setText("");
		status2.setText("");
		// initially set that the building has two sides (A and B)
		hasNext = true;
		webView.loadUrl(url + first);
		// switch for the webview to load a second time for the second building
		switcher = true;
		progDialog = ProgressDialog.show(List.this, "", "Working..", true);
	}

	private void setCommunity() {
		new CallAPI().execute(communityURL, "community");
	}

	private void setBuilding(String community) {
		new CallAPI().execute(buildingURL + community, "building");
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case com.amv.binglaundrychecker2.R.id.about:
			Intent i = new Intent(List.this,
					com.amv.binglaundrychecker2.About.class);
			startActivity(i);
			break;
		case com.amv.binglaundrychecker2.R.id.reset:
			setCommunity();
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("FIRSTRUN", true);
			editor.commit();
			break;
		}
		return true;
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
				String community = communities[selected].replace(" ", "_");
				new CallAPI().execute(buildingURL + community, "building");
				dialog.dismiss();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public void postStatusCall(JSONArray json) {
		try {
			statusA.setText(json.getJSONObject(0).getString("name"));
			//statusA.setText(json.getJSONObject(0).getJSONArray("dryerTimes")
			//		.getString(0));
			Log.i("", json.getJSONObject(0).toString(1));
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
				System.out.println(buildings[selected]);
				String building = buildings[selected].replace(" ", "_");
				new CallAPI().execute(statusURL + building, "status");
				dialog.dismiss();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}