package easyway.Mobile.LightingControl;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.content.Context;
import android.util.Log;

public class LightingControl {
	public int Id; // 回路标识
	public String Circuit; // 回路名称
	public String AreaName; // 所属区域
	public int CurrentVal; // 当前值
	public int DefaultVal; // 默认值

	// 每个的回路返回的具体信息

	public int id; // 灯编号
	public int OrderNum; // 灯排序
	public String PointName; // 灯的名称
	public int DefaultValue; // 灯的开关值

	public static final String LCTRL_AREANAME = "AreaName"; // 所属区域

	public static ArrayList<LightingControl> ParseFromString(String result) {
		ArrayList<LightingControl> list = new ArrayList<LightingControl>();

		if (result == null)
			return list;
		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			LightingControl lc = new LightingControl();

			lc.Id = JsonUtil.GetJsonObjIntValue(jsonObj, "Id");
			lc.Circuit = JsonUtil.GetJsonObjStringValue(jsonObj, "Circuit");
			lc.AreaName = JsonUtil.GetJsonObjStringValue(jsonObj, "AreaName");
			lc.CurrentVal = JsonUtil.GetJsonObjIntValue(jsonObj, "CurrentVal");
			lc.DefaultVal = JsonUtil.GetJsonObjIntValue(jsonObj, "DefaultVal");
			list.add(lc);
		}

		return list;
	}

	// 设置回路状态
	public static Boolean SetLampStatus(Context ctx, String lampId, String val) {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("lampId", lampId);
		parmValues.put("setVal", val);

		String methodPath = Constant.MP_SERVICE;
		String methodName = Constant.MN_SET_LAMPSTATUS;
		WebServiceManager webServiceManager = new WebServiceManager(ctx,
				methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return false;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			String msg = JsonUtil.GetJsonString(result, "Msg");

			if (msg.equals(R.string.Lighting_Control_FAIL) || msg.length() == 0)
				return false;

			break;
		case Constant.EXCEPTION:
		default:
			return false;
		}

		return true;
	}

	// 获取所有回路信息
	public static ArrayList<LightingControl> GetLampStatus(Context ctx,
			String areaId) {
		ArrayList<LightingControl> list = new ArrayList<LightingControl>();

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId",  Property.SessionId);
		
		parmValues.put("areaId", areaId);

		String methodPath = Constant.MP_SERVICE;
		String methodName = Constant.MN_GET_LAMPSTATUS;
		WebServiceManager webServiceManager = new WebServiceManager(ctx,
				methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return null;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			if (jsonArray == null || jsonArray.length() == 0)
				return list;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				LightingControl lc = new LightingControl();

				lc.Id = JsonUtil.GetJsonObjIntValue(jsonObj, "Id");
				lc.Circuit = JsonUtil.GetJsonObjStringValue(jsonObj, "Circuit");
				lc.AreaName = JsonUtil.GetJsonObjStringValue(jsonObj,
						"AreaName");
				lc.CurrentVal = JsonUtil.GetJsonObjIntValue(jsonObj,
						"CurrentVal");
				lc.DefaultVal = JsonUtil.GetJsonObjIntValue(jsonObj,
						"DefaultVal");
				list.add(lc);
			}
			break;
		case Constant.EXCEPTION:
		default:
			return null;
		}

		return list;
	}

	// 获取每个回路的具体信息
	public static ArrayList<LightingControl> GetLampPoints(Context ctx,
			String lampId) {
		ArrayList<LightingControl> list = new ArrayList<LightingControl>();

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("lampId", lampId);

		String methodPath = Constant.MP_SERVICE;
		String methodName = Constant.MN_GET_LAMPPOINTS;
		WebServiceManager webServiceManager = new WebServiceManager(ctx,
				methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return null;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			if (jsonArray == null || jsonArray.length() == 0)
				return list;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				LightingControl lc = new LightingControl();
				lc.id = JsonUtil.GetJsonObjIntValue(jsonObj, "id");
				lc.OrderNum = JsonUtil.GetJsonObjIntValue(jsonObj, "OrderNum");
				lc.PointName = JsonUtil.GetJsonObjStringValue(jsonObj,
						"PointName");
				Log.e("sdvsdv", lc.PointName);
				lc.DefaultValue = JsonUtil.GetJsonObjIntValue(jsonObj,
						"DefaultValue");
				list.add(lc);
			}
			break;
		case Constant.EXCEPTION:
		default:
			return null;
		}

		return list;
	}

	// 设置每个回路的具体信息
	public static Boolean SetLampPoints(Context ctx, String valueType,
			String pointsValue) {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("valueType", valueType);
		parmValues.put("pointsValue", pointsValue);
		String methodPath = Constant.MP_SERVICE;
		String methodName = Constant.MN_SET_LAMPPOINTS;
		WebServiceManager webServiceManager = new WebServiceManager(ctx,
				methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return false;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			String msg = JsonUtil.GetJsonString(result, "Msg");

			if (msg.equals(R.string.Lighting_Control_FAIL) || msg.length() == 0)
				return false;

			break;
		case Constant.EXCEPTION:
		default:
			return false;
		}

		return true;

	}

}
