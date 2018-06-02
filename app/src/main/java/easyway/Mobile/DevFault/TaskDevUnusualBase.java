package easyway.Mobile.DevFault;

import java.io.Serializable;

public class TaskDevUnusualBase implements Serializable{
	
	public String StaffName;
	public String Workspace;
	public String StationCode;
	
	public TaskDevUnusualBase() {
		super();
	}

	public TaskDevUnusualBase(String staffName, String workspace,
			String stationCode) {
		super();
		StaffName = staffName;
		Workspace = workspace;
		StationCode = stationCode;
	}

	public String getStaffName() {
		return StaffName;
	}

	public void setStaffName(String staffName) {
		StaffName = staffName;
	}

	public String getWorkspace() {
		return Workspace;
	}

	public void setWorkspace(String workspace) {
		Workspace = workspace;
	}

	public String getStationCode() {
		return StationCode;
	}

	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}

	@Override
	public String toString() {
		return "TaskDevUnusualBase [StaffName=" + StaffName + ", Workspace="
				+ Workspace + ", StationCode=" + StationCode + "]";
	}
	
}
