package easyway.Mobile.ShiftData;

import java.io.Serializable;

public class Shift2OutAllType<T> implements Serializable{
	public int Sd_RDeptId  ;
	public int Sd_TssId  ;
	public String Sd_RStaffNames  ;
	public int Sd_DeptId  ;
	public int Shift_Detail_Id  ;
	public String Sd_EndWorkTime  ;
	public String Sd_ScheName  ;
	public int Sd_StaffId  ;
	public int Sd_ShiftStaffId  ;
	public int Sd_TaskId  ;
	public String Sd_DeptName  ;
	public int Sd_SaId  ;
	public int Sd_ShiftDeptId  ;
	public String Sd_RStaffIds  ;
	public String Sd_RWorkspaces  ;
	public String Sd_Workspace  ;
	public int Sd_TrainId  ;
	public String Sd_TRNO_PRO  ;
	public String Sd_StaffName  ;
	public String Sd_Remark  ;
	public String Sd_VoicePath  ;
	public int Shift_Id  ;
	public String Sd_RDeptName  ;
	public String Sd_ShiftStaffName  ;
	public String Sd_ShiftDeptName  ;
	public String Sd_RBeginWorkTime  ;
	public String Sd_BeginWorkTime  ;
	public Boolean DevIsWork;
	public int TaskId;
	public String REndWorkTime;
	public String EndWorkTime;
	public String RWorkspaces;
	public Boolean DevIsFixed;
	public Boolean IsAccepted;
	public int PId;
	public String DevFixStaffName;
	public String TRNO_PRO;
	public int SaId;
	public String RStaffNames;
	public int AExcStat;
	public int RTeamId;
	public String DeptName;
	public String TaskRemark;
	public String DevFixDttm;
	public String PositionName;
	public int StaffId;
	public int PdId;
	public int DeptId;
	public String ScheName;
	public String StaffName;
	public String BeginWorkTime;
	public String RBeginWorkTime;
	public String RTeamName;
	public Boolean Conflict;
	public String RStaffIds;
	public String AcceptDttm;
	public int TwId;
	public int TssId;
	public int DevFixStaffId;
	public int TrainId;
	public String Workspace;
	public T TB_Task_Schedule_Actor_Exc;
	@Override
	public String toString() {
		return "shift2OutAllType [Sd_RDeptId=" + Sd_RDeptId + ", Sd_TssId="
				+ Sd_TssId + ", Sd_RStaffNames=" + Sd_RStaffNames
				+ ", Sd_DeptId=" + Sd_DeptId + ", Shift_Detail_Id="
				+ Shift_Detail_Id + ", Sd_EndWorkTime=" + Sd_EndWorkTime
				+ ", Sd_ScheName=" + Sd_ScheName + ", Sd_StaffId=" + Sd_StaffId
				+ ", Sd_ShiftStaffId=" + Sd_ShiftStaffId + ", Sd_TaskId="
				+ Sd_TaskId + ", Sd_DeptName=" + Sd_DeptName + ", Sd_SaId="
				+ Sd_SaId + ", Sd_ShiftDeptId=" + Sd_ShiftDeptId
				+ ", Sd_RStaffIds=" + Sd_RStaffIds + ", Sd_RWorkspaces="
				+ Sd_RWorkspaces + ", Sd_Workspace=" + Sd_Workspace
				+ ", Sd_TrainId=" + Sd_TrainId + ", Sd_TRNO_PRO=" + Sd_TRNO_PRO
				+ ", Sd_StaffName=" + Sd_StaffName + ", Sd_Remark=" + Sd_Remark
				+ ", Sd_VoicePath=" + Sd_VoicePath + ", Shift_Id=" + Shift_Id
				+ ", Sd_RDeptName=" + Sd_RDeptName + ", Sd_ShiftStaffName="
				+ Sd_ShiftStaffName + ", Sd_ShiftDeptName=" + Sd_ShiftDeptName
				+ ", Sd_RBeginWorkTime=" + Sd_RBeginWorkTime
				+ ", Sd_BeginWorkTime=" + Sd_BeginWorkTime + ", DevIsWork="
				+ DevIsWork + ", TaskId=" + TaskId + ", REndWorkTime="
				+ REndWorkTime + ", EndWorkTime=" + EndWorkTime
				+ ", RWorkspaces=" + RWorkspaces + ", DevIsFixed=" + DevIsFixed
				+ ", IsAccepted=" + IsAccepted + ", PId=" + PId
				+ ", DevFixStaffName=" + DevFixStaffName + ", TRNO_PRO="
				+ TRNO_PRO + ", SaId=" + SaId + ", RStaffNames=" + RStaffNames
				+ ", AExcStat=" + AExcStat + ", RTeamId=" + RTeamId
				+ ", DeptName=" + DeptName + ", TaskRemark=" + TaskRemark
				+ ", DevFixDttm=" + DevFixDttm + ", PositionName="
				+ PositionName + ", StaffId=" + StaffId + ", PdId=" + PdId
				+ ", DeptId=" + DeptId + ", ScheName=" + ScheName
				+ ", StaffName=" + StaffName + ", BeginWorkTime="
				+ BeginWorkTime + ", RBeginWorkTime=" + RBeginWorkTime
				+ ", RTeamName=" + RTeamName + ", Conflict=" + Conflict
				+ ", RStaffIds=" + RStaffIds + ", AcceptDttm=" + AcceptDttm
				+ ", TwId=" + TwId + ", TssId=" + TssId + ", DevFixStaffId="
				+ DevFixStaffId + ", TrainId=" + TrainId + ", Workspace="
				+ Workspace + ", TB_Task_Schedule_Actor_Exc="
				+ TB_Task_Schedule_Actor_Exc
				+ ", TB_Task_Schedule_Water_Application="
				+ TB_Task_Schedule_Water_Application + "]";
	}
	public int getSd_RDeptId() {
		return Sd_RDeptId;
	}
	public void setSd_RDeptId(int sd_RDeptId) {
		Sd_RDeptId = sd_RDeptId;
	}
	public int getSd_TssId() {
		return Sd_TssId;
	}
	public void setSd_TssId(int sd_TssId) {
		Sd_TssId = sd_TssId;
	}
	public String getSd_RStaffNames() {
		return Sd_RStaffNames;
	}
	public void setSd_RStaffNames(String sd_RStaffNames) {
		Sd_RStaffNames = sd_RStaffNames;
	}
	public int getSd_DeptId() {
		return Sd_DeptId;
	}
	public void setSd_DeptId(int sd_DeptId) {
		Sd_DeptId = sd_DeptId;
	}
	public int getShift_Detail_Id() {
		return Shift_Detail_Id;
	}
	public void setShift_Detail_Id(int shift_Detail_Id) {
		Shift_Detail_Id = shift_Detail_Id;
	}
	public String getSd_EndWorkTime() {
		return Sd_EndWorkTime;
	}
	public void setSd_EndWorkTime(String sd_EndWorkTime) {
		Sd_EndWorkTime = sd_EndWorkTime;
	}
	public String getSd_ScheName() {
		return Sd_ScheName;
	}
	public void setSd_ScheName(String sd_ScheName) {
		Sd_ScheName = sd_ScheName;
	}
	public int getSd_StaffId() {
		return Sd_StaffId;
	}
	public void setSd_StaffId(int sd_StaffId) {
		Sd_StaffId = sd_StaffId;
	}
	public int getSd_ShiftStaffId() {
		return Sd_ShiftStaffId;
	}
	public void setSd_ShiftStaffId(int sd_ShiftStaffId) {
		Sd_ShiftStaffId = sd_ShiftStaffId;
	}
	public int getSd_TaskId() {
		return Sd_TaskId;
	}
	public void setSd_TaskId(int sd_TaskId) {
		Sd_TaskId = sd_TaskId;
	}
	public String getSd_DeptName() {
		return Sd_DeptName;
	}
	public void setSd_DeptName(String sd_DeptName) {
		Sd_DeptName = sd_DeptName;
	}
	public int getSd_SaId() {
		return Sd_SaId;
	}
	public void setSd_SaId(int sd_SaId) {
		Sd_SaId = sd_SaId;
	}
	public int getSd_ShiftDeptId() {
		return Sd_ShiftDeptId;
	}
	public void setSd_ShiftDeptId(int sd_ShiftDeptId) {
		Sd_ShiftDeptId = sd_ShiftDeptId;
	}
	public String getSd_RStaffIds() {
		return Sd_RStaffIds;
	}
	public void setSd_RStaffIds(String sd_RStaffIds) {
		Sd_RStaffIds = sd_RStaffIds;
	}
	public String getSd_RWorkspaces() {
		return Sd_RWorkspaces;
	}
	public void setSd_RWorkspaces(String sd_RWorkspaces) {
		Sd_RWorkspaces = sd_RWorkspaces;
	}
	public String getSd_Workspace() {
		return Sd_Workspace;
	}
	public void setSd_Workspace(String sd_Workspace) {
		Sd_Workspace = sd_Workspace;
	}
	public int getSd_TrainId() {
		return Sd_TrainId;
	}
	public void setSd_TrainId(int sd_TrainId) {
		Sd_TrainId = sd_TrainId;
	}
	public String getSd_TRNO_PRO() {
		return Sd_TRNO_PRO;
	}
	public void setSd_TRNO_PRO(String sd_TRNO_PRO) {
		Sd_TRNO_PRO = sd_TRNO_PRO;
	}
	public String getSd_StaffName() {
		return Sd_StaffName;
	}
	public void setSd_StaffName(String sd_StaffName) {
		Sd_StaffName = sd_StaffName;
	}
	public String getSd_Remark() {
		return Sd_Remark;
	}
	public void setSd_Remark(String sd_Remark) {
		Sd_Remark = sd_Remark;
	}
	public String getSd_VoicePath() {
		return Sd_VoicePath;
	}
	public void setSd_VoicePath(String sd_VoicePath) {
		Sd_VoicePath = sd_VoicePath;
	}
	public int getShift_Id() {
		return Shift_Id;
	}
	public void setShift_Id(int shift_Id) {
		Shift_Id = shift_Id;
	}
	public String getSd_RDeptName() {
		return Sd_RDeptName;
	}
	public void setSd_RDeptName(String sd_RDeptName) {
		Sd_RDeptName = sd_RDeptName;
	}
	public String getSd_ShiftStaffName() {
		return Sd_ShiftStaffName;
	}
	public void setSd_ShiftStaffName(String sd_ShiftStaffName) {
		Sd_ShiftStaffName = sd_ShiftStaffName;
	}
	public String getSd_ShiftDeptName() {
		return Sd_ShiftDeptName;
	}
	public void setSd_ShiftDeptName(String sd_ShiftDeptName) {
		Sd_ShiftDeptName = sd_ShiftDeptName;
	}
	public String getSd_RBeginWorkTime() {
		return Sd_RBeginWorkTime;
	}
	public void setSd_RBeginWorkTime(String sd_RBeginWorkTime) {
		Sd_RBeginWorkTime = sd_RBeginWorkTime;
	}
	public String getSd_BeginWorkTime() {
		return Sd_BeginWorkTime;
	}
	public void setSd_BeginWorkTime(String sd_BeginWorkTime) {
		Sd_BeginWorkTime = sd_BeginWorkTime;
	}
	public Boolean getDevIsWork() {
		return DevIsWork;
	}
	public void setDevIsWork(Boolean devIsWork) {
		DevIsWork = devIsWork;
	}
	public int getTaskId() {
		return TaskId;
	}
	public void setTaskId(int taskId) {
		TaskId = taskId;
	}
	public String getREndWorkTime() {
		return REndWorkTime;
	}
	public void setREndWorkTime(String rEndWorkTime) {
		REndWorkTime = rEndWorkTime;
	}
	public String getEndWorkTime() {
		return EndWorkTime;
	}
	public void setEndWorkTime(String endWorkTime) {
		EndWorkTime = endWorkTime;
	}
	public String getRWorkspaces() {
		return RWorkspaces;
	}
	public void setRWorkspaces(String rWorkspaces) {
		RWorkspaces = rWorkspaces;
	}
	public Boolean getDevIsFixed() {
		return DevIsFixed;
	}
	public void setDevIsFixed(Boolean devIsFixed) {
		DevIsFixed = devIsFixed;
	}
	public Boolean getIsAccepted() {
		return IsAccepted;
	}
	public void setIsAccepted(Boolean isAccepted) {
		IsAccepted = isAccepted;
	}
	public int getPId() {
		return PId;
	}
	public void setPId(int pId) {
		PId = pId;
	}
	public String getDevFixStaffName() {
		return DevFixStaffName;
	}
	public void setDevFixStaffName(String devFixStaffName) {
		DevFixStaffName = devFixStaffName;
	}
	public String getTRNO_PRO() {
		return TRNO_PRO;
	}
	public void setTRNO_PRO(String tRNO_PRO) {
		TRNO_PRO = tRNO_PRO;
	}
	public int getSaId() {
		return SaId;
	}
	public void setSaId(int saId) {
		SaId = saId;
	}
	public String getRStaffNames() {
		return RStaffNames;
	}
	public void setRStaffNames(String rStaffNames) {
		RStaffNames = rStaffNames;
	}
	public int getAExcStat() {
		return AExcStat;
	}
	public void setAExcStat(int aExcStat) {
		AExcStat = aExcStat;
	}
	public int getRTeamId() {
		return RTeamId;
	}
	public void setRTeamId(int rTeamId) {
		RTeamId = rTeamId;
	}
	public String getDeptName() {
		return DeptName;
	}
	public void setDeptName(String deptName) {
		DeptName = deptName;
	}
	public String getTaskRemark() {
		return TaskRemark;
	}
	public void setTaskRemark(String taskRemark) {
		TaskRemark = taskRemark;
	}
	public String getDevFixDttm() {
		return DevFixDttm;
	}
	public void setDevFixDttm(String devFixDttm) {
		DevFixDttm = devFixDttm;
	}
	public String getPositionName() {
		return PositionName;
	}
	public void setPositionName(String positionName) {
		PositionName = positionName;
	}
	public int getStaffId() {
		return StaffId;
	}
	public void setStaffId(int staffId) {
		StaffId = staffId;
	}
	public int getPdId() {
		return PdId;
	}
	public void setPdId(int pdId) {
		PdId = pdId;
	}
	public int getDeptId() {
		return DeptId;
	}
	public void setDeptId(int deptId) {
		DeptId = deptId;
	}
	public String getScheName() {
		return ScheName;
	}
	public void setScheName(String scheName) {
		ScheName = scheName;
	}
	public String getStaffName() {
		return StaffName;
	}
	public void setStaffName(String staffName) {
		StaffName = staffName;
	}
	public String getBeginWorkTime() {
		return BeginWorkTime;
	}
	public void setBeginWorkTime(String beginWorkTime) {
		BeginWorkTime = beginWorkTime;
	}
	public String getRBeginWorkTime() {
		return RBeginWorkTime;
	}
	public void setRBeginWorkTime(String rBeginWorkTime) {
		RBeginWorkTime = rBeginWorkTime;
	}
	public String getRTeamName() {
		return RTeamName;
	}
	public void setRTeamName(String rTeamName) {
		RTeamName = rTeamName;
	}
	public Boolean getConflict() {
		return Conflict;
	}
	public void setConflict(Boolean conflict) {
		Conflict = conflict;
	}
	public String getRStaffIds() {
		return RStaffIds;
	}
	public void setRStaffIds(String rStaffIds) {
		RStaffIds = rStaffIds;
	}
	public String getAcceptDttm() {
		return AcceptDttm;
	}
	public void setAcceptDttm(String acceptDttm) {
		AcceptDttm = acceptDttm;
	}
	public int getTwId() {
		return TwId;
	}
	public void setTwId(int twId) {
		TwId = twId;
	}
	public int getTssId() {
		return TssId;
	}
	public void setTssId(int tssId) {
		TssId = tssId;
	}
	public int getDevFixStaffId() {
		return DevFixStaffId;
	}
	public void setDevFixStaffId(int devFixStaffId) {
		DevFixStaffId = devFixStaffId;
	}
	public int getTrainId() {
		return TrainId;
	}
	public void setTrainId(int trainId) {
		TrainId = trainId;
	}
	public String getWorkspace() {
		return Workspace;
	}
	public void setWorkspace(String workspace) {
		Workspace = workspace;
	}
	public T getTB_Task_Schedule_Actor_Exc() {
		return TB_Task_Schedule_Actor_Exc;
	}
	public void setTB_Task_Schedule_Actor_Exc(T tB_Task_Schedule_Actor_Exc) {
		TB_Task_Schedule_Actor_Exc = tB_Task_Schedule_Actor_Exc;
	}
	public T getTB_Task_Schedule_Water_Application() {
		return TB_Task_Schedule_Water_Application;
	}
	public void setTB_Task_Schedule_Water_Application(
			T tB_Task_Schedule_Water_Application) {
		TB_Task_Schedule_Water_Application = tB_Task_Schedule_Water_Application;
	}
	public T TB_Task_Schedule_Water_Application;
}
