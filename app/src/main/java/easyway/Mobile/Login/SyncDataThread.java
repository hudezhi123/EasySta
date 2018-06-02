package easyway.Mobile.Login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import easyway.Mobile.Broadcast.BroadcastInfo;
import easyway.Mobile.Data.TB_IMP_AREAS;
import easyway.Mobile.Property;
import easyway.Mobile.Data.Department;
import easyway.Mobile.Data.DevFaultCate;
import easyway.Mobile.Data.LampArea;
import easyway.Mobile.Data.LinkStation;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Data.ColorStation;
import easyway.Mobile.Data.StaffDeptRS;
import easyway.Mobile.Data.Parameter;
import easyway.Mobile.Data.TaskPWRS;
import easyway.Mobile.Data.TaskPosition;
import easyway.Mobile.Data.TaskWorkspace;
import easyway.Mobile.Data.TrainType;
import easyway.Mobile.Data.VIAStation;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.bean.VersionInfoResult;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.util.DataVersionsUtil;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PTTUtil;
import easyway.Mobile.bean.TrainBase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;

/*
 * // 获取检票状态等参数  增加parmValues.put("sessionId", Property.SessionId);p477
 * 基础数据同步
 */
public class SyncDataThread extends Thread {
    private int vBroad = BasicDataVersion.VERSION_INVALID;
    private int vDept = BasicDataVersion.VERSION_INVALID;
    private int vParam = BasicDataVersion.VERSION_INVALID;
    private int vUser = BasicDataVersion.VERSION_INVALID;
    private int vGroup = BasicDataVersion.VERSION_INVALID;
    private int vSchedule = BasicDataVersion.VERSION_INVALID;
    private int vStation = BasicDataVersion.VERSION_INVALID;
    private int vTType = BasicDataVersion.VERSION_INVALID;
    private int vVIAStation = BasicDataVersion.VERSION_INVALID;
    private int vLinkStation = BasicDataVersion.VERSION_INVALID;
    private int vPosition = BasicDataVersion.VERSION_INVALID;
    private int vWorkspace = BasicDataVersion.VERSION_INVALID;
    private int vPWRS = BasicDataVersion.VERSION_INVALID;
    private int vSDRS = BasicDataVersion.VERSION_INVALID;
    // private int vSMWC = BasicDataVersion.VERSION_INVALID;
    @SuppressWarnings("unused")
    private int vDevFaultCate = BasicDataVersion.VERSION_INVALID;
    private int vLCtrlCate = BasicDataVersion.VERSION_INVALID;

    private Activity context = null;
    private DBHelper dbHelper = null;
    private ISyncData iSyncData = null;
    private Handler mHandler = null;

    public static final int SUBMENU_BEGIN = 100;

    public SyncDataThread(Activity context, ISyncData iSyncData) {
        this.context = context;
        this.iSyncData = iSyncData;
        dbHelper = new DBHelper(context);
    }

