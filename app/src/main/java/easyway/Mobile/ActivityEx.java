package easyway.Mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.util.HomeKey;

public class ActivityEx extends Activity {
    protected String errMsg = "";
    private ProgressDialog mProDialog; // 进度显示框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        if (HomeKey.work && !HomeKey.disableHome) {
            getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
        }
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
        if (mProDialog != null)
            mProDialog.dismiss();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (!ExitApplication.isBoYuan) {
            try {
                Thread.sleep((long) (Math.random() * 2000 + 500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void closeProgressDialog() {
        if (mProDialog != null) {
            mProDialog.dismiss();
            mProDialog = null;
        }
    }

    public void showToast(CharSequence message) {
        if (message == null) return;
        if (message.equals("")) return;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showToast(int resid) {
        Toast.makeText(this, resid, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog(int msgResId) {
        showProgressDialog(getString(msgResId));
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, false);
    }

    public void showProgressDialog(int msgResId, boolean flag) {
        showProgressDialog(getString(msgResId), flag);
    }

    public void showProgressDialog(String message, boolean flag) {
        if (message == null) return;
        if (message.equals("")) return;

        if (mProDialog == null) {
            if (getParent() != null) {
                mProDialog = new ProgressDialog(getParent());
            } else {
                mProDialog = new ProgressDialog(this);
            }
        }

        if (this.isFinishing()) {
            return;
        }

        if (!mProDialog.isShowing()) {
            mProDialog.dismiss();
            mProDialog.setMessage(message);
            mProDialog.setIndeterminate(false);
            mProDialog.setCancelable(flag);
            mProDialog.setIcon(R.drawable.waiting);
            mProDialog.show();
        } else {
            mProDialog.setMessage(message);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean exitApp = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            exitApp = super.onKeyDown(keyCode, event);
        } else {
            exitApp = super.onKeyDown(keyCode, event);
        }
        return exitApp;
    }

    public void showErrMsg(int msgResId) {
        showErrMsg(getString(msgResId));
    }

    public void showErrMsg(String message) {
        if (message == null)
            return;

        if (message.equals(""))
            return;

        if (this.isFinishing()) {
            return;
        }

        AlertDialog.Builder builder = new Builder(this);
        builder.setIcon(R.drawable.error);
        builder.setMessage(message);
        builder.setTitle(R.string.Prompt);
        builder.setPositiveButton(R.string.OK, null);
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ActiveStatusService.class));
    }
}
