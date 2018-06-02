package easyway.Mobile.Login;

import java.util.HashMap;
import java.util.Map;

import easyway.Mobile.Property;
import easyway.Mobile.util.DBHelper;
import android.content.Context;
import android.database.Cursor;

// 基础数据版本管理
public class BasicDataVersion {
	public static final int FLAG_INVALID = 0;
	public static final int FLAG_BROAD = 1;		// 广播
	public static final int FLAG_PARAM = 2;		// 参数
	public static final int FLAG_DEPT = 3;			// 部门
	public static final int FLAG_USER = 4;		// 人员
	public static final int FLAG_GROUP = 5;		// 群组
	public static final int FLAG_SCHEDULE = 6;	// 时刻表
	public static final int FLAG_STATION = 7;	// 车站信息
	public static final int FLAG_TTYPE = 8;		// 车辆类型
	public static final int FLAG_VIASTATION = 9;	// 途经站信息
	public static final int FLAG_LINKSTATION = 10;	// 车站名索引
	public static final int FLAG_POSITION = 11;	// 岗位
	public static final int FLAG_WORKSPACE = 12;	// 任务区域
	public static final int FLAG_PWRS = 13;	// 岗位<->任务区域
	public static final int FLAG_SDRS = 14;	// 人员<->部门
	public static final int FLAG_DEVFAULTCATE = 15;
	public static final int FLAG_LCTRLCATE = 16; //照明控制区域分类

    // 基础数据版本信息
    public static final String VERSION_BROAD = "GetAllBoardcastInfo";
	public static final String VERSION_PARAM = "GetAllParam";
    public static final String VERSION_DEPT = "GetAllDepartment";
    public static final String VERSION_USER = "GetAllUser";
    public static final String VERSION_GROUP = "GetAllGroupByUser";
    public static final String VERSION_SCHEDULE = "GetTrainSchedule";
    public static final String VERSION_STATION = "GetTrainStation";
    public static final String VERSION_TTYPE = "GetTrainTypeInfo";
    public static final String VERSION_VIASTATION = "GetAllViaStation";
    public static final String VERSION_LINKSTATION = "GetLinkStation";
    public static final String VERSION_POSITION = "GetAllPosition";
    public static final String VERSION_WORKSPACE = "GetAllTaskWorkspace";
    public static final String VERSION_PWRS = "GetAllPositionWorkspace";
    public static final String VERSION_SDRS = "GetStaffDepartment";
//    public static final String VERSION_SMWC = "GetTaskWorkspaceCate";    
    public static final String VERSION_DEVFAULTCATE = "GetDevFaultCate";
    public static final String VERSION_LCTRLCATE = "GetLCtrLCate";
    
    public static final int VERSION_INVALID = -1;
    
    public static Map<String, Integer> Versions = new HashMap<String, Integer>();
    
    public static int VBroad = VERSION_INVALID;
    public static int VParam = VERSION_INVALID;
    public static int VDept = VERSION_INVALID;
    public static int VUser = VERSION_INVALID;
    public static int VGroup = VERSION_INVALID;
    public static int VSchedule = VERSION_INVALID;
    public static int VStation = VERSION_INVALID;
    public static int VTType = VERSION_INVALID;
    public static int VViaStation = VERSION_INVALID;
    public static int VLinkStation = VERSION_INVALID;
    public static int VPosition = VERSION_INVALID;
    public static int VWorkspace = VERSION_INVALID;
    public static int VPWRS = VERSION_INVALID;
    public static int VSDRS = VERSION_INVALID;
//    public static int VSMWC = VERSION_INVALID;
    public static int VDevFaultCate = VERSION_INVALID;
    public static int VLCtrlCate = VERSION_INVALID;
    
    public static void ResetVersions() {
    	VBroad = VERSION_INVALID;
    	VParam = VERSION_INVALID;
    	VDept = VERSION_INVALID;
    	VUser = VERSION_INVALID;
    	VGroup = VERSION_INVALID;
    	VSchedule = VERSION_INVALID;
    	VStation = VERSION_INVALID;
        VTType = VERSION_INVALID;
        VViaStation = VERSION_INVALID;
        VLinkStation = VERSION_INVALID;
        VPosition = VERSION_INVALID;
        VWorkspace = VERSION_INVALID;
        VPWRS = VERSION_INVALID;
        VSDRS = VERSION_INVALID;
//        VSMWC = VERSION_INVALID;
        VDevFaultCate = VERSION_INVALID;
        VLCtrlCate = VERSION_INVALID;
    }
    
