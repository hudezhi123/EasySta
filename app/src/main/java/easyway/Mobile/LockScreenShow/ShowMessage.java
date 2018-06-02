package easyway.Mobile.LockScreenShow;


import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.DevFault.DFList;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.R;
import easyway.Mobile.Caution.CautionDetailActivity;
import easyway.Mobile.Caution.CautionEditActivity;
import easyway.Mobile.Message.MessageList;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Task.TaskTabActivity;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.JsonUtil;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowMessage extends Activity {

    private Activity act;
    private TextView text;
    private TextView titile;
    private LinearLayout lin2;
    private TextView bt;
    private LinearLayout lin;
    private String s;
    private ImMessage mlis;
    private final int msgIsNull = 100;
    private final int msgIsError = 99;
    private int SoundId = 0;

    /**
     * 判断来自哪里的消息。然后做相应的处理。
     * 1 的话，是消息列表的消息。跳转到消息列表去。
     * 默认的话。都是任务提醒的都是跳转到待办任务中去。
     */
    int WhereAreYouFrom;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case msgIsNull:

                    break;
                case msgIsError:

                    break;
                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        voicePool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        SoundId = voicePool.load(this, R.raw.message_arrive, 20);
        act = this;
        mlis = ExitApplication.getInstance().getmLisner();
        ExitApplication.getInstance().addActivity(act);
        setContentView(R.layout.activity_showmessage);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        text = (TextView) findViewById(R.id.local_scereen_mes);
        titile = (TextView) findViewById(R.id.local_scereen_title);
        lin2 = (LinearLayout) findViewById(R.id.local_scereen_lin);
        lin = (LinearLayout) findViewById(R.id.local_scereen);
        bt = (TextView) findViewById(R.id.local_scereen_ok);
        initView(null);
        super.onCreate(savedInstanceState);
    }

    private SoundPool voicePool = null;

    @Override
    protected void onResume() {
        super.onResume();
        voicePool.play(SoundId, 1, 1, 0, 0, 1);
    }

    private void initView(Intent isNewIntent) {
        Intent mIntent;
        if (isNewIntent != null) {
            mIntent = isNewIntent;
        } else {
            //这里是第一次的意思，第一次，肯定是没有意图传进来的。只能自己去get.
            mIntent = getIntent();
        }

        WhereAreYouFrom = mIntent.getIntExtra("WhereAreYouFrom", 0);
        if (WhereAreYouFrom == 1) {
            final int num = mIntent.getIntExtra("MessageNum", 0);
            final int dfMsgNum = mIntent.getIntExtra("dfMsgNum", 0);
            if (dfMsgNum == 0) {
                titile.setText("未读消息");
                text.setText("您收到" + String.valueOf(num) + "条短信,请注意查收");
            } else {
                if (num == 0) {
                    titile.setText("设备报障消息");
                    text.setText("您收到" + String.valueOf(dfMsgNum) + "条设备报障信息,请注意查收");
                } else {
                    titile.setText("未读消息");
                    text.setText("您收到" + String.valueOf(num) + "条短信,其中" + String.valueOf(dfMsgNum) + "条设备报障信息，" + "请注意查收");
                }

            }

            lin2.setVisibility(View.GONE);
            bt.setVisibility(View.GONE);
            lin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (dfMsgNum == 0) {
                        Intent intent = new Intent(act, MessageList.class);
                        startActivity(intent);
                    } else {
                        if (num == 0) {
                            Intent intent = new Intent(act, DFList.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(act, MessageList.class);
                            startActivity(intent);
                        }
                    }

                }
            });
        }
        if (WhereAreYouFrom == 0) {
            titile.setText("待办工作");
            s = mlis.getMsg();
            text.setText(s + "");
            bt.setVisibility(View.GONE);
            lin2.setVisibility(View.GONE);
            lin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act, TaskTabActivity.class);
                    startActivity(intent);
                }
            });

            bt.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
        }
        if (WhereAreYouFrom == 2) {
            titile.setText("事件提示");
            long id = (Long) mIntent.getLongExtra(CautionEditActivity.KEY_CAUTIONID, 0);
            lin2.setVisibility(View.GONE);
            bt.setVisibility(View.GONE);
            Intent intent = new Intent(act, CautionDetailActivity.class);
            intent.putExtra(CautionEditActivity.KEY_CAUTIONID, id);
            startActivity(intent);
        }

    }

	/*如果弹窗Activity本身并不主动更新信息，当有新的信息来时需要更新Activity的界面，由于在上面我们设的是singleInstance启动模式，
    所以需要覆写onNewIntent(Intent intent)方法，这样当再次启动这个activity时，新的intent会在该方法中传入。*/

    @Override
    protected void onNewIntent(Intent intent) {
        initView(intent);
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
            super.onNewIntent(intent);
        }
    }

    private void SetIsReceipt(String nmid) {
        SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
        String SessionId = sp.getString(CommonFunc.CONFIG_SessionId, "");

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", SessionId);
        parmValues.put("nmid", nmid);

        String methodPath = Constant.MP_Notify;
        String methodName = Constant.MN_SETISRECEIPT;
        WebServiceManager webServiceManager = new WebServiceManager(this,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (!TextUtils.isEmpty(result)) {
            int code = JsonUtil.GetJsonInt(result, "Code");
            if (code == 1000) {
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                int size = jsonArray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject base = (JSONObject) jsonArray.opt(i);

                }
            } else {

            }
        } else {
            handler.sendEmptyMessage(msgIsNull);
        }
    }

}
