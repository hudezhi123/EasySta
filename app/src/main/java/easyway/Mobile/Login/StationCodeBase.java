package easyway.Mobile.Login;

import java.io.Serializable;

public class StationCodeBase implements Serializable{
	
	public String StationName;
	public String StationCode;
	
	public StationCodeBase(String stationName, String stationCode) {
		super();
		StationName = stationName;
		StationCode = stationCode;
	}

	public StationCodeBase() {
		super();
	}

	@Override
	public String toString() {
		return "StationCodeBase [StationName=" + StationName + ", StationCode="
				+ StationCode + "]";
	}
	
	
}
