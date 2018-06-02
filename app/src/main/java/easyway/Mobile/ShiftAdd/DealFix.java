package easyway.Mobile.ShiftAdd;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/** 
 * 类说明 设备维修处理
 */
public class DealFix extends ActivityEx implements OnClickListener{
	private Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	act = this;
		setContentView(R.layout.act_deal_fix);
		initView();
    }
    private void initView(){
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("设备维修处理");
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnReturn:
			finish();
			break;

		default:
			break;
		}
	}
}
