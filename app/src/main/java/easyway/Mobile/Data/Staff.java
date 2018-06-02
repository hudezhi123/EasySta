package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

import easyway.Mobile.util.DBHelper;
import easyway.Mobile.util.JsonUtil;

// 人员信息
public class Staff implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public long StaffId;
	public String StaffCode;
	public String StaffName;
//	public long Age;
//	public String Sex;
//	public String NativePlace;
//	public String Nation;
//	public String Nationality;
//	public String Birthday;
//	public String Email;
	public String Mobile;
//	public String ShortNumber;
//	public String Telephone;
//	public String IDCard;
//	public long JobStatus;
//	public String Title;
//	public String Duty;
//	public String OfficePhone;
//	public String OfficeFax;
//	public String OfficeZipCode;
//	public String OfficeAddress;
//	public String HomePhone;
//	public String HomeFax;
//	public String HomeZipCode;
	public String HomeAddress;
	public String Expend1;
//	public String Expend2;
//	public String Expend3;
//	public String Remarks;
//	public String Creater;
//	public String CreateTime;
//	public String Modifier;
//	public String ModifyTime;
//	public long Validity;

	public long Type;
	public long Owner;
	public boolean BOnLine = false;

	public final static int TYPE_STAFF = 0; // 正常角色、组织
	public final static int TYPE_GROUP = 1; // 群呼分组
	public final static int OWNER_ALL = 0; // 所有者为全部人员

	public static ArrayList<Staff> ParseFromString(String result) {
		ArrayList<Staff> list = new ArrayList<Staff>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			Staff staff = new Staff();
			
			staff.StaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "StaffId");
			 staff.StaffCode = JsonUtil.GetJsonObjStringValue(jsonObj, "StaffCode");
			 staff.StaffName = JsonUtil.GetJsonObjStringValue(jsonObj, "StaffName");
//			 staff.Age = JsonUtil.GetJsonObjLongValue(jsonObj, "Age");
//			 staff.Sex = JsonUtil.GetJsonObjStringValue(jsonObj, "Sex");
//			 staff.NativePlace = JsonUtil.GetJsonObjStringValue(jsonObj, "NativePlace");
//			 staff.Nation = JsonUtil.GetJsonObjStringValue(jsonObj, "Nation");
//			 staff.Nationality = JsonUtil.GetJsonObjStringValue(jsonObj, "Nationality");
//			 staff.Birthday = JsonUtil.GetJsonObjStringValue(jsonObj, "Birthday");
//			 staff.Email = JsonUtil.GetJsonObjStringValue(jsonObj, "Email");
			 staff.Mobile = JsonUtil.GetJsonObjStringValue(jsonObj, "Mobile");
//			 staff.ShortNumber = JsonUtil.GetJsonObjStringValue(jsonObj, "ShortNumber");
//			 staff.Telephone = JsonUtil.GetJsonObjStringValue(jsonObj, "Telephone");
//			 staff.IDCard = JsonUtil.GetJsonObjStringValue(jsonObj, "IDCard");
//			 staff.JobStatus = JsonUtil.GetJsonObjLongValue(jsonObj, "JobStatus");
//			 staff.Title = JsonUtil.GetJsonObjStringValue(jsonObj, "Title");
//			 staff.Duty = JsonUtil.GetJsonObjStringValue(jsonObj, "Duty");
//			 staff.OfficePhone = JsonUtil.GetJsonObjStringValue(jsonObj, "OfficePhone");
//			 staff.OfficeFax = JsonUtil.GetJsonObjStringValue(jsonObj, "OfficeFax");
//			 staff.OfficeZipCode = JsonUtil.GetJsonObjStringValue(jsonObj, "OfficeZipCode");
//			 staff.OfficeAddress = JsonUtil.GetJsonObjStringValue(jsonObj, "OfficeAddress");
//			 staff.HomePhone = JsonUtil.GetJsonObjStringValue(jsonObj, "HomePhone");
//			 staff.HomeFax = JsonUtil.GetJsonObjStringValue(jsonObj, "HomeFax");
//			 staff.HomeZipCode = JsonUtil.GetJsonObjStringValue(jsonObj, "HomeZipCode");
			 staff.HomeAddress = JsonUtil.GetJsonObjStringValue(jsonObj, "HomeAddress");
			 staff.Expend1 = JsonUtil.GetJsonObjStringValue(jsonObj, "Expend1");
//			 staff.Expend2 = JsonUtil.GetJsonObjStringValue(jsonObj, "Expend2");
//			 staff.Expend3 = JsonUtil.GetJsonObjStringValue(jsonObj, "Expend3");
//			 staff.Remarks = JsonUtil.GetJsonObjStringValue(jsonObj, "Remarks");
//			 staff.Creater = JsonUtil.GetJsonObjStringValue(jsonObj, "Creater");
//			 staff.CreateTime = JsonUtil.GetJsonObjStringValue(jsonObj, "CreateTime");
//			 staff.Modifier = JsonUtil.GetJsonObjStringValue(jsonObj, "Modifier");
//			 staff.ModifyTime = JsonUtil.GetJsonObjStringValue(jsonObj, "ModifyTime");
//			 staff.Validity = JsonUtil.GetJsonObjLongValue(jsonObj, "Validity");
			
			list.add(staff);
		}

		return list;
	}
	
	// 根据StaffId 获取 Expend1(VOIP ID)
	public static String GetExpend1ByStaffId(Context ctx, long staffId) {
		String Expend1 = "";
		DBHelper dbHelper = new DBHelper(ctx);
		Cursor cursor = null;
		
		try {
			String[] columns = {DBHelper.STAFF_EXPEND1};
			cursor = dbHelper.exeSql(DBHelper.STAFF_TABLE_NAME, columns,
					DBHelper.STAFF_ID + "='"+ staffId + "'", null, null, null, null);
			
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						Expend1 = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_EXPEND1));
					} catch (Exception ex) {
						continue;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}
		
		return Expend1;
	}
}
