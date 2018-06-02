package easyway.Mobile.Data;

import java.io.Serializable;

public class DFMainData implements Serializable {

    private int Id;

    private String DevName;

    private String DevCode;

    private String DevCate;

    private String TwName;

    private String Location;

    private String ReportedTime;

    private String FaultContent;

    private String AppendixUrl;

    private String DevIssueImgFrom;

    private String Remark;

    private String RepStaffType;

    public int getDevId() {
        return DevId;
    }

    public void setDevId(int devId) {
        DevId = devId;
    }

    public boolean isGroup() {
        return IsGroup;
    }

    public void setGroup(boolean group) {
        IsGroup = group;
    }

    private int DevId;

    private String Reporteder;

    private int AppStatus;

    private boolean IsGroup;

    private String GroupId;

    private String FixStaffName;

    private String FixTime;

    private String ConfirmStaffName;

    private String ConfirmTime;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDevName() {
        return DevName;
    }

    public void setDevName(String devName) {
        DevName = devName;
    }

    public String getDevCode() {
        return DevCode;
    }

    public void setDevCode(String devCode) {
        DevCode = devCode;
    }

    public String getDevCate() {
        return DevCate;
    }

    public void setDevCate(String devCate) {
        DevCate = devCate;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTwName() {
        return TwName;
    }

    public void setTwName(String twName) {
        TwName = twName;
    }

    public String getReportedTime() {
        return ReportedTime;
    }

    public void setReportedTime(String reportedTime) {
        ReportedTime = reportedTime;
    }

    public String getFaultContent() {
        return FaultContent;
    }

    public void setFaultContent(String faultContent) {
        FaultContent = faultContent;
    }

    public String getDevIssueImgFrom() {
        return DevIssueImgFrom;
    }

    public void setDevIssueImgFrom(String devIssueImgFrom) {
        DevIssueImgFrom = devIssueImgFrom;
    }

    public String getReporteder() {
        return Reporteder;
    }

    public void setReporteder(String reporteder) {
        Reporteder = reporteder;
    }

    /**
     * 0 == 已报障（走维修流程）
     * 1 == 维修完成（走设备保障处理，就是确认修复。）
     * 2 == 已确认（已经确认了修复。不需要处理了。）
     *
     * @return
     */
    public int getAppStatus() {
        return AppStatus;
    }

    public void setAppStatus(int appStatus) {
        AppStatus = appStatus;
    }

    public boolean isIsGroup() {
        return IsGroup;
    }

    public void setIsGroup(boolean isGroup) {
        IsGroup = isGroup;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }


    public String getAppendixUrl() {
        return AppendixUrl;
    }

    public void setAppendixUrl(String appendixUrl) {
        AppendixUrl = appendixUrl;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getRepStaffType() {
        return RepStaffType;
    }

    public void setRepStaffType(String repStaffType) {
        RepStaffType = repStaffType;
    }

    public String getFixStaffName() {
        return FixStaffName;
    }

    public void setFixStaffName(String fixStaffName) {
        FixStaffName = fixStaffName;
    }

    public String getFixTime() {
        return FixTime;
    }

    public void setFixTime(String fixTime) {
        FixTime = fixTime;
    }

    public String getConfirmStaffName() {
        return ConfirmStaffName;
    }

    public void setConfirmStaffName(String confirmStaffName) {
        ConfirmStaffName = confirmStaffName;
    }

    public String getConfirmTime() {
        return ConfirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        ConfirmTime = confirmTime;
    }

    @Override
    public String toString() {
        return "DFMainData [Id=" + Id + ", DevName=" + DevName + ", DevCode="
                + DevCode + ", DevCate=" + DevCate + ", Location=" + Location
                + ", ReportedTime=" + ReportedTime + ", FaultContent="
                + FaultContent + ", AppendixUrl=" + AppendixUrl
                + ", DevIssueImgFrom=" + DevIssueImgFrom + ", Remark=" + Remark
                + ", RepStaffType=" + RepStaffType + ", Reporteder="
                + Reporteder + ", AppStatus=" + AppStatus + ", IsGroup="
                + IsGroup + ", GroupId=" + GroupId + ", FixStaffName="
                + FixStaffName + ", FixTime=" + FixTime + ", ConfirmStaffName="
                + ConfirmStaffName + ", ConfirmTime=" + ConfirmTime + "]";
    }


}
