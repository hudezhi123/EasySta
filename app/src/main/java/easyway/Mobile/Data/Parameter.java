package easyway.Mobile.Data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import easyway.Mobile.util.DBHelper;

/*
 * 参数字典
 */
public class Parameter {
	public String value;
	public String name;
	public String code;

	public static final String PARAM_CODE_REPORT = "ReportType";						// 情况上报类型
	public static final String PARAM_CODE_GRAVE = "graveLevel";						// 情况上报级别
	public static final String PARAM_CODE_TICKET = "b65b8f41da1e4ccca637325b919e8fad";			// 检票状态
	public static final String PARAM_CODE_TASK = "TaskName";							// 重点任务类型
	public static final String PARAM_CODE_LEAVETYPE = "LeaveType";							// 请假类型
	public static final String PARAM_CODE_LEAVESTATUS = "LeaveStatus";					 	// 请假状态
	
	public Parameter() {
		value = "";
		name = "";
		code = "";
	}
	
	public Parameter(String value, String name, String code) {
		this.value = value;
		this.name = name;
		this.code = code;
	}
	
	// 根据参数类型获取参数列表
	public static ArrayList<Parameter> GetParamByCode(String paramCode, Context ctx) {
		ArrayList<Parameter> list = new ArrayList<Parameter>();
		DBHelper dbHelper = new DBHelper(ctx);
		Cursor cursor = null;
		
		try {
			String[] columns = {DBHelper.PARAM_CODE, DBHelper.PARAM_VALUE, DBHelper.PARAM_NAME};
			cursor = dbHelper.exeSql(DBHelper.PARAM_TABLE_NAME, columns,
					DBHelper.PARAM_CODE + "='"+ paramCode + "'", null, null, null, null);
			if (0 == cursor.getCount()) {
				cursor.close();
			}
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						Parameter param = new Parameter();
						param.name = cursor.getString(cursor
								.getColumnIndex(DBHelper.PARAM_NAME));
						param.value = cursor.getString(cursor
								.getColumnIndex(DBHelper.PARAM_VALUE));
						param.code = cursor.getString(cursor
								.getColumnIndex(DBHelper.PARAM_CODE));
						list.add(param);
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
}
