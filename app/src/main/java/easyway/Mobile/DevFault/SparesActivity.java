package easyway.Mobile.DevFault;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.DspFaultReport;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SparesActivity extends ActivityEx {
    private final int MSG_GET_DATA_SUCCEED = 1;
    private final int MSG_GET_DATA_FAIL = 2;
    private final int MSG_GET_DATA_NO = 3;
    private ListView lv_spares;
    private DspAdapter dspAdapter;
    private ArrayList<DspFaultReport> list = new ArrayList<DspFaultReport>();
    private int dfId;
    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();

            switch (msg.what) {
                case MSG_GET_DATA_SUCCEED:

                    dspAdapter = new DspAdapter(SparesActivity.this,
                            (ArrayList<DspFaultReport>) msg.obj);
                    lv_spares.setAdapter(dspAdapter);

                    break;
                case MSG_GET_DATA_FAIL:
                    showToast(errMsg);
                    break;
                case MSG_GET_DATA_NO:
                    showToast("暂时无备品");
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
        setContentView(R.layout.activity_spares);
        dfId = getIntent().getIntExtra("dfId", -1);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("在库的备品备件");
        Button btnset = (Button) findViewById(R.id.btnset);
        btnset.setVisibility(ViewGroup.VISIBLE);
        btnset.setText("确定");
        initview();
        showProgressDialog("数据加载中");
        new Thread(new Runnable() {

            @Override
            public void run() {

                getSpareData();

            }
        }).start();
        btnset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = "";
                if (dspAdapter != null) {
                    ArrayList<DspFaultReport> list = dspAdapter.getDataList();
                    if (list != null && list.size() > 0) {

                        for (int i = 0; i < dspAdapter.getDataList().size(); i++) {
                            if (i < dspAdapter.getDataList().size() - 1) {
                                str = str + dspAdapter.getDataList().get(i).DspId + ",";
                            } else {
                                str = str + String.valueOf(dspAdapter.getDataList()
                                        .get(i).DspId);
                            }
                        }
                    }
                }
//				DRHandlerActivity.instance.finish();
//				DFList.instance.finish();
                Intent intent = new Intent(SparesActivity.this, DFList.class);
                intent.putExtra("shebei", true);
                intent.putExtra("dfId", dfId);
                intent.putExtra("dspIds", str);
                startActivity(intent);
//                finish();

            }
        });

    }

    private void initview() {
        lv_spares = (ListView) findViewById(R.id.lv_spares);

    }

    protected void getSpareData() {

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);

        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_Dsp_FAULT;
        WebServiceManager webServiceManager = new WebServiceManager(
                SparesActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
            return;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    DspFaultReport dspFaultReport = new DspFaultReport();
                    dspFaultReport.DspId = JsonUtil.GetJsonObjLongValue(jsonObj,
                            "DspId");
                    dspFaultReport.DevCode = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "DevCode");
                    dspFaultReport.DevName = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "DevName");
                    dspFaultReport.DevCate = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "DevCate");
                    list.add(dspFaultReport);
                }
                if (list.size() > 0) {
                    Message msg = new Message();
                    msg.obj = list;
                    msg.what = MSG_GET_DATA_SUCCEED;
                    myhandle.sendMessage(msg);
                } else {
                    myhandle.sendEmptyMessage(MSG_GET_DATA_NO);
                }

                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
                break;
        }

    }
}
