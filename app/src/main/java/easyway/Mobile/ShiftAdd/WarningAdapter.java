package easyway.Mobile.ShiftAdd;

import java.util.List;

import easyway.Mobile.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * @author lushiju 2016-8-24 下午4:58:31 
 * 类说明 
 */
public class WarningAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;
	private LayoutInflater mInflater;
	public WarningAdapter(Context context,List<String> list){
		this.context = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.warning_item,parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position==0) {
			holder.tv_warning_operate.setVisibility(View.VISIBLE);
			holder.rl_warning_btn.setVisibility(View.GONE);
		}else {
			holder.tv_warning_operate.setVisibility(View.GONE);
			holder.rl_warning_btn.setVisibility(View.VISIBLE);
		}
		holder.tv_warning_code.setText(list.get(position));
		return convertView;
	}
    static class ViewHolder{
    	TextView tv_warning_code,tv_warning_name,tv_warning_operate;
    	Button btn_warning_check,btn_warning_delete;
    	RelativeLayout rl_warning_btn;
    	public ViewHolder(View view){
    		tv_warning_code = (TextView) view.findViewById(R.id.tv_warning_code);
    		tv_warning_name = (TextView) view.findViewById(R.id.tv_warning_name);
    		tv_warning_operate = (TextView) view.findViewById(R.id.tv_warning_operate);
    		btn_warning_check = (Button) view.findViewById(R.id.btn_warning_check);
    		btn_warning_delete = (Button) view.findViewById(R.id.btn_warning_delete);
    		rl_warning_btn = (RelativeLayout) view.findViewById(R.id.rl_warning_btn);
    	}
    }
}
