package easyway.Mobile.LiveCase;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
 * 情况上报列表
 */
public class LiveCaseList extends ActivityEx {
    private long startIndex = 0, totalItems = 0;
    private final int limit = 10;
    private ArrayList<LiveCaseReport> mLClist = new ArrayList<LiveCaseReport>();
    private PullRefreshListView gv_FM_List;
    private boolean isPullRefresh = false;
    private LiveCaseListAdapter mAdapter;
    private Button btnShowMore;
    private boolean addedFoot = false;
    
    private final int MSG_GETDATA_FAIL = 0;			// 获取数据失败
    private final int MSG_GETDATA_SUCCEED = 1;		// 获取数据成功
    
    @SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MSG_GETDATA_FAIL:		// 获取数据失败
                    showToast(errMsg);
                    if (isPullRefresh) {
						isPullRefresh = false;
                        gv_FM_List.onRefreshComplete();
                    }
                    break;
                case MSG_GETDATA_SUCCEED:		// 获取数据成功
                    if (isPullRefresh) {
						isPullRefresh = false;
                        gv_FM_List.onRefreshComplete();
                    }
                    
                    if (mLClist == null) {
                    	mLClist = new ArrayList<LiveCaseReport>();
                    }
                    	
                	if (startIndex == 0) {
                		mLClist.clear();
                	}
                	
                	mLClist.addAll((ArrayList<LiveCaseReport>) msg.obj);
                    mAdapter.setData(mLClist);
                    mAdapter.notifyDataSetChanged();
                    showHideFoot();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livecase_list);
        initView();
        startIndex = 0;
        getData();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_livecase);
        
        gv_FM_List = (PullRefreshListView) findViewById(R.id.gvFMList);
        mAdapter = new LiveCaseListAdapter(this, mLClist);
        gv_FM_List.setAdapter(mAdapter);

        gv_FM_List.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                isPullRefresh = true;
                startIndex = 0;
                getData();
            }
        });
        
        // 显示更多按钮
        btnShowMore=new Button(this);
        btnShowMore.setText(R.string.search_show_more);
        btnShowMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mLClist == null) {
					startIndex = 0;
				} else {
					startIndex = mLClist.size();
				}
				
				getData();
			}
		});
    }

    // 获取现场情况上报列表
    private void GetLiveCaseReport() {
        ArrayList<LiveCaseReport> Fieldlist = new ArrayList<LiveCaseReport>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("limit", String.valueOf(limit));
        parmValues.put("start", String.valueOf(startIndex));
        parmValues.put("keyword", "");

        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_LIVECASE_REPORT;
        WebServiceManager webServiceManager = new WebServiceManager(
                LiveCaseList.this, methodName, parmValues);
        
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
        	errMsg = getString(R.string.exp_getdata);
        	myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				long id = JsonUtil.GetJsonObjLongValue(jsonObj, "id");

				LiveCaseReport lcReprot = new LiveCaseReport();

				lcReprot.id = id;
				lcReprot.ReportedType = JsonUtil
						.GetJsonObjStringValue(jsonObj, "ReportedType");
				lcReprot.ReportedLevel = JsonUtil
						.GetJsonObjStringValue(jsonObj, "ReportedLevel");
				lcReprot.ReportedTime = JsonUtil
						.GetJsonObjDateValue(jsonObj, "ReportedTime");
				lcReprot.Reporteder = JsonUtil
						.GetJsonObjStringValue(jsonObj, "Reporteder");
				lcReprot.AppendixUrl = JsonUtil
						.GetJsonObjStringValue(jsonObj, "AppendixUrl");
				lcReprot.Remarks = JsonUtil
						.GetJsonObjStringValue(jsonObj, "Remarks");
				lcReprot.ConfirmStatus = JsonUtil
						.GetJsonObjStringValue(jsonObj, "ConfirmStatus");
				Fieldlist.add(lcReprot);
			}

			totalItems = JsonUtil.GetJsonLong(result, "total");

			Message message = new Message();
			message.what = MSG_GETDATA_SUCCEED;
			message.obj = Fieldlist;
			myhandle.sendMessage(message);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			break;
		}
    }

    // 设置是否显示 “显示更多”按钮
    private void showHideFoot() {
    	if (mLClist.size() == 0) {
    		if (addedFoot) {
    			addedFoot = false;
    			gv_FM_List.removeFooterView(btnShowMore);
    		}
    		return;
    	}
    	
    	if (mLClist.size() >= totalItems) {
    		if (addedFoot) {
    			gv_FM_List.removeFooterView(btnShowMore);
    			addedFoot = false;
    		}
    	} else {
    		if (!addedFoot) {
    			addedFoot=true;
    			gv_FM_List.addFooterView(btnShowMore);
    		}    		
    	}
    }
    
    // 创建线程从平台获取数据
    public void getData() {
        showProgressDialog(R.string.GettingData);
        
    	new Thread() {
    		public void run() {
    			GetLiveCaseReport();
    		}
    	}.start();
    }
}