    public static void GetVersions(Context context) {
    	DBHelper dbHelper = new DBHelper(context);
    	
        String[] columns = {DBHelper.BASICDATA_NAME, DBHelper.BASICDATA_VERSION };
        Cursor cursor = null;
        
        try {
            cursor = dbHelper.exeSql(DBHelper.BASICDATA_TABLE_NAME, columns,
                    null, null, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {	
                	String name = cursor.getString(cursor
							.getColumnIndex(DBHelper.BASICDATA_NAME));
                	int version = cursor.getInt(cursor
							.getColumnIndex(DBHelper.BASICDATA_VERSION));

                	String groupname = VERSION_GROUP + Property.StaffId;			// 防止StaffId与其他FLAG冲突，添加偏移量
                	if (name.equalsIgnoreCase(VERSION_BROAD)) {
                		VBroad = version;
                	} else if (name.equalsIgnoreCase(VERSION_PARAM)) {
                		VParam = version;
                	} else if (name.equalsIgnoreCase(VERSION_DEPT)) {
                		VDept = version;
                	} else if (name.equalsIgnoreCase(VERSION_USER)) {
                		VUser = version;
                	} else if (name.equalsIgnoreCase(groupname)) {
                		VGroup = version;
                	} else if (name.equalsIgnoreCase(VERSION_SCHEDULE)) {
                		VSchedule = version;
                	} else if (name.equalsIgnoreCase(VERSION_STATION)) {
                		VStation = version;
                	} else if (name.equalsIgnoreCase(VERSION_TTYPE)) {
                		VTType = version;
                	} else if (name.equalsIgnoreCase(VERSION_VIASTATION)) {
                		VViaStation = version;
                	} else if (name.equalsIgnoreCase(VERSION_LINKSTATION)) {
                		VLinkStation = version;
                	} else if (name.equalsIgnoreCase(VERSION_POSITION)) {
                		VPosition = version;
                	} else if (name.equalsIgnoreCase(VERSION_WORKSPACE)) {
                		VWorkspace = version;
                	} else if (name.equalsIgnoreCase(VERSION_PWRS)) {
                		VPWRS = version;
                	} else if (name.equalsIgnoreCase(VERSION_SDRS)) {
                		VSDRS = version;
//                	} else if (name.equalsIgnoreCase(VERSION_SMWC)) {
//                		VSMWC = version;
                	} else if (name.equalsIgnoreCase(VERSION_DEVFAULTCATE)) {
                		VDevFaultCate = version;                		
                	} else if (name.equalsIgnoreCase(VERSION_LCTRLCATE)) {
                		VLCtrlCate = version;                		
                	}
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	dbHelper.closeCursor(cursor);
        	dbHelper.close();
        }
    }
    
    // 设置数据版本
    public static void SetVersions(Context context, String name, int version) {
		DBHelper dbHelper = new DBHelper(context);
		try {
			String sql_del = "delete from " + DBHelper.BASICDATA_TABLE_NAME
					+ " where " + DBHelper.BASICDATA_NAME + " = '" + name + "';";
			dbHelper.execSQL(sql_del);

			String sql_ins = "insert into " + DBHelper.BASICDATA_TABLE_NAME
					+ "(" + DBHelper.BASICDATA_NAME + ","
					+ DBHelper.BASICDATA_VERSION + ") values ('" + name + "','"
					+ version + "');";
			dbHelper.execSQL(sql_ins);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}
	}
    
    public static void setVBroad(Context context, int version) {
    	VBroad = version;
    	SetVersions(context, VERSION_BROAD, version);
    }
    
    public static void setVDept(Context context, int version) {
    	VDept = version;
    	SetVersions(context, VERSION_DEPT, version);
    }
    
    public static void setVParam(Context context, int version) {
    	VParam = version;
    	SetVersions(context, VERSION_PARAM, version);
    }
    
    public static void setVUser(Context context, int version) {
    	VUser = version;
    	SetVersions(context, VERSION_USER, version);
    }
    
    public static void setVGroup(Context context, int version) {
    	VGroup = version;
    	SetVersions(context, VERSION_GROUP + Property.StaffId, version);
    }

    public static void setVSchedule(Context context, int version) {
    	VSchedule = version;
    	SetVersions(context, VERSION_SCHEDULE, version);
    }
    
    public static void setVStation(Context context, int version) {
    	VStation = version;
    	SetVersions(context, VERSION_STATION, version);
    }
    
    public static void setVTType(Context context, int version) {
    	VTType = version;
    	SetVersions(context, VERSION_TTYPE, version);
    }
    
    public static void setVViaStaion(Context context, int version) {
    	VViaStation = version;
    	SetVersions(context, VERSION_VIASTATION, version);
    }
    
    public static void setVLinkStation(Context context, int version) {
    	VLinkStation = version;
    	SetVersions(context, VERSION_LINKSTATION, version);
    }
    
    public static void setVPosition(Context context, int version) {
    	VPosition = version;
    	SetVersions(context, VERSION_POSITION, version);
    }
    
    public static void setVWorkspace(Context context, int version) {
    	VWorkspace = version;
    	SetVersions(context, VERSION_WORKSPACE, version);
    }
    
    public static void setVPWRS(Context context, int version) {
    	VPWRS = version;
    	SetVersions(context, VERSION_PWRS, version);
    }
    
    public static void setVSDRS(Context context, int version) {
    	VSDRS = version;
    	SetVersions(context, VERSION_SDRS, version);
    }
    
//    public static void setVSMWC(Context context, int version) {
//    	VSMWC = version;
//    	SetVersions(context, VERSION_SMWC, version);
//    }
    
    public static void setVDevFaultCate(Context context, int version) {
    	VDevFaultCate = version;
    	SetVersions(context, VERSION_DEVFAULTCATE, version);
    }    
    
    public static void setVLCtrlCate(Context context, int version) {
    	VLCtrlCate = version;
    	SetVersions(context, VERSION_LCTRLCATE, version);
    }        
}
