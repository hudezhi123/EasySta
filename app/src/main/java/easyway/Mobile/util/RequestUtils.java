package easyway.Mobile.util;

import android.content.Context;

import java.util.HashMap;

import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;

/**
 * Created by boy on 2018/2/6.
 */

public class RequestUtils {

    public static final String KEY_E = "exception";
    public static final String KEY_M = "message";
    public static final String KEY_J = "json";


    private static HashMap<String, Object> getHashMap(String json) {
        return getHashMap(true, "", json);
    }

    private static String getJson(String methodPath, String methodName, HashMap<String, String> paramValues) {
        WebServiceManager webServiceManager = new WebServiceManager(methodName, paramValues);
        return webServiceManager.OpenConnect(methodPath);
    }

    /**
     * 返回封装的HashMap
     *
     * @param noException
     * @param message
     * @param json
     * @return
     */
    private static HashMap<String, Object> getHashMap(boolean noException, String message, String json) {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put(KEY_E, noException);
        resultMap.put(KEY_M, message);
        resultMap.put(KEY_J, json);
        return resultMap;
    }

    /**
     * TODO 用于登陆验证
     *
     * @param userName
     * @param password
     * @return
     */
    private static HashMap<String, Object> Login(String userName, String password) {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("username", userName);
        paramValues.put("password", password);
        paramValues.put("IpAddress", CommonFunc.getLocalIpAddress());
        String temp;
        String key_m = "";
        boolean key_n_e = true;
        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_LOGIN;
        return getHashMap(key_n_e, key_m, getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取站名和站码信息。
     */
    public static HashMap<String, Object> getStationCodeInfoPrepare() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_STATIONCODE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 检测更新
     *
     * @param context
     * @return
     */
    public static HashMap<String, Object> checkUpdate(Context context) {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        String versionName = "";
        paramValues.put("versionId", versionName);
        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_CHECK_UPDATE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取用户权限
     *
     * @return
     */
    public static HashMap<String, Object> LoadUIPermission() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_PERMISSION;
        String methodName = Constant.MN_GET_PERMISSION;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取用户离线时未接收的消息条数
     *
     * @return
     */
    public static HashMap<String, Object> GetUnReadMessage() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("staffId", Long.toString(Property.StaffId));
        String methodPath = Constant.MP_SMS;
        String methodName = Constant.MN_GET_UNREAD_MESSAGE;
        WebServiceManager webServiceManager = new WebServiceManager(methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        return getHashMap(result);
    }

    /**
     * TODO 检测当前人员是否具有巡检权限
     *
     * @return
     */
    public static HashMap<String, Object> CheckIsValidStaff() {
        HashMap<String, String> paramValue = new HashMap<>();
        paramValue.put("sessionId", Property.SessionId);
        paramValue.put("stationCode", Property.StationCode);
        String methodName = Constant.PATROL_VALID_STAFF;
        String methodPath = Constant.MP_PATROL;
        WebServiceManager service = new WebServiceManager(methodName, paramValue);
        String result = service.OpenConnect(methodPath);
        return getHashMap(result);
    }

    /**
     * TODO 检测是否为带巡检的对象
     *
     * @return
     */
    public static HashMap<String, Object> CheckIsWait2Task(HashMap<String, String> paramValue) {
        String methodName = Constant.PATROL_IS_TASK;
        String methodPath = Constant.MP_PATROL;
        WebServiceManager service = new WebServiceManager(methodName, paramValue);
        String result = service.OpenConnect(methodPath);
        return getHashMap(result);
    }

    /**
     * TODO 上传巡检任务
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> UploadDeviceTask(HashMap<String, String> paramValues) {
        String methodName = Constant.PATROL_SUBMIT_TASK;
        String methodPath = Constant.MP_PATROL;
        WebServiceManager service = new WebServiceManager(methodName, paramValues);
        String result = service.OpenConnect(methodPath);
        return getHashMap(result);
    }


    /**
     * TODO 获取客运日志列表
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetPTLogList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.SEARCHPTT_WORKLOG;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 更新客运日志
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> UpdatePTLog(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.UPDATEPTT_WORKLOG;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 提交客运日志
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> SubmitPTLog(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.INSERTPTT_WORKLOG;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取售票日志
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetSTLog(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.SEARCH_TICKET_LOG;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 更新售票日志
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> UpdateSTLog(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.UPDATE_TICKET_LOG;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 提交售票日志
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> SubmitSTLog(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.INSERT_TICKET_LOG;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取上报类型和相关参数  (故障报修)
     *
     * @return
     */
    public static HashMap<String, Object> GetReportParams() {
        HashMap<String, String> paramValues = new HashMap<>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("StationCode", Property.StationCode);
        String methodName = Constant.GET_REPAIR_TYPES;
        String methodPath = Constant.MP_REPAIR;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 上报设备故障报告   (故障报修)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> SubmitDevReport(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_REPAIR;
        String methodName = Constant.ADD_REPAIR_INFO;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取设备故障报告列表    (故障报修)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetDevReportList(HashMap<String, String> paramValues) {
        String methodName = Constant.QUERY_REPAIR_LIST;
        String methodPath = Constant.MP_REPAIR;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取所有待办工作  (所有、重点、完成)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetAllWaitToDo(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASK;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取正在做的工作  (到岗)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetDoingWork(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_RUNNING_TASK;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }


    /**
     * TODO 获取检票口列表  (到发通告)
     *
     * @return
     */
    public static HashMap<String, Object> GetCheckPortList() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("stationCode", Property.StationCode);
        paramValues.put("pId", "JP");
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKWORKSPACEBYPID;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取站台列表   (到发通告)
     *
     * @return
     */
    public static HashMap<String, Object> GetPlatformList() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("stationCode", Property.StationCode);
        paramValues.put("pId", "JP");
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKWORKSPACEBYPID;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取所有到发通告   (所有)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetAllAG(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_TRAINGO;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取到发通告    (始发)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetStartAG(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_TRAINGOARR;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取到发通告    (终到)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetEndAG(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_TRAINGODEP;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取到发通告   (途径)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetViaAG(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_TRAINGOAPP;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取到发通告  (晚点)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetLateAG(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_TRAINGOLATE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 股道占用
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetTrackOccupyList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_LANEOCCUPANCY;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 股道占用   (某一股道详细信息)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetTrackOccupyDetail(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_LANEALLMPS;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取在线人员列表
     *
     * @return
     */
    public static HashMap<String, Object> GetOnlineStaff() {
        HashMap<String, String> paramValues = new HashMap<>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("stationCode", Property.StationCode);
        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_GET_ONLINE_USER;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取站码列表
     *
     * @return
     */
    public static HashMap<String, Object> GetStationCode() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_STATIONCODE;
        paramValues.put("sessionId", Property.SessionId);
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 提交故障报表   (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> SubmitFaultReport(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_ADD_DEV_FAULT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }


    /**
     * TODO 获取故障报告列表    (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetDevReportedList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_DEV_FAULT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 开始维修     (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> StartRepairDev(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_BEGINREPAIRDEVFAULT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 停止维修    (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> StopRepairDev(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_SUSPENDREPAIRDEVFAULT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 继续维修     (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> ContinueRepairDev(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_RESUMEREPAIRDEVFAULT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 确认没有维修     (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> ConfirmNotRepairedDev(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_NotConfirmDevFault;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 确认修复     (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> ConfirmRepairDev(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_ok_FAULT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 完成维修     (故障上报)   最终确认
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> CompleteRepair(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_Repair_FAULT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取设备组     (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetDevInGroup(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_DEVINGROUP;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取设备类别     (故障上报)
     *
     * @return
     */
    public static HashMap<String, Object> GetAllDevCate() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("stationCode", Property.StationCode);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_ALLDEVCATE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取供应商     (故障上报)
     *
     * @return
     */
    public static HashMap<String, Object> GetDevSupplier() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("stationCode", Property.OwnStation.Code);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_ALLDEVSUPPLIER;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 提交维修记录      (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> SubmitRepairLog(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_SAVE_DEVREPAIRLOG;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 保存损坏的备品备件     (故障上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> SaveDamageParts(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_SAVE_BROKENDEVSPAREPARTS;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 上传图片
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> UploadImg(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_SAVEDEVISSUEREPAIRIMAGE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 上报现场情况    (情况上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> ReportLiveCase(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_SAVE_LIVECASE_REPORT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取现场情况报告列表     (情况上报)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetLiveCaseList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_LIVECASE_REPORT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取所有重点任务信息    (重点任务)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetPointTaskList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKMAIN;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 发布任务     (重点任务)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> PublishTask(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_PUBLISH_TASK;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 取消任务   (重点任务)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> CancelTask(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_DELETE_TASK;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取子任务列表    (重点任务)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetChildTaskList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKACTOR;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 取消子任务     (重点任务)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> CancelChildTask(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_DELETE_TASKITEM;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取文档类型   (站内规章)
     *
     * @return
     */
    public static HashMap<String, Object> GetRuleFileType() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("key", "RegulationsType");
        paramValues.put("xPath", "");
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_CMKEYVALUEXML;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取规章制度文件详细信息    (站内规章)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetRulesDetailList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_REGULATIONS;
        String methodName = Constant.MN_GET_REGULATIONSALL;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }


    /**
     * TODO 获取巡检数据   (客运巡检)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetPTPatrol(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_PATROL;
        String methodName = Constant.MN_GET_PATROL;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 提交危险品报表    (危险品登记)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> SubmitDPReport(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.DANGEROUS_SAVE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取已经提交的报表列表    (危险品登记)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetDPReportList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.DANGEROUS_FIND;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取未完成的上水任务   (上水管理)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetUnDoneWaterTaskList(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_WATERTASK;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 申请和完成上水工作    (上水管理)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> RequestAndCompleteWaterTask(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_APPLYWATER;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取已经完成的上水工作    (上水管理)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetCompleteWaterTask(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_WATERTASK;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 抽取污水    (吸污功能)
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> ExtractDirtyWater(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.SEWAGEWATER_METHOD;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO  获取检票状态等参数
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetCheckTicketParam(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_QUERYINFO;
        String methodName = Constant.MN_GET_PARA_CODE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取用户群组
     *
     * @return
     */
    public static HashMap<String, Object> GetUserContactGroup() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("keyword", "");
        String methodPath = Constant.MP_SMS;
        String methodName = Constant.MN_GET_GROUP;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取人员信息
     *
     * @return
     */
    public static HashMap<String, Object> GetUserInfo() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_STAFF;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取组织结构信息
     *
     * @return
     */
    public static HashMap<String, Object> GetDeptStruct() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_DEPT;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取专题广播区域
     *
     * @return
     */
    public static HashMap<String, Object> GetThemeBroadcastArea() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", String.valueOf(Property.SessionId));
        String methodPath = Constant.MP_BROADCAST;
        String methodName = Constant.MN_GET_BROADCASTI_NFO;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取列车时刻表
     *
     * @param paramValues
     * @return
     */
    public static HashMap<String, Object> GetTrainSchedule(HashMap<String, String> paramValues) {
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_SCHEDULE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    //syncDataThread  getStation   maybe  not in use
    public static HashMap<String, Object> GetStation() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_STATION;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取所有途径站的信息
     *
     * @return
     */
    public static HashMap<String, Object> GetViaStationInfo() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_VIASTATION;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取关联站的信息
     *
     * @return
     */
    public static HashMap<String, Object> GetLinkStationInfo() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_LINKSTATION;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取员工、部门对应关系
     *
     * @return
     */
    public static HashMap<String, Object> GetDep2StaffRelation() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_STAFFDEPTRELATION;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取所有岗位信息
     *
     * @return
     */
    public static HashMap<String, Object> GetPositionInfo() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_POSITION;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 保存所有任务区域信息
     *
     * @return
     */
    public static HashMap<String, Object> GetAllWorkSpaceInfo() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKWORKSPACE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 保存岗位任务区域关联信息
     *
     * @return
     */
    public static HashMap<String, Object> GetAllPWRInfo() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKPWRS;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }

    /**
     * TODO 获取设备报障分类信息
     *
     * @return
     */
    public static HashMap<String, Object> GetDRCate() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_DEV_FAULT_CATE;
        return getHashMap(getJson(methodPath, methodName, paramValues));
    }
}
