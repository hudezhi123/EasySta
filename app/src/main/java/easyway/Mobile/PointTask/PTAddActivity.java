package easyway.Mobile.PointTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Contacts.Contacts;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

/*
 * 添加重点任务
 */
public class PTAddActivity extends ActivityEx implements OnClickListener {
	public static final String KEY_INDEX = "index";
	public static final String KEY_MAJOR = "major";
	public static final String KEY_CHILD = "child";
	public static final String KEY_EDIT = "edit";

	public static final int INDEX_FROM_EDIT = -3;
	public static final int INDEX_FROM_MAJOR = -2;
	public static final int INDEX_FROM_LIST = -1;

	private final int REQUEST_CODE_SELECTCHARGE = 100;
	private final int REQUEST_CODE_SELECTTRAINTYPE = 200;

	private final int TASK_LEVEL_HIGH = 3;
	private final int TASK_LEVEL_NORMAL = 2;
	private final int TASK_LEVEL_LOW = 1;

	private int mYear;
	private int mMonth;
	private int mDay;

	private ArrayList<String> mNameList;
	private PopupWindow mPopTaskName;
	private ListView mListView;
	private NameAdapter mAdapter;
	private boolean mIsSelect = false;

	private EditText edtTaskName;
	private EditText edtTrainNo;
	private Button btnTrainNo;
	private Button btnDate;
	private Button btnCharge;
	private RadioButton radLevelHigh;
	private RadioButton radLevelNormal;
	private RadioButton radLevelLow;

	private MajorMode mMajor;
	private boolean mIsEdit = false;

	private final int MSG_RELEASE_TASK_SUCCESS = 1;
	private final int MSG_RELEASE_TASK_FAIL = 2;
	private final int MSG_DATA_CHECK_FAIL = 3;
	private final int MSG_GETDATA_NULL = 4;
	private final int MSG_GETDATA_SUCCEED = 5;
	private final int MSG_GETDATA_FAIL = 6;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_RELEASE_TASK_SUCCESS:
				if (!mIsEdit) {
					Intent intent = new Intent(PTAddActivity.this,
							PTAddChildActivity.class);
					Bundle extras = new Bundle();
					extras.putInt(KEY_INDEX, INDEX_FROM_MAJOR);
					extras.putSerializable(KEY_MAJOR, mMajor);
					intent.putExtras(extras);
					startActivity(intent);
				}

				finish();
				break;
			case MSG_DATA_CHECK_FAIL:
				int resid = (Integer) msg.obj;
				showToast(resid);
				break;
			case MSG_RELEASE_TASK_FAIL:
				showToast(errMsg);
				break;
			case MSG_GETDATA_SUCCEED:
				mNameList = (ArrayList<String>) msg.obj;
				if (mAdapter != null)
					mAdapter.notifyDataSetChanged();
				if (mPopTaskName == null)
					intialPopWindow();

				if (!isFinishing())
					if (!mPopTaskName.isShowing()) {
						mPopTaskName.showAsDropDown(edtTaskName, 5, -7);
					}
				break;
			case MSG_GETDATA_NULL:
			case MSG_GETDATA_FAIL:
				if (mPopTaskName == null)
					return;

