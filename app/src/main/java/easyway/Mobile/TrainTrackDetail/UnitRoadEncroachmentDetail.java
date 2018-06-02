package easyway.Mobile.TrainTrackDetail;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Station;
import easyway.Mobile.Data.TB_Lane_AllMPS;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import easyway.Mobile.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 *  股道占用详细信息列表
 */
public class UnitRoadEncroachmentDetail extends ActivityEx implements OnClickListener {
	private PullRefreshListView gv_queryList;
	private UnitRoadEncroachDetailAdapter mAdapter;
	private ArrayList<TB_Lane_AllMPS> mList = new ArrayList<TB_Lane_AllMPS>();
	private ArrayList<TB_Lane_AllMPS> turelist = new ArrayList<TB_Lane_AllMPS>();
	private String trackName, laneDir = "-2", laneStatus = "";
	private boolean isPullRefresh = false;
	private Station mStation;
	private TextView txtStation;
	private EditText txTrackNo;
	private CheckBox chkLaneDirUp;
	private CheckBox chkLaneDirDown;
	private CheckBox chkLaneStatusBusying;
	private CheckBox chkLaneStatusBusy;
	private CheckBox chkLaneStatusFree;

	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;
	
	private String laneName = "";
	private String planDate = "";

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETDATA_FAIL:
				if (isPullRefresh) {
					isPullRefresh = false;
					gv_queryList.onRefreshComplete();
				}
				showToast(errMsg);
				break;
			case MSG_GETDATA_SUCCEED:
				if (isPullRefresh) {
					isPullRefresh = false;
					gv_queryList.onRefreshComplete();
				}

				if (mList == null) {
					mList = new ArrayList<TB_Lane_AllMPS>();
				} else {
					mList.clear();
				}
				if (turelist == null) {
					turelist = new ArrayList<TB_Lane_AllMPS>();
				} else {
					turelist.clear();
				}
				
