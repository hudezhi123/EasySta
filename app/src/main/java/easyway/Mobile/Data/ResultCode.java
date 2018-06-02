package easyway.Mobile.Data;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by boy on 2017/12/7.
 */

public class ResultCode {
    public static final int RESULT_IS_NULL = 0;
    public static final int RESULT_IS_VALUE = 1;   // 返回的是 reportId   以及 true 或者 false
    public static final int CODE_IS_1000 = 2;
    public static final int CODE_IS_911 = 3;
    public static final int CODE_IS_ZERO = 4;
    public static final int CODE_IS_USELESS = 1200;
    public static final int MSG_TYPE_IS_TRUE = 5;
    public static final int MSG_TYPE_IS_FALSE = 6;
    public static final int SESSION_ID_IS_NULL = 7;
    public static final int SESSION_ID_IS_NOT_NULL = 8;
    public static final int DATA_IS_ARRAY = 9;
    public static final int DATA_IS_STRING = 10;
    public static final int DATA_IS_NULL = 11;
    public static final int MSG_IS_NULL = 12;
    public static final int MSG_IS_STRING = 13;

    private Handler mHandler;
    private Context mContext;

    public ResultCode(Context context) {
        mContext = context;
    }

    public void init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String tips = "";
                switch (msg.what) {
                    case RESULT_IS_NULL:
                        tips = "请检查你的接口名称或者字段名是否有误！";
                        break;
                    case RESULT_IS_VALUE:
                        tips = "Json本身作为返回值！";
                        break;
                    case CODE_IS_1000:
                        tips = "Code is 1000";
                        break;
                    case CODE_IS_911:
                        tips = "Code is 911";
                        break;
                    case CODE_IS_ZERO:
                        tips = "Code is 0";
                        break;
                    case MSG_TYPE_IS_TRUE:
                        tips = "MsgType is true";
                        break;
                    case MSG_TYPE_IS_FALSE:
                        tips = "MsgType is false";
                        break;
                    case SESSION_ID_IS_NULL:
                        tips = "sessionId是空的";
                        break;
                    case SESSION_ID_IS_NOT_NULL:
                        tips = "sessionId不是空的";
                        break;
                    case DATA_IS_ARRAY:
                        tips = "数据区域中的值时Json数组，并且不为空！";
                        break;
                    case DATA_IS_STRING:
                        tips = "数据区域中数据未字符串！";
                        break;
                    case MSG_IS_NULL:
                        tips = "msg is null";
                        break;
                    case MSG_IS_STRING:
                        tips = "msg is string";
                        break;
                }
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
            }

        };
    }

    public Handler getHandler() {
        return mHandler;
    }

}
