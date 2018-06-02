package easyway.Mobile.Shift;

import java.io.Serializable;
import java.util.Date;

public class New_ShiftOut_Base implements Serializable{
	
	//交班人
	public String shiftOutName;
	//交班时间
	public Date ShiftOutDt;
	//录音附件地址
	public String VoicePath;
	//任务类型
	public String WorkTypeName;
	
	public int WorkTypeId;
	
	public long TeamId;
	public long ShiftId;
	
	public New_ShiftOut_Base(String shiftOutName, Date shiftOutDt,
			String voicePath, String workTypeName, int woekTyoeId,long teamId,long shiftId) {
		super();
		this.shiftOutName = shiftOutName;
		ShiftOutDt = shiftOutDt;
		VoicePath = voicePath;
		WorkTypeName = workTypeName;
		TeamId = teamId;
		ShiftId = shiftId;
		WorkTypeId = woekTyoeId;
	}


	public New_ShiftOut_Base() {
		super();
	}


	@Override
	public String toString() {
		return "New_ShiftOut_Base [shiftOutName=" + shiftOutName
				+ ", ShiftOutDt=" + ShiftOutDt + ", VoicePath=" + VoicePath
				+ ", WorkTypeName=" + WorkTypeName + ", WorkTypeId="
				+ WorkTypeId + ", TeamId=" + TeamId + ", ShiftId=" + ShiftId
				+ "]";
	}


	public String getShiftOutName() {
		return shiftOutName;
	}


	public void setShiftOutName(String shiftOutName) {
		this.shiftOutName = shiftOutName;
	}


	public Date getShiftOutDt() {
		return ShiftOutDt;
	}


	public void setShiftOutDt(Date shiftOutDt) {
		ShiftOutDt = shiftOutDt;
	}


	public String getVoicePath() {
		return VoicePath;
	}


	public void setVoicePath(String voicePath) {
		VoicePath = voicePath;
	}


	public String getWorkTypeName() {
		return WorkTypeName;
	}


	public void setWorkTypeName(String workTypeName) {
		WorkTypeName = workTypeName;
	}


	public long getTeamId() {
		return TeamId;
	}


	public void setTeamId(long teamId) {
		TeamId = teamId;
	}


	public long getShiftId() {
		return ShiftId;
	}


	public void setShiftId(long shiftId) {
		ShiftId = shiftId;
	}


	public int getWorkTypeId() {
		return WorkTypeId;
	}


	public void setWorkTypeId(int workTypeId) {
		WorkTypeId = workTypeId;
	}
	
	
	
}
