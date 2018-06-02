package easyway.Mobile.Broadcast;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*
 * 专题广播
 */
public class BroadcastSubject extends ActivityEx implements OnClickListener {
	private ArrayList<BroadcastInfo> mListArea;
	private ArrayList<BroadcastInfo> mListCategory;
	private ArrayList<BroadcastInfo> mListTitle;
	private String mArea;
	private String mCategory;
	private String mTitle;
	private BroadcastInfo mBroadcast;
	
	private Button btnArea;
	private Button btnCategory;
	private Button btnTitle;
	private EditText edtContent;
	
	private final int FLAG_AREA = 0;
	private final int FLAG_CATEGORY = 1;
	private final int FLAG_TITLE = 2;

	private final int MSG_GET_DATA_FAIL = 0;
	private final int MSG_GET_DATA_SUCC = 1;
	private final int MSG_PLAY_FAIL = 2;
	private final int MSG_PLAY_SUCC = 3;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GET_DATA_FAIL:
				break;
			case MSG_GET_DATA_SUCC:
				break;
			case MSG_PLAY_FAIL:
				if (errMsg != null)
					showToast(errMsg);
				break;
			case MSG_PLAY_SUCC:
				showToast(R.string.broad_subject_play_succeed);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcast_subject);

		btnArea = (Button) findViewById(R.id.btnArea);
		btnCategory = (Button) findViewById(R.id.btnCategory);
		btnTitle = (Button) findViewById(R.id.btnTitle);
		btnArea.setOnClickListener(this);
		btnCategory.setOnClickListener(this);
		btnTitle.setOnClickListener(this);
		
		edtContent = (EditText) findViewById(R.id.edtContent);

		Button btnPost = (Button) findViewById(R.id.btnPost);
		btnPost.setOnClickListener(this);

		getData(FLAG_AREA);
	}

	private void PostBroad() {
		if (mBroadcast == null)
			return;
		
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("BCid", String.valueOf(mBroadcast.id));
		parmValues.put("UserName", String.valueOf(Property.UserName));
		parmValues.put("Status", getString(R.string.notbroad));
		String broadArea = CommonFunc.ClearSameItem(mBroadcast.Area, ",");
		String ideNO_EQTs = CommonFunc.ClearSameItem(mBroadcast.IDENO_EQTs, ",");
		parmValues.put("OperatingArea", broadArea);
		parmValues.put("ideNO_EQTs", ideNO_EQTs);
		parmValues.put("broadTitle", mBroadcast.Title);

		String methodPath = Constant.MP_ISTATIONSERVICE;
		String methodName = Constant.MN_PAY_BROADCAST;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.length() == 0) {
			errMsg = getString(R.string.broad_subject_play_fail);
			myhandle.sendEmptyMessage(MSG_PLAY_FAIL);
			return;
		}
		
		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			myhandle.sendEmptyMessage(MSG_PLAY_SUCC);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_PLAY_FAIL);
			break;
		}
	}

	private void postLis() {
		if (mBroadcast == null) {
			AlertDialog.Builder builder = new Builder(BroadcastSubject.this);
			builder.setMessage(R.string.broad_notify_select_broadcast);
			builder.setTitle(R.string.Prompt);
			builder.setPositiveButton(R.string.OK, null);
			builder.create().show();
		} else {
			showProgressDialog(R.string.SavingData);
			new Thread() {
				public void run() {
					PostBroad();
				}
			}.start();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnArea:
			if (mListArea == null || mListArea.size() == 0)
				showToast(R.string.broad_notify_nodata);
			else
				showSelectAreaDlg();
			break;
		case R.id.btnCategory:
			if (mArea == null)
				showToast(R.string.broad_notify_select_area);
			else if (mListCategory == null || mListCategory.size() == 0)
				showToast(R.string.broad_notify_nodata);
			else
				showSelectCategoryDlg();
			break;
		case R.id.btnTitle:
			if (mArea == null)
				showToast(R.string.broad_notify_select_area);
			else if (mCategory == null)
				showToast(R.string.broad_notify_select_category);
			else if (mListTitle == null || mListTitle.size() == 0)
				showToast(R.string.broad_notify_nodata);
			else
				showSelectTitleDlg();
			break;
		case R.id.btnPost:
			postLis();
			break;
		default:
			break;
		}
	}

	// 选择广播区域
	private void showSelectAreaDlg() {
		if (null != mListArea) {
			String[] m = new String[mListArea.size()];
			for (int i = 0; i < mListArea.size(); i++) {
				m[i] = mListArea.get(i).Area;
			}

			AlertDialog dlg = new AlertDialog.Builder(BroadcastSubject.this)
					.setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < mListArea.size()) {
								if (mArea != null
										&& !mArea.equals(mListArea.get(item).Area)) {
									mCategory = null;
									mTitle = null;
									mBroadcast = null;
									btnCategory
											.setText(R.string.task_input_workspace);
									btnTitle.setText(R.string.task_input_workspace);
								}

								mArea = mListArea.get(item).Area;
								btnArea.setText(mArea);

								getData(FLAG_CATEGORY);
							}
						}
					}).create();
			dlg.show();
		}
	};

	// 选择广播类型
	private void showSelectCategoryDlg() {
		if (null != mListCategory) {
			String[] m = new String[mListCategory.size()];
			for (int i = 0; i < mListCategory.size(); i++) {
				m[i] = mListCategory.get(i).Category;
			}

			AlertDialog dlg = new AlertDialog.Builder(BroadcastSubject.this)
					.setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < mListCategory.size()) {
								if (mCategory != null
										&& !mCategory.equals(mListCategory
												.get(item).Category)) {
									mTitle = null;
									mBroadcast = null;
									btnTitle.setText(R.string.task_input_workspace);
								}

								mCategory = mListArea.get(item).Category;
								btnCategory.setText(mCategory);

								getData(FLAG_TITLE);
							}
						}
					}).create();
			dlg.show();
		}
	};

	// 选择广播类型
	private void showSelectTitleDlg() {
		if (null != mListTitle) {
			String[] m = new String[mListTitle.size()];
			for (int i = 0; i < mListTitle.size(); i++) {
				m[i] = mListTitle.get(i).Title;
			}

			AlertDialog dlg = new AlertDialog.Builder(BroadcastSubject.this)
					.setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < mListTitle.size()) {
								mTitle = mListTitle.get(item).Title;
								btnTitle.setText(mTitle);
								edtContent.setText(mListTitle.get(item).Content);
								mBroadcast = mListTitle.get(item);
							}
						}
					}).create();
			dlg.show();
		}
	};

	private void getData(final int flag) {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				boolean ret = false;
				switch (flag) {
				case FLAG_AREA:
					ret = GetArea();
					break;
				case FLAG_CATEGORY:
					ret = GetCategory();
					break;
				case FLAG_TITLE:
					ret = GetTitle();
					break;
				default:
					break;
				}

				if (ret)
					myhandle.sendEmptyMessage(MSG_GET_DATA_SUCC);
				else
					myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
			}
		}.start();
	}

	// 获取广播区域
	private boolean GetArea() {
		if (mListArea == null)
			mListArea = new ArrayList<BroadcastInfo>();
		mListArea.clear();

		DBHelper dbHelper = new DBHelper(BroadcastSubject.this);
		Cursor cursor = null;
		try {
			String sql = "select distinct BroadcastArea,IDENO_EQTs from BroadAreaSpecSubject;";

			LogUtil.i("sql -->  " + sql);
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (null != cursor && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					BroadcastInfo item = new BroadcastInfo();
					item.Area = cursor.getString(cursor
							.getColumnIndex("BroadcastArea"));
					item.IDENO_EQTs = cursor.getString(cursor
							.getColumnIndex("IDENO_EQTs"));

					mListArea.add(item);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		if (mListArea == null || mListArea.size() == 0)
			return false;
		else
			return true;
	}

	// 获取广播类型
	private boolean GetCategory() {
		if (mListCategory == null)
			mListCategory = new ArrayList<BroadcastInfo>();
		mListCategory.clear();

		if (mArea == null)
			return false;

		DBHelper dbHelper = new DBHelper(BroadcastSubject.this);
		Cursor cursor = null;
		try {
			String sql = "select distinct BroadcastCategory,IDENO_EQTs from BroadAreaSpecSubject "
					+ "where BroadcastArea like '%" + mArea + "%';";

			LogUtil.i("sql -->  " + sql);
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (null != cursor && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					BroadcastInfo item = new BroadcastInfo();
					item.Category = cursor.getString(cursor
							.getColumnIndex("BroadcastCategory"));
					item.IDENO_EQTs = cursor.getString(cursor
							.getColumnIndex("IDENO_EQTs"));

					mListCategory.add(item);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		if (mListCategory == null || mListCategory.size() == 0)
			return false;
		else
			return true;
	}

	// 获取广播标题
	private boolean GetTitle() {
		if (mListTitle == null)
			mListTitle = new ArrayList<BroadcastInfo>();
		mListTitle.clear();

		if (mArea == null || mCategory == null)
			return false;

		DBHelper dbHelper = new DBHelper(BroadcastSubject.this);
		Cursor cursor = null;
		try {
			String sql = "select distinct id,BroadcastCategory,BroadcastTitle,BroadcastContent,BroadcastArea,IDENO_EQTs from BroadAreaSpecSubject "
					+ "where BroadcastArea like '%"
					+ mArea
					+ "%' and BroadcastCategory like '%" + mCategory + "%';";

			LogUtil.i("sql -->  " + sql);
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (null != cursor && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					BroadcastInfo item = new BroadcastInfo();
					item.id = cursor.getLong(cursor.getColumnIndex("id"));
					item.Category = cursor.getString(cursor
							.getColumnIndex("BroadcastCategory"));
					item.Title = cursor.getString(cursor
							.getColumnIndex("BroadcastTitle"));
					item.Content = cursor.getString(cursor
							.getColumnIndex("BroadcastContent"));
					item.Area =  cursor.getString(cursor
							.getColumnIndex("BroadcastArea"));
					item.IDENO_EQTs = cursor.getString(cursor
							.getColumnIndex("IDENO_EQTs"));

					mListTitle.add(item);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		if (mListTitle == null || mListTitle.size() == 0)
			return false;
		else
			return true;
	}
}
