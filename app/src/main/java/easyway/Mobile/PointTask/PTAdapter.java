package easyway.Mobile.PointTask;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.TaskMajor;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.LinearLayout;

/*
 * 任务Adapter
 */
public class PTAdapter extends BaseAdapter {
    private ArrayList<TaskMajor> tasklist;
    private Context context;
    private LayoutInflater mInflater;

    public PTAdapter(Context context, ArrayList<TaskMajor> list) {
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

    public TaskMajor getItem(int position) {
        if (tasklist == null)
            return null;
        if (tasklist.size() <= position)
            return null;
        return tasklist.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<TaskMajor> models) {
        tasklist = models;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final TaskMajor bean = tasklist.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.pointtask_item, null);
            holder = new ViewHolder();
            holder.itemView = (LinearLayout) convertView
                    .findViewById(R.id.item_view);
            holder.taskname = (TextView) convertView
                    .findViewById(R.id.taskname);
            holder.tasktrainno = (TextView) convertView
                    .findViewById(R.id.tasktrainno);
            holder.plantime = (TextView) convertView
                    .findViewById(R.id.plantime);
            holder.realtime = (TextView) convertView
                    .findViewById(R.id.realtime);
            holder.publisher = (TextView) convertView
                    .findViewById(R.id.publisher);
            holder.principal = (TextView) convertView
                    .findViewById(R.id.principal);
            holder.taskstatus = (TextView) convertView
                    .findViewById(R.id.taskstatus);
            holder.layouttrainno = (LinearLayout) convertView
                    .findViewById(R.id.layouttrainno);
            // holder.btnCancel = (Button) convertView
            // .findViewById(R.id.btnCancel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 任务名称
        holder.taskname.setText(bean.TaskName);
        holder.taskname.getPaint().setFakeBoldText(true);

        // 任务内容（备注）非重点任务任务内容部不显示
        if (bean.TRNO_PRO == null || bean.TRNO_PRO.equals("") || !bean.IsMajor) {
            holder.layouttrainno.setVisibility(View.GONE);
        } else {
            holder.layouttrainno.setVisibility(View.VISIBLE);
            holder.tasktrainno.setText(bean.TRNO_PRO);
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

        holder.publisher.setText(bean.CreateStaffName);
        holder.principal.setText(bean.ChargeStaffName);

        if (bean.TaskSta == TaskMajor.TASK_STATE_CANCEL) {
            holder.taskstatus.setText(R.string.task_status_cancel); // 已取消
            holder.taskstatus.setTextColor(context.getResources().getColor(
                    R.color.gray));
        } else {
            if (bean.IsDraft) {
                holder.taskstatus.setText(R.string.task_status_draft); // 未发布
                holder.taskstatus.setTextColor(context.getResources().getColor(
                        R.color.darkgray));
            } else {
                if (bean.AcceptStat == TaskMajor.TASK_ACCEPTSTATUS_NOT) {
                    holder.taskstatus.setText(R.string.task_status_unaccept); // 未接收
                    holder.taskstatus.setTextColor(context.getResources()
                            .getColor(R.color.yellow));
                } else if (bean.AcceptStat == TaskMajor.TASK_ACCEPTSTATUS_PART) {
                    holder.taskstatus.setText(R.string.task_status_partaccept); // 部分接收
                    holder.taskstatus.setTextColor(context.getResources()
                            .getColor(R.color.blue));
                } else if (bean.AcceptStat == TaskMajor.TASK_ACCEPTSTATUS_ALL) {
                    if (bean.ExcSta == TaskMajor.TASK_EXCSTATE_UNDUTY) {
                        holder.taskstatus.setText(R.string.task_status_accept); // 已接收（未到岗）
                        holder.taskstatus.setTextColor(context.getResources()
                                .getColor(R.color.blue));
                    } else if (bean.ExcSta == TaskMajor.TASK_EXCSTATE_PARTONDUTY) {
                        holder.taskstatus
                                .setText(R.string.task_status_partonduty); // 部分到岗
                        holder.taskstatus.setTextColor(context.getResources()
                                .getColor(R.color.black));
                    } else if (bean.ExcSta == TaskMajor.TASK_EXCSTATE_ONDUTY) {
                        holder.taskstatus.setText(R.string.task_status_onduty); // 已到岗
                        holder.taskstatus.setTextColor(context.getResources()
                                .getColor(R.color.black));
                    } else if (bean.ExcSta == TaskMajor.TASK_EXCSTATE_PARTCOMPLETE) {
                        holder.taskstatus
                                .setText(R.string.task_status_partcomplete); // 部分完成
                        holder.taskstatus.setTextColor(context.getResources()
                                .getColor(R.color.green));
                    } else if (bean.ExcSta == TaskMajor.TASK_EXCSTATE_ONDUTYCOMPLETE) {
                        holder.taskstatus.setText(R.string.task_status_ondutycomplete); // 到岗完成
                        holder.taskstatus.setTextColor(context.getResources().getColor(R.color.green));
                    } else if (bean.ExcSta == TaskMajor.TASK_EXCSTATE_COMPLETE) {
                        holder.taskstatus
                                .setText(R.string.task_status_complete); // 已完成
                        holder.taskstatus.setTextColor(context.getResources()
                                .getColor(R.color.green));
                    } else {
                        holder.taskstatus.setText("");
                    }
                }
            }
        }

        return convertView;
    }

    static class ViewHolder {
        View itemView;
        TextView taskname;
        TextView tasktrainno;
        TextView plantime;
        TextView realtime;
        TextView publisher;
        TextView taskstatus;
        TextView principal;
        LinearLayout layouttrainno;
        // Button btnCancel;
    }
}
