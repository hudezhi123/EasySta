package easyway.Mobile.Contacts;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.Department;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Message.MessageChat;
import easyway.Mobile.util.PTTUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 联系人Adapter
 */

public class ContactsAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private Context context;
	private int showflag = Contacts.SHOWFLAG_DEPT;
	private int flag = Contacts.FLAG_CONTACTS;
	private ArrayList<Staff> mStafflist;
	private ArrayList<Department> mDeptlist;

	public ContactsAdapter(Context context, int flag) {
		this.context = context;
		layoutInflater = LayoutInflater.from(this.context);
		this.flag = flag;
	}

	public void setData(ArrayList<Staff> stafflist,
			ArrayList<Department> deptlist) {
		mStafflist = stafflist;
		mDeptlist = deptlist;
	}

	public void setShow(int flag) {
		showflag = flag;
	}

	public int getCount() {
		if (showflag == Contacts.SHOWFLAG_DEPT) {
			if (mDeptlist == null)
				return 0;
			else
				return mDeptlist.size();
		} else if (showflag == Contacts.SHOWFLAG_STAFF
				|| showflag == Contacts.SHOWFLAG_ONLINE) {
			if (mStafflist == null)
				return 0;
			else
				return mStafflist.size();
		} else {
			return 0;
		}
	}

	public Object getItem(int position) {
		if (showflag == Contacts.SHOWFLAG_DEPT) {
			if (mDeptlist == null)
				return null;
			else
				return mDeptlist.get(position);
		} else if (showflag == Contacts.SHOWFLAG_STAFF
				|| showflag == Contacts.SHOWFLAG_ONLINE) {
			if (mStafflist == null)
				return null;
			else
				return mStafflist.get(position);
		} else {
			return null;
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
		if (null != obj) {
			View temp = null;
			if (convertView != null) {
				temp = convertView;
			} else {
				temp = layoutInflater.inflate(R.layout.contacts_item, parent,
						false);
			}

			ImageView imgType = (ImageView) temp.findViewById(R.id.imgType);
			TextView txtName = (TextView) temp.findViewById(R.id.txtName);

			Button btnSMS = (Button) temp.findViewById(R.id.btnSMS);
			Button btnCall = (Button) temp.findViewById(R.id.btnCall);

			if (showflag == Contacts.SHOWFLAG_DEPT) { // 显示部门结构
				final Department dept = (Department) getItem(position);
				imgType.setImageResource(R.drawable.contact_type_dep);
				btnSMS.setVisibility(View.GONE);
				btnCall.setVisibility(View.GONE);
				txtName.setText(dept.FullName);
			} else if (showflag == Contacts.SHOWFLAG_STAFF
					|| showflag == Contacts.SHOWFLAG_ONLINE) { // 显示部门人员
				final Staff staff = (Staff) getItem(position);
				
				if(flag == Contacts.FLAG_POINTTASK){
					btnSMS.setVisibility(View.GONE);
					btnCall.setVisibility(View.GONE);
					txtName.setText(staff.StaffName);
					return temp;
				}
				if (staff.Type == Staff.TYPE_GROUP) {
					imgType.setImageResource(R.drawable.contact_type_group);
				} else {
					if (staff.BOnLine)
						imgType.setImageResource(R.drawable.contact_type_man_online);
					else
						imgType.setImageResource(R.drawable.contact_type_man_offline);
				}

				btnCall.setVisibility(View.VISIBLE);
				txtName.setText(staff.StaffName);

				if (flag == Contacts.FLAG_LIVECONTROL) { // 现场指挥/联系人
					btnCall.setBackgroundResource(R.drawable.btn_videophone_selector);
					btnSMS.setVisibility(View.GONE);
				} else {
					btnCall.setBackgroundResource(R.drawable.btn_phone_selector);

					if (staff.Type == Staff.TYPE_GROUP) { // 群组号不能发消息
						btnSMS.setVisibility(View.GONE);
					} else {
						btnSMS.setVisibility(View.VISIBLE);
						btnSMS.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(context,
										MessageChat.class);
								Bundle bundle = new Bundle();
								bundle.putLong(MessageChat.TAG_STAFF_ID, staff.StaffId);
								bundle.putString(MessageChat.TAG_STAFF_NAME,
										staff.StaffName);
//								bundle.putInt(MessageChat.TAG_FLAG,
//										MessageChat.FLAG_SEND);
								intent.putExtras(bundle);
								context.startActivity(intent);
							}
						});
					}

				}

				btnCall.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (flag == Contacts.FLAG_LIVECONTROL) {
							PTTUtil.call(context, staff.Expend1,
									PTTUtil.VIDEO_CALL);
						} else {
							PTTUtil.call(context, staff.Expend1,
									PTTUtil.AUDIO_CALL);
						}
					}
				});

				if (showflag == Contacts.SHOWFLAG_ONLINE) {
					temp.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context,
									ContactDetail.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable(Contacts.KEY_STAFF, staff);
							bundle.putInt(Contacts.KEY_FLAG, flag);
							intent.putExtras(bundle);
							context.startActivity(intent);
						}
					});
				}
			} else {
				btnSMS.setVisibility(View.GONE);
				btnCall.setVisibility(View.GONE);
			}

			return temp;
		}

		return null;
	}

}
