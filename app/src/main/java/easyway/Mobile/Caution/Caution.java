package easyway.Mobile.Caution;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import easyway.Mobile.Property;
import easyway.Mobile.util.DBHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

// 提醒事项
public class Caution {
	public long ID;
	public String title = ""; // 标题
	public String content = ""; // 内容
	public String attachaudio = ""; // 语音附件
	public String attachphoto = ""; // 照片附件
	public String date; // 日期 yyyy-MM-dd
	public String time; // 时间 HH:mm:ss
	public int level; // 级别
	public int valid; // 是否已启用
	public boolean isplay = false;

	public static final int VALID_ON = 1; // 启用
	public static final int VALID_OFF = 2; // 关闭
	public static final int VALID_DRAFT = 3; // 草稿

	public static final int LEVEL_HIGH = 1; // 高
	public static final int LEVEL_NORMAL = 2; // 中
	public static final int LEVEL_LOW = 3; // 低

	// 从数据库中获取
	public static ArrayList<Caution> LocalLoad(Context context, String date) {
		ArrayList<Caution> list = new ArrayList<Caution>();
		DBHelper dbHelper = null;
		Cursor cursor = null;

		try {
			dbHelper = new DBHelper(context);

			String sql = "select * from " + DBHelper.CAUTION_TABLE_NAME
					+ " where " + DBHelper.CAUTION_DATE + " =? and "
					+ DBHelper.CAUTION_CREATER + " = ?  order by "
					+ DBHelper.CAUTION_TIME;
			String[] bindArgs = { date, String.valueOf(Property.StaffId) };

			cursor = dbHelper.getReadableDatabase().rawQuery(sql, bindArgs);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Caution caution = new Caution();
					caution.ID = cursor.getLong(cursor
							.getColumnIndex(DBHelper.CAUTION_ID));
					caution.title = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_TITLE));
					caution.content = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_CONTENT));
					caution.attachaudio = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_ATTACHAUDIO));
					caution.attachphoto = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_ATTACHPHOTO));
					caution.date = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_DATE));
					caution.time = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_TIME));
					caution.level = cursor.getInt(cursor
							.getColumnIndex(DBHelper.CAUTION_LEVEL));
					caution.valid = cursor.getInt(cursor
							.getColumnIndex(DBHelper.CAUTION_VALID));
					list.add(caution);
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

	// 从数据库中获取
	@SuppressLint("SimpleDateFormat")
	public static ArrayList<Caution> LocalLoad(Context context) {
		ArrayList<Caution> list = new ArrayList<Caution>();
		DBHelper dbHelper = null;
		Cursor cursor = null;
		SimpleDateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat TimeFormatter = new SimpleDateFormat("HH:mm");
		Date curDate = new Date(System.currentTimeMillis());
		String date = DateFormatter.format(curDate);
		String time = TimeFormatter.format(curDate);
		time = time + ":00";
		
		try {
			dbHelper = new DBHelper(context);

			String sql = "select * from " + DBHelper.CAUTION_TABLE_NAME
					+ " where " + DBHelper.CAUTION_DATE + " =? and "
					+ DBHelper.CAUTION_TIME + " =? and "
					+ DBHelper.CAUTION_CREATER + " = ?  order by "
					+ DBHelper.CAUTION_TIME;
			String[] bindArgs = { date, time, String.valueOf(Property.StaffId) };

			cursor = dbHelper.getReadableDatabase().rawQuery(sql, bindArgs);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Caution caution = new Caution();
					caution.ID = cursor.getLong(cursor
							.getColumnIndex(DBHelper.CAUTION_ID));
					caution.title = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_TITLE));
					caution.content = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_CONTENT));
					caution.attachaudio = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_ATTACHAUDIO));
					caution.attachphoto = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_ATTACHPHOTO));
					caution.date = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_DATE));
					caution.time = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_TIME));
					caution.level = cursor.getInt(cursor
							.getColumnIndex(DBHelper.CAUTION_LEVEL));
					caution.valid = cursor.getInt(cursor
							.getColumnIndex(DBHelper.CAUTION_VALID));
					list.add(caution);
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

	// 根据ID获取数据
	public static Caution GetById(Context context, long id) {
		DBHelper dbHelper = null;
		Cursor cursor = null;
		Caution caution = null;
		try {
			dbHelper = new DBHelper(context);

			String sql = "select * from " + DBHelper.CAUTION_TABLE_NAME
					+ " where " + DBHelper.CAUTION_ID + " =?";
			String[] bindArgs = { String.valueOf(id) };

			cursor = dbHelper.getReadableDatabase().rawQuery(sql, bindArgs);

			if (cursor.getCount() == 1) {
				while (cursor.moveToNext()) {
					caution = new Caution();
					caution.ID = cursor.getLong(cursor
							.getColumnIndex(DBHelper.CAUTION_ID));
					caution.title = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_TITLE));
					caution.content = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_CONTENT));
					caution.attachaudio = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_ATTACHAUDIO));
					caution.attachphoto = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_ATTACHPHOTO));
					caution.date = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_DATE));
					caution.time = cursor.getString(cursor
							.getColumnIndex(DBHelper.CAUTION_TIME));
					caution.level = cursor.getInt(cursor
							.getColumnIndex(DBHelper.CAUTION_LEVEL));
					caution.valid = cursor.getInt(cursor
							.getColumnIndex(DBHelper.CAUTION_VALID));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		return caution;
	}

	// 更新状态
	public static boolean setValid(Context context, long ID, int valid) {
		boolean ret = true;
		DBHelper dbHelper = new DBHelper(context);

		try {
			String sql = "update " + DBHelper.CAUTION_TABLE_NAME + " set "
					+ DBHelper.CAUTION_VALID + " = '" + valid + "' where "
					+ DBHelper.CAUTION_ID + " = '" + ID + "';";

			dbHelper.execSQL(sql);
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}

		return ret;
	}

	// 更新状态
	public static boolean UpdateCaution(Context context, Caution caution) {
		boolean ret = true;
		DBHelper dbHelper = new DBHelper(context);

		try {
			String sql = "update " + DBHelper.CAUTION_TABLE_NAME + " set "
					+ DBHelper.CAUTION_TITLE + " = '" + caution.title + "',"
					+ DBHelper.CAUTION_DATE + " = '" + caution.date + "',"
					+ DBHelper.CAUTION_TIME + " = '" + caution.time + "',"
					+ DBHelper.CAUTION_LEVEL + " = '" + caution.level + "',"
					+ DBHelper.CAUTION_ATTACHAUDIO + " = '"
					+ caution.attachaudio + "'," + DBHelper.CAUTION_ATTACHPHOTO
					+ " = '" + caution.attachphoto + "',"
					+ DBHelper.CAUTION_CONTENT + " = '" + caution.content
					+ "'," + DBHelper.CAUTION_VALID + " = '" + caution.valid
					+ "' where " + DBHelper.CAUTION_ID + " = '" + caution.ID
					+ "';";

			dbHelper.execSQL(sql);
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}

		return ret;
	}
	
	// 删除
	public static void DeleteCaution(Context context, long id) {
		DBHelper dbHelper = new DBHelper(context);

		try {
			String sql = "delete from  " + DBHelper.CAUTION_TABLE_NAME
					+ " where " + DBHelper.CAUTION_ID + " = '" + id + "';";

			dbHelper.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}
	}

	// 添加
	public static long AddCaution(Context context, Caution caution) {
		DBHelper dbHelper = new DBHelper(context);
		Long ID = -1L;
		Cursor cursor = null;

		try {
			dbHelper = new DBHelper(context);
			String sql = "select max(ID) as MaxID from "
					+ DBHelper.CAUTION_TABLE_NAME;

			cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);

			if (cursor.getCount() == 1) {
				while (cursor.moveToNext()) {
					ID = cursor.getLong(cursor.getColumnIndex("MaxID"));
				}

				ID++;
			} else {
				ID = 1L;
			}

			String sqlInsert = "insert into " + DBHelper.CAUTION_TABLE_NAME
					+ "(" + DBHelper.CAUTION_ID + ","
					+ DBHelper.CAUTION_CREATER + "," + DBHelper.CAUTION_TITLE
					+ "," + DBHelper.CAUTION_CONTENT + ","
					+ DBHelper.CAUTION_ATTACHAUDIO + ","
					+ DBHelper.CAUTION_ATTACHPHOTO + ","
					+ DBHelper.CAUTION_TIME + "," + DBHelper.CAUTION_DATE + ","
					+ DBHelper.CAUTION_LEVEL + "," + DBHelper.CAUTION_VALID
					+ ") values ('" + ID + "','" + Property.StaffId + "','"
					+ caution.title + "','" + caution.content + "','"
					+ caution.attachaudio + "','" + caution.attachphoto + "','"
					+ caution.time + "','" + caution.date + "','"
					+ caution.level + "','" + caution.valid + "');";
			dbHelper.execSQL(sqlInsert);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		return ID;
	}
}