    @Override
    public void run() {
        mHandler = iSyncData.getHandler();
        if (mHandler == null) {
            new RuntimeException("handle不能为空，空了，同步数据只能失败");
        }
        boolean ret = true;
        for (int time = 0; time < 3; time++) {
            ret = DataVersionCheck(mHandler);
            DataVersionsUtil versionsUtils = new DataVersionsUtil(context, DataVersionsUtil.VERSIONS);
            for (int flag = BasicDataVersion.FLAG_INVALID; flag <= BasicDataVersion.FLAG_LCTRLCATE; flag++) {
                switch (flag) {
                    case BasicDataVersion.FLAG_BROAD: {// 广播信息
                    }
                    break;
                    case BasicDataVersion.FLAG_PARAM: { // 系统参数
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVP = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_PARAM);
//                        int oldVP = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_PARAM);
                        int oldVP = -1;
                        if (newVP != oldVP) {
                            if (SaveAllParamValues()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_PARAM, newVP);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "同步获取系统参数信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_DEPT: {// 部门信息
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVD = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_DEPT);
                        int oldVD = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_DEPT);
                        if (newVD != oldVD) {
                            if (SaveDepartment()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_DEPT, newVD);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "同步获取信部门息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_USER: {// 人员信息
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVU = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_USER);
                        int oldVU = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_USER);
                        if (newVU != oldVU) {
                            if (SaveUser()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_USER, newVU);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "同步获取人员信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_SDRS: { // 部门信息 <->人员信息
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVS = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_SDRS);
                        int oldVS = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_SDRS);
                        if (newVS != oldVS) {
                            if (SaveStaffDeptRelationShip()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_SDRS, newVS);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "同步获取部门和人员信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_GROUP: { // 群组信息
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVG = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_GROUP);
                        int oldVG = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_GROUP);
                        if (newVG != oldVG) {
                            if (SaveGroup()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_GROUP, newVG);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "同步获取群组信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_SCHEDULE: {// 列车时刻表
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVS = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_SCHEDULE);
                        int oldVS = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_SCHEDULE);
                        if (newVS != oldVS) {
                            if (SaveStationSchedule()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_SCHEDULE, newVS);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "同步获取列车时刻表信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_VIASTATION: { // 途经站信息
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVV = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_VIASTATION);
                        int oldVV = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_VIASTATION);
                        if (newVV != oldVV) {
                            if (SaveAllVIAStation()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_VIASTATION, newVV);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "同步获取途经站信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_POSITION: {// 岗位信息
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVP = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_POSITION);
                        int oldVP = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_POSITION);
                        if (newVP != oldVP) {
                            if (SavePosition()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_POSITION, newVP);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "获取岗位信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_WORKSPACE: { // 任务区域信息
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVW = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_WORKSPACE);
                        int oldVW = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_WORKSPACE);
                        if (newVW != oldVW) {
                            if (SaveWorkspace()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_WORKSPACE, newVW);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "获取任务区域信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_PWRS: { // 任务区域 <-> 岗位
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVP = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_PWRS);
                        int oldVP = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_PWRS);
                        if (newVP != oldVP) {
                            if (SavePWRS()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_PWRS, newVP);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "获取任务区域和岗位信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_DEVFAULTCATE: { // 设备报障分类
                        if (DataVersionsUtil.versions == null || DataVersionsUtil.versions.size() <= 0) {
                            continue;
                        }
                        if (!DataVersionsUtil.versions.containsKey(BasicDataVersion.VERSION_PARAM)) {
                            continue;
                        }
                        int newVP = DataVersionsUtil.versions.get(BasicDataVersion.VERSION_PARAM);
//                        int oldVP = versionsUtils.getEditionVersion(BasicDataVersion.VERSION_PARAM);
                        int oldVP = -1;
                        if (newVP != oldVP) {
                            if (SaveDevFaultCate()) {
                                versionsUtils.setVersion(BasicDataVersion.VERSION_PARAM, newVP);
                            } else {
                                ret = false;
                                Message mes = mHandler.obtainMessage();
                                mes.obj = "获取设备报障信息失败";
                                mes.what = 14;
                                mHandler.sendMessage(mes);
                            }
                        }
                    }
                    break;
                    case BasicDataVersion.FLAG_INVALID:
                    default:
                        break;
                }
            }
            if (ret) {
                break;
            }
        }
        if (iSyncData != null)
            iSyncData.SyncEnd(ret);
    }


    // 保存检票状态等参数
    private boolean SaveAllParamValues() {
        dbHelper.clearTable(DBHelper.PARAM_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<Parameter> list = GetAllParamValues();

            if (list == null)
                return false;

            for (Parameter param : list) {
                String sql = "insert into TB_SYS_ParamDetail(ParamCode,ParamValue,DetailName) values ('" + param.code
                        + "','" + param.value + "','" + param.name + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取检票状态等参数
    private ArrayList<Parameter> GetAllParamValues() {
        String paramCode = Parameter.PARAM_CODE_GRAVE + "," + Parameter.PARAM_CODE_REPORT + ","
                + Parameter.PARAM_CODE_TASK + "," + Parameter.PARAM_CODE_TICKET + "," + Parameter.PARAM_CODE_LEAVETYPE
                + "," + Parameter.PARAM_CODE_LEAVESTATUS;

        ArrayList<Parameter> list = new ArrayList<Parameter>();

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("paramCode", paramCode);
        String methodPath = Constant.MP_QUERYINFO;
        String methodName = Constant.MN_GET_PARA_CODE;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vParam = Integer.valueOf(tempVer);
                }
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    Parameter paramDetail = new Parameter();
                    paramDetail.value = JsonUtil.GetJsonObjStringValue(jsonObj, "ParamValue");
                    paramDetail.name = JsonUtil.GetJsonObjStringValue(jsonObj, "DetailName");
                    paramDetail.code = JsonUtil.GetJsonObjStringValue(jsonObj, "ParamCode");
                    list.add(paramDetail);
                }

                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 保存所有人员信息
    private boolean SaveUser() {
        dbHelper.clearStaff(Staff.OWNER_ALL);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<Staff> list = GetStaffContact();
            if (list == null)
                return false;

            ArrayList<CharSequence> contacts = new ArrayList<CharSequence>();
            for (Staff staff : list) {
                String sql = "insert into TB_ORG_Staff(StaffId,StaffName,StaffCode,Mobile,HomeAddress, Type, Owner, Expend1) values ('"
                        + staff.StaffId
                        + "','"
                        + staff.StaffName
                        + "','"
                        + staff.StaffCode
                        + "','"
                        + staff.Mobile
                        + "','"
                        + staff.HomeAddress
                        + "','"
                        + Staff.TYPE_STAFF
                        + "','"
                        + Staff.OWNER_ALL
                        + "','"
                        + staff.Expend1 + "')";
                dbHelper.execSQL(sql);
                contacts.add(staff.Expend1 + "," + staff.StaffName);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            // 与PTT同步联系人
            PTTUtil.updateContact(context, contacts);

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 保存群组信息
    private boolean SaveGroup() {
        dbHelper.clearStaff((int) Property.StaffId);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<Staff> list = GetGroupContact();
            if (list == null)
                return false;

            for (Staff staff : list) {
                String sql = "insert into TB_ORG_Staff(StaffId,StaffName,StaffCode,Mobile,HomeAddress, Type, Owner) values ('"
                        + staff.StaffId
                        + "','"
                        + staff.StaffName
                        + "','"
                        + staff.StaffCode
                        + "','"
                        + staff.Mobile
                        + "','" + staff.HomeAddress + "','" + Staff.TYPE_GROUP + "','" + Property.StaffId + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取用户群组
    private ArrayList<Staff> GetGroupContact() {
        ArrayList<Staff> staffList = new ArrayList<Staff>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("keyword", "");
        String methodPath = Constant.MP_SMS;
        String methodName = Constant.MN_GET_GROUP;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vGroup = Integer.valueOf(tempVer);
                }
                staffList = Staff.ParseFromString(result);
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return staffList;
    }

    // 获取所有人员信息
    private ArrayList<Staff> GetStaffContact() {
        ArrayList<Staff> staffList = new ArrayList<Staff>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_STAFF;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        LogUtil.e("同步联系人结果" + result.toString());


        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {
                    vUser = -1;
                } else {
                    vUser = Integer.valueOf(tempVer);
                }
                staffList = Staff.ParseFromString(result);

                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return staffList;
    }

    // 保存部门组织结构
    private boolean SaveDepartment() {
        dbHelper.clearTable(DBHelper.DEPT_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<Department> list = GetAllDepartment();
            if (list == null)
                return false;
            for (Department dept : list) {
                String sql = "insert into TB_ORG_Dept(DeptId,ParentDeptId,FullName,Expend1, DeptMark,Type) values ('"
                        + dept.DeptId + "','" + dept.ParentDeptId + "','" + dept.FullName + "','" + dept.Expend1
                        + "','" + dept.DeptMark + "','" + dept.type + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取部门组织结构
    private ArrayList<Department> GetAllDepartment() {
        ArrayList<Department> deptList = new ArrayList<>();
        HashMap<String, String> parmValues = new HashMap<>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_DEPT;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return null;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                deptList = Department.ParseFromString(result);
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }
        return deptList;
    }

    // 保存专题广播区域
    private boolean SaveBroadAreaSpecSubject() {
        dbHelper.clearTable(DBHelper.BROAD_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<BroadcastInfo> list = GetBroadAreaSpecSubject();
            if (list == null)
                return false;
            for (BroadcastInfo item : list) {
                String sql = "insert into BroadAreaSpecSubject(id,BroadcastCategory,BroadcastTitle,BroadcastContent,BroadcastArea,IDENO_EQTs) values ('"
                        + item.id
                        + "','"
                        + item.Category
                        + "','"
                        + item.Title
                        + "','"
                        + item.Content.replace("\'", "\'\'") + "','" + item.Area + "','" + item.IDENO_EQTs + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取专题广播区域
    private ArrayList<BroadcastInfo> GetBroadAreaSpecSubject() {
        ArrayList<BroadcastInfo> list = new ArrayList<BroadcastInfo>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", String.valueOf(Property.SessionId));

        String methodPath = Constant.MP_BROADCAST;
        String methodName = Constant.MN_GET_BROADCASTI_NFO;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vBroad = Integer.valueOf(tempVer);
                }
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    BroadcastInfo broadcastInfo = new BroadcastInfo();

                    broadcastInfo.id = JsonUtil.GetJsonObjLongValue(jsonObj, "id");
                    broadcastInfo.Category = JsonUtil.GetJsonObjStringValue(jsonObj, "BroadcastCategory");
                    broadcastInfo.Title = JsonUtil.GetJsonObjStringValue(jsonObj, "BroadcastTitle");
                    broadcastInfo.Content = JsonUtil.GetJsonObjStringValue(jsonObj, "BroadcastContent");
                    broadcastInfo.Area = JsonUtil.GetJsonObjStringValue(jsonObj, "BroadcastArea");
                    broadcastInfo.IDENO_EQTs = JsonUtil.GetJsonObjStringValue(jsonObj, "IDENO_EQTs");
                    list.add(broadcastInfo);
                }
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 列车时刻表
    private boolean SaveStationSchedule() {
        dbHelper.clearTable(DBHelper.TRAINSCHE_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<TrainBase> list = GetStationSchedule();
            if (list == null)
                return false;
            for (TrainBase base : list) {
                String sql = "insert into train_schedule(TRNO_PRO,STRTSTN_TT,TILSTN_TT, DepaTime, StationArrTime, StationDepaTime, StationAttr, Miles) values ('"
                        + base.TRNO_PRO
                        + "','"
                        + base.STRTSTN_TT
                        + "','"
                        + base.TILSTN_TT
                        + "','"
                        + base.DepaTime
                        + "','"
                        + base.StationArrTime
                        + "','"
                        + base.StationDepaTime
                        + "','"
                        + base.StationAttr
                        + "','"
                        + base.Miles
                        + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    //TODO  添加了站名参数。所以必须在这添加参数。不然接口返回异常，数据库没有信息。
    private ArrayList<TrainBase> GetStationSchedule() {
        ArrayList<TrainBase> list = new ArrayList<TrainBase>();
        LoginFrame sup = (LoginFrame) context;
        String[] name = sup.getStationCode2SyncThread();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("stationName", name[0]);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_SCHEDULE;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String Remarks = JsonUtil.GetJsonString(result, "Remarks");
                SharedPreferences sp = context.getSharedPreferences(CommonFunc.CONFIG, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("ScheduleRemarks", Remarks);
                editor.commit();
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vSchedule = Integer.valueOf(tempVer);
                }
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    TrainBase base = new TrainBase();
                    base.TRNO_PRO = JsonUtil.GetJsonObjStringValue(jsonObj, "TRNO_PRO");
                    base.STRTSTN_TT = JsonUtil.GetJsonObjStringValue(jsonObj, "STRTSTN_TT");
                    base.TILSTN_TT = JsonUtil.GetJsonObjStringValue(jsonObj, "TILSTN_TT");
                    base.DepaTime = JsonUtil.GetJsonObjStringValue(jsonObj, "DepaTime");
                    base.StationArrTime = JsonUtil.GetJsonObjStringValue(jsonObj, "StationArrTime");
                    base.StationDepaTime = JsonUtil.GetJsonObjStringValue(jsonObj, "StationDepaTime");
                    base.StationAttr = JsonUtil.GetJsonObjStringValue(jsonObj, "StationAttr");
                    base.Miles = JsonUtil.GetJsonObjIntValue(jsonObj, "Miles");
                    list.add(base);
                }
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 列车时刻表乘务担当信息
    private boolean SaveStation() {
        dbHelper.clearTable(DBHelper.STATION_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<ColorStation> list = GetStation();
            if (list == null)
                return false;
            for (ColorStation staion : list) {
                String sql = "insert into station(Name,Color) values ('" + staion.Name + "','" + staion.Color + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    private ArrayList<ColorStation> GetStation() {
        ArrayList<ColorStation> list = new ArrayList<ColorStation>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_STATION;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vStation = Integer.valueOf(tempVer);
                }
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    ColorStation sation = new ColorStation();
                    sation.Name = JsonUtil.GetJsonObjStringValue(jsonObj, "Name");
                    sation.Color = JsonUtil.GetJsonObjStringValue(jsonObj, "Color");

                    list.add(sation);
                }
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 列车时刻表车型信息
    private boolean SaveTrainType() {
        dbHelper.clearTable(DBHelper.TRAINTYPE_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<TrainType> list = GetTrainType();
            if (list == null)
                return false;
            for (TrainType type : list) {
                String sql = "insert into train_type(Name,Number) values ('" + type.Name + "','" + type.Number + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    //TODO  函数被删除需要修补。
    private ArrayList<TrainType> GetTrainType() {
        ArrayList<TrainType> list = new ArrayList<TrainType>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_TRAINTYPE;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vTType = Integer.valueOf(tempVer);
                }
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    TrainType traintype = new TrainType();
                    traintype.Name = JsonUtil.GetJsonObjStringValue(jsonObj, "Name");
                    traintype.Number = JsonUtil.GetJsonObjStringValue(jsonObj, "Number");

                    list.add(traintype);
                }
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 保存所有途经站信息
    private boolean SaveAllVIAStation() {
        dbHelper.clearTable(DBHelper.VIASTATION_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();

            ArrayList<VIAStation> list = GetAllViaStation();
            if (list == null)
                return false;
            for (VIAStation station : list) {
                String sql = "insert into viastation(ID, TRNO_TT, STRTSTN_TT, TILSTN_TT, STRTTIME_TT, TILTIME_TT, StationOrder, Station, ArrTime, ArrDate, DepaTime, DepaDate, StationAttr, Miles) values ('"
                        + station.ID
                        + "','"
                        + station.TRNO_TT
                        + "','"
                        + station.STRTSTN_TT
                        + "','"
                        + station.TILSTN_TT
                        + "','"
                        + station.STRTTIME_TT
                        + "','"
                        + station.TILTIME_TT
                        + "','"
                        + station.StationOrder
                        + "','"
                        + station.Station
                        + "','"
                        + station.ArrTime
                        + "','"
                        + station.ArrDate
                        + "','"
                        + station.DepaTime
                        + "','"
                        + station.DepaDate
                        + "','"
                        + station.StationAttr
                        + "','"
                        + station.Miles + "')";
                dbHelper.execSQL(sql);
            }

            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取所有途经站信息
    private ArrayList<VIAStation> GetAllViaStation() {
        ArrayList<VIAStation> list = new ArrayList<VIAStation>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_VIASTATION;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vVIAStation = Integer.valueOf(tempVer);
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);

                    VIAStation station = new VIAStation();
                    station.ID = JsonUtil.GetJsonObjIntValue(jsonObj, "ID");
                    station.TRNO_TT = JsonUtil.GetJsonObjStringValue(jsonObj, "TRNO_TT");
                    station.STRTSTN_TT = JsonUtil.GetJsonObjStringValue(jsonObj, "STRTSTN_TT");
                    station.TILSTN_TT = JsonUtil.GetJsonObjStringValue(jsonObj, "TILSTN_TT");
                    station.STRTTIME_TT = JsonUtil.GetJsonObjStringValue(jsonObj, "STRTTIME_TT");
                    station.TILTIME_TT = JsonUtil.GetJsonObjStringValue(jsonObj, "TILTIME_TT");
                    station.StationOrder = JsonUtil.GetJsonObjIntValue(jsonObj, "StationOrder");
                    station.Station = JsonUtil.GetJsonObjStringValue(jsonObj, "Station");
                    station.ArrTime = JsonUtil.GetJsonObjStringValue(jsonObj, "ArrTime");
                    station.ArrDate = JsonUtil.GetJsonObjIntValue(jsonObj, "ArrDate");
                    station.DepaTime = JsonUtil.GetJsonObjStringValue(jsonObj, "DepaTime");
                    station.DepaDate = JsonUtil.GetJsonObjIntValue(jsonObj, "DepaDate");
                    station.StationAttr = JsonUtil.GetJsonObjStringValue(jsonObj, "StationAttr");
                    station.Miles = JsonUtil.GetJsonObjIntValue(jsonObj, "Miles");

                    list.add(station);
                }
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 时刻表查询-关联站
    private boolean SaveLinkStation() {
        dbHelper.clearTable(DBHelper.LINKSTATION_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<LinkStation> list = GetLinkStation();
            if (list == null)
                return false;
            for (LinkStation station : list) {
                String sql = "insert into linkstation(Name, Pinyin, Number) values ('" + station.Name + "','"
                        + station.Pinyin + "','" + station.Num + "')";
                dbHelper.execSQL(sql);
            }

            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    //TODO 获取所有关联站信息
    private ArrayList<LinkStation> GetLinkStation() {
        ArrayList<LinkStation> list = new ArrayList<LinkStation>();


        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_LINKSTATION;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vLinkStation = Integer.valueOf(tempVer);
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);

                    LinkStation station = new LinkStation();
                    station.Name = JsonUtil.GetJsonObjStringValue(jsonObj, "StationName");
                    station.Pinyin = JsonUtil.GetJsonObjStringValue(jsonObj, "StationPy");
                    station.Num = JsonUtil.GetJsonObjIntValue(jsonObj, "Num");

                    list.add(station);
                }
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 保存员工、部门对应关系
    private boolean SaveStaffDeptRelationShip() {
        dbHelper.clearTable(DBHelper.DEPTSTAFF_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<StaffDeptRS> list = GetStaffDeptRelationShip();
            if (list == null)
                return false;
            for (StaffDeptRS obj : list) {
                String sql = "insert into TB_ORG_DeptStaff(Id,DeptId,StaffId) values ('" + obj.Id + "','" + obj.DeptId
                        + "','" + obj.StaffId + "')";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取员工、部门对应关系
    private ArrayList<StaffDeptRS> GetStaffDeptRelationShip() {
        ArrayList<StaffDeptRS> list = new ArrayList<StaffDeptRS>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_STAFFDEPTRELATION;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vSDRS = Integer.valueOf(tempVer);
                }
                list = StaffDeptRS.ParseFromString(result);

                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 保存所有岗位信息
    private boolean SavePosition() {
        dbHelper.clearTable(DBHelper.POSITION_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<TaskPosition> list = GetPosition();
            if (list == null)
                return false;
            for (TaskPosition position : list) {
                String sql = "insert into Position(PId,PositionName,AdvanceMin,DelayMin,PositionCode,StationCode) values ('"
                        + position.PId
                        + "','"
                        + position.PositionName
                        + "','"
                        + position.AdvanceMin
                        + "','"
                        + position.DelayMin + "','" + position.PositionCode + "','" + position.StationCode + "')";
                dbHelper.execSQL(sql);
            }

            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取所有岗位信息
    private ArrayList<TaskPosition> GetPosition() {
        ArrayList<TaskPosition> list = new ArrayList<TaskPosition>();
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_POSITION;

        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vPosition = Integer.valueOf(tempVer);
                }
                list = TaskPosition.ParseFromString(result);
                break;
            case Constant.EXCEPTION:
            default:
                break;
        }

        return list;
    }

    // 保存所有任务区域信息
    private boolean SaveWorkspace() {
        dbHelper.clearTable(DBHelper.WORKSPACE_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<TaskWorkspace> list = GetWorkspace();

            if (list == null)
                return false;

            for (TaskWorkspace workspace : list) {
                String sql = "insert into Worksapce(TwId,Workspace,StationCode) values ('" + workspace.TwId + "','"
                        + workspace.Workspace + "','" + workspace.StationCode + "')";
                dbHelper.execSQL(sql);
            }

            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取所有任务区域信息
    private ArrayList<TaskWorkspace> GetWorkspace() {
        ArrayList<TaskWorkspace> list = new ArrayList<TaskWorkspace>();
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKWORKSPACE;

        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vWorkspace = Integer.valueOf(tempVer);
                }
                list = TaskWorkspace.ParseFromString(result);
                break;
            case Constant.EXCEPTION:
            default:
                break;
        }

        return list;
    }

    // 保存岗位任务区域关联信息
    private boolean SavePWRS() {
        dbHelper.clearTable(DBHelper.PWRS_TABLE_NAME);
        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<TaskPWRS> list = GetPWRS();
            if (list == null)
                return false;
            for (TaskPWRS obj : list) {
                String sql = "insert into PWRS(PwId,TwId,PId,WpType) values ('" + obj.PwId + "','" + obj.TwId + "','"
                        + obj.PId + "','" + obj.WpType + "')";
                dbHelper.execSQL(sql);
            }

            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取岗位任务区域关联信息
    private ArrayList<TaskPWRS> GetPWRS() {
        ArrayList<TaskPWRS> list = new ArrayList<TaskPWRS>();
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKPWRS;

        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                String tempVer = JsonUtil.GetJsonString(result, "Version");
                if (TextUtils.isEmpty(tempVer)) {

                } else {
                    vPWRS = Integer.valueOf(tempVer);
                }
                list = TaskPWRS.ParseFromString(result);
                break;
            case Constant.EXCEPTION:
            default:
                break;
        }

        return list;
    }

    // 保存各站所有任务区域分类
    // private boolean SaveSMWC() {
    // dbHelper.clearTable(DBHelper.PWRS_TABLE_NAME);
    // try {
    // dbHelper.getWritableDatabase().beginTransaction();
    // ArrayList<TaskSMWC> list = GetSMWC();
    // if (list == null)
    // return false;
    // for (TaskSMWC obj : list) {
    // String sql = "insert into SMWC(Id,Text,StationCode) values ('"
    // + obj.Id + "','" + obj.Text + "','"
    // + obj.StationCode + "')";
    // dbHelper.execSQL(sql);
    // }
    //
    // dbHelper.getWritableDatabase().setTransactionSuccessful();
    // return true;
    // } catch (Exception ex) {
    // return false;
    // } finally {
    // dbHelper.getWritableDatabase().endTransaction();
    // }
    // }

    // 获取各站所有任务区域分类
    // private ArrayList<TaskSMWC> GetSMWC() {
    // ArrayList<TaskSMWC> list = new ArrayList<TaskSMWC>();
    // HashMap<String, String> paramValues = new HashMap<String, String>();
    // paramValues.put("sessionId", Property.SessionId);
    // String methodPath = Constant.MP_SPARK;
    // String methodName = Constant.MN_GET_TASKWORKSPACECATE;
    //
    // WebServiceMananger webServiceManager = new WebServiceMananger(context,
    // methodName, paramValues);
    // String result = webServiceManager.OpenConnect(methodPath);
    //
    // if (result == null || result.equals("")) {
    // return null;
    // }
    //
    // int Code = JsonUtil.GetJsonInt(result, "code");
    // switch (Code) {
    // case Constant.NORMAL:
    // vSMWC = JsonUtil.GetJsonInt(result, "version");
    // list = TaskSMWC.ParseFromString(result);
    // break;
    // case Constant.EXCEPTION:
    // default:
    // break;
    // }
    //
    // return list;
    // }

    // 保存设备报障分类信息
    private boolean SaveDevFaultCate() {
        dbHelper.clearTable(DBHelper.DEVFAULTCATE_TABLE_NAME);

        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<DevFaultCate> list = GetDevFaultCate();

            if (list == null)
                return false;
            for (DevFaultCate cate : list) {
                String sql = "insert into " + DBHelper.DEVFAULTCATE_TABLE_NAME + "(" + DBHelper.DEVFAULTCATE_CONFIGID
                        + "," + DBHelper.DEVFAULTCATE_CONFIGKEY + "," + DBHelper.DEVFAULTCATE_CONFIGNAME + ","
                        + DBHelper.DEVFAULTCATE_CONFIGVALUE + "," + DBHelper.DEVFAULTCATE_CODE + ") values ('"
                        + cate.id + "','" + cate.key + "','" + cate.name + "','" + cate.value + "','" + cate.code
                        + "');";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取设备报障分类信息
    private ArrayList<DevFaultCate> GetDevFaultCate() {
        ArrayList<DevFaultCate> list = new ArrayList<DevFaultCate>();

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_DEV_FAULT_CATE;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        //HDZ_LOG
        Message msg = mHandler.obtainMessage();
        msg.what = 10001;
        msg.obj = result;
        mHandler.sendMessage(msg);
        //HDZ_LOG
        if (result == null || result.equals("")) {
            return null;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL_ZERO:
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    try {
                        int j = 0;
                        String objStr = jsonObj.getString("Values");
                        objStr = objStr.replaceAll("'", "\"");
//					JSONObject obj = (JSONObject) jsonObj.getJSONObject("Values");
                        JSONObject obj = new JSONObject(objStr);
                        JSONArray jsonSubArray = obj.getJSONArray("Data");
                        for (j = 0; j < jsonSubArray.length(); j++) {
                            DevFaultCate cateDetail = new DevFaultCate();
                            JSONObject jsonSubObj = (JSONObject) jsonSubArray.opt(j);
//						JSONArray DataArray = jsonSubObj.getJSONArray("Data");
//						int size = DataArray.length();
//						for(int k = 0; k < size; k++){
//							JSONObject base = (JSONObject) DataArray.opt(k);
                            cateDetail.id = JsonUtil.GetJsonObjStringValue(jsonSubObj, "ConfigId");
                            cateDetail.key = JsonUtil.GetJsonObjStringValue(jsonSubObj, "ConfigKey");
                            cateDetail.name = JsonUtil.GetJsonObjStringValue(jsonSubObj, "ConfigName");
                            cateDetail.value = JsonUtil.GetJsonObjStringValue(jsonSubObj, "ConfigValue");
                            cateDetail.code = "DevFaultCate";
                            list.add(cateDetail);
//						}
                        }
                        return list;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("错误原因" + e.toString());
                        return null;
                    }
                }

                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return null;
    }

    // 保存照明控制区域分类信息
    private boolean SaveLampArea() {
        dbHelper.clearTable(DBHelper.LCTRLCATE_TABLE_NAME);

        try {
            dbHelper.getWritableDatabase().beginTransaction();
            ArrayList<LampArea> list = GetLampArea();

            if (list == null)
                return false;

            for (LampArea cate : list) {
                String sql = "insert into " + DBHelper.LCTRLCATE_TABLE_NAME + "(" + DBHelper.LCTRLCATE_ID + ","
                        + DBHelper.LCTRLCATE_AREANAME + ") values ('" + cate.Id + "','" + cate.AreaName + "');";
                dbHelper.execSQL(sql);
            }
            dbHelper.getWritableDatabase().setTransactionSuccessful();

            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            dbHelper.getWritableDatabase().endTransaction();
        }
    }

    // 获取照明控制区域分类信息
    private ArrayList<LampArea> GetLampArea() {
        ArrayList<LampArea> list = new ArrayList<LampArea>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SERVICE;
        String methodName = Constant.MN_GET_LAMPAREA;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                if (jsonArray == null || jsonArray.length() == 0)
                    return list;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    LampArea la = new LampArea();

                    la.Id = JsonUtil.GetJsonObjIntValue(jsonObj, "Id");
                    la.AreaName = JsonUtil.GetJsonObjStringValue(jsonObj, "AreaName");
                    list.add(la);
                }
                break;
            case Constant.EXCEPTION:
            default:
                return null;
        }

        return list;
    }

    // 基础数据版本检测
    private boolean DataVersionCheck(Handler handler) {
        boolean ret = false;
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_GET_DATAVERSION;
        WebServiceManager webServiceManager = new WebServiceManager(context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            return ret;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                VersionInfoResult versionResult = new Gson().fromJson(result, VersionInfoResult.class);
                if (versionResult != null) {
                    List<VersionInfoResult.DataBean> dataList = versionResult.getData();
                    if (dataList != null && dataList.size() > 0) {
                        for (int index = 0; index < dataList.size(); index++) {
                            VersionInfoResult.DataBean data = dataList.get(index);
                            DataVersionsUtil.versions.put(data.getName(), data.getVersion());
                        }
                    }
                }
                ret = true;
                break;
            case Constant.EXCEPTION:
                String errMsg = JsonUtil.GetJsonString(result, "Msg");
                Message mes = mHandler.obtainMessage();
                mes.obj = errMsg;
                mes.what = 14;
                mHandler.sendMessage(mes);
            default:
                break;
        }

        return ret;
    }
}
