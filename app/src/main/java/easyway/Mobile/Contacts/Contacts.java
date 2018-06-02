package easyway.Mobile.Contacts;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.Data.Department;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Data.Station;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/*
 * 联系人/现场指挥
 */
public class Contacts extends ActivityEx {
	public static final int SHOWFLAG_DEPT = 1; // 组织结构
	public static final int SHOWFLAG_STAFF = 2; // 人员
	public static final int SHOWFLAG_ONLINE = 3; // 在线人员

	public static final String KEY_FLAG = "flag";
	public static final String KEY_STAFF = "staff";
	
	public static final int FLAG_CONTACTS = 0;		// 联系人
	public static final int FLAG_LIVECONTROL = 1;	// 现场指挥
	public static final int FLAG_POINTTASK = 2;		// 重点任务
	public static final int FLAG_VACATION = 3;		// 请假处理
	
	private final long DEPT_ROOT = 0;		// 根目录
	private final long DEPT_SEARCH = -1;		// 搜索

	private TextView txtMenu; // 导航栏
	private ListView lstView;
	private ContactsAdapter mAdapter;
	private ArrayList<Staff> mStafflist;
	private ArrayList<Department> mDeptlist;
	private AutoCompleteTextView edtSearch;
	private ImageView imgDel;

	private int mFlag = FLAG_CONTACTS; // false:联系人 true:现场指挥
	private ArrayList<Department> mStack = new ArrayList<Department>();
	private ArrayList<Staff> mOnlineStafflist = new ArrayList<Staff>();
	private TextView txtOnlinesMenu;
	private ListView lstOnlines;
	private ContactsAdapter mOnlineAdapter;
	private ArrayList<Long> mOnlines;
	private Button btnset;
	private Station mStation;
	private TextView txtStation;

	private int showflag = SHOWFLAG_DEPT;
	private LinearLayout LayoutAll;
	private LinearLayout LayoutOnline;

	private final int MSG_GET_DEPT_SUCC = 1; // 获取组织结构成功
	private final int MSG_GET_STAFF_SUCC = 2; // 获取人员列表成功
	private final int MSG_GET_DATA_NULL = 3; // 获取数据为空
	private final int MSG_GET_DATA_FAIL = 4; // 获取数据失败
	private final int MSG_SEARCH_END = 5; // 检索完成
	private final int MSG_GET_ONLINE_SUCC = 6;// 获取在线人员列表成功
	private final int MSG_GET_ONLINE_FAIL = 7;// 获取在线人员列表失败
	private final int MSG_GET_ONLINE_STAFF_SUCC = 8;		// 获取在线人员列表成功
	private final int MSG_SEARCH_ONLINE_END = 10;	// 检索在线人员完成
	private final int MSG_GET_STATION_SUCC = 11;	// 获取站码成功
	private final int MSG_GET_STATION_FAIL = 12;	// 获取站码失败

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_DEPT_SUCC:	// 获取组织结构成功
				setMenu(false);
				showflag = SHOWFLAG_DEPT;
				mDeptlist = (ArrayList<Department>) msg.obj;
				mAdapter.setShow(showflag);
				mAdapter.setData(mStafflist, mDeptlist);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_GET_STAFF_SUCC:	// 获取人员列表成功
				setMenu(false);
				showflag = SHOWFLAG_STAFF;
				mStafflist = (ArrayList<Staff>) msg.obj;
				if (mStafflist != null && mStafflist.size() != 0
						&& mOnlines != null) {
					for (Staff staff : mStafflist) {
						if (mOnlines.contains(staff.StaffId)) {
							staff.BOnLine = true;
						} else {
							staff.BOnLine = false;
						}
					}
				}
				mAdapter.setShow(showflag);
				mAdapter.setData(mStafflist, mDeptlist);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_GET_DATA_NULL:	// 获取数据为空
				mStack.remove(mStack.size() - 1);
				showErrMsg(R.string.NonRecord);
				break;
			case MSG_GET_DATA_FAIL:// 获取数据失败
				mStack.remove(mStack.size() - 1);
				showErrMsg(R.string.exp_getdata);
				break;
			case MSG_SEARCH_END:// 检索完成
				setMenu(true);
				showflag = SHOWFLAG_STAFF;
				mStafflist = (ArrayList<Staff>) msg.obj;
				mAdapter.setShow(showflag);
				mAdapter.setData(mStafflist, mDeptlist);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_SEARCH_ONLINE_END:// 获取在线人员列表成功
				txtOnlinesMenu.setVisibility(View.VISIBLE);

