package easyway.Mobile.Shift;

import java.io.Serializable;

public class RollCallBase implements Serializable {
	
	public long staffId;
	
	public String name;

	public RollCallBase(long staffId, String name) {
		super();
		this.staffId = staffId;
		this.name = name;
	}
	
	public RollCallBase() {
		super();
	}



	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "RollCallBase [staffId=" + staffId + ", name=" + name + "]";
	}
	
	
}
