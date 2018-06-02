package easyway.Mobile.DefaultReportToRepair;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JSC on 2017/12/4.
 */

public class RepairPicBean implements Serializable {
    public static RepairPicBean objectFromData(String str) {

        return new Gson().fromJson(str, RepairPicBean.class);
    }

    /**
     * MsgType : true
     * Msg :
     * Data : [{"FileName":"IMG_2848_171201_092226.jpg","LinkUrl":"58.211.125.61//Upload/Device/Repaire/053946c8-6241-4086-90c4-30f7c10a6859/IMG_2848_171201_092226.jpg"},{"FileName":"IMG_2848_171201_092233.jpg","LinkUrl":"58.211.125.61//Upload/Device/Repaire/053946c8-6241-4086-90c4-30f7c10a6859/IMG_2848_171201_092233.jpg"},{"FileName":"IMG_2848_171201_092230.jpg","LinkUrl":"58.211.125.61//Upload/Device/Repaire/053946c8-6241-4086-90c4-30f7c10a6859/IMG_2848_171201_092230.jpg"}]
     * SessionId : bf91135f-6e05-4b14-bfef-d928772604da
     * Total : 3
     * Code : 1000
     * Version :
     */

    private boolean MsgType;
    private String Msg;
    private String SessionId;
    private int Total;
    private int Code;
    private String Version;
    private List<DataBean> Data;

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

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * FileName : IMG_2848_171201_092226.jpg
         * LinkUrl : 58.211.125.61//Upload/Device/Repaire/053946c8-6241-4086-90c4-30f7c10a6859/IMG_2848_171201_092226.jpg
         */

        private String FileName;
        private String LinkUrl;

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String FileName) {
            this.FileName = FileName;
        }

        public String getLinkUrl() {
            return LinkUrl;
        }

        public void setLinkUrl(String LinkUrl) {
            this.LinkUrl = LinkUrl;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "FileName='" + FileName + '\'' +
                    ", LinkUrl='" + LinkUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RepairPicBean{" +
                "MsgType=" + MsgType +
                ", Msg='" + Msg + '\'' +
                ", SessionId='" + SessionId + '\'' +
                ", Total=" + Total +
                ", Code=" + Code +
                ", Version='" + Version + '\'' +
                ", Data=" + Data +
                '}';
    }
}
