package easyway.Mobile.PointTask;

import java.io.Serializable;

// 主任务添加信息模板
public class MajorMode implements Serializable {
	private static final long serialVersionUID = 535567714209056628L;

	public long TaskId = TASKID_INVALID;
	public String TaskName;
	public String TRNO_PRO;
	public int TaskLevel;
	public long StaffId;
	public String StaffName;
	public String PlanDate;
	
	public static final long TASKID_INVALID = 0;
}
