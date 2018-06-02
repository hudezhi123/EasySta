package easyway.Mobile.LiveCase;

import java.util.Date;

// 现场情况
public class LiveCaseReport {
	public String ReportedType, ReportedLevel, Reporteder, Remarks,
			AppendixUrl, ConfirmStatus;
	public long id = 0;
	public Date ReportedTime;

	// public Date ReportedTime;

	public LiveCaseReport() {
		ReportedType = "";
		ReportedLevel = "";
		ReportedTime = new Date();
		Reporteder = "";
		Remarks = "";
		ConfirmStatus = "";
		AppendixUrl = "";
		id = 0;
	}
}
