package easyway.Mobile.Data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import easyway.Mobile.util.DBHelper;

public class LampArea {
	public int Id; // 区域标识
	public String AreaName; // 所属区域

	// 获取所属区域列表
	public static ArrayList<LampArea> GetAllLampArea(Context ctx) {
		ArrayList<LampArea> list = new ArrayList<LampArea>();
		DBHelper dbHelper = new DBHelper(ctx);
		Cursor cursor = null;
		try {
			String[] columns = { DBHelper.LCTRLCATE_ID,  DBHelper.LCTRLCATE_AREANAME};
			cursor = dbHelper.exeSql(DBHelper.LCTRLCATE_TABLE_NAME, columns, null, null, null, null, null);

			/*
			String sql_query = "SELECT * FROM "
					+ DBHelper.LCTRLCATE_TABLE_NAME;
			cursor = dbHelper.getReadableDatabase().rawQuery(sql_query, null);
			*/
			if (0 == cursor.getCount()) {
				cursor.close();
			}
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						LampArea cate = new LampArea();
						cate.Id = cursor.getInt(cursor.getColumnIndex(DBHelper.LCTRLCATE_ID));
						cate.AreaName = cursor.getString(cursor.getColumnIndex(DBHelper.LCTRLCATE_AREANAME));
						list.add(cate);
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
