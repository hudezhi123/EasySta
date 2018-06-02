package easyway.Mobile.DangerousGoods;

import java.io.Serializable;

/**
 * Created by boy on 2017/7/26.
 */

public class DangerousObjectResult implements Serializable {

    /**
     * Id : 1
     * FindDate : 2017-08-03 00:00:00
     * FullName : 陶
     * Sex : true
     * TrainNo : G88
     * TicketNo : I123
     * HomeAddr : 苏州
     * Contact : 137
     * ProdName : 禁止携带
     * ProdTotal : 1
     * ProdQuity : 1
     * FindDeptId : 1539
     * FindDeptName : 客运一班
     * FindStaffId : 2833
     * FindStaffName : 浮斌
     * FindStaffPosition :
     * ProdGoTo : 收缴
     * DealDeptId : 0
     * DealStaffId : 0
     * DealStaffName : 陶亚军
     * StationCode : XAB
     * IdNum : 320
     * DealPosition : 执机
     * DealGroupNo : 一
     * Remark : 禁止携带禁止携带禁止携带禁止携带
     * ProdNameDetail : 酒水
     */

    private int Id;
    private String FindDate;
    private String FullName;
    private boolean Sex;
    private String TrainNo;
    private String TicketNo;
    private String HomeAddr;
    private String Contact;
    private String ProdName;
    private int ProdTotal;
    private int ProdQuity;
    private int FindDeptId;
    private String FindDeptName;
    private int FindStaffId;
    private String FindStaffName;
    private String FindStaffPosition;
    private String ProdGoTo;
    private int DealDeptId;
    private int DealStaffId;
    private String DealStaffName;
    private String StationCode;
    private String IdNum;
    private String DealPosition;
    private String DealGroupNo;
    private String Remark;
    private String ProdNameDetail;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getFindDate() {
        return FindDate;
    }

    public void setFindDate(String FindDate) {
        this.FindDate = FindDate;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    public boolean isSex() {
        return Sex;
    }

    public void setSex(boolean Sex) {
        this.Sex = Sex;
    }

    public String getTrainNo() {
        return TrainNo;
    }

    public void setTrainNo(String TrainNo) {
        this.TrainNo = TrainNo;
    }

    public String getTicketNo() {
        return TicketNo;
    }

    public void setTicketNo(String TicketNo) {
        this.TicketNo = TicketNo;
    }

    public String getHomeAddr() {
        return HomeAddr;
    }

    public void setHomeAddr(String HomeAddr) {
        this.HomeAddr = HomeAddr;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String Contact) {
        this.Contact = Contact;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String ProdName) {
        this.ProdName = ProdName;
    }

    public int getProdTotal() {
        return ProdTotal;
    }

    public void setProdTotal(int ProdTotal) {
        this.ProdTotal = ProdTotal;
    }

    public int getProdQuity() {
        return ProdQuity;
    }

    public void setProdQuity(int ProdQuity) {
        this.ProdQuity = ProdQuity;
    }

    public int getFindDeptId() {
        return FindDeptId;
    }

    public void setFindDeptId(int FindDeptId) {
        this.FindDeptId = FindDeptId;
    }

    public String getFindDeptName() {
        return FindDeptName;
    }

    public void setFindDeptName(String FindDeptName) {
        this.FindDeptName = FindDeptName;
    }

    public int getFindStaffId() {
        return FindStaffId;
    }

    public void setFindStaffId(int FindStaffId) {
        this.FindStaffId = FindStaffId;
    }

    public String getFindStaffName() {
        return FindStaffName;
    }

    public void setFindStaffName(String FindStaffName) {
        this.FindStaffName = FindStaffName;
    }

    public String getFindStaffPosition() {
        return FindStaffPosition;
    }

    public void setFindStaffPosition(String FindStaffPosition) {
        this.FindStaffPosition = FindStaffPosition;
    }

    public String getProdGoTo() {
        return ProdGoTo;
    }

    public void setProdGoTo(String ProdGoTo) {
        this.ProdGoTo = ProdGoTo;
    }

    public int getDealDeptId() {
        return DealDeptId;
    }

    public void setDealDeptId(int DealDeptId) {
        this.DealDeptId = DealDeptId;
    }

    public int getDealStaffId() {
        return DealStaffId;
    }

    public void setDealStaffId(int DealStaffId) {
        this.DealStaffId = DealStaffId;
    }

    public String getDealStaffName() {
        return DealStaffName;
    }

    public void setDealStaffName(String DealStaffName) {
        this.DealStaffName = DealStaffName;
    }

    public String getStationCode() {
        return StationCode;
    }

    public void setStationCode(String StationCode) {
        this.StationCode = StationCode;
    }

    public String getIdNum() {
        return IdNum;
    }

    public void setIdNum(String IdNum) {
        this.IdNum = IdNum;
    }

    public String getDealPosition() {
        return DealPosition;
    }

    public void setDealPosition(String DealPosition) {
        this.DealPosition = DealPosition;
    }

    public String getDealGroupNo() {
        return DealGroupNo;
    }

    public void setDealGroupNo(String DealGroupNo) {
        this.DealGroupNo = DealGroupNo;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getProdNameDetail() {
        return ProdNameDetail;
    }

    public void setProdNameDetail(String ProdNameDetail) {
        this.ProdNameDetail = ProdNameDetail;
    }
}
