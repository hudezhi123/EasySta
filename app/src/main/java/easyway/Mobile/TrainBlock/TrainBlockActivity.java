package easyway.Mobile.TrainBlock;

import easyway.Mobile.ActivityEx;
import android.view.View;
import android.view.View.OnClickListener;

/*
 * 车辆闭塞
 */
public class TrainBlockActivity extends ActivityEx implements OnClickListener {

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
//	private PullRefreshListView gv_queryList;
//	private UnitRoadEncroachDetailAdapter mAdapter;
//	private ArrayList<TB_AreaOccupancy> mList = new ArrayList<TB_AreaOccupancy>();
//	private String trackName, laneDir = "-2", laneStatus = "";
//	private boolean isPullRefresh = false;
//	private Station mStation;
//	private TextView txtStation;
//	private EditText txTrackNo;
//	private CheckBox chkLaneDirUp;
//	private CheckBox chkLaneDirDown;
//	private CheckBox chkLaneStatusBusying;
//	private CheckBox chkLaneStatusBusy;
//	private CheckBox chkLaneStatusFree;
//
//	private final int MSG_GETDATA_FAIL = 0;
//	private final int MSG_GETDATA_SUCCEED = 1;
//
//	@SuppressLint("HandlerLeak")
//	private Handler myhandle = new Handler() {
//		@SuppressWarnings("unchecked")
//		@Override
//		public void handleMessage(Message msg) {
//			closeProgressDialog();
//			switch (msg.what) {
//			case MSG_GETDATA_FAIL:
//				if (isPullRefresh) {
//					isPullRefresh = false;
//					gv_queryList.onRefreshComplete();
//				}
//				showToast(TrainBlockActivity.this.msg);
//				break;
//			case MSG_GETDATA_SUCCEED:
//				if (isPullRefresh) {
//					isPullRefresh = false;
//					gv_queryList.onRefreshComplete();
//				}
//
//				if (mList == null) {
//					mList = new ArrayList<TB_AreaOccupancy>();
//				} else {
//					mList.clear();
//				}
//
//				mList.addAll((ArrayList<TB_AreaOccupancy>) msg.obj);
//				mAdapter.setData(mList);
//				mAdapter.notifyDataSetChanged();
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.unitroad_occupancy);
//
//		mStation = Property.OwnStation;
//
//		initView();
//		getData();
//	}
//
//	private void initView() {
//		TextView title = (TextView) findViewById(R.id.title);
//		title.setText(R.string.title_occupation);
//
//		txtStation = (TextView) findViewById(R.id.station);
//
//		if (mStation != null) {
//			txtStation.setText("(" + mStation.Name + ")");
//		}
//		txtStation.setVisibility(View.VISIBLE);
//		txtStation.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (Property.ChargeStation == null)
//					return;
//
//				if (Property.ChargeStation.size() == 0)
//					return;
//
//				showSelectStaionDlg();
//			}
//
//		});
//
//		txTrackNo = (EditText) findViewById(R.id.search_edit);
//		chkLaneDirUp = (CheckBox) findViewById(R.id.chkLaneDirUp);
//		chkLaneDirDown = (CheckBox) findViewById(R.id.chkLaneDirDown);
//		chkLaneStatusBusying = (CheckBox) findViewById(R.id.chkLaneStatusBusying);
//		chkLaneStatusBusy = (CheckBox) findViewById(R.id.chkLaneStatusBusy);
//		chkLaneStatusFree = (CheckBox) findViewById(R.id.chkLaneStatusFree);
//
//		Button searchBtn = (Button) findViewById(R.id.btnset);
////		searchBtn.setVisibility(View.VISIBLE);
//		searchBtn.setVisibility(View.INVISIBLE);
//		searchBtn.setText(R.string.search);
//		searchBtn.setOnClickListener(this);
//
//		Button btnSearch = (Button) findViewById(R.id.btnSearch);
//		btnSearch.setOnClickListener(this);
//
//		gv_queryList = (PullRefreshListView) findViewById(R.id.unitroadgvdata);
//		mAdapter = new UnitRoadEncroachDetailAdapter(TrainBlockActivity.this,
//				mList);
//		gv_queryList.setAdapter(mAdapter);
//		gv_queryList.setonRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				isPullRefresh = true;
//				getData();
//			}
//		});
//
//	}
//
//	// 获取股道占用信息
//	private void SearchAreaOccupancy() {
//		ArrayList<TB_AreaOccupancy> list = new ArrayList<TB_AreaOccupancy>();
//		HashMap<String, String> parmValues = new HashMap<String, String>();
//		parmValues.put("sessionId", Property.SessionId);
//		if (!StringUtil.isNullOrEmpty(trackName)) {
//			parmValues.put("trackName", trackName);
//		}
//		parmValues.put("direct", laneDir);
//		parmValues.put("stationStatus", laneStatus);
//		if (mStation != null)
//			parmValues.put("stationCode", mStation.Code);
//
//		String methodPath = Constant.MP_TRAININFO;
//		String methodName = Constant.MN_GET_LANEOCCUPANCY;
//
//		WebServiceMananger webServiceManager = new WebServiceMananger(
//				getApplicationContext(), methodName, parmValues);
//		String result = webServiceManager.OpenConnect(methodPath);
//
//		if (result == null || result.equals("")) {
//			msg = getString(R.string.exp_getdata);
//			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
//			return;
//		}
//
//		int Code = JsonUtil.GetJsonInt(result, "code");
//		switch (Code) {
//		case Constant.NORMAL:
//			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "data");
//			for (int i = 0; i < jsonArray.length(); i++) {
//				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
//				TB_AreaOccupancy OccupancyStatus = new TB_AreaOccupancy();
//				OccupancyStatus.AREANAME = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "AREANAME");
//				OccupancyStatus.StationStatus = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "StationStatus");
//				OccupancyStatus.TRNO_PRO = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "TRNO_PRO");
//				OccupancyStatus.GRPNO_PTTI = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "GRPNO_PTTI");
//				OccupancyStatus.PLATFORM_PTTI = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "PLATFORM_PTTI");
//				OccupancyStatus.ARRTIMR_PTTI = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "ARRTIMR_PTTI");
//				OccupancyStatus.DEPATIME_PTTI = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "DEPATIME_PTTI");
//				OccupancyStatus.STATIONBEGINTIME = JsonUtil
//						.GetJsonObjStringValue(jsonObj, "STATIONBEGINTIME");
//				OccupancyStatus.STATIONENDTIME = JsonUtil
//						.GetJsonObjStringValue(jsonObj, "STATIONENDTIME");
//				OccupancyStatus.PTRNO_PRO = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "PTRNO_PRO");
//				OccupancyStatus.ID = JsonUtil.GetJsonObjStringValue(jsonObj,
//						"ID");
//				list.add(OccupancyStatus);
//			}
//
//			Message message = new Message();
//			message.what = MSG_GETDATA_SUCCEED;
//			message.obj = list;
//			myhandle.sendMessage(message);
//			break;
//		case Constant.EXCEPTION:
//			msg = getString(R.string.exp_getdata);
//			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
//			break;
//		default:
//			msg = JsonUtil.GetJsonString(result, "errMsg");
//			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
//			break;
//		}
//	}
//
//	// 获取数据
//	private void getData() {
//		showProgressDialog(R.string.GettingData);
//		SearchInfo();
//		
//		new Thread() {
//			public void run() {
//				SearchAreaOccupancy();
//			}
//		}.start();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btnset:
//			LinearLayout layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
//			if (layoutSearch.getVisibility() == View.VISIBLE) {
//				layoutSearch.setVisibility(View.GONE);
//			} else {
//				layoutSearch.setVisibility(View.VISIBLE);
//			}
//			break;
//		case R.id.btnSearch:
//			getData();
//			break;
//		default:
//			break;
//		}
//	}
//
//	// 检索股道信息
//	private void SearchInfo() {
//		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		inputMethodManager.hideSoftInputFromWindow(txTrackNo.getWindowToken(),
//				0);
//
//		trackName = txTrackNo.getText().toString();
//		if (chkLaneDirUp.isChecked() || chkLaneDirDown.isChecked()) {
//			if (chkLaneDirUp.isChecked() && chkLaneDirDown.isChecked()) {
//				laneDir = "-1";
//			} else {
//				if (chkLaneDirUp.isChecked()) {
//					laneDir = "0";
//				}
//
//				if (chkLaneDirDown.isChecked()) {
//					laneDir = "1";
//				}
//			}
//		} else {
//			laneDir = "-2";
//		}
//
//		laneStatus = "";
//		if (chkLaneStatusBusying.isChecked() || chkLaneStatusBusy.isChecked()
//				|| chkLaneStatusFree.isChecked()) {
//			if (chkLaneStatusBusying.isChecked()) {
//				laneStatus = "1";
//			}
//
//			if (chkLaneStatusBusy.isChecked()) {
//				laneStatus += ",2";
//			}
//
//			if (chkLaneStatusFree.isChecked()) {
//				laneStatus += ",0";
//			}
//		}
//
//		if (laneStatus.startsWith(",")) {
//			laneStatus = laneStatus.subSequence(1, laneStatus.length())
//					.toString();
//		}
//	}
//
//	// 选择所属站
//	private void showSelectStaionDlg() {
//		if (null != Property.ChargeStation) {
//			String[] m = new String[Property.ChargeStation.size()];
//			for (int i = 0; i < Property.ChargeStation.size(); i++) {
//				m[i] = Property.ChargeStation.get(i).Name;
//			}
//
//			AlertDialog dlg = new AlertDialog.Builder(TrainBlockActivity.this)
//					.setTitle("")
//					.setItems(m, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int item) {
//							if (item < Property.ChargeStation.size()) {
//								mStation = Property.ChargeStation.get(item);
//
//								if (mStation != null) {
//									txtStation.setText("(" + mStation.Name
//											+ ")");
//									getData();
//								}
//							}
//						}
//					}).create();
//			dlg.show();
//		}
//	};
}
