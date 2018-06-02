package easyway.Mobile.PointTask;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Attach.AttachList;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.LinearLayout;

/*
 * 添加重点任务-子任务 Adapter
 */
public class PTAddAdapter extends BaseAdapter {
	private ArrayList<ChildMode> tasklist;
	private Context context;
	private LayoutInflater mInflater;

	public PTAddAdapter(Context context, ArrayList<ChildMode> list) {
		mInflater = LayoutInflater.from(context);
		tasklist = list;
		this.context = context;
	}

	public int getCount() {
		if (tasklist == null) {
			return 0;
		}
		return tasklist.size();
	}

	public ChildMode getItem(int position) {
		if (tasklist == null)
			return null;
		if (tasklist.size() <= position)
			return null;
		return tasklist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<ChildMode> models) {
		tasklist = models;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ChildMode bean = tasklist.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.pointtask_additem,
					null);
			holder = new ViewHolder();
			holder.itemView = (LinearLayout) convertView
					.findViewById(R.id.item_view);
			holder.taskremark = (TextView) convertView
					.findViewById(R.id.taskremark);
			holder.plantime = (TextView) convertView
					.findViewById(R.id.plantime);
			holder.workspaces = (TextView) convertView
					.findViewById(R.id.workspaces);
			holder.staffname = (TextView) convertView
					.findViewById(R.id.staffname);
			holder.position = (TextView) convertView
					.findViewById(R.id.position);
			holder.imgattach = (TextView) convertView
					.findViewById(R.id.imgattach);
			holder.txtUploadState = (TextView) convertView
					.findViewById(R.id.txtUploadState);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 备注
		holder.taskremark.setText(bean.TaskRemark);

		// 计划任务时间
		if (StringUtil.isNullOrEmpty(DateUtil
				.formatDate(bean.BeginWorkTime,DateUtil.HH_MM))) {
			holder.plantime.setText("");
		} else {
			holder.plantime.setText(DateUtil
					.formatDate(bean.BeginWorkTime,DateUtil.HH_MM)
					+ " - "
					+ DateUtil.formatDate(bean.EndWorkTime,DateUtil.HH_MM));
		}

		// 上传状态
		if (bean.UpdateState == ChildMode.UPDATESTATE_ALL)
			holder.txtUploadState.setBackgroundResource(R.drawable.upload_ed);
		else if (bean.UpdateState == ChildMode.UPDATESTATE_PART)
			holder.txtUploadState.setBackgroundResource(R.drawable.upload_ing);
		else if (bean.UpdateState == ChildMode.UPDATESTATE_NOT)
			holder.txtUploadState.setBackgroundResource(R.drawable.upload_wait);
		else
			holder.txtUploadState.setBackgroundResource(R.drawable.upload_wait);
		
		// 任务区域
		holder.workspaces.setText(bean.Workspace);
		// 执行人
		holder.staffname.setText(bean.StaffName);
		// 岗位
		holder.position.setText(bean.PositionName);
		// 附件
		if (bean.AttachList == null || bean.AttachList.size() == 0)
			holder.imgattach.setVisibility(View.GONE);
		else
			holder.imgattach.setVisibility(View.VISIBLE);

		final ArrayList<String> list = bean.AttachList;
		holder.imgattach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AttachList.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("url", list);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		View itemView;
		TextView taskremark;
		TextView plantime;
		TextView workspaces;
		TextView staffname;
		TextView position;
		TextView imgattach;
		TextView txtUploadState;
	}
}
