package easyway.Mobile.Broadcast;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class SpecSubjectList extends ActivityEx {
    // private static final String TAG = "SpecSubjectList";
    private ArrayList<BroadcastInfo> listBroadcastInfo = new ArrayList<BroadcastInfo>();

    private PullRefreshListView mListView;
    private SpecSubjectList_Adapter mAdapter;
    private boolean isPullRefresh = false;
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case 0:
                	if (errMsg==null) errMsg="Error";
                	if (errMsg.equals("")) errMsg="Error";
                    showToast(errMsg);
                    break;
                case 1:
                    if (isPullRefresh) {
						isPullRefresh = false;
                        mListView.onRefreshComplete();
                    }
                    mAdapter.setData(listBroadcastInfo);
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
        setContentView(R.layout.broadcast_spec_subject_list);
        initView();
        showProgressDialog(R.string.GettingData);
        new Thread(new ServerDataThread()).start();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_Special);
        mListView = (PullRefreshListView) findViewById(R.id.gvList);
        mAdapter = new SpecSubjectList_Adapter(this, listBroadcastInfo);
        mListView.setAdapter(mAdapter);
        mListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullRefresh = true;
                new Thread(new ServerDataThread()).start();

            }
        });
    }

    private class ServerDataThread implements Runnable {
        @Override
        public void run() {
            listBroadcastInfo = GetList();
            if (null == listBroadcastInfo) {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = listBroadcastInfo;
                mHandler.sendMessage(msg);
            }

        }
    }

    private ArrayList<BroadcastInfo> GetList() {
        ArrayList<BroadcastInfo> list = new ArrayList<BroadcastInfo>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("UserName", Property.UserName);
        String methodPath = "IStationService.asmx";
        String methodName = "GetPlayRecords";
        try {
            WebServiceManager webServiceManager = new WebServiceManager(this,
                    methodName, parmValues);
            String result = webServiceManager.OpenConnect(methodPath);
            errMsg = JsonUtil.GetJsonString(result, "Msg");
            if (!errMsg.equals("")) {
                return null;
            }
            JSONArray jsonArray = JsonUtil.GetJsonArray(result,
                    "Table");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                BroadcastInfo item = new BroadcastInfo();
                item.id = JsonUtil.GetJsonObjLongValue(jsonObj, "id");
                item.OpStatus = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "OpStatus");
                item.PlayTime = JsonUtil.GetJsonObjStringValue(jsonObj,
                        "PlayTime");
                item.Category = JsonUtil
                        .GetJsonObjStringValue(jsonObj, "BroadcastCategory");
                item.Title = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "BroadcastTitle");
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
