package easyway.Mobile.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Data.MessageContact;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

/*
 * 选择收件人
 */
public class SelectContact extends ActivityEx {
    private ArrayList<MessageContact> mContacts;
    private PullRefreshListView lstContacts;
    private SelectContactAdapter mAdapter;
    private CheckBox mCheckBox;

    private ExpandableListView mlExpandableListView;

    private String selectedStaffString = "";
    private AutoCompleteTextView edtSearch;
    private ImageView imgDel;

    private Map<Long, MessageContact> selectedMap = new HashMap<Long, MessageContact>();
    private ArrayList<Long> mOnlines;
    private final int MSG_GET_CONTACT = 0;    // 获取联系人
    private final int MSG_GET_ONLINE_FAIL = 1;    // 获取在线联系人失败
    private final int MSG_GET_ONLINE_SUCC = 2;    // 获取在线联系人成功
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_CONTACT:        // 获取联系人
                    ArrayList<MessageContact> objects = (ArrayList<MessageContact>) msg.obj;

                    if (!selectedStaffString.equals("")) {
                        String[] selectStaffStrings = selectedStaffString
                                .split(":");
                        if (selectStaffStrings.length == objects.size()) {
                            isAllSelected = true;
                        } else {
                            isAllSelected = false;
                        }
                        mCheckBox.setChecked(isAllSelected);
                        for (int i = 0; i < selectStaffStrings.length; i++) {
                            String[] contactArray = selectStaffStrings[i]
                                    .split(",");

                            for (int j = 0; j < objects.size(); j++) {
                                if (objects.get(j).contactId == Long
                                        .valueOf(contactArray[0])) {
                                    selectedMap.put(objects.get(j).contactId,
                                            objects.get(j));
                                    break;
                                }
                            }
                        }
                    }

                    if (objects != null && objects.size() != 0 && mOnlines != null
                            && mOnlines.size() != 0) {
                        ArrayList<MessageContact> Online = new ArrayList<MessageContact>();
                        ArrayList<MessageContact> Offline = new ArrayList<MessageContact>();
                        for (MessageContact contact : objects) {
                            if (mOnlines.contains(contact.contactId)) {
                                Online.add(contact);
                            } else {
                                Offline.add(contact);
                            }
                        }
                        Online.addAll(Offline);
                        objects = Online;
                    }

                    mContacts = objects;
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_GET_ONLINE_FAIL:        // 获取在线联系人失败
                    closeProgressDialog();
                    showToast(errMsg);
                    break;
                case MSG_GET_ONLINE_SUCC:        // 获取在线联系人成功
                    closeProgressDialog();
                    if (isPull) {
                        isPull = false;
                        lstContacts.onRefreshComplete();
                    }
                    if (mOnlines != null && mOnlines.size() > 0) {
                        mOnlines.clear();
                    }
                    mOnlines = (ArrayList<Long>) msg.obj;
                    if (mContacts == null)
                        break;

