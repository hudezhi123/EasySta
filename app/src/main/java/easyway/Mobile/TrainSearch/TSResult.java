package easyway.Mobile.TrainSearch;

import java.io.Serializable;

public class TSResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String TrainNo;
	public String StationOrigin;
	public String StationArr;
	public String DepTime;
	public int DepDate;
	public String ArrTime;
	public int ArrDate;
}
