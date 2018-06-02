package easyway.Mobile.PassengerTrafficLog;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JSC on 2017/11/27.
 */

public class SearchPtlLogBean implements Serializable {

    /**
     * MsgType : true
     * Msg :
     * Data : [{"ID":5,"Classes":"A","DutyOfficer":"哦哦哦","TicketsPerson":0,"TicketsMoney":0,"WeightNum":0,"Weights":0,"WeightMoney":0,"DangerPerson":0,"DangerNum":0,"SellTicketNum":0,"SellTicketMoney":0,"SellTicketOverflow":"0","GoodDeedPerson":0,"GoodDeedNum":0,"ReTroublePerson":0,"ReTroubleNum":0,"TroublePerson":0,"TroubleNum":0,"SuperiorRemark":"无","MainWork":"无","WorkRemark":"正常","MatterRemark":"正常","WorkDate":"2017-11-27T09:10:21","WorkTime":"2017-11-27T09:10:21","CreateDate":"2017-11-08T10:33:26"}]
     * SessionId :
     * Total : 1
     * Code : 0
     * Version : 0
     */

    public static SearchPtlLogBean objectFromData(String str) {

        return new Gson().fromJson(str, SearchPtlLogBean.class);
    }

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

    @Override
    public String toString() {
        return "SearchPtlLogBean{" +
                "MsgType=" + MsgType +
                ", Msg='" + Msg + '\'' +
                ", SessionId='" + SessionId + '\'' +
                ", Total=" + Total +
                ", Code=" + Code +
                ", Version='" + Version + '\'' +
                ", Data=" + Data +
                '}';
    }

    public static class DataBean implements Serializable{
        /**
         * ID : 5
         * Classes : A
         * DutyOfficer : 哦哦哦
         * TicketsPerson : 0
         * TicketsMoney : 0.0
         * WeightNum : 0
         * Weights : 0.0
         * WeightMoney : 0.0
         * DangerPerson : 0
         * DangerNum : 0
         * SellTicketNum : 0
         * SellTicketMoney : 0.0
         * SellTicketOverflow : 0
         * GoodDeedPerson : 0
         * GoodDeedNum : 0
         * ReTroublePerson : 0
         * ReTroubleNum : 0
         * TroublePerson : 0
         * TroubleNum : 0
         * SuperiorRemark : 无
         * MainWork : 无
         * WorkRemark : 正常
         * MatterRemark : 正常
         * WorkDate : 2017-11-27T09:10:21
         * WorkTime : 2017-11-27T09:10:21
         * CreateDate : 2017-11-08T10:33:26
         * Weather
         */

        private int ID;
        private String Classes;
        private String DutyOfficer;
        private int TicketsPerson;
        private double TicketsMoney;
        private int WeightNum;
        private double Weights;
        private double WeightMoney;
        private int DangerPerson;
        private int DangerNum;
        private int SellTicketNum;
        private double SellTicketMoney;
        private String SellTicketOverflow;
        private int GoodDeedPerson;
        private int GoodDeedNum;
        private int ReTroublePerson;
        private int ReTroubleNum;
        private int TroublePerson;
        private int TroubleNum;
        private String SuperiorRemark;
        private String MainWork;
        private String WorkRemark;
        private String MatterRemark;
        private String WorkDate;
        private String WorkTime;
        private String CreateDate;
        private String Weather;

        public String getWeather() {
            return Weather;
        }

