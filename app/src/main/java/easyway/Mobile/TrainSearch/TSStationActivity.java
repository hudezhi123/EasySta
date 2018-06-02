package easyway.Mobile.TrainSearch;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.LinkStation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

/*
 * 车站查询
 */
public class TSStationActivity extends ActivityEx implements OnClickListener {
    private final int MSG_GETDATA_NULL = 0;
    private final int MSG_GETDATA_SUCCEED = 1;
    private final int MSG_GETDATA_FAIL = 2;
    private final int MSG_GETSTATION_SUCCEED = 3;

    private GridView gridStation;
    private StationAdapter mAdapter;

    private ArrayList<LinkStation> mlist;
    private EditText edtStation;
    private EditText lastStation;
    private String mKey = "";

    private Button exchange;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MSG_GETDATA_NULL:
                    showToast(R.string.exp_nullstation);
                    break;
                case MSG_GETDATA_SUCCEED:
                    ArrayList<TSResult> list = (ArrayList<TSResult>) msg.obj;
                    Intent intent = new Intent(TSStationActivity.this,
                            TSResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", list);
                    bundle.putString("key", mKey);
                    bundle.putString("Value", "" + value);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case MSG_GETDATA_FAIL:
                    showToast(R.string.exp_tssearch);
                    break;
                case MSG_GETSTATION_SUCCEED:
                    mlist = (ArrayList<LinkStation>) msg.obj;
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainsearch_station);
        initView();
    }

    private void initView() {
        edtStation = (EditText) findViewById(R.id.edtStation);
        lastStation = (EditText) findViewById(R.id.lastStation);
//		edtStation.setInputType(InputType.TYPE_NULL);
        exchange = (Button) findViewById(R.id.exchange);
        exchange.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String start = edtStation.getText().toString().trim();
                String stop = lastStation.getText().toString().trim();
                if (start != null && stop != null) {
                    edtStation.setText(stop);
                    lastStation.setText(start);
                }

            }
        });
        edtStation.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mKey = edtStation.getText().toString();
                new Thread() {
                    public void run() {
                        searchStation();
                    }
                }.start();
            }
        });

        int widthRight = Property.screenwidth / 6 + 20;
        int widthLeft = (Property.screenwidth - widthRight) / 5;

        Button btnA = (Button) findViewById(R.id.btnA);
        Button btnB = (Button) findViewById(R.id.btnB);
        Button btnC = (Button) findViewById(R.id.btnC);
        Button btnD = (Button) findViewById(R.id.btnD);
        Button btnE = (Button) findViewById(R.id.btnE);
        Button btnF = (Button) findViewById(R.id.btnF);
        Button btnG = (Button) findViewById(R.id.btnG);
        Button btnH = (Button) findViewById(R.id.btnH);
        Button btnJ = (Button) findViewById(R.id.btnJ);
        Button btnK = (Button) findViewById(R.id.btnK);
        Button btnL = (Button) findViewById(R.id.btnL);
        Button btnM = (Button) findViewById(R.id.btnM);
        Button btnN = (Button) findViewById(R.id.btnN);
        Button btnO = (Button) findViewById(R.id.btnO);
        Button btnP = (Button) findViewById(R.id.btnP);
        Button btnQ = (Button) findViewById(R.id.btnQ);
        Button btnR = (Button) findViewById(R.id.btnR);
        Button btnS = (Button) findViewById(R.id.btnS);
        Button btnT = (Button) findViewById(R.id.btnT);
        Button btnW = (Button) findViewById(R.id.btnW);
        Button btnX = (Button) findViewById(R.id.btnX);
        Button btnY = (Button) findViewById(R.id.btnY);
        Button btnZ = (Button) findViewById(R.id.btnZ);

        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        Button btnBack = (Button) findViewById(R.id.btnBack);
        Button btnClear = (Button) findViewById(R.id.btnClear);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);

        Button btnNull1 = (Button) findViewById(R.id.btnNull1);
        Button btnNull2 = (Button) findViewById(R.id.btnNull2);
        Button btnNull3 = (Button) findViewById(R.id.btnNull3);
        Button btnNull4 = (Button) findViewById(R.id.btnNull4);

        btnA.setWidth(widthLeft);
        btnB.setWidth(widthLeft);
        btnC.setWidth(widthLeft);
        btnD.setWidth(widthLeft);
        btnE.setWidth(widthLeft);
        btnF.setWidth(widthLeft);
        btnG.setWidth(widthLeft);
        btnH.setWidth(widthLeft);
        btnJ.setWidth(widthLeft);
        btnK.setWidth(widthLeft);
        btnL.setWidth(widthLeft);
        btnM.setWidth(widthRight);
        btnN.setWidth(widthLeft);
        btnO.setWidth(widthLeft);
        btnP.setWidth(widthLeft);
        btnQ.setWidth(widthLeft);
        btnR.setWidth(widthLeft);
        btnS.setWidth(widthLeft);
        btnT.setWidth(widthLeft);
        btnW.setWidth(widthLeft);
        btnX.setWidth(widthLeft);
        btnY.setWidth(widthLeft);
        btnZ.setWidth(widthLeft);

        btnBack.setWidth(widthRight);
        btnDelete.setWidth(widthRight);
        btnClear.setWidth(widthRight);
        btnSearch.setWidth(widthRight);

        btnNull1.setWidth(widthLeft);
        btnNull2.setWidth(widthLeft);
        btnNull3.setWidth(widthRight);
        btnNull4.setWidth(widthLeft);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
        btnE.setOnClickListener(this);
        btnF.setOnClickListener(this);
        btnG.setOnClickListener(this);
        btnH.setOnClickListener(this);
        btnJ.setOnClickListener(this);
        btnK.setOnClickListener(this);
        btnL.setOnClickListener(this);
        btnM.setOnClickListener(this);
        btnN.setOnClickListener(this);
        btnO.setOnClickListener(this);
        btnP.setOnClickListener(this);
        btnQ.setOnClickListener(this);
        btnR.setOnClickListener(this);
        btnS.setOnClickListener(this);
        btnT.setOnClickListener(this);
        btnW.setOnClickListener(this);
        btnX.setOnClickListener(this);
        btnY.setOnClickListener(this);
        btnZ.setOnClickListener(this);

        btnDelete.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        gridStation = (GridView) findViewById(R.id.gridStation);
        mAdapter = new StationAdapter(this);
        gridStation.setAdapter(mAdapter);

        new Thread() {
            public void run() {
                searchStation();
            }
        }.start();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnClear:
                edtStation.setText("");
                break;
            case R.id.btnSearch:
                if (edtStation.getText().toString().equals("")) {
                    showToast(R.string.exp_tsstation_null);
                    return;
                }
                if (lastStation.getText().toString().equals("")) {
                    showToast(R.string.exp_arstation_null);
                    return;
                }

                showProgressDialog(R.string.GettingData);
                new Thread() {
                    public void run() {
                        search();
                    }
                }.start();
                break;
            case R.id.btnDelete:
                String str = edtStation.getText().toString();
                if (str != null && str.length() != 0) {
                    str = str.substring(0, str.length() - 1);
                }
                edtStation.setText(str);
                break;
            default:
                edtStation.append(((Button) arg0).getText());
                break;
        }
    }


    private String value = "";

    private void search() {
        mKey = edtStation.getText().toString().trim();
        String start = mKey;
        String stop = lastStation.getText().toString().trim();
        value = start + " - " + stop;
        ArrayList<TSResult> list = new ArrayList<TSResult>();
        DBHelper dbHelper = new DBHelper(TSStationActivity.this);

        Cursor cursor = null;
        String sql = "";

        sql = "select a.TRNO_TT, a.Station as aStation, b.Station as bStation, a.DepaDate,"
                + " a.DepaTime, b.ArrDate, b.ArrTime"
                + " from viastation a, viastation b "
                + " where a.TRNO_TT  = b.TRNO_TT and a.Station like '%" + mKey + "%'"
                + " and a.StationOrder < b.StationOrder "
                + " and b.Station like '%" + stop + "%' order by a.DepaTime;";

        try {
            cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    TSResult result = new TSResult();
                    result.TrainNo = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_TRNO));
                    result.StationOrigin = cursor.getString(cursor
                            .getColumnIndex("aStation"));
                    result.StationArr = cursor.getString(cursor
                            .getColumnIndex("bStation"));
                    result.DepTime = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_DEPATIME));
                    result.ArrTime = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_ARRTIME));
                    result.DepDate = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.VIASTATION_DEPADATE));
                    result.ArrDate = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.VIASTATION_ARRDATE));

                    list.add(result);
                }
                Message msg = new Message();
                msg.what = MSG_GETDATA_SUCCEED;
                msg.obj = list;
                myhandle.sendMessage(msg);
            } else {
                myhandle.sendEmptyMessage(MSG_GETDATA_NULL);
            }

        } catch (Exception e) {
            myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
            e.printStackTrace();
        } finally {
            dbHelper.closeCursor(cursor);
            dbHelper.close();
        }
    }

    private void searchStation() {
        mKey = edtStation.getText().toString();
        ArrayList<LinkStation> list = new ArrayList<LinkStation>();
        DBHelper dbHelper = new DBHelper(TSStationActivity.this);

        Cursor cursor = null;
        String sql = "";

        sql = "select Name from linkstation " + " where Pinyin like '" + mKey
                + "%' order by Number desc;";

        try {
            cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    LinkStation station = new LinkStation();
                    station.Name = cursor.getString(cursor
                            .getColumnIndex(DBHelper.LINKSTATION_NAME));
                    if (!station.Name.contains("北京"))
                        list.add(station);
                }
                Message msg = new Message();
                msg.what = MSG_GETSTATION_SUCCEED;
                msg.obj = list;
                myhandle.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.closeCursor(cursor);
            dbHelper.close();
        }
    }

    class StationAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        public StationAdapter(Context context) {
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
            View temp = null;
            if (convertView != null) {
                temp = convertView;
            } else {
                temp = layoutInflater.inflate(
                        R.layout.trainsearch_station_item, parent, false);
            }

            TextView txtStaion = (TextView) temp.findViewById(R.id.txtStaion);

            final LinkStation station = (LinkStation) getItem(position);
            txtStaion.setText(station.Name);

            temp.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    edtStation.setText(station.Name);
                }

            });

            return temp;
        }
    }
}
