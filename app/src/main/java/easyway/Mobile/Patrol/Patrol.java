package easyway.Mobile.Patrol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.R;
import easyway.Mobile.util.JsonUtil;

public class Patrol {
	public String TimeArea; // 时间范围
	public int ScheStat; // 状态 -2：过期；-1:未到开始时间；0:允许客运巡检但未开始巡检；1:部分完成；2:完成。
	public ArrayList<PatrolW> SchList;

	public static final int SCHESTAT_OUTTIME = -2; // 过期
	public static final int SCHESTAT_NOSTARTTIME = -1; // 未到开始时间
	public static final int SCHESTAT_NOSTART = 0; // 允许客运巡检但未开始巡检
	public static final int SCHESTAT_PERCENTCOMPLETE = 1; // 部分完成
	public static final int SCHESTAT_COMPLETE = 2; // 完成

	// 获取状态对应信息
	public static int getScheRes(int stat) {
		int resId = 0;
		switch (stat) {
		case SCHESTAT_OUTTIME:
			resId = R.string.Patrol_Sche_outtime;
			break;
		case SCHESTAT_NOSTARTTIME:
			resId = R.string.Patrol_Sche_nostarttime;
			break;
		case SCHESTAT_NOSTART:
			resId = R.string.Patrol_Sche_nostart;
			break;
		case SCHESTAT_PERCENTCOMPLETE:
			resId = R.string.Patrol_Sche_percentcomplete;
			break;
		case SCHESTAT_COMPLETE:
			resId = R.string.Patrol_Sche_complete;
			break;
		default:
			resId = R.string.Patrol_Sche_complete;
			break;
		}
		
		return resId;
	}

	public static ArrayList<Patrol> ParseFromString(String result) {
		ArrayList<Patrol> list = new ArrayList<Patrol>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			Patrol patrol = new Patrol();

			patrol.ScheStat = JsonUtil.GetJsonObjIntValue(jsonObj, "ScheStat");
			patrol.TimeArea = JsonUtil.GetJsonObjStringValue(jsonObj,
					"TimeArea");
			JSONArray jsonArrayW = JsonUtil.GetJsonObjJsonArrayValue(jsonObj,
					"ScheduleRecordList");

			ArrayList<PatrolW> listw = new ArrayList<PatrolW>();
			if (jsonArrayW == null || jsonArrayW.length() == 0) {
				// do nothing
			} else {
				for (int j = 0; j < jsonArrayW.length(); j++) {
					JSONObject jsonObjW = (JSONObject) jsonArrayW.opt(j);
					PatrolW patrolW = new PatrolW();
					patrolW.Workspace = JsonUtil.GetJsonObjStringValue(
							jsonObjW, "Workspace");
					patrolW.IsPatrol = JsonUtil.GetJsonObjIntValue(jsonObjW,
							"IsPatrol");
					listw.add(patrolW);
				}
			}
			patrol.SchList = listw;
			list.add(patrol);
		}

		return list;
	}


}
