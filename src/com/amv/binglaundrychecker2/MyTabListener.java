package com.amv.binglaundrychecker2;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;

public class MyTabListener implements ActionBar.TabListener{
	Fragment fragment;
	
	public MyTabListener(Fragment fragment){
		this.fragment = fragment;
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		ft.replace(R.id.fragment_container, fragment, "FRAGMENT");
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		ft.remove(fragment);
	}


}
