package easyway.Mobile.site_monitoring;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

public class SMWorkspace implements Serializable {
	private static final long serialVersionUID = -8417866343248765081L;
	
	public SMTask taskCurr = new SMTask();		// 当前任务
	public SMTask taskNext = new SMTask();		// 下一任务
	
	public static ArrayList<SMWorkspace> ParseFromString(String result) {
		ArrayList<SMWorkspace> list = new ArrayList<SMWorkspace>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			SMWorkspace obj = new SMWorkspace();
			
			// 当前任务
			obj.taskCurr.TwId = JsonUtil.GetJsonObjLongValue(jsonObj, "TwId");
			obj.taskCurr.Workspace = JsonUtil.GetJsonObjStringValue(jsonObj, "Workspace");
			obj.taskCurr.TaskId = JsonUtil.GetJsonObjLongValue(jsonObj, "TaskId1");
			obj.taskCurr.SaId = JsonUtil.GetJsonObjLongValue(jsonObj, "SaId1");
			obj.taskCurr.TrainNum = JsonUtil.GetJsonObjStringValue(jsonObj, "TrainNum1");
			obj.taskCurr.BeginWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj, "BeginWorkTime1");
			obj.taskCurr.EndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj, "EndWorkTime1");
			obj.taskCurr.DeptId = JsonUtil.GetJsonObjLongValue(jsonObj, "DeptId1");
			obj.taskCurr.DeptName = JsonUtil.GetJsonObjStringValue(jsonObj, "DeptName1");
			obj.taskCurr.StaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "StaffId1");
			obj.taskCurr.StaffName = JsonUtil.GetJsonObjStringValue(jsonObj, "StaffName1");
			obj.taskCurr.AExcStat = JsonUtil.GetJsonObjLongValue(jsonObj, "AExcStat1");
			
			// 下一任务
			obj.taskNext.TwId = JsonUtil.GetJsonObjLongValue(jsonObj, "TwId");
			obj.taskNext.Workspace = JsonUtil.GetJsonObjStringValue(jsonObj, "Workspace");
			obj.taskNext.TaskId = JsonUtil.GetJsonObjLongValue(jsonObj, "TaskId2");
			obj.taskNext.SaId = JsonUtil.GetJsonObjLongValue(jsonObj, "SaId2");
			obj.taskNext.TrainNum = JsonUtil.GetJsonObjStringValue(jsonObj, "TrainNum2");
			obj.taskNext.BeginWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj, "BeginWorkTime2");
			obj.taskNext.EndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj, "EndWorkTime2");
			obj.taskNext.StaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "StaffId2");
			obj.taskNext.StaffName = JsonUtil.GetJsonObjStringValue(jsonObj, "StaffName2");
			obj.taskNext.DeptId = JsonUtil.GetJsonObjLongValue(jsonObj, "DeptId2");
			obj.taskNext.DeptName = JsonUtil.GetJsonObjStringValue(jsonObj, "DeptName2");
			obj.taskNext.AExcStat = JsonUtil.GetJsonObjLongValue(jsonObj, "AExcStat2");
			list.add(obj);
		}

		return list;
	}
}
