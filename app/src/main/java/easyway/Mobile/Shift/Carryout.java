package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Carryout extends ActivityEx{
	
	private Activity act;
	private Button bt;
	private ListView list;
	private Adapter_Carryout adapter;
	private long TeamId;
	private String trainNo;
	
	private final int GetNetInfoOk = 0;
	private final int GetNetInfoIsFail = 1;
	private final int GetNetInfoIsNull = 2;
	private final int ParmValuesIsNull = 3;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GetNetInfoOk:
				showToast("保存执行人成功！");
				break;
			case GetNetInfoIsFail:
				String errMsg = (String)msg.obj;
				showErrMsg(errMsg);
				break;
			case ParmValuesIsNull:
				showErrMsg("您还未选择执行人！请您添加。");
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_carryout);
		act = this;
		Intent intent = getIntent();
		TeamId = intent.getLongExtra("TeamId", 0);
		trainNo = intent.getStringExtra("trainNo");
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	protected void onStart() {
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("添加执行人");
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		if (btnReturn != null) {
			btnReturn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}
		
		super.onStart();
	}
	
	
	@Override
	protected void onResume() {
		initView();
		super.onResume();
	}
	
	void initView(){
		list = (ListView)findViewById(R.id.Activity_list);
		bt = (Button)findViewById(R.id.Activity_button);
		ArrayList<Staff> list1 = getAllStaff();
		adapter = new Adapter_Carryout(list1, act);
		list.setAdapter(adapter);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 NetWorkPrepare();
			}
		});
	}
	
	
	void NetWorkPrepare(){
		
		new Thread(){
			public void run() {
				NetWork();
			};
		}.start();
		
	}
	
	void NetWork(){
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		ArrayList<Long> data = new ArrayList<Long>();
		ArrayList<String> data1 = new ArrayList<String>();
		//为参数赋值。
		ArrayList<ArrayList<?>> mDataList = adapter.getData();
		data = (ArrayList<Long>)mDataList.get(0);
		data1 = (ArrayList<String>)mDataList.get(0);
		if(data.size() < 0){
			handler.sendEmptyMessage(ParmValuesIsNull);
			return;
		}
		parmValues.put("StaffIdList", data.toString());
		parmValues.put("StaffNameList", data1.toString());
		parmValues.put("teamId", String.valueOf(TeamId));
		parmValues.put("trainNo", trainNo); 
		String methodName = Constant.MN_SaveScheStaff;
		String methodPath = Constant.MP_SHIFT;
		WebServiceManager webService = new WebServiceManager(act, methodName, parmValues);
		String result = webService.OpenConnect(methodPath);
		String errMsg = JsonUtil.GetJsonString(result, "Msg");
		if(TextUtils.isEmpty(errMsg)){
			int code = JsonUtil.GetJsonInt(result, "Code");
			if(code == 1000){
				handler.sendEmptyMessage(GetNetInfoOk);
			}else{
				Message ms = handler.obtainMessage();
				ms.obj = "服务器返回失败";
				ms.what = GetNetInfoIsFail;
				handler.sendMessage(ms);
			}
		}else{
			Message ms = handler.obtainMessage();
			ms.obj = errMsg;
			ms.what = GetNetInfoIsFail;
			handler.sendMessage(ms);
		}
	}
	
	// 获取所有人员
		private ArrayList<Staff> getAllStaff() {
			ArrayList<Staff> stafflist = new ArrayList<Staff>();
			DBHelper dbHelper = new DBHelper(act);
			String[] columns = { DBHelper.STAFF_ID, DBHelper.STAFF_NAME,
					DBHelper.STAFF_TYPE, DBHelper.STAFF_CODE, DBHelper.STAFF_EXPEND1 };
			Cursor cursor = null;

			try {
				cursor = dbHelper.exeSql(DBHelper.STAFF_TABLE_NAME, columns,
						"Type = " + Staff.TYPE_STAFF, null, null, null,
						DBHelper.STAFF_CODE);
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						Staff staff = new Staff();
						staff.StaffId = cursor.getLong(cursor
								.getColumnIndex(DBHelper.STAFF_ID));
						staff.StaffName = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_NAME));
						staff.Type = cursor.getInt(cursor
								.getColumnIndex(DBHelper.STAFF_TYPE));
						staff.StaffCode = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_CODE));
						staff.Expend1 = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_EXPEND1));

						stafflist.add(staff);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dbHelper.closeCursor(cursor);
				dbHelper.close();
			}

			return stafflist;
		}
	
}