				mList.addAll((ArrayList<TB_Lane_AllMPS>) msg.obj);
//				ArrayList<TB_Lane_AllMPS> use = new ArrayList<TB_Lane_AllMPS>();
//				ArrayList<TB_Lane_AllMPS> noUse = new ArrayList<TB_Lane_AllMPS>();
//				for (int i = 0; i < mList.size(); i++) {
//
//					TB_AreaOccupancy base = mList.get(i);
//					int baseState = Integer.valueOf(base.StationStatus);
//					if(baseState == 0){
//						noUse.add(base);
//					}else{
//						use.add(base);
//					}
//				}
//				turelist.addAll(use);
//				turelist.addAll(noUse);
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unitroad_occupancy);

		mStation = Property.OwnStation;
        laneName = getIntent().getStringExtra("laneName");
        planDate = getIntent().getStringExtra("planDate");
        
		initView();
		getData();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_occupation);

		txtStation = (TextView) findViewById(R.id.station);
        
		if (mStation != null) {
			txtStation.setText("(" + mStation.Name + ")");
		}
		txtStation.setVisibility(View.VISIBLE);
		txtStation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Property.ChargeStation == null)
					return;

				if (Property.ChargeStation.size() == 0)
					return;

				showSelectStaionDlg();
			}

		});

		txTrackNo = (EditText) findViewById(R.id.search_edit);
		chkLaneDirUp = (CheckBox) findViewById(R.id.chkLaneDirUp);
		chkLaneDirDown = (CheckBox) findViewById(R.id.chkLaneDirDown);
		chkLaneStatusBusying = (CheckBox) findViewById(R.id.chkLaneStatusBusying);
		chkLaneStatusBusy = (CheckBox) findViewById(R.id.chkLaneStatusBusy);
		chkLaneStatusFree = (CheckBox) findViewById(R.id.chkLaneStatusFree);

		Button searchBtn = (Button) findViewById(R.id.btnset);
		// searchBtn.setVisibility(View.VISIBLE);
		searchBtn.setVisibility(View.INVISIBLE);
		searchBtn.setText(R.string.search);
		searchBtn.setOnClickListener(this);

		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);

		gv_queryList = (PullRefreshListView) findViewById(R.id.unitroadgvdata);
		mAdapter = new UnitRoadEncroachDetailAdapter(UnitRoadEncroachmentDetail.this,
				mList);
		gv_queryList.setAdapter(mAdapter);
		gv_queryList.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});

	}

	// 获取股道占用信息
	private void SearchAreaOccupancy() {
		ArrayList<TB_Lane_AllMPS> list = new ArrayList<TB_Lane_AllMPS>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		if (!StringUtil.isNullOrEmpty(planDate)) {
			parmValues.put("planDate",planDate);
		}
		parmValues.put("laneName", laneName);
		if (mStation != null)
			parmValues.put("stationCode", mStation.Code);

		String methodPath = Constant.MP_TRAININFO;
		String methodName = Constant.MN_GET_LANEALLMPS;

		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		LogUtil.e("股道占用详细信息" + result);
		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				TB_Lane_AllMPS OccupancyStatus = new TB_Lane_AllMPS();
				OccupancyStatus.ID = JsonUtil.GetJsonObjIntValue(
						jsonObj, "ID");
				OccupancyStatus.PLANDATE_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "PLANDATE_PTTI");
				OccupancyStatus.TRNUM_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "TRNUM_PTTI");
				OccupancyStatus.TRNO_PRO = JsonUtil.GetJsonObjStringValue(
						jsonObj, "TRNO_PRO");
				OccupancyStatus.TRRUN_PRO = JsonUtil.GetJsonObjIntValue(
						jsonObj, "TRRUN_PRO");
				OccupancyStatus.STRTSTN_PRO = JsonUtil.GetJsonObjStringValue(
						jsonObj, "STRTSTN_PRO");
				OccupancyStatus.TILSTN_PRO = JsonUtil.GetJsonObjStringValue(
						jsonObj, "TILSTN_PRO");
				OccupancyStatus.DEPADATE_PTTI = JsonUtil
						.GetJsonObjStringValue(jsonObj, "DEPADATE_PTTI");
				OccupancyStatus.ARRTIMR_PTTI = JsonUtil
						.GetJsonObjStringValue(jsonObj, "ARRTIMR_PTTI");
				OccupancyStatus.ALATETIME_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "ALATETIME_PTTI");
				OccupancyStatus.ARRTIMR_PTTI_T = JsonUtil.GetJsonObjStringValue(jsonObj,
						"ARRTIMR_PTTI_T");
				OccupancyStatus.DEPATIME_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "DEPATIME_PTTI");
				OccupancyStatus.DLATETIME_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "DLATETIME_PTTI");
				OccupancyStatus.DLATECAUSE_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "DLATECAUSE_PTTI");
				OccupancyStatus.DEPATIME_PTTI_T = JsonUtil.GetJsonObjStringValue(
						jsonObj, "DEPATIME_PTTI_T");
				OccupancyStatus.LANE_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "LANE_PTTI");
				OccupancyStatus.PLATFORM_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "PLATFORM_PTTI");
				OccupancyStatus.WAITROOM_PTTI = JsonUtil
						.GetJsonObjStringValue(jsonObj, "WAITROOM_PTTI");
				OccupancyStatus.INTICKET_PTTI = JsonUtil
						.GetJsonObjStringValue(jsonObj, "INTICKET_PTTI");
				OccupancyStatus.OUTTICKET_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "OUTTICKET_PTTI");
				OccupancyStatus.INCHECKTIME_PTTI = JsonUtil.GetJsonObjStringValue(jsonObj,
						"INCHECKTIME_PTTI");
				OccupancyStatus.INSTOPTIME_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "INSTOPTIME_PTTI");
				OccupancyStatus.GRPNO_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "GRPNO_PTTI");
				OccupancyStatus.GRPORDER_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "GRPORDER_PTTI");
				OccupancyStatus.STOPSTATES_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "STOPSTATES_PTTI");
				OccupancyStatus.INTICKETST_PTTI = JsonUtil.GetJsonObjStringValue(
						jsonObj, "INTICKETST_PTTI");
				OccupancyStatus.TRTYPE = JsonUtil
						.GetJsonObjStringValue(jsonObj, "TRTYPE");
				OccupancyStatus.ALATESTNAME_PTTI = JsonUtil
						.GetJsonObjStringValue(jsonObj, "ALATESTNAME_PTTI");
				OccupancyStatus.StationCode = JsonUtil.GetJsonObjStringValue(
						jsonObj, "StationCode");
				OccupancyStatus.OrderDttm = JsonUtil.GetJsonObjStringValue(jsonObj,
						"OrderDttm");
				list.add(OccupancyStatus);
			}

			Message message = new Message();
			message.what = MSG_GETDATA_SUCCEED;
			message.obj = list;
			myhandle.sendMessage(message);
			break;
		case Constant.EXCEPTION:
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			break;
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			break;
		}
	}

	// 获取数据
	private void getData() {
		showProgressDialog(R.string.GettingData);
		SearchInfo();

		new Thread() {
			public void run() {
				SearchAreaOccupancy();
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnset:
			LinearLayout layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
			if (layoutSearch.getVisibility() == View.VISIBLE) {
				layoutSearch.setVisibility(View.GONE);
			} else {
				layoutSearch.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btnSearch:
			getData();
			break;
		default:
			break;
		}
	}

	// 检索股道信息
	private void SearchInfo() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(txTrackNo.getWindowToken(),
				0);

		trackName = txTrackNo.getText().toString();
		if (chkLaneDirUp.isChecked() || chkLaneDirDown.isChecked()) {
			if (chkLaneDirUp.isChecked() && chkLaneDirDown.isChecked()) {
				laneDir = "-1";
			} else {
				if (chkLaneDirUp.isChecked()) {
					laneDir = "0";
				}

				if (chkLaneDirDown.isChecked()) {
					laneDir = "1";
				}
			}
		} else {
			laneDir = "-2";
		}

		laneStatus = "";
		if (chkLaneStatusBusying.isChecked() || chkLaneStatusBusy.isChecked()
				|| chkLaneStatusFree.isChecked()) {
			if (chkLaneStatusBusying.isChecked()) {
				laneStatus = "1";
			}

			if (chkLaneStatusBusy.isChecked()) {
				laneStatus += ",2";
			}

			if (chkLaneStatusFree.isChecked()) {
				laneStatus += ",0";
			}
		}

		if (laneStatus.startsWith(",")) {
			laneStatus = laneStatus.subSequence(1, laneStatus.length())
					.toString();
		}
	}

	// 选择所属站
	private void showSelectStaionDlg() {
		if (null != Property.ChargeStation) {
			String[] m = new String[Property.ChargeStation.size()];
			for (int i = 0; i < Property.ChargeStation.size(); i++) {
				m[i] = Property.ChargeStation.get(i).Name;
			}

			AlertDialog dlg = new AlertDialog.Builder(UnitRoadEncroachmentDetail.this)
					.setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < Property.ChargeStation.size()) {
								mStation = Property.ChargeStation.get(item);

								if (mStation != null) {
									txtStation.setText("(" + mStation.Name
											+ ")");
									getData();
								}
							}
						}
					}).create();
			dlg.show();
		}
	};
}
