package easyway.Mobile.PointTask;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.StringUtil;

// 子任务信息
public class TaskChild implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public static final int TASK_EXCSTATE_UNDUTY = 0; // 未到岗
	public static final int TASK_EXCSTATE_ONDUTY = 1; // 已到岗
	public static final int TASK_EXCSTATE_COMPLETE = 2; // 已完成

	public long SaId; // 子任务ID
	public long TaskId; // 大任务ID
	public long DeptId;
	public String DeptName;
	public long StaffId;
	public String StaffName;
	public String BeginWorkTime;
	public String EndWorkTime;
	public long RTeamId;
	public String RTeamName;
	public String RStaffIds; // 执行者ID（可多人）
	public String RStaffNames;
	public String RBeginWorkTime;
	public String REndWorkTime;
	public long TwId; // 任务区域ID
	public String Workspace; // 任务区域
	public String RWorkspaces;
	public long PId; // 岗位ID
	public String PositionName; // 岗位名称
	public boolean Conflict; // 是否冲突
	public String TaskRemark; // 备注
	public boolean IsAccepted;
	public String AcceptDttm;
	public int AExcStat;		// 小任务执行状态
	public String PlanDate;
	public String TaskName; // 任务名称
	public String TRNO_PRO; // 任务车次，若为空，则任务为非车次任务
	public String tBeginWorkTime;
	public String tEndWorkTime;
	public String tRBeginWorkTime;
	public String tREndWorkTime;
	public int TaskMethod;
	public int TaskLevel;
	public int ExcSta;
	public int TaskSta;
	public int CreateMethod;
	public int AcceptStat;
	public boolean IsDraft;
	public boolean IsMajor;
	public long ChargeStaffId;
	public String ChargeStaffName;
	public String StationCode;
	//上车人数
	public String SCRS;
	//下车人数
	public String XCRS;
	//中转人数
	public String ZZRS;
	//任务类型
	//上水-0，站台-1，候车厅-2，检票-3，出站口-4
	public int WorkType;
	//是否吸污完成
	public int Garbage;

	public String GarbageStaffName;

	//planName
	public String ScheName;
	public ArrayList<String> AttachList; // 附件

	public static ArrayList<TaskChild> ParseFromString(String result) {
		ArrayList<TaskChild> list = new ArrayList<TaskChild>();
		
		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TaskChild task = new TaskChild();

			JSONArray jsonArraytask = JsonUtil.GetJsonObjJsonArrayValue(
					jsonObj, "TaskInfo");
			if (jsonArraytask != null && jsonArraytask.length() != 0) {
				JSONObject jsonObjtask = (JSONObject) jsonArraytask.opt(0);
				task.SaId = JsonUtil.GetJsonObjLongValue(jsonObjtask, "SaId");
				task.TaskId = JsonUtil.GetJsonObjLongValue(jsonObjtask,
						"TaskId");
				task.DeptId = JsonUtil.GetJsonObjLongValue(jsonObjtask,
						"DeptId");
				task.DeptName = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"DeptName");
				task.StaffId = JsonUtil.GetJsonObjLongValue(jsonObjtask,
						"StaffId");
				task.StaffName = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"StaffName");
				task.BeginWorkTime = JsonUtil.GetJsonObjStringValue(
						jsonObjtask, "BeginWorkTime");
				task.EndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"EndWorkTime");
				task.RTeamId = JsonUtil.GetJsonObjLongValue(jsonObjtask,
						"RTeamId");
				task.RTeamName = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"RTeamName");
				task.RStaffIds = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"RStaffIds");
				task.RStaffNames = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"RStaffNames");
				task.RBeginWorkTime = JsonUtil.GetJsonObjStringValue(
						jsonObjtask, "RBeginWorkTime");
				task.REndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"REndWorkTime");
				task.TwId = JsonUtil.GetJsonObjLongValue(jsonObjtask, "TwId");
				task.Workspace = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"Workspace");
				task.RWorkspaces = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"RWorkspace");
				task.PId = JsonUtil.GetJsonObjLongValue(jsonObjtask, "PId");
				task.PositionName = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"PositionName");
				task.Conflict = JsonUtil.GetJsonObjBooleanValue(jsonObjtask,
						"Conflict");
				task.TaskRemark = StringUtil.Encode(JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"TaskRemark"), false);
				task.IsAccepted = JsonUtil.GetJsonObjBooleanValue(jsonObjtask,
						"IsAccepted");
				task.AcceptDttm = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"AcceptDttm");
				task.AExcStat = JsonUtil.GetJsonObjIntValue(jsonObjtask,
						"AExcStat");
				task.PlanDate = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"PlanDate");
				task.TaskName = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"TaskName");
				task.TRNO_PRO = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"TRNO_PRO");
				task.tBeginWorkTime = JsonUtil.GetJsonObjStringValue(
						jsonObjtask, "tBeginWorkTime");
				task.tEndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"tEndWorkTime");
				task.tRBeginWorkTime = JsonUtil.GetJsonObjStringValue(
						jsonObjtask, "tRBeginWorkTime");
				task.tREndWorkTime = JsonUtil.GetJsonObjStringValue(
						jsonObjtask, "tREndWorkTime");
				task.TaskMethod = JsonUtil.GetJsonObjIntValue(jsonObjtask,
						"TaskMethod");
				task.TaskLevel = JsonUtil.GetJsonObjIntValue(jsonObjtask,
						"TaskLevel");
				task.ExcSta = JsonUtil
						.GetJsonObjIntValue(jsonObjtask, "ExcSta");
				task.TaskSta = JsonUtil.GetJsonObjIntValue(jsonObjtask,
						"TaskSta");
				task.CreateMethod = JsonUtil.GetJsonObjIntValue(jsonObjtask,
						"CreateMethod");
				task.AcceptStat = JsonUtil.GetJsonObjIntValue(jsonObjtask,
						"AcceptStat");
				task.IsDraft = JsonUtil.GetJsonObjBooleanValue(jsonObjtask,
						"IsDraft");
				task.IsMajor = JsonUtil.GetJsonObjBooleanValue(jsonObjtask,
						"IsMajor");
				task.ChargeStaffId = JsonUtil.GetJsonObjLongValue(jsonObjtask,
						"ChargeStaffId");
				task.ChargeStaffName = JsonUtil.GetJsonObjStringValue(
						jsonObjtask, "ChargeStaffName");
				task.StationCode = JsonUtil.GetJsonObjStringValue(jsonObjtask,
						"StationCode");
				task.ScheName = JsonUtil.GetJsonObjStringValue(jsonObjtask, "ScheName");
				//TODO 上车人数
				task.SCRS = JsonUtil.GetJsonObjStringValue(jsonObjtask, "SCRS");
				task.XCRS = JsonUtil.GetJsonObjStringValue(jsonObjtask, "XCRS");
				task.ZZRS = JsonUtil.GetJsonObjStringValue(jsonObjtask, "ZZRS");
				task.WorkType = JsonUtil.GetJsonObjIntValue(jsonObjtask,"WorkType");
				task.Garbage=JsonUtil.GetJsonObjIntValue(jsonObjtask,"Garbage");
				task.GarbageStaffName=JsonUtil.GetJsonObjStringValue(jsonObjtask, "GarbageStaffName");

			}

			JSONArray jsonArrayattach = JsonUtil.GetJsonObjJsonArrayValue(
					jsonObj, "AttachList");
			if (jsonArrayattach != null && jsonArrayattach.length() != 0) {
				task.AttachList = new ArrayList<String>();
				for (int j = 0; j < jsonArrayattach.length(); j++) {
					JSONObject jsonObjattach = (JSONObject) jsonArrayattach
							.opt(j);
					task.AttachList.add(JsonUtil.GetJsonObjStringValue(
							jsonObjattach, "FilePath"));
				}
			}

			list.add(task);
		}

		return list;
	}
}
