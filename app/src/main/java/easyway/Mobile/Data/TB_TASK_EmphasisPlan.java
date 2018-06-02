package easyway.Mobile.Data;

import java.util.Date;

public class TB_TASK_EmphasisPlan {
	public String TaskName;			// 任务名称
	public String BeginWorkTime;	// 开始时间
	public String EndWorkTime;		// 结束时间
	public String TeamName;			// 执行组
	public String StaffName;			// 执行者
	public String TrainNum;				// 任务车次
	public String RBeginWorkTime;	// 执行开始时间
	public String REndWorkTime;		// 执行结束时间
	public String Publisher;				// 发布人
	public String PublisherName;		// 发布人名字
	public String TaskContent;			// 任务内容
	public String AppendixUrl;			// 任务附件url
	public Date CreateTime;				// 任务生成时间
	public Date PlanDate;					// 计划时间
	public long TeamId;					// 执行组ID
	public long ExcSta;					// 任务状态
	public long ID;							// 任务ID

	public TB_TASK_EmphasisPlan() {
		TaskName = "";
		AppendixUrl = "";
		TrainNum = "";
		StaffName = "";
		TeamName = "";
		TaskContent = "";
		BeginWorkTime = "";
		EndWorkTime = "";
		RBeginWorkTime = "";
		REndWorkTime = "";
		CreateTime = new Date();
		PlanDate = new Date();
		Publisher = "";
		PublisherName = "";
		ExcSta = 0;
		ID = 0;
		TeamId = 0;

	}
}
