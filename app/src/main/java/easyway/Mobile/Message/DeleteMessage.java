package easyway.Mobile.Message;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.util.StringUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;

/*
 * 删除短消息
 */
public class DeleteMessage extends ActivityEx {
	public DBHelper dbHelper = null;
	private ArrayList<ZWTMessageDel> mMsgList = new ArrayList<ZWTMessageDel>();
	private Adapter mAdapter;
	private ListView mListView;
	private Button mBtnDel;
	private Button mBtnSelect;

	private final int MSG_GET_SUCC = 1;		// 获取成功
	private final int MSG_GET_FAIL = 2;		// 获取失败
	private final int MSG_ITEM_CLICK = 3;	// 单独选中
	private final int MSG_SELETC_CLICK = 4;	// 点击 全选/全不选
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GET_SUCC:		// 获取成功
				mMsgList = (ArrayList<ZWTMessageDel>) msg.obj;
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_GET_FAIL:		// 获取失败
				showErrMsg(errMsg);
				break;
			case MSG_ITEM_CLICK:		// 单独选中
				int index = (Integer) msg.obj;
				mMsgList.get(index).del = !mMsgList.get(index).del;
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_SELETC_CLICK:		// 点击 全选/全不选
				boolean select = false;
				if (mBtnSelect.getText().toString()
						.equals(getString(R.string.selectAll))) {
					select = true;
					mBtnSelect.setText(R.string.unselectAll);
				} else if (mBtnSelect.getText().toString()
						.equals(getString(R.string.unselectAll))) {
					select = false;
					mBtnSelect.setText(R.string.selectAll);
				}

				if (mMsgList != null && mMsgList.size() != 0) {
					for (ZWTMessageDel obj : mMsgList)
						obj.del = select;
				}
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
		setContentView(R.layout.msg_del);

		initView();

		showProgressDialog(R.string.GettingData);

		new Thread() {
			public void run() {
				LoadMessage();		// 获取短消息
			}
		}.start();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_deletemsg);

		mListView = (ListView) findViewById(R.id.dmListMessage);
		mAdapter = new Adapter(DeleteMessage.this);
		mListView.setAdapter(mAdapter);

		mBtnDel = (Button) findViewById(R.id.dmButtonDel);
		mBtnDel.setEnabled(true);
		mBtnDel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				boolean select = false;

				for (ZWTMessageDel obj : mMsgList) {
					if (obj.del) {
						select = true;
						break;
					}
				}

				if (select) {
					new AlertDialog.Builder(DeleteMessage.this)
							.setTitle(R.string.Prompt)
							.setMessage(R.string.confirmDelete)
							.setPositiveButton(R.string.deleteMessage,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											for (ZWTMessageDel obj : mMsgList) {
												if (obj.del)
													DeleteMessageById(String
															.valueOf(obj.Id));
											}

											finish();
										}
									}).setNegativeButton(R.string.Cancel, null)
							.show();
				} else {		// 未选中任何消息
					showToast(R.string.notifyunselect);
				}

			}
		});

		mBtnSelect = (Button) findViewById(R.id.dmButtonSelect);
		mBtnSelect.setText(R.string.selectAll);
		mBtnSelect.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				myHandler.sendEmptyMessage(MSG_SELETC_CLICK);
			}
		});
	}

	// 删除单条消息
	private void DeleteMessageById(String messageId) {
		String sql = "delete from " + DBHelper.MESSAGE_TABLE_NAME + " where "
				+ DBHelper.MESSAGE_ID + " =?";

		Object[] bindArgs = { messageId };
		dbHelper.getWritableDatabase().execSQL(sql, bindArgs);
	}

	public void onPause() {
		super.onPause();

		if (dbHelper != null)
			dbHelper.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (dbHelper == null)
			dbHelper = new DBHelper(DeleteMessage.this);
	}

	// 载入消息
	private void LoadMessage() {
		ArrayList<ZWTMessageDel> msglist = new ArrayList<ZWTMessageDel>();

		Cursor cursor = null;
		try {
			if (dbHelper == null)
				dbHelper = new DBHelper(DeleteMessage.this);

			String sql = "select * from " + DBHelper.MESSAGE_TABLE_NAME
					+ " where " + DBHelper.MESSAGE_OWNERID + " = '"
					+ Property.StaffId + "' order by "
					+ DBHelper.MESSAGE_CREATETIME + " desc;";

			cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				ZWTMessageDel obj = new ZWTMessageDel();
				obj.Id = cursor.getLong(cursor
						.getColumnIndex(DBHelper.MESSAGE_ID));

				obj.createTime = cursor.getString(cursor
						.getColumnIndex(DBHelper.MESSAGE_CREATETIME));
				obj.contactName = cursor.getString(cursor
						.getColumnIndex(DBHelper.MESSAGE_CONTACTNAME));
				obj.content = cursor.getString(cursor
						.getColumnIndex(DBHelper.MESSAGE_CONTENT));
				obj.status = cursor.getInt(cursor
						.getColumnIndex(DBHelper.MESSAGE_STATUS));

				msglist.add(obj);
			}

			Message msg = new Message();
			msg.what = MSG_GET_SUCC;
			msg.obj = msglist;
			myHandler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
			myHandler.sendEmptyMessage(MSG_GET_FAIL);
		} finally {
			if (cursor != null)
				dbHelper.closeCursor(cursor);
		}
	}

	private class ZWTMessageDel extends ZWTMessage {
		public boolean del = false;
	}

	/*
	 * 删除短消息Adapter
	 */
	public class Adapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public Adapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if (mMsgList != null)
				return mMsgList.size();
			else
				return 0;
		}

		public Object getItem(int position) {
			if (mMsgList != null)
				return mMsgList.get(position);
			else
				return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View contentView,
				ViewGroup parent) {
			ZWTMessageDel data = (ZWTMessageDel) getItem(position);
			if (data != null) {
				ViewHolder holder;
				if (null == contentView) {
					contentView = mInflater
							.inflate(R.layout.msg_del_item, null);
					holder = new ViewHolder();

					holder.contactTextView = (TextView) contentView
							.findViewById(R.id.lfdtextViewContact);

					holder.messageTextView = (TextView) contentView
							.findViewById(R.id.lfdtextViewContent);
					holder.chkDel = (CheckBox) contentView
							.findViewById(R.id.lfdCheckBoxMessage);
					contentView.setTag(holder);
				} else {
					holder = (ViewHolder) contentView.getTag();
				}

				contentView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Message msg = new Message();
						msg.what = MSG_ITEM_CLICK;
						msg.obj = position;
						myHandler.sendMessage(msg);
					}
				});

				holder.contactTextView.setText(data.contactName + "  "
						+ data.createTime);
				if (data.content == null || data.content.length() == 0) {
					if (data.attach == null || data.attach.length() == 0) {
						holder.messageTextView.setText("");
					} else {
						holder.messageTextView.setText("附件");
					}
				} else {
					String strMsg = StringUtil.Encode(data.content, false);
					if (strMsg.length() >= 10)
						strMsg = strMsg.substring(0, 9) + "...";

					holder.messageTextView.setText(strMsg);
				}

				holder.chkDel.setChecked(data.del);
				holder.chkDel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Message msg = new Message();
						msg.what = MSG_ITEM_CLICK;
						msg.obj = position;
						myHandler.sendMessage(msg);
					}
				});

				return contentView;
			}

			return null;
		}
	}

	static class ViewHolder {
		TextView contactTextView;
		TextView messageTextView;

		CheckBox chkDel;
	}
}