        public void setWeather(String weather) {
            Weather = weather;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getClasses() {
            return Classes;
        }

        public void setClasses(String Classes) {
            this.Classes = Classes;
        }

        public String getDutyOfficer() {
            return DutyOfficer;
        }

        public void setDutyOfficer(String DutyOfficer) {
            this.DutyOfficer = DutyOfficer;
        }

        public int getTicketsPerson() {
            return TicketsPerson;
        }

        public void setTicketsPerson(int TicketsPerson) {
            this.TicketsPerson = TicketsPerson;
        }

        public double getTicketsMoney() {
            return TicketsMoney;
        }

        public void setTicketsMoney(double TicketsMoney) {
            this.TicketsMoney = TicketsMoney;
        }

        public int getWeightNum() {
            return WeightNum;
        }

        public void setWeightNum(int WeightNum) {
            this.WeightNum = WeightNum;
        }

        public double getWeights() {
            return Weights;
        }

        public void setWeights(double Weights) {
            this.Weights = Weights;
        }

        public double getWeightMoney() {
            return WeightMoney;
        }

        public void setWeightMoney(double WeightMoney) {
            this.WeightMoney = WeightMoney;
        }

        public int getDangerPerson() {
            return DangerPerson;
        }

        public void setDangerPerson(int DangerPerson) {
            this.DangerPerson = DangerPerson;
        }

        public int getDangerNum() {
            return DangerNum;
        }

        public void setDangerNum(int DangerNum) {
            this.DangerNum = DangerNum;
        }

        public int getSellTicketNum() {
            return SellTicketNum;
        }

        public void setSellTicketNum(int SellTicketNum) {
            this.SellTicketNum = SellTicketNum;
        }

        public double getSellTicketMoney() {
            return SellTicketMoney;
        }

        public void setSellTicketMoney(double SellTicketMoney) {
            this.SellTicketMoney = SellTicketMoney;
        }

        public String getSellTicketOverflow() {
            return SellTicketOverflow;
        }

        public void setSellTicketOverflow(String SellTicketOverflow) {
            this.SellTicketOverflow = SellTicketOverflow;
        }

        public int getGoodDeedPerson() {
            return GoodDeedPerson;
        }

        public void setGoodDeedPerson(int GoodDeedPerson) {
            this.GoodDeedPerson = GoodDeedPerson;
        }

        public int getGoodDeedNum() {
            return GoodDeedNum;
        }

        public void setGoodDeedNum(int GoodDeedNum) {
            this.GoodDeedNum = GoodDeedNum;
        }

        public int getReTroublePerson() {
            return ReTroublePerson;
        }

        public void setReTroublePerson(int ReTroublePerson) {
            this.ReTroublePerson = ReTroublePerson;
        }

        public int getReTroubleNum() {
            return ReTroubleNum;
        }

        public void setReTroubleNum(int ReTroubleNum) {
            this.ReTroubleNum = ReTroubleNum;
        }

        public int getTroublePerson() {
            return TroublePerson;
        }

        public void setTroublePerson(int TroublePerson) {
            this.TroublePerson = TroublePerson;
        }

        public int getTroubleNum() {
            return TroubleNum;
        }

        public void setTroubleNum(int TroubleNum) {
            this.TroubleNum = TroubleNum;
        }

        public String getSuperiorRemark() {
            return SuperiorRemark;
        }

        public void setSuperiorRemark(String SuperiorRemark) {
            this.SuperiorRemark = SuperiorRemark;
        }

        public String getMainWork() {
            return MainWork;
        }

        public void setMainWork(String MainWork) {
            this.MainWork = MainWork;
        }

        public String getWorkRemark() {
            return WorkRemark;
        }

        public void setWorkRemark(String WorkRemark) {
            this.WorkRemark = WorkRemark;
        }

        public String getMatterRemark() {
            return MatterRemark;
        }

        public void setMatterRemark(String MatterRemark) {
            this.MatterRemark = MatterRemark;
        }

        public String getWorkDate() {
            return WorkDate;
        }

        public void setWorkDate(String WorkDate) {
            this.WorkDate = WorkDate;
        }

        public String getWorkTime() {
            return WorkTime;
        }

        public void setWorkTime(String WorkTime) {
            this.WorkTime = WorkTime;
        }

        public String getCreateDate() {
            return CreateDate;
        }

        public void setCreateDate(String CreateDate) {
            this.CreateDate = CreateDate;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "ID=" + ID +
                    ", Classes='" + Classes + '\'' +
                    ", DutyOfficer='" + DutyOfficer + '\'' +
                    ", TicketsPerson=" + TicketsPerson +
                    ", TicketsMoney=" + TicketsMoney +
                    ", WeightNum=" + WeightNum +
                    ", Weights=" + Weights +
                    ", WeightMoney=" + WeightMoney +
                    ", DangerPerson=" + DangerPerson +
                    ", DangerNum=" + DangerNum +
                    ", SellTicketNum=" + SellTicketNum +
                    ", SellTicketMoney=" + SellTicketMoney +
                    ", SellTicketOverflow='" + SellTicketOverflow + '\'' +
                    ", GoodDeedPerson=" + GoodDeedPerson +
                    ", GoodDeedNum=" + GoodDeedNum +
                    ", ReTroublePerson=" + ReTroublePerson +
                    ", ReTroubleNum=" + ReTroubleNum +
                    ", TroublePerson=" + TroublePerson +
                    ", TroubleNum=" + TroubleNum +
                    ", SuperiorRemark='" + SuperiorRemark + '\'' +
                    ", MainWork='" + MainWork + '\'' +
                    ", WorkRemark='" + WorkRemark + '\'' +
                    ", MatterRemark='" + MatterRemark + '\'' +
                    ", WorkDate='" + WorkDate + '\'' +
                    ", WorkTime='" + WorkTime + '\'' +
                    ", CreateDate='" + CreateDate + '\'' +
                    '}';
        }
    }
}
