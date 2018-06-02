package easyway.Mobile.DefaultReportToRepair;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JSC on 2017/12/4.
 */

public class QueryAllReportRepairBean implements Serializable {
    public static QueryAllReportRepairBean objectFromData(String str) {

        return new Gson().fromJson(str, QueryAllReportRepairBean.class);
    }

    /**
     * MsgType : true
     * Msg :
     * Data : [{"Id":"dc9ab1df-2a3c-4691-9de5-3b971468007c","DeviceType":"电梯","DevicePosition":"额发个","DeviceStatus":2,"Remark":"蛋炒饭","CreateTime":"2017-11-07T17:33:32.133","CreateUser":"1563","ModifyTime":"2017-12-01T14:00:51.007","ModifyUser":"0"},{"Id":"c1e0cf18-328b-4e72-81f8-67d0c9a6de27","DeviceType":"AFC","DevicePosition":"好嘎嘎嘎","DeviceStatus":3,"CompleteTime":"2017-12-01T16:04:14.787","CompleteUser":"0","Remark":"gfvg","A3":"2017-12-01T16:04:20.337","A4":"0","A5":"水电费水电费","CreateTime":"2017-11-07T17:20:21.113","CreateUser":"1563","ModifyTime":"2017-12-01T16:04:20.34","ModifyUser":"0"},{"Id":"9f920401-1f71-4763-9ecc-b857146bcfeb","DeviceType":"AFC","DevicePosition":"就是这里","DeviceStatus":0,"Remark":"算了","CreateTime":"2017-11-07T17:14:59.067","CreateUser":"1563","ModifyTime":"2017-11-07T17:14:59.067","ModifyUser":"1563"},{"Id":"4e4efc46-450f-4f5e-b79f-3e3eb4200c34","DeviceType":"ATM","DevicePosition":"2221111","DeviceStatus":2,"Remark":"3332222","CreateTime":"2017-11-07T17:00:43.56","CreateUser":"1563","ModifyTime":"2017-12-01T14:00:34.137","ModifyUser":"0"},{"Id":"053946c8-6241-4086-90c4-30f7c10a6859","DeviceType":"ATM","DevicePosition":"想你行不行你","DeviceStatus":1,"Remark":"伤心好想好想","A1":"2017-12-01T17:35:23.94","A2":"0","CreateTime":"2017-11-07T11:45:13.287","CreateUser":"1563","ModifyTime":"2017-12-01T17:35:23.953","ModifyUser":"0"},{"Id":"45203c05-ddcb-48ed-a947-e69c4450c734","DeviceType":"AFC","DevicePosition":"谢谢还不下班","DeviceStatus":0,"Remark":"下决心很喜欢","CreateTime":"2017-11-07T11:44:50.427","CreateUser":"1563","ModifyTime":"2017-11-07T11:44:50.427","ModifyUser":"1563"},{"Id":"a448a2b5-8723-4f8a-9322-e325499bd19e","DeviceType":"AFC","DevicePosition":"ixixjx","DeviceStatus":0,"Remark":"是喜欢吃","CreateTime":"2017-11-07T11:44:21.66","CreateUser":"1563","ModifyTime":"2017-11-07T11:44:21.66","ModifyUser":"1563"},{"Id":"11ff5b78-d03d-4cef-8afa-d6d2cb5ca208","DeviceType":"AFC","DevicePosition":"续继续奖学金","DeviceStatus":0,"Remark":"叙叙旧学姐","CreateTime":"2017-11-07T11:43:44.157","CreateUser":"1563","ModifyTime":"2017-11-07T11:43:44.157","ModifyUser":"1563"},{"Id":"4ce78ffb-2478-4265-b978-39c51d84f259","DeviceType":"AFC","DevicePosition":"帅哥喜欢喜欢","DeviceStatus":0,"Remark":"知道很喜欢","CreateTime":"2017-11-07T11:43:19","CreateUser":"1563","ModifyTime":"2017-11-07T11:43:19","ModifyUser":"1563"},{"Id":"d5f3019d-6b56-4cab-b0e9-b01baddbc5fe","DeviceType":"ATM","DevicePosition":"继续继续姐姐","DeviceStatus":3,"CompleteTime":"2017-12-01T15:47:01.117","CompleteUser":"0","Remark":"继续继续奖学金","A1":"2017-12-01T14:05:54.283","A2":"0","A3":"2017-12-01T16:01:05.517","A4":"0","A5":"123123","CreateTime":"2017-11-07T11:43:05.543","CreateUser":"1563","ModifyTime":"2017-12-01T16:01:05.52","ModifyUser":"0"},{"Id":"ab157f15-c26d-4d1d-8f2a-593397df8b92","DeviceType":"电梯","DevicePosition":"北戴河电话","DeviceStatus":2,"Remark":"很喜欢好像就喜欢","CreateTime":"2017-11-07T11:41:34.807","CreateUser":"1563","ModifyTime":"2017-12-01T13:59:01.457","ModifyUser":"0"},{"Id":"332fbcf8-d7ed-4e0e-aca4-0aefd0f4450d","DeviceType":"电梯","DevicePosition":"翻滚吧","DeviceStatus":0,"Remark":"vcvv","CreateTime":"2017-11-06T20:13:44.2","CreateUser":"1563","ModifyTime":"2017-11-06T20:13:44.2","ModifyUser":"1563"},{"Id":"1182b8d0-79d3-42de-8b7d-db29a86cc109","DeviceType":"电梯","DevicePosition":"烦烦烦","DeviceStatus":0,"Remark":"的地方","CreateTime":"2017-11-06T18:14:51.73","CreateUser":"1563","ModifyTime":"2017-11-06T18:14:51.73","ModifyUser":"1563"},{"Id":"2fc775aa-63fb-471e-b66d-dcac9c596e71","DeviceType":"ATM","DevicePosition":"互相喜欢","DeviceStatus":0,"Remark":"都很喜欢下班","CreateTime":"2017-11-06T18:12:11.74","CreateUser":"1563","ModifyTime":"2017-11-06T18:12:11.74","ModifyUser":"1563"},{"Id":"4a2de5b2-088a-47f4-a44a-8efdf80b7fef","DeviceType":"AFC","DevicePosition":"你大包小包","DeviceStatus":1,"Remark":"好像","CreateTime":"2017-11-06T18:09:37.39","CreateUser":"1563","ModifyTime":"2017-11-30T17:33:03.867","ModifyUser":"0"},{"Id":"6594352d-969e-4b82-b6ed-863096e6c057","DeviceType":"AFC","DevicePosition":"bvbb","DeviceStatus":3,"CompleteTime":"2017-12-01T16:02:40.85","CompleteUser":"0","Remark":"不吵架","A1":"2017-12-01T13:43:11.217","A2":"0","CreateTime":"2017-11-06T17:59:41.607","CreateUser":"1563","ModifyTime":"2017-12-01T16:02:40.853","ModifyUser":"0"},{"Id":"7e16ebc7-5c91-47d1-ae06-2ffc6d739e8b","DeviceType":"电梯","DevicePosition":"vchf","DeviceStatus":2,"Remark":"吃大锅饭","CreateTime":"2017-11-06T17:58:25.117","CreateUser":"1563","ModifyTime":"2017-12-01T17:35:42.98","ModifyUser":"0"},{"Id":"126575c7-2c29-4cdb-bcdb-a1770bc4b8f5","DeviceType":"电梯","DevicePosition":"继续继续姐姐","DeviceStatus":1,"Remark":"刺激刺激想你","CreateTime":"2017-11-06T17:49:04.497","CreateUser":"1563","ModifyTime":"2017-11-30T17:30:49.127","ModifyUser":"0"},{"Id":"efe74c87-5831-4f43-a974-d58a20442b99","DeviceType":"电梯","DevicePosition":"vctcxxx","DeviceStatus":1,"Remark":"vdgr","CreateTime":"2017-11-06T17:46:14.933","CreateUser":"1563","ModifyTime":"2017-11-30T17:28:48.793","ModifyUser":"0"},{"Id":"61ebea6a-1811-46b8-93fb-84538d84c906","DeviceType":"电梯","DevicePosition":"vctcxxx","DeviceStatus":2,"Remark":"vdgr","CreateTime":"2017-11-06T17:45:09.837","CreateUser":"1563","ModifyTime":"2017-12-01T14:05:23.023","ModifyUser":"0"},{"Id":"c39d175a-9ce5-4b2c-bdd5-0b151fa5fb0a","DeviceType":"电梯","DevicePosition":"呵呵大家下班","DeviceStatus":1,"Remark":"不对劲放假","CreateTime":"2017-11-06T17:40:35.743","CreateUser":"1563","ModifyTime":"2017-11-30T17:28:36.71","ModifyUser":"0"}]
     * SessionId : 25de9227-1757-4d9a-8737-d2a074106dc5
     * Total : 21
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

    public static class DataBean implements Serializable{
        /**
         * Id : dc9ab1df-2a3c-4691-9de5-3b971468007c
         * DeviceType : 电梯
         * DevicePosition : 额发个
         * DeviceStatus : 2
         * Remark : 蛋炒饭
         * CreateTime : 2017-11-07T17:33:32.133
         * CreateUser : 1563
         * ModifyTime : 2017-12-01T14:00:51.007
         * ModifyUser : 0
         * CompleteTime : 2017-12-01T16:04:14.787
         * CompleteUser : 0
         * A3 : 2017-12-01T16:04:20.337
         * A4 : 0
         * A5 : 水电费水电费
         * A1 : 2017-12-01T17:35:23.94
         * A2 : 0
         */

