package easyway.Mobile.site_monitoring;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TeamsActivity extends ActivityEx {
    private ArrayList<ActorTeamsBean> mList = new ArrayList<ActorTeamsBean>();
    private TeamAdapter mAdapter;
    private ListView mListView;
    private double averageDayRate;
    private double averageMonthRate;
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    mAdapter.setData((ArrayList<ActorTeamsBean>) msg.obj);
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
        setContentView(R.layout.site_team_layout);
        mListView = (ListView) findViewById(R.id.team_list);
        mAdapter = new TeamAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        showProgressDialog(R.string.GettingData);
        getAllteamSummaryInfo();
        new Thread(new ServerDataThread()).start();
    }

    private class ServerDataThread implements Runnable {
        ArrayList<ActorTeamsBean> list = new ArrayList<TeamsActivity.ActorTeamsBean>();

        @Override
        public void run() {
            list = getEachTeamInfo();
            if (list.size() == 0) {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                mHandler.sendMessage(msg);
            }
        }
    }

    private void getAllteamSummaryInfo() {
        String methodPath = "WebService/TaskMonitoring.asmx";
        String methodName = "GetAllTeamSummaryInfo";

        try {
            WebServiceManager webServiceManager = new WebServiceManager(
                    getApplicationContext(), methodName,
                    new HashMap<String, String>());
            String result = webServiceManager.OpenConnect(methodPath);
            String errMsg = JsonUtil.GetJsonString(result, "Msg");
            if (!errMsg.equals("")) {
                return;
            }
            JSONArray jsonArray =JsonUtil
                    .GetJsonArray(result, "Data");
            int dayAllNum = 0;
            int dayDutyNum = 0;
            int monthAllNum = 0;
            int monthDutyNum = 0;
            if (null != jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    dayAllNum = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "Day_Num0");
                    dayDutyNum = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "Day_Num1");
                    monthAllNum = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "Mon_Num0");
                    monthDutyNum = JsonUtil.GetJsonObjIntValue(
                            jsonObj, "Mon_Num1");
                    if (0 == dayDutyNum) {
                        averageDayRate = 0;
                    } else {
                        averageDayRate = dayAllNum / dayDutyNum;
                    }
                    if (0 == monthDutyNum) {
                        averageMonthRate = 0;
                    } else {
                        averageMonthRate = monthAllNum / monthDutyNum;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<ActorTeamsBean> getEachTeamInfo() {
        ArrayList<ActorTeamsBean> list = new ArrayList<TeamsActivity.ActorTeamsBean>();
        String methodPath = "WebService/TaskMonitoring.asmx";
        String methodName = "GetEachTeamSummaryInfo";
        try {
            WebServiceManager webServiceManager = new WebServiceManager(
                    getApplicationContext(), methodName,
                    new HashMap<String, String>());
            String result = webServiceManager.OpenConnect(methodPath);
            String errMsg = JsonUtil.GetJsonString(result, "Msg");
            if (!errMsg.equals("")) {
                return list;
            }
            JSONArray jsonArray = JsonUtil
                    .GetJsonArray(result, "Data");
            // 当班人数
            int totalNum = 0;
            // 到岗人数
            int dutyNum = 0;

            int Day_Num0 = 0;
            int Day_Num1 = 0;
            int Mon_Num0 = 0;
            int Mon_Num1 = 0;
            if (null != jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    ActorTeamsBean bean = new ActorTeamsBean();
                    bean.teamName = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "TeamName");
                    totalNum = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "TotalNum");
                    dutyNum = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "DutyNum");
                    bean.dutyNo = String.valueOf(totalNum);
                    bean.arriveNo = String.valueOf(dutyNum);
                    bean.freeNo = String.valueOf((totalNum - dutyNum));

                    Day_Num0 = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "Day_Num0");
                    Day_Num1 = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "Day_Num1");
                    if (0 == Day_Num0) {
                        bean.dayRate = "0";
                    } else {
                        bean.dayRate = String.valueOf(Day_Num1 / Day_Num0);
                    }
                    bean.averageDayRate = String.valueOf(averageDayRate);
                    Mon_Num0 = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "Mon_Num0");
                    Mon_Num1 = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "Mon_Num1");
                    if (0 == Mon_Num0) {
                        bean.monthRate = "0";
                    } else {
                        bean.monthRate = String.valueOf((Mon_Num1 / Mon_Num0));
                    }
                    bean.averageMonthRate = String.valueOf(averageMonthRate);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private class TeamAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;
        private ArrayList<ActorTeamsBean> list = new ArrayList<TeamsActivity.ActorTeamsBean>();

        public TeamAdapter(Context context, ArrayList<ActorTeamsBean> list) {
            super();
            this.mContext = context;
            mInflater = LayoutInflater.from(mContext);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public void setData(ArrayList<ActorTeamsBean> models) {
            list = models;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActorTeamsBean bean = list.get(position);
            ViewHolder holder;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.site_team_item, null);
                holder = new ViewHolder();
                holder.teamName = (TextView) convertView
                        .findViewById(R.id.btnPosition);
                holder.dutyNo = (TextView) convertView
                        .findViewById(R.id.dutyNo);
                holder.arriveNo = (TextView) convertView
                        .findViewById(R.id.arriveNo);
                holder.freeNo = (TextView) convertView
                        .findViewById(R.id.freeNo);
                holder.dayRate = (TextView) convertView
                        .findViewById(R.id.dayRate);
                holder.averageDayRate = (TextView) convertView
                        .findViewById(R.id.averageDayRate);
                holder.monthRate = (TextView) convertView
                        .findViewById(R.id.monthRate);
                holder.averageMonthRate = (TextView) convertView
                        .findViewById(R.id.averageMonthRate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.teamName.getPaint().setFakeBoldText(true);
            holder.teamName.setText(bean.teamName);
            holder.dutyNo.setText(bean.dutyNo + "人(总)");
            holder.arriveNo.setText(bean.arriveNo + "人(在岗)");
            holder.freeNo.setText(bean.freeNo + "人(空闲)");
            holder.dayRate.setText(bean.dayRate + "%" + " 当天到岗率");
            holder.averageDayRate
                    .setText(bean.averageDayRate + "%" + " 当天平均到岗率");
            holder.monthRate.setText(bean.monthRate + "%" + " 当月到岗率");
            holder.averageMonthRate.setText(bean.averageMonthRate + "%"
                    + " 当月平均到岗率");
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView teamName;
        TextView dutyNo;
        TextView arriveNo;
        TextView freeNo;
        TextView dayRate;
        TextView averageDayRate;
        TextView monthRate;
        TextView averageMonthRate;
    }

    private class ActorTeamsBean {
        public String teamName;
        public String dutyNo;
        public String arriveNo;
        public String freeNo;
        public String dayRate;
        public String averageDayRate;
        public String monthRate;
        public String averageMonthRate;
    }
}
