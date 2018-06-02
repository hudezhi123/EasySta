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
 * 类说明 中止维修
 */
public class AbortFix extends ActivityEx implements OnClickListener{
	private Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	act = this;
		setContentView(R.layout.act_abort_fix);
		initView();
    }
    private void initView(){
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("中止维修");
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
