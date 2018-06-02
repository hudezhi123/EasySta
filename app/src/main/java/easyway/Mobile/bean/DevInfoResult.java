package easyway.Mobile.bean;

import java.io.Serializable;

/**
 * Created by boy on 2017/7/10.
 */

public class DevInfoResult implements Serializable {

    /**
     * Flag : 2
     * DevCode : XABZT-4.5
     * DevName : ZT-4.5
     * DevCate : 站台
     * DevLocation :
     * TwName : 4、5站台
     * DevId : 1723
     * IsGroup : true
     */

    private int Flag;
    private String DevCode;
    private String DevName;
    private String DevCate;
    private String DevLocation;
    private String TwName;
    private int DevId;
    private boolean IsGroup;
    private String DevKeeper;

    public String getDevKeeper() {
        return DevKeeper;
    }

    public void setDevKeeper(String devKeeper) {
        DevKeeper = devKeeper;
    }

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int Flag) {
        this.Flag = Flag;
    }

    public String getDevCode() {
        return DevCode;
    }

    public void setDevCode(String DevCode) {
        this.DevCode = DevCode;
    }

    public String getDevName() {
        return DevName;
    }

    public void setDevName(String DevName) {
        this.DevName = DevName;
    }

    public String getDevCate() {
        return DevCate;
    }

    public void setDevCate(String DevCate) {
        this.DevCate = DevCate;
    }

    public String getDevLocation() {
        return DevLocation;
    }

    public void setDevLocation(String DevLocation) {
        this.DevLocation = DevLocation;
    }

    public String getTwName() {
        return TwName;
    }

    public void setTwName(String TwName) {
        this.TwName = TwName;
    }

    public int getDevId() {
        return DevId;
    }

    public void setDevId(int DevId) {
        this.DevId = DevId;
    }

    public boolean isIsGroup() {
        return IsGroup;
    }

    public void setIsGroup(boolean IsGroup) {
        this.IsGroup = IsGroup;
    }

    @Override
    public String toString() {
        return "DevInfoResult{" +
                "Flag=" + Flag +
                ", DevCode='" + DevCode + '\'' +
                ", DevName='" + DevName + '\'' +
                ", DevCate='" + DevCate + '\'' +
                ", DevLocation='" + DevLocation + '\'' +
                ", TwName='" + TwName + '\'' +
                ", DevId=" + DevId +
                ", IsGroup=" + IsGroup +
                ", DevKeeper='" + DevKeeper + '\'' +
                '}';
    }
}
