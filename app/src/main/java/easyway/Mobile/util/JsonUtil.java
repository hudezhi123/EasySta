package easyway.Mobile.util;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtil {
	public static JSONArray GetJsonArray(String result, String name) {
		try {
			JSONTokener jsonParser = new JSONTokener(result);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			JSONArray jsonArray = jsonObject.getJSONArray(name);

			return jsonArray;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static JSONArray GetJsonArray(String result) {
		try {
			JSONArray jsonArray = new JSONArray(result);
			return jsonArray;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static JSONObject GetJsonObj(String result, String name) {
		try {
			JSONTokener jsonParser = new JSONTokener(result);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			JSONObject obj = jsonObject.getJSONObject(name);
			
			return obj;
		} catch (Exception ex) {
			return null;
		}
	}

	public static String GetJsonString(String result, String name) {
		try {
			String retValue = "";
			JSONTokener jsonParser = new JSONTokener(result);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			if (jsonObject.has(name)) {
				retValue = CommonFunc.GetCSharpString(jsonObject
						.getString(name));
			}

			return retValue;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static long GetJsonLong(String result, String name) {
		try {
			long retValue = 0;
			JSONTokener jsonParser = new JSONTokener(result);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			if (jsonObject.has(name)) {
				retValue = jsonObject.getLong(name);
			}

			return retValue;
		} catch (Exception ex) {
			return 0;
		}
	}
	
	public static Boolean GetJsonBoolean(String result, String name) {
		try {
			boolean retValue = false;
			JSONTokener jsonParser = new JSONTokener(result);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			if (jsonObject.has(name)) {
				retValue = jsonObject.getBoolean(name);
			}

			return retValue;
		} catch (Exception ex) {
			return false;
		}
	}

	public static int GetJsonInt(String result, String name) {
		try {
			int retValue = 0;
			JSONTokener jsonParser = new JSONTokener(result);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			if (jsonObject.has(name)) {
				retValue = Integer.valueOf(jsonObject.getString(name));
			}

			return retValue;
		} catch (Exception ex) {
			return 0;
		}
	}
	
	public static String GetJsonObjStringValue(JSONObject jsonObj, String name) {
		if (!jsonObj.has(name)) {
			return "";
		} else {
			try {
				return CommonFunc.GetCSharpString(jsonObj.getString(name))
						.trim();
			} catch (Exception ex) {
				return "";
			}
		}
	}
	
	public static JSONObject GetJsonObjJsonObjValue(JSONObject jsonObj, String name) {
		if (!jsonObj.has(name)) {
			return null;
		} else {
			try {
				return jsonObj.getJSONObject(name);
			} catch (Exception ex) {
				return null;
			}
		}
	}

	public static Date GetJsonObjDateValue(JSONObject jsonObj, String name) {
		if (!jsonObj.has(name)) {
			return null;
		} else {
			try {
				return CommonUtils.GetCSharpDate(jsonObj.getString(name));
			} catch (Exception ex) {
				return null;
			}
		}
	}
	
	public static long GetJsonObjLongValue(JSONObject jsonObj, String name) {
		if (!jsonObj.has(name)) {
			return -1;
		} else {
			try {
				return jsonObj.getLong(name);
			} catch (Exception ex) {
				return -1;
			}
		}
	}

	public static Boolean GetJsonObjBooleanValue(JSONObject jsonObj, String name) {
		if (!jsonObj.has(name)) {
			return false;
		} else {
			try {
				return jsonObj.getBoolean(name);
			} catch (Exception ex) {
				return false;
			}
		}
	}

	public static int GetJsonObjIntValue(JSONObject jsonObj, String name) {
		if (!jsonObj.has(name)) {
			return -1;
		} else {
			try {
				return jsonObj.getInt(name);
			} catch (Exception ex) {
				return -1;
			}
		}
	}
	
	public static JSONArray GetJsonObjJsonArrayValue(JSONObject jsonObj, String name) {
		if (!jsonObj.has(name)) {
			return null;
		} else {
			try {
				return jsonObj.getJSONArray(name);
			} catch (Exception ex) {
				return null;
			}
		}
	}
}
