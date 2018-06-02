package easyway.Mobile.TrainSearch;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.TrainAD.TrainADSchedule;
import easyway.Mobile.util.DateUtil;

/*
 * 检索结果
 */
public class TSResultActivity extends ActivityEx {
    private ArrayList<TSResult> mlist;
    private String mKey;
    private String mValue;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainsearch_result);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_search_result);
        mlist = (ArrayList<TSResult>) getIntent().getSerializableExtra("list");
        mKey = getIntent().getStringExtra("key");
        mValue = getIntent().getStringExtra("Value");
        initView();
    }

    private void initView() {
        TextView txtTime = (TextView) findViewById(R.id.text_time_stamp);
        txtTime.setText(DateUtil.getTodayTimeStamp());
        TextView txtKey = (TextView) findViewById(R.id.txtKey);
        if (TextUtils.isEmpty(mValue)) {
            if (!TextUtils.isEmpty(mKey)) {
                txtKey.setText(mKey);
            }
        } else {
            txtKey.setText(mValue);
        }
        TextView txtResult = (TextView) findViewById(R.id.txtResult);
        int count = 0;
        if (mlist != null)
            count = mlist.size();
        String result = String.format(getString(R.string.TS_Result), count + "");
        txtResult.setText(result);
        ListView lstResult = (ListView) findViewById(R.id.lstResult);
        ResultAdapter adapter = new ResultAdapter(this);
        lstResult.setAdapter(adapter);
        lstResult.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (mlist == null)
                    return;

                if (arg2 < mlist.size()) {
                    Intent intent = new Intent(TSResultActivity.this, TrainADSchedule.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("TrainNo", mlist.get(arg2).TrainNo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    class ResultAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        public ResultAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            if (mlist == null)
                return 0;

            return mlist.size();
        }

        public Object getItem(int position) {
            if (mlist == null)
                return null;
            else
                return mlist.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.trainsearch_result_item, parent, false);
                holder = new Holder();
                holder.txtTrainNo = (TextView) convertView.findViewById(R.id.txtTrainNo);
                holder.txtStationOrigin = (TextView) convertView.findViewById(R.id.txtStationOrigin);
                holder.txtDepTime = (TextView) convertView.findViewById(R.id.txtDepTime);
                holder.txtStationArr = (TextView) convertView.findViewById(R.id.txtStationArr);
                holder.txtArrTime = (TextView) convertView.findViewById(R.id.txtArrTime);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final TSResult result = (TSResult) getItem(position);
            holder.txtTrainNo.setText(result.TrainNo);
            holder.txtStationOrigin.setText(result.StationOrigin);
            holder.txtDepTime.setText(result.DepTime);
            holder.txtStationArr.setText(result.StationArr);
            holder.txtArrTime.setText(result.ArrTime);
            return convertView;
        }

        private class Holder {
            TextView txtTrainNo, txtStationOrigin, txtDepTime, txtStationArr, txtArrTime;
        }
    }
}
