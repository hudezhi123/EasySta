package easyway.Mobile.Data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 工作岗位
public class TaskPosition {
	public long PId;
	public String PositionName;
	public long AdvanceMin;
	public long DelayMin;
	public String PositionCode;
	public String StationCode;
	
	public static ArrayList<TaskPosition> ParseFromString(String result) {
		ArrayList<TaskPosition> list = new ArrayList<TaskPosition>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TaskPosition positon = new TaskPosition();
			
			positon.PId = JsonUtil.GetJsonObjLongValue(jsonObj, "PId");
			positon.PositionName = JsonUtil.GetJsonObjStringValue(jsonObj, "PositionName");
			positon.AdvanceMin = JsonUtil.GetJsonObjLongValue(jsonObj, "AdvanceMin");
			positon.DelayMin = JsonUtil.GetJsonObjLongValue(jsonObj, "DelayMin");
			positon.PositionCode = JsonUtil.GetJsonObjStringValue(jsonObj, "PositionCode");
			positon.StationCode = JsonUtil.GetJsonObjStringValue(jsonObj, "StationCode");
		
			list.add(positon);
		}

		return list;
	}
}
