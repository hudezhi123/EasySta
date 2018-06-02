package easyway.Mobile.SellTicktLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import easyway.Mobile.R;
import easyway.Mobile.util.DateUtil;

/**
 * Created by boy on 2017/11/21.
 */

public class SellTicketAdapter extends BaseAdapter {

    private Context context;
    private List<SellTicketLog> mList;
    private LayoutInflater inflater;

    public SellTicketAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
        inflater = LayoutInflater.from(this.context);
    }

    public void setData(List<SellTicketLog> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public SellTicketLog getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sell_log_item, parent, false);
            holder = new ViewHolder();
            holder.textTime = (TextView) convertView.findViewById(R.id.text_time_sell_ticket_item);
            holder.textTitle = (TextView) convertView.findViewById(R.id.text_title_sell_ticket_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SellTicketLog logItem = getItem(position);
        holder.textTime.setText(DateUtil.formatDate(logItem.WorkTime, DateUtil.HH_MM));
        int flag = DateUtil.getWhiteOrNight(logItem.WorkDate);
        if (flag == DateUtil.DAYTIME) {
            holder.textTitle.setText("售票工作日志" + "(白班)");
        } else if (flag == DateUtil.NIGHT) {
            holder.textTitle.setText("售票工作日志" + "(夜班)");
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textTime;
        TextView textTitle;
    }
}
