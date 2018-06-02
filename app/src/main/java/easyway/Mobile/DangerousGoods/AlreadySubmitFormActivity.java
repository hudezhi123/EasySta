package easyway.Mobile.DangerousGoods;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.JsonUtil;

public class AlreadySubmitFormActivity extends ActivityEx {

    private AlreadyDoneAdapter adapter;
    private List<DangerousObjectResult> taskList;
    private ListView listView;

    private static final int DATA_LOAD_COMPLETE = 1;
    private static final int DATA_LOAD_EMPTY = 0;
    private static final int DATA_LOAD_EXCEPTION = -1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DATA_LOAD_COMPLETE:
                    taskList = (List<DangerousObjectResult>) msg.obj;
                    adapter.setData(taskList);
                    break;
                case DATA_LOAD_EMPTY:
                    showToast("获取数据失败！");
                    break;
                case DATA_LOAD_EXCEPTION:
                    showToast((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_submit_form);
        initView();
        getServerDate();
    }

    private void initView() {
        initTitleBar();
        listView = (ListView) findViewById(R.id.listView_already_submit_list);
        adapter = new AlreadyDoneAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AlreadySubmitFormActivity.this, DangerousFormActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Flags.TO_DANGER_FLAG, Flags.Flag_FROM_DANGERLIST_TO_DANGER);
                bundle.putSerializable(Flags.DANGER_NAME, adapter.getItem(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initTitleBar() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.DangerousModel);
    }

    /**
     * 从本地获取缓存的数据
     */
    private void getServerDate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("keyword", "");
                parmValues.put("beginDttm", "");
                parmValues.put("endDttm", "");
                parmValues.put("stationCode", Property.StationCode);
                String methodPath = Constant.MP_TASK;
                String methodName = Constant.DANGEROUS_FIND;
                WebServiceManager webServiceManager = new WebServiceManager(
                        AlreadySubmitFormActivity.this, methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                if (!TextUtils.isEmpty(result)) {
                    boolean msgType = JsonUtil.GetJsonBoolean(result, "MsgType");
                    Message message = mHandler.obtainMessage();
                    if (msgType) {
                        message.what = DATA_LOAD_COMPLETE;
                        JSONArray dataList = JsonUtil.GetJsonArray(result, "Data");
                        List<DangerousObjectResult> dangerousObjectResults = new ArrayList<DangerousObjectResult>();
                        for (int i = 0; i < dataList.length(); i++) {
                            DangerousObjectResult dangerousObjectResult = new Gson().fromJson(dataList.optJSONObject(i).toString(), DangerousObjectResult.class);
                            dangerousObjectResults.add(dangerousObjectResult);
                        }
                        message.obj = dangerousObjectResults;
                        mHandler.sendMessage(message);
                    } else {
                        message.what = DATA_LOAD_EXCEPTION;
                        message.obj = JsonUtil.GetJsonString(result, "Msg");
                        mHandler.sendMessage(message);
                    }
                } else {
                    mHandler.sendEmptyMessage(DATA_LOAD_EMPTY);
                }
            }
        }).start();
    }


}
