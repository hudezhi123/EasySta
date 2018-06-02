package easyway.Mobile.Broadcast;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

public class ViewPlayRecord extends ActivityEx {
    private String tag = "SpecSubjectList";
    private Handler handel = new Handler();
    private ProgressDialog progDialog;
    private BroadcastInfo broadcastInfo = new BroadcastInfo();
    private long id = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_spec_subject_view);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.broad_spec_subject);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getLong("id");

        progDialog = ProgressDialog.show(ViewPlayRecord.this,
                getString(R.string.Waiting), getString(R.string.GettingData), true,
                false);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(true);
        progDialog.setIcon(R.drawable.waiting);

        Thread thread = new Thread(null, getAllBackground, "getAllPlayRecords");
        thread.start();
    }

    private Runnable getAllBackground = new Runnable() {

        public void run() {
            try {
                broadcastInfo = GetItem();
                handel.post(mUpdateResults);
            } catch (Exception ex) {
                Log.d(tag, ex.getMessage());
                handel.post(mUpdateError);
            }
        }

    };

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            if (progDialog != null) {
                progDialog.dismiss();
            }
            EditText txBroadContent = (EditText) findViewById(R.id.txBroadContent);
            txBroadContent.setText(broadcastInfo.Content);
        }
    };

    final Runnable mUpdateError = new Runnable() {
        public void run() {
            if (progDialog != null) {
                progDialog.dismiss();
            }

            showErrMsg(errMsg);
        }
    };

    private BroadcastInfo GetItem() throws Exception {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("BCid", String.valueOf(id));
        String methodPath = "IStationService.asmx";
        String methodName = "GetBroadcastContentByID";
        WebServiceManager webServiceManager = new WebServiceManager(this,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        
        errMsg = JsonUtil.GetJsonString(result, "Msg");
        if (!errMsg.equals("")) {
            throw new Exception(errMsg);
        }
        errMsg = JsonUtil.GetJsonString(result, "Msg");
        if (!errMsg.equals("")) {
            throw new Exception(errMsg);
        }
        JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Table");
        if (jsonArray.length() == 0) {
            throw new Exception(getString(R.string.NonRecord));
        }
        JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
        BroadcastInfo item = new BroadcastInfo();
        item.Content = JsonUtil.GetJsonObjStringValue(
                jsonObj, "BroadcastContent");
        return item;
    }
}
