package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by boy on 2017/5/3.
 */

public class GetTaskActorResult implements Serializable {

    /**
     * MsgType : true
     * Msg :
     * Data : [{"Id":6,"Title":"检票风险点1","Content":"检票措施1","Order":1,"Remark":"检票备注1","Duty":"进站检票口","Creater":"","CreateTime":"2017-04-27 12:29:17","Modifier":"","ModifyTime":"2017-04-27 12:29:17","StationCode":"XAB","DutyType":3},{"Id":8,"Title":"检票风险点2","Content":"检票措施2","Order":2,"Remark":"检票备注2","Duty":"进站检票口","Creater":"","CreateTime":"2017-04-27 12:30:03","Modifier":"","ModifyTime":"2017-04-27 12:30:03","StationCode":"XAB","DutyType":3},{"Id":7,"Title":"检票风险点3","Content":"检票措施3","Order":3,"Remark":"检票备注3","Duty":"进站检票口","Creater":"","CreateTime":"2017-04-27 12:29:41","Modifier":"","ModifyTime":"2017-04-27 12:29:41","StationCode":"XAB","DutyType":3}]
     * SessionId :
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
         * Id : 6
         * Title : 检票风险点1
         * Content : 检票措施1
         * Order : 1
         * Remark : 检票备注1
         * Duty : 进站检票口
         * Creater :
         * CreateTime : 2017-04-27 12:29:17
         * Modifier :
         * ModifyTime : 2017-04-27 12:29:17
         * StationCode : XAB
         * DutyType : 3
         */

        private int Id;
        private String Title;
        private String Content;
        private int Order;
        private String Remark;
        private String Duty;
        private String Creater;
        private String CreateTime;
        private String Modifier;
        private String ModifyTime;
        private String StationCode;
        private int DutyType;

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String Content) {
            this.Content = Content;
        }

        public int getOrder() {
            return Order;
        }

        public void setOrder(int Order) {
            this.Order = Order;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
        }

        public String getDuty() {
            return Duty;
        }

        public void setDuty(String Duty) {
            this.Duty = Duty;
        }

        public String getCreater() {
            return Creater;
        }

        public void setCreater(String Creater) {
            this.Creater = Creater;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getModifier() {
            return Modifier;
        }

        public void setModifier(String Modifier) {
            this.Modifier = Modifier;
        }

        public String getModifyTime() {
            return ModifyTime;
        }

        public void setModifyTime(String ModifyTime) {
            this.ModifyTime = ModifyTime;
        }

        public String getStationCode() {
            return StationCode;
        }

        public void setStationCode(String StationCode) {
            this.StationCode = StationCode;
        }

        public int getDutyType() {
            return DutyType;
        }

        public void setDutyType(int DutyType) {
            this.DutyType = DutyType;
        }
    }
}
