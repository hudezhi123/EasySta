package easyway.Mobile.Data;

import java.util.Date;

public class TB_Shift {
	public long Shift_Id, Shift_Out_TeamId, Shift_Out_Shaff_Id,
			Shift_In_TeamId, Shift_In_Shaff_Id;
	public Date Shift_Out_Dt, Shift_In_Dt;
	public String Shift_Out_Staff_Name, Shift_In_Staff_Name, Shift_Out_Team;

	public TB_Shift() {
		Shift_Id = 0;
		Shift_Out_TeamId = 0;
		Shift_Out_Shaff_Id = 0;
		Shift_In_TeamId = 0;
		Shift_In_Shaff_Id = 0;
		Shift_Out_Dt = new Date();
		Shift_In_Dt = new Date();
		Shift_Out_Staff_Name = "";
		Shift_In_Staff_Name = "";
		Shift_Out_Team = "";
	}
}
