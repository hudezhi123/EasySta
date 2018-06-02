package easyway.Mobile.SiteRules;

import easyway.Mobile.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.ArrayList;

/*
 * 文件类型Adapter
 */
public class SRTypeAdapter extends BaseAdapter {
	private ArrayList<SiteRuleType> mLists;
	private LayoutInflater mInflater;
	private IDataChange iDataChange;
	
	public SRTypeAdapter(Context context, ArrayList<SiteRuleType> list) {
		super();
		this.mLists = list;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		if (mLists == null)
			return 0;
		else
			return mLists.size();
	}

	public Object getItem(int i) {
		if (mLists == null)
			return null;
		else
			return mLists.get(i);
	}

	public long getItemId(int i) {
		return i;
	}
	
	public void setData(ArrayList<SiteRuleType> list) {
		this.mLists = list;
	}
	
	public void setIDataChange(IDataChange iDataChange) {
		this.iDataChange = iDataChange;
	}

	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		ViewHolder holder;
		SiteRuleType obj = (SiteRuleType) getItem(position);
		if (obj == null)
			return null;

		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.sr_type_item, null);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.checkBox.setText(obj.text);
		holder.checkBox.setChecked(obj.check);
		holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (iDataChange != null)
					iDataChange.ItemClick(position, isChecked);	
			}
		});
		

		return convertView;
	}

	class ViewHolder {
		CheckBox checkBox;
	}
}
