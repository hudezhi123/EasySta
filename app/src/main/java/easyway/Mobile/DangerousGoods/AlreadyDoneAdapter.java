package easyway.Mobile.DangerousGoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import easyway.Mobile.R;

/**
 * Created by boy on 2017/7/24.
 */

public class AlreadyDoneAdapter extends BaseAdapter {

    private Context context;
    private List<DangerousObjectResult> list;

    public AlreadyDoneAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setData(List<DangerousObjectResult> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public DangerousObjectResult getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.danger_already_submit_form_item, parent, false);
            holder.textCarrier = (TextView) convertView.findViewById(R.id.text_carrier_danger_item);
            holder.textWorker = (TextView) convertView.findViewById(R.id.text_worker_danger_item);
            holder.textTrainNo = (TextView) convertView.findViewById(R.id.text_train_no_danger_item);
            holder.textTime = (TextView) convertView.findViewById(R.id.text_time_danger_item);
            holder.textProDetail = (TextView) convertView.findViewById(R.id.text_danger_name_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DangerousObjectResult result = getItem(position);
        holder.textWorker.setText("经办人：" + result.getDealStaffName());
        holder.textCarrier.setText("危险品携带者：" + result.getFullName());
        holder.textTrainNo.setText("车次：" + result.getTrainNo());
        holder.textTime.setText("事件" + result.getFindDate().replace("T"," "));
        holder.textProDetail.setText("时间：" + result.getProdNameDetail());
        return convertView;
    }

    private class ViewHolder {
        TextView textProDetail;
        TextView textCarrier;
        TextView textWorker;
        TextView textTrainNo;
        TextView textTime;
    }
}