				mOnlineAdapter.setData((ArrayList<Staff>) msg.obj, null);
				mOnlineAdapter.notifyDataSetChanged();
				break;
			case MSG_GET_ONLINE_SUCC:// 获取在线人员列表成功
				mOnlines = (ArrayList<Long>) msg.obj;

				getOnlineStaff();
				if (mOnlines == null || mOnlines.size() == 0)
					break;

				if (mStafflist == null || mStafflist.size() == 0) {
					// do nothing
				} else {
					for (Staff staff : mStafflist) {
						if (mOnlines.contains(staff.StaffId)) {
							staff.BOnLine = true;
						} else {
							staff.BOnLine = false;
						}
					}

					mAdapter.setData(mStafflist, mDeptlist);
					mAdapter.notifyDataSetChanged();
				}

				break;
			case MSG_GET_ONLINE_FAIL: // 获取在线人员列表失败
				showToast(R.string.exp_getonlineuser);
				break;
			case MSG_GET_ONLINE_STAFF_SUCC: // 获取在线人员列表成功

				mOnlineStafflist = (ArrayList<Staff>) msg.obj;
				LogUtil.i("mOnlineStafflist -->" + mOnlineStafflist.size());
				mOnlineAdapter.setData(mOnlineStafflist, null);
				mOnlineAdapter.notifyDataSetChanged();
				break;
			case MSG_GET_STATION_SUCC:
				Property.AllStation = (ArrayList<Station>) msg.obj;
				break;
			case MSG_GET_STATION_FAIL:
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);

		Bundle bundle = getIntent().getExtras();
		mFlag = FLAG_CONTACTS;
		if (bundle != null) {
			mFlag = bundle.getInt(KEY_FLAG, FLAG_CONTACTS);
		}

		TextView title = (TextView) findViewById(R.id.title);
		if (mFlag == FLAG_LIVECONTROL)
			title.setText(R.string.title_livectrl);
		else
			title.setText(R.string.title_contact);

		txtStation = (TextView) findViewById(R.id.station);

		mStation = Property.OwnStation;
		if (mStation != null) {
			txtStation.setText("(" + mStation.Name + ")");
		}
		if (mFlag == FLAG_POINTTASK || mFlag == FLAG_VACATION)
			txtStation.setVisibility(View.GONE);
		else
			txtStation.setVisibility(View.VISIBLE);

		txtStation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSelectStaionDlg();
			}

		});

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		if (btnReturn != null) {
			btnReturn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}

		btnset = (Button) findViewById(R.id.btnset);
		btnset.setVisibility(View.VISIBLE);
		btnset.setText(R.string.contact_online);
		btnset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LayoutOnline.getVisibility() == View.GONE) {
					LayoutAll.setVisibility(View.GONE);
					LayoutOnline.setVisibility(View.VISIBLE);
					showflag = SHOWFLAG_STAFF;
					btnset.setText(R.string.contact_all);
				} else {
					LayoutAll.setVisibility(View.VISIBLE);
					LayoutOnline.setVisibility(View.GONE);
					showflag = SHOWFLAG_DEPT;					
					btnset.setText(R.string.contact_online);
				}
			}
		});

		LayoutAll = (LinearLayout) findViewById(R.id.LayoutAll);
		LayoutOnline = (LinearLayout) findViewById(R.id.LayoutOnline);
		LayoutOnline.setVisibility(View.GONE);

		// 导航栏
		txtMenu = (TextView) findViewById(R.id.txtMenu);
		txtMenu.setVisibility(View.GONE);
		txtMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mStack == null || mStack.size() < 2) {
					txtMenu.setVisibility(View.GONE);
				} else {
					mStack.remove(mStack.size() - 1);

					new Thread() {
						public void run() {
							getData();
						}
					}.start();
				}
			}

		});

		edtSearch = (AutoCompleteTextView) findViewById(R.id.edtSearch);
		imgDel = (ImageView) findViewById(R.id.imgDel);
		imgDel.setVisibility(View.INVISIBLE);
		imgDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edtSearch.setText("");
			}
		});

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// do nothing
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// do nothing
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (edtSearch.getText().toString().length() != 0) {
					imgDel.setVisibility(View.VISIBLE);
					new Thread() {
						public void run() {
							searchStaff();
						}
					}.start();
				} else {
					imgDel.setVisibility(View.INVISIBLE);
					getBaseData();

					txtOnlinesMenu.setVisibility(View.GONE);
					mOnlineAdapter.setData(mOnlineStafflist, null);
					mOnlineAdapter.notifyDataSetChanged();
				}
			}
		});

		lstView = (ListView) findViewById(R.id.lstContacts);
		mAdapter = new ContactsAdapter(this, mFlag);
		lstView.setAdapter(mAdapter);
		lstView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (showflag == SHOWFLAG_DEPT) {
					mStack.add(mDeptlist.get(arg2));
					new Thread() {
						public void run() {
							getData();
						}
					}.start();
				} else if (showflag == SHOWFLAG_STAFF) {
					Staff staff = mStafflist.get(arg2);
					if (staff.Type == Staff.TYPE_GROUP)
						return;

					if (mFlag == FLAG_POINTTASK || mFlag == FLAG_VACATION) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable(KEY_STAFF, staff);
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						finish();
					} else {
						Intent intent = new Intent(Contacts.this,
								ContactDetail.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(KEY_STAFF, staff);
						bundle.putInt(KEY_FLAG, mFlag);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			}
		});

		// 在线人员
		lstOnlines = (ListView) findViewById(R.id.lstOnlines);
		mOnlineAdapter = new ContactsAdapter(this, mFlag);
		mOnlineAdapter.setShow(SHOWFLAG_ONLINE);
		lstOnlines.setAdapter(mOnlineAdapter);
		lstOnlines.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (showflag == SHOWFLAG_DEPT) {
					mStack.add(mDeptlist.get(arg2));
					new Thread() {
						public void run() {
							getData();
						}
					}.start();
				} else if (showflag == SHOWFLAG_STAFF) {
					Staff staff = mOnlineStafflist.get(arg2);
					if (staff.Type == Staff.TYPE_GROUP)
						return;

					if (mFlag == FLAG_POINTTASK || mFlag == FLAG_VACATION) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable(KEY_STAFF, staff);
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						finish();
					} else {
						Intent intent = new Intent(Contacts.this,
								ContactDetail.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(KEY_STAFF, staff);
						bundle.putInt(KEY_FLAG, mFlag);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			}
		});		

		txtOnlinesMenu = (TextView) findViewById(R.id.txtOnlinesMenu);
		txtOnlinesMenu.setVisibility(View.GONE);
		txtOnlinesMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				txtOnlinesMenu.setVisibility(View.GONE);
				mOnlineAdapter.setData(mOnlineStafflist, null);
				mOnlineAdapter.notifyDataSetChanged();
			}

		});

		getBaseData();
		getServerData();

		if (Property.AllStation == null || Property.AllStation.size() == 0) {
			new Thread() {
				public void run() {
					getAllStationCode();
				}
			}.start();
		}
	}

	private void getServerData() {
		new Thread() {
			public void run() {
				getOnLineContact();
			}
		}.start();
	}

	// 获取在线人员列表
	private void getOnLineContact() {
		ArrayList<Long> onlines = new ArrayList<Long>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		String methodPath = Constant.MP_ISTATIONSERVICE;
		String methodName = Constant.MN_GET_ONLINE_USER;
		parmValues.put("sessionId", Property.SessionId);
		if (mStation != null)
			parmValues.put("stationCode", mStation.Code);
		WebServiceManager webServiceManager = new WebServiceManager(
				Contacts.this, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		if (result == null || result.equals("")) {
			myhandle.sendEmptyMessage(MSG_GET_ONLINE_FAIL);
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				Long StaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "StaffId");
				onlines.add(StaffId);
			}
			Message msg = new Message();
			msg.what = MSG_GET_ONLINE_SUCC;
			msg.obj = onlines;
			myhandle.sendMessage(msg);
			break;
		case Constant.EXCEPTION:
		default:
			myhandle.sendEmptyMessage(MSG_GET_ONLINE_FAIL);
			break;
		}
	}

	// 获取站码
	private void getAllStationCode() {
		ArrayList<Station> stations = new ArrayList<Station>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		String methodPath = Constant.MP_SPARK;
		String methodName = Constant.MN_GET_STATIONCODE;
		parmValues.put("sessionId", Property.SessionId);
		WebServiceManager webServiceManager = new WebServiceManager(
				Contacts.this, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		if (result == null || result.equals("")) {
			myhandle.sendEmptyMessage(MSG_GET_STATION_FAIL);
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			for (int i = 0; i < jsonArray.length(); i++) {
				Station station = new Station();
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				station.Code = JsonUtil.GetJsonObjStringValue(jsonObj,
						"StationCode");
				station.Name = JsonUtil.GetJsonObjStringValue(jsonObj,
						"StationName");
				stations.add(station);
			}
			Message msg = new Message();
			msg.what = MSG_GET_STATION_SUCC;
			msg.obj = stations;
			myhandle.sendMessage(msg);
			break;
		case Constant.EXCEPTION:
		default:
			myhandle.sendEmptyMessage(MSG_GET_STATION_FAIL);
			break;
		}
	}

	private void getBaseData() {
		if (mStack == null)
			mStack = new ArrayList<Department>();
		mStack.clear();

		Department dept = new Department();
		dept.DeptId = DEPT_ROOT;
		dept.FullName = getString(R.string.contactframe);

		mStack.add(dept);
		new Thread() {
			public void run() {
				getData();
			}
		}.start();
	}

	// 匹配在线人员与本地所有人员列表
	private void getOnlineStaff() {
		new Thread() {
			public void run() {
				ArrayList<Staff> staffs = getAllStaff();
				ArrayList<Staff> onlines = new ArrayList<Staff>();
				if (staffs == null || staffs.size() == 0 || mOnlines == null
						|| mOnlines.size() == 0) {

				} else {
					for (Staff staff : staffs) {
						if (mOnlines.contains(staff.StaffId)) {
							staff.BOnLine = true;
							onlines.add(staff);
						}
					}
				}

				Message msg = new Message();
				msg.obj = onlines;
				msg.what = MSG_GET_ONLINE_STAFF_SUCC;
				myhandle.sendMessage(msg);
			}
		}.start();
	}

	// 获取所有人员
	private ArrayList<Staff> getAllStaff() {
		ArrayList<Staff> stafflist = new ArrayList<Staff>();
		DBHelper dbHelper = new DBHelper(Contacts.this);
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

	// 检索
	private void searchStaff() {
		String searchkey = edtSearch.getText().toString().trim();
		if (searchkey == null)
			return;

		if (searchkey.trim().length() == 0)
			return;

		if (mStation == null)
			return;

		ArrayList<Staff> stafflist = new ArrayList<Staff>();
		DBHelper dbHelper = new DBHelper(Contacts.this);
		String[] columns = { DBHelper.STAFF_ID, DBHelper.STAFF_NAME,
				DBHelper.STAFF_TYPE, DBHelper.STAFF_CODE, DBHelper.STAFF_MOBILE, DBHelper.STAFF_EXPEND1 };
		Cursor cursor = null;

		try {
			if (mFlag == FLAG_LIVECONTROL) {
				cursor = dbHelper.exeSql("TB_ORG_Staff", columns, "Type = "
						+ Staff.TYPE_STAFF + " and StaffName like '%"
						+ searchkey.trim() + "%' and HomeAddress = '"
						+ mStation.Code + "'", null, null, null, "StaffCode");
			} else {
				cursor = dbHelper.exeSql("TB_ORG_Staff", columns,
						"StaffName like '%" + searchkey.trim() + "%' "
								+ " and ( Owner = " + Staff.OWNER_ALL
								+ " or  Owner = " + Property.StaffId
								+ " )  and HomeAddress = '" + mStation.Code
								+ "'", null, null, null, "StaffCode");
			}

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
					staff.Mobile = cursor.getString(cursor
							.getColumnIndex(DBHelper.STAFF_MOBILE));
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

		Message msg = new Message();
		msg.obj = stafflist;
		msg.what = MSG_SEARCH_END;
		myhandle.sendMessage(msg);

		ArrayList<Staff> onlines = new ArrayList<Staff>();
		if (mOnlineStafflist != null) {
			for (Staff staff : mOnlineStafflist) {
				if (staff.StaffName.contains(searchkey.trim()))
					onlines.add(staff);
			}
		}
		Message msgonline = new Message();
		msgonline.obj = onlines;
		msgonline.what = MSG_SEARCH_ONLINE_END;
		myhandle.sendMessage(msgonline);
	}

	// 数据检索
	private void getData() {
		if (mStation == null)
			return;
		ArrayList<Department> deptlist = new ArrayList<Department>();
		ArrayList<Staff> stafflist = new ArrayList<Staff>();
		DBHelper dbHelper = new DBHelper(Contacts.this);

		Cursor cursor = null;
		Long deptId = mStack.get(mStack.size() - 1).DeptId;
		try {
			if (deptId == DEPT_ROOT) { // 根目录
				String sql = "select distinct a.DeptId, a.FullName, a.ParentDeptId, a.Type from TB_ORG_Dept a, TB_ORG_Dept b "
						+ " where a.DeptMark = '1' and a.Expend1 = '"
						+ mStation.Code
						+ "' and (a.ParentDeptId = '0' or (a.ParentDeptId = b.DeptId and  b.Expend1 <> '"
						+ mStation.Code + "')) order by a.DeptId;";

				LogUtil.i("sql -->  " + sql);
				cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			} else {
				String[] columns = { DBHelper.DEPT_ID, DBHelper.DEPT_FULLNAME,
						DBHelper.DEPT_PARENTID, DBHelper.DEPT_TYPE };
				cursor = dbHelper.exeSql(DBHelper.DEPT_TABLE_NAME, columns,
						DBHelper.DEPT_PARENTID + " = " + deptId + " and "
								+ DBHelper.DEPT_DEPTMARK + " = '1' and "
								+ DBHelper.DEPT_EXPEND1 + " = '"
								+ mStation.Code + "'", null, null, null, null);
			}

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Department dept = new Department();
					dept.ParentDeptId = cursor.getLong(cursor
							.getColumnIndex(DBHelper.DEPT_PARENTID));
					dept.DeptId = cursor.getLong(cursor
							.getColumnIndex(DBHelper.DEPT_ID));
					dept.FullName = cursor.getString(cursor
							.getColumnIndex(DBHelper.DEPT_FULLNAME));
					dept.type = cursor.getInt(cursor
							.getColumnIndex(DBHelper.DEPT_TYPE));

					if (mFlag == FLAG_LIVECONTROL
							&& dept.type == Staff.TYPE_GROUP) {
						// 现场指挥，群组不显示
					} else {
						deptlist.add(dept);
					}
				}

				Message msg = new Message();
				msg.obj = deptlist;
				msg.what = MSG_GET_DEPT_SUCC;
				myhandle.sendMessage(msg);
			} else {
				String sql = "select a.StaffId, a.StaffCode, a.StaffName, a.Mobile, a.Expend1 from TB_ORG_Staff a, TB_ORG_DeptStaff b "
						+ " where a.StaffId = b.StaffId and b.DeptId  = "
						+ deptId
						+ " and ( a.Owner = "
						+ Staff.OWNER_ALL
						+ " or a.Owner =  "
						+ Property.StaffId
						+ " ) order  by  a.StaffCode;";

				cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						Staff staff = new Staff();
						staff.StaffId = cursor.getLong(cursor
								.getColumnIndex(DBHelper.STAFF_ID));
						staff.StaffName = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_NAME));
						// staff.Type = cursor.getInt(cursor
						// .getColumnIndex(DBHelper.STAFF_TYPE));
						staff.StaffCode = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_CODE));
						staff.Mobile = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_MOBILE));
						staff.Expend1 = cursor.getString(cursor
								.getColumnIndex(DBHelper.STAFF_EXPEND1));
						
						stafflist.add(staff);
					}

					Message msg = new Message();
					msg.obj = stafflist;
					msg.what = MSG_GET_STAFF_SUCC;
					myhandle.sendMessage(msg);
				} else {
					myhandle.sendEmptyMessage(MSG_GET_DATA_NULL);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}
	}

	// 设置导航栏信息
	private void setMenu(boolean search) {
		txtMenu.setVisibility(View.VISIBLE);

		if (search) {	// 搜索情况
			mStack.clear();
			Department dept = new Department();
			dept.DeptId = DEPT_ROOT;
			dept.FullName = getString(R.string.contactframe);
			mStack.add(dept);

			Department deptSearch = new Department();
			deptSearch.DeptId = DEPT_SEARCH;
			deptSearch.FullName = getString(R.string.searchresult);
			mStack.add(deptSearch);
		} else {
			if (mStack.size() < 2) {
				txtMenu.setVisibility(View.GONE);
			}
		}

		String text = "";
		for (int i = 0; i < mStack.size(); i++) {
			text += mStack.get(i).FullName;
			if (i != mStack.size() - 1)
				text += " > ";
		}
		txtMenu.setText(text);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			finish();
		}
		return true;
	}

	// 选择所属站
	private void showSelectStaionDlg() {
		if (Property.AllStation == null || Property.AllStation.size() == 0)
			return;

		String[] m = new String[Property.AllStation.size()];
		for (int i = 0; i < Property.AllStation.size(); i++) {
			m[i] = Property.AllStation.get(i).Name;
		}

		AlertDialog dlg = new AlertDialog.Builder(Contacts.this).setTitle("")
				.setItems(m, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item < Property.AllStation.size()) {
							mStation = Property.AllStation.get(item);

							if (mStation != null) {
								txtStation.setText("(" + mStation.Name + ")");
								getBaseData();
								getServerData();
							}
						}
					}
				}).create();
		dlg.show();
	};
}
