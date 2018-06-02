package easyway.Mobile.Data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 任务区域
public class TaskWorkspace {
	public long TwId;
	public String Workspace;
	public String StationCode;
	
	public static ArrayList<TaskWorkspace> ParseFromString(String result) {
		ArrayList<TaskWorkspace> list = new ArrayList<TaskWorkspace>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TaskWorkspace positon = new TaskWorkspace();
			
			positon.TwId = JsonUtil.GetJsonObjLongValue(jsonObj, "TwId");
			positon.Workspace = JsonUtil.GetJsonObjStringValue(jsonObj, "Workspace");
			positon.StationCode = JsonUtil.GetJsonObjStringValue(jsonObj, "StationCode");
		
			list.add(positon);
		}

		return list;
	}
}
