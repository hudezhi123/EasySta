package easyway.Mobile.ShiftData;

import java.io.Serializable;

public class Shift2InGetAllShiftOutData implements Serializable{
	private int Shift_Id;
	private int Shift_WorkType;
	private int Shift_Out_DeptId;
	private String Shift_Out_DeptName;
	private long Shift_Out_StaffId;
	private String Shift_Out_StaffName;
	private String Shift_Out_Remark;
	private String Shift_Out_Voice;
	private String Shift_Out_Dttm;
	private int Shift_In_DeptId;
	private String Shift_In_DeptName;
	private int Shift_In_StaffId;
	private String Shift_In_StaffName;
	private String Shift_In_Dttm;
	public int getShift_Id() {
		return Shift_Id;
	}
	public void setShift_Id(int shift_Id) {
		Shift_Id = shift_Id;
	}
	public int getShift_WorkType() {
		return Shift_WorkType;
	}
	public void setShift_WorkType(int shift_WorkType) {
		Shift_WorkType = shift_WorkType;
	}
	public int getShift_Out_DeptId() {
		return Shift_Out_DeptId;
	}
	public void setShift_Out_DeptId(int shift_Out_DeptId) {
		Shift_Out_DeptId = shift_Out_DeptId;
	}
	public String getShift_Out_DeptName() {
		return Shift_Out_DeptName;
	}
	public void setShift_Out_DeptName(String shift_Out_DeptName) {
		Shift_Out_DeptName = shift_Out_DeptName;
	}
	public long getShift_Out_StaffId() {
		return Shift_Out_StaffId;
	}
	public void setShift_Out_StaffId(long shift_Out_StaffId) {
		Shift_Out_StaffId = shift_Out_StaffId;
	}
	public String getShift_Out_StaffName() {
		return Shift_Out_StaffName;
	}
	public void setShift_Out_StaffName(String shift_Out_StaffName) {
		Shift_Out_StaffName = shift_Out_StaffName;
	}
	public String getShift_Out_Remark() {
		return Shift_Out_Remark;
	}
	public void setShift_Out_Remark(String shift_Out_Remark) {
		Shift_Out_Remark = shift_Out_Remark;
	}
	public String getShift_Out_Voice() {
		return Shift_Out_Voice;
	}
	public void setShift_Out_Voice(String shift_Out_Voice) {
		Shift_Out_Voice = shift_Out_Voice;
	}
	public String getShift_Out_Dttm() {
		return Shift_Out_Dttm;
	}
	public void setShift_Out_Dttm(String shift_Out_Dttm) {
		Shift_Out_Dttm = shift_Out_Dttm;
	}
	public int getShift_In_DeptId() {
		return Shift_In_DeptId;
	}
	public void setShift_In_DeptId(int shift_In_DeptId) {
		Shift_In_DeptId = shift_In_DeptId;
	}
	public String getShift_In_DeptName() {
		return Shift_In_DeptName;
	}
	public void setShift_In_DeptName(String shift_In_DeptName) {
		Shift_In_DeptName = shift_In_DeptName;
	}
	public int getShift_In_StaffId() {
		return Shift_In_StaffId;
	}
	public void setShift_In_StaffId(int shift_In_StaffId) {
		Shift_In_StaffId = shift_In_StaffId;
	}
	public String getShift_In_StaffName() {
		return Shift_In_StaffName;
	}
	public void setShift_In_StaffName(String shift_In_StaffName) {
		Shift_In_StaffName = shift_In_StaffName;
	}
	public String getShift_In_Dttm() {
		return Shift_In_Dttm;
	}
	public void setShift_In_Dttm(String shift_In_Dttm) {
		Shift_In_Dttm = shift_In_Dttm;
	}
	@Override
	public String toString() {
		return "Shift2InGetAllShiftOutData [Shift_Id=" + Shift_Id
				+ ", Shift_WorkType=" + Shift_WorkType + ", Shift_Out_DeptId="
				+ Shift_Out_DeptId + ", Shift_Out_DeptName=" + Shift_Out_DeptName
				+ ", Shift_Out_StaffId=" + Shift_Out_StaffId
				+ ", Shift_Out_StaffName=" + Shift_Out_StaffName
				+ ", Shift_Out_Remark=" + Shift_Out_Remark + ", Shift_Out_Voice="
				+ Shift_Out_Voice + ", Shift_Out_Dttm=" + Shift_Out_Dttm
				+ ", Shift_In_DeptId=" + Shift_In_DeptId + ", Shift_In_DeptName="
				+ Shift_In_DeptName + ", Shift_In_StaffId=" + Shift_In_StaffId
				+ ", Shift_In_StaffName=" + Shift_In_StaffName + ", Shift_In_Dttm="
				+ Shift_In_Dttm + "]";
	}


}
