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

public class SMCatgOneAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<SMWorkspace> list;

    public SMCatgOneAdapter(Context context, ArrayList<SMWorkspace> list) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else {
            if (list.size() % 2 == 0)
                return list.size() / 2;
            else
                return list.size() / 2 + 1;
        }
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
        int indexA = position * 2;
        int indexB = position * 2 + 1;

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

        // 左边区域
        if (indexA < list.size()) {
            final SMWorkspace beanA = list.get(indexA);
            String workspace = beanA.taskCurr.Workspace;
            if (workspace != null) {
//				workspace = workspace.replace(mContext.getString(R.string.checkin),
//						"");
//				workspace = workspace.replace(mContext.getString(R.string.platform),
//						"");
                workspace = workspace.replace("、", "\n");
            }
            holder.tv1.setText(workspace);
            holder.trainNo_a.setText(beanA.taskCurr.TrainNum);
            if (beanA.taskCurr.StaffId == SMTask.INVALID_STAFFID)        // 任务到组
                holder.teams_a.setText(beanA.taskCurr.DeptName);
            else                                                                                // 任务到人
                holder.teams_a.setText(beanA.taskCurr.StaffName);

            if (StringUtil.isNullOrEmpty(DateUtil
                    .formatDate(beanA.taskCurr.BeginWorkTime, DateUtil.HH_MM))) {
                holder.time_a.setText("");
            } else {
                holder.time_a.setText(DateUtil
                        .formatDate(beanA.taskCurr.BeginWorkTime, DateUtil.HH_MM)
                        + " - "
                        + DateUtil.formatDate(beanA.taskCurr.EndWorkTime, DateUtil.HH_MM));
            }

            if (beanA.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {
                holder.status_a.setText(R.string.task_Review);
                holder.status_a.setTextColor(mContext.getResources().getColor(
                        R.color.blue));
            } else if (beanA.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) {
                holder.status_a.setText(R.string.task_Completed);
                holder.status_a.setTextColor(mContext.getResources().getColor(
                        R.color.green));
            } else if (beanA.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) {
                holder.status_a.setText(R.string.unduty);
                if (!CommonUtils.datecheck(beanA.taskCurr.BeginWorkTime))
                    holder.status_a.setTextColor(mContext.getResources()
                            .getColor(R.color.red));
                else
                    holder.status_a.setTextColor(mContext.getResources()
                            .getColor(R.color.black));
            } else {
                holder.status_a.setText("");
            }

            holder.view_A.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (beanA.taskCurr.Workspace == null
                            || beanA.taskCurr.Workspace.trim().length() == 0)
                        return;

                    if (beanA.taskCurr.TrainNum == null
                            || beanA.taskCurr.TrainNum.trim().length() == 0)
                        return;

                    Intent intent = new Intent(mContext,
                            TaskDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SMTask.KEY_TASK, beanA.taskCurr);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.tv1.setText("");
            holder.trainNo_a.setText("");
            holder.status_a.setText("");
            holder.teams_a.setText("");
            holder.time_a.setText("");
            holder.view_A.setOnClickListener(null);
        }

        // 右边区域
        if (indexB < list.size()) {
            final SMWorkspace beanB = list.get(indexB);
            String workspace = beanB.taskCurr.Workspace;
            if (workspace != null) {
//				workspace = workspace.replace(mContext.getString(R.string.checkin),
//						"");
//				workspace = workspace.replace(mContext.getString(R.string.platform),
//						"");
                workspace = workspace.replace("、", "\n");
            }
            holder.tv2.setText(workspace);
            holder.trainNo_b.setText(beanB.taskCurr.TrainNum);
            if (beanB.taskCurr.StaffId == SMTask.INVALID_STAFFID)        // 任务到组
                holder.teams_b.setText(beanB.taskCurr.DeptName);
            else                                                                                // 任务到人
                holder.teams_b.setText(beanB.taskCurr.StaffName);

            if (StringUtil.isNullOrEmpty(DateUtil
                    .formatDate(beanB.taskCurr.BeginWorkTime, DateUtil.HH_MM))) {
                holder.time_b.setText("");
            } else {
                holder.time_b.setText(DateUtil
                        .formatDate(beanB.taskCurr.BeginWorkTime, DateUtil.HH_MM)
                        + " - "
                        + DateUtil.formatDate(beanB.taskCurr.EndWorkTime, DateUtil.HH_MM));
            }

            if (beanB.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {
                holder.status_b.setText(R.string.task_Review);
                holder.status_b.setTextColor(mContext.getResources().getColor(
                        R.color.blue));
            } else if (beanB.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) {
                holder.status_b.setText(R.string.task_Completed);
                holder.status_b.setTextColor(mContext.getResources().getColor(
                        R.color.green));
            } else if (beanB.taskCurr.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) {
                holder.status_b.setText(R.string.unduty);
                if (!CommonUtils.datecheck(beanB.taskCurr.BeginWorkTime))
                    holder.status_b.setTextColor(mContext.getResources()
                            .getColor(R.color.red));
                else
                    holder.status_b.setTextColor(mContext.getResources()
                            .getColor(R.color.black));
            } else {
                holder.status_b.setText("");
            }

            holder.view_B.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (beanB.taskCurr.Workspace == null
                            || beanB.taskCurr.Workspace.trim().length() == 0)
                        return;

                    if (beanB.taskCurr.TrainNum == null
                            || beanB.taskCurr.TrainNum.trim().length() == 0)
                        return;

                    Intent intent = new Intent(mContext,
                            TaskDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SMTask.KEY_TASK, beanB.taskCurr);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.tv2.setText("");
            holder.trainNo_b.setText("");
            holder.status_b.setText("");
            holder.teams_b.setText("");
            holder.time_b.setText("");
            holder.view_B.setOnClickListener(null);
        }

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
