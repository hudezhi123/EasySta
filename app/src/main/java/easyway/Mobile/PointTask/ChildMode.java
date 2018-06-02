package easyway.Mobile.PointTask;

import java.io.Serializable;
import java.util.ArrayList;

// 子任务添加信息模板
public class ChildMode implements Serializable {
	private static final long serialVersionUID = -8853194737804390453L;
	
	public int index = -1;
	public String PositionName;			// 岗位
	public long PId;
	public String Workspace;				// 任务区域
	public long TwId;
	public String BeginWorkTime;		// 开始时间
	public String EndWorkTime;			// 结束时间
	public String TaskRemark;				// 备注
	public long StaffId;
	public String StaffName;				// 执行人
	public ArrayList<String> AttachList;	// 附件
	public int UpdateState = UPDATESTATE_NOT;
	public long SaId = SAID_INVALID;
	public ArrayList<Boolean> IsAttachUpdateList;
	
	public static final int UPDATESTATE_NOT = 0;		// 未上传
	public static final int UPDATESTATE_PART = 1;		// 部分上传
	public static final int UPDATESTATE_ALL = 2;		// 上传完毕
	
	public static final long SAID_INVALID = 0;
}
