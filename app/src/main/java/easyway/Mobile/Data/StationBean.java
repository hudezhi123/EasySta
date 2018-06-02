package easyway.Mobile.Data;

import java.io.Serializable;

public class StationBean implements Serializable {
   private String StationName;

   private String StationCode;

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public String getStationCode() {
		return StationCode;
	}

	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}

	@Override
	public String toString() {
		return "StationBean{" +
				"StationName='" + StationName + '\'' +
				", StationCode='" + StationCode + '\'' +
				'}';
	}
}