package easyway.Mobile.Watering;

import java.util.ArrayList;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.DateUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/*
 * 上水管理 Adapter
 */
public class WateringAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Watering> mList;
    private IWatering iWatering;
    // private Context mContext;

    public WateringAdapter(Context context, ArrayList<Watering> list) {
        super();
        mInflater = LayoutInflater.from(context);
        mList = list;
        // mContext = context;
    }

    @Override
    public int getCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mList == null)
            return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<Watering> models) {
        mList = models;
    }

    public void setIWatering(IWatering iWatering) {
        this.iWatering = iWatering;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Watering obj = (Watering) getItem(position);
        if (obj == null)
            return null;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.water_item, null);
            holder = new ViewHolder();
            holder.txtTrainNo = (TextView) convertView
                    .findViewById(R.id.txtTrainNo);
            holder.txtTrack = (TextView) convertView
                    .findViewById(R.id.txtTrack);
            holder.txtArriveTime = (TextView) convertView
                    .findViewById(R.id.txtArriveTime);
            holder.txtLeaveTime = (TextView) convertView
                    .findViewById(R.id.txtLeaveTime);
            holder.txtControl = (TextView) convertView
                    .findViewById(R.id.txtControl);
            holder.btnControl = (Button) convertView
                    .findViewById(R.id.btnControl);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTrainNo.setWidth(Property.screenwidth / 6);
        holder.txtTrack.setWidth(Property.screenwidth / 6);
        holder.txtArriveTime.setWidth(Property.screenwidth / 6);
        holder.txtLeaveTime.setWidth(Property.screenwidth / 6);
        holder.txtControl.setWidth(Property.screenwidth / 3);
        holder.btnControl.setWidth(Property.screenwidth / 3);

        holder.txtTrainNo.setText(obj.TRNO_PRO);
        holder.txtTrack.setText(obj.Workspace);
        holder.txtArriveTime.setText(DateUtil.formatDate(obj.BeginWorkTime, DateUtil.HH_MM));
        holder.txtLeaveTime.setText(DateUtil.formatDate(obj.EndWorkTime, DateUtil.HH_MM));

        switch (obj.AppStatus) {
            case Watering.STATUS_NOT_REPUEST:            // 显示申请按钮
                holder.txtControl.setVisibility(View.GONE);
                holder.btnControl.setVisibility(View.VISIBLE);
                holder.btnControl.setText(R.string.Watering_Request);
                break;
            case Watering.STATUS_APPROVAL_REJECT:        //显示 审核驳回
                holder.txtControl.setVisibility(View.VISIBLE);
                holder.btnControl.setVisibility(View.GONE);
                holder.txtControl.setText(R.string.Watering_ApprovalReject);
                break;
            case Watering.STATUS_APPROVALING:    // 显示 审核中
                holder.txtControl.setVisibility(View.VISIBLE);
                holder.btnControl.setVisibility(View.GONE);
                holder.txtControl.setText(R.string.Watering_Approvaling);
                break;
            case Watering.STATUS_APPROVAL_PASS:        // 显示完成按钮
                holder.txtControl.setVisibility(View.GONE);
                holder.btnControl.setVisibility(View.VISIBLE);
                holder.btnControl.setText(R.string.Watering_Complete);
                break;
            case Watering.STATUS_COMPLETED:        // 显示完成时间
            default:
                holder.txtControl.setVisibility(View.VISIBLE);
                holder.btnControl.setVisibility(View.GONE);
                holder.txtControl.setText(DateUtil.formatDate(obj.CompleteDttm, DateUtil.HH_MM));
                break;
        }

        holder.btnControl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (iWatering != null) {
                    iWatering.ItemClicked(position);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView txtTrainNo;
        TextView txtTrack;
        TextView txtArriveTime;
        TextView txtLeaveTime;
        TextView txtControl;
        Button btnControl;
    }
}
