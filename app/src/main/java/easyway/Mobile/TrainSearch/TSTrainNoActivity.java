package easyway.Mobile.TrainSearch;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*
 * 车次查询
 */
public class TSTrainNoActivity extends ActivityEx implements OnClickListener {
	private final int MSG_GETDATA_NULL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;
	private final int MSG_GETDATA_FAIL = 2;
	
	private EditText edtTrainNo;
	private String mKey = "";

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETDATA_NULL:
				showToast(R.string.exp_tssearchrestlt_null);
				break;
			case MSG_GETDATA_SUCCEED:
				ArrayList<TSResult> list = (ArrayList<TSResult>) msg.obj;
				
				 Intent intent = new Intent(TSTrainNoActivity.this,TSResultActivity.class);  
				 Bundle bundle = new Bundle();  
				 bundle.putSerializable("list", list);
				 bundle.putString("key", mKey);
				 intent.putExtras(bundle);
				 startActivity(intent);
				break;
			case MSG_GETDATA_FAIL:
				showToast(R.string.exp_tssearch);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trainsearch_trainno);

		initView();
	}

	private void initView() {
		edtTrainNo = (EditText) findViewById(R.id.edtTrainNo);
		edtTrainNo.setInputType(InputType.TYPE_NULL);

		int widthtype = Property.screenwidth / 6;
		int widthnum = Property.screenwidth / 3;

		Button btnG = (Button) findViewById(R.id.btnG);
		Button btnD = (Button) findViewById(R.id.btnD);
		Button btnC = (Button) findViewById(R.id.btnC);
		Button btnZ = (Button) findViewById(R.id.btnZ);
		Button btnT = (Button) findViewById(R.id.btnT);
		Button btnK = (Button) findViewById(R.id.btnK);
		Button btnOne = (Button) findViewById(R.id.btnOne);
		Button btnTwo = (Button) findViewById(R.id.btnTwo);
		Button btnThree = (Button) findViewById(R.id.btnThree);
		Button btnFour = (Button) findViewById(R.id.btnFour);
		Button btnFive = (Button) findViewById(R.id.btnFive);
		Button btnSix = (Button) findViewById(R.id.btnSix);
		Button btnSeven = (Button) findViewById(R.id.btnSeven);
		Button btnEight = (Button) findViewById(R.id.btnEight);
		Button btnNine = (Button) findViewById(R.id.btnNine);
		Button btnZero = (Button) findViewById(R.id.btnZero);
		Button btnDelete = (Button) findViewById(R.id.btnDelete);
		Button btnNull = (Button) findViewById(R.id.btnNull);
		Button btnBack = (Button) findViewById(R.id.btnBack);
		Button btnClear = (Button) findViewById(R.id.btnClear);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);

		btnG.setWidth(widthtype);
		btnD.setWidth(widthtype);
		btnC.setWidth(widthtype);
		btnZ.setWidth(widthtype);
		btnT.setWidth(widthtype);
		btnK.setWidth(widthtype);
		btnOne.setWidth(widthnum);
		btnTwo.setWidth(widthnum);
		btnThree.setWidth(widthnum);
		btnFour.setWidth(widthnum);
		btnFive.setWidth(widthnum);
		btnSix.setWidth(widthnum);
		btnSeven.setWidth(widthnum);
		btnEight.setWidth(widthnum);
		btnNine.setWidth(widthnum);
		btnZero.setWidth(widthnum);
		btnDelete.setWidth(widthnum);
		btnNull.setWidth(widthnum);
		btnBack.setWidth(widthnum);
		btnClear.setWidth(widthnum);
		btnSearch.setWidth(widthnum);

		btnG.setOnClickListener(this);
		btnD.setOnClickListener(this);
		btnC.setOnClickListener(this);
		btnZ.setOnClickListener(this);
		btnT.setOnClickListener(this);
		btnK.setOnClickListener(this);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnThree.setOnClickListener(this);
		btnFour.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnSix.setOnClickListener(this);
		btnSeven.setOnClickListener(this);
		btnEight.setOnClickListener(this);
		btnNine.setOnClickListener(this);
		btnZero.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnClear:
			edtTrainNo.setText("");
			break;
		case R.id.btnSearch:
			if (edtTrainNo.getText().toString().equals("")) {
				showToast(R.string.exp_tstrainno_null);
				return;
			}
				
			showProgressDialog(R.string.GettingData);
			new Thread() {
				public void run() {
					search();
				}
			}.start();
			break;
		case R.id.btnDelete:
			String str = edtTrainNo.getText().toString();
			if (str != null && str.length() != 0) {
				str = str.substring(0, str.length() - 1);
			}
			edtTrainNo.setText(str);
			break;
		case R.id.btnG:
		case R.id.btnD:
		case R.id.btnC:
		case R.id.btnZ:
		case R.id.btnT:
		case R.id.btnK:
			edtTrainNo.setText(((Button) arg0).getText());
			break;
		default:
			edtTrainNo.append(((Button) arg0).getText());
			break;
		}
	}
	
	private void search() {
		mKey = edtTrainNo.getText().toString();
		ArrayList<TSResult> list = new ArrayList<TSResult>();
		DBHelper dbHelper = new DBHelper(TSTrainNoActivity.this);
    	
        Cursor cursor = null;
        String sql = "";
		if (StringUtil.isNumeric(mKey)) {
			String values = " ("
									+ "'G" + mKey + "',"
									+ "'D" + mKey + "',"
									+ "'C" + mKey + "',"
									+ "'Z" + mKey + "',"
									+ "'T" + mKey + "',"
									+ "'K" + mKey + "')";
			sql = "select a.TRNO_TT, a.STRTSTN_TT, b.TILSTN_TT, a.DepaDate,"
					+ " a.DepaTime, b.ArrDate, b.ArrTime"
					+ " from viastation a, viastation b "
					+ " where a.TRNO_TT  = b.TRNO_TT and a.STRTSTN_TT = a.Station"
					+" and b.TILSTN_TT = b.Station and a.TRNO_TT  in " + values + " order by a.DepaTime;";
		} else {
			sql = "select a.TRNO_TT, a.STRTSTN_TT, b.TILSTN_TT, a.DepaDate,"
					+ " a.DepaTime, b.ArrDate, b.ArrTime"
					+ " from viastation a, viastation b "
					+ " where a.TRNO_TT  = b.TRNO_TT and a.STRTSTN_TT = a.Station"
					+" and b.TILSTN_TT = b.Station and a.TRNO_TT  = '" + mKey + "' order by a.DepaTime;";
		}
		
		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					TSResult result = new TSResult();
					result.TrainNo = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_TRNO));
					result.StationOrigin = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_STRTSTN));
					result.StationArr = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_TILSTN));
					result.DepTime = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_DEPATIME));
					result.ArrTime = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_ARRTIME));
					result.DepDate = cursor.getInt(cursor
							.getColumnIndex(DBHelper.VIASTATION_DEPADATE));
					result.ArrDate = cursor.getInt(cursor
							.getColumnIndex(DBHelper.VIASTATION_ARRDATE));

					list.add(result);
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
}
