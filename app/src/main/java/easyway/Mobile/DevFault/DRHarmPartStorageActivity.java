package easyway.Mobile.DevFault;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.GetAllDevCateData;
import easyway.Mobile.Data.GetAllDevSupplierData;
import easyway.Mobile.Data.GetDevInGroupResult;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.ViewUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class DRHarmPartStorageActivity extends ActivityEx implements OnClickListener, OnItemSelectedListener{

	private Activity act;
	@BindView(id=R.id.title)
	private TextView title;
	@BindView(id=R.id.btnReturn)
	private Button btnReturn;
	@BindView(id=R.id.DeviceStatus)
	private Spinner DeviceStatus;
	@BindView(id=R.id.SpareNumber)
	private EditText SpareNumber;
	@BindView(id=R.id.SparePartsName)
	private EditText SparePartsName;
	@BindView(id=R.id.DevicePrice)
	private EditText DevicePrice;
	@BindView(id=R.id.Supplier)
	private Spinner Supplier;
	@BindView(id=R.id.PurchaseDate)
	private EditText PurchaseDate;
	@BindView(id=R.id.MaintenancePeriod)
	private EditText MaintenancePeriod;
	@BindView(id=R.id.DiscardDate)
	private EditText DiscardDate;

	@BindView(id=R.id.DRHarmPartStorageView)
	private LinearLayout DRHarmPartStorageView;
	
	@BindView(id=R.id.saveBtn)
	private Button saveBtn;

	private  final static int GET_ALLDEVGATE_SUCCESS = 0;
	private  final static int GET_ALLDEVGATE_FAIL = 1;
	private  final static int  NET_ERROR = 2;
	private  final static int GET_ALLDEVSUPPLIER_SUCCESS = 3;
	private  final static int SAVE_BROKENDEV_SUCCESS = 4;

	private ArrayAdapter cateAadapter;
	private ArrayAdapter supplierAadapter;
	private ArrayList<GetAllDevCateData> list = new ArrayList<GetAllDevCateData>();
	private ArrayList<String>cateName = new ArrayList<String>();
	private ArrayList<GetAllDevSupplierData> SupplierList = new ArrayList<GetAllDevSupplierData>();
	private ArrayList<String>supplierName = new ArrayList<String>();

	private PopupWindow popupWindow; 
	private String chooseDate = null;
	private String chooseTime = null;
	private int type = 0;
	private int currPosition = 0;
	private int dfId;

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_ALLDEVGATE_SUCCESS:
				closeProgressDialog();
				list = (ArrayList<GetAllDevCateData>)msg.obj;
				if(list != null && list.size() > 0){
					for(GetAllDevCateData cateData:list){
						cateName.add(cateData.getDcName());
					}
					cateAadapter.notifyDataSetChanged();
				}
				getDevSupplier();
				break;
			case GET_ALLDEVGATE_FAIL:
				closeProgressDialog();
				showToast(R.string.GetDataFail);
				break;
			case NET_ERROR:
				closeProgressDialog();
				showToast(R.string.ConnectFail);
				break;
			case GET_ALLDEVSUPPLIER_SUCCESS:
				closeProgressDialog();
				SupplierList = (ArrayList<GetAllDevSupplierData>)msg.obj;
				if(SupplierList != null && SupplierList.size() > 0){
					for(GetAllDevSupplierData supplierData : SupplierList){
						supplierName.add(supplierData.getCompName());
					}
					supplierAadapter.notifyDataSetChanged();
				}
				break;
			case SAVE_BROKENDEV_SUCCESS:
				closeProgressDialog();
				String msgStr = msg.obj.toString();
				showToast(msgStr);
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act  = this;
		setContentView(R.layout.activity_dr_harm_part_storage);
		AnnotateUtil.initBindView(act);
		initView();
	}
	private void initView(){
		dfId = getIntent().getIntExtra("dfId", -1);
		title.setText("损坏零件入库");
		btnReturn.setOnClickListener(this);
		cateAadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cateName);    
		cateAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);   
		DeviceStatus.setAdapter(cateAadapter);
		DeviceStatus.setOnItemSelectedListener(this);    
		supplierAadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, supplierName);
		supplierAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);   
		Supplier.setAdapter(supplierAadapter);
		Supplier.setOnItemSelectedListener(this);

		PurchaseDate.setText(DateUtil.getCurrDate());
		PurchaseDate.setOnClickListener(this);
		MaintenancePeriod.setText(DateUtil.getCurrDate());
		MaintenancePeriod.setOnClickListener(this);
		DiscardDate.setText(DateUtil.getCurrDate());
		DiscardDate.setOnClickListener(this);
		
		saveBtn.setOnClickListener(this);
		
		getDevCate();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnReturn:
			finish();
			break;
		case R.id.PurchaseDate:
			type = 1;
			showPopView();
			break;
		case R.id.MaintenancePeriod:
			type = 2;
			showPopView();
			break;
		case R.id.DiscardDate:
			type = 3;
			showPopView();
			break;
		case R.id.sureBtn:
			switch (type) {
			case 1:
				PurchaseDate.setText(chooseDate);
				break;
			case 2:
				MaintenancePeriod.setText(chooseDate);
				break;
			case 3:
				DiscardDate.setText(chooseDate);
				break;
			default:
				break;
			}
			if (popupWindow != null && popupWindow.isShowing()) {  
				popupWindow.dismiss();  
				popupWindow = null;  
				ViewUtil.backgroundAlpha(act,1f);
			}  
			break;
		case R.id.cancelBtn:
			if (popupWindow != null && popupWindow.isShowing()) {  
				popupWindow.dismiss();  
				popupWindow = null;  
				ViewUtil.backgroundAlpha(act,1f);
			} 
			break;
		case R.id.saveBtn:
			String devCode = SpareNumber.getText().toString();
			String devName = SparePartsName.getText().toString();
			String devPrice = DevicePrice.getText().toString();
			String buyDate = PurchaseDate.getText().toString();
			String warranty = MaintenancePeriod.getText().toString();
			String retireAge = DiscardDate.getText().toString();
			String devCateId = String.valueOf(list.get(currPosition).getDcId());
			String supId = String.valueOf(SupplierList.get(currPosition).getSupId());
			if(TextUtils.isEmpty("devCateId")){
				showToast("设备类别不能为空");
				return;
			}
			if(TextUtils.isEmpty("devCode")){
				showToast("备品备件编号不能为空");
				return;
			}
			if(TextUtils.isEmpty("devName")){
				showToast("备品备件名称不能为空");
				return;
			}
			if(TextUtils.isEmpty("devPrice")){
				showToast("设备价格不能为空");
				return;
			}
			if(TextUtils.isEmpty("supId")){
				showToast("供应商不能为空");
				return;
			}
			if(TextUtils.isEmpty("buyDate")){
				showToast("采购日期不能为空");
				return;
			}
			if(TextUtils.isEmpty("warranty")){
				showToast("保修期不能为空");
				return;
			}
			if(TextUtils.isEmpty("retireAge")){
				showToast("报废日期不能为空");
				return;
			}
			Save(devCode,devName,devPrice,buyDate,warranty,retireAge,devCateId,supId);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View v, int position,
			long arg3) {
		// TODO Auto-generated method stub
		currPosition = position;
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void showPopView(){
		ViewUtil.backgroundAlpha(act,0.5f);
		View popupWindow_view = null;

		// 获取自定义布局文件activity_popupwindow_left.xml的视图  
		popupWindow_view = getLayoutInflater().inflate(R.layout.date_time_pop, null,false);  
		DatePicker dpPicker = (DatePicker)popupWindow_view.findViewById(R.id.dpPicker);
		TimePicker tpPicker = (TimePicker)popupWindow_view.findViewById(R.id.tpPicker);
		Button sureBtn = (Button)popupWindow_view.findViewById(R.id.sureBtn);
		sureBtn.setTag(type);
		sureBtn.setOnClickListener(this);
		Button cancelBtn = (Button)popupWindow_view.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(this);
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度  
		popupWindow = new PopupWindow(popupWindow_view, 600, 700, true);  
		// 设置动画效果  
		popupWindow.setAnimationStyle(R.style.AnimationFade);  
		// 这里是位置显示方式,在屏幕的左侧  
		popupWindow.showAtLocation(DRHarmPartStorageView, Gravity.CENTER, 0, 0);  
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
		chooseDate = DateUtil.getCurrDate();
		//		String[] strings = currDateTime.split(" ");
		//		chooseDate = strings[0];
		//		chooseTime = strings[1];
		Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料  
		t.setToNow(); // 取得系统时间。  
		int year = t.year;  
		int month = t.month;  
		int date = t.monthDay;  
		dpPicker.init(year, month, date, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// 获取一个日历对象，并初始化为当前选中的时间
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");// 
//				showToast(format.format(calendar.getTime()));
				chooseDate = format.format(calendar.getTime());
				//	                PurchaseDate.setText(format.format(calendar.getTime()));
			}
		});

		tpPicker.setIs24HourView(false);
		tpPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay,
					int minute) {
				String[] strs = chooseDate.split(" ");
				chooseDate = strs[0];
				if(minute/10 <=0)
					chooseTime = hourOfDay + ":0" + minute;
				else
					chooseTime = hourOfDay + ":" + minute;
				chooseDate = chooseDate+" "+chooseTime;
				//	                    	 showToast( hourOfDay + "小时" + minute + "分钟");
			}
		});

	}


	private void getDevCate(){
		showProgressDialog("正在获取设备类别");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("stationCode", Property.OwnStation.Code);
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_GET_ALLDEVCATE;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<ResultForListData<GetAllDevCateData>>(){}.getType();
				ResultForListData<GetDevInGroupResult> allDevCate = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(allDevCate.isMsgType()){
						handler.sendMessage(handler.obtainMessage(GET_ALLDEVGATE_SUCCESS, allDevCate.getData()));
					}else{
						handler.sendEmptyMessage(GET_ALLDEVGATE_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();

	}
	private void getDevSupplier(){
		showProgressDialog("正在获取供应商");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("stationCode", Property.OwnStation.Code);
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_GET_ALLDEVSUPPLIER;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<ResultForListData<GetAllDevSupplierData>>(){}.getType();
				ResultForListData<GetAllDevSupplierData> allDevSupplier = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(allDevSupplier.isMsgType()){
						handler.sendMessage(handler.obtainMessage(GET_ALLDEVSUPPLIER_SUCCESS, allDevSupplier.getData()));
					}else{
						handler.sendEmptyMessage(GET_ALLDEVGATE_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();

	}

	public void Save(final String devCode,final String devName,final String devPrice,final String buyDate,final String warranty,
			final String retireAge,final String devCateId,final String supId){
		showProgressDialog("正在保存损坏的备品备件");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("dfId", String.valueOf(dfId));
				parmValues.put("devCode", devCode);
				parmValues.put("devCateId",devCateId);
				parmValues.put("devName",devName);
				parmValues.put("devPrice",devPrice);
				parmValues.put("supId",supId);
				parmValues.put("buyDate",buyDate);
				parmValues.put("warranty",warranty);
				parmValues.put("retireAge",retireAge);
				parmValues.put("stationCode",Property.OwnStation.Code);
				String methodPath = Constant.MP_DEVFAULT;
				String methodName = Constant.MN_SAVE_BROKENDEVSPAREPARTS;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<Result<String>>(){}.getType();
				Result<String> saveResult = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(saveResult.isMsgType()){
						handler.sendMessage(handler.obtainMessage(SAVE_BROKENDEV_SUCCESS, saveResult.getData()));
					}else{
						handler.sendEmptyMessage(GET_ALLDEVGATE_FAIL);
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}

			};
		}.start();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	public void Cancel(View view){
         finish();
	}


}
