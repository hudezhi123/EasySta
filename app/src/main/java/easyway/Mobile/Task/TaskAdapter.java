package easyway.Mobile.Task;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Attach.AttachList;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Data.TaskMajor;

import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.PTTUtil;
import easyway.Mobile.util.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;

/*
 * 任务Adapter
 */
public class TaskAdapter extends BaseAdapter {
	private ArrayList<TaskChild> todoList;
	private Context context;
	private LayoutInflater mInflater;
	private ITaskReview iTaskReview;
	private int flag = FLAG_REVIEW;

	public static final int FLAG_REVIEW = 0;
	public static final int FLAG_ALL = 1;
	public static final int FLAG_OPEN = 2;
	public static final int FLAG_EMPHASIS = 3;
	public static final int FLAG_WORKSPACE = 4;

	public TaskAdapter(Context context, ArrayList<TaskChild> list, int flag) {
		mInflater = LayoutInflater.from(context);
		todoList = list;
		this.context = context;
		this.flag = flag;
	}

	public void setITaskReview(ITaskReview iTaskReview) {
		this.iTaskReview = iTaskReview;
	}





	public int getCount() {
		if (todoList == null) {
			return 0;
		}
		return todoList.size();
	}

	public TaskChild getItem(int position) {
		if (todoList == null)
			return null;
		if (todoList.size() <= position)
			return null;
		return todoList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<TaskChild> models) {
		todoList = models;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final TaskChild bean = todoList.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.task_item, null);
			holder = new ViewHolder();
			holder.itemView = (LinearLayout) convertView
					.findViewById(R.id.item_view);
			holder.taskname = (TextView) convertView
					.findViewById(R.id.taskname);
			holder.tasktrainno = (TextView) convertView
					.findViewById(R.id.tasktrainno);
			holder.taskcontent = (TextView) convertView
					.findViewById(R.id.taskcontent);
			holder.plantime = (TextView) convertView
					.findViewById(R.id.plantime);
			holder.realtime = (TextView) convertView
					.findViewById(R.id.realtime);
			holder.workspaces = (TextView) convertView
					.findViewById(R.id.workspaces);
			holder.teamname = (TextView) convertView
					.findViewById(R.id.teamname);
			holder.staffname = (TextView) convertView
					.findViewById(R.id.staffname);
			holder.taskstatus = (TextView) convertView
					.findViewById(R.id.taskstatus);
			holder.imgattach = (TextView) convertView
					.findViewById(R.id.imgattach);
			holder.ScheName = (TextView) convertView.findViewById(R.id.ScheName);
			holder.layoutcontent = (LinearLayout) convertView
					.findViewById(R.id.layoutcontent);
			holder.layouttrainno = (LinearLayout) convertView
					.findViewById(R.id.layouttrainno);
			holder.layoutprincipal = (RelativeLayout) convertView
					.findViewById(R.id.layoutprincipal);
			holder.principal = (TextView) convertView
					.findViewById(R.id.principal);
			holder.btnCall = (Button) convertView.findViewById(R.id.btnCall);
			holder.btnok = (Button) convertView.findViewById(R.id.btnok);
			holder.passenger_num = (TextView)convertView.findViewById(R.id.passenger_num);
			holder.btnGarbage= (Button) convertView.findViewById(R.id.btnGarbage);
			holder.GarbageStaffName= (TextView) convertView.findViewById(R.id.GarbageStaffName);
			holder.layGarbage=(RelativeLayout) convertView
					.findViewById(R.id.layGarbage);
			holder.btnSecureTips = (Button) convertView.findViewById(R.id.btn_secure_tips);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (flag == 0) {
			holder.btnok.setVisibility(ViewGroup.VISIBLE);
		}

		holder.taskname.setText(bean.TaskName);
		if (bean.IsMajor) { // 重点任务任务名为红色，否则为黑
			holder.taskname.setTextColor(context.getResources().getColor(
					R.color.red));
		} else {
			holder.taskname.setTextColor(context.getResources().getColor(
					R.color.black));
		}
		holder.taskname.getPaint().setFakeBoldText(true);

