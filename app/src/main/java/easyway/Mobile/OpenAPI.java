package easyway.Mobile;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.bean.MPSLog;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.JsonUtil;

/**
 * @author wisely
 */

public class OpenAPI {

    public interface Callback<T>{
        void onSuccess(T t);
        void onFail();
    }

    public static abstract class AbstractCallback<T> implements Callback <T>{
        public void onFail(){

        }
    }

    /**
     * 获取到发变更
     */
    public static void loadChange(ActivityEx context,String trnoPro,Callback callback) {

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", Property.SessionId);
//        params.put("sessionId","768A47A3-F0A4-4AA60-B983-7A7576F62B39");
        params.put("stationCode", Property.OwnStation.Code);
//        params.put("stationCode", "XAB");
        params.put("trnoPro", trnoPro);
//        params.put("trnoPro","G1921");
        params.put("planDate", DateUtil.getTodayTimeStamp());
//        params.put("planDate","2017-02-03");


        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_MPSLog;

        WebServiceManager webServiceMananger = new WebServiceManager(context.getApplicationContext(), methodName, params);
        String result = webServiceMananger.OpenConnect(methodPath);
        if (TextUtils.isEmpty(result)) {
            String errMsg = context.getString(R.string.exp_getdata);
            context.showToast(errMsg);
            return;
        }


        int code = JsonUtil.GetJsonInt(result, "Code");
        switch (code) {
            case Constant.NORMAL:
                JSONArray data = JsonUtil.GetJsonArray(result, "Data");
                List<MPSLog> list = parse2MPSLogList(data);
                if (list != null && list.size() > 0){
                    callback.onSuccess(list.get(0));
                } else {
                    callback.onSuccess(null);
                }

                break;

            case Constant.EXCEPTION:
            default:
                String errMsg = JsonUtil.GetJsonString(result, "Msg");
                context.showToast(errMsg);
                break;
        }

    }

    private static List<MPSLog> parse2MPSLogList(JSONArray jsonArray) {

        List<MPSLog> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.opt(i);

            MPSLog mpsLog = new MPSLog();
            mpsLog.Id = JsonUtil.GetJsonObjLongValue(jsonObject, "Id");
            mpsLog.HId = JsonUtil.GetJsonObjLongValue(jsonObject, "HId");
            mpsLog.PLANDATE_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "PLANDATE_PTTI");
            mpsLog.TRNUM_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "TRNUM_PTTI");
            mpsLog.TRNO_PRO = JsonUtil.GetJsonObjStringValue(jsonObject, "TRNO_PRO");
            mpsLog.TRRUN_PRO = JsonUtil.GetJsonObjStringValue(jsonObject, "TRRUN_PRO");
            mpsLog.STRTSTN_PRO = JsonUtil.GetJsonObjStringValue(jsonObject, "STRTSTN_PRO");
            mpsLog.TILSTN_PRO = JsonUtil.GetJsonObjStringValue(jsonObject, "TILSTN_PRO");
            mpsLog.DEPADATE_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "DEPADATE_PTTI");
            mpsLog.ARRTIMR_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "ARRTIMR_PTTI");
            mpsLog.ALATETIME_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "ALATETIME_PTTI");
            mpsLog.AATECAUSE_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "AATECAUSE_PTTI");
            mpsLog.ARRTIMR_PTTI_T = JsonUtil.GetJsonObjStringValue(jsonObject, "ARRTIMR_PTTI_T");
            mpsLog.DEPATIME_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "DEPATIME_PTTI");
            mpsLog.DLATETIME_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "DLATETIME_PTTI");
            mpsLog.DLATECAUSE_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "DLATECAUSE_PTTI");
            mpsLog.DEPATIME_PTTI_T = JsonUtil.GetJsonObjStringValue(jsonObject, "DEPATIME_PTTI_T");
            mpsLog.LANE_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "LANE_PTTI");
            mpsLog.PLATFORM_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "PLATFORM_PTTI");
            mpsLog.WAITROOM_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "WAITROOM_PTTI");
            mpsLog.INTICKET_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "INTICKET_PTTI");
            mpsLog.OUTTICKET_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "OUTTICKET_PTTI");
            mpsLog.INCHECKTIME_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "INCHECKTIME_PTTI");
            mpsLog.INSTOPTIME_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "INSTOPTIME_PTTI");
            mpsLog.GRPNO_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "GRPNO_PTTI");
            mpsLog.GRPORDER_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "GRPORDER_PTTI");
            mpsLog.STOPSTATES_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "STOPSTATES_PTTI");
            mpsLog.INTICKETST_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "INTICKETST_PTTI");
            mpsLog.TRTYPE = JsonUtil.GetJsonObjStringValue(jsonObject, "TRTYPE");
            mpsLog.ALATESTNAME_PTTI = JsonUtil.GetJsonObjStringValue(jsonObject, "ALATESTNAME_PTTI");
            mpsLog.StationCode = JsonUtil.GetJsonObjStringValue(jsonObject, "StationCode");
            mpsLog.ChangeTime = JsonUtil.GetJsonObjDateValue(jsonObject, "ChangeTime");
            mpsLog.TaskFlag = JsonUtil.GetJsonObjBooleanValue(jsonObject, "TaskFlag");
            mpsLog.ReTask = JsonUtil.GetJsonObjBooleanValue(jsonObject, "ReTask");

            list.add(mpsLog);
        }

        return list;

    }
}
