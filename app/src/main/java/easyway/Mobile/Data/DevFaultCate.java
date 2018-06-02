package easyway.Mobile.Data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import easyway.Mobile.util.DBHelper;

/*
 * 参数字典
 */
public class DevFaultCate {
	public String id;
	public String key;	
	public String value;
	public String name;
	public String code;

	public static final String PARAM_CODE_DESC = "DevFaultCate";						// 设备故障描述
	
	public DevFaultCate() {
		id = "";
		key = "";
		value = "";
		name = "";
		code = "";
	}
	
	// 根据参数类型获取参数列表
	public static ArrayList<DevFaultCate> GetDevFaultCateByCode(String cateCode, Context ctx) {
		ArrayList<DevFaultCate> list = new ArrayList<DevFaultCate>();
		DBHelper dbHelper = new DBHelper(ctx);
		Cursor cursor = null;		
		try {
			String[] columns = {DBHelper.DEVFAULTCATE_CONFIGID, DBHelper.DEVFAULTCATE_CONFIGKEY, 
					DBHelper.DEVFAULTCATE_CONFIGNAME, DBHelper.DEVFAULTCATE_CONFIGVALUE, DBHelper.DEVFAULTCATE_CODE};
			cursor = dbHelper.exeSql(DBHelper.DEVFAULTCATE_TABLE_NAME, columns,
					DBHelper.DEVFAULTCATE_CODE + "='"+ cateCode + "'", null, null, null, null);
			
			if (0 == cursor.getCount()) {
				cursor.close();
			}
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						DevFaultCate devCate = new DevFaultCate();
						devCate.id = cursor.getString(cursor
								.getColumnIndex(DBHelper.DEVFAULTCATE_CONFIGID));
						devCate.key = cursor.getString(cursor
								.getColumnIndex(DBHelper.DEVFAULTCATE_CONFIGKEY));
						devCate.name = cursor.getString(cursor
								.getColumnIndex(DBHelper.DEVFAULTCATE_CONFIGNAME));	
						devCate.value = cursor.getString(cursor
								.getColumnIndex(DBHelper.DEVFAULTCATE_CONFIGVALUE));
						devCate.code = cursor.getString(cursor
								.getColumnIndex(DBHelper.DEVFAULTCATE_CODE));						
						list.add(devCate);
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
		
		return list;
	}

	@Override
	public String toString() {
		return "DevFaultCate{" +
				"id='" + id + '\'' +
				", key='" + key + '\'' +
				", value='" + value + '\'' +
				", name='" + name + '\'' +
				", code='" + code + '\'' +
				'}';
	}
}
