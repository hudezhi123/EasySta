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
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

/*
 * 子任务Adapter
 */
public class PTChildAdapter extends BaseAdapter {
    private ArrayList<TaskChild> tasklist;
    private Context context;
    private LayoutInflater mInflater;
    private IOnItemClick iClick;

    public PTChildAdapter(Context context, ArrayList<TaskChild> list) {
        mInflater = LayoutInflater.from(context);
        tasklist = list;
        this.context = context;
    }

    public void setIOnItemClick(IOnItemClick iClick) {
        this.iClick = iClick;
    }

    public int getCount() {
        if (tasklist == null) {
            return 0;
        }
        return tasklist.size();
    }

    public TaskChild getItem(int position) {
        if (tasklist == null)
            return null;
        if (tasklist.size() <= position)
            return null;
        return tasklist.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<TaskChild> models) {
        tasklist = models;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        TaskChild bean = tasklist.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.pointtask_childitem, null);
            holder = new ViewHolder();
//			holder.itemView = (LinearLayout) convertView
//					.findViewById(R.id.item_view);
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
            holder.btnCall = (Button) convertView
                    .findViewById(R.id.btnCall);
            holder.layoutcontent = (LinearLayout) convertView
                    .findViewById(R.id.layoutcontent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 任务内容（备注）非重点任务任务内容部不显示
        if (bean.TaskRemark == null || bean.TaskRemark.equals("")) {
            holder.layoutcontent.setVisibility(View.GONE);
        } else {
            holder.layoutcontent.setVisibility(View.VISIBLE);
            holder.taskcontent.setText(bean.TaskRemark);
        }

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
        if (StringUtil.isNullOrEmpty(DateUtil.formatDate(bean.BeginWorkTime, DateUtil.HH_MM))) {
            holder.plantime.setText("");
        } else {
            holder.plantime.setText(DateUtil
                    .formatDate(bean.BeginWorkTime, DateUtil.HH_MM)
                    + " - "
                    + DateUtil.formatDate(bean.EndWorkTime, DateUtil.HH_MM));
        }

        // 实际任务时间
        if (StringUtil.isNullOrEmpty(DateUtil
                .formatDate(bean.RBeginWorkTime, DateUtil.HH_MM))) {
            holder.realtime.setText("");
        } else {
            holder.realtime.setText(DateUtil
                    .formatDate(bean.RBeginWorkTime, DateUtil.HH_MM)
                    + " - "
                    + DateUtil.formatDate(bean.REndWorkTime, DateUtil.HH_MM));
        }

        holder.workspaces.setText(bean.Workspace);

        // 执行组
        if (StringUtil.isNullOrEmpty(bean.RTeamName)) {
            holder.teamname.setText(bean.DeptName);
        } else {
            if (bean.RTeamName.equals(bean.DeptName))
                holder.teamname.setText(bean.DeptName);
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
            else
                holder.staffname.setText(bean.RStaffNames + "("
                        + bean.StaffName + ")");
        }

        // 任务状态
        if (bean.IsMajor) {
            if (!bean.IsAccepted) {
                holder.taskstatus.setText(R.string.task_status_unaccept); // 未接收
                holder.taskstatus.setTextColor(context.getResources().getColor(
                        R.color.yellow));
            } else {
                if (bean.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) { // 已完成
                    holder.taskstatus.setText(R.string.task_status_complete);
                    holder.taskstatus.setTextColor(context.getResources()
                            .getColor(R.color.green));
                } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) { // 已到岗
                    holder.taskstatus.setText(R.string.task_status_onduty);
                    holder.taskstatus.setTextColor(context.getResources()
                            .getColor(R.color.black));
                } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) { // 未到岗
                    holder.taskstatus.setText(R.string.task_status_unduty);
                    holder.taskstatus.setTextColor(context.getResources()
                            .getColor(R.color.gray));
                } else {
                    holder.taskstatus.setText("");
                }
            }
        } else {
            if (bean.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) { // 已完成
                holder.taskstatus.setText(R.string.task_status_complete);
                holder.taskstatus.setTextColor(context.getResources().getColor(
                        R.color.green));
            } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) { // 已到岗
                holder.taskstatus.setText(R.string.task_status_onduty);
                holder.taskstatus.setTextColor(context.getResources().getColor(
                        R.color.blue));
            } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) { // 未到岗
                holder.taskstatus.setText(R.string.task_status_unduty);
                holder.taskstatus.setTextColor(context.getResources().getColor(
                        R.color.black));
            } else {
                holder.taskstatus.setText("");
            }
        }

        holder.btnCall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClick != null)
                    iClick.onClick(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        //		View itemView;
        TextView taskcontent;
        TextView plantime;
        TextView realtime;
        TextView workspaces;
        TextView teamname;
        TextView taskstatus;
        TextView staffname;
        TextView imgattach;
        Button btnCall;
        LinearLayout layoutcontent;
    }
}
