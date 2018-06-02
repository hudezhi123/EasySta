package easyway.Mobile.Data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 现场监控 - 任务区域
public class TaskSMWC {
	public long MId;
	public String MName;
	public long MOrder;
	public long MDisplayStyle;
	public String StationCode;
	
	public static ArrayList<TaskSMWC> ParseFromString(String result) {
		ArrayList<TaskSMWC> list = new ArrayList<TaskSMWC>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TaskSMWC obj = new TaskSMWC();
			
			obj.MId = JsonUtil.GetJsonObjLongValue(jsonObj, "MId");
			obj.MName = JsonUtil.GetJsonObjStringValue(jsonObj, "MName");
			obj.MOrder = JsonUtil.GetJsonObjLongValue(jsonObj, "MOrder");
			obj.MDisplayStyle = JsonUtil.GetJsonObjLongValue(jsonObj, "MDisplayStyle");
			
			list.add(obj);
		}

		return list;
	}
}
