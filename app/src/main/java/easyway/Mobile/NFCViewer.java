package easyway.Mobile;

import org.json.JSONException;
import org.json.JSONObject;

import easyway.Mobile.DevFault.DFReport;
import easyway.Mobile.DevFault.NFCDevicesInfo;
import easyway.Mobile.Patrol.PatrolActivity;
import easyway.Mobile.Task.TaskTabActivity;
import easyway.Mobile.Task.TaskTodoActivity;
import easyway.Mobile.bean.DevInfoResult;
import easyway.Mobile.util.ByteUtil;
import easyway.Mobile.util.VibratorUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.gson.Gson;

/*
 * NFC
 */
public class NFCViewer extends ActivityEx {
    private TextView txtContent;
    private final int FLAG_DEVICE = 2; // 设备
    private final int FLAG_WORKSAPCE = 1; // 任务区域
    private final int FLAG_PATROL = 3; // 巡检

    private String sqlExists = ""; // where条件
    private String taskFlag = ""; // 任务标识
    private int dateFlag = TaskTabActivity.DATEFLAG_TODAY;
    private Activity act;
    boolean isgroup = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfcviewer);
        act = this;
        txtContent = (TextView) findViewById(R.id.txtContent);
    }

    @Override
    public void onResume() {
        super.onResume();
        String nfctext = "";
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            nfctext = ParseNdef();
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent()
                .getAction())) {
            nfctext = ParseTag();
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent()
                .getAction())) {
            nfctext = ParseTag();
        }

        if (ParseData(nfctext))
            finish();
    }

    // 对NFC数据进行解析，进入对应的处理流程
    private boolean ParseData(String str) {
        boolean ret = false;
        if (str == null) {
            txtContent.setText(R.string.exp_nfccontent);
            return ret;
        }
        VibratorUtil.Vibrate(NFCViewer.this, 800); // 震动100ms
        txtContent.setText(str);
        if (Property.SessionId == null || Property.SessionId.length() == 0) {
            txtContent.setText(R.string.exp_notlogin);
        } else {
            try {
                JSONObject obj = new JSONObject(str);
                int flag = obj.optInt("Flag");
                switch (flag) {
                    case FLAG_DEVICE: // 设备flag==2
                        if (!"SYB".equals(Property.StationCode)) {
                            DevInfoResult result = new Gson().fromJson(str, DevInfoResult.class);
                            Intent intentdev = new Intent(NFCViewer.this,
                                    NFCDevicesInfo.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("DevInfoResult", result);
                            intentdev.putExtras(bundle);
                            startActivity(intentdev);
                        } else {
                            DevInfoResult result = new Gson().fromJson(str, DevInfoResult.class);
                            Intent intent = new Intent(NFCViewer.this, DFReport.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("DevInfoResult", result);
                            bundle.putString("Flag", "NFCViewer");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        ret = true;
                        break;
                    case FLAG_WORKSAPCE: // 任务区域
                        Long taskareaid = obj.getLong("TaskAreaId");
                        String workspace = obj.getString("Workspace");
                        Intent intentworkspace = new Intent(NFCViewer.this,
                                TaskTodoActivity.class);
                        intentworkspace.putExtra("taskareaid", taskareaid);
                        intentworkspace.putExtra("workspace", workspace);
                        // HDZ_LOG
//                        intentworkspace.putExtra("taskareaid", 1706);
//                        intentworkspace.putExtra("workspace", " A12、A13检票口");
                        startActivity(intentworkspace);
                        ret = true;
                        break;
                    case FLAG_PATROL: // 客运巡检
                        Long AreaId = obj.getLong("AreaId");
                        Intent intentpatrol = new Intent(NFCViewer.this,
                                PatrolActivity.class);
                        intentpatrol.putExtra("AreaId", AreaId);
                        startActivity(intentpatrol);
                        ret = true;
                        break;
                    default:
                        txtContent.setText(R.string.exp_nfccontent);
                        break;
                }
            } catch (Exception e) {
                txtContent.setText(R.string.exp_nfccontent);
                e.printStackTrace();
                return false;
            }
        }

        return ret;
    }

    /*
     * ACTION_TAG_DISCOVERED 获取MifareClassic数据
     */
    @SuppressLint({"InlinedApi", "NewApi"})
    private String ParseTag() {
        Tag tagFromIntent = getIntent()
                .getParcelableExtra(NfcAdapter.EXTRA_TAG);

        boolean auth = false;
        // 读取TAG
        MifareClassic mfc = MifareClassic.get(tagFromIntent);
        try {
            mfc.connect();
            int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数

            byte[] out = {};
            for (int j = 1; j < sectorCount; j++) {
                // Authenticate a sector with key A.
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

    /*
     * ACTION_NDEF_DISCOVERED 获取NdefMessage数据
     */
    private String ParseNdef() {
        Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] msgs;
        String text = "";
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }

            if (msgs != null && msgs.length > 0) {

                try {
                    byte[] payload = msgs[0].getRecords()[0].getPayload();
                    String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
                            : "UTF-16";
                    int languageCodeLength = payload[0] & 0077;
                    text = new String(payload, languageCodeLength + 1,
                            payload.length - languageCodeLength - 1,
                            textEncoding);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                txtContent.setText(text);
            }
        }
        return text;
    }

}
