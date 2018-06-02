package easyway.Mobile.Message;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
 *  常用短消息
 */
public class CommonMessage extends ActivityEx {
	private ArrayList<String> comMsgs;
	private MsgAdapter msgAdapter;
	private ListView msgList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_com);

		msgList = (ListView) findViewById(R.id.msgList);
		msgAdapter = new MsgAdapter(this);
		msgList.setAdapter(msgAdapter);

		msgList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == msgAdapter.getCount() - 1) { // 添加常用短语
					final EditText edttext = new EditText(arg1.getContext());
					edttext.setMaxLines(5);
					edttext.setLines(5);
					edttext.setPadding(10, 10, 10, 10);
					edttext.setGravity(Gravity.LEFT | Gravity.TOP);

					AlertDialog.Builder builder = new Builder(
							CommonMessage.this)
							.setTitle(R.string.addcommonmsg)
							.setView(edttext)
							.setPositiveButton(R.string.OK,
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											String msg = edttext.getText()
													.toString();
											addCommonMSG(msg);

											onResume();
										}
									}).setNegativeButton(R.string.Cancel, null);

					builder.create().show();
				} else {
					// 选择常用短语
					String msg = comMsgs.get(arg2);
					Intent intent = new Intent(CommonMessage.this,
							MessageChat.class);

					intent.putExtra("return", msg);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});

		// 长按
		msgList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == msgAdapter.getCount() - 1) {
					// 长按 “添加常用短语” 不做处理
				} else {
					try {
						final String msg = comMsgs.get(arg2);
						AlertDialog dlg = new AlertDialog.Builder(
								CommonMessage.this)
								.setTitle("")
								.setItems(R.array.MsgDelete,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int item) {
												if (item == 1) { // 取消
													// do nothing
												} else {
													// 删除
													removeCommonMSG(msg);

													// 界面刷新
													onResume();
												}
											}
										}).create();
						dlg.show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		});
	}

	public void onResume() {
		super.onResume();
		getCommonMSG();
		msgAdapter.notifyDataSetChanged();
	}

	// 获取常用消息
	private void getCommonMSG() {
		if (comMsgs == null)
			comMsgs = new ArrayList<String>();
		else
			comMsgs.clear();

		DBHelper dbHelper = new DBHelper(CommonMessage.this);
		Cursor cursor = null;
		
		String sql = "select content from common_text "
				+ " where type = '" + DBHelper.TEXT_TYPE_MSG + "';";

		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String msg = cursor.getString(cursor
							.getColumnIndex(DBHelper.TEXT_CONTENT));

					comMsgs.add(msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}
	}

	// 删除常用消息
	private void removeCommonMSG(String content) {
		DBHelper dbHelper = new DBHelper(CommonMessage.this);

		try {
			String sql = "delete from " + DBHelper.TEXT_TABLE_NAME + " where "
					+ DBHelper.TEXT_TYPE + " = '" + DBHelper.TEXT_TYPE_MSG
					+ "' and " + DBHelper.TEXT_CONTENT + " =?";
			Object[] bindArgs = { content };

			SQLiteDatabase dbDatabase = dbHelper.getWritableDatabase();
			dbDatabase.execSQL(sql, bindArgs);
			dbDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}
	}

	// 添加常用消息
	private void addCommonMSG(String msg) {
		if (msg == null)
			return;

		if (msg.trim().length() == 0)
			return;

		DBHelper dbHelper = new DBHelper(CommonMessage.this);
		try {
			String sql = "INSERT INTO " + DBHelper.TEXT_TABLE_NAME + "("
					+ DBHelper.TEXT_TYPE + ", " + DBHelper.TEXT_CONTENT
					+ ")values(?,?);";
			Object[] bindArgs = { DBHelper.TEXT_TYPE_MSG, msg };

			SQLiteDatabase dbDatabase = dbHelper.getWritableDatabase();
			dbDatabase.execSQL(sql, bindArgs);
			dbDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.close();
		}
	}

	/*
	 * adapter
	 */
	private class MsgAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MsgAdapter(Context mContext) {
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			if (comMsgs == null)
				return 1;
			else
				return comMsgs.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (comMsgs == null) {
				return getString(R.string.addcommonmsg);
			} else {
				if (position < comMsgs.size()) {
					return comMsgs.get(position);
				} else {
					return getString(R.string.addcommonmsg);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String content = (String) getItem(position);
			ViewHolder holder;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.msg_com_item, null);
				holder = new ViewHolder();

				holder.content = (TextView) convertView
						.findViewById(R.id.txtmsg);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.content.setText(content);
			holder.content.setSelected(true);

			return convertView;
		}
	}

	private static class ViewHolder {
		TextView content;
	}
}
