package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.TB_TASK_PlanReal;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Shift_In_Show_Team_Train_Task_Adapter extends BaseAdapter {

	private ArrayList<TB_TASK_PlanReal> todoList;
	private Context context;
	LayoutInflater infl = null;

	public Shift_In_Show_Team_Train_Task_Adapter(Context context,
			ArrayList<TB_TASK_PlanReal> todoList) {
		this.context = context;
		this.todoList = todoList;
		this.infl = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		if (todoList == null)
			return 0;
		return todoList.size();
	}

	public TB_TASK_PlanReal getItem(int position) {
		if (getCount() == 0)
			return null;
		return todoList.get(position);
	}

	public long getItemId(int position) {
		if (getCount() == 0)
			return 0;
		return getItem(position).ID;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		TB_TASK_PlanReal taskPlanReal = getItem(position);
		// shift_in_team_train_task_list_item
		convertView = infl.inflate(R.layout.shift_in_team_train_task_list_item,
				null);

		if (taskPlanReal == null)
			return null;
		TextView labTaskName = (TextView) convertView
				.findViewById(R.id.labTaskName), labBeginWorkTime = (TextView) convertView
				.findViewById(R.id.labBeginWorkTime), labEndWorkTime = (TextView) convertView
				.findViewById(R.id.labEndWorkTime), labRBeginWorkTime = (TextView) convertView
				.findViewById(R.id.labRBeginWorkTime), labREndWorkTime = (TextView) convertView
				.findViewById(R.id.labREndWorkTime), labWorkSpaces = (TextView) convertView
				.findViewById(R.id.labWorkSpaces), labExcSta = (TextView) convertView
				.findViewById(R.id.labExcSta), labTaskLevel = (TextView) convertView
				.findViewById(R.id.labTaskLevel);

		
		

		labTaskName.setText(":"+taskPlanReal.TaskName);
		
		labBeginWorkTime.setText(":"+taskPlanReal.BeginWorkTime);

		labEndWorkTime.setText(":"+taskPlanReal.EndWorkTime);
		labRBeginWorkTime.setText(":"+taskPlanReal.RBeginWorkTime);
		labREndWorkTime.setText(":"+taskPlanReal.REndWorkTime);
		labWorkSpaces.setText(":"+taskPlanReal.WorkSpaces);

		String[] task_ExcSta_Values = context.getResources().getStringArray(
				R.array.task_ExcSta_Values), task_TaskLevel_Values = context
				.getResources().getStringArray(R.array.task_TaskLevel_Values);

		
		int taskLevel = (int) taskPlanReal.TaskLevel - 1;
		if (taskLevel >= 0 && taskLevel < task_TaskLevel_Values.length) {
			labTaskLevel.setText(":"+task_TaskLevel_Values[(int) taskPlanReal.TaskLevel - 1]);
		} else {
			labTaskLevel.setText(":"+context.getString(R.string.task_TaskLevel_Err));
		}

		
		int excStat = (int) taskPlanReal.ExcSta;

		
		if (excStat >= 0 && excStat < task_ExcSta_Values.length) {
			labExcSta.setText(":"+task_ExcSta_Values[excStat]);
		} else {
			labExcSta.setText(":"+context.getString(R.string.task_ExcSta_Err));
		}

		return convertView;
	}

}
