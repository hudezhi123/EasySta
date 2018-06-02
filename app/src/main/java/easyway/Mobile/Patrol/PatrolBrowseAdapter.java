package easyway.Mobile.Patrol;

import java.util.ArrayList;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 客运巡检 Adapter
 */
public class PatrolBrowseAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<PatrolShow> mListAll;
	private ArrayList<PatrolShow> mListShow;
//	private Context mContext;

	public PatrolBrowseAdapter(Context context, ArrayList<Patrol> list) {
		super();
		mInflater = LayoutInflater.from(context);
//		mContext = context;
		initData(list);
	}

	@Override
	public int getCount() {
		if (mListShow == null)
			return 0;
		return mListShow.size();
	}

	@Override
	public Object getItem(int position) {
		if (mListShow == null)
			return null;
		return mListShow.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<Patrol> models) {
		initData(models);
	}

	// 数据整理
	private void initData(ArrayList<Patrol> patrols) {
		if (patrols == null || patrols.size() == 0) {
			mListAll = null;
		} else {
			mListAll = new ArrayList<PatrolShow>();
			int id = 0;
			for (Patrol patrol : patrols) { // 时间区域
				PatrolShow showF = new PatrolShow();
				showF.flag = PatrolShow.FLAG_TIME_CLOSE;
				showF.Name = patrol.TimeArea;
				showF.Status = patrol.ScheStat;
				showF.id = id;

				mListAll.add(showF);
				;
				id++;
				if (patrol.SchList == null || patrol.SchList.size() == 0)
					continue;

				for (PatrolW patrolw : patrol.SchList) { // 任务区域
					PatrolShow showW = new PatrolShow();
					showW.flag = PatrolShow.FLAG_WORKSAPCE;
					showW.Name = patrolw.Workspace;
					showW.Status = patrolw.IsPatrol;
					showW.id = id;
					mListAll.add(showW);
					id++;
				}
			}
		}

		DataChange();
	}

	// 生成显示数据
	private void DataChange() {
		if (mListAll == null || mListAll.size() == 0)
			mListShow = null;
		else {
			if (mListShow == null)
				mListShow = new ArrayList<PatrolShow>();

			mListShow.clear();
			boolean add = false;
			for (PatrolShow show : mListAll) {
				switch (show.flag) {
				case PatrolShow.FLAG_TIME_OPEN:
					add = true;
					mListShow.add(show);
					break;
				case PatrolShow.FLAG_TIME_CLOSE:
					add = false;
					mListShow.add(show);
					break;
				case PatrolShow.FLAG_WORKSAPCE:
					if (add)
						mListShow.add(show);
					break;
				default:
					break;
				}
			}
		}

		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final PatrolShow obj = (PatrolShow) getItem(position);
		if (obj == null)
			return null;

		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.patrol_browse_item, null);
			holder = new ViewHolder();
			holder.item_view = convertView.findViewById(R.id.item_view);
			holder.imgListState = (ImageView) convertView
					.findViewById(R.id.imgListState);
			holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
			holder.txtStatus = (TextView) convertView
					.findViewById(R.id.txtStatus);
			holder.LayoutStatus = (LinearLayout) convertView
					.findViewById(R.id.LayoutStatus);
			holder.imgStatus = (ImageView) convertView
					.findViewById(R.id.imgStatus);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtName.setText(obj.Name);
		
		int width = (Property.screenwidth - 80) /2;
		holder.txtName.setWidth(width);
		holder.LayoutStatus.setMinimumWidth(width);
		
		if (obj.flag == PatrolShow.FLAG_WORKSAPCE) {
			holder.imgListState.setVisibility(View.INVISIBLE);
			
			if (obj.Status == PatrolW.PATROL_OFF)
				holder.imgStatus.setImageResource(R.drawable.patrol_off);
			else
				holder.imgStatus.setImageResource(R.drawable.patrol_on);
			
			holder.txtStatus.setVisibility(View.GONE);
			holder.imgStatus.setVisibility(View.VISIBLE);
		} else {
			holder.txtStatus.setText(Patrol.getScheRes(obj.Status));
			holder.txtStatus.setVisibility(View.VISIBLE);
			holder.imgStatus.setVisibility(View.GONE);
			
			holder.imgListState.setVisibility(View.VISIBLE);
			if (obj.flag == PatrolShow.FLAG_TIME_CLOSE)
				holder.imgListState.setImageResource(R.drawable.list_close);
			else
				holder.imgListState.setImageResource(R.drawable.list_open);
		}

		holder.item_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (obj.flag == PatrolShow.FLAG_WORKSAPCE)
					return;

				if (obj.flag == PatrolShow.FLAG_TIME_CLOSE)
					mListAll.get(obj.id).flag = PatrolShow.FLAG_TIME_OPEN;
				else
					mListAll.get(obj.id).flag = PatrolShow.FLAG_TIME_CLOSE;

				DataChange();
			}
		});

		return convertView;
	}

	class ViewHolder {
		View item_view;
		LinearLayout LayoutStatus;
		ImageView imgListState;
		ImageView imgStatus;
		TextView txtName;
		TextView txtStatus;
	}

	class PatrolShow {
		int id;
		String Name;
		int Status;
		int flag;

		static final int FLAG_WORKSAPCE = 0; // 巡检区域
		static final int FLAG_TIME_OPEN = 1; // 时间区域 打开
		static final int FLAG_TIME_CLOSE = 2; // 时间区域 关闭
	}
}