		// 负责人
		if (bean.IsMajor) {
			holder.layoutprincipal.setVisibility(View.VISIBLE);
			holder.principal.setText(bean.ChargeStaffName);
			holder.btnCall.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PTTUtil.call(
							context,
							Staff.GetExpend1ByStaffId(context,
									todoList.get(position).ChargeStaffId),
							PTTUtil.AUDIO_CALL);
				}
			});
			//暂时不直接呼叫
			holder.btnCall.setVisibility(View.GONE);
		} else {
			holder.layoutprincipal.setVisibility(View.GONE);
		}

		// 任务内容（备注）非重点任务任务内容部不显示
		if (bean.TaskRemark == null || bean.TaskRemark.equals("")
				|| !bean.IsMajor) {
			holder.layoutcontent.setVisibility(View.GONE);
		} else {
			holder.layoutcontent.setVisibility(View.VISIBLE);
			holder.taskcontent.setText(bean.TaskRemark);
		}

		// 任务内容（备注）非重点任务任务内容部不显示
		if (bean.TRNO_PRO == null || bean.TRNO_PRO.equals("") || !bean.IsMajor) {
			holder.layouttrainno.setVisibility(View.GONE);
		} else {
			holder.layouttrainno.setVisibility(View.VISIBLE);
			holder.tasktrainno.setText(bean.TRNO_PRO);
		}
		//显示计划名称
		holder.ScheName.setText(bean.ScheName + "");
		//TODO 上车人数
		holder.passenger_num.setText(bean.SCRS);

		// 附件
		if (bean.AttachList == null || bean.AttachList.size() == 0) {
			holder.imgattach.setVisibility(View.GONE);
		} else {
			holder.imgattach.setVisibility(View.VISIBLE);
			final ArrayList<String> list = bean.AttachList;
			holder.imgattach.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, AttachList.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("url", list);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
		}

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

		// 实际任务时间
		if (StringUtil.isNullOrEmpty(DateUtil
				.formatDate(bean.RBeginWorkTime,DateUtil.HH_MM))) {
			holder.realtime.setText("");
		} else {
			holder.realtime.setText(DateUtil
					.formatDate(bean.RBeginWorkTime,DateUtil.HH_MM)
					+ " - "
					+ DateUtil.formatDate(bean.REndWorkTime,DateUtil.HH_MM));
		}

		holder.workspaces.setText(bean.Workspace);

		// 执行组
		if (StringUtil.isNullOrEmpty(bean.RTeamName)) {
			holder.teamname.setText(bean.DeptName);
		} else {
			if (bean.RTeamName.equals(bean.DeptName))
				holder.teamname.setText(bean.DeptName);
			else if (bean.DeptName.equals(""))
				holder.teamname.setText(bean.RTeamName);
			else
				holder.teamname.setText(bean.RTeamName + "(" + bean.DeptName
						+ ")");
		}

		// 执行人
		if (StringUtil.isNullOrEmpty(bean.RStaffNames)) {
			holder.staffname.setText(bean.StaffName);
		} else {
			if (bean.RStaffNames.equals(bean.StaffName))
				holder.staffname.setText(bean.StaffName);
			else {
				if (bean.StaffName.equals(""))
					holder.staffname.setText(bean.RStaffNames);
				else
					holder.staffname.setText(bean.RStaffNames + "("
							+ bean.StaffName + ")");
			}
		}

		// 任务状态
		if (bean.TaskSta == TaskMajor.TASK_STATE_CANCEL) {
			holder.taskstatus.setText(R.string.task_status_cancel); // 已取消
			holder.taskstatus.setTextColor(context.getResources().getColor(
					R.color.gray));
		} else if (bean.IsMajor) {
			if (!bean.IsAccepted) {
				holder.taskstatus.setText(R.string.task_status_unaccept); // 未接收
				holder.taskstatus.setTextColor(context.getResources().getColor(
						R.color.yellow));
			} else {
				if (bean.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) { // 已完成
					holder.taskstatus.setText(R.string.task_status_complete);
					ColorStateList csl = (ColorStateList) context
							.getResources().getColorStateList(R.color.green);
					if (csl != null) {
						holder.taskstatus.setTextColor(csl);
					}

				} else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) { // 已到岗
					holder.taskstatus.setText(R.string.task_status_onduty);

					ColorStateList csl = (ColorStateList) context
							.getResources().getColorStateList(R.color.yellow);
					if (csl != null) {
						holder.taskstatus.setTextColor(csl);
					}

				} else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) { // 未到岗
					holder.taskstatus.setText(R.string.task_status_unduty);
					ColorStateList csl = (ColorStateList) context
							.getResources().getColorStateList(R.color.red);
					if (csl != null) {
						holder.taskstatus.setTextColor(csl);
					}

				} else {
					holder.taskstatus.setText("");
				}
			}
		} else {
			if (bean.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) { // 已完成
				holder.taskstatus.setText(R.string.task_status_complete);
				ColorStateList csl = (ColorStateList) context.getResources()
						.getColorStateList(R.color.green);
				if (csl != null) {
					holder.taskstatus.setTextColor(csl);
				}
			} else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) { // 已到岗
				holder.taskstatus.setText(R.string.task_status_onduty);
				ColorStateList csl = (ColorStateList) context.getResources()
						.getColorStateList(R.color.yellow);
				if (csl != null) {
					holder.taskstatus.setTextColor(csl);
				}
			} else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) { // 未到岗
				holder.taskstatus.setText(R.string.task_status_unduty);
				ColorStateList csl = (ColorStateList) context.getResources()
						.getColorStateList(R.color.red);
				if (csl != null) {
					holder.taskstatus.setTextColor(csl);
				}
			} else {
				holder.taskstatus.setText("");
			}
		}

		//吸污
		if (iTaskReview != null) {
			if (bean.WorkType==1 && bean.StationCode.equals("XAB"))
			{
				if ((bean.AExcStat==TaskChild.TASK_EXCSTATE_COMPLETE || bean.AExcStat==TaskChild.TASK_EXCSTATE_ONDUTY)) {


					holder.GarbageStaffName.setText(bean.GarbageStaffName);
					holder.layGarbage.setVisibility(View.VISIBLE);
					if (bean.Garbage!=1) {
						holder.btnGarbage.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (iTaskReview != null)
									iTaskReview.ItemGarbage(position);
							}
						});
						holder.btnGarbage.setVisibility(View.VISIBLE);
					} else {
						holder.btnGarbage.setVisibility(View.GONE);
					}

				} else {
					holder.layGarbage.setVisibility(ViewGroup.GONE);
				}
			} else {
				holder.layGarbage.setVisibility(ViewGroup.GONE);
			}
		} else {
			holder.layGarbage.setVisibility(ViewGroup.GONE);

		}

		if (bean.AExcStat==TaskChild.TASK_EXCSTATE_ONDUTY && iTaskReview != null)
		{
			holder.btnok.setVisibility(View.VISIBLE);
			holder.btnok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (iTaskReview != null)
						iTaskReview.ItemClicked(position);
				}
			});
		} else {
			holder.btnok.setVisibility(View.GONE);
		}

		if (flag == 3) {

			if (holder.taskstatus.getText().toString().equals("未到岗")) {
				holder.btnok.setVisibility(ViewGroup.INVISIBLE);
			} else if (holder.taskstatus.getText().toString().equals("未接收")) {
				holder.btnok.setText("接收");
				holder.btnok.setVisibility(ViewGroup.VISIBLE);
			}

		}
		if (flag == 0 || flag == 3) {
			holder.btnok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (iTaskReview != null)
						iTaskReview.ItemClicked(position);
				}
			});

		} else {
			holder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (iTaskReview != null)
						iTaskReview.ItemClicked(position);
				}
			});
		}

		if (bean.WorkType<0)
		{
			holder.btnSecureTips.setVisibility(View.GONE);
		}
		holder.btnSecureTips.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						getSecureTips(bean.SaId);
					}
				}).start();
			}
		});
		return convertView;
	}

	private void getSecureTips(long saId) {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("saId", String.valueOf(saId));
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_ACTOR_SAVE_PROMPT_CARED;

		WebServiceManager webServiceManager = new WebServiceManager(
				context, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		if (result == null || result.equals("")) {
			return;
		} else {
			Intent intent = new Intent(context, SecureTipsInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("GetTaskActorResult", result);
			intent.putExtras(bundle);
			context.startActivity(intent);
		}
	}

	static class ViewHolder {
		View itemView;
		TextView taskname;
		TextView tasktrainno;
		TextView taskcontent;
		TextView plantime;
		TextView realtime;
		TextView workspaces;
		TextView teamname;
		TextView taskstatus;
		TextView staffname;
		TextView imgattach;
		TextView principal;
		TextView ScheName;
		TextView GarbageStaffName;
		Button btnCall, btnok,btnGarbage;
		LinearLayout layouttrainno;
		LinearLayout layoutcontent;
		RelativeLayout layoutprincipal;
		TextView passenger_num;
		RelativeLayout layGarbage;
		Button btnSecureTips;
	}
}
