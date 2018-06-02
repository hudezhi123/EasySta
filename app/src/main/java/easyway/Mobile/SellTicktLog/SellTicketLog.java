package easyway.Mobile.SellTicktLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import easyway.Mobile.util.DateUtil;


/**
 * Created by boy on 2017/11/20.
 */

public class SellTicketLog implements Serializable {
    public int ID;
    public String Classes;
    public String DutyOfficer;
    public int SellTicketNum;
    public double SellTicketMoney;
    public int AgentTicketsNum;
    public double AgentTicketsMoney;
    public int AbnormalBounceTicketNum;
    public int BounceTicketNum;
    public double BounceMoney;
    public String CreateDate;
    public String WorkDate;
    public String WorkTime;
    public String SuperiorRemark;
    public String MainWork;
    public String WorkRemark;
    public String MatterRemark;
    public String Weather;

    // 从本地数据库获取一整天（2011-01-22 ）的售票日志
    public static List<SellTicketLog> LoadTicketSellLogList() {

        return null;
    }

    public static boolean SaveTicketSellLog() {

        return false;
    }

    public static List<SellTicketLog> JsonArray2List(JSONArray jsonArray) {
        List<SellTicketLog> logList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.optJSONObject(i);
            SellTicketLog log = new SellTicketLog();
            log.ID = jsonObj.optInt("ID");
            log.Weather = jsonObj.optString("Weather");
            log.Classes = jsonObj.optString("Classes");
            log.SellTicketNum = jsonObj.optInt("SellTicketNum");
            log.DutyOfficer = jsonObj.optString("DutyOfficer");
            log.SellTicketMoney = jsonObj.optDouble("SellTicketMoney");
            log.AbnormalBounceTicketNum = jsonObj.optInt("AbnormalBounceTicketNum");
            log.AgentTicketsMoney = jsonObj.optDouble("AgentTicketsMoney");
            log.AgentTicketsNum = jsonObj.optInt("AgentTicketsNum");
            log.BounceMoney = jsonObj.optDouble("BounceMoney");
            log.BounceTicketNum = jsonObj.optInt("BounceTicketNum");
            log.CreateDate = DateUtil.formatDate(jsonObj.optString("CreateDate"), DateUtil.YYYY_MM_DD_HH_MM_SS);
            log.WorkTime = DateUtil.formatDate(jsonObj.optString("WorkTime"), DateUtil.YYYY_MM_DD_HH_MM_SS);
            log.WorkDate = DateUtil.formatDate(jsonObj.optString("WorkDate"), DateUtil.YYYY_MM_DD_HH_MM_SS);
            log.MainWork = jsonObj.optString("MainWork");
            log.MatterRemark = jsonObj.optString("MatterRemark");
            log.SuperiorRemark = jsonObj.optString("SuperiorRemark");
            log.WorkRemark = jsonObj.optString("WorkRemark");
            logList.add(log);
        }
        return logList;
    }

    public static List<SellTicketLog> ParseToList(String jsonResult) {

        return null;
    }

    @Override
    public String toString() {
        return "SellTicketLog{" +
                "ID=" + ID +
                ", Classes='" + Classes + '\'' +
                ", DutyOfficer='" + DutyOfficer + '\'' +
                ", SellTicketNum=" + SellTicketNum +
                ", SellTicketMoney=" + SellTicketMoney +
                ", AgentTicketsNum=" + AgentTicketsNum +
                ", AgentTicketsMoney=" + AgentTicketsMoney +
                ", AbnormalBounceTicketNum=" + AbnormalBounceTicketNum +
                ", BounceTicketNum=" + BounceTicketNum +
                ", BounceMoney=" + BounceMoney +
                ", CreateDate='" + CreateDate + '\'' +
                ", WorkDate='" + WorkDate + '\'' +
                ", WorkTime='" + WorkTime + '\'' +
                ", SuperiorRemark='" + SuperiorRemark + '\'' +
                ", MainWork='" + MainWork + '\'' +
                ", WorkRemark='" + WorkRemark + '\'' +
                ", MatterRemark='" + MatterRemark + '\'' +
                '}';
    }
}
