package easyway.Mobile.Data;

import java.io.Serializable;

public class GetDevSparePartsUsingHistoryData implements Serializable{
	private String  DevCode;
	private String DevName;
	private String UseTime;
	private String UseStaffName;
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
	public String getUseTime() {
		return UseTime;
	}
	public void setUseTime(String useTime) {
		UseTime = useTime;
	}
	public String getUseStaffName() {
		return UseStaffName;
	}
	public void setUseStaffName(String useStaffName) {
		UseStaffName = useStaffName;
	}
	@Override
	public String toString() {
		return "GetDevSparePartsUsingHistoryData [DevCode=" + DevCode
				+ ", DevName=" + DevName + ", UseTime=" + UseTime
				+ ", UseStaffName=" + UseStaffName + "]";
	}


} 
