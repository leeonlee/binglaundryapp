package com.amv.binglaundrychecker2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

public class OldViewGraph {
	public TextView statusWash, statusDry, name;

	public OldViewGraph() {

	}

	public void setValues(JSONObject object) {
		try {
			name.setText(object.getString("name"));
			int avail = object.getInt("washerAvail");
			int total = object.getInt("washerTotal");
			int inUse = object.getInt("washerInUse");
			int complete = object.getInt("washerComplete");
			JSONArray timeArray;

			String wash = avail + "/" + total + " washers available" + "\n"
					+ complete + " washers completed" + "\n" + inUse
					+ " washers in use";

			if (inUse != 0) {
				timeArray = object.getJSONArray("washerTimes");
				wash += getEarliest(timeArray);
			}

			statusWash.setText(wash);

			avail = object.getInt("dryerAvail");
			total = object.getInt("dryerTotal");
			inUse = object.getInt("dryerInUse");
			complete = object.getInt("dryerComplete");
			String dry = avail + "/" + total + " dryers available" + "\n"
					+ complete + " dryers completed" + "\n" + inUse
					+ " dryers in use";

			if (inUse != 0) {
				timeArray = object.getJSONArray("dryerTimes");
				dry += getEarliest(timeArray);
			}

			statusDry.setText(dry);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private String getEarliest(JSONArray jsonArray) {
		int length = jsonArray.length();
		if (length != 0) {
			try {
				return jsonArray.getString(0) + " min";
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}