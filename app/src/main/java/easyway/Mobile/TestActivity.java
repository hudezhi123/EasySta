package easyway.Mobile;

import android.os.Bundle;

import easyway.Mobile.util.CopyDBUtils;
import easyway.Mobile.util.PollingService;
import easyway.Mobile.util.PollingUtils;


public class TestActivity extends ActivityEx {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        new Thread(new Runnable() {
            @Override
            public void run() {
                CopyDBUtils.Copy(TestActivity.this);
            }
        }).start();
    }
}
