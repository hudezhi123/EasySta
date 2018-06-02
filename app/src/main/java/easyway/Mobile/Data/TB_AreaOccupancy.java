package easyway.Mobile.Data;

// 股道占用
public class TB_AreaOccupancy {
//	public String AREANAME;							// 股道
//	public String ID;										// 车次
//	public String TRNO_PRO;
//	public String GRPNO_PTTI;							// 编组
//	public String PLATFORM_PTTI;					// 站台
//	public String ARRTIMR_PTTI;						// 到达时间
//	public String DEPATIME_PTTI;						// 发车时间
//	public String OccupancyTime;						// 停留时长
//	public String StationStatus;							// 状态
//	public String PTRNO_PRO;							// 预办车次
//	public String STATIONBEGINTIME;				// 停留起始时间
//	public String STATIONENDTIME;				// 停留结束时间


	/**
	 * AREANAME : 5股道
	 * StationStatus : 1
	 * TRNO_PRO : G858(西安北==>武汉)
	 * GRPNO_PTTI : 16
	 * PLATFORM_PTTI : 5站台
	 * ARRTIMR_PTTI : --:--
	 * DEPATIME_PTTI : 14:59
	 * PLANDATE_PTTI : 2017-02-03
	 * PLANENDDATE_PTTI : 2017-02-03
	 * STATIONBEGINTIME : 2017-02-03 14:39:00
	 * STATIONENDTIME : 2017-02-03 14:59:00
	 * StaySpan : 20
	 * PTRNO_PRO :
	 * OrderPtti : 升序
	 * ID : 185588
	 * VID : 1580
	 * LaneNo : 5
	 */

	public String AREANAME;
	public int StationStatus;
	public String TRNO_PRO;
	public String GRPNO_PTTI;
	public String PLATFORM_PTTI;
	public String ARRTIMR_PTTI;
	public String DEPATIME_PTTI;
	public String PLANDATE_PTTI;
	public String PLANENDDATE_PTTI;
	public String STATIONBEGINTIME;
	public String STATIONENDTIME;
	public String StaySpan;
	public String PTRNO_PRO;
	public String OrderPtti;
	public int ID;
	public int VID;
	public int LaneNo;
}
