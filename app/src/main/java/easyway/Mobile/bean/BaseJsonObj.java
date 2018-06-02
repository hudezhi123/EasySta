package easyway.Mobile.bean;

/**
 * Created by boy on 2018/2/5.
 */

public class BaseJsonObj {
    private int Code;
    private Object Data;
    private String Msg;
    private boolean MsgType;
    private String SessionId;
    private int Total;
    private String Version;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public boolean isMsgType() {
        return MsgType;
    }

    public void setMsgType(boolean msgType) {
        MsgType = msgType;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    @Override
    public String toString() {
        return "BaseJsonObj{" +
                "Code=" + Code +
                ", Data=" + Data +
                ", Msg='" + Msg + '\'' +
                ", MsgType=" + MsgType +
                ", SessionId='" + SessionId + '\'' +
                ", Total=" + Total +
                ", Version='" + Version + '\'' +
                '}';
    }
}
