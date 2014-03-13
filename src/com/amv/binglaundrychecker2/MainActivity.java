package com.amv.binglaundrychecker2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import com.amv.binglaundrychecker2.NewViewFragment.OnViewChangeListener;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity implements OnRefreshListener, OnViewChangeListener {
	private ProgressDialog progDialog;
	private ActionBar actionBar;
	private String building;
	int selected;
	NewViewFragment frag;
	private String statusURL = "http://binglaundry.herokuapp.com/status/";
	private String communityURL = "http://binglaundry.herokuapp.com/communities";
	private String buildingURL = "http://binglaundry.herokuapp.com/buildings/";

	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab tab1 = actionBar.newTab().setText("New view");
		ActionBar.Tab tab2 = actionBar.newTab().setText("Old View");

		Fragment newViewFrag = new NewViewFragment();
		Fragment oldViewFrag = new OldViewFragment();

		tab1.setTabListener(new MyTabListener(newViewFrag));
		tab2.setTabListener(new MyTabListener(oldViewFrag));

		actionBar.addTab(tab1);
		actionBar.addTab(tab2);

		// load saved configurations
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		// if building pref is not there then set building to null
		building = prefs.getString("building", null);
		if (building == null) {
			getActionBar().setTitle("Laundry Status");

		} else {
			getActionBar().setTitle(building.replace("_", " "));
			getActionBar().setSubtitle("Laundry Status");
			//getStatus();
		}
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

	private void setCommunity() {
		callProgDialog();
		new CallAPI().execute(communityURL, "community");
	}

	public void setBuilding(String community) {
		callProgDialog();
		new CallAPI().execute(buildingURL + community, "building");
	}

	// The three types are used for- params, progress, result
	private class CallAPI extends AsyncTask<String, String, String[]> {
		// Get JSON response from url supplied
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

		}
	}

	private void postStatusCall(final JSONArray json) {
		NewViewFragment fragment = (NewViewFragment) getFragmentManager()
				.findFragmentByTag("FRAGMENT");

		fragment.update(json);
		if (mPullToRefreshLayout.isRefreshing()) {
			mPullToRefreshLayout.setRefreshComplete();
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
					SharedPreferences.Editor editor = getPreferences(
							MODE_PRIVATE).edit();
					editor.putString("building", building);
					editor.commit();

					building = buildings[selected].replace(" ", "_");
					dialog.dismiss();

					getActionBar().setTitle(building);
					getActionBar().setSubtitle("Laundry Status");
					getStatus();
				}
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		progDialog.dismiss();
	}

	public void getStatus() {
		mPullToRefreshLayout.setRefreshing(true);
		new CallAPI().execute(statusURL + building, "status");
	}

	private void callProgDialog() {
		progDialog = new ProgressDialog(this);
		progDialog.setMessage("Loading..");
		progDialog.show();
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

	@Override
	public void onRefreshStarted(View view) {
		getStatus();
	}

	@Override
	public void updateView() {
		getStatus();
	}
}
