package easyway.Mobile.SiteRules;

import java.util.ArrayList;
import java.util.Map;

import easyway.Mobile.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MyLocalFileAdapter extends BaseExpandableListAdapter{
	
	private Map<String, ArrayList<SiteRule>> dataList;
	
	private Activity act;
	
	private ArrayList<String> typeList;
	
	public MyLocalFileAdapter(ArrayList<String> typeList,Map<String, ArrayList<SiteRule>> dataList, Activity act) {
		super();
		this.dataList = dataList;
		this.act = act;
		this.typeList = typeList;
	}

	@Override
	public int getGroupCount() {
		
		return typeList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		String key = typeList.get(groupPosition);
		ArrayList<SiteRule> data = dataList.get(key);
		return data.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return typeList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		String key = typeList.get(groupPosition);
		ArrayList<SiteRule> data = dataList.get(key);
		SiteRule child = data.get(childPosition);
		return child;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View group;
		TextView text;
		if(convertView == null){
			group = View.inflate(act, R.layout.myfile_group, null);
			text = (TextView)group.findViewById(R.id.ReadFileGroup);
			group.setTag(text);
		}else{
			group = convertView;
			text = (TextView)group.getTag();
		}
		String type = typeList.get(groupPosition);
		text.setText("           " + type);
		
		return group;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String type = typeList.get(groupPosition);
		ArrayList<SiteRule> data =  dataList.get(type);
		SiteRule sr = data.get(childPosition);
		View child;
		if(convertView == null){
			child = View.inflate(act, R.layout.my_localfile_group, null);
		}else{
			child = convertView;
		}
		TextView text = (TextView)child.findViewById(R.id.ReadFileGroup);
		
		text.setText(sr.Title);
		return child;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
