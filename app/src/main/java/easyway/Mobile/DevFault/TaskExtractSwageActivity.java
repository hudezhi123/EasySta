package easyway.Mobile.DevFault;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Data.SewageWaterResult;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Task.SewageWaterAdapter;
import easyway.Mobile.util.JsonUtil;

public class TaskExtractSwageActivity extends ActivityEx {


    private RecyclerView recyclerView;
    private List<SewageWaterResult.DataBean> dataBeanList;
    private SewageWaterAdapter adapter;

    private static final int GET_DATA_SUCCEED = 1;
    public static final int CODE_NORMAL = 1000;
    public static final int CODE_ERROR = 911;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA_SUCCEED:
                    dataBeanList = (List<SewageWaterResult.DataBean>) msg.obj;
                    recyclerView.setAdapter(adapter);
                    adapter.setData(dataBeanList);
                    break;
                case CODE_NORMAL:
                    int position = msg.arg1;
                    SewageWaterResult.DataBean dataBean = (SewageWaterResult.DataBean) msg.obj;
                    adapter.updateItem(position, dataBean);
                    break;
                case CODE_ERROR:
                    showToast((CharSequence) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_extract_swage);
        initView();
        getSewageList();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.Sewage_Water);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_task_sewage_water);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SewageWaterAdapter(this,handler);
    }

    private void getSewageList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }).start();
    }

    private void getData() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        parmValues.put("planDate", dateFormat.format(new Date()));
        parmValues.put("stationCode", Property.StationCode);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.SEWAGEWATER_METHOD;
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            // TODO: 2017/5/21
            return;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case 1000:
                SewageWaterResult sewageResult = new Gson().fromJson(result, SewageWaterResult.class);
                if (sewageResult != null && sewageResult.getData() != null && sewageResult.getData().size() > 0) {
                    List<SewageWaterResult.DataBean> tempList = sewageResult.getData();
                    for (int i = 0; i < tempList.size(); i++) {
                        SewageWaterResult.DataBean dataBean = tempList.get(i);
                        if (!TextUtils.isEmpty(dataBean.getTRNO_PRO())) {
                            dataBean.setAllowed(true);
                            dataBean.setSubmit(false);
                        } else {
                            dataBean.setAllowed(false);
                            dataBean.setSubmit(false);
                        }
                    }
                    // TODO: 2017/5/21
                    Message message = handler.obtainMessage();
                    message.what = GET_DATA_SUCCEED;
                    message.obj = tempList;
                    handler.sendMessage(message);
                }
                break;
            case 911:
                break;
        }
    }
}
