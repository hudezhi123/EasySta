package easyway.Mobile.StationSchedule;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.bean.TrainBase;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.ColorStation;
import easyway.Mobile.Data.TrainType;

/*
 * 站内列车时刻表
 */
public class StationSchedule extends ActivityEx implements OnClickListener {
	public static final int FLAG_ORIGIN = 0;
	public static final int FLAG_TERMINAL = 1;
	public static final int FLAG_REMARK = 2;

	private ListView lstSche;
	private ListView lstTrainType;
	private ArrayList<TrainBase> mListOrigin;

	private int mFlag = FLAG_ORIGIN;

	private ImageView imgOrigin;
	private ImageView imgTerminal;
	private ImageView imgRemark;
	private LinearLayout LinearSchedule;
	private LinearLayout LinearRemark;
	private TextView txtTime;
	private TextView txtStation;
	private TextView txtTimeZs;
	private TextView txtRemark;
	private TextView txtTrainInfo;
	private TextView txtStationArrTime;
	private LinearLayout LinearStationInfo;
	
	private ScheduleAdapter mScheduleAdapter;

	private final int MSG_GET_ORIGIN_SUCC = 0;
	private final int MSG_GET_DATA_FAIL = 1;
	private final int MSG_GET_TERMINAL_SUCC = 2;
	private final int MSG_GET_STAION_SUCC = 3;
	private final int MSG_GET_TYPE_SUCC = 4;
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_ORIGIN_SUCC:
				mListOrigin = new ArrayList<TrainBase>();
				if(mListOrigin.size() > 0){
					mListOrigin.clear();
				}
				mListOrigin.addAll((ArrayList<TrainBase>) msg.obj);
				
				if (mFlag == FLAG_ORIGIN) {
					mScheduleAdapter.setDate(mListOrigin, mFlag);
					mScheduleAdapter.notifyDataSetChanged();
				}
				break;
			case MSG_GET_DATA_FAIL:
				int resid = (Integer) msg.obj;
				showToast(resid);
				break;
			case MSG_GET_TERMINAL_SUCC:
				if(mListOrigin.size() > 0){
					mListOrigin.clear();
				}
				mListOrigin.addAll((ArrayList<TrainBase>) msg.obj);
				if (mFlag == FLAG_TERMINAL) {
					mScheduleAdapter.setDate(mListOrigin, mFlag);
					mScheduleAdapter.notifyDataSetChanged();
				}
				break;
			case MSG_GET_STAION_SUCC:
				ArrayList<ColorStation> ListStation = (ArrayList<ColorStation>) msg.obj;
				if (ListStation == null || ListStation.size() == 0)
					return;
				
				LinearStationInfo.removeAllViews();
				TextView txtDuty = new TextView(StationSchedule.this);
				txtDuty.setText(R.string.station_duty);
				txtDuty.setTextColor(Color.BLACK);
				txtDuty.setTextSize(20);
				LinearStationInfo.addView(txtDuty);
				for (ColorStation station :  ListStation) {
					TextView txtName= new TextView(StationSchedule.this);
					txtName.setText(station.Name);
					txtName.setTextColor(Color.BLACK);
					txtName.setTextSize(20);
					LinearStationInfo.addView(txtName);
					
					TextView txtColor = new TextView(StationSchedule.this);
					txtColor.setWidth(20);
					txtColor.setHeight(20);
					try {
						int color = Color.parseColor(station.Color);
						txtColor.setBackgroundColor(color);
					} catch (Exception e) {
						e.printStackTrace();
					}
					LinearStationInfo.addView(txtColor);
				}
				break;
			case MSG_GET_TYPE_SUCC:
				ArrayList<TrainType> ListType = (ArrayList<TrainType>) msg.obj;
				TrainTypeAdapter adapter = new TrainTypeAdapter(StationSchedule.this, ListType);
				lstTrainType.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stationschedule);

		LinearSchedule = (LinearLayout) findViewById(R.id.LinearSchedule);
		LinearRemark = (LinearLayout) findViewById(R.id.LinearRemark);
		
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtStation = (TextView) findViewById(R.id.txtStation);
		txtTimeZs = (TextView) findViewById(R.id.txtTimeZs);
		txtRemark = (TextView) findViewById(R.id.txtRemark);
		txtTrainInfo = (TextView) findViewById(R.id.txtTrainInfo);
		txtStationArrTime = (TextView) findViewById(R.id.txtStationArrTime);
		LinearStationInfo = (LinearLayout) findViewById(R.id.LinearStationInfo);
		
