package com.amv.binglaundrychecker2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.TextView;

public class OldViewGraph {
	public TextView statusWash, statusDry, name;

	public OldViewGraph() {

	}

	public void setVisible() {
		statusWash.setVisibility(View.VISIBLE);
		statusDry.setVisibility(View.VISIBLE);
		name.setVisibility(View.VISIBLE);
	}

	public void setInvisible() {
		statusWash.setVisibility(View.GONE);
		statusDry.setVisibility(View.GONE);
		name.setVisibility(View.GONE);
	}

	public void setValues(JSONObject object) {
		try {
			name.setText(object.getString("name"));
			int avail = object.getInt("washerAvail");
			int total = object.getInt("washerTotal");
			int inUse = object.getInt("washerInUse");
			int complete = object.getInt("washerComplete");
			String times = object.getString("washerTimes");
			Pattern pattern = Pattern.compile("^(\\d+ \\w+),");

			if (total == 0)
				setInvisible();
			else {
				setVisible();
				String wash = avail + "/" + total + " washers available" + "\n"
						+ complete + " washers completed" + "\n" + inUse
						+ " washers in use";

				if (inUse != 0) {
					Matcher match = pattern.matcher(times);
					if (match.find()) {
						wash += "\nEarliest: " + match.group(1);
					}
				}

				statusWash.setText(wash);
			}

			avail = object.getInt("dryerAvail");
			total = object.getInt("dryerTotal");
			inUse = object.getInt("dryerInUse");
			complete = object.getInt("dryerComplete");
			times = object.getString("dryerTimes");

			if (total == 0) {
				setInvisible();
			} else {
				setVisible();
				String dry = avail + "/" + total + " dryers available" + "\n"
						+ complete + " dryers completed" + "\n" + inUse
						+ " dryers in use";

				if (inUse != 0) {
					Matcher match = pattern.matcher(times);
					if (match.find()) {
						dry += "\nEarliest: " + match.group(1);
					}
				}

				statusDry.setText(dry);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}