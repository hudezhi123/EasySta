package easyway.Mobile.site_monitoring;

import java.io.Serializable;

public class SMTask implements Serializable {
	private static final long serialVersionUID = 8519467244800000425L;
	
	public long TwId;
	public String Workspace;
	public long TaskId;
	public long SaId;
	public String TrainNum;
	public String BeginWorkTime;
	public String EndWorkTime;
	public long DeptId;
	public String DeptName;
	public long StaffId = INVALID_STAFFID;
	public String StaffName;
	public long AExcStat;
	
	public static final String KEY_TASK = "SMTASK";
	
	public static final long INVALID_STAFFID = 0;
}
