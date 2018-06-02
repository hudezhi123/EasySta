package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

public class TrainGoTo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long Id = 0;
	public String TRNO_PRO;		// 火车车次
	public String TRAIN_TYPE;		// 车型
	public String STRTSTN_TT;		// 始发站
	public String TILSTN_TT;			// 终到站
	public String INTICKETST_PTTI;	// 检票状态
	public String LANE_PTTI;		// 股道
	public String INTICKET_PTTI;		// 检票口
	public String OUTTICKET_PTTI;	// 出站口
	public String ARRTIMR_PTTI;		// 到达时间
	public String DEPATIME_PTTI;		// 离开时间
	public String GRPNO_PTTI;			// 编组
	public String  GRPORDER_PTTI;	//方向，升序还是降序
	public String FirstTime;				// 始发站出发时间
	public String DestDate;				// 终到站到达时间
	public String DestTime;				// 路途天数
	public String PLATFORM_PTTI;	// 站台
	public String Status;					// 状态
	public String Color;					// 颜色
	public String PLANDATE_PTTI;
	public String TakeOn; //担当段
	public String TrainType; //车型
	public String StationCode;
	
	// TRINFO_TT,WAITROOM_PTTI,ALATETIME_PTTI,DLATETIME_PTTI,TrainRouting,Capacity,SCRS,XCRS,CZDD
	
	public static ArrayList<TrainGoTo> ParseFromString(String result) {
		ArrayList<TrainGoTo> list = new ArrayList<TrainGoTo>();
		
		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);

			TrainGoTo trainGoto = new TrainGoTo();
			trainGoto.Id = JsonUtil.GetJsonObjLongValue(jsonObj,
					"id");
			trainGoto.TRNO_PRO = JsonUtil.GetJsonObjStringValue(
					jsonObj, "TRNO_PRO");
			trainGoto.PLANDATE_PTTI = JsonUtil.GetJsonObjStringValue(
					jsonObj, "PLANDATE_PTTI");
			trainGoto.STRTSTN_TT = JsonUtil.GetJsonObjStringValue(
					jsonObj, "STRTSTN_TT");
			trainGoto.TILSTN_TT = JsonUtil.GetJsonObjStringValue(
					jsonObj, "TILSTN_TT");
			trainGoto.LANE_PTTI = JsonUtil.GetJsonObjStringValue(
					jsonObj, "LANE_PTTI");
			trainGoto.PLATFORM_PTTI = JsonUtil.GetJsonObjStringValue(
					jsonObj, "PLATFORM_PTTI");
			trainGoto.DEPATIME_PTTI = JsonUtil
					.GetJsonObjStringValue(jsonObj, "DEPATIME_PTTI");
			trainGoto.ARRTIMR_PTTI = JsonUtil
					.GetJsonObjStringValue(jsonObj, "ARRTIMR_PTTI");
			trainGoto.INTICKET_PTTI = JsonUtil
					.GetJsonObjStringValue(jsonObj, "INTICKET_PTTI");
			trainGoto.OUTTICKET_PTTI = JsonUtil
					.GetJsonObjStringValue(jsonObj, "OUTTICKET_PTTI");
			trainGoto.Status = JsonUtil
					.GetJsonObjStringValue(jsonObj, "Status");
			trainGoto.Color = JsonUtil
					.GetJsonObjStringValue(jsonObj, "Color");
			trainGoto.GRPNO_PTTI = JsonUtil.GetJsonObjStringValue(
					jsonObj, "GRPNO_PTTI");
			trainGoto.GRPORDER_PTTI=JsonUtil.GetJsonObjStringValue(
					jsonObj, "GRPORDER_PTTI");
			trainGoto.TRAIN_TYPE = JsonUtil.GetJsonObjStringValue(
					jsonObj, "TRAIN_TYPE");
			trainGoto.INTICKETST_PTTI = JsonUtil
					.GetJsonObjStringValue(jsonObj, "INTICKETST_PTTI");
			trainGoto.FirstTime = JsonUtil.GetJsonObjStringValue(
					jsonObj, "FirstTime");
			trainGoto.DestDate = JsonUtil.GetJsonObjStringValue(
					jsonObj, "DestDate");
			trainGoto.DestTime = JsonUtil.GetJsonObjStringValue(
					jsonObj, "DestTime");

			trainGoto.TakeOn = JsonUtil.GetJsonObjStringValue(
					jsonObj, "TakeOn");
			trainGoto.TrainType=JsonUtil.GetJsonObjStringValue(
					jsonObj, "TrainType");

			trainGoto.StationCode=JsonUtil.GetJsonObjStringValue(
					jsonObj, "StationCode");
			
			list.add(trainGoto);
		}
		
		return list;
	}

	@Override
	public String toString() {
		return "TrainGoTo{" +
				"Id=" + Id +
				", TRNO_PRO='" + TRNO_PRO + '\'' +
				", TRAIN_TYPE='" + TRAIN_TYPE + '\'' +
				", STRTSTN_TT='" + STRTSTN_TT + '\'' +
				", TILSTN_TT='" + TILSTN_TT + '\'' +
				", INTICKETST_PTTI='" + INTICKETST_PTTI + '\'' +
				", LANE_PTTI='" + LANE_PTTI + '\'' +
				", INTICKET_PTTI='" + INTICKET_PTTI + '\'' +
				", OUTTICKET_PTTI='" + OUTTICKET_PTTI + '\'' +
				", ARRTIMR_PTTI='" + ARRTIMR_PTTI + '\'' +
				", DEPATIME_PTTI='" + DEPATIME_PTTI + '\'' +
				", GRPNO_PTTI='" + GRPNO_PTTI + '\'' +
				", GRPORDER_PTTI='" + GRPORDER_PTTI + '\'' +
				", FirstTime='" + FirstTime + '\'' +
				", DestDate='" + DestDate + '\'' +
				", DestTime='" + DestTime + '\'' +
				", PLATFORM_PTTI='" + PLATFORM_PTTI + '\'' +
				", Status='" + Status + '\'' +
				", Color='" + Color + '\'' +
				", PLANDATE_PTTI=" + PLANDATE_PTTI +
				", TakeOn='" + TakeOn + '\'' +
				", TrainType='" + TrainType + '\'' +
				", StationCode='" + StationCode + '\'' +
				'}';
	}
}
