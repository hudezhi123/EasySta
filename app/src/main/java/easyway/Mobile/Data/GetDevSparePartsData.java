package easyway.Mobile.Data;

import java.io.Serializable;

public class GetDevSparePartsData implements Serializable{
	private long DspId;
	private long DrId;
	private String DevCode;
	private String DevName;
	private long DevCateId;
	private String DevCate;
	private double DevPrice;
	private int SupId;
	private String BuyDate;
	private String Warranty;
	private String RetireAge;
	private int DspStatus;
	private String StationCode;
	private String DevCateName;
	private String SupName;
	private String FromDevName;
	private String ToDevName;
	public long getDspId() {
		return DspId;
	}
	public void setDspId(long dspId) {
		DspId = dspId;
	}
	public long getDrId() {
		return DrId;
	}
	public void setDrId(long drId) {
		DrId = drId;
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
	public long getDevCateId() {
		return DevCateId;
	}
	public void setDevCateId(long devCateId) {
		DevCateId = devCateId;
	}
	public String getDevCate() {
		return DevCate;
	}
	public void setDevCate(String devCate) {
		DevCate = devCate;
	}
	public double getDevPrice() {
		return DevPrice;
	}
	public void setDevPrice(double devPrice) {
		DevPrice = devPrice;
	}
	public int getSupId() {
		return SupId;
	}
	public void setSupId(int supId) {
		SupId = supId;
	}
	public String getBuyDate() {
		return BuyDate;
	}
	public void setBuyDate(String buyDate) {
		BuyDate = buyDate;
	}
	public String getWarranty() {
		return Warranty;
	}
	public void setWarranty(String warranty) {
		Warranty = warranty;
	}
	public String getRetireAge() {
		return RetireAge;
	}
	public void setRetireAge(String retireAge) {
		RetireAge = retireAge;
	}
	public int getDspStatus() {
		return DspStatus;
	}
	public void setDspStatus(int dspStatus) {
		DspStatus = dspStatus;
	}
	public String getStationCode() {
		return StationCode;
	}
	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}
	
	public String getDevCateName() {
		return DevCateName;
	}
	public void setDevCateName(String devCateName) {
		DevCateName = devCateName;
	}
	public String getSupName() {
		return SupName;
	}
	public void setSupName(String supName) {
		SupName = supName;
	}
	public String getFromDevName() {
		return FromDevName;
	}
	public void setFromDevName(String fromDevName) {
		FromDevName = fromDevName;
	}
	public String getToDevName() {
		return ToDevName;
	}
	public void setToDevName(String toDevName) {
		ToDevName = toDevName;
	}
	
	@Override
	public String toString() {
		return "GetDevSparePartsData [DspId=" + DspId + ", DrId=" + DrId
				+ ", DevCode=" + DevCode + ", DevName=" + DevName
				+ ", DevCateId=" + DevCateId + ", DevCate=" + DevCate
				+ ", DevPrice=" + DevPrice + ", SupId=" + SupId + ", BuyDate="
				+ BuyDate + ", Warranty=" + Warranty + ", RetireAge="
				+ RetireAge + ", DspStatus=" + DspStatus + ", StationCode="
				+ StationCode + ", DevCateName=" + DevCateName + ", SupName="
				+ SupName + ", FromDevName=" + FromDevName + ", ToDevName="
				+ ToDevName + "]";
	}


}
