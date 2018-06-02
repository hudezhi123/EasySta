package easyway.Mobile.ReportChart;

import java.util.ArrayList;
import easyway.Mobile.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 报表一览Adapter
 */
public class ReportsAdapter extends BaseAdapter {
    private ArrayList<DataForm> mList;
//    private Context context;
    private LayoutInflater mInflater;

    public ReportsAdapter(Context context,
            ArrayList<DataForm> list) {
        mInflater = LayoutInflater.from(context);
//        this.context = context;
        mList = list;
    }

    public int getCount() {
    	if (mList == null)
    		return 0;
        return mList.size();
    }

    public DataForm getItem(int position) {
    	if (mList == null)
    		return null;
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<DataForm> models) {
        mList = models;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		DataForm obj = mList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rc_reports_item,
                    null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView
                    .findViewById(R.id.txtName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtName.setText(obj.Name);
 
        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
    }

}
