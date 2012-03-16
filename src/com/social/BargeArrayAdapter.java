package com.social;

import java.util.List;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class BargeArrayAdapter extends ArrayAdapter<Model> {
	private final Activity context;
	private final List<Model> bargeNames;
	private final List<Model> bargeStatus;
	private final List<Model> bargeTimes;
	
	public BargeArrayAdapter(Activity context, List<Model> bargeList,
			List<Model> statusList, List<Model> timeList) {
		super(context, R.layout.list, bargeList);
		this.bargeNames = bargeList;
		this.context = context;
		this.bargeStatus = statusList;
		this.bargeTimes = timeList;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		int ind = 0;

		if (bargeStatus.get(position).getBargeStatus().equals("Docked")) {
			ind = 0;
		} else if (bargeStatus.get(position).getBargeStatus()
				.equals("Transporting")) {
			ind = 1;
		}

		else if (bargeStatus.get(position).getBargeStatus().equals("Stopped")) {
			ind = 2;
		}

		String name = bargeNames.get(position).getBargeName();
		String status = bargeStatus.get(position).getBargeStatus();
		String time = bargeTimes.get(position).getBargeTime();

		if (convertView == null) {
			Log.d("nullcheck",position+" is the position null");
			BargeItemView temp = new BargeItemView(context, name, time, ind);
			return temp;
		} else {
			Log.d("nullcheck",position+" is the position not null");
			((BargeItemView) convertView).setBargeName(name);
			((BargeItemView) convertView).setBargeTime(time);
			((BargeItemView) convertView).setAptDrawable(ind);
			return convertView;
		}

	}

}
