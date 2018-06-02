package easyway.Mobile.Data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 员工、部门对应关系
public class StaffDeptRS {
	public long Id;
	public long StaffId;
	public long DeptId;
	
	public static ArrayList<StaffDeptRS> ParseFromString(String result) {
		ArrayList<StaffDeptRS> list = new ArrayList<StaffDeptRS>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			StaffDeptRS obj = new StaffDeptRS();
			
			obj.Id = JsonUtil.GetJsonObjLongValue(jsonObj, "Id");
			obj.StaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "StaffId");
			obj.DeptId = JsonUtil.GetJsonObjLongValue(jsonObj, "DeptId");
		
			
			list.add(obj);
		}

		return list;
	}
}
