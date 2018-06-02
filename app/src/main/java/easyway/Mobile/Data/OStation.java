package easyway.Mobile.Data;

import java.io.Serializable;

 public class OStation implements Serializable {
	private String OStationName;

	private String OStationValue;

	public String getOStationName() {
		return OStationName;
	}

	public void setOStationName(String oStationName) {
		OStationName = oStationName;
	}

	public String getOStationValue() {
		return OStationValue;
	}

	public void setOStationValue(String oStationValue) {
		OStationValue = oStationValue;
	}

	@Override
	public String toString() {
		return "OStation [OStationName=" + OStationName
				+ ", OStationValue=" + OStationValue + "]";
	}

}