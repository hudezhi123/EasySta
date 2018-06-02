package easyway.Mobile.Data;

import java.io.Serializable;

public class LoginData implements Serializable {
	private String SessionID;

	private String UserId;

	private String StaffId;

	private String StaffName;

	private String DeptId;

	private String DeptName;

	private String OwnStation;

	private String ChargeStation;

	private String VOIPServiceAddress;

	private String VOIPServicePort;

	private String VOIPId;

	private String VOIPPwd;

	private String IsTeamLeader;

	private String ServerTime;

	public void setSessionID(String SessionID) {
		this.SessionID = SessionID;
	}

	public String getSessionID() {
		return this.SessionID;
	}

	public void setUserId(String UserId) {
		this.UserId = UserId;
	}

	public String getUserId() {
		return this.UserId;
	}

	public void setStaffId(String StaffId) {
		this.StaffId = StaffId;
	}

	public String getStaffId() {
		return this.StaffId;
	}

	public void setStaffName(String StaffName) {
		this.StaffName = StaffName;
	}

	public String getStaffName() {
		return this.StaffName;
	}

	public void setDeptId(String DeptId) {
		this.DeptId = DeptId;
	}

	public String getDeptId() {
		return this.DeptId;
	}

	public void setDeptName(String DeptName) {
		this.DeptName = DeptName;
	}

	public String getDeptName() {
		return this.DeptName;
	}

	public void setOwnStation(String OwnStation) {
		this.OwnStation = OwnStation;
	}

	public String getOwnStation() {
		return this.OwnStation;
	}

	public void setChargeStation(String ChargeStation) {
		this.ChargeStation = ChargeStation;
	}

	public String getChargeStation() {
		return this.ChargeStation;
	}

	public void setVOIPServiceAddress(String VOIPServiceAddress) {
		this.VOIPServiceAddress = VOIPServiceAddress;
	}

	public String getVOIPServiceAddress() {
		return this.VOIPServiceAddress;
	}

	public void setVOIPServicePort(String VOIPServicePort) {
		this.VOIPServicePort = VOIPServicePort;
	}

	public String getVOIPServicePort() {
		return this.VOIPServicePort;
	}

	public void setVOIPId(String VOIPId) {
		this.VOIPId = VOIPId;
	}

	public String getVOIPId() {
		return this.VOIPId;
	}

	public void setVOIPPwd(String VOIPPwd) {
		this.VOIPPwd = VOIPPwd;
	}

	public String getVOIPPwd() {
		return this.VOIPPwd;
	}

	public void setIsTeamLeader(String IsTeamLeader) {
		this.IsTeamLeader = IsTeamLeader;
	}

	public String getIsTeamLeader() {
		return this.IsTeamLeader;
	}

	public void setServerTime(String ServerTime) {
		this.ServerTime = ServerTime;
	}

	public String getServerTime() {
		return this.ServerTime;
	}

	@Override
	public String toString() {
		return "LoginData [SessionID=" + SessionID + ", UserId=" + UserId
				+ ", StaffId=" + StaffId + ", StaffName=" + StaffName
				+ ", DeptId=" + DeptId + ", DeptName=" + DeptName
				+ ", OwnStation=" + OwnStation + ", ChargeStation="
				+ ChargeStation + ", VOIPServiceAddress=" + VOIPServiceAddress
				+ ", VOIPServicePort=" + VOIPServicePort + ", VOIPId=" + VOIPId
				+ ", VOIPPwd=" + VOIPPwd + ", IsTeamLeader=" + IsTeamLeader
				+ ", ServerTime=" + ServerTime + "]";
	}

}
