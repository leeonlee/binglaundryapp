package com.amv.binglaundrychecker2;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


class Graph{
		int TOTAL_WEIGHT = 96;
		public TextView washerAvail, washerComplete, washerInUse, dryerAvail,
				dryerComplete, dryerInUse, name;

		public TableRow washers, dryers, line;
		
		public TableLayout tableLayout;

		public Graph() {
		}

		public void changeWasherSizes(int total, int heightInDp, int avail,
				int inUse, int complete) {
			float weightPerMachine = TOTAL_WEIGHT / total;
			washerAvail.setLayoutParams(new TableRow.LayoutParams(0,
					heightInDp, weightPerMachine * avail));
			washerInUse.setLayoutParams(new TableRow.LayoutParams(0,
					heightInDp, weightPerMachine * inUse));
			washerComplete.setLayoutParams(new TableRow.LayoutParams(0,
					heightInDp, weightPerMachine * complete));
		}

		public void changeDryerSizes(int total, int heightInDp, int avail,
				int inUse, int complete) {
			float weightPerMachine = TOTAL_WEIGHT / total;
			dryerAvail.setLayoutParams(new TableRow.LayoutParams(0,
					heightInDp, weightPerMachine * avail));
			dryerInUse.setLayoutParams(new TableRow.LayoutParams(0,
					heightInDp, weightPerMachine * inUse));
			dryerComplete.setLayoutParams(new TableRow.LayoutParams(0,
					heightInDp, weightPerMachine * complete));
		}

		public void setValues(JSONObject object, int heightInDp) {
			try {
				setLineVisible();
				name.setText(object.getString("name"));
				int avail = object.getInt("washerAvail");
				int total = object.getInt("washerTotal");
				int inUse = object.getInt("washerInUse");
				int complete = object.getInt("washerComplete");

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

		public void setWashersInvisible() {
			washers.setVisibility(View.INVISIBLE);
			setLineInvisible();
		}

		public void setDryersInvisible() {
			dryers.setVisibility(View.INVISIBLE);
			setLineInvisible();
		}

		public void setWashersVisible() {
			washers.setVisibility(View.VISIBLE);
		}

		public void setDryerBarInvisible() {
			dryers.setVisibility(View.INVISIBLE);
			setLineInvisible();
		}

		public void setDryersVisible() {
			dryers.setVisibility(View.VISIBLE);
		}

		public void setLineInvisible() {
			line.setVisibility(View.INVISIBLE);
		}

		public void setLineVisible() {
			line.setVisibility(View.VISIBLE);
		}
		
		public void setName(TextView a){
			name = a;
		}

		public void setGraphInvisible() {
			tableLayout.setVisibility(View.INVISIBLE);
		}
		
		public void setGraphVisible() {
			tableLayout.setVisibility(View.VISIBLE);
		}
	}