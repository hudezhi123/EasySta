package easyway.Mobile.DefaultReportToRepair;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import easyway.Mobile.R;
import easyway.Mobile.util.DateUtil;

/**
 * Created by JSC on 2017/11/30.
 */

public class DevReportListAdapter extends BaseAdapter {
    private Context context;
    private List<QueryAllReportRepairBean.DataBean> list;

    public DevReportListAdapter(Context context, List<QueryAllReportRepairBean.DataBean> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dev_reportlist_item_layout, null);
            holder = new ViewHolder();
            holder.reprotType = (TextView) convertView.findViewById(R.id.reprot_type);
            holder.reportTime = (TextView) convertView.findViewById(R.id.report_time);
            holder.imgattach = (TextView) convertView.findViewById(R.id.report_image);
            holder.reportLocation = (TextView) convertView.findViewById(R.id.report_location);
            holder.reportStatus = (TextView) convertView.findViewById(R.id.report_status);
            holder.reportRemark = (TextView) convertView.findViewById(R.id.report_remark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.reprotType.setText(list.get(position).getDeviceType());
        String time = list.get(position).getCreateTime();
        holder.reportTime.setText(DateUtil.formatDate(time, DateUtil.YYYY_MM_DD_HH_MM_SS));
        holder.reportLocation.setText(list.get(position).getDevicePosition());
        int status = list.get(position).getDeviceStatus();
        switch (status) {
            case 0:
                holder.reportStatus.setText("已上报");
                break;
            case 1:
                holder.reportStatus.setText("已接报");
                break;
            case 2:
                holder.reportStatus.setText("已转报");
                break;
            case 3:
                holder.reportStatus.setText("维修完成");
                break;
        }
        holder.reportRemark.setText(list.get(position).getRemark());
        holder.imgattach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RepairPicActivity.class);
                intent.putExtra("repairId", list.get(position).getId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    static class ViewHolder {
        TextView reprotType;
        TextView reportTime;
        TextView imgattach;
        TextView reportLocation;
        TextView reportRemark;
        TextView reportStatus;
    }
}
