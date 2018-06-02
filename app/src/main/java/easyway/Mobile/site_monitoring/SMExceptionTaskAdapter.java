package easyway.Mobile.site_monitoring;

import java.util.ArrayList;

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
 * 异常任务Adapter
 */
public class SMExceptionTaskAdapter extends BaseAdapter {
    private ArrayList<TaskChild> todoList;
    private Context context;
    private LayoutInflater mInflater;

    public SMExceptionTaskAdapter(Context context, ArrayList<TaskChild> list) {
        mInflater = LayoutInflater.from(context);
        todoList = list;
        this.context = context;
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
        TaskChild bean = todoList.get(position);
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
            holder.layoutcontent = (LinearLayout) convertView
                    .findViewById(R.id.layoutcontent);
            holder.layouttrainno = (LinearLayout) convertView
                    .findViewById(R.id.layouttrainno);
            holder.layoutprincipal = (RelativeLayout) convertView
                    .findViewById(R.id.layoutprincipal);
            holder.principal = (TextView) convertView
                    .findViewById(R.id.principal);
            holder.btnCall = (Button) convertView
                    .findViewById(R.id.btnCall);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.taskname.setText(bean.TaskName);
        if (bean.IsMajor) {    // 重点任务任务名为红色，否则为黑
            holder.taskname.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.taskname.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.taskname.getPaint().setFakeBoldText(true);

        // 负责人
        if (bean.IsMajor) {
            holder.layoutprincipal.setVisibility(View.VISIBLE);
            holder.principal.setText(bean.ChargeStaffName);
            holder.btnCall.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PTTUtil.call(context, Staff.GetExpend1ByStaffId(context, (todoList.get(position).ChargeStaffId)),
                            PTTUtil.AUDIO_CALL);
                }
            });
        } else {
            holder.layoutprincipal.setVisibility(View.GONE);
        }

        // 任务内容（备注）非重点任务任务内容部不显示
        if (bean.TaskRemark == null || bean.TaskRemark.equals("") || !bean.IsMajor) {
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

        // 附件
        if (bean.AttachList == null || bean.AttachList.size() == 0) {
            holder.imgattach.setVisibility(View.GONE);
        } else {
            holder.imgattach.setVisibility(View.VISIBLE);
            final ArrayList<String> list = bean.AttachList;
            holder.imgattach.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(context,
                            AttachList.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("url", list);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }

        // 计划任务时间
        if (StringUtil.isNullOrEmpty(DateUtil
                .formatDate(bean.BeginWorkTime, DateUtil.HH_MM))) {
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
                holder.teamname.setText(bean.RTeamName + "(" + bean.DeptName + ")");
        }

        // 执行人
        if (StringUtil.isNullOrEmpty(bean.RStaffNames)) {
            holder.staffname.setText(bean.StaffName);
        } else {
            if (bean.RStaffNames.equals(bean.StaffName))
                holder.staffname.setText(bean.StaffName);
            else
                holder.staffname.setText(bean.RStaffNames + "(" + bean.StaffName + ")");
        }

        // 任务状态
        if (bean.TaskSta == TaskMajor.TASK_STATE_CANCEL) {
            holder.taskstatus.setText(R.string.task_status_cancel);    // 已取消
            holder.taskstatus.setTextColor(context.getResources().getColor(R.color.gray));
        } else if (bean.IsMajor) {
            if (!bean.IsAccepted) {
                holder.taskstatus.setText(R.string.task_status_unaccept);                // 未接收
                holder.taskstatus.setTextColor(context.getResources().getColor(R.color.yellow));
            } else {
                if (bean.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) {            // 已完成
                    holder.taskstatus.setText(R.string.task_status_complete);
                    holder.taskstatus.setTextColor(context.getResources().getColor(R.color.green));
                } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {        // 已到岗
                    holder.taskstatus.setText(R.string.task_status_onduty);
                    holder.taskstatus.setTextColor(context.getResources().getColor(R.color.blue));
                } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) {        // 未到岗
                    holder.taskstatus.setText(R.string.task_status_unduty);
                    holder.taskstatus.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    holder.taskstatus.setText("");
                }
            }
        } else {
            if (bean.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) {            // 已完成
                holder.taskstatus.setText(R.string.task_status_complete);
                holder.taskstatus.setTextColor(context.getResources().getColor(R.color.green));
            } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {        // 已到岗
                holder.taskstatus.setText(R.string.task_status_onduty);
                holder.taskstatus.setTextColor(context.getResources().getColor(R.color.blue));
            } else if (bean.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) {        // 未到岗
                holder.taskstatus.setText(R.string.task_status_unduty);
                holder.taskstatus.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                holder.taskstatus.setText("");
            }
        }

        return convertView;
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
        Button btnCall;
        LinearLayout layouttrainno;
        LinearLayout layoutcontent;
        RelativeLayout layoutprincipal;
    }
}
