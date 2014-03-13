package com.amv.binglaundrychecker2;

import android.app.Activity;
import android.app.Fragment;

abstract class ViewFragment extends Fragment implements UpdateInterface{
	ViewChangeListener viewChangeListener;

	public interface ViewChangeListener {
		public void update();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			viewChangeListener = (ViewChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ViewChangeListener");
		}
	}
}