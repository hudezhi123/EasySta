package easyway.Mobile.TrainAD;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import easyway.Mobile.Data.TrainGoTo;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.bean.MPSLog;
import easyway.Mobile.util.CommonFunc;

/**
 * Created by doeve on 2017-05-29.
 */

public class TrainChangeAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<MPSLog> mList;
    private Context mContext;

    public TrainChangeAdapter(Context context, ArrayList<MPSLog> list) {
        super();
        mInflater = LayoutInflater.from(context);
        mList = list;
        mContext = context;
    }

    public void setData(ArrayList<MPSLog> mList)
    {
        this.mList=mList;
    }

    @Override
    public int getCount() {
        if (mList==null) return 0;
        return mList.size();
    }

    @Override
    public MPSLog getItem(int i) {
        if (mList==null) return null;
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        MPSLog mpsLog=getItem(i);
        if (mpsLog==null) return 0;
        return mpsLog.Id;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TrainChangeAdapter.ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.train_mps_change, null);
            holder = new TrainChangeAdapter.ViewHolder();

            holder.labArrTime = (TextView) convertView
                    .findViewById(R.id.labArrTime);
            holder.labDeptTime = (TextView) convertView
                    .findViewById(R.id.labDeptTime);
            holder.labLaterArrTime = (TextView) convertView
                    .findViewById(R.id.labLaterArrTime);
            holder.labLaterDeptTime = (TextView) convertView
                    .findViewById(R.id.labLaterDeptTime);
            holder.labTrainType = (TextView) convertView
                    .findViewById(R.id.labTrainType);
            holder.labGroup = (TextView) convertView
                    .findViewById(R.id.labGroup);
            holder.labLanePtti = (TextView) convertView
                    .findViewById(R.id.labLanePtti);
            holder.labPlatform = (TextView) convertView.findViewById(R.id.labPlatform);
            holder.labINTICKET_PTTI = (TextView) convertView
                    .findViewById(R.id.labINTICKET_PTTI);

            holder.labOUTTICKET_PTTI=(TextView) convertView.findViewById(R.id.labOUTTICKET_PTTI);
            holder.labChangeTime=(TextView) convertView.findViewById(R.id.labChangeTime);


            LinearLayout LLeft = (LinearLayout) convertView
                    .findViewById(R.id.LinearLeft);
            LinearLayout LMiddle = (LinearLayout) convertView
                    .findViewById(R.id.LinearMiddle);
            LinearLayout LRight = (LinearLayout) convertView
                    .findViewById(R.id.LinearRight);
            holder.linearCheckPoint = (LinearLayout) convertView
                    .findViewById(R.id.linearCheckPoint);

            convertView.setTag(holder);
        } else {
            holder = (TrainChangeAdapter.ViewHolder) convertView.getTag();
        }

        holder.labArrTime.setText(mList.get(position).ARRTIMR_PTTI);
        holder.labDeptTime.setText(mList.get(position).DEPATIME_PTTI);

        holder.labLaterArrTime.setText(mList.get(position).ALATETIME_PTTI);
        holder.labLaterDeptTime.setText(mList.get(position).DLATETIME_PTTI);

        holder.labTrainType.setText(mList.get(position).TRTYPE);
        holder.labGroup.setText(mList.get(position).GRPNO_PTTI);

        holder.labLanePtti.setText(mList.get(position).LANE_PTTI);
        holder.labPlatform.setText(mList.get(position).PLATFORM_PTTI);

        holder.labINTICKET_PTTI.setText(mList.get(position).INTICKET_PTTI);
        holder.labOUTTICKET_PTTI.setText(mList.get(position).OUTTICKET_PTTI);
        holder.labChangeTime.setText(CommonFunc.ConvertData2Str(mList.get(position).ChangeTime,"HH:mm"));
        return convertView;
    }

    static class ViewHolder {
        LinearLayout linearCheckPoint;
        TextView labArrTime;
        TextView labDeptTime;
        TextView labLaterArrTime;
        TextView labLaterDeptTime;
        TextView labTrainType;
        TextView labGroup;
        TextView labLanePtti;
        TextView labPlatform;
        TextView labINTICKET_PTTI;
        TextView labOUTTICKET_PTTI;
        TextView labChangeTime;
    }
}
