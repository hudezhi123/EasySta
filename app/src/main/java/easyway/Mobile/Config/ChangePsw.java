package easyway.Mobile.Config;

import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Property;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*
 *  修改密码
 */
public class ChangePsw extends ActivityEx {
    private EditText edtOldPsw;
    private EditText edtNewPsw;
    private EditText edtConfirmPsw;

    private Button btnCancel;
    private Button btnOK;
    
    private final int MSG_DATA_EXP = 0;			// 数据输入不正确
    private final int MSG_CHANGE_SUCC = 1;		// 修改密码成功
    private final int MSG_CHANGE_FAIL = 2;			// 修改密码失败
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MSG_DATA_EXP:		// 数据输入不正确
                    showToast(errMsg);
                    break;
                case MSG_CHANGE_SUCC:		// 修改密码成功
                	showToast(R.string.changepasswordsucceed);
                	
                	Property.Reset(ChangePsw.this);
                	Intent intent = new Intent(ChangePsw.this,
							LoginFrame.class);
					startActivity(intent);

					finish();
                    break;
                case MSG_CHANGE_FAIL:		// 修改密码失败
                	showToast(R.string.exp_changepassword);
                	break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_psw);

        initView();
    }

    private void initView() {
    	edtOldPsw = (EditText) findViewById(R.id.edtOldPsw);
    	edtNewPsw = (EditText) findViewById(R.id.edtNewPsw);
    	edtConfirmPsw = (EditText) findViewById(R.id.edtConfirmPsw);

    	btnCancel = (Button) findViewById(R.id.btnCancel);
    	btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	finish();
            }
        });
        
    	btnOK = (Button) findViewById(R.id.btnOK);
    	btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	showProgressDialog(R.string.connect);
            	new Thread() {
            		public void run() {
            			if (CheckData()) {
            				ChangeData();
            			}
            		}
            	}.start();
            }
        });
    }
    
    // 检测数据
    private boolean CheckData() {
    	Boolean ret = true;
    	String oldpsw = edtOldPsw.getText().toString().trim();
    	String cfmpsw = edtConfirmPsw.getText().toString().trim();
    	String newpsw = edtNewPsw.getText().toString().trim();

    	if (!newpsw.equals(cfmpsw)) {
    		errMsg = getString(R.string.exp_twopassword);
    		ret = false;
    	}    	
    	
    	if (cfmpsw.length() == 0) {
    		errMsg = getString(R.string.exp_confirmpassword);
    		ret = false;
    	}
    	
    	if (newpsw.length() == 0) {
    		errMsg = getString(R.string.exp_newpassword);
    		ret = false;
    	}
    	
    	if (oldpsw.length() == 0) {
    		errMsg = getString(R.string.exp_oldpassword);
    		ret = false;
    	}
    	
    	
    	if (!ret) {
    		mHandler.sendEmptyMessage(MSG_DATA_EXP);
    	}
    	
    	return ret;
    }
    
    // 修改密码
    private void ChangeData() {
    	boolean ret = true;
    	String oldpsw = edtOldPsw.getText().toString().trim();
    	String newpsw = edtNewPsw.getText().toString().trim();

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("loginName", Property.UserName);
		parmValues.put("oldPassword", oldpsw);
		parmValues.put("newPassword", newpsw);
		
		String methodPath = Constant.MP_ISTATIONSERVICE;
		String methodName = Constant.MN_CHANGE_PSW; 
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		

		String result = webServiceManager.OpenConnect(methodPath);
		if (result == null || result.trim().length() == 0) {
			ret = false;
		} else {
			int Code = JsonUtil.GetJsonInt(result, "Code");
			if (Code != Constant.NORMAL)
				ret = false;
		}	
		
		if (ret) {
			mHandler.sendEmptyMessage(MSG_CHANGE_SUCC);
		} else {
			mHandler.sendEmptyMessage(MSG_CHANGE_FAIL);
		}
    }
}
