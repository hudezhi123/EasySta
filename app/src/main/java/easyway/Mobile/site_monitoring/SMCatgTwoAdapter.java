package easyway.Mobile.site_monitoring;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.util.CommonUtils;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SMCatgTwoAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<SMWorkspace> list;

    public SMCatgTwoAdapter(Context context, ArrayList<SMWorkspace> list) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<SMWorkspace> models) {
        list = models;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SMWorkspace bean = list.get(position);
        final ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.site_task_list_item,
                    null);
            holder = new ViewHolder();

            holder.view_A = (RelativeLayout) convertView
                    .findViewById(R.id.a_view);

            holder.view_B = (RelativeLayout) convertView
                    .findViewById(R.id.b_view);

            holder.tv1 = (TextView) convertView.findViewById(R.id.item_tv1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.item_tv2);

            holder.trainNo_a = (TextView) convertView
                    .findViewById(R.id.trainNo_a);
            holder.trainNo_b = (TextView) convertView
                    .findViewById(R.id.trainNo_b);
            holder.status_a = (TextView) convertView
                    .findViewById(R.id.status_a);
            holder.status_b = (TextView) convertView
                    .findViewById(R.id.status_b);
            holder.teams_a = (TextView) convertView
                    .findViewById(R.id.teams_a);
            holder.teams_b = (TextView) convertView
                    .findViewById(R.id.teams_b);
            holder.time_a = (TextView) convertView
                    .findViewById(R.id.time_a);
            holder.time_b = (TextView) convertView
                    .findViewById(R.id.time_b);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String workspaceCurr = bean.taskCurr.Workspace;
        if (workspaceCurr != null) {
            workspaceCurr = workspaceCurr.replace("、", "\n");
        }

        String workspaceNext = bean.taskNext.Workspace;
        if (workspaceNext != null) {
            workspaceNext = workspaceNext.replace("、", "\n");
        }

        holder.tv1.setText(workspaceCurr);
        holder.tv2.setText(workspaceNext);
        holder.trainNo_a.setText(bean.taskCurr.TrainNum);
        holder.trainNo_b.setText(bean.taskNext.TrainNum);

        if (bean.taskCurr.StaffId == SMTask.INVALID_STAFFID)        // 任务到组
            holder.teams_a.setText(bean.taskCurr.DeptName);
        else                                                                        // 任务到人
            holder.teams_a.setText(bean.taskCurr.StaffName);

        if (bean.taskNext.StaffId == SMTask.INVALID_STAFFID)        // 任务到组
            holder.teams_b.setText(bean.taskNext.DeptName);
        else                                                                        // 任务到人
            holder.teams_b.setText(bean.taskNext.StaffName);


        if (StringUtil.isNullOrEmpty(DateUtil
                .formatDate(bean.taskCurr.BeginWorkTime, DateUtil.HH_MM))) {
            holder.time_a.setText("");
        } else {
            holder.time_a.setText(DateUtil
                    .formatDate(bean.taskCurr.BeginWorkTime, DateUtil.HH_MM)
                    + " - "
                    + DateUtil.formatDate(bean.taskCurr.EndWorkTime, DateUtil.HH_MM));
        }

        if (StringUtil.isNullOrEmpty(DateUtil
                .formatDate(bean.taskNext.BeginWorkTime, DateUtil.HH_MM))) {
            holder.time_b.setText("");
        } else {
            holder.time_b.setText(DateUtil
                    .formatDate(bean.taskNext.BeginWorkTime, DateUtil.HH_MM)
                    + " - "
                    + DateUtil.formatDate(bean.taskNext.EndWorkTime, DateUtil.HH_MM));
        }

        if (bean.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {
            holder.status_a.setText(R.string.task_Review);
            holder.status_a.setTextColor(mContext.getResources().getColor(
                    R.color.blue));
        } else if (bean.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) {
            holder.status_a.setText(R.string.task_Completed);
            holder.status_a.setTextColor(mContext.getResources().getColor(
                    R.color.green));
        } else if (bean.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) {
            holder.status_a.setText(R.string.unduty);
            if (!CommonUtils.datecheck(bean.taskCurr.BeginWorkTime))
                holder.status_a.setTextColor(mContext.getResources()
                        .getColor(R.color.red));
            else
                holder.status_a.setTextColor(mContext.getResources()
                        .getColor(R.color.black));
        } else {
            holder.status_a.setText("");
        }

        if (bean.taskNext.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {
            holder.status_b.setText(R.string.task_Review);
            holder.status_b.setTextColor(mContext.getResources().getColor(
                    R.color.blue));
        } else if (bean.taskNext.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) {
            holder.status_b.setText(R.string.task_Completed);
            holder.status_b.setTextColor(mContext.getResources().getColor(
                    R.color.green));
        } else if (bean.taskNext.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) {
            holder.status_b.setText(R.string.unduty);
            if (!CommonUtils.datecheck(bean.taskNext.BeginWorkTime))
                holder.status_b.setTextColor(mContext.getResources()
                        .getColor(R.color.red));
            else
                holder.status_b.setTextColor(mContext.getResources()
                        .getColor(R.color.black));
        } else {
            holder.status_b.setText("");
        }

        holder.view_A.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (bean.taskCurr.Workspace == null
                        || bean.taskCurr.Workspace.trim().length() == 0)
                    return;

                if (bean.taskCurr.TrainNum == null
                        || bean.taskCurr.TrainNum.trim().length() == 0)
                    return;

                Intent intent = new Intent(mContext,
                        TaskDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SMTask.KEY_TASK, bean.taskCurr);
                intent.putExtras(bundle);
                mContext.startActivity(intent);

            }
        });

        holder.view_B.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.taskNext.Workspace == null
                        || bean.taskNext.Workspace.trim().length() == 0)
                    return;

                if (bean.taskNext.TrainNum == null
                        || bean.taskNext.TrainNum.trim().length() == 0)
                    return;

                Intent intent = new Intent(mContext,
                        TaskDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SMTask.KEY_TASK, bean.taskNext);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        public TextView tv1;
        public TextView tv2;

        public TextView trainNo_a;
        public TextView trainNo_b;
        public TextView status_a;
        public TextView status_b;
        public TextView teams_a;
        public TextView teams_b;
        public TextView time_a;
        public TextView time_b;

        public View view_A;
        public View view_B;
    }
}
