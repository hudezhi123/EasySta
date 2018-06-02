package easyway.Mobile.Broadcast;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TB_PA_PlayRecords;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

/*
 * 广播列表
 */
public class ManualBroad extends ActivityEx {
    private static final String TAG = "BoardAddM";

    private ArrayList<TB_PA_PlayRecords> listPlayRecords = new ArrayList<TB_PA_PlayRecords>();
    private int startIndex = 0;
    private int limit = 10;
//    private int totalItems = 0;

    private PullRefreshListView mListView;
    private ManualBroadAdapter manualBoardAdapter;
    private boolean isPullRefresh = false;

    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case 0:
                    showToast("Error");
                    break;
                case 1:
                    if (isPullRefresh) {
						isPullRefresh = false;
                        mListView.onRefreshComplete();
                    }
                    manualBoardAdapter.setData(listPlayRecords);
                    manualBoardAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_service_manual_board);

        initView();
        showProgressDialog(R.string.GettingData);
        new Thread(new ServerDataThread()).start();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.broad_manual);

        mListView = (PullRefreshListView) findViewById(R.id.gvList);
        manualBoardAdapter = new ManualBroadAdapter(this, listPlayRecords);
        mListView.setAdapter(manualBoardAdapter);
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
            listPlayRecords = GetAllPlayRecord(startIndex, limit);
            if (null == listPlayRecords) {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = listPlayRecords;
                mHandler.sendMessage(msg);
            }

        }
    }

    @Override
    public void onDestroy() {
        if (manualBoardAdapter != null) {
            manualBoardAdapter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            if (manualBoardAdapter != null) {
                manualBoardAdapter.onDestroy();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private ArrayList<TB_PA_PlayRecords> GetAllPlayRecord(int start, int limit) {
        ArrayList<TB_PA_PlayRecords> list = new ArrayList<TB_PA_PlayRecords>();

        try {
            HashMap<String, String> parmValues = new HashMap<String, String>();
            parmValues.put("sessionId", Property.SessionId);
            parmValues.put("limit", String.valueOf(limit));
            parmValues.put("start", String.valueOf(start));
            String methodPath = "WebService/Broadcast.asmx";
            String methodName = "GetPlayRecords";
            WebServiceManager webServiceManager = new WebServiceManager(
                    getApplicationContext(), methodName, parmValues);
            String result = webServiceManager.OpenConnect(methodPath);
            String errMsg = JsonUtil.GetJsonString(result, "Msg");
            if (!errMsg.equals("")) {
                // throw new Exception(errMsg);
                return null;
            }
            JSONArray jsonArray = JsonUtil
                    .GetJsonArray(result, "Data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                int id = (int) JsonUtil.GetJsonObjLongValue(jsonObj,
                        "id");

                TB_PA_PlayRecords item = new TB_PA_PlayRecords();
                item.id = id;
                item.BroadcastID = (int) JsonUtil.GetJsonObjLongValue(
                        jsonObj, "BroadcastID");
                item.OpStatus = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "OpStatus");
                item.PlayTime = JsonUtil.GetJsonObjStringValue(jsonObj,
                        "PlayTime");
                item.Operator = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "Operator");
                item.OperatingArea = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "OperatingArea");
                list.add(item);
            }
            
//            try {
//                totalItems = (int) webServiceManager.total;
//            } catch (Exception ex) {
//                totalItems = list.size();
//            }

            return list;
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            return list;
        }
    }
}
