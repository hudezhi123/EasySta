package easyway.Mobile.TrainAD;

import java.util.ArrayList;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TrainGoTo;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/*
 * 到发通告 - 终到 Adapter
 */
public class TrainADTerminalAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<TrainGoTo> mList;
    private Context mContext;

    public TrainADTerminalAdapter(Context context, ArrayList<TrainGoTo> list) {
        super();
        mInflater = LayoutInflater.from(context);
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public TrainGoTo getItem(int position) {
        if (mList == null)
            return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<TrainGoTo> models) {
        mList = models;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.trainad_origin_item, null);
            holder = new ViewHolder();
            holder.itemView = (LinearLayout) convertView
                    .findViewById(R.id.item_view);
            holder.trainNo = (TextView) convertView.findViewById(R.id.train_no);
            holder.checkStatus = (TextView) convertView
                    .findViewById(R.id.checkStatus);
            holder.arriveTime = (TextView) convertView
                    .findViewById(R.id.arriveTime);
            holder.leaveTime = (TextView) convertView
                    .findViewById(R.id.leaveTime);
            holder.trainType = (TextView) convertView
                    .findViewById(R.id.trainType);
            holder.startingStation = (TextView) convertView
                    .findViewById(R.id.startingStation);
            holder.finalStation = (TextView) convertView
                    .findViewById(R.id.finalStation);
            holder.checkPoint = (TextView) convertView
                    .findViewById(R.id.checkPoint);
            holder.exitPoint = (TextView) convertView
                    .findViewById(R.id.exitPoint);
            holder.group = (TextView) convertView
                    .findViewById(R.id.group);
            holder.platform = (TextView) convertView
                    .findViewById(R.id.platform);
            holder.labLanePtti = (TextView) convertView
                    .findViewById(R.id.labLanePtti);
            holder.labTrain_loc_arrTime = (TextView) convertView
                    .findViewById(R.id.labTrain_loc_arrTime);
            holder.labTrain_loc_depaTime = (TextView) convertView
                    .findViewById(R.id.labTrain_loc_depaTime);
            LinearLayout LLeft = (LinearLayout) convertView
                    .findViewById(R.id.LinearLeft);
            LinearLayout LMiddle = (LinearLayout) convertView
                    .findViewById(R.id.LinearMiddle);
            LinearLayout LRight = (LinearLayout) convertView
                    .findViewById(R.id.LinearRight);
            holder.LinearCheckPoint = (LinearLayout) convertView
                    .findViewById(R.id.LinearCheckPoint);
            holder.LinearExitPoint = (LinearLayout) convertView
                    .findViewById(R.id.LinearExitPoint);
            holder.TRowTime = (TableRow) convertView
                    .findViewById(R.id.TRowTime);
            holder.tlTrainTakeOn = (TableLayout) convertView
                    .findViewById(R.id.tlTrainTakeOn);
            holder.labTrainTakeOn = (TextView) convertView
                    .findViewById(R.id.labTrainTakeOn);

            int wmiddle = LMiddle.getWidth();
            int width = (Property.screenwidth - wmiddle) / 2;
            if (width > 0) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) LLeft
                        .getLayoutParams();
                if (lp != null) {
                    lp.width = width;
                    LLeft.setLayoutParams(lp);
                    LRight.setLayoutParams(lp);
                }
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TrainGoTo trainGoTo = getItem(position);
        holder.trainNo.setText(trainGoTo.TRNO_PRO);
        holder.trainNo.getPaint().setFakeBoldText(true);
        holder.platform.setText(trainGoTo.PLATFORM_PTTI);
        holder.checkStatus.setText(trainGoTo.Status);
        if (trainGoTo.Status.equals("正在检票")) {

            ColorStateList csl = (ColorStateList) mContext.getResources()
                    .getColorStateList(R.color.green);
            if (csl != null) {
                holder.checkStatus.setTextColor(csl);
            }
            holder.checkStatus.setTextColor(csl);
        } else if (trainGoTo.Status.equals("停止检票")) {
            ColorStateList csl = (ColorStateList) mContext.getResources()
                    .getColorStateList(R.color.red);
            if (csl != null) {
                holder.checkStatus.setTextColor(csl);
            }
            holder.checkStatus.setTextColor(csl);
        } else {
            try {
                int color = Color.parseColor(trainGoTo.Color);
                holder.checkStatus.setTextColor(color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.arriveTime.setText(trainGoTo.FirstTime);
        holder.leaveTime.setText(trainGoTo.DestTime);

        holder.labTrain_loc_arrTime.setText(trainGoTo.ARRTIMR_PTTI);
        holder.labTrain_loc_depaTime.setText(trainGoTo.DEPATIME_PTTI);

        holder.trainType.setText(trainGoTo.TrainType);

        holder.startingStation.setText(trainGoTo.STRTSTN_TT);
        holder.startingStation.getPaint().setFakeBoldText(true);
        holder.finalStation.setText(trainGoTo.TILSTN_TT);
        holder.finalStation.getPaint().setFakeBoldText(true);

        holder.checkPoint.setText(trainGoTo.INTICKET_PTTI);
        holder.exitPoint.setText(trainGoTo.OUTTICKET_PTTI);
        holder.group.setText(trainGoTo.GRPNO_PTTI + "(" + trainGoTo.GRPORDER_PTTI + ")");

        holder.LinearExitPoint.setVisibility(View.VISIBLE);
        holder.LinearCheckPoint.setVisibility(View.VISIBLE);
        holder.TRowTime.setVisibility(View.VISIBLE);

        if (TrainADTabActivity.mStationName != null) {
            if (TrainADTabActivity.mStationName.equalsIgnoreCase(mList
                    .get(position).STRTSTN_TT)) {
                holder.LinearExitPoint.setVisibility(View.GONE);
            } else if (TrainADTabActivity.mStationName.equalsIgnoreCase(mList
                    .get(position).TILSTN_TT)) {
                holder.LinearCheckPoint.setVisibility(View.GONE);
            }
        }

        holder.labLanePtti.setText(trainGoTo.LANE_PTTI);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, TrainADSchedule.class);
                Bundle bundle = new Bundle();
                bundle.putString("TrainNo", trainGoTo.TRNO_PRO);
                bundle.putString("PlanDate", trainGoTo.PLANDATE_PTTI);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        if (Property.StationCode.equals("SYB")) {
            holder.LinearExitPoint.setVisibility(View.GONE);
            holder.tlTrainTakeOn.setVisibility(View.VISIBLE);
            holder.labTrainTakeOn.setText(trainGoTo.TakeOn);
        }
        return convertView;
    }

    static class ViewHolder {
        View itemView;
        TextView trainNo;
        TextView checkStatus;
        TextView arriveTime;
        TextView leaveTime;
        TextView trainType;
        TextView startingStation;
        TextView finalStation;
        TextView checkPoint;
        TextView exitPoint;
        TextView platform;
        TextView group;
        TextView labTrain_loc_arrTime, labTrain_loc_depaTime;
        TextView labLanePtti;
        LinearLayout LinearCheckPoint;
        LinearLayout LinearExitPoint;
        TableRow TRowTime;
        TableLayout tlTrainTakeOn;
        TextView labTrainTakeOn;
    }
}
