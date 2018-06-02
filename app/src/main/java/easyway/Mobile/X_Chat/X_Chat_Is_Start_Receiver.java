package easyway.Mobile.X_Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import easyway.Mobile.Application.ExitApplication;

/**
 * Created by boy on 2017/6/29.
 */


public class X_Chat_Is_Start_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("easyway.Mobile.x_chat_is_start_receiver")) {
            Bundle bundle = intent.getExtras();
            ExitApplication.isX_Chat_Exit = bundle.getBoolean("X-Chat_Exit");
            ExitApplication.isX_Chat_Start = bundle.getBoolean("X-Chat_Login");
        }
    }
}
