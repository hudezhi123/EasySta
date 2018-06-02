package easyway.Mobile.StationSchedule;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.VIAStation;

/*
 * 途经站信息
 */
public class TrainSchedule extends ActivityEx {
	private final int MSG_GET_VIASTATION = 0;

	private ArrayList<VIAStation> mlist;
	private ListView lstVIA;
	private String mTrainNo;
	private TrainVIAAdapter mAdapter;
	
	private TextView txtTrainNo;
	private Button btnClose;
	
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_VIASTATION:
				mlist = (ArrayList<VIAStation>) msg.obj;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.stationschedule_via);

		mTrainNo = getIntent().getStringExtra("TrainNo");
		if (!TextUtils.isEmpty(mTrainNo)){
			if (mTrainNo.contains("G"))
				mTrainNo = mTrainNo.substring(mTrainNo.indexOf("G"));

			if (mTrainNo.contains("D"))
				mTrainNo = mTrainNo.substring(mTrainNo.indexOf("D"));
		}

		
		txtTrainNo = (TextView) findViewById(R.id.txtTrainNo);
		txtTrainNo.setText(mTrainNo);
		
		btnClose = (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		lstVIA = (ListView) findViewById(R.id.lstVIA);
		mAdapter = new TrainVIAAdapter(this);
		lstVIA.setAdapter(mAdapter);

		new Thread() {
			public void run() {
				LoadVIAInfo(mTrainNo);
			}
		}.start();
	}

	// 获取车次途经站信息信息
	private void LoadVIAInfo(String trainNo) {
		if (trainNo == null)
			return;

		if (trainNo.length() == 0)
			return;

		ArrayList<VIAStation> list = new ArrayList<VIAStation>();
		DBHelper dbHelper = new DBHelper(TrainSchedule.this);
		String[] columns = { DBHelper.VIASTATION_TRNO,
				DBHelper.VIASTATION_ORDER, DBHelper.VIASTATION_STATION,
				DBHelper.VIASTATION_ARRTIME, DBHelper.VIASTATION_ARRDATE,
				DBHelper.VIASTATION_DEPATIME, DBHelper.VIASTATION_DEPADATE};
		Cursor cursor = null;

		try {
			cursor = dbHelper.exeSql(DBHelper.VIASTATION_TABLE_NAME, columns,
					DBHelper.VIASTATION_TRNO + " = '" + trainNo + "'", null, null,
					null, DBHelper.VIASTATION_ORDER);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					VIAStation station = new VIAStation();
					station.TRNO_TT = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_TRNO));
					station.StationOrder = cursor.getInt(cursor
							.getColumnIndex(DBHelper.VIASTATION_ORDER));
					station.Station = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_STATION));
					station.ArrTime = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_ARRTIME));
					station.ArrDate = cursor.getInt(cursor
							.getColumnIndex(DBHelper.VIASTATION_ARRDATE));
					station.DepaTime = cursor.getString(cursor
							.getColumnIndex(DBHelper.VIASTATION_DEPATIME));
					station.DepaDate = cursor.getInt(cursor
							.getColumnIndex(DBHelper.VIASTATION_DEPADATE));

					list.add(station);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		Message msg = new Message();
		msg.obj = list;
		msg.what = MSG_GET_VIASTATION;
		myhandle.sendMessage(msg);
	}
	
	class TrainVIAAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;

		public TrainVIAAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if (mlist == null)
				return 0;

			return mlist.size();
		}

		public Object getItem(int position) {
			if (mlist == null)
				return null;
			else
				return mlist.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View temp = null;
			if (convertView != null) {
				temp = convertView;
			} else {
				temp = layoutInflater.inflate(
						R.layout.stationschedule_via_item, parent, false);
			}

			TextView txtTrainNo = (TextView) temp.findViewById(R.id.txtTrainNo);
			TextView txtLearve = (TextView) temp.findViewById(R.id.txtLearve);
			TextView txtArrive = (TextView) temp.findViewById(R.id.txtArrive);

			int width = temp.getWidth();
			txtTrainNo.setWidth(width/3);
			txtLearve.setWidth(width/3);
			txtArrive.setWidth(width/3);
				
			final VIAStation station = (VIAStation) getItem(position);
			txtTrainNo.setText(station.StationOrder + "." + station.Station);
			if (position == 0) {
				txtLearve.setText(station.DepaTime + "发");
				txtArrive.setText("");
			} else if (position == getCount() - 1) {
				txtLearve.setText("");
				txtArrive.setText(station.ArrTime + "到");
			} else {
				txtLearve.setText(station.DepaTime + "发");
				txtArrive.setText(station.ArrTime + "到");
			}
				
			return temp;
		}
	}
}
