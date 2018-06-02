package easyway.Mobile.Data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 岗位任务区域对照表
public class TaskPWRS {
	public long PwId;
	public long TwId;		// 任务区域ID
	public long PId;		// 岗位ID
	public long WpType;
	
	public static ArrayList<TaskPWRS> ParseFromString(String result) {
		ArrayList<TaskPWRS> list = new ArrayList<TaskPWRS>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TaskPWRS positon = new TaskPWRS();
			
			positon.TwId = JsonUtil.GetJsonObjLongValue(jsonObj, "TwId");
			positon.PwId = JsonUtil.GetJsonObjLongValue(jsonObj, "PwId");
			positon.PId = JsonUtil.GetJsonObjLongValue(jsonObj, "PId");
			positon.WpType = JsonUtil.GetJsonObjLongValue(jsonObj, "WpType");
			
			list.add(positon);
		}

		return list;
	}
}
