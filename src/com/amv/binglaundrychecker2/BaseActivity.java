package com.amv.binglaundrychecker2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Fragment sampleFragment = getSampleFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// This method is for menu. This menu items will appear in all
		// activities extends this class. I have use this menus to navigate
		// between activities. You can change this code as you wish
		//

		switch (item.getItemId()) {
		case R.id.action_scrollview:
			Toast.makeText(this, "Pull to Refresh in Scroll View",
					Toast.LENGTH_SHORT).show();

			Intent x = new Intent(this, List.class);
			startActivity(x);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// This method will override by child class. Then base class can get the
	// fragment
	protected Fragment getSampleFragment() {
		return null;
	}
}
