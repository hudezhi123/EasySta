package easyway.Mobile.SiteRules;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import easyway.Mobile.Property;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.util.JsonUtil;

/*
 * 	站内规章
 */
public class SiteRule {
	public String Title; // 标题
	public String Type; // 类型
	public String Keywords; // 文档关键字
	public String FileName; // 文件名（带完整路径）
	public String Cover; // 封面
	public String MD5;		// 文件MD5值，用于验证其完整性
	public int downloaded = DOWNLOAD_UN; // 是否已下载 0：未下载 1：已下载 2：下载中
	public int downloadpro = 0; // 下载进度
	public int downloadmax = 0; // 下载进度
	
	public static final int DOWNLOAD_ED = 1; // 已下载
	public static final int DOWNLOAD_UN = 0; // 未下载
	public static final int DOWNLOAD_ING = 2; // 下载中
	public static final int DOWNLOAD_FAIL = 3; // 下载失败

	// 解析字符串
	public static ArrayList<SiteRule> ParseFromString(String result) {
		ArrayList<SiteRule> list = new ArrayList<SiteRule>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);

			SiteRule rule = new SiteRule();
			rule.Title = JsonUtil.GetJsonObjStringValue(jsonObj, "RTitle");
			rule.Type = JsonUtil.GetJsonObjStringValue(jsonObj, "RType");
			rule.Keywords = JsonUtil
					.GetJsonObjStringValue(jsonObj, "RKeywords");
			rule.FileName = JsonUtil
					.GetJsonObjStringValue(jsonObj, "RFileName");
			rule.Cover = JsonUtil.GetJsonObjStringValue(jsonObj, "RCover");
			rule.MD5 = JsonUtil.GetJsonObjStringValue(jsonObj, "MD5");
			
			list.add(rule);
		}

		return list;
	}

	// 添加数据
	public static void Insert(Context context, SiteRule rule) {
		if (rule == null)
			return;

		DBHelper dbHelper = new DBHelper(context);
		try {
			String sql = "insert into " + DBHelper.SR_TABLE_NAME + "("
					+ DBHelper.SR_TITLE + "," + DBHelper.SR_FILENAME + ","
					+ DBHelper.SR_TYPE + "," + DBHelper.SR_KEYWORDS + ","
					+ DBHelper.SR_COVER + "," + DBHelper.SR_OWNER+ ","
					+ DBHelper.SR_MD5
					+ ") values ('"
					+ rule.Title + "','" + rule.FileName
					+ "','" + rule.Type + "','" + rule.Keywords
					+ "','" + rule.Cover + "','" + Property.StaffId 
					+ "','" + rule.MD5 + "');";
			dbHelper.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}
	}

	// 删除数据
	public static void Delete(Context context, SiteRule rule) {
		if (rule == null)
			return;

		DBHelper dbHelper = new DBHelper(context);
		try {
			String sql = "delete from " + DBHelper.SR_TABLE_NAME + " where "
					+ DBHelper.SR_TITLE + "= '" + rule.Title + "' and "
					+ DBHelper.SR_OWNER + "= '"+ Property.StaffId + "';";

			dbHelper.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}
	}

	// 判断是否已保存
	public static boolean CheckExists(Context context, SiteRule rule, boolean checkowner) {
		boolean ret = false;

		if (rule == null)
			return ret;

		DBHelper dbHelper = new DBHelper(context);
		Cursor cursor = null;
		try {
			String sql = null;
			if (checkowner) {
				sql = "select count(*) from " + DBHelper.SR_TABLE_NAME
				+ " where " + DBHelper.SR_TITLE + " = '" + rule.Title + "'and "
				+ DBHelper.SR_OWNER  + " ='" + Property.StaffId + "'" ;

			} else {
				sql = "select count(*) from " + DBHelper.SR_TABLE_NAME
				+ " where " + DBHelper.SR_TITLE + " = '" + rule.Title + "'";
			}

			cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					ret = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			dbHelper.close();
		}

		return ret;
	}
	
	// 判断是否已保存
	public static boolean CheckExists(Context context, SiteRule rule) {
		return CheckExists(context, rule, true);
	}

	// 从数据库中获取
	public static ArrayList<SiteRule> LocalLoad(Context context, String key,
			String type) {
		ArrayList<SiteRule> list = new ArrayList<SiteRule>();
		DBHelper dbHelper = null;
		Cursor cursor = null;

		try {
			dbHelper = new DBHelper(context);
			String sql = DBHelper.SR_OWNER + " ='" + Property.StaffId + "'";

			String[] columns = { DBHelper.SR_TITLE, DBHelper.SR_TYPE,
					DBHelper.SR_KEYWORDS, DBHelper.SR_FILENAME, DBHelper.SR_COVER,
					DBHelper.SR_MD5};
			
			if (type != null && !type.trim().equals(""))
				sql +=  " and " + DBHelper.SR_TYPE + " in ( " + type.trim() + " ) ";
			
			if (key != null && !key.trim().equals(""))
				sql += " and " + DBHelper.SR_TITLE + " like '%"
						+ key.trim() + "%' or " + DBHelper.SR_KEYWORDS
						+ " like '%" + key.trim() + "%'";

			cursor = dbHelper.exeSql(DBHelper.SR_TABLE_NAME, columns, sql, null, null, null, DBHelper.SR_TITLE);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					SiteRule rule = new SiteRule();
					rule.Title = cursor.getString(cursor
							.getColumnIndex(DBHelper.SR_TITLE));
					rule.Type = cursor.getString(cursor
							.getColumnIndex(DBHelper.SR_TYPE));
					rule.Keywords = cursor.getString(cursor
							.getColumnIndex(DBHelper.SR_KEYWORDS));
					rule.FileName = cursor.getString(cursor
							.getColumnIndex(DBHelper.SR_FILENAME));
					rule.Cover = cursor.getString(cursor
							.getColumnIndex(DBHelper.SR_COVER));
					rule.MD5 = cursor.getString(cursor
							.getColumnIndex(DBHelper.SR_MD5));

					list.add(rule);
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
