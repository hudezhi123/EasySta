package easyway.Mobile.Data;

import java.io.Serializable;

/*
 * 设备报障
 */
public class DevFaultReport implements Serializable {
	public long Id;
	public long UsingDeptId;
	public long UsingEmpoyeeId;
	public String DevCode;
	public String DevName;
	public String DevCate;
	public String Location;
	public String FaultContent;
	public String AppendixUrl;
	public String DevIssueImgFrom;
	public String Reporteder;
	public String ReportedTime;
	public String AppStatus;
	// 任务区域
	public String TwName;
	public boolean IsGroup;

	@Override
	public String toString() {
		return "DevFaultReport [Id=" + Id + ", UsingDeptId=" + UsingDeptId
				+ ", UsingEmpoyeeId=" + UsingEmpoyeeId + ", DevCode=" + DevCode
				+ ", DevName=" + DevName + ", DevCate=" + DevCate
				+ ", Location=" + Location + ", FaultContent=" + FaultContent
				+ ", AppendixUrl=" + AppendixUrl + ", DevIssueImgFrom="
				+ DevIssueImgFrom + ", Reporteder=" + Reporteder
				+ ", ReportedTime=" + ReportedTime + ", AppStatus=" + AppStatus
				+ ", TwName=" + TwName + ", IsGroup=" + IsGroup + "]";
	}

}
