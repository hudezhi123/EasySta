package easyway.Mobile.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * 数据库
 */
public final class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 15;
    private static final String DB_NAME = "Easyway_zwt";

    // 短消息
    public static final String MESSAGE_TABLE_NAME = "zwtmessage";
    public static final String MESSAGE_ID = "Id";
    public static final String MESSAGE_TYPE = "Type";
    public static final String MESSAGE_OWNERID = "OwnerId";
    public static final String MESSAGE_OWNERNAME = "OwnerName";
    public static final String MESSAGE_CONTACTID = "ContacdId";
    public static final String MESSAGE_CONTACTNAME = "ContacdName";
    public static final String MESSAGE_CONTENT = "Content";
    public static final String MESSAGE_CREATETIME = "CreateTime";
    public static final String MESSAGE_ATTACH = "Attach";
    public static final String MESSAGE_STATUS = "Status";
    public static final String MESSAGE_FLAG = "Flag";
    public static final String MESSAGE_receipt = "Receipt";
    public static final String MESSAGE_MgsId = "MgsId";

    // 常用短消息
    public static final String TEXT_TABLE_NAME = "common_text";
    public static final String TEXT_CONTENT = "content";
    public static final String TEXT_TYPE = "type";
    public static final String TEXT_DATE = "date";

    public static final String TEXT_TYPE_MSG = "msg";
    public static final String TEXT_TYPE_TASKNAME = "tn";

    // 系统参数
    public static final String PARAM_TABLE_NAME = "TB_SYS_ParamDetail";
    public static final String PARAM_CODE = "ParamCode";
    public static final String PARAM_VALUE = "ParamValue";
    public static final String PARAM_NAME = "DetailName";

    // 人员信息
    public static final String STAFF_TABLE_NAME = "TB_ORG_Staff";
    public static final String STAFF_ID = "StaffId";
    public static final String STAFF_CODE = "StaffCode";
    public static final String STAFF_NAME = "StaffName";
    public static final String STAFF_MOBILE = "Mobile";
    public static final String STAFF_HOMEADDRESS = "HomeAddress";
    public static final String STAFF_TYPE = "Type"; // 0：人员 1：群组
    public static final String STAFF_OWNER = "Owner"; // 用户StaffId， 其中0表示所有人
    public static final String STAFF_EXPEND1 = "Expend1"; // VOIP 用户ID

    // 部门信息
    public static final String DEPT_TABLE_NAME = "TB_ORG_Dept";
    public static final String DEPT_ID = "DeptId";
    public static final String DEPT_PARENTID = "ParentDeptId";
    public static final String DEPT_FULLNAME = "FullName";
    public static final String DEPT_DEPTMARK = "DeptMark";
    public static final String DEPT_EXPEND1 = "Expend1";
    public static final String DEPT_TYPE = "Type"; // 0：部门 1：群组

    // 部门、人员关联信息
    public static final String DEPTSTAFF_TABLE_NAME = "TB_ORG_DeptStaff";
    public static final String DEPTSTAFF_ID = "Id";
    public static final String DEPTSTAFF_DEPTID = "DeptId";
    public static final String DEPTSTAFF_STAFFID = "StaffId";

    // 广播
    public static final String BROAD_TABLE_NAME = "BroadAreaSpecSubject";
    public static final String BROAD_ID = "id";
    public static final String BROAD_CATEGORY = "BroadcastCategory";
    public static final String BROAD_TITLE = "BroadcastTitle";
    public static final String BROAD_CONTENT = "BroadcastContent";
    public static final String BROAD_AREA = "BroadcastArea";
    public static final String BROAD_IDENO_EQT = "IDENO_EQTs";

    // 列车时刻表
    public static final String TRAINSCHE_TABLE_NAME = "train_schedule";
    public static final String TRAINSCHE_ID = "_id";
    public static final String TRAINSCHE_TRNO_PRO = "TRNO_PRO";
    public static final String TRAINSCHE_STRTSTN_TT = "STRTSTN_TT";
    public static final String TRAINSCHE_TILSTN_TT = "TILSTN_TT";
    public static final String TRAINSCHE_DepaTime = "DepaTime";
    public static final String TRAINSCHE_StationArrTime = "StationArrTime";
    public static final String TRAINSCHE_StationDepaTime = "StationDepaTime";
    public static final String TRAINSCHE_StationAttr = "StationAttr";
    public static final String TRAINSCHE_Miles = "Miles";

    // 列车所属站信息
    public static final String STATION_TABLE_NAME = "station";
    public static final String STATION_ID = "_id";
    public static final String STATION_NAME = "Name";
    public static final String STATION_COLOR = "Color";

    // 列车类型
    public static final String TRAINTYPE_TABLE_NAME = "train_type";
    public static final String TRAINTYPE_ID = "_id";
    public static final String TRAINTYPE_NAME = "Name";
    public static final String TRAINTYPE_NUMBER = "Number";

    // 基础数据版本
    public static final String BASICDATA_TABLE_NAME = "basicdata";
    public static final String BASICDATA_NAME = "NAME";
    public static final String BASICDATA_VERSION = "Version";

    // 车次途经站信息
    public static final String VIASTATION_TABLE_NAME = "viastation";
    public static final String VIASTATION_ID = "ID";
    public static final String VIASTATION_TRNO = "TRNO_TT";
    public static final String VIASTATION_STRTSTN = "STRTSTN_TT";
    public static final String VIASTATION_TILSTN = "TILSTN_TT";
    public static final String VIASTATION_STRTTIME = "STRTTIME_TT";
    public static final String VIASTATION_TILTIME = "TILTIME_TT";
    public static final String VIASTATION_ORDER = "StationOrder";
    public static final String VIASTATION_STATION = "Station";
    public static final String VIASTATION_ARRTIME = "ArrTime";
    public static final String VIASTATION_ARRDATE = "ArrDate";
    public static final String VIASTATION_DEPATIME = "DepaTime";
    public static final String VIASTATION_DEPADATE = "DepaDate";
    public static final String VIASTATION_STATIONATTR = "StationAttr";
    public static final String VIASTATION_MILES = "Miles";

    // 车站检索
    public static final String LINKSTATION_TABLE_NAME = "linkstation";
    public static final String LINKSTATION_NAME = "Name";
    public static final String LINKSTATION_PY = "Pinyin";
    public static final String LINKSTATION_NUM = "Number";

    // 岗位
    public static final String POSITION_TABLE_NAME = "Position";
    public static final String POSITON_PID = "PId";
    public static final String POSITON_NAME = "PositionName";
    public static final String POSITON_ADAVANCEMIN = "AdvanceMin";
    public static final String POSITON_DELAYMIN = "DelayMin";
    public static final String POSITON_CODE = "PositionCode";
    public static final String POSITON_STATIONCODE = "StationCode";

    // 任务区域
    public static final String WORKSPACE_TABLE_NAME = "Worksapce";
    public static final String WORKSPACE_TWID = "TwId";
    public static final String WORKSPACE_NAME = "Workspace";
    public static final String WORKSPACE_STATIONCODE = "StationCode";

    // 岗位、任务区域关系
    public static final String PWRS_TABLE_NAME = "PWRS";
    public static final String PWRS_PWID = "PwId";
    public static final String PWRS_TWID = "TwId";
    public static final String PWRS_PID = "PId";
    public static final String PWRS_WPTYPE = "WpType";

    // 设备报障故障列表
    public static final String DEVFAULTCATE_TABLE_NAME = "Devfaultcate";
    public static final String DEVFAULTCATE_CONFIGID = "ConfigId";
    public static final String DEVFAULTCATE_CONFIGKEY = "ConfigKey";
    public static final String DEVFAULTCATE_CONFIGNAME = "ConfigName";
    public static final String DEVFAULTCATE_CONFIGVALUE = "ConfigValue";
    public static final String DEVFAULTCATE_CODE = "Code";

    // 照明控制列表
    public static final String LCTRLCATE_TABLE_NAME = "LCtrlcate";
    public static final String LCTRLCATE_ID = "Id";
    public static final String LCTRLCATE_AREANAME = "AreaName";

    // 记事本
    public static final String CAUTION_TABLE_NAME = "Caution";
    public static final String CAUTION_ID = "ID";
    public static final String CAUTION_CREATER = "Creater";
    public static final String CAUTION_TITLE = "Title";
    public static final String CAUTION_CONTENT = "Content";
    public static final String CAUTION_ATTACHPHOTO = "AttachImag";
    public static final String CAUTION_ATTACHAUDIO = "AttachAudio";
    public static final String CAUTION_DATE = "Date";
    public static final String CAUTION_TIME = "Time";
    public static final String CAUTION_LEVEL = "Level";
    public static final String CAUTION_VALID = "Valid";

    // // 现场监控-任务区域
    // public static final String SMWC_TABLE_NAME = "SMWC";
    // public static final String SMWC_ID = "Id";
    // public static final String SMWC_TEXT = "Text";
    // public static final String SMWC_STATIONCODE = "StationCode";

    // 站内规章
    public static final String SR_TABLE_NAME = "SR";
    public static final String SR_TITLE = "Title";
    public static final String SR_FILENAME = "FileName";
    public static final String SR_TYPE = "Type";
    public static final String SR_KEYWORDS = "Keywords";
    public static final String SR_COVER = "Cover";
    public static final String SR_OWNER = "Owner";
    public static final String SR_MD5 = "MD5";

    //售票日志
    public static final String STL_TABLE_NAME = "Sell_Ticket_Log";
    public static final String STL_ID = "Id";
    public static final String STL_DATE = "Date";
    public static final String STL_TIME = "Time";
    public static final String STL_DAY_OF_WEEK = "Day_OF_Week";
    public static final String STL_GROUP_NO = "Group_No";
    public static final String STL_STAFF_ON_DUTY = "STAFF_ON_DUTY";  //负责的人
    public static final String STL_TICKET_TOTAL_NO = "Total_Ticket_No";
    public static final String STL_MONEY_TOTAL = "Total_Money";
    public static final String STL_TICKET_AGENCY_NO = "Agency_Ticket_No";  //代售点售出的钱
    public static final String STL_MONEY_AGENCY = "Agency_Money";
    public static final String STL_TICKET_ALLOPATRIC_NO = "Allopatric_Ticket_Total_No";
    public static final String STL_TICKET_ALLOPATRIC_NO_RETURN = "Allopatric_Ticket_No_Return";
    public static final String STL_MONEY_ALLOPATRIC_RETURN = "Allopatric_Money_Return";
    public static final String STL_BOSS_ORDER = "Boss_Order";
    public static final String STL_KEY_POINT = "Key_Point";
    public static final String STL_WORK_STATUS = "Work_Status";
    public static final String STL_EXCHANGE_ITEM = "Exchange_item";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE_NAME + "("
                + MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MESSAGE_TYPE + " INTEGER," + MESSAGE_OWNERID + " INTEGER,"
                + MESSAGE_OWNERNAME + " TEXT," + MESSAGE_CONTACTID
                + " INTEGER," + MESSAGE_CONTACTNAME + " TEXT,"
                + MESSAGE_CONTENT + " TEXT," + MESSAGE_CREATETIME
                + " TIMESTAMP ," + MESSAGE_ATTACH + " TEXT," + MESSAGE_STATUS
                + " INTEGER," + MESSAGE_receipt + " TEXT," + MESSAGE_MgsId
                + " INTEGER," + MESSAGE_FLAG + " INTEGER);";
        db.execSQL(sql);

        String sql_text = "CREATE TABLE IF NOT EXISTS " + TEXT_TABLE_NAME
                + " (" + TEXT_CONTENT + " TEXT ," + TEXT_TYPE + " TEXT,"
                + TEXT_DATE + " INTEGER);";
        db.execSQL(sql_text);

        String sql_param = "CREATE TABLE IF NOT EXISTS " + PARAM_TABLE_NAME
                + " (" + PARAM_CODE + " TEXT," + PARAM_VALUE + " TEXT,"
                + PARAM_NAME + " TEXT);";
        db.execSQL(sql_param);

        String sql_satff = "CREATE TABLE IF NOT EXISTS " + STAFF_TABLE_NAME
                + " (" + STAFF_ID + " INTEGER," + STAFF_NAME + " TEXT,"
                + STAFF_CODE + " TEXT," + STAFF_MOBILE + " TEXT,"
                + STAFF_HOMEADDRESS + " TEXT," + STAFF_TYPE + " INTEGER,"
                + STAFF_OWNER + " INTEGER," + STAFF_EXPEND1 + " TEXT);";
        db.execSQL(sql_satff);

        String sql_dept = "CREATE TABLE IF NOT EXISTS " + DEPT_TABLE_NAME
                + " (" + DEPT_ID + " INTEGER PRIMARY KEY," + DEPT_PARENTID
                + " INTEGER," + DEPT_FULLNAME + " TEXT," + DEPT_EXPEND1
                + " TEXT," + DEPT_TYPE + " INTEGER," + DEPT_DEPTMARK
                + " INTEGER);";
        db.execSQL(sql_dept);

        String sql_deptstaff = "CREATE TABLE IF NOT EXISTS "
                + DEPTSTAFF_TABLE_NAME + " (" + DEPTSTAFF_ID
                + " INTEGER PRIMARY KEY," + DEPTSTAFF_STAFFID + " INTEGER,"
                + DEPTSTAFF_DEPTID + " INTEGER);";
        db.execSQL(sql_deptstaff);

        String sql_broad = "CREATE TABLE IF NOT EXISTS " + BROAD_TABLE_NAME
                + " (" + BROAD_ID + " INTEGER," + BROAD_CATEGORY + " TEXT,"
                + BROAD_TITLE + " TEXT," + BROAD_CONTENT + " TEXT,"
                + BROAD_AREA + " TEXT," + BROAD_IDENO_EQT + " TEXT);";
        db.execSQL(sql_broad);

        String sql_train_schedule = "CREATE TABLE IF NOT EXISTS "
                + TRAINSCHE_TABLE_NAME + " (" + TRAINSCHE_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + TRAINSCHE_TRNO_PRO
                + " TEXT," + TRAINSCHE_STRTSTN_TT + " TEXT," + TRAINSCHE_TILSTN_TT
                + " TEXT," + TRAINSCHE_DepaTime + " TEXT,"
                + TRAINSCHE_StationArrTime + " TEXT," + TRAINSCHE_StationDepaTime + " TEXT,"
                + TRAINSCHE_StationAttr + " TEXT," + TRAINSCHE_Miles + " INTEGER)";
        db.execSQL(sql_train_schedule);

        String sql_station = "CREATE TABLE IF NOT EXISTS " + STATION_TABLE_NAME
                + " (" + STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STATION_NAME + " TEXT," + STATION_COLOR + " TEXT" + ");";
        db.execSQL(sql_station);

        String sql_train_type = "CREATE TABLE IF NOT EXISTS "
                + TRAINTYPE_TABLE_NAME + " (" + TRAINTYPE_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + TRAINTYPE_NAME
                + " TEXT," + TRAINTYPE_NUMBER + " INTEGER" + ");";
        db.execSQL(sql_train_type);

        String sql_basicdata = "CREATE TABLE IF NOT EXISTS "
                + BASICDATA_TABLE_NAME + " (" + BASICDATA_NAME
                + " TEXT PRIMARY KEY ," + BASICDATA_VERSION + " INTEGER" + ");";
        db.execSQL(sql_basicdata);

        String sql_viastation = "CREATE TABLE IF NOT EXISTS "
                + VIASTATION_TABLE_NAME + " (" + VIASTATION_ID + " INTEGER,"
                + VIASTATION_TRNO + " TEXT," + VIASTATION_STRTSTN + " TEXT,"
                + VIASTATION_TILSTN + " TEXT," + VIASTATION_STRTTIME + " TEXT,"
                + VIASTATION_TILTIME + " TEXT," + VIASTATION_ORDER
                + " INTEGER," + VIASTATION_STATION + " TEXT,"
                + VIASTATION_ARRTIME + " TEXT," + VIASTATION_ARRDATE
                + " INTEGER," + VIASTATION_DEPATIME + " TEXT,"
                + VIASTATION_DEPADATE + " INTEGER," + VIASTATION_STATIONATTR
                + " TEXT," + VIASTATION_MILES + " INTEGER" + ");";
        db.execSQL(sql_viastation);

        String sql_linkstation = "CREATE TABLE IF NOT EXISTS "
                + LINKSTATION_TABLE_NAME + " (" + LINKSTATION_NAME + " TEXT,"
                + LINKSTATION_PY + " TEXT," + LINKSTATION_NUM + " INTEGER"
                + ");";
        db.execSQL(sql_linkstation);

        String sql_position = "CREATE TABLE IF NOT EXISTS "
                + POSITION_TABLE_NAME + " (" + POSITON_PID + " INTEGER,"
                + POSITON_NAME + " TEXT," + POSITON_ADAVANCEMIN + " INTEGER,"
                + POSITON_DELAYMIN + " INTEGER," + POSITON_CODE + " TEXT,"
                + POSITON_STATIONCODE + " TEXT" + ");";
        db.execSQL(sql_position);

        String sql_workspace = "CREATE TABLE IF NOT EXISTS "
                + WORKSPACE_TABLE_NAME + " (" + WORKSPACE_TWID + " INTEGER,"
                + WORKSPACE_NAME + " TEXT," + WORKSPACE_STATIONCODE + " TEXT"
                + ");";
        db.execSQL(sql_workspace);

        String sql_pwrs = "CREATE TABLE IF NOT EXISTS " + PWRS_TABLE_NAME
                + " (" + PWRS_PWID + " INTEGER," + PWRS_TWID + " INTEGER,"
                + PWRS_PID + " INTEGER," + PWRS_WPTYPE + " INTEGER" + ");";
        db.execSQL(sql_pwrs);

        String sql_caution = "CREATE TABLE IF NOT EXISTS " + CAUTION_TABLE_NAME
                + " (" + CAUTION_TITLE + " TEXT," + CAUTION_ID
                + " INTEGER PRIMARY KEY," + CAUTION_CREATER + " INTEGER,"
                + CAUTION_CONTENT + " TEXT," + CAUTION_ATTACHPHOTO + " TEXT,"
                + CAUTION_ATTACHAUDIO + " TEXT," + CAUTION_DATE + " TEXT,"
                + CAUTION_TIME + " TEXT," + CAUTION_LEVEL + " INTEGER,"
                + CAUTION_VALID + " INTEGER" + ");";
        db.execSQL(sql_caution);

        String sql_siterules = "CREATE TABLE IF NOT EXISTS " + SR_TABLE_NAME
                + " (" + SR_TITLE + " TEXT ," + SR_TYPE + " TEXT,"
                + SR_KEYWORDS + " TEXT," + SR_FILENAME + " TEXT," + SR_COVER
                + " TEXT," + SR_OWNER + " TEXT," + SR_MD5 + " TEXT" + ");";
        db.execSQL(sql_siterules);

        // String sql_smtc = "CREATE TABLE IF NOT EXISTS "
        // + SMWC_TABLE_NAME + " (" + SMWC_ID
        // + " TEXT," + SMWC_TEXT
        // + " TEXT," + SMWC_STATIONCODE
        // + " TEXT" + ");";
        // db.execSQL(sql_smtc);

        String sql_devfaultcate = "CREATE TABLE IF NOT EXISTS "
                + DEVFAULTCATE_TABLE_NAME + " (" + DEVFAULTCATE_CONFIGID
                + " TEXT," + DEVFAULTCATE_CONFIGKEY + " TEXT,"
                + DEVFAULTCATE_CONFIGNAME + " TEXT," + DEVFAULTCATE_CONFIGVALUE
                + " TEXT," + DEVFAULTCATE_CODE + " TEXT" + ");";
        db.execSQL(sql_devfaultcate);

        String sql_lctrlcate = "CREATE TABLE IF NOT EXISTS "
                + LCTRLCATE_TABLE_NAME + " (" + LCTRLCATE_ID + " TEXT,"
                + LCTRLCATE_AREANAME + " TEXT" + ");";
        db.execSQL(sql_lctrlcate);

        String sql_sell_ticket = "CREATE TABLE IF NOT EXISTS "
                + STL_TABLE_NAME + "("
                + STL_ID + " TEXT,"
                + STL_DATE + " TEXT,"
                + STL_TIME + " TEXT,"
                + STL_DAY_OF_WEEK + " TEXT,"
                + STL_GROUP_NO + " TEXT,"
                + STL_STAFF_ON_DUTY + " TEXT,"
                + STL_TICKET_TOTAL_NO + " INTEGER,"
                + STL_MONEY_TOTAL + " INTEGER,"
                + STL_TICKET_AGENCY_NO + " INTEGER,"
                + STL_MONEY_AGENCY + " INTEGER,"
                + STL_TICKET_ALLOPATRIC_NO + " INTEGER,"
                + STL_TICKET_ALLOPATRIC_NO_RETURN + " INTEGER,"
                + STL_MONEY_ALLOPATRIC_RETURN + " INTEGER,"
                + STL_BOSS_ORDER + " TEXT,"
                + STL_KEY_POINT + " TEXT,"
                + STL_WORK_STATUS + "　TEXT,"
                + STL_EXCHANGE_ITEM + " TEXT"
                + ")";
        db.execSQL(sql_sell_ticket);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TEXT_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + PARAM_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + STAFF_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DEPT_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DEPTSTAFF_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + BROAD_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TRAINSCHE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + STATION_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TRAINTYPE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + BASICDATA_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + VIASTATION_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + LINKSTATION_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + POSITION_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + WORKSPACE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + PWRS_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + CAUTION_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + SR_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DEVFAULTCATE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + LCTRLCATE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + STL_TABLE_NAME + ";");
        this.onCreate(db);
    }

    public void clearTable(String tableName) {
        if (tableName == null)
            return;

        Cursor cursor = null;

        // 判断表是否存在
        String[] columns = {"name"};
        cursor = exeSql("sqlite_master", columns, "type='table' and name='"
                + tableName + "'", null, null, null, null);
        if (cursor == null) return;
        if (cursor.getCount() > 0) {
            getWritableDatabase().execSQL("delete from " + tableName + ";");
            if (tableName.equals(TEXT_TABLE_NAME)
                    || tableName.equals(TRAINSCHE_TABLE_NAME)
                    || tableName.equals(STATION_TABLE_NAME)
                    || tableName.equals(TRAINTYPE_TABLE_NAME)) {
                getWritableDatabase().execSQL(
                        "update sqlite_sequence set seq=0 where name=" + "'"
                                + tableName + "';");
            }
        }
    }

    public void clearStaff(int owner) {
        Cursor cursor = null;

        // 判断表是否存在
        String[] columns = {"name"};
        cursor = exeSql("sqlite_master", columns, "type='table' and name='"
                + STAFF_TABLE_NAME + "'", null, null, null, null);
        if (cursor == null) return;
        if (cursor.getCount() > 0) {
            getWritableDatabase().execSQL(
                    "delete from " + STAFF_TABLE_NAME + " where " + STAFF_OWNER
                            + " = " + owner + ";");
        }
    }

    public Cursor exeSql(String table, String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy) {
        try {
            Cursor cursor = getWritableDatabase().query(table, columns, selection,
                    selectionArgs, groupBy, having, orderBy);

            return cursor;
        } catch (Exception ex) {
            return null;
        }
    }

    public Cursor getCursorResult(String sql) {
        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        return cursor;
    }

    public void execSQL(String sql) {
        try {
            getWritableDatabase().execSQL(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
