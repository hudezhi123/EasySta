package easyway.Mobile.Data;

import java.io.Serializable;

/**
 * Created by boy on 2017/9/20.
 */

public class FinishRepairResult implements Serializable {

    /**
     * MsgType : true
     * Msg :
     * Data : 完成维修成功
     * SessionId :
     * Total : 1
     * Code : 0
     * Version : 0
     */

    private boolean MsgType;
    private String Msg;
    private String Data;
    private String SessionId;
    private int Total;
    private int Code;
    private String Version;

    public boolean isMsgType() {
        return MsgType;
    }

    public void setMsgType(boolean MsgType) {
        this.MsgType = MsgType;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public String getData() {
        return Data;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String SessionId) {
        this.SessionId = SessionId;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }
}
