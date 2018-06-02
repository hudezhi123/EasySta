package easyway.Mobile.Data;

import java.io.Serializable;

public class GetDevInGroupResult implements Serializable{
    private int DevId;
    private String DevCode;
    private String DevName;
    private String DevCate;
    private int SupId;
    private String StockInDate;
    private String Location;
    private String StationCode;
    private String GroupId;
    private Boolean IsGroup;
    private int LocationId;
    private int DcId;
    private int TwId;
    private String TwName;
    private String BeginUseDate;
	
	public int getDevId() {
		return DevId;
	}
	public void setDevId(int devId) {
		DevId = devId;
	}
	public String getDevCode() {
		return DevCode;
	}
	public void setDevCode(String devCode) {
		DevCode = devCode;
	}
	public String getDevName() {
		return DevName;
	}
	public void setDevName(String devName) {
		DevName = devName;
	}
	public String getDevCate() {
		return DevCate;
	}
	public void setDevCate(String devCate) {
		DevCate = devCate;
	}
	public int getSupId() {
		return SupId;
	}
	public void setSupId(int supId) {
		SupId = supId;
	}
	public String getStockInDate() {
		return StockInDate;
	}
	public void setStockInDate(String stockInDate) {
		StockInDate = stockInDate;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getStationCode() {
		return StationCode;
	}
	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public Boolean getIsGroup() {
		return IsGroup;
	}
	public void setIsGroup(Boolean isGroup) {
		IsGroup = isGroup;
	}
	public int getLocationId() {
		return LocationId;
	}
	public void setLocationId(int locationId) {
		LocationId = locationId;
	}
	public int getDcId() {
		return DcId;
	}
	public void setDcId(int dcId) {
		DcId = dcId;
	}
	public int getTwId() {
		return TwId;
	}
	public void setTwId(int twId) {
		TwId = twId;
	}
	public String getTwName() {
		return TwName;
	}
	public void setTwName(String twName) {
		TwName = twName;
	}
	public String getBeginUseDate() {
		return BeginUseDate;
	}
	public void setBeginUseDate(String beginUseDate) {
		BeginUseDate = beginUseDate;
	}
	@Override
	public String toString() {
		return "GetDevInGroupResult [DevId=" + DevId + ", DevCode=" + DevCode
				+ ", DevName=" + DevName + ", DevCate=" + DevCate + ", SupId="
				+ SupId + ", StockInDate=" + StockInDate + ", Location="
				+ Location + ", StationCode=" + StationCode + ", GroupId="
				+ GroupId + ", IsGroup=" + IsGroup + ", LocationId="
				+ LocationId + ", DcId=" + DcId + ", TwId=" + TwId
				+ ", TwName=" + TwName + ", BeginUseDate=" + BeginUseDate + "]";
	}
    
    
}
