package easyway.Mobile.Watering;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import easyway.Mobile.util.JsonUtil;

public class Watering {
	public String TRNO_PRO; // 车次
	public String PlanDate; // 计划日期
	public String Workspace; // 股道
	public long TwId;		// 任务区域ID
	public long SaId;	// 任务ID
	public long PId;	// 岗位ID
	public String PositionName;	// 岗位
	public String TaskRemark;	// 注释
	public String BeginWorkTime; // 开始时间
	public String EndWorkTime; // 结束时间
	public int AppStatus; // 状态
	public String AppDttm; // 申请时间
	public String AuditDttm; // 审核时间
	public String AuditStaffName; // 审核人
	public String CompleteDttm; // 完成时间

	public static final int STATUS_NOT_REPUEST = 0; // 未申请
	public static final int STATUS_APPROVALING = 1; // 等待审核（审核中）
	public static final int STATUS_APPROVAL_REJECT = 2; // 审核驳回
	public static final int STATUS_APPROVAL_PASS = 3; // 审核通过
	public static final int STATUS_COMPLETED = 4; // 完成上水

	public static final int FLAG_NOTCOMPLETE = -1;		// 未完成
	public static final int FLAG_COMPLETED = 1;		// 已完成
	public static final int FLAG_ALL = 0;		// 所有
	
	public static final int PROCESS_REQUEST = 0	;  // 申请上水
	public static final int PROCESS_COMPLETE = 4;	// 完成上水
	
	public static ArrayList<Watering> ParseFromString(String result) {
		ArrayList<Watering> list = new ArrayList<Watering>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			Watering water = new Watering();

			water.TRNO_PRO = JsonUtil
					.GetJsonObjStringValue(jsonObj, "TRNO_PRO");
			water.PlanDate = JsonUtil
					.GetJsonObjStringValue(jsonObj, "PlanDate");
			water.Workspace = JsonUtil.GetJsonObjStringValue(jsonObj,
					"Workspace");
			water.TwId = JsonUtil.GetJsonObjLongValue(jsonObj, "TwId");
			water.SaId = JsonUtil.GetJsonObjLongValue(jsonObj, "SaId");
			water.PId = JsonUtil.GetJsonObjLongValue(jsonObj, "PId");
			water.PositionName = JsonUtil.GetJsonObjStringValue(jsonObj,
					"PositionName");
			water.TaskRemark = JsonUtil.GetJsonObjStringValue(jsonObj,
					"TaskRemark");
			water.BeginWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj,
					"BeginWorkTime");
			water.EndWorkTime = JsonUtil.GetJsonObjStringValue(jsonObj,
					"EndWorkTime");
			water.AppStatus = JsonUtil.GetJsonObjIntValue(jsonObj, "AppStatus");
			water.AppDttm = JsonUtil.GetJsonObjStringValue(jsonObj, "AppDttm");
			water.AuditDttm = JsonUtil.GetJsonObjStringValue(jsonObj,
					"AuditDttm");
			water.AuditStaffName = JsonUtil.GetJsonObjStringValue(jsonObj,
					"AuditStaffName");
			water.CompleteDttm = JsonUtil.GetJsonObjStringValue(jsonObj,
					"CompleteDttm");

			list.add(water);
		}

		return list;
	}
}
