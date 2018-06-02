package easyway.Mobile.Data;

import java.io.Serializable;

public class GetAllDevCateData implements Serializable{
	private int DcId;
	private String DcName;
	private int DcParentId;
	private String DcCode;
	private String StationCode;
	public int getDcId() {
		return DcId;
	}
	public void setDcId(int dcId) {
		DcId = dcId;
	}
	public String getDcName() {
		return DcName;
	}
	public void setDcName(String dcName) {
		DcName = dcName;
	}
	public int getDcParentId() {
		return DcParentId;
	}
	public void setDcParentId(int dcParentId) {
		DcParentId = dcParentId;
	}
	public String getDcCode() {
		return DcCode;
	}
	public void setDcCode(String dcCode) {
		DcCode = dcCode;
	}
	public String getStationCode() {
		return StationCode;
	}
	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}
	@Override
	public String toString() {
		return "GetAllDevCateData [DcId=" + DcId + ", DcName=" + DcName
				+ ", DcParentId=" + DcParentId + ", DcCode=" + DcCode
				+ ", StationCode=" + StationCode + "]";
	}


}
