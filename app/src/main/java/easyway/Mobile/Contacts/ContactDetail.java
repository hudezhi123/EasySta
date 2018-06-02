package easyway.Mobile.Contacts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Message.MessageChat;
import easyway.Mobile.util.PTTUtil;

/*
 * 联系人详情
 */
public class ContactDetail extends ActivityEx {
	private Staff mStaff = null;
	private int mFlag = Contacts.FLAG_CONTACTS;

	private TextView txtDept;
	private Button btnCall;
	private Button btnMsg;

	private final int MSG_GET_DEPT_SUCC = 0;
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_DEPT_SUCC:
				String dept = (String) msg.obj;
				txtDept.setText(dept);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_detail);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mStaff = (Staff) bundle.getSerializable(Contacts.KEY_STAFF);
			mFlag = bundle.getInt(Contacts.KEY_FLAG);
		}

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_contactdetail);

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		if (btnReturn != null) {
			btnReturn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}

		TextView txtName = (TextView) findViewById(R.id.txtName);
		TextView txtID = (TextView) findViewById(R.id.txtID);
		TextView txtMobile = (TextView) findViewById(R.id.txtMobile);
		txtDept = (TextView) findViewById(R.id.txtDept);
		btnCall = (Button) findViewById(R.id.btnCall);
		btnMsg = (Button) findViewById(R.id.btnMsg);

		if (mStaff != null) {
			txtName.setText(mStaff.StaffName);
			txtID.setText(mStaff.StaffCode);
			txtMobile.setText(mStaff.Mobile);

			btnMsg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ContactDetail.this,
							MessageChat.class);
					Bundle bundle = new Bundle();
					bundle.putLong(MessageChat.TAG_STAFF_ID, mStaff.StaffId);
					bundle.putString(MessageChat.TAG_STAFF_NAME, mStaff.StaffName);
					intent.putExtras(bundle);
					startActivity(intent);
				}

			});

			btnCall.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (mFlag == Contacts.FLAG_LIVECONTROL) {
						PTTUtil.call(ContactDetail.this, mStaff.Expend1,
								PTTUtil.VIDEO_CALL);
					} else {
						PTTUtil.call(ContactDetail.this, mStaff.Expend1,
								PTTUtil.AUDIO_CALL);
					}
				}

			});
		}

		if (mFlag == Contacts.FLAG_LIVECONTROL) {
			btnMsg.setVisibility(View.GONE);
		} else {
			btnMsg.setVisibility(View.VISIBLE);
		}

		new Thread() {
			public void run() {
				getDeptName();
			}
		}.start();
	}

	// 获取所在组名称
	private void getDeptName() {
		if (mStaff == null)
			return;

		DBHelper dbHelper = new DBHelper(ContactDetail.this);		
		Cursor cursor = null;
		String sql = "";

		sql = "select a.FullName from TB_ORG_Dept a, TB_ORG_Staff b, TB_ORG_DeptStaff c "
				+ " where a.DeptId  = c.DeptId and c.StaffId = b.StaffId and c.StaffId = '" + mStaff.StaffId + "' ;";

		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String dept = cursor.getString(cursor
							.getColumnIndex(DBHelper.DEPT_FULLNAME));
							
					Message msg = new Message();
					msg.what = MSG_GET_DEPT_SUCC;
					msg.obj = dept;
					myhandle.sendMessage(msg);
					
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}
	}
}