				if (mPopTaskName.isShowing())
					mPopTaskName.dismiss();
				break;
			default:
				break;
			}
		}
	};

	// 设置日期控件
	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			mMajor.PlanDate = new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay).toString();
			btnDate.setText(mMajor.PlanDate);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pointtask_add);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.task_title_addmain);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mMajor = (MajorMode) bundle
					.getSerializable(PTAddActivity.KEY_MAJOR);
			mIsEdit = bundle.getBoolean(KEY_EDIT, false);
		}

		initView();

		if (mMajor == null) {
			mMajor = new MajorMode();

			Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			mMajor.PlanDate = new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay).toString();
			btnDate.setText(mMajor.PlanDate);

			mMajor.StaffId = Property.StaffId;
			mMajor.StaffName = Property.StaffName;
			btnCharge.setText(mMajor.StaffName);
		} else {
			mIsSelect = true;
			edtTaskName.setText(mMajor.TaskName);
			edtTaskName.setSelection(edtTaskName.getText().length());
			
			if (mMajor.TRNO_PRO != null && mMajor.TRNO_PRO.length() != 0) {
				try {
					Integer.parseInt(mMajor.TRNO_PRO);
					btnTrainNo.setText(R.string.task_train_NULL);
					edtTrainNo.setText(mMajor.TRNO_PRO);
				} catch (Exception e) {
					btnTrainNo.setText(mMajor.TRNO_PRO.substring(0,1));
					edtTrainNo.setText(mMajor.TRNO_PRO.substring(1));
				}
			}

			btnCharge.setText(mMajor.StaffName);
			btnDate.setText(mMajor.PlanDate);
			if (mMajor.TaskLevel == TASK_LEVEL_HIGH)
				radLevelHigh.setChecked(true);
			else if (mMajor.TaskLevel == TASK_LEVEL_NORMAL)
				radLevelNormal.setChecked(true);
			else if (mMajor.TaskLevel == TASK_LEVEL_LOW)
				radLevelLow.setChecked(true);

			String[] strs = mMajor.PlanDate.split("-");
			if (strs == null || strs.length != 3) {
				Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
			} else {
				mYear = Integer.parseInt(strs[0]);
				mMonth = Integer.parseInt(strs[1]);
				mDay = Integer.parseInt(strs[2]);
			}
		}
	}

	private void initView() {
		// 任务名
		edtTaskName = (EditText) findViewById(R.id.edtTaskName);
		// 车次号
		edtTrainNo = (EditText) findViewById(R.id.edtTrainNo);
		btnTrainNo = (Button) findViewById(R.id.btnTrainNo);
		btnTrainNo.setOnClickListener(this);

		// 所属日期
		btnDate = (Button) findViewById(R.id.btnDate);
		btnDate.setOnClickListener(this);

		// 责任人
		btnCharge = (Button) findViewById(R.id.btnCharge);
		btnCharge.setOnClickListener(this);

		// 优先级
		radLevelHigh = (RadioButton) findViewById(R.id.radLevelHigh);
		radLevelNormal = (RadioButton) findViewById(R.id.radLevelNormal);
		radLevelLow = (RadioButton) findViewById(R.id.radLevelLow);

		// 发布
		Button btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		if (mIsEdit)
			btnNext.setText(R.string.task_save);

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(this);

		edtTaskName.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mIsSelect) {
					mIsSelect = false;
					if (mPopTaskName != null && mPopTaskName.isShowing())
						mPopTaskName.dismiss();
				} else {
					new Thread() {
						public void run() {
							searchTaskName();
						}
					}.start();
				}
			}
		});

		edtTaskName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					new Thread() {
						public void run() {
							searchTaskName();
						}
					}.start();
				} else {
					if (mPopTaskName != null && mPopTaskName.isShowing())
						mPopTaskName.dismiss();
				}

			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// 添加重点任务
	private void releaseTask() {
		String valueStr = setReleaseData();

		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("valueStr", valueStr);
		paramValues.put("taskId", String.valueOf(mMajor.TaskId));
		if (Property.OwnStation != null)
			paramValues.put("stationCode", Property.OwnStation.Code);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_SAVE_TASKMAJOR;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_addpointtask);
			myhandle.sendEmptyMessage(MSG_RELEASE_TASK_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			JSONObject jsonObj = (JSONObject) JsonUtil.GetJsonObj(result,
					"Data");
			if (jsonObj == null) {
				if (mMajor.TaskId == MajorMode.TASKID_INVALID) {
					myhandle.sendEmptyMessage(MSG_RELEASE_TASK_FAIL);
					return;
				}
			}
			if (mMajor.TaskId == MajorMode.TASKID_INVALID)
				mMajor.TaskId = JsonUtil.GetJsonObjLongValue(jsonObj, "TaskId");
			myhandle.sendEmptyMessage(MSG_RELEASE_TASK_SUCCESS);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_RELEASE_TASK_FAIL);
			break;
		}
	}

	// 重点任务信息
	private String setReleaseData() {
		JSONObject object = new JSONObject();

		mMajor.TaskLevel = TASK_LEVEL_NORMAL;
		if (radLevelHigh.isChecked())
			mMajor.TaskLevel = TASK_LEVEL_HIGH;
		else if (radLevelNormal.isChecked())
			mMajor.TaskLevel = TASK_LEVEL_NORMAL;
		else if (radLevelLow.isChecked())
			mMajor.TaskLevel = TASK_LEVEL_LOW;

		if (btnTrainNo.getText().toString().trim().equals(getString(R.string.task_train_NULL))) {
			mMajor.TRNO_PRO = edtTrainNo.getText().toString().trim();
		} else {
			if (edtTrainNo.getText().toString().trim().length() != 0)
				mMajor.TRNO_PRO = btnTrainNo.getText().toString().trim() + edtTrainNo.getText().toString().trim();
			else
				mMajor.TRNO_PRO = "";
		}
		
		try {
			object.put("TaskName", mMajor.TaskName);
			object.put("TRNO_PRO", mMajor.TRNO_PRO);
			object.put("TaskLevel", mMajor.TaskLevel);
			object.put("StaffId", mMajor.StaffId);
			object.put("PlanDate", mMajor.PlanDate);
		} catch (Exception e) {

		}
		return object.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDate:
			new DatePickerDialog(PTAddActivity.this, date, mYear, mMonth, mDay)
					.show();
			break;
		case R.id.btnCharge:
			Intent intent = new Intent(PTAddActivity.this, Contacts.class);
			intent.putExtra(Contacts.KEY_FLAG, Contacts.FLAG_POINTTASK);
			startActivityForResult(intent, REQUEST_CODE_SELECTCHARGE);
			break;
		case R.id.btnTrainNo:
			Intent intentTrainNo = new Intent(PTAddActivity.this,
					PTTrainTypeDialogActivity.class);
			intentTrainNo.putExtra(PTTrainTypeDialogActivity.KEY_TRAINTYPE,
					btnTrainNo.getText().toString().trim());
			startActivityForResult(intentTrainNo, REQUEST_CODE_SELECTTRAINTYPE);
			break;
		case R.id.btnNext:
			showProgressDialog(R.string.releasetasking);
			new Thread() {
				public void run() {
					if (checkData()) {
						insertTaskName();
						releaseTask();
					}
				}
			}.start();
			break;
		case R.id.btnReturn:
			finish();
			break;
		default:
			break;
		}
	}

	private boolean checkData() {
		mMajor.TaskName = edtTaskName.getText().toString().trim();
		if (mMajor.TaskName == null || mMajor.TaskName.equals("")) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_inputtaskname;
			myhandle.sendMessage(msg);

			return false;
		}
		
		if (mMajor.TaskName.contains("'") 
				|| mMajor.TaskName.contains("\"")
				|| mMajor.TaskName.contains("&")) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_inputvalidtaskname;
			myhandle.sendMessage(msg);

			return false;
		}
			
		// if (!CommonUtils.datecheck(mDate)) {
		// Message msg = new Message();
		// msg.what = MSG_DATA_CHECK_FAIL;
		// msg.obj = R.string.task_notify_invaliddata;
		// myhandle.sendMessage(msg);
		//
		// return false;
		// }

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SELECTCHARGE:
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Staff staff = (Staff) bundle
							.getSerializable(Contacts.KEY_STAFF);
					if (staff != null) {
						btnCharge.setText(staff.StaffName);
						mMajor.StaffId = staff.StaffId;
						mMajor.StaffName = staff.StaffName;
					}
				}
				break;
			case REQUEST_CODE_SELECTTRAINTYPE:
				Bundle bundleTrainType = data.getExtras();
				if (bundleTrainType != null) {
					String traintype = bundleTrainType.getString(PTTrainTypeDialogActivity.KEY_TRAINTYPE);
					if (traintype == null || traintype.length() == 0)
						btnTrainNo.setText(R.string.task_train_NULL);
					else
						btnTrainNo.setText(traintype);
				}
				break;
			default:
				break;
			}
		}
	}

	private void intialPopWindow() {
		View view = this.getLayoutInflater().inflate(
				R.layout.pointtask_taskname, null);

		mPopTaskName = new PopupWindow(view, edtTaskName.getWidth() - 10,
				LayoutParams.WRAP_CONTENT);
		mPopTaskName.setFocusable(false);
		mPopTaskName.update();
		mPopTaskName.setOutsideTouchable(true);
		mPopTaskName.setBackgroundDrawable(new BitmapDrawable());

		mListView = (ListView) view.findViewById(R.id.nameList);
		mAdapter = new NameAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setCacheColorHint(Color.TRANSPARENT);

		TextView txtClear = (TextView) view.findViewById(R.id.txtClear);
		txtClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						clearTaskName();
					}
				}.start();
			}
		});

		TextView txtClose = (TextView) view.findViewById(R.id.txtClose);
		txtClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopTaskName.dismiss();
			}
		});

	}

	private class NameAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public NameAdapter(Context mContext) {
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			if (mNameList == null)
				return 0;
			else if (mNameList.size() > 6)
				return 6;
			else
				return mNameList.size();
		}

		@Override
		public Object getItem(int position) {
			if (mNameList == null) {
				return null;
			} else {
				return mNameList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String content = (String) getItem(position);
			ViewHolder holder;
			if (null == convertView) {
				convertView = mInflater.inflate(
						R.layout.pointtask_taskname_item, null);
				holder = new ViewHolder();

				holder.content = (TextView) convertView
						.findViewById(R.id.txtname);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.content.setText(content);
			holder.content.setSelected(true);
			holder.content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mIsSelect = true;
					edtTaskName.setText(content);
					edtTaskName.setSelection(edtTaskName.getText().length());
				}
			});

			return convertView;
		}
	}

	private static class ViewHolder {
		TextView content;
	}

	// 搜索
	private void searchTaskName() {
		String key = edtTaskName.getText().toString();
		DBHelper dbHelper = new DBHelper(PTAddActivity.this);
		ArrayList<String> list = new ArrayList<String>();

		Cursor cursor = null;
		String sql = "select content from common_text "
				+ " where type = 'tn' and content like '" + key
				+ "%' order by date desc;";

		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String name = cursor.getString(cursor
							.getColumnIndex(DBHelper.TEXT_CONTENT));

					list.add(name);
				}

				Message msg = new Message();
				msg.what = MSG_GETDATA_SUCCEED;
				msg.obj = list;
				myhandle.sendMessage(msg);
			} else {
				myhandle.sendEmptyMessage(MSG_GETDATA_NULL);
			}

		} catch (Exception e) {
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}
	}

	// 清除历史记录
	private void clearTaskName() {
		DBHelper dbHelper = new DBHelper(PTAddActivity.this);

		Cursor cursor = null;
		String sql = "delete from common_text  where type = 'tn';";

		try {
			dbHelper.execSQL(sql);
			myhandle.sendEmptyMessage(MSG_GETDATA_NULL);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}
	}

	private void insertTaskName() {
		String key = edtTaskName.getText().toString();

		DBHelper dbHelper = new DBHelper(PTAddActivity.this);
		Cursor cursor = null;

		String sql = "select content from common_text "
				+ " where type = 'tn' and content = '" + key + "';";

		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				sql = "update common_text set date = '"
						+ System.currentTimeMillis() + "' where content = '"
						+ key + "';";
				dbHelper.execSQL(sql);
			} else {
				sql = "insert into common_text(content,type,date) values ('"
						+ key + "','" + DBHelper.TEXT_TYPE_TASKNAME + "','"
						+ System.currentTimeMillis() + "')";
				dbHelper.execSQL(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}
	}
}
