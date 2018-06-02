package easyway.Mobile.Broadcast;

import java.util.ArrayList;

import easyway.Mobile.R.drawable;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BroadAreaAdapter extends BaseAdapter {
	private ArrayList<BroadcastInfo> listBroadcastArea;
	private Context context;

	public BroadAreaAdapter(Context context,
			ArrayList<BroadcastInfo> listBroadcastArea) {
		this.context = context;
		this.listBroadcastArea = listBroadcastArea;
	}

	public int getCount() {
		return listBroadcastArea.size();
	}

	public BroadcastInfo getItem(int arg0) {
		return listBroadcastArea.get(arg0);
	}

	public long getItemId(int arg0) {
		return getItem(arg0).id;
	}

	public View getView(int position, View arg1, ViewGroup arg2) {
		LinearLayout tableRowRoot = new LinearLayout(context);
		if (position == 0) {
			tableRowRoot.setBackgroundDrawable(context.getResources()
					.getDrawable(drawable.bg_list_text_top));
		} else if (position == listBroadcastArea.size() - 1) {
			tableRowRoot.setBackgroundDrawable(context.getResources()
					.getDrawable(drawable.bg_list_text_bottom));
		} else {
			tableRowRoot.setBackgroundDrawable(context.getResources()
					.getDrawable(drawable.bg_list_text_middle));
		}
		LinearLayout trItem = new LinearLayout(context);

		TextView txBaName = new TextView(context);
		txBaName.setText("	" + getItem(position).Area);
		txBaName.setTextColor(Color.BLACK);
		txBaName.setTextSize(20);
		trItem.addView(txBaName);

		tableRowRoot.addView(trItem);
		return tableRowRoot;
	}

}
