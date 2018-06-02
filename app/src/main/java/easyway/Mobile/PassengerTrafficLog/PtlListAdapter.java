package easyway.Mobile.PassengerTrafficLog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import easyway.Mobile.R;
import easyway.Mobile.util.DateUtil;

/**
 * Created by JSC on 2017/11/24.
 */

public class PtlListAdapter extends BaseAdapter {
    private Context context;
    private List<SearchPtlLogBean.DataBean> list;

    public PtlListAdapter(Context context, List<SearchPtlLogBean.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_ptl_item_layout, null);
            holder = new ViewHolder();

            holder.time = (TextView) convertView.findViewById(R.id.tv_ptl_item_time);
            holder.title = (TextView) convertView.findViewById(R.id.tv_ptl_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String date = list.get(position).getWorkDate();

        String str = DateUtil.formatDate(date, DateUtil.HH_MM);

        holder.time.setText(str);

        holder.title.setText(DateUtil.getClassesOfDate(date));
        return convertView;
    }

    static class ViewHolder {
        TextView time;
        TextView title;

    }
}
