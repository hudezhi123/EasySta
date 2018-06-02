package easyway.Mobile.Data;

import java.io.Serializable;

public class GetAllDevSupplierData implements Serializable{
	private int SupId;
	private String CompName;
	private String CompAddr;
	private String CompWebSite;
	private String CompPostcode;
	private String CompContact;
	private String CompContactMobile;
	private String CompContactTelephone;
	private String CompContactEmail;
	private String CompFax;
	private int CompCate;
	private int CompLevel;
	private String StationCode;
	public int getSupId() {
		return SupId;
	}
	public void setSupId(int supId) {
		SupId = supId;
	}
	public String getCompName() {
		return CompName;
	}
	public void setCompName(String compName) {
		CompName = compName;
	}
	public String getCompAddr() {
		return CompAddr;
	}
	public void setCompAddr(String compAddr) {
		CompAddr = compAddr;
	}
	public String getCompWebSite() {
		return CompWebSite;
	}
	public void setCompWebSite(String compWebSite) {
		CompWebSite = compWebSite;
	}
	public String getCompPostcode() {
		return CompPostcode;
	}
	public void setCompPostcode(String compPostcode) {
		CompPostcode = compPostcode;
	}
	public String getCompContact() {
		return CompContact;
	}
	public void setCompContact(String compContact) {
		CompContact = compContact;
	}
	public String getCompContactMobile() {
		return CompContactMobile;
	}
	public void setCompContactMobile(String compContactMobile) {
		CompContactMobile = compContactMobile;
	}
	public String getCompContactTelephone() {
		return CompContactTelephone;
	}
	public void setCompContactTelephone(String compContactTelephone) {
		CompContactTelephone = compContactTelephone;
	}
	public String getCompContactEmail() {
		return CompContactEmail;
	}
	public void setCompContactEmail(String compContactEmail) {
		CompContactEmail = compContactEmail;
	}
	public String getCompFax() {
		return CompFax;
	}
	public void setCompFax(String compFax) {
		CompFax = compFax;
	}
	public int getCompCate() {
		return CompCate;
	}
	public void setCompCate(int compCate) {
		CompCate = compCate;
	}
	public int getCompLevel() {
		return CompLevel;
	}
	public void setCompLevel(int compLevel) {
		CompLevel = compLevel;
	}
	public String getStationCode() {
		return StationCode;
	}
	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}
	@Override
	public String toString() {
		return "GetAllDevSupplierData [SupId=" + SupId + ", CompName="
				+ CompName + ", CompAddr=" + CompAddr + ", CompWebSite="
				+ CompWebSite + ", CompPostcode=" + CompPostcode
				+ ", CompContact=" + CompContact + ", CompContactMobile="
				+ CompContactMobile + ", CompContactTelephone="
				+ CompContactTelephone + ", CompContactEmail="
				+ CompContactEmail + ", CompFax=" + CompFax + ", CompCate="
				+ CompCate + ", CompLevel=" + CompLevel + ", StationCode="
				+ StationCode + "]";
	}


}
