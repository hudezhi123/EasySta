package easyway.Mobile.ShiftAdd;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;


import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.GetAllDevCateData;
import easyway.Mobile.Data.GetAllDevSupplierData;
import easyway.Mobile.Data.GetDevSparePartsData;
import easyway.Mobile.Data.GetDevSparePartsUsingHistoryData;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.DevFault.SpareUsingHistoryAdapter;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.BitmapUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.ViewUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/** 
 * 类说明 备品备件明细
 */
public class EquipmentDetail extends ActivityEx implements OnClickListener{
	private Activity act;

	@BindView(id = R.id.DevCateName)
	private TextView DevCateName;
	@BindView(id = R.id.SupName)
	private TextView SupName;

	@BindView(id = R.id.DevCode)
	private TextView DevCode;
	@BindView(id = R.id.DevName)
	private TextView DevName;
	@BindView(id = R.id.DevPrice)
	private TextView DevPrice;
	@BindView(id = R.id.BuyDate)
	private TextView BuyDate;
	@BindView(id = R.id.Warranty)
	private TextView Warranty;
	@BindView(id = R.id.RetireAge)
	private TextView RetireAge;
	@BindView(id = R.id.DevCate)
	private TextView DevCate;
	@BindView(id = R.id.FromDevName)
	private TextView FromDevName;
	@BindView(id = R.id.ToDevName)
	private TextView ToDevName;
	@BindView(id = R.id.DspStatus)
	private TextView DspStatus;

	@BindView (id = R.id.QRCode)
	private Button QRCode;
	@BindView (id = R.id.getUsingHistory)
	private Button getUsingHistory;
	@BindView (id = R.id.startRepire)
	private Button startRepire;
	@BindView (id = R.id.repireFinish)
	private Button repireFinish;
	@BindView (id = R.id.cancel)
	private Button cancel;

	@BindView(id = R.id.QRimg)
	private ImageView QRimg;

	@BindView(id = R.id.equipmentView)
	private LinearLayout equipmentView;

	private ArrayAdapter cateAadapter;
	private ArrayAdapter supplierAadapter;
	private ArrayList<GetAllDevCateData> list = new ArrayList<GetAllDevCateData>();
	private ArrayList<String>cateName = new ArrayList<String>();
	private ArrayList<GetAllDevSupplierData> SupplierList = new ArrayList<GetAllDevSupplierData>();
	private ArrayList<String>supplierName = new ArrayList<String>();
	private ArrayList<GetDevSparePartsData> sparePartsDatas = new ArrayList<GetDevSparePartsData>();
	private ArrayList<GetDevSparePartsUsingHistoryData> useHistoryDatas = new ArrayList<GetDevSparePartsUsingHistoryData>();
	private ArrayList<GetDevSparePartsUsingHistoryData> currUseHistoryDatas = new ArrayList<GetDevSparePartsUsingHistoryData>();
	private GetDevSparePartsData partsData;
	private  final static int GET_DEVSPATEPARTS_FAIL = 1;
	private  final static int  NET_ERROR = 2;
	private final static int GET_DEVSPATEPARTS_SUCCESS = 4;
	private final static int GET_DEVSPATEPARTSUSINGHIS_SUCCESS = 3;
	private final static int GET_DEVSPATEPARTSUSINGHIS_FAIL = 11;
	private final static int BEGINREPAITDEVSPAREPARTS_SUCCESS = 5;
	private final static int BEGINREPAITDEVSPAREPARTS_FAIL = 10;
	private final static int FIXDEVSPAREPARTS_SUCCESS = 6;
	private final static int FIXDEVSPAREPARTS_FAIL = 9;
	private final static int RETIRESPAREPARTS_SUCCESS = 7;
	private final static int RETIRESPAREPARTS_FAIL = 8;

	private int currPosition = 0;
	private long dspId;
	private int start = 0;
	private int count = 0;

