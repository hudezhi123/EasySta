package easyway.Mobile.ShiftAdd;

import java.util.ArrayList;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/** 
 * 类说明 设备维修
 */
public class EquipmentFix extends ActivityEx implements OnClickListener{
	private Activity act;
	private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	act = this;
		setContentView(R.layout.act_equipment_fix);
		initView();
    }
    private void initView(){
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("设备维修");
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.lv_waring);
		List<String> list = new ArrayList<String>();
		list.add("设备编号");
		list.add("XAXAB-C3N1-CZZ165");
		list.add("XAXAB-C3N1-CZZ166");
		WarningAdapter mAdapter = new WarningAdapter(act, list);
		mListView.setAdapter(mAdapter);
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