                    if (mOnlines == null || mOnlines.size() == 0)
                        break;
                    StringBuilder sb = new StringBuilder("");
                    ArrayList<MessageContact> Online = new ArrayList<MessageContact>();
                    ArrayList<MessageContact> Offline = new ArrayList<MessageContact>();
                    for (MessageContact contact : mContacts) {
                        if (mOnlines.contains(contact.contactId)) {
                            sb.append(contact.contactName);
                            sb.append(",");
                            contact.BOnline = true;
                            Online.add(contact);
                        } else {
                            contact.BOnline = false;
                            Offline.add(contact);
                        }
                    }
//                    Toast.makeText(SelectContact.this, sb.toString() + "", Toast.LENGTH_SHORT).show();
                    Online.addAll(Offline);
                    mContacts = Online;
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };


    //	private Map<Long, MessageContact> tempMap = new HashMap<Long, MessageContact>();
    private boolean isAllSelected = false;

    //全选
    private void addAll() {

        //当全选了联系人退出，再进入时，先更改了CheckBox的状态，而这是mContacts为null。
        if (mContacts == null) {
            return;
        }

        for (MessageContact messageContact : mContacts) {
            if (!selectedMap.containsKey(messageContact.contactId)) {
                selectedMap.put(messageContact.contactId, messageContact);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    //全部移除
    private void removeAll() {
        selectedMap.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_contact);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_selectcontact);
        mCheckBox = (CheckBox) findViewById(R.id.cb_select_all);


//		不要使用回调setOnCheckedChangeListener，会发现逻辑上存在bug。
        mCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mCheckBox.isChecked();
                if (isChecked) {
                    addAll();
                } else {
                    removeAll();
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedStaffString = bundle.getString("selectedStaff");
        }

        Button okButton = (Button) findViewById(R.id.scButtonOk);
        okButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SelectContact.this,
                        MessageChat.class);
                String contactListString = "";

                for (Map.Entry<Long, MessageContact> entry : selectedMap
                        .entrySet()) {
                    MessageContact contact = entry.getValue();
                    contactListString += contact.contactId + ","
                            + contact.contactName + ":";
                }

                intent.putExtra("return", contactListString);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.scButtonCancel);
        cancelButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SelectContact.this,
                        MessageChat.class);
                setResult(RESULT_CANCELED, intent);
                finish();
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

                RenderContact();
                if (edtSearch.getText().toString().length() != 0) {
                    imgDel.setVisibility(View.VISIBLE);
                } else {
                    imgDel.setVisibility(View.INVISIBLE);

                }
            }
        });

        lstContacts = (PullRefreshListView) findViewById(R.id.scListContact);
        mAdapter = new SelectContactAdapter(SelectContact.this);
        lstContacts.setAdapter(mAdapter);
        lstContacts.setonRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPull = true;
                getContactList();
            }
        });
        getContactList();
    }

    private boolean isPull = false;

    private void getContactList() {
        RenderContact();
        matchOnline();
    }

    private void RenderContact() {
        new Thread() {
            public void run() {
                LoadContact();
            }
        }.start();
    }

    /**
     * 匹配在線人員
     */
    private void matchOnline() {
        showDialog(R.string.GettingData);
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
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);
        WebServiceManager webServiceManager = new WebServiceManager(
                SelectContact.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getonlineuser);
            myHandler.sendEmptyMessage(MSG_GET_ONLINE_FAIL);
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
                myHandler.sendMessage(msg);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myHandler.sendEmptyMessage(MSG_GET_ONLINE_FAIL);
                break;
        }
    }

    // 获取用户列表
    private void LoadContact() {
        String searchkey = edtSearch.getText().toString().trim();

        ArrayList<MessageContact> contacts = new ArrayList<MessageContact>();
        DBHelper dbHelper = new DBHelper(SelectContact.this);
        String[] columns = {DBHelper.STAFF_ID, DBHelper.STAFF_NAME};
        Cursor cursor = null;

        String stationCode = "";
        if (Property.OwnStation != null)
            stationCode = Property.OwnStation.Code;
        try {
            cursor = dbHelper.exeSql(
                    DBHelper.STAFF_TABLE_NAME,
                    columns,
                    DBHelper.STAFF_TYPE + " = " + Staff.TYPE_STAFF + " and "
                            + DBHelper.STAFF_NAME + " like '%"
                            + searchkey.trim() + "%' and HomeAddress = '"
                            + stationCode + "'", null, null, null,

                    DBHelper.STAFF_CODE);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MessageContact contact = new MessageContact();
                    contact.contactId = (long) cursor.getInt(cursor
                            .getColumnIndex(DBHelper.STAFF_ID));
                    contact.contactName = cursor.getString(cursor
                            .getColumnIndex(DBHelper.STAFF_NAME));

                    if (contact.contactId != Property.StaffId)
                        contacts.add(contact);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbHelper.closeCursor(cursor);
            dbHelper.close();
        }

        Message msg = new Message();
        msg.what = MSG_GET_CONTACT;
        msg.obj = contacts;
        myHandler.sendMessage(msg);
    }

    ;

    // adapter
    class SelectContactAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private Context context;

        public SelectContactAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
        }

        public int getCount() {
            if (mContacts != null)
                return mContacts.size();
            else
                return 0;
        }

        public Object getItem(int position) {
            if (mContacts != null)
                return mContacts.get(position);
            else
                return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.msg_contact_item,
                        null);
                holder = new ViewHolder();

                holder.txtName = (TextView) convertView
                        .findViewById(R.id.txtName);
                holder.txtStatus = (TextView) convertView
                        .findViewById(R.id.txtStatus);
                holder.chkSelect = (CheckBox) convertView
                        .findViewById(R.id.chkSelect);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v
                            .findViewById(R.id.chkSelect);

                    checkBox.setChecked(!checkBox.isChecked());
                    if (checkBox.isChecked()) {
                        if (!selectedMap.containsKey(mContacts.get(position).contactId)) {
                            selectedMap.put(mContacts.get(position).contactId,
                                    mContacts.get(position));
                        }
                    } else {
                        if (selectedMap.containsKey(mContacts.get(position).contactId)) {
                            selectedMap.remove(mContacts.get(position).contactId);
                        }
                    }

                    isAll();
                }
            });

            holder.chkSelect.setChecked(selectedMap.containsKey(mContacts
                    .get(position).contactId));
            holder.chkSelect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        if (!selectedMap.containsKey(mContacts.get(position).contactId)) {
                            selectedMap.put(mContacts.get(position).contactId,
                                    mContacts.get(position));
                            LogUtil.i("map add  --> "
                                    + mContacts.get(position).contactId);
                        }
                    } else {
                        if (selectedMap.containsKey(mContacts.get(position).contactId)) {
                            selectedMap.remove(mContacts.get(position).contactId);
                            LogUtil.i("map remove  --> "
                                    + mContacts.get(position).contactId);
                        }
                    }

                    isAll();
                }
            });

            if (mContacts.get(position).BOnline) {
                holder.txtStatus.setText(R.string.user_online);
                holder.txtStatus.setTextColor(context.getResources().getColor(
                        R.color.green));
            } else {
                holder.txtStatus.setText(R.string.user_offline);
                holder.txtStatus.setTextColor(context.getResources().getColor(
                        R.color.gray));
            }

            holder.txtName.setText(mContacts.get(position).contactName);

            return convertView;
        }

        private void isAll() {
            boolean isAll = selectedMap.size() == mContacts.size();
            mCheckBox.setChecked(isAll);
        }
    }

    private static class ViewHolder {
        TextView txtName;
        TextView txtStatus;
        CheckBox chkSelect;
    }
}
