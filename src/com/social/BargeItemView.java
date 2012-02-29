package com.social;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BargeItemView extends LinearLayout {
	public TextView bargeNameView;
	public TextView bargeTimeView;
	public ImageView bargeStatusImage;

	public BargeItemView(Context context, String bargeName, String bargeTime,
			int ind) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.list, this, true);

		// initilize eements of the row
		bargeNameView = (TextView) findViewById(R.id.bargetextView1);
		bargeTimeView = (TextView) findViewById(R.id.statustextView1);
		bargeStatusImage = (ImageView) findViewById(R.id.statusimageView1);

		bargeNameView.setText(bargeName);
		bargeTimeView.setText(bargeTime);

		setAptDrawable(ind);
		setItemWarning(bargeTime);

	}

	public void setBargeName(String foodName) {
		bargeNameView.setText(foodName);
	}

	public void setBargeTime(String time) {
		bargeTimeView.setText(time);
		Log.d("Time string", time);
	}

	public void setItemWarning(String bargeTime)
	{
		if(bargeTime.charAt(0)!='0')
		{
			Drawable bgred=getResources().getDrawable(R.drawable.back_red);
			bgred.setAlpha(100);
			Log.d("color", "colour is set");
//			this.setBackgroundResource(R.drawable.back_red);
			this.setBackgroundDrawable(bgred);
		}
	}

	public void setAptDrawable(int ind) {
		if (ind == 0) {
			Log.d("nullcheck", ind + " is 0");
			bargeStatusImage.setImageResource(R.drawable.yellow);
		} else if (ind == 1) {
			Log.d("nullcheck", ind + " is 1");
			bargeStatusImage.setImageResource(R.drawable.green);
		} else if (ind == 2) {
			Log.d("nullcheck", ind + " is 2");
			bargeStatusImage.setImageResource(R.drawable.red);
		} else {
			Log.d("nullcheck", ind + "");
		}
	}

}
