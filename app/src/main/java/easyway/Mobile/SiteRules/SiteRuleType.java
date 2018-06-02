package easyway.Mobile.SiteRules;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

/*
 * 	站内规章 - 类型
 */
public class SiteRuleType {
	public String id = "";
	public String text = "";
	public boolean check = false;
	

	// 解析字符串
	public static ArrayList<SiteRuleType> ParseFromString(String result) {
		ArrayList<SiteRuleType> list = new ArrayList<SiteRuleType>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);

			JSONObject Obj = (JSONObject) JsonUtil.GetJsonObjJsonObjValue(
					jsonObj, "Configuration");

			SiteRuleType type = new SiteRuleType();
			type.id = JsonUtil.GetJsonObjStringValue(Obj, "@id");
			type.text = JsonUtil.GetJsonObjStringValue(Obj, "@text");

			list.add(type);
		}

		return list;
	}
}
