package com.amv.binglaundrychecker2;

import java.util.Scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amv.binglaundrychecker2.R;

public class List extends Activity {
	private ProgressDialog progDialog;
	private String title, washers, dryers, url;
	private String[] buildings;
	private String building, first, next;
	private String[] communities = { "Hinman", "MountainView", "Dickinson",
			"College in the Woods", "Susquehanna", "Newing" };
	int selected;
	boolean hasNext;
	private WebView webView;
	private TextView buildingName, statusA, statusB, buildingNameB, status1, status2,
			time;
	boolean switcher;
	int numComplete;
	private TableLayout table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blding_list);
		//load the setup
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		//if building pref is not there then set building to null
		building = prefs.getString("building", null);
		//url of esuds to scrape data from
		url = "http://binghamton-asi.esuds.net/RoomStatus/showRoomStatus.i?locationId=";
		table = (TableLayout) findViewById(R.id.tableLayout);
		initializeTextViews();
		if (building != null) {
			getStatus(building);
		} else {
			//start the building setup
			setCommunity();
		}
	}
	//save the setup 
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
		//statusA = A side of first building
		statusA = (TextView) findViewById(R.id.statusA);
		statusA.setTypeface(tf2);
		//statusB = B side of first building
		statusB = (TextView) findViewById(R.id.statusB);
		statusB.setTypeface(tf2);
		//building name of second building
		buildingNameB = (TextView) findViewById(R.id.buildingB);
		buildingNameB.setTypeface(tf);
		//status 1 = A side of second building
		status1 = (TextView) findViewById(R.id.status1);
		status1.setTypeface(tf2);
		//status 2 = B side of second building
		status2 = (TextView) findViewById(R.id.status2);
		status2.setTypeface(tf2);
		time = (TextView) findViewById(R.id.time);
		table.setLongClickable(true);
		table.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				getStatus(building);
				return true;
			}
		});
	}

	private void getStatus(String building) {
		//setting values to reset to while it loads
		buildingName.setText("");
		statusA.setText("");
		statusB.setText("");
		buildingNameB.setText("");
		status1.setText("");
		status2.setText("");
		//use webview to get onto Esuds in the background
		webView = new WebView(List.this);
		//Esuds works with javascript functions, have to let them load before scraping
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyClient());
		//loads the javascript code into an interface called "HtmlViewer"
		webView.addJavascriptInterface(new JIFace(List.this), "HtmlViewer");
		//initially set that the building has two sides (A and B)
		hasNext = true;
		if (building.equals("Lehman")) {
			first = "1008732";
			next = "1008733";
		} else if (building.equals("Cleveland")) {
			first = "7205";
			next = "1008730";
		} else if (building.equals("Hughes")) {
			first = "1008728";
			next = "1008729";
		} else if (building.equals("Roosevelt")) {
			first = "1008721";
			next = "1008723";
		} else if (building.equals("Smith")) {
			first = "1008725";
			next = "1008726";
		} else if (building.equals("Cayuga")) {
			first = "1008750";
			hasNext = false;
		} else if (building.equals("Mohawk")) {
			first = "1008719";
			hasNext = false;
		} else if (building.equals("Oneida")) {
			first = "1008711";
			hasNext = false;
		} else if (building.equals("Onondaga")) {
			first = "1008717";
			hasNext = false;
		} else if (building.equals("Seneca")) {
			first = "1008713";
			hasNext = false;
		} else if (building.equals("Champlaign")) {
			first = "1039287";
			hasNext = false;
		} else if (building.equals("Digman")) {
			first = "1039187";
			hasNext = false;
		} else if (building.equals("Rafuse")) {
			first = "1039267";
			hasNext = false;
		} else if (building.equals("Whitney")) {
			first = "1039234";
			next = "1039196";
		} else if (building.equals("Cascade")) {
			first = "2043877";
			hasNext = false;
		} else if (building.equals("Marcy")) {
			first = "2044114";
			hasNext = false;
		} else if (building.equals("Hunter")) {
			first = "2043913";
			hasNext = false;
		} else if (building.equals("Bingham")) {
			first = "1033795";
			next = "1033937";
		} else if (building.equals("Broome")) {
			first = "1040623";
			next = "1040603";
		} else if (building.equals("Delaware")) {
			first = "1040605";
			next = "1040604";
		} else if (building.equals("Endicott")) {
			first = "1040646";
			next = "1040624";
		} else if (building.equals("Brandywine")) {
			first = "1013011";
			hasNext = false;
		} else if (building.equals("Choconut")) {
			first = "1013007";
			hasNext = false;
		} else if (building.equals("Glenwood")) {
			first = "1013019";
			hasNext = false;
		} else if (building.equals("Nanticoke")) {
			first = "1012990";
			hasNext = false;
		}
		webView.loadUrl(url + first);
		//switch for the webview to load a second time for the second building
		switcher = true;
		progDialog = ProgressDialog.show(List.this, "", "Working..", true);
	}

	private void setCommunity() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Community");
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
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		Button positiveButton = alert
				.getButton(DialogInterface.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (selected != -1) {
					setBuilding(communities[selected]);
					alert.dismiss();
				}
			}
		});
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

	private void setBuilding(String community) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Building");
		builder.setCancelable(false);
		selected = -1;
		buildings = null;
		if (community.equals("Hinman")) {
			buildings = new String[5];
			buildings[0] = "Cleveland";
			buildings[1] = "Hughes";
			buildings[2] = "Lehman";
			buildings[3] = "Roosevelt";
			buildings[4] = "Smith";
		} else if (community.equals("MountainView")) {
			buildings = new String[4];
			buildings[0] = "Cascade";
			buildings[1] = "Marcy";
			buildings[2] = "Hunter";
			buildings[3] = "Windham";
		} else if (community.equals("College in the Woods")) {
			buildings = new String[5];
			buildings[0] = "Cayuga";
			buildings[1] = "Mohawk";
			buildings[2] = "Oneida";
			buildings[3] = "Onondaga";
			buildings[4] = "Seneca";
		} else if (community.equals("Dickinson")) {
			buildings = new String[4];
			buildings[0] = "Champlaign";
			buildings[1] = "Digman";
			buildings[2] = "Rafuse";
			buildings[3] = "Whitney";
		} else if (community.equals("Newing")) {
			buildings = new String[4];
			buildings[0] = "Bingham";
			buildings[1] = "Broome";
			buildings[2] = "Delaware";
			buildings[3] = "Endicott";
		} else if (community.equals("Susquehanna")) {
			buildings = new String[4];
			buildings[0] = "Brandywine";
			buildings[1] = "Choconut";
			buildings[2] = "Glenwood";
			buildings[3] = "Nanticoke";
		}

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
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		Button positiveButton = alert
				.getButton(DialogInterface.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (selected != -1) {
					building = buildings[selected];
					getStatus(building);
					alert.dismiss();
				}
			}
		});
	}

	class MyClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			//when page is done loading, inject javascript to return the html
			webView.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByTagName('html')[0].innerHTML);");
			progDialog.dismiss();
		}
	}

	class JIFace {
		private Context ctx;
		JIFace(Context ctx) {
			this.ctx = ctx;
		}
		//add @JavascriptInterface to be compatible with Android 4.2
		//once the html has been scraped, retrieve all relevant information
		@JavascriptInterface
		public void showHTML(String html) {
			Scanner input = new Scanner(html);
			//earliest washer/dryers available
			int earliestWash = 0;
			int earliestDry = 0;
			//how many washers/dryers are available
			int washAvail = 0;
			int dryAvail = 0;
			//how many are complete and waiting to be picked up
			int washComplete = 0;
			int dryComplete = 0;
			//how many are currently in true
			int washInUse = 0;
			int dryInUse = 0;
			//all the information that is seen is pertaining to washers until
			//"footer" is seen in the html
			boolean wash = true;
			while (input.hasNextLine()) {
				String string = input.nextLine();
				//parse each individual line of html
				if (string.indexOf("font class=\"title\">") != -1) {
					title = string.substring(20, string.indexOf("<", 20));
				}
				if (string.indexOf("footer") != -1)
					wash = false;
				if (string.indexOf("Available") != -1)
					if (wash)
						washAvail++;
					else
						dryAvail++;
				if (string.indexOf("Cycle Complete") != -1) {
					if (wash)
						washComplete++;
					else
						dryComplete++;
				}
				if (string.indexOf("In Use") != -1) {
					if (wash) {
						washInUse++;
						input.nextLine();
						String timeLeft = input.nextLine();
						timeLeft = timeLeft.substring(4, timeLeft.indexOf("<", 4));
						if (earliestWash == 0 || Integer.parseInt(timeLeft) < earliestWash)
							earliestWash = Integer.parseInt(timeLeft);
					} else {
						dryInUse++;
						input.nextLine();
						String timeLeft = input.nextLine();
						timeLeft = timeLeft.substring(4, timeLeft.indexOf("<", 4));
						if (earliestDry == 0 || Integer.parseInt(timeLeft) < earliestDry)
							earliestDry = Integer.parseInt(timeLeft);
					}
				}
			}
			int washTotal = washAvail + washInUse + washComplete;
			int dryTotal = dryAvail + dryInUse + dryComplete;
			washers = washAvail + "/" + washTotal + " washers available" + "\n" + washComplete
					+ " washers completed" + "\n"
							+ washInUse + " washers in use";
			if (earliestWash != 0){
				washers += "\n(Earliest: "+earliestWash +" mins)";
			}
			dryers = dryAvail + "/" + dryTotal + " dryers available" + "\n" + dryComplete
					+ " dryers completed" + "\n"+ dryInUse + " dryers in use";
			if (earliestDry != 0){
				dryers += "\n(Earliest: "+earliestDry +" mins)";
			}
			//need to run on UI thread to be able to have textviews update on the spot
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (switcher) {
						buildingName.setText(title);
						statusA.setText(washers);
						statusB.setText(dryers);
						switcher = false;
						if (hasNext)
							webView.loadUrl(url + next);
						Time now = new Time();
						now.setToNow();
						String hours = now.format("%H");
						if (Integer.parseInt(hours) > 12) {
							hours = Integer
									.toString(Integer.parseInt(hours) - 12);
						}
						hours += now.format(":%M");
						time.setText("Status as of " + hours
								+ "\nHold the screen to refresh");
					} else {
						buildingNameB.setText(title);
						status1.setText(washers);
						status2.setText(dryers);
					}
				}
			});

		}
	}
}