package easyway.Mobile.TrainTrack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import easyway.Mobile.R;
import easyway.Mobile.Data.TB_AreaOccupancy;
import easyway.Mobile.TrainTrackDetail.UnitRoadEncroachmentDetail;
import easyway.Mobile.util.StringUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * 股道占用Adapter
 */
public class UnitRoadEncroachmentAdapter extends BaseAdapter {
    private ArrayList<TB_AreaOccupancy> mList;
    private Activity context;
    private LayoutInflater mInflater;

    public UnitRoadEncroachmentAdapter(Activity context,
                                       ArrayList<TB_AreaOccupancy> list) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        mList = list;
    }

    public int getCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    public TB_AreaOccupancy getItem(int position) {
        if (mList == null)
            return null;
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<TB_AreaOccupancy> models) {
        mList = models;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TB_AreaOccupancy bean = mList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.new_station_track_list_item, parent, false);
            holder = new ViewHolder();
            holder.stationTrack = (TextView) convertView.findViewById(R.id.text_stationtrack_item);
            holder.status = (TextView) convertView.findViewById(R.id.text_station_status_item);
            holder.station = (TextView) convertView.findViewById(R.id.text_platform_item);
            holder.trainNo = (TextView) convertView.findViewById(R.id.text_train_no_item);
            holder.forceCast = (TextView) convertView.findViewById(R.id.text_pretrain_item);
            holder.arriveTime = (TextView) convertView.findViewById(R.id.text_arrive_and_leave_item);
            holder.stayTime = (TextView) convertView.findViewById(R.id.text_stay_item);
            holder.station_track_list_item = (LinearLayout) convertView.findViewById(R.id.linear_station_track_list_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.stationTrack.setText(bean.AREANAME);
        holder.stationTrack.getPaint().setFakeBoldText(true);
        holder.station.setText(bean.PLATFORM_PTTI);
        if (bean.StationStatus == 1) {
            holder.status.setText(R.string.TT_tsbusy);
            holder.status.setTextColor(context.getResources().getColor(
                    R.color.red));
        } else if (bean.StationStatus == 2) {
            holder.status.setText(R.string.TT_tsprebusy);
            holder.status.setTextColor(context.getResources().getColor(
                    R.color.orange));
        } else {
            holder.status.setText(R.string.TT_tsfree);
            holder.status.setTextColor(context.getResources().getColor(
                    R.color.green));
        }
        holder.trainNo.setText(bean.TRNO_PRO);
        holder.forceCast.setText(bean.PTRNO_PRO);

        if (bean.ARRTIMR_PTTI != null
                && bean.ARRTIMR_PTTI.equals("") == Boolean.FALSE) {
            if (bean.DEPATIME_PTTI != null
                    && bean.DEPATIME_PTTI.equals("") == Boolean.FALSE) {
                holder.arriveTime.setText(bean.ARRTIMR_PTTI + "到/"
                        + bean.DEPATIME_PTTI + "发");
            } else {
                holder.arriveTime.setText(bean.ARRTIMR_PTTI + "到/" + "--:--发");
            }
        } else {
            holder.arriveTime.setText("");
        }
        if (bean.DEPATIME_PTTI != null
                && bean.DEPATIME_PTTI.equals("") == Boolean.FALSE) {
            if (bean.ARRTIMR_PTTI != null
                    && bean.ARRTIMR_PTTI.equals("") == Boolean.FALSE) {

                holder.arriveTime.setText(bean.ARRTIMR_PTTI + "到/"
                        + bean.DEPATIME_PTTI + "发");
            } else {
                holder.arriveTime.setText("--:--到/" + bean.DEPATIME_PTTI + "发");
            }
        } else {
            holder.arriveTime.setText("");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date endDate = new Date();
        Date beginDate = new Date();
        try {
            endDate = sdf.parse(bean.STATIONENDTIME);
            beginDate = sdf.parse(bean.STATIONBEGINTIME);
        } catch (ParseException e) {
//            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(bean.StaySpan) && !"null".equals(bean.StaySpan)) {
            holder.stayTime.setText("停:" + bean.StaySpan + " 分钟");
        } else {
            holder.stayTime.setVisibility(View.GONE);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final String currTime = format.format(new Date(System.currentTimeMillis()));
        holder.station_track_list_item.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context, UnitRoadEncroachmentDetail.class);
                intent.putExtra("laneName", bean.AREANAME);
                intent.putExtra("planDate", currTime);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView stationTrack;// 股道
        TextView status;// 状态
        TextView trainNo;// 车次
        TextView station;// 站台
        TextView arriveTime; // 到时
        TextView stayTime;// 停留
        TextView forceCast;// 预办
        LinearLayout station_track_list_item;
    }

}
