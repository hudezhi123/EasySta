package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 请假申请
public class Vacation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String KEY_VACATION = "key_vacation";
	
	public long Id;		// ID
	public long UserId;		// 申请人ID
	public String UserName;		// 申请人姓名
	public long DeptId;	// 部门名
	public String DeptName;	// 部门名
	public String Type;		// 假别
	public int TypeId;		// 假别
	public String StartTime;		// 开始时间
	public String EndTime;		// 结束时间
	public int Days;	// 天数
	public int Hours;	// 小时数
	public String Remark;		// 备注
	public String StatusName;	// 状态
	public int Status;
	public String CreateTime; // 申请时间
	public String Writer;		// 填单人
	public String StationCode;
	
	public static ArrayList<Vacation> ParseFromString(String result) {
		ArrayList<Vacation> list = new ArrayList<Vacation>();
		
		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			Vacation item = new Vacation();
			item.Id = JsonUtil.GetJsonObjLongValue(jsonObj, "Id");
			item.UserId = JsonUtil.GetJsonObjLongValue(jsonObj, "ApplyStaffId");
			item.UserName = JsonUtil.GetJsonObjStringValue(jsonObj, "ApplyStaffName");
			item.DeptId = JsonUtil.GetJsonObjLongValue(jsonObj, "DeptId");
			item.DeptName = JsonUtil.GetJsonObjStringValue(jsonObj, "DeptName");
			item.Type = JsonUtil.GetJsonObjStringValue(jsonObj, "VacationType");
			item.TypeId = JsonUtil.GetJsonObjIntValue(jsonObj, "LeaveTypeId");
			item.StartTime = JsonUtil.GetJsonObjStringValue(jsonObj, "BeginTime");
			item.EndTime = JsonUtil.GetJsonObjStringValue(jsonObj, "EndTime");
			item.Days = JsonUtil.GetJsonObjIntValue(jsonObj, "Days");
			item.Hours = JsonUtil.GetJsonObjIntValue(jsonObj, "Hours");
			item.Remark = JsonUtil.GetJsonObjStringValue(jsonObj, "Remark");
			item.Status = JsonUtil.GetJsonObjIntValue(jsonObj, "LastStatus");
			item.StatusName = JsonUtil.GetJsonObjStringValue(jsonObj, "StatusName");
			item.CreateTime = JsonUtil.GetJsonObjStringValue(jsonObj, "CreateTime");
			item.StationCode = JsonUtil.GetJsonObjStringValue(jsonObj, "StationCode");
			
			list.add(item);
		}

		return list;
	}
}
