package easyway.Mobile.bean;

import java.io.Serializable;

public class TrainBase implements Serializable{
	
	/**
	 * 车次号
	 */
	public String TRNO_PRO;
	/**
	 * 始发站
	 */
	public String STRTSTN_TT;
	/**
	 * 终到站
	 */
	public String TILSTN_TT;
	/**
	 * 始发站发点
	 */
	public String DepaTime;
	/**
	 * 当前站到点
	 */
	public String StationArrTime;
	/**
	 * 当前站发点
	 */
	public String StationDepaTime;
	/**
	 * 当前站类型，分为始发站、终到站与途经站
	 */
	public String StationAttr;
	/**
	 * 里程数，自始发站到达本站已经运行历程,单位为“公里
	 */
	public int Miles;
	
	public TrainBase(String tRNO_PRO, String sTRTSTN_TT, String tILSTN_TT,
			String depaTime, String stationArrTime, String stationDepaTime,
			String stationAttr, int miles) {
		super();
		TRNO_PRO = tRNO_PRO;
		STRTSTN_TT = sTRTSTN_TT;
		TILSTN_TT = tILSTN_TT;
		DepaTime = depaTime;
		StationArrTime = stationArrTime;
		StationDepaTime = stationDepaTime;
		StationAttr = stationAttr;
		Miles = miles;
	}

	public TrainBase() {
		super();
	}

	@Override
	public String toString() {
		return "TrainBase [TRNO_PRO=" + TRNO_PRO + ", STRTSTN_TT=" + STRTSTN_TT
				+ ", TILSTN_TT=" + TILSTN_TT + ", DepaTime=" + DepaTime
				+ ", StationArrTime=" + StationArrTime + ", StationDepaTime="
				+ StationDepaTime + ", StationAttr=" + StationAttr + ", Miles="
				+ Miles + "]";
	}
	
	
}