        private String Id;
        private String DeviceType;
        private String DevicePosition;
        private int DeviceStatus;
        private String Remark;
        private String CreateTime;
        private String CreateUser;
        private String ModifyTime;
        private String ModifyUser;
        private String CompleteTime;
        private String CompleteUser;
        private String A3;
        private String A4;
        private String A5;
        private String A1;
        private String A2;

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getDeviceType() {
            return DeviceType;
        }

        public void setDeviceType(String DeviceType) {
            this.DeviceType = DeviceType;
        }

        public String getDevicePosition() {
            return DevicePosition;
        }

        public void setDevicePosition(String DevicePosition) {
            this.DevicePosition = DevicePosition;
        }

        public int getDeviceStatus() {
            return DeviceStatus;
        }

        public void setDeviceStatus(int DeviceStatus) {
            this.DeviceStatus = DeviceStatus;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getCreateUser() {
            return CreateUser;
        }

        public void setCreateUser(String CreateUser) {
            this.CreateUser = CreateUser;
        }

        public String getModifyTime() {
            return ModifyTime;
        }

        public void setModifyTime(String ModifyTime) {
            this.ModifyTime = ModifyTime;
        }

        public String getModifyUser() {
            return ModifyUser;
        }

        public void setModifyUser(String ModifyUser) {
            this.ModifyUser = ModifyUser;
        }

        public String getCompleteTime() {
            return CompleteTime;
        }

        public void setCompleteTime(String CompleteTime) {
            this.CompleteTime = CompleteTime;
        }

        public String getCompleteUser() {
            return CompleteUser;
        }

        public void setCompleteUser(String CompleteUser) {
            this.CompleteUser = CompleteUser;
        }

        public String getA3() {
            return A3;
        }

        public void setA3(String A3) {
            this.A3 = A3;
        }

        public String getA4() {
            return A4;
        }

        public void setA4(String A4) {
            this.A4 = A4;
        }

        public String getA5() {
            return A5;
        }

        public void setA5(String A5) {
            this.A5 = A5;
        }

        public String getA1() {
            return A1;
        }

        public void setA1(String A1) {
            this.A1 = A1;
        }

        public String getA2() {
            return A2;
        }

        public void setA2(String A2) {
            this.A2 = A2;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "Id='" + Id + '\'' +
                    ", DeviceType='" + DeviceType + '\'' +
                    ", DevicePosition='" + DevicePosition + '\'' +
                    ", DeviceStatus=" + DeviceStatus +
                    ", Remark='" + Remark + '\'' +
                    ", CreateTime='" + CreateTime + '\'' +
                    ", CreateUser='" + CreateUser + '\'' +
                    ", ModifyTime='" + ModifyTime + '\'' +
                    ", ModifyUser='" + ModifyUser + '\'' +
                    ", CompleteTime='" + CompleteTime + '\'' +
                    ", CompleteUser='" + CompleteUser + '\'' +
                    ", A3='" + A3 + '\'' +
                    ", A4='" + A4 + '\'' +
                    ", A5='" + A5 + '\'' +
                    ", A1='" + A1 + '\'' +
                    ", A2='" + A2 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QueryAllReportRepairBean{" +
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
