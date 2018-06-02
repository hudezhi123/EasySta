package easyway.Mobile.Broadcast;

//广播信息
public class BroadcastInfo {
	public long id;
	public String PlayTime;
	public String OpStatus;
	public String Category;
	public String Title;
	public String Content;
	public String Area;
	public String IDENO_EQTs;
	public boolean IsSelected;

	@Override
	public String toString() {
		return "BroadcastInfo [id=" + id + ", PlayTime=" + PlayTime
				+ ", OpStatus=" + OpStatus + ", Category=" + Category
				+ ", Title=" + Title + ", Content=" + Content + ", Area="
				+ Area + ", IDENO_EQTs=" + IDENO_EQTs + ", IsSelected="
				+ IsSelected + "]";
	}
}