//		SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
//		String trainInfo = sp.getString("ScheduleRemarks", "");
//		txtTrainInfo.setText(trainInfo);
		txtTrainInfo.setVisibility(View.GONE);
		
		imgOrigin = (ImageView) findViewById(R.id.imgOrigin);
		imgOrigin.setVisibility(View.GONE);
		imgTerminal = (ImageView) findViewById(R.id.imgTerminal);
		imgTerminal.setVisibility(View.GONE);
		imgRemark = (ImageView) findViewById(R.id.imgRemark);
		imgRemark.setVisibility(View.GONE);

		imgOrigin.setOnClickListener(this);
		imgTerminal.setOnClickListener(this);
		imgRemark.setOnClickListener(this);

		lstSche = (ListView) findViewById(R.id.lstSche);
		lstSche.setCacheColorHint(0);
		mScheduleAdapter = new ScheduleAdapter(StationSchedule.this, mListOrigin);
		lstSche.setAdapter(mScheduleAdapter);
		
		lstTrainType =  (ListView) findViewById(R.id.lstTrainTypes);
		ShowView(mFlag);
		getBaseData();
	}

	private void getBaseData() {
		new Thread() {
			public void run() {
				getScheduleData(mFlag);
//				getScheduleData(FLAG_TERMINAL);
				
//				getStation();
//				getTrainType();
			}
		}.start();
	}

	// 获取时刻表
	private void getScheduleData(int flag) {
		ArrayList<TrainBase> schelist = new ArrayList<TrainBase>();
		DBHelper smsDbHelper = new DBHelper(StationSchedule.this);
		Cursor cursor = null;

		Message msg = new Message();
		try {
			String[] columns = { DBHelper.TRAINSCHE_TRNO_PRO,
					DBHelper.TRAINSCHE_STRTSTN_TT, DBHelper.TRAINSCHE_TILSTN_TT,
					DBHelper.TRAINSCHE_DepaTime,
					DBHelper.TRAINSCHE_StationArrTime,
					DBHelper.TRAINSCHE_StationDepaTime, DBHelper.TRAINSCHE_StationAttr,
					DBHelper.TRAINSCHE_Miles
					};
			cursor = smsDbHelper.exeSql(DBHelper.TRAINSCHE_TABLE_NAME,
					columns, null, null,
					null, null, DBHelper.TRAINSCHE_ID); //DBHelper.TRAINSCHE_Miles + "=?" new String[]{"0"}

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					TrainBase base = new TrainBase();
					base.TRNO_PRO = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_TRNO_PRO));
					base.STRTSTN_TT = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_STRTSTN_TT));
					base.TILSTN_TT = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_TILSTN_TT));
					base.DepaTime = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_DepaTime));
					base.StationArrTime = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_StationArrTime));
					base.StationDepaTime = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_StationDepaTime));
					base.StationAttr = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_StationAttr));
					base.Miles = cursor.getInt(cursor
							.getColumnIndex(DBHelper.TRAINSCHE_Miles));

					schelist.add(base);
				}

				msg.obj = schelist;
				if (flag == FLAG_ORIGIN)
					msg.what = MSG_GET_ORIGIN_SUCC;
				else
					msg.what = MSG_GET_TERMINAL_SUCC;
			} else {
				if (flag == FLAG_ORIGIN)
					msg.obj = R.string.exp_origin_null;
				else
					msg.obj = R.string.exp_terminal_null;
				msg.what = MSG_GET_DATA_FAIL;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

			if (flag == FLAG_ORIGIN)
				msg.obj = R.string.exp_get_origin;
			else
				msg.obj = R.string.exp_get_terminal;

			msg.what = MSG_GET_DATA_FAIL;
		} finally {
			smsDbHelper.closeCursor(cursor);
			smsDbHelper.close();
		}

		myhandle.sendMessage(msg);
	}
	
	// 获取乘务担当表
	private void getStation() {
		ArrayList<ColorStation> stationlist = new ArrayList<ColorStation>();
		DBHelper smsDbHelper = new DBHelper(StationSchedule.this);
		Cursor cursor = null;

		Message msg = new Message();
		try {
			String[] columns = {DBHelper.STATION_NAME,
					DBHelper.STATION_COLOR };
			cursor = smsDbHelper.exeSql(DBHelper.STATION_TABLE_NAME,
					columns, null, null,
					null, null, DBHelper.STATION_ID);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					ColorStation station = new ColorStation();
					station.Name = cursor.getString(cursor
							.getColumnIndex(DBHelper.STATION_NAME));
					station.Color = cursor.getString(cursor
							.getColumnIndex(DBHelper.STATION_COLOR));

					stationlist.add(station);
				}

				msg.obj = stationlist;
				msg.what = MSG_GET_STAION_SUCC;
			} else {
				msg.obj = R.string.exp_station_null;
				msg.what = MSG_GET_DATA_FAIL;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			msg.obj = R.string.exp_get_station;
			msg.what = MSG_GET_DATA_FAIL;
		} finally {
			smsDbHelper.closeCursor(cursor);
			smsDbHelper.close();
		}

		myhandle.sendMessage(msg);
	}
	
	// 获取乘务担当表
	private void getTrainType() {
		ArrayList<TrainType> typelist = new ArrayList<TrainType>();
		DBHelper smsDbHelper = new DBHelper(StationSchedule.this);
		Cursor cursor = null;

		Message msg = new Message();
		try {
			String[] columns = { DBHelper.TRAINTYPE_NAME,
					DBHelper.TRAINTYPE_NUMBER };
			cursor = smsDbHelper.exeSql(DBHelper.TRAINTYPE_TABLE_NAME,
					columns, null, null,
					null, null, DBHelper.TRAINTYPE_ID);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					TrainType type = new TrainType();
					type.Name = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINTYPE_NAME));
					type.Number = cursor.getString(cursor
							.getColumnIndex(DBHelper.TRAINTYPE_NUMBER));

					typelist.add(type);
				}

				msg.obj = typelist;
				msg.what = MSG_GET_TYPE_SUCC;
			} else {
				msg.obj = R.string.exp_traintype_null;
				msg.what = MSG_GET_DATA_FAIL;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			msg.obj = R.string.exp_get_traintype;
			msg.what = MSG_GET_DATA_FAIL;
		} finally {
			smsDbHelper.closeCursor(cursor);
			smsDbHelper.close();
		}

		myhandle.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgOrigin:
			mFlag = FLAG_ORIGIN;
			ShowView(mFlag);
			break;
		case R.id.imgTerminal:
			mFlag = FLAG_TERMINAL;
			ShowView(mFlag);
			break;
		case R.id.imgRemark:
			ShowView(FLAG_REMARK);
			break;
		default:
			break;
		}
	}

	private void ShowView(int flag) {
		imgOrigin.setEnabled(true);
		imgTerminal.setEnabled(true);
		imgRemark.setEnabled(true);
		switch (flag) {
		case FLAG_ORIGIN:
			LinearSchedule.setVisibility(View.VISIBLE);
			LinearRemark.setVisibility(View.GONE);
			imgOrigin.setEnabled(false);
			
			txtTime.setText(R.string.DEPATIME_PTTI);
			txtStation.setText(R.string.station_terminal);
			txtStationArrTime.setVisibility(View.GONE);
			txtRemark.setVisibility(View.GONE);
			getBaseData();
//			mScheduleAdapter.setDate(mListOrigin, mFlag);
//			mScheduleAdapter.notifyDataSetChanged();
			break;
		case FLAG_TERMINAL:
			LinearSchedule.setVisibility(View.VISIBLE);
			LinearRemark.setVisibility(View.GONE);
			imgTerminal.setEnabled(false);
			
//			txtTime.setText(R.string.station_origin);
//			txtStation.setText(R.string.ARRTIMR_PTTI);
			txtStationArrTime.setVisibility(View.VISIBLE);
			txtRemark.setVisibility(View.VISIBLE);
			getBaseData();
//			mScheduleAdapter.setDate(mListTerminal, mFlag);
//			mScheduleAdapter.notifyDataSetChanged();
			break;
		case FLAG_REMARK:
			LinearSchedule.setVisibility(View.GONE);
			LinearRemark.setVisibility(View.VISIBLE);
			imgRemark.setEnabled(false);
			break;
		default:
			break;
		}
	}
}
