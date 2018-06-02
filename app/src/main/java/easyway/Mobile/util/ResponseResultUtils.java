package easyway.Mobile.util;

import android.os.Message;
import android.text.TextUtils;

import org.json.JSONArray;

import easyway.Mobile.Data.ResultCode;
import easyway.Mobile.Net.Constant;

/**
 * Created by boy on 2017/12/5.
 */

public class ResponseResultUtils {

    /**
     * *****************************************************
     * Key     *       Type     *          value           *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * MsgType       boolean
     * Msg           String
     * Data          Object
     * SessionId     String
     * Total          int
     * Code           int
     * Version       String
     *
     * @param jsonResult
     * @return
     */


    private static int parseResultType(String jsonResult) {
        if (TextUtils.isEmpty(jsonResult)) {   //返回的json为空
            return ResultCode.RESULT_IS_NULL;
        } else {
            if (jsonResult.contains("Code")) {
                int code = JsonUtil.GetJsonInt(jsonResult, "Code");
                if (code == 1000) {
                    return ResultCode.CODE_IS_1000;
                } else if (code == Constant.EXCEPTION) {
                    return ResultCode.CODE_IS_911;
                } else if (code == Constant.NORMAL_ZERO) {
                    // TODO: 2017/12/5
                    return ResultCode.CODE_IS_ZERO;
                } else {
                    return ResultCode.CODE_IS_USELESS;
                }
            } else {
                return ResultCode.RESULT_IS_VALUE;
            }
        }

    }

    public static void parseJsonResult(String jsonResult, Message msg) {
        int type = parseResultType(jsonResult);
        switch (type) {
            case ResultCode.RESULT_IS_NULL:
                String exc = "未接收到返回结果（可能原因：1.Main线程执行耗时操作 2.接口参数或者接口Key值异常）";
                msg.what = ResultCode.RESULT_IS_NULL;
                msg.obj = exc;
                break;
            case ResultCode.RESULT_IS_VALUE:
                msg.what = ResultCode.RESULT_IS_VALUE;
                msg.obj = jsonResult;
                break;
            case ResultCode.CODE_IS_1000:
                JSONArray arrayValue = JsonUtil.GetJsonArray(jsonResult, "Data");
                if (arrayValue == null) {
                    String value = JsonUtil.GetJsonString(jsonResult, "Data");
                    if (TextUtils.isEmpty(value)) {
                        msg.what = ResultCode.DATA_IS_NULL;
                        msg.obj = JsonUtil.GetJsonString(jsonResult, "Msg");
                    } else {
                        msg.obj = value;
                        msg.what = ResultCode.DATA_IS_STRING;
                    }
                } else if (arrayValue.length() == 0) {
                    msg.what = ResultCode.DATA_IS_ARRAY;
                    msg.obj = arrayValue;
                } else {
                    msg.what = ResultCode.DATA_IS_ARRAY;
                    msg.obj = arrayValue;
                }
                break;
            case ResultCode.CODE_IS_911:
                msg.obj = JsonUtil.GetJsonString(jsonResult, "Msg");
                msg.what = ResultCode.CODE_IS_911;
                break;
            case ResultCode.CODE_IS_ZERO:

                break;
            case ResultCode.CODE_IS_USELESS:
                msg.obj = "接口异常";
                msg.what = ResultCode.CODE_IS_USELESS;
                break;
        }
    }
}