	private PopupWindow popupWindow; 
	private SpareUsingHistoryAdapter adapter;
	//for listView add more
	private PullRefreshListView spareUseHisList;
	private boolean isLastRow;
	private boolean isTopRow;
	private boolean isPullRefresh = false;
	private int total;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_DEVSPATEPARTS_SUCCESS:
				closeProgressDialog();
				sparePartsDatas = (ArrayList<GetDevSparePartsData>)msg.obj;
				if(sparePartsDatas != null && sparePartsDatas.size()>0){
					partsData = sparePartsDatas.get(0);
					initData(partsData);
				}
				break;
			case GET_DEVSPATEPARTSUSINGHIS_SUCCESS:
				closeProgressDialog();
				currUseHistoryDatas = (ArrayList<GetDevSparePartsUsingHistoryData>)msg.obj;
				if(start == 0){
					useHistoryDatas.clear();
					useHistoryDatas.addAll(currUseHistoryDatas);
				}else
					useHistoryDatas.addAll(currUseHistoryDatas);
				if(useHistoryDatas!=null && useHistoryDatas.size()>0){
					if(count == 0){
						count = 1;
						showPopView();
					}	
					 adapter.notifyDataSetChanged();
				}else
					showToast("没有使用记录");
				break;
			case GET_DEVSPATEPARTSUSINGHIS_FAIL:
				closeProgressDialog();
				showToast("获取使用记录失败");
				break;
			case BEGINREPAITDEVSPAREPARTS_SUCCESS:
				closeProgressDialog();
				String beginStr = (String)msg.obj;
				showToast(beginStr);
				getDevSpareParts();
				startRepire.setEnabled(false);
				repireFinish.setEnabled(true);
				cancel.setEnabled(true);
				break;
			case BEGINREPAITDEVSPAREPARTS_FAIL:
				closeProgressDialog();
				showToast("开始维修失败");
				break;
			case FIXDEVSPAREPARTS_SUCCESS:
				closeProgressDialog();
				String fixStr = (String)msg.obj;
				showToast(fixStr);
				getDevSpareParts();
				startRepire.setEnabled(false);
				repireFinish.setEnabled(false);
				cancel.setEnabled(false);
				break;
			case FIXDEVSPAREPARTS_FAIL:
				closeProgressDialog();
				showToast("维修完成失败");
				break;
			case RETIRESPAREPARTS_SUCCESS:
				closeProgressDialog();
				String retireStr = (String)msg.obj;
				showToast(retireStr);
				getDevSpareParts();
				startRepire.setEnabled(false);
				repireFinish.setEnabled(false);
				cancel.setEnabled(false);
				break;
			case RETIRESPAREPARTS_FAIL:
				closeProgressDialog();
				showToast("报废备品备件失败");
				break;
			case GET_DEVSPATEPARTS_FAIL:
				closeProgressDialog();
				showToast("获取备品备件明细失败");
				break;
			case NET_ERROR:
				closeProgressDialog();
				showToast(R.string.ConnectFail);
				break;
			default:
				break;
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = this;
		setContentView(R.layout.act_equipment_detail);
		AnnotateUtil.initBindView(act);
		initView();
	}
	private void initView(){
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("备品备件明细");
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(this);
		dspId = getIntent().getLongExtra("dspId", -1);
		QRCode.setOnClickListener(this);
		getUsingHistory.setOnClickListener(this);
		startRepire.setOnClickListener(this);
		repireFinish.setOnClickListener(this);
		cancel.setOnClickListener(this);
		getDevSpareParts();
	}

	private void initData(GetDevSparePartsData sparePartsData){
		DevCode.setText(sparePartsData.getDevCode());
		DevName.setText(sparePartsData.getDevName());
		DevPrice.setText(String.valueOf(sparePartsData.getDevPrice()));
		BuyDate.setText(sparePartsData.getBuyDate());
		Warranty.setText(sparePartsData.getWarranty());
		RetireAge.setText(sparePartsData.getRetireAge());
		DevCate.setText(sparePartsData.getDevCate());
		DevCateName.setText(sparePartsData.getDevCateName());
		SupName.setText(sparePartsData.getSupName());
		FromDevName.setText(sparePartsData.getFromDevName());
		ToDevName.setText(sparePartsData.getToDevName());
		switch (sparePartsData.getDspStatus()) {
		case 0:
			DspStatus.setText(R.string.spare_nomal);
			DspStatus.setTextColor(getResources().getColor(R.color.color_3D86C8));
			startRepire.setEnabled(false);
			repireFinish.setEnabled(false);
			cancel.setEnabled(false);
			break;
		case 1:
			DspStatus.setText(R.string.spare_using);
			DspStatus.setTextColor(getResources().getColor(R.color.green));
			startRepire.setEnabled(false);
			repireFinish.setEnabled(false);
			cancel.setEnabled(false);
			break;
		case 2:
			DspStatus.setText(R.string.spare_waitrepair);
			DspStatus.setTextColor(getResources().getColor(R.color.red));
			startRepire.setEnabled(true);
			repireFinish.setEnabled(false);
			cancel.setEnabled(false);
			break;
		case 3:
			DspStatus.setText(R.string.spare_repair);
			DspStatus.setTextColor(getResources().getColor(R.color.yellow));
			startRepire.setEnabled(false);
			repireFinish.setEnabled(true);
			cancel.setEnabled(true);
			break;
		case 4:
			DspStatus.setText(R.string.spare_cancel);
			DspStatus.setTextColor(getResources().getColor(R.color.gray));
			startRepire.setEnabled(false);
			repireFinish.setEnabled(false);
			cancel.setEnabled(false);
			break;
		default:
			break;
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnReturn:
			finish();
			break;
		case R.id.QRCode:
			String url = "{DspId:"+partsData.getDspId()+",DevCode:"+partsData.getDevName()+
			",SupName:"+partsData.getSupName()+",BuyDate:"+partsData.getBuyDate()+"}";
			Create2QR(url);
			break;
		case R.id.getUsingHistory:
			start = 0;
			count = 0;
			isLastRow = false;
			isTopRow = true;
			getDevSparePartsUsingHis();
			break;
		case R.id.startRepire:
			BeginRepairDev();
			break;
		case R.id.repireFinish:
			FixDevSpare();
			break;
		case R.id.cancel:
			RetireSpareParts();
			break;
		default:
			break;
		}
	}
	private void showPopView(){
		ViewUtil.backgroundAlpha(act,0.5f);
		View popupWindow_view = null;

		// 获取自定义布局文件activity_popupwindow_left.xml的视图  
		popupWindow_view = getLayoutInflater().inflate(R.layout.device_usehislist, null,false);  
		spareUseHisList = (PullRefreshListView)popupWindow_view.findViewById(R.id.devUseHis_List);	    
		adapter = new SpareUsingHistoryAdapter(act, useHistoryDatas);
		spareUseHisList.setAdapter(adapter);
		spareUseHisList.setRefreshable(false);
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度  
		popupWindow = new PopupWindow(popupWindow_view, 600, 700, true);  
		// 设置动画效果  
		popupWindow.setAnimationStyle(R.style.AnimationFade);  
		// 这里是位置显示方式,在屏幕的左侧  
		popupWindow.showAtLocation(equipmentView, Gravity.CENTER, 0, 0);  
		// 点击其他地方消失  
		popupWindow_view.setOnTouchListener(new OnTouchListener() {  
			@Override  
			public boolean onTouch(View v, MotionEvent event) {  
				// TODO Auto-generated method stub  
				if (popupWindow != null && popupWindow.isShowing()) {  
					popupWindow.dismiss();  
					popupWindow = null;  
					ViewUtil.backgroundAlpha(act,1f);
				}  
				return false;  
			}  
		});  
		isLastRow = false;
		isTopRow = true;
		AddMoreListener(spareUseHisList);
	}
	private void AddMoreListener(final PullRefreshListView listView){
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
					if(listView.getLastVisiblePosition() == (listView.getCount() - 1)){
						if( useHistoryDatas.size() == total){
							showToast("没有更多数据");
						}else{
							start = useHistoryDatas.size();
							 getDevSparePartsUsingHis();
						}
					}
						
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0){
					isLastRow = true;
				}
				if(listView.getFirstVisiblePosition() == 0){
					isTopRow = true;
				}
			}
		});
	}
	// 生成二维码
	public void Create2QR(String uri) {
		Bitmap bitmap;
		try {
			bitmap = BitmapUtil.createQRCode(uri, 300);

			if (bitmap != null) {
				BitmapUtil.saveImageToGallery(act,bitmap,partsData.getDevName()+dspId);
				QRimg.setImageBitmap(bitmap);
				QRimg.setVisibility(View.VISIBLE);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	private void getDevSpareParts(){
		showProgressDialog("正在获取备品备件明细");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("dspId", String.valueOf(dspId));
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_GET_DEVSPAREPARTS;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<ResultForListData<GetDevSparePartsData>>(){}.getType();
				ResultForListData<GetDevSparePartsData> getDevSpare = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(getDevSpare.isMsgType()){
						handler.sendMessage(handler.obtainMessage(GET_DEVSPATEPARTS_SUCCESS, getDevSpare.getData()));
					}else{
						handler.sendEmptyMessage(GET_DEVSPATEPARTS_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();

	}

	private void getDevSparePartsUsingHis(){
		showProgressDialog("正在获取使用记录");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("dspId", String.valueOf(dspId));
				parmValues.put("limit", String.valueOf(4));
				parmValues.put("start", String.valueOf(start));
				parmValues.put("stationCode",Property.OwnStation.Code);
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_GET_DEVSPAREPARTSUSINGHISTORY;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<ResultForListData<GetDevSparePartsUsingHistoryData>>(){}.getType();
				ResultForListData<GetDevSparePartsUsingHistoryData> getDevSpareUseHis = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(getDevSpareUseHis.isMsgType()){
						total = getDevSpareUseHis.getTotal();
						handler.sendMessage(handler.obtainMessage(GET_DEVSPATEPARTSUSINGHIS_SUCCESS, getDevSpareUseHis.getData()));
					}else{
						handler.sendEmptyMessage(GET_DEVSPATEPARTSUSINGHIS_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();

	}

	private void BeginRepairDev(){
		showProgressDialog("开始维修");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("dspId", String.valueOf(dspId));
				parmValues.put("stationCode",Property.OwnStation.Code);
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_BEGINREPAIRDEVSPAREPARTS;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<Result<String>>(){}.getType();
				Result<String> beginRepair= gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(beginRepair.isMsgType()){
						handler.sendMessage(handler.obtainMessage(BEGINREPAITDEVSPAREPARTS_SUCCESS, beginRepair.getData()));
					}else{
						handler.sendEmptyMessage(BEGINREPAITDEVSPAREPARTS_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();

	}

	private void FixDevSpare(){
		showProgressDialog("维修完成");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("dspId", String.valueOf(dspId));
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_FIXDEVSPAREPARTS;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<Result<String>>(){}.getType();
				Result<String> fixRepair= gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(fixRepair.isMsgType()){
						handler.sendMessage(handler.obtainMessage(FIXDEVSPAREPARTS_SUCCESS, fixRepair.getData()));
					}else{
						handler.sendEmptyMessage(FIXDEVSPAREPARTS_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();

	}

	private void RetireSpareParts(){
		showProgressDialog("报废备品备件");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("dspId", String.valueOf(dspId));
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_RETIRESPAREPARTS;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<Result<String>>(){}.getType();
				Result<String> retireRepair= gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(retireRepair.isMsgType()){
						handler.sendMessage(handler.obtainMessage(RETIRESPAREPARTS_SUCCESS, retireRepair.getData()));
					}else{
						handler.sendEmptyMessage(RETIRESPAREPARTS_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();

	}






}
