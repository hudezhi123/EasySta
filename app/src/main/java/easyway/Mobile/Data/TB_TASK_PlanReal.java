package easyway.Mobile.Data;

import java.io.Serializable;

public class TB_TASK_PlanReal implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public String TaskName;
	public String WorkSpaces; 
	public String LinkRoleID;
	public String TrainNum;
	public String TeamName;
	public String BeginWorkTime;
	public String EndWorkTime;
	public String RBeginWorkTime;
	public String REndWorkTime;

	public String PlanDate;
	public String CreateTime;
	public long TempID;
	public long TaskLevel;
	public long TaskSta;
	public long ExcSta;
	public long ID;
	public long TeamId;
	public long IsConflict;
}
