package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 主任务信息
public class TaskMajor implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public static final int TASK_ACCEPTSTATUS_NOT = 0; // 未接收
	public static final int TASK_ACCEPTSTATUS_PART = 1; // 部分接收
	public static final int TASK_ACCEPTSTATUS_ALL = 2; // 完全接收
	
	public static final int TASK_EXCSTATE_UNDUTY = 0; // 未到岗
	public static final int TASK_EXCSTATE_PARTONDUTY = 1; // 部分到岗
	public static final int TASK_EXCSTATE_PARTCOMPLETE = 2; //部分完成
	public static final int TASK_EXCSTATE_ONDUTY = 11; // 完全到岗
	public static final int TASK_EXCSTATE_ONDUTYCOMPLETE = 21; // 到岗完成
	public static final int TASK_EXCSTATE_COMPLETE = 22; // 完成

	public static final int TASK_STATE_NORMAL = 0;	// 计划内任务
	public static final int TASK_STATE_CANCEL = 1;		// 取消任务
	public static final int TASK_STATE_ADD = 2;			// 新增任务
	public static final int TASK_STATE_COMPLETE = 3;	// 任务执行完成
	
	public long TaskId; // 大任务ID
	public String PlanDate;
	public String TaskName; // 任务名称
	public String TRNO_PRO; // 任务车次，若为空，则任务为非车次任务
	public String BeginWorkTime;
	public String EndWorkTime;
	public String RBeginWorkTime;
	public String REndWorkTime;
	public int TaskMethod;
	public int TaskLevel;
	public int AcceptStat;
	public int ExcSta;
	public int TaskSta;
	public int CreateMethod;
	public boolean IsMajor;
	public boolean IsDraft;
	public long ChargeStaffId;
	public String ChargeStaffName;
	public long CreateStaffId;
	public String CreateStaffName;
	public String CreateDttm;
	public String StationCode;

	public static ArrayList<TaskMajor> ParseFromString(String result) {
		ArrayList<TaskMajor> list = new ArrayList<TaskMajor>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TaskMajor task = new TaskMajor();

			task.TaskId = JsonUtil.GetJsonObjLongValue(jsonObj, "TaskId");
			task.PlanDate = JsonUtil.GetJsonObjStringValue(jsonObj, "PlanDate");
			task.TaskName = JsonUtil.GetJsonObjStringValue(jsonObj, "TaskName");
			task.TRNO_PRO = JsonUtil.GetJsonObjStringValue(jsonObj, "TRNO_PRO");
			task.BeginWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj,
					"BeginWorkTime");
			task.EndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj,
					"EndWorkTime");
			task.RBeginWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj,
					"RBeginWorkTime");
			task.REndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj,
					"REndWorkTime");
			task.TaskMethod = JsonUtil
					.GetJsonObjIntValue(jsonObj, "TaskMethod");
			task.TaskLevel = JsonUtil.GetJsonObjIntValue(jsonObj, "TaskLevel");
			task.AcceptStat = JsonUtil
					.GetJsonObjIntValue(jsonObj, "AcceptStat");
			task.ExcSta = JsonUtil.GetJsonObjIntValue(jsonObj, "ExcSta");
			task.TaskSta = JsonUtil.GetJsonObjIntValue(jsonObj, "TaskSta");
			task.CreateMethod = JsonUtil.GetJsonObjIntValue(jsonObj,
					"CreateMethod");
			task.IsMajor = JsonUtil.GetJsonObjBooleanValue(jsonObj, "IsMajor");
			task.IsDraft = JsonUtil.GetJsonObjBooleanValue(jsonObj, "IsDraft");
			task.ChargeStaffId = JsonUtil.GetJsonObjLongValue(jsonObj,
					"ChargeStaffId");
			task.ChargeStaffName = JsonUtil.GetJsonObjStringValue(jsonObj,
					"ChargeStaffName");
			task.CreateStaffId = JsonUtil.GetJsonObjLongValue(jsonObj,
					"CreateStaffId");
			task.CreateStaffName = JsonUtil.GetJsonObjStringValue(jsonObj,
					"CreateStaffName");
			task.CreateDttm = JsonUtil.GetJsonObjStringValue(jsonObj,
					"CreateDttm");
			task.StationCode = JsonUtil.GetJsonObjStringValue(jsonObj,
					"StationCode");

			if (task.IsMajor)
				list.add(task);
		}

		return list;
	}
}
