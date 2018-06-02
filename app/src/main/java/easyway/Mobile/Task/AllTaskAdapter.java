package easyway.Mobile.Task;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.Attach.AttachList;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Data.TaskMajor;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.PTTUtil;
import easyway.Mobile.util.StringUtil;

/**
 * @author wisely
 */

public class AllTaskAdapter extends BaseExpandableListAdapter {

    LayoutInflater layoutInflater;
    Context context;
    private ITaskReview iTaskReview;

    public AllTaskAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setITaskReview(ITaskReview iTaskReview) {
        this.iTaskReview = iTaskReview;
    }

    List<String> groupList;
    List<List<TaskChild>> childList;

    public void setDatas(List<String> groupList, List<List<TaskChild>> childList) {
        this.groupList = groupList;
        this.childList = childList;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return groupList == null ? 0 : groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList == null ? 0 : childList.get(groupPosition).size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_expandable_group, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_all_task_group);
            holder.iv_expandable = (ImageView) convertView.findViewById(R.id.iv_expandable);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        String s = groupList.get(groupPosition);
        holder.textView.setText(s);
        if (isExpanded) {
            holder.iv_expandable.setImageResource(R.drawable.list_open);
        } else {
            holder.iv_expandable.setImageResource(R.drawable.list_close);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = layoutInflater.inflate(R.layout.task_item, null);
            holder.itemView = convertView
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
            holder.passenger_num = (TextView) convertView.findViewById(R.id.passenger_num);
            holder.tv_xcrs = (TextView) convertView.findViewById(R.id.tv_xcrs);
            holder.tv_zzrs = (TextView) convertView.findViewById(R.id.tv_zzrs);

            holder.btnGarbage = (Button) convertView.findViewById(R.id.btnGarbage);
            holder.GarbageStaffName = (TextView) convertView.findViewById(R.id.GarbageStaffName);
            holder.layGarbage = (RelativeLayout) convertView
                    .findViewById(R.id.layGarbage);
            holder.btnSecureTips = (Button) convertView.findViewById(R.id.btn_secure_tips);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }


        List<TaskChild> taskChildren = childList.get(groupPosition);
        final TaskChild bean = taskChildren.get(childPosition);


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
            holder.btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PTTUtil.call(
                            context,
                            Staff.GetExpend1ByStaffId(context,
                                    bean.ChargeStaffId),
                            PTTUtil.AUDIO_CALL);
                }
            });
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

        holder.passenger_num.setText(bean.SCRS);
        holder.tv_xcrs.setText(bean.XCRS);
        holder.tv_zzrs.setText(bean.ZZRS);

        // 附件
        if (bean.AttachList == null || bean.AttachList.size() == 0) {
            holder.imgattach.setVisibility(View.GONE);
        } else {
            holder.imgattach.setVisibility(View.VISIBLE);
            final ArrayList<String> list = bean.AttachList;
            holder.imgattach.setOnClickListener(new View.OnClickListener() {

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
        if (StringUtil.isNullOrEmpty(DateUtil.formatDate(bean.BeginWorkTime, DateUtil.YYYY_MM_DD_HH_MM_SS))) {
            holder.plantime.setText("");
        } else {
            holder.plantime.setText(DateUtil.formatDate(bean.BeginWorkTime, DateUtil.HH_MM)
                    + " - "
                    + DateUtil.formatDate(bean.EndWorkTime, DateUtil.HH_MM));
        }

        // 实际任务时间
        if (StringUtil.isNullOrEmpty(DateUtil.formatDate(bean.RBeginWorkTime,DateUtil.HH_MM))) {
            holder.realtime.setText("");
        } else {
            holder.realtime.setText(DateUtil.formatDate(bean.RBeginWorkTime,DateUtil.HH_MM)
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

        holder.layGarbage.setVisibility(ViewGroup.GONE);
        holder.btnok.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpNext(bean);
            }
        });

        holder.btnSecureTips.setOnClickListener(new View.OnClickListener() {
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

    public void jumpNext(TaskChild task) {
        Intent intent = new Intent(context, RelatedTaskList.class);
        intent.putExtra(RelatedTaskList.KEY_TRNO, task.TRNO_PRO);
        intent.putExtra(RelatedTaskList.KEY_DATE,
                DateUtil.formatDate(task.PlanDate,DateUtil.YYYY_MM_DD));
        context.startActivity(intent);
    }

    public class GroupViewHolder {
        TextView textView;
        ImageView iv_expandable;
    }

    public class ChildViewHolder {
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
        Button btnCall, btnok, btnGarbage;
        LinearLayout layouttrainno;
        LinearLayout layoutcontent;
        RelativeLayout layoutprincipal;
        TextView passenger_num;
        TextView tv_xcrs;
        TextView tv_zzrs;
        TextView GarbageStaffName;
        RelativeLayout layGarbage;
        Button btnSecureTips;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
