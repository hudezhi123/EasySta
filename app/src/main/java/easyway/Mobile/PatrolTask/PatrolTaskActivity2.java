package easyway.Mobile.PatrolTask;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcF;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Data.ResultCode;
import easyway.Mobile.DevFault.DFReport;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.bean.DevInfoResult;
import easyway.Mobile.util.ByteUtil;
import easyway.Mobile.util.ResponseResultUtils;
import easyway.Mobile.util.VibratorUtil;

public class PatrolTaskActivity2 extends ActivityEx implements View.OnClickListener {

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] IntentFilterArray;
    private TextView textContentTips;
    private Button btnSet;
    private Button reportToRepair;
    private final int FLAG_DEVICE = 2; // 设备
    private final int FLAG_WORKSAPCE = 1; // 任务区域
    private final int FLAG_PATROL = 3; // 巡检
    private String[][] techListArray;
    private DevInfoResult mDevInfo;
    private boolean isFault = false;
    private boolean isCheckFinish = false;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ResultCode.CODE_IS_911:
                    textContentTips.setText(R.string.exp_nfc_task);
                    break;
                case ResultCode.CODE_IS_1000:

                    break;
                case ResultCode.DATA_IS_STRING:
                    if (isCheckFinish) {

                    } else {
                        isCheckFinish = true;
                        if (Boolean.parseBoolean(msg.obj + "")) {
                            if (mDevInfo.getFlag() == FLAG_DEVICE) {
                                textContentTips.setText(""
                                        + "设备名称：" + mDevInfo.getDevName() + "\n"
                                        + "任务区域：" + mDevInfo.getTwName() + "\n"
                                        + "设备位置：" + mDevInfo.getDevLocation() + "\n"
                                );
                            } else if (mDevInfo.getFlag() == FLAG_WORKSAPCE) {
                                textContentTips.setText(""
                                        + "区域ID：" + mDevInfo.getDevId() + "\n"
                                        + "巡检区域：" + mDevInfo.getDevName() + "\n"
                                );
                            }
                            uploadData();
                        } else {
                            mDevInfo = new DevInfoResult();
                            textContentTips.setText(R.string.exp_nfc_task);
                        }
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_task);
        prepareNFCEnvironment();
        init();
    }

    /**
     * 准备NFC前台分派参数
     */
    private void prepareNFCEnvironment() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        int requestCode = 0;
        int flags = 0;
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcPendingIntent = PendingIntent.getActivity(this, requestCode, nfcIntent, flags);
        IntentFilter ndefIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        ndefIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            ndefIntentFilter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        IntentFilter techIntentFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        techIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        IntentFilterArray = new IntentFilter[]{ndefIntentFilter, tagIntentFilter, techIntentFilter};
        techListArray = new String[][]{
                new String[]{
                        NfcF.class.getName()
                }
        };
    }

    //等待扫描界面
    private void wait2Scan(boolean flag) {
        if (flag) {
            textContentTips.setVisibility(View.VISIBLE);
        } else {
            textContentTips.setVisibility(View.GONE);
        }

    }

    private void init() {
        initTitleBar();
        initView();
        wait2Scan(true);
    }

    private void initTitleBar() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("巡检任务");
        btnSet = (Button) findViewById(R.id.btnset);
        btnSet.setVisibility(View.VISIBLE);
        btnSet.setText("未巡检");
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatrolTaskActivity2.this, UndoPatrolTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        textContentTips = (TextView) findViewById(R.id.text_content_patrol_task);
        reportToRepair = (Button) findViewById(R.id.btn_report2repair);
        reportToRepair.setOnClickListener(this);
    }

    private void checkIsWait2Task() {  //
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> paramValue = new HashMap<>();
                paramValue.put("sessionId", Property.SessionId);
                paramValue.put("objectId", mDevInfo.getDevId() + "");
                paramValue.put("objectName", mDevInfo.getDevName());
                paramValue.put("stationCode", Property.StationCode);
                String methodName = Constant.PATROL_IS_TASK;
                String methodPath = Constant.MP_PATROL;
                WebServiceManager service = new WebServiceManager(PatrolTaskActivity2.this, methodName, paramValue);
                String jsonResult = service.OpenConnect(methodPath);
                Message msg = myHandler.obtainMessage();
                ResponseResultUtils.parseJsonResult(jsonResult, msg);
                myHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 上传无问题的设备数据
     */
    private void uploadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mDevInfo.getFlag() == FLAG_DEVICE) {
                    up2DeviceTask();
                } else if (mDevInfo.getFlag() == FLAG_WORKSAPCE) {
                    up2AreaTask();
                }
            }
        }).start();
    }

    /**
     * 上传到设备巡检
     */
    private void up2DeviceTask() {
        HashMap<String, String> paramValue = new HashMap<>();
        paramValue.put("sessionId", Property.SessionId);
        paramValue.put("objectId", mDevInfo.getDevId() + "");
        paramValue.put("objectName", mDevInfo.getDevName());
        paramValue.put("stationCode", Property.StationCode);
        String methodName = Constant.PATROL_SUBMIT_TASK;
        String methodPath = Constant.MP_PATROL;
        WebServiceManager service = new WebServiceManager(PatrolTaskActivity2.this, methodName, paramValue);
        String jsonResult = service.OpenConnect(methodPath);
        Message msg = myHandler.obtainMessage();
        ResponseResultUtils.parseJsonResult(jsonResult, msg);
        myHandler.sendMessage(msg);
    }

    /**
     * 上传到区域巡检
     */
    private void up2AreaTask() {
        up2DeviceTask();
    }

    /**
     * 跳转到报障页面
     */
    private void turn2Report() {
        Intent intent = new Intent(PatrolTaskActivity2.this, DFReport.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DevInfoResult", mDevInfo);
        bundle.putString("Flag", "PatrolTaskActivity2");
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(
                this,
                nfcPendingIntent,    //用于打包Tag Intent的Intent
                IntentFilterArray,      //用于声明想要拦截的Intent的Intent Filter数组
                techListArray           // 想要处理的标签技术
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        String content = "";
        switch (action) {
            case NfcAdapter.ACTION_TAG_DISCOVERED:
                content = parseTag(intent);
                break;
            case NfcAdapter.ACTION_NDEF_DISCOVERED:
                content = parseNdef(intent);
                break;
            case NfcAdapter.ACTION_TECH_DISCOVERED:
                content = parseTech(intent);
                break;
        }
        if (ParseContent(content)) {
            checkIsWait2Task();
        } else {
            textContentTips.setText(R.string.exp_nfc_task);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    /**
     * 解析 Tag 标签
     */
    private String parseTag(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        boolean auth = false;
        // 读取TAG
        MifareClassic mfc = MifareClassic.get(tagFromIntent);
        try {
            mfc.connect();
            int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
            byte[] out = {};
            for (int j = 1; j < sectorCount; j++) {
                auth = mfc.authenticateSectorWithKeyA(j,
                        MifareClassic.KEY_DEFAULT);
                int bCount;
                int bIndex;
                if (auth) {
                    // 读取扇区中的块
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        if (i + 1 == bCount)
                            continue;

                        byte[] data = mfc.readBlock(bIndex);
                        if (bIndex == 4) {
                            if (data[0] == (byte) 0x00) {
                                out = ByteUtil.byteSub(data, 9);
                            } else {
                                out = ByteUtil.byteSub(data, 12);
                            }
                        } else {

                            out = ByteUtil.byteMerger(out, data);
                        }
                        bIndex++;
                    }
                }
            }
            byte[] data = ByteUtil.byteSub(out, 0, (byte) 0xEF);
            String str = new String(data);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析 Ndef 标签
     */
    private String parseNdef(Intent intent) {
        wait2Scan(true);
        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage ndefs[];
        String data = "";
        if (messages != null) {
            ndefs = new NdefMessage[messages.length];
            for (int i = 0; i < messages.length; i++) {
                ndefs[i] = (NdefMessage) messages[i];
            }
            if (ndefs != null && ndefs.length > 0) {
                NdefRecord record = ndefs[0].getRecords()[0];
                byte[] payload = record.getPayload();
                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                int languageCodeLength = payload[0] & 0077;
                try {
                    data = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                    textContentTips.setText(data);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 解析 tech 标签
     */
    private String parseTech(Intent intent) {
        return parseTag(intent);
    }

    /**
     * 解析从标签中获取的数据，并进行相应处理
     *
     * @param content
     */
    private boolean ParseContent(String content) {
        boolean ret = false;
        if (TextUtils.isEmpty(content)) {
            textContentTips.setVisibility(View.VISIBLE);
            textContentTips.setText(R.string.exp_nfccontent);
            return ret;
        }
        VibratorUtil.Vibrate(PatrolTaskActivity2.this, 800); // 震动100ms
        try {
            JSONObject obj = new JSONObject(content);
            int flag = obj.optInt("Flag");
            switch (flag) {
                case FLAG_DEVICE: // 设备flag==2
                    mDevInfo = new Gson().fromJson(content, DevInfoResult.class);
                    ret = true;
                    break;
                case FLAG_WORKSAPCE: // 任务区域
                    mDevInfo = new DevInfoResult();
                    mDevInfo.setFlag(FLAG_WORKSAPCE);
                    mDevInfo.setDevId(obj.optInt("TaskAreaId"));
                    mDevInfo.setDevName(obj.optString("Workspace"));
                    ret = true;
                    break;
                case FLAG_PATROL: // 客运巡检
                    ret = false;
                    break;
                default:
                    ret = false;
                    break;
            }
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_report2repair) {
            if (mDevInfo == null) {
                Toast.makeText(this, "尚未获取到设备数据", Toast.LENGTH_SHORT).show();
            } else {
                turn2Report();
            }
        }
    }
}
