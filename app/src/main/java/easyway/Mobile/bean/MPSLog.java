package easyway.Mobile.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import easyway.Mobile.Data.TrainGoTo;
import easyway.Mobile.util.JsonUtil;

/**
 * @author wisely
 */

public class MPSLog {


    public long Id;
    public long HId;
    public String PLANDATE_PTTI;
    public String TRNUM_PTTI;
    public String TRNO_PRO;
    public String TRRUN_PRO;
    public String STRTSTN_PRO;
    public String TILSTN_PRO;
    public String DEPADATE_PTTI;
    public String ARRTIMR_PTTI;
    public String ALATETIME_PTTI;
    public String AATECAUSE_PTTI;
    public String ARRTIMR_PTTI_T;
    public String DEPATIME_PTTI;
    public String DLATETIME_PTTI;
    public String DLATECAUSE_PTTI;
    public String DEPATIME_PTTI_T;
    public String LANE_PTTI;
    public String PLATFORM_PTTI;
    public String WAITROOM_PTTI;
    public String INTICKET_PTTI;
    public String OUTTICKET_PTTI;
    public String INCHECKTIME_PTTI;
    public String INSTOPTIME_PTTI;
    public String GRPNO_PTTI;
    public String GRPORDER_PTTI;
    public String STOPSTATES_PTTI;
    public String INTICKETST_PTTI;
    public String TRTYPE;
    public String ALATESTNAME_PTTI;
    public boolean TaskFlag;
    public String StationCode;
    public boolean ReTask;
    public Date ChangeTime;

    public static ArrayList<MPSLog> ParseFromString(String result) {
        ArrayList<MPSLog> list = new ArrayList<MPSLog>();

        if (result == null)
            return list;

        JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
        if (jsonArray == null || jsonArray.length() == 0)
            return list;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = (JSONObject) jsonArray.opt(i);

            MPSLog item = new MPSLog();
            item.Id = JsonUtil.GetJsonObjLongValue(jsonObj,
                    "Id");
            item.HId = JsonUtil.GetJsonObjLongValue(jsonObj,
                    "HId");
            item.PLANDATE_PTTI = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "PLANDATE_PTTI");
            item.TRNUM_PTTI = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "TRNUM_PTTI");
            item.TRNO_PRO = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "TRNO_PRO");
            item.STRTSTN_PRO = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "STRTSTN_PRO");
            item.TILSTN_PRO = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "TILSTN_PRO");
            item.DEPADATE_PTTI = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "DEPADATE_PTTI");
            item.ARRTIMR_PTTI = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "ARRTIMR_PTTI");
            item.ALATESTNAME_PTTI=JsonUtil
                    .GetJsonObjStringValue(jsonObj, "ALATESTNAME_PTTI");
            item.AATECAUSE_PTTI=JsonUtil
                    .GetJsonObjStringValue(jsonObj, "AATECAUSE_PTTI");
            item.ARRTIMR_PTTI_T=JsonUtil
                    .GetJsonObjStringValue(jsonObj, "ARRTIMR_PTTI_T");
            item.DEPATIME_PTTI= JsonUtil
                    .GetJsonObjStringValue(jsonObj, "DEPATIME_PTTI");
            item.DLATETIME_PTTI= JsonUtil
                    .GetJsonObjStringValue(jsonObj, "DLATETIME_PTTI");
            item.DLATECAUSE_PTTI= JsonUtil
                    .GetJsonObjStringValue(jsonObj, "DLATECAUSE_PTTI");
            item.DEPATIME_PTTI_T= JsonUtil
                    .GetJsonObjStringValue(jsonObj, "DEPATIME_PTTI_T");
            item.LANE_PTTI = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "LANE_PTTI");
            item.PLATFORM_PTTI = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "PLATFORM_PTTI");
            item.WAITROOM_PTTI=JsonUtil.GetJsonObjStringValue(
                    jsonObj, "WAITROOM_PTTI");
            item.INTICKET_PTTI = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "INTICKET_PTTI");
            item.OUTTICKET_PTTI = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "OUTTICKET_PTTI");
            item.INTICKETST_PTTI = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "INTICKETST_PTTI");
            item.INSTOPTIME_PTTI = JsonUtil
                    .GetJsonObjStringValue(jsonObj, "INSTOPTIME_PTTI");
            item.GRPNO_PTTI = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "GRPNO_PTTI");
            item.GRPORDER_PTTI= JsonUtil.GetJsonObjStringValue(
                    jsonObj, "GRPORDER_PTTI");
            item.ChangeTime= JsonUtil.GetJsonObjDateValue(
                    jsonObj, "ChangeTime");

            item.StationCode=JsonUtil.GetJsonObjStringValue(
                    jsonObj, "StationCode");

            list.add(item);
        }

        return list;
    }
}
