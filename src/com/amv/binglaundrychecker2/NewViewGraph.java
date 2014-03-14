package com.amv.binglaundrychecker2;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

class NewViewGraph {
	int TOTAL_WEIGHT = 96;
	public TextView washerAvail, washerComplete, washerInUse, dryerAvail,
			dryerComplete, dryerInUse, name;

	public TableRow washers, dryers, line;

	public TableLayout tableLayout;

	public Context context;

	public NewViewGraph(Context context) {
		this.context = context;
	}

	public String washerTimes, dryerTimes;

	public void changeWasherSizes(int total, int heightInDp, int avail,
			int inUse, int complete) {
		float weightPerMachine = TOTAL_WEIGHT / total;
		washerAvail.setLayoutParams(new TableRow.LayoutParams(0, heightInDp,
				weightPerMachine * avail));
		washerInUse.setLayoutParams(new TableRow.LayoutParams(0,
				LayoutParams.MATCH_PARENT, weightPerMachine * inUse));
		washerComplete.setLayoutParams(new TableRow.LayoutParams(0, heightInDp,
				weightPerMachine * complete));
	}

	public void changeDryerSizes(int total, int heightInDp, int avail,
			int inUse, int complete) {
		float weightPerMachine = TOTAL_WEIGHT / total;
		dryerAvail.setLayoutParams(new TableRow.LayoutParams(0, heightInDp,
				weightPerMachine * avail));
		dryerInUse.setLayoutParams(new TableRow.LayoutParams(0,
				LayoutParams.MATCH_PARENT, weightPerMachine * inUse));
		dryerComplete.setLayoutParams(new TableRow.LayoutParams(0, heightInDp,
				weightPerMachine * complete));
	}

	public void setValues(JSONObject object, int heightInDp) {
		washerInUse.setLines(2);
		try {
			setLineVisible();
			name.setText(object.getString("name"));
			int avail = object.getInt("washerAvail");
			int total = object.getInt("washerTotal");
			int inUse = object.getInt("washerInUse");
			int complete = object.getInt("washerComplete");
			washerTimes = object.getString("washerTimes");

			if (total > 0) {
				setWashersVisible();
				washerAvail.setText(Integer.toString(avail));
				washerInUse.setText(Integer.toString(inUse));
				washerComplete.setText(Integer.toString(complete));

				changeWasherSizes(total, heightInDp, avail, inUse, complete);
			} else {
				setWashersInvisible();
			}

			avail = object.getInt("dryerAvail");
			total = object.getInt("dryerTotal");
			inUse = object.getInt("dryerInUse");
			complete = object.getInt("dryerComplete");
			dryerTimes = object.getString("dryerTimes");

			if (total > 0) {
				setDryersVisible();
				dryerAvail.setText(Integer.toString(avail));
				dryerInUse.setText(Integer.toString(inUse));
				dryerComplete.setText(Integer.toString(complete));

				changeDryerSizes(total, heightInDp, avail, inUse, complete);
			} else {
				setDryersInvisible();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void setClickListeners() {
		washerInUse.setClickable(true);
		dryerInUse.setClickable(true);

		washerInUse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, washerTimes, duration);
				toast.show();
			}
		});

		dryerInUse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, dryerTimes, duration);
				toast.show();
			}
		});
	}

	public void setWashersInvisible() {
		washers.setVisibility(View.GONE);
		setLineInvisible();
	}

	public void setDryersInvisible() {
		dryers.setVisibility(View.GONE);
		setLineInvisible();
	}

	public void setWashersVisible() {
		washers.setVisibility(View.VISIBLE);
	}

	public void setDryerBarInvisible() {
		dryers.setVisibility(View.GONE);
		setLineInvisible();
	}

	public void setDryersVisible() {
		dryers.setVisibility(View.VISIBLE);
	}

	public void setLineInvisible() {
		line.setVisibility(View.GONE);
	}

	public void setLineVisible() {
		line.setVisibility(View.VISIBLE);
	}

	public void setGraphInvisible() {
		tableLayout.setVisibility(View.GONE);
	}

	public void setGraphVisible() {
		tableLayout.setVisibility(View.VISIBLE);
	}
}