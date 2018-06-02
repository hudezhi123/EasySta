package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.R;
import easyway.Mobile.Data.TB_TASK_PlanReal;
import easyway.Mobile.R.drawable;
import easyway.Mobile.R.string;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShiftOut_Task_List_Adapter extends BaseAdapter
{

    private ArrayList<TB_TASK_PlanReal> todoList;
    private Context context;
    private HashMap<Long, String> showedTrainNo = new HashMap<Long, String>();

    public ShiftOut_Task_List_Adapter(Context context,
            ArrayList<TB_TASK_PlanReal> todoList)
    {
        this.context = context;
        this.todoList = todoList;
    }

    public int getCount()
    {
        if (todoList == null)
            return 0;
        return todoList.size();
    }

    public TB_TASK_PlanReal getItem(int position)
    {
        if (getCount() == 0)
            return null;
        return todoList.get(position);
    }

    public long getItemId(int position)
    {
        if (getCount() == 0)
            return 0;
        return getItem(position).ID;
    }

    private OnClickListener remarkLis(final TB_TASK_PlanReal taskPlanReal)
    {
        return new OnClickListener()
        {

            public void onClick(View v)
            {
                Intent intent = new Intent(context, Shift_In_Remark.class);
                Bundle bundle = new Bundle();
                bundle.putString("TrainNo", taskPlanReal.TrainNum);
                bundle.putLong("TeamId", taskPlanReal.TeamId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TB_TASK_PlanReal taskPlanReal = getItem(position);
        if (taskPlanReal == null)
            return null;
        long id = taskPlanReal.ID;

        LinearLayout tableRowRoot = new LinearLayout(context);
        tableRowRoot.setOrientation(LinearLayout.VERTICAL);
        tableRowRoot.setBackgroundDrawable(context.getResources().getDrawable(
                drawable.bg_list));

        LinearLayout trTop = new LinearLayout(context);
        trTop.setBackgroundDrawable(context.getResources().getDrawable(
                drawable.bg_list_top));
        trTop.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 5));
        tableRowRoot.addView(trTop);

        LinearLayout trItem = new LinearLayout(context);
        String trainNo = taskPlanReal.TrainNum;

        if (!showedTrainNo.containsValue(trainNo))
        {
            showedTrainNo.put(id, trainNo);
            Log.d("Shift_Out_Adapter", "Show TrainNo" + trainNo);
        }

        if (showedTrainNo.containsKey(id))
        {
            LinearLayout trTrainNum = new LinearLayout(context);
            trTrainNum.setBackgroundColor(Color.GRAY);
            trTrainNum.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, 70));
            trTrainNum.setGravity(Gravity.CENTER | Gravity.LEFT);

            // TrainNum
            TextView labTrainNum = new TextView(context);
            labTrainNum.setText(context.getString(string.trainNo) + ":"
                    + trainNo);
            labTrainNum.setTextColor(Color.BLACK);
            labTrainNum.setTextSize(20);
            trTrainNum.addView(labTrainNum);

            LinearLayout trBtnsTrainNum = new LinearLayout(context);
            trBtnsTrainNum.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            trBtnsTrainNum.setGravity(Gravity.RIGHT | Gravity.CENTER);
            Button btnRemark = new Button(context);
            btnRemark.setText(string.remark);
            btnRemark.setBackgroundDrawable(context.getResources().getDrawable(
                    drawable.bg_btn_text));
            btnRemark.setOnClickListener(remarkLis(taskPlanReal));
            trBtnsTrainNum.addView(btnRemark);
            trTrainNum.addView(trBtnsTrainNum);
            tableRowRoot.addView(trTrainNum);
        }

        // TaskName
        TextView labTaskName = new TextView(context);
        labTaskName.setText(context.getString(string.task_taskName) + ":"
                + taskPlanReal.TaskName);
        labTaskName.setTextColor(Color.BLACK);
        trItem.addView(labTaskName);
        tableRowRoot.addView(trItem);

        // ActorName
        TextView labStaffName = new TextView(context);
        /*labStaffName.setText(context.getString(string.staff) + ":"
                + taskPlanReal.ActorName);*/
        labStaffName.setTextColor(Color.BLACK);
        tableRowRoot.addView(labStaffName);

        // BeginWorkTime
        TextView labBeginWorkTime = new TextView(context);
        labBeginWorkTime.setText(context.getString(string.task_BeginWorkTime)
                + ":" + taskPlanReal.BeginWorkTime);
        labBeginWorkTime.setTextColor(Color.BLACK);
        tableRowRoot.addView(labBeginWorkTime);

        // EndWorkTime
        TextView labEndWorkTime = new TextView(context);
        labEndWorkTime.setText(context.getString(string.task_EndWorkTime) + ":"
                + taskPlanReal.EndWorkTime);
        labEndWorkTime.setTextColor(Color.BLACK);
        tableRowRoot.addView(labEndWorkTime);

        // RBeginWorkTime
        TextView labRBeginWorkTime = new TextView(context);
        labRBeginWorkTime.setText(context.getString(string.task_RBeginWorkTime)
                + ":" + taskPlanReal.RBeginWorkTime);
        labRBeginWorkTime.setTextColor(Color.BLACK);
        tableRowRoot.addView(labRBeginWorkTime);

        // REndWorkTime
        TextView labREndWorkTime = new TextView(context);
        labREndWorkTime.setText(context.getString(string.task_REndWorkTime)
                + ":" + taskPlanReal.REndWorkTime);
        labREndWorkTime.setTextColor(Color.BLACK);
        tableRowRoot.addView(labREndWorkTime);

        // WorkSpaces
        TextView labWorkSpaces = new TextView(context);
        labWorkSpaces.setText(context.getString(string.task_WorkSpaces) + ":"
                + taskPlanReal.WorkSpaces);
        labWorkSpaces.setTextColor(Color.BLACK);
        tableRowRoot.addView(labWorkSpaces);

        String[] task_ExcSta_Values = context.getResources().getStringArray(
                R.array.task_ExcSta_Values), task_TaskLevel_Values = context
                .getResources().getStringArray(R.array.task_TaskLevel_Values);

        // TaskLevel
        TextView labTaskLevel = new TextView(context);
        int taskLevel = (int) taskPlanReal.TaskLevel - 1;
        if (taskLevel >= 0 && taskLevel < task_TaskLevel_Values.length)
        {
            labTaskLevel.setText(context.getString(string.task_TaskLevel) + ":"
                    + task_TaskLevel_Values[(int) taskPlanReal.TaskLevel - 1]);
        }
        else
        {
            labTaskLevel.setText(context.getString(string.task_TaskLevel) + ":"
                    + context.getString(string.task_TaskLevel_Err));
        }

        labTaskLevel.setTextColor(Color.BLACK);
        tableRowRoot.addView(labTaskLevel);

        int excStat = (int) taskPlanReal.ExcSta;

        // TaskLevel
        TextView labExcSta = new TextView(context);
        if (excStat >= 0 && excStat < task_ExcSta_Values.length)
        {
            labExcSta.setText(context.getString(string.task_ExcSta) + ":"
                    + task_ExcSta_Values[excStat]);
        }
        else
        {
            labExcSta.setText(context.getString(string.task_ExcSta) + ":"
                    + context.getString(string.task_ExcSta_Err));
        }

        labExcSta.setTextColor(Color.BLACK);
        tableRowRoot.addView(labExcSta);

        LinearLayout trBottom = new LinearLayout(context);
        trBottom.setBackgroundDrawable(context.getResources().getDrawable(
                drawable.bg_list_bottom));
        trBottom.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 5));
        tableRowRoot.addView(trBottom);

        return tableRowRoot;
    }

}
