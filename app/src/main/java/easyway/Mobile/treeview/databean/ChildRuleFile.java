package easyway.Mobile.treeview.databean;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Property;
import easyway.Mobile.util.JsonUtil;


/**
 * Created by boy on 2017/5/8.
 */

public class ChildRuleFile implements Serializable {

    /**
     * RTitle : 标题
     * RType : 1第一类
     * RKeywords : 关键字
     * RFileName : http://58.211.125.61:8005/Upload/Regulation/%e8%b7%af%e5%b1%80%e5%ae%a2%e8%bf%90%e8%a7%84%e7%ab%a0%ef%bc%88%e6%96%b015%e5%b9%b4%ef%bc%89/1-%e5%b1%80%e6%b1%87%e7%bc%96/1%e7%ac%ac%e4%b8%80%e7%b1%bb/20170505163533.png
     * RCover : http://58.211.125.61:8005/Upload/Regulation/路局客运规章（新15年）/1-局汇编/1第一类/Thumb_e207abeb-49b5-48f3-bc70-0f6ccd720b31.png
     * MD5 : f328cb4f3e54eb5cb70f508d90cab343
     */

    public String RTitle;
    public String RType;
    public String RKeywords;
    public String RFileName;
    public String RCover;
    public String MD5;
    public int downloaded = DOWNLOAD_UN; // 是否已下载 0：未下载 1：已下载 2：下载中
    public int downloadpro = 0; // 下载进度
    public int downloadmax = 0; // 下载进度

    public static final int DOWNLOAD_ED = 1; // 已下载
    public static final int DOWNLOAD_UN = 0; // 未下载
    public static final int DOWNLOAD_ING = 2; // 下载中
    public static final int DOWNLOAD_FAIL = 3; // 下载失败

    public static List<ChildRuleFile> parseRuleFile(String jsonResult) {
        List<ChildRuleFile> list = new ArrayList<ChildRuleFile>();

        if (jsonResult == null)
            return list;

        JSONArray jsonArray = JsonUtil.GetJsonArray(jsonResult, "Data");
        if (jsonArray == null || jsonArray.length() == 0)
            return list;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = (JSONObject) jsonArray.opt(i);

            ChildRuleFile rule = new ChildRuleFile();
            rule.RTitle = JsonUtil.GetJsonObjStringValue(jsonObj, "RTitle");
            rule.RType = JsonUtil.GetJsonObjStringValue(jsonObj, "RType");
            rule.RKeywords = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "RKeywords");
            rule.RFileName = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "RFileName");
            rule.RCover = JsonUtil.GetJsonObjStringValue(jsonObj, "RCover");
            rule.MD5 = JsonUtil.GetJsonObjStringValue(jsonObj, "MD5");
            list.add(rule);
        }

        return list;
    }

    // 添加数据
    public static void Insert(Context context, ChildRuleFile rule) {
        if (rule == null)
            return;

        DBHelper dbHelper = new DBHelper(context);
        try {
            String sql = "insert into " + DBHelper.SR_TABLE_NAME + "("
                    + DBHelper.SR_TITLE + "," + DBHelper.SR_FILENAME + ","
                    + DBHelper.SR_TYPE + "," + DBHelper.SR_KEYWORDS + ","
                    + DBHelper.SR_COVER + "," + DBHelper.SR_OWNER + ","
                    + DBHelper.SR_MD5
                    + ") values ('"
                    + rule.RTitle + "','" + rule.RFileName
                    + "','" + rule.RType + "','" + rule.RKeywords
                    + "','" + rule.RCover + "','" + Property.StaffId
                    + "','" + rule.MD5 + "');";
            dbHelper.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    // 删除数据
    public static void Delete(Context context, ChildRuleFile rule) {
        if (rule == null)
            return;

        DBHelper dbHelper = new DBHelper(context);
        try {
            String sql = "delete from " + DBHelper.SR_TABLE_NAME + " where "
                    + DBHelper.SR_TITLE + "= '" + rule.RTitle + "' and "
                    + DBHelper.SR_OWNER + "= '" + Property.StaffId + "';";

            dbHelper.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    // 判断是否已保存
    public static boolean CheckExists(Context context, ChildRuleFile rule, boolean checkowner) {
        boolean ret = false;

        if (rule == null)
            return ret;

        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = null;
        try {
            String sql = null;
            if (checkowner) {
                sql = "select count(*) from " + DBHelper.SR_TABLE_NAME
                        + " where " + DBHelper.SR_TITLE + " = '" + rule.RTitle + "'and "
                        + DBHelper.SR_OWNER + " ='" + Property.StaffId + "'";

            } else {
                sql = "select count(*) from " + DBHelper.SR_TABLE_NAME
                        + " where " + DBHelper.SR_TITLE + " = '" + rule.RTitle + "'";
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
    public static boolean CheckExists(Context context, ChildRuleFile rule) {
        return CheckExists(context, rule, true);
    }

    // 从数据库中获取
    public static ArrayList<ChildRuleFile> LocalLoad(Context context, String key,
                                                     String type) {
        ArrayList<ChildRuleFile> list = new ArrayList<ChildRuleFile>();
        DBHelper dbHelper = null;
        Cursor cursor = null;

        try {
            dbHelper = new DBHelper(context);
            String sql = DBHelper.SR_OWNER + " ='" + Property.StaffId + "'";

            String[] columns = {DBHelper.SR_TITLE, DBHelper.SR_TYPE,
                    DBHelper.SR_KEYWORDS, DBHelper.SR_FILENAME, DBHelper.SR_COVER,
                    DBHelper.SR_MD5};

            if (type != null && !type.trim().equals(""))
                sql += " and " + DBHelper.SR_TYPE + " in ( " + type.trim() + " ) ";

            if (key != null && !key.trim().equals(""))
                sql += " and " + DBHelper.SR_TITLE + " like '%"
                        + key.trim() + "%' or " + DBHelper.SR_KEYWORDS
                        + " like '%" + key.trim() + "%'";

            cursor = dbHelper.exeSql(DBHelper.SR_TABLE_NAME, columns, sql, null, null, null, DBHelper.SR_TITLE);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ChildRuleFile rule = new ChildRuleFile();
                    rule.RTitle = cursor.getString(cursor
                            .getColumnIndex(DBHelper.SR_TITLE));
                    rule.RType = cursor.getString(cursor
                            .getColumnIndex(DBHelper.SR_TYPE));
                    rule.RKeywords = cursor.getString(cursor
                            .getColumnIndex(DBHelper.SR_KEYWORDS));
                    rule.RFileName = cursor.getString(cursor
                            .getColumnIndex(DBHelper.SR_FILENAME));
                    rule.RCover = cursor.getString(cursor
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
