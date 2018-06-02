package easyway.Mobile.Shift;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Staff;

public class AddStaff extends ActivityEx {
	
	private ActivityEx act;
	private Button bt;
	private ListView list;
	private long WorkTypeId;
	private long TeamId;
	private long ShiftId;
	
	private int shift_Detail_Id;
	
	private Adapter_AddStaff adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		act = this;
		setContentView(R.layout.activity_add_staff);
		Intent intent = getIntent();
		WorkTypeId = intent.getLongExtra("WorkTypeId", 0);
		TeamId = intent.getLongExtra("TeamId", 0);
		ShiftId = intent.getLongExtra("ShiftId", 0);
		shift_Detail_Id = intent.getIntExtra("shift_Detail_Id", -1);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("分配人员");
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
	
	private void initView(){
		list = (ListView)findViewById(R.id.Activity_list);
		bt = (Button)findViewById(R.id.Activity_button);
		ArrayList<Staff> list1 = getAllStaff();
		adapter = new Adapter_AddStaff(act, list1);
		list.setAdapter(adapter);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<Long> data = new ArrayList<Long>();
				ArrayList<String> data1 = new ArrayList<String>();
				//这里为集合赋值.
				ArrayList<ArrayList<?>> mDataList = adapter.getData();
				data = (ArrayList<Long>)mDataList.get(0);
				data1 = (ArrayList<String>)mDataList.get(1);
				
				ExitApplication.getInstance().getMStaff().setData(data, data1);
				Intent intent = new Intent();
				intent.putExtra("staffId", data.get(0));
				intent.putExtra("shift_Detail_Id", shift_Detail_Id);
				setResult(1111, intent);
				finish();
			}
		});
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
