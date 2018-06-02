package easyway.Mobile.TrainTrackDetail;

import java.util.ArrayList;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TB_Lane_AllMPS;
import easyway.Mobile.util.LogUtil;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

/*
 * 到发通告 - 所有 Adapter
 */
public class UnitRoadEncroachDetailAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<TB_Lane_AllMPS> mList;
	private Context mContext;

	public UnitRoadEncroachDetailAdapter(Context context, ArrayList<TB_Lane_AllMPS> list) {
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
	public Object getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<TB_Lane_AllMPS> models) {
		mList = models;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.trainad_all_item, null);
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
			holder.group = (TextView) convertView.findViewById(R.id.group);
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
		holder.trainNo.setText(mList.get(position).TRNO_PRO);
		holder.trainNo.getPaint().setFakeBoldText(true);
		holder.platform.setText(mList.get(position).PLATFORM_PTTI);

		LogUtil.i(position + "  status -->" + mList.get(position).ALATESTNAME_PTTI);
		holder.checkStatus.setText(mList.get(position).ALATESTNAME_PTTI);

		if (mList.get(position).ALATESTNAME_PTTI.equals("正在检票")) {

			ColorStateList csl = (ColorStateList) mContext.getResources()
					.getColorStateList(R.color.green);
			if (csl != null) {
				holder.checkStatus.setTextColor(csl);
			}
			holder.checkStatus.setTextColor(csl);
		} else if (mList.get(position).ALATESTNAME_PTTI.equals("停止检票")) {
			ColorStateList csl = (ColorStateList) mContext.getResources()
					.getColorStateList(R.color.red);
			if (csl != null) {
				holder.checkStatus.setTextColor(csl);
			}
			holder.checkStatus.setTextColor(csl);

		} else {
			try {
				ColorStateList color = (ColorStateList) mContext.getResources()
						.getColorStateList(R.color.black);
				holder.checkStatus.setTextColor(color);
				holder.trainNo.getPaint().setFakeBoldText(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

//		holder.arriveTime.setText(mList.get(position).FirstTime);
//		holder.leaveTime.setText(mList.get(position).DestTime);

		holder.labTrain_loc_arrTime.setText(mList.get(position).ARRTIMR_PTTI);
		holder.labTrain_loc_depaTime.setText(mList.get(position).DEPATIME_PTTI);

//		holder.trainType.setText(mList.get(position).TRAIN_TYPE);

		holder.startingStation.setText(mList.get(position).STRTSTN_PRO);
		holder.startingStation.getPaint().setFakeBoldText(true);
		holder.finalStation.setText(mList.get(position).TILSTN_PRO);
		holder.finalStation.getPaint().setFakeBoldText(true);

		holder.checkPoint.setText(mList.get(position).INTICKET_PTTI);
		holder.exitPoint.setText(mList.get(position).OUTTICKET_PTTI);
		holder.group.setText(mList.get(position).GRPNO_PTTI);

		holder.LinearExitPoint.setVisibility(View.VISIBLE);
		holder.LinearCheckPoint.setVisibility(View.VISIBLE);
		holder.TRowTime.setVisibility(View.VISIBLE);
		if (Property.OwnStation != null) {
			 String mStationName = Property.OwnStation.Name;
			 if (mStationName!=null) {
				 if (mStationName.equalsIgnoreCase(mList
							.get(position).STRTSTN_PRO)) {
						holder.LinearExitPoint.setVisibility(View.GONE);
					} else if (mStationName.equalsIgnoreCase(mList
							.get(position).TILSTN_PRO)) {
						holder.LinearCheckPoint.setVisibility(View.GONE);
					}
			}
		}
		holder.labLanePtti.setText(mList.get(position).LANE_PTTI);
		return convertView;
	}

	static class ViewHolder {
		View itemView;
		TextView trainNo;//车次
		TextView checkStatus;//检票状态
		TextView arriveTime;
		TextView leaveTime;
		TextView trainType;
		TextView startingStation;//出发站点
		TextView finalStation;//到达站点
		TextView checkPoint;
		TextView exitPoint;
		TextView platform;//站台
		TextView group;
		TextView labTrain_loc_arrTime, labTrain_loc_depaTime;
		TextView labLanePtti;
		LinearLayout LinearCheckPoint;
		LinearLayout LinearExitPoint;
		TableRow TRowTime;
	}
}
