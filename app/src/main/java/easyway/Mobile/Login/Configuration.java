package easyway.Mobile.Login;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.StringUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*
 * 设置服务器地址
 */
public class Configuration extends ActivityEx {
	private EditText edtServer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        edtServer = (EditText) findViewById(R.id.txServer);
        String server=CommonFunc.GetServer(Configuration.this);
        if (StringUtil.isNullOrEmpty(server))
        {
            server="http://";
        }
        edtServer.setText(server);
        edtServer.setSelection(server.length());

        Button btnSaveConfig = (Button) findViewById(R.id.btnSaveConfig);
        btnSaveConfig.setOnClickListener(saveConfig());

    }

    private OnClickListener saveConfig() {
        return new OnClickListener() {
            public void onClick(View v) {
                String server = edtServer.getText().toString().trim();
                
                CommonFunc.SetServer(server, Configuration.this);
                AlertDialog.Builder builder = new Builder(Configuration.this);
                builder.setIcon(R.drawable.information);
                builder.setTitle(R.string.Prompt);
                builder.setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                finish();
                            }
                        });
                builder.setMessage(R.string.config_success);
                builder.create().show();
            }
        };
    }
}
