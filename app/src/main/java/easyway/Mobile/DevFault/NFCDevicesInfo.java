package easyway.Mobile.DevFault;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.DevFaultReport;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.bean.DevInfoResult;
import easyway.Mobile.util.JsonUtil;

public class NFCDevicesInfo extends ActivityEx {
    private ListView devicesInfoList;
    private long devid;
    private boolean isgroup;
    private String devname, devLocation;
    private String devCate;
    private String devkeeper;
    private String devcode;
    private String TwName;
    private DevInfoResult devInfoResult = null;
    private ArrayList<DevFaultReport> devicesInfo = new ArrayList<DevFaultReport>();

    private final int MSG_PARTOLDEVICES_SUCCESS = 100;
    private final int MSG_PARTOLDEVICES_FINAL = 101;
    private final int MSG_GET_DATA_FAIL = 102;
    private final int MSG_GETGROUPDEVICES_SUCCESS = 103;
    private final int MSG_GETGROUPDEVICES_FALSE = 104;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_GET_DATA_FAIL:
                    showToast("获取数据失败！");
                    closeProgressDialog();
                    break;
                case MSG_PARTOLDEVICES_SUCCESS:
                    ShowDialog(true);
                    closeProgressDialog();
                    break;
                case MSG_PARTOLDEVICES_FINAL:
                    ShowDialog(false);
                    closeProgressDialog();
                    break;
                case MSG_GETGROUPDEVICES_SUCCESS:
                    DevicesInfoListAdapter mAdapter = new DevicesInfoListAdapter(
                            getApplicationContext(), devicesInfo);
                    devicesInfoList.setAdapter(mAdapter);
                    closeProgressDialog();
                    break;
                case MSG_GETGROUPDEVICES_FALSE:
                    ShowDialog(false);
                    closeProgressDialog();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_get_deviceinfo);
        devicesInfoList = (ListView) findViewById(R.id.deviceinfo_list);
        TextView titleString = (TextView) findViewById(R.id.title);
        titleString.setText(R.string.title_devicescheck);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        devInfoResult = (DevInfoResult) bundle.getSerializable("DevInfoResult");
        devid = devInfoResult.getDevId();
        devcode = devInfoResult.getDevCode();
        devname = devInfoResult.getDevName();
        devCate = devInfoResult.getDevCate();
        devLocation = devInfoResult.getDevLocation();
        isgroup = devInfoResult.isIsGroup();
        TwName = devInfoResult.getTwName();
        devkeeper = devInfoResult.getDevKeeper();
        isPartolDevices();
    }

    private void isPartolDevices() {
//        if (Constant.isPartolDevices) {
        showProgressDialog("正在上报巡检");
        new Thread() {
            public void run() {
                PartolDevices(devid);
            }
        }.start();

//        } else {
//            errMsg = "没有巡检权限";
//            ShowDialog(false);
//        }

    }


    private void PartolDevices(Long devid) {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("devId", String.valueOf(devid));
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_PARTOL_DEVICES;
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdata);
            mHandler.sendEmptyMessage(MSG_GET_DATA_FAIL);
            return;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case 0:
                mHandler.sendEmptyMessage(MSG_PARTOLDEVICES_SUCCESS);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                mHandler.sendEmptyMessage(MSG_PARTOLDEVICES_FINAL);
                break;
        }

    }

    private void GetGroupDevices(Long devid) {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("DevId", devid.toString());
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_GROUP_DEV_FAULT;
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdata);
            mHandler.sendEmptyMessage(MSG_GET_DATA_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                devicesInfo.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    DevFaultReport devFault = new DevFaultReport();
                    devFault.Id = JsonUtil.GetJsonObjLongValue(jsonObj, "id");
                    devFault.DevName = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "DevName");
                    devFault.Location = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "Location");
                    devFault.IsGroup = JsonUtil.GetJsonObjBooleanValue(jsonObj,
                            "IsGroup");
                    devFault.TwName = JsonUtil.GetJsonObjStringValue(jsonObj, "TwName");
                    devicesInfo.add(devFault);
                }

                // totalItems = JsonUtil.GetJsonLong(result, "total");

                Message msg = new Message();
                msg.obj = devicesInfo;
                msg.what = MSG_GETGROUPDEVICES_SUCCESS;
                mHandler.sendMessage(msg);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                mHandler.sendEmptyMessage(MSG_GETGROUPDEVICES_FALSE);
                break;
        }
    }

    private void ShowDialog(final boolean bSucc) {
        final AlertDialog.Builder builder = new Builder(NFCDevicesInfo.this);
        if (!bSucc)
            builder.setIcon(R.drawable.error);
        else
            builder.setIcon(R.drawable.ok);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(false);

        if (bSucc)
            builder.setMessage(R.string.dev_check_success);
        else
            builder.setMessage(errMsg);

        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // finish();
                        builder.create().hide();
                        // new Thread() {
                        // public void run() {
                        if (errMsg.equals("设备不存在")) {
                            finish();
                        } else {
                            DevFaultReport devFault = new DevFaultReport();
                            devFault.Id = devid;
                            devFault.IsGroup = isgroup;
                            devFault.DevName = devname;
                            devFault.Location = devLocation;
                            devFault.TwName = TwName;
                            devicesInfo.add(devFault);
                            DevicesInfoListAdapter mAdapter = new DevicesInfoListAdapter(
                                    getApplicationContext(), devicesInfo);
                            devicesInfoList.setAdapter(mAdapter);
                        }

                    }
                });

        builder.create().show();
    }

    class DevicesInfoListAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<DevFaultReport> listData = new ArrayList<DevFaultReport>();

        public DevicesInfoListAdapter(Context context,
                                      ArrayList<DevFaultReport> listData) {
            this.mContext = context;
            this.listData = listData;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (listData.size() > 0)
                return listData.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            if (listData.size() > 0)
                return listData.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub
            DevFaultReport faultBean = listData.get(position);
            ViewHolder holder = null;
            if (null == convertView) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.nfc_get_deviceinfo_item, null);
                holder.devNameText = (TextView) convertView
                        .findViewById(R.id.devName);
                holder.devFaultReport = (Button) convertView
                        .findViewById(R.id.devfault_report);
                holder.isGroupText = (TextView) convertView
                        .findViewById(R.id.isgroup);
                holder.devaddress = (TextView) convertView
                        .findViewById(R.id.devaddress);
                holder.TwName = (TextView) convertView.
                        findViewById(R.id.TwName);
                holder.groupClick = (LinearLayout) convertView
                        .findViewById(R.id.group_click);
                holder.iv_next = (ImageView) convertView
                        .findViewById(R.id.iv_next);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = faultBean.DevName;
            holder.devNameText.setText(name);
            holder.devaddress.setText(faultBean.Location);
            holder.TwName.setText(faultBean.TwName);
            final boolean group = faultBean.IsGroup;
            if (group == true) {
                holder.isGroupText.setText("是");
                holder.iv_next.setVisibility(ViewGroup.VISIBLE);
                holder.groupClick.setEnabled(true);
            } else {
                holder.isGroupText.setText("否");
                holder.iv_next.setVisibility(ViewGroup.INVISIBLE);
                holder.groupClick.setEnabled(false);
            }
            holder.groupClick.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (group) {
                        showProgressDialog("正在获取设备分组信息");
                        new Thread() {
                            public void run() {
                                GetGroupDevices(devid);
                            }
                        }.start();
                    }

                }
            });
            holder.devFaultReport.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(NFCDevicesInfo.this, DFReport.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DevInfoResult", devInfoResult);
                    bundle.putString("Flag", "NFCDevicesInfo");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }

    private class ViewHolder {
        LinearLayout groupClick;
        TextView devNameText, devaddress;
        TextView isGroupText;
        TextView TwName;
        Button devFaultReport;
        ImageView iv_next;
    }

}
