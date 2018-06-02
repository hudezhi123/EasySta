package easyway.Mobile.site_monitoring;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.StringUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/*
 * 任务详情
 */
public class TaskDetailActivity extends ActivityEx {
    public static final String KEY_STAFFID = "STAFFID";
    public static final String KEY_STAFFNAME = "STAFFNAME";

    private SMTask mtask;
    private TeamAdapter teamAdapter;

    // 任务责任人
    private long mStaffId = SMTask.INVALID_STAFFID;
    private String mStaffName;

    private int count = 0;
    private int max = 2;

    private Button tipsBtn;

    private final int MSG_GET_ACTOR_FAIL = 0;
    private final int MSG_GET_ACTOR_SUCCEED = 1;
    private final int MSG_GET_MANAGER_FAIL = 2;
    private final int MSG_GET_MANAGER_SUCCEED = 3;
    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            count++;
            if (count == max)
                closeProgressDialog();

            switch (msg.what) {
                case MSG_GET_ACTOR_SUCCEED:
                    teamAdapter.setData((ArrayList<ActorBean>) msg.obj);
                    teamAdapter.notifyDataSetChanged();
                    break;
                case MSG_GET_ACTOR_FAIL:
                case MSG_GET_MANAGER_FAIL:
                    showToast(errMsg);
                    break;
                case MSG_GET_MANAGER_SUCCEED:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_task_detail_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mtask = (SMTask) extras.getSerializable(SMTask.KEY_TASK);

        initView();
    }

    private void initView() {
        TextView checkPointerTxt = (TextView) findViewById(R.id.check_point);
        checkPointerTxt.setText(mtask.Workspace);

        TextView trainNOTxt = (TextView) findViewById(R.id.traninNo);
        TextView plantime = (TextView) findViewById(R.id.plantime);
        trainNOTxt.setText(mtask.TrainNum);
        if (StringUtil.isNullOrEmpty(DateUtil
                .formatDate(mtask.BeginWorkTime, DateUtil.HH_MM))) {
            plantime.setText("");
        } else {
            plantime.setText(DateUtil.formatDate(mtask.BeginWorkTime, DateUtil.HH_MM)
                    + " - " + DateUtil.formatDate(mtask.EndWorkTime, DateUtil.HH_MM));
        }

        Button cancelBtn = (Button) findViewById(R.id.closeBtn);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tipsBtn = (Button) findViewById(R.id.tips_undutty);
        tipsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStaffId == SMTask.INVALID_STAFFID) { // 无负责人或负责人不在线
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            TaskDetailActivity.this);
                    builder.setTitle(R.string.Prompt);
                    builder.setMessage(R.string.nobodyonline);
                    builder.setPositiveButton(R.string.OK, null);
                    builder.create().show();
                } else {
                    Intent intent = new Intent(TaskDetailActivity.this,
                            UnDutyTipsActivity.class);

                    Bundle extras = new Bundle();
                    extras.putSerializable(SMTask.KEY_TASK, mtask);
                    extras.putString(KEY_STAFFNAME, mStaffName);
                    extras.putLong(KEY_STAFFID, mStaffId);
                    intent.putExtras(extras);
                    startActivity(intent);

                    finish();
                }
            }
        });

        setActorsData();
    }

    private void setActorsData() {
        TextView teamNameTxt = (TextView) findViewById(R.id.teams);

        if (mtask.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) { // 未到岗
            if (mtask.StaffName == null || mtask.StaffName.length() == 0)
                teamNameTxt.setText(mtask.DeptName);
            else
                teamNameTxt.setText(mtask.StaffName);
            getData(false);
        } else { // 到岗、完成
            teamNameTxt.setVisibility(View.GONE);
            ListView teamList = (ListView) findViewById(R.id.nameList);
            teamList.setVisibility(View.VISIBLE);
            teamAdapter = new TeamAdapter(this, null);
            teamList.setAdapter(teamAdapter);
            getData(true);
        }
    }

    // 获取数据
    private void getData(boolean onduty) {
        showProgressDialog(R.string.GettingData);

        if (onduty) {
            max = 2;
            new Thread() {
                public void run() {
                    getActorDataFromServer();
                }
            }.start();
        } else {
            max = 1;
        }

        if (mtask.StaffId == SMTask.INVALID_STAFFID) {
            new Thread() {
                public void run() {
                    getTeamMgr();
                }
            }.start();
        } else {
            mStaffId = mtask.StaffId;
            mStaffName = mtask.StaffName;
            myhandle.sendEmptyMessage(MSG_GET_MANAGER_SUCCEED);
        }
    }

    // 根据组编号，获取组长编号和姓名
    private void getTeamMgr() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("deptId", String.valueOf(mtask.DeptId));
        paramValues.put("sessionId", Property.SessionId);
        if (SMTabActivity.mStationCode != null)
            paramValues.put("stationCode", SMTabActivity.mStationCode);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TEAMHEAD_INFO;

        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getteammanager);
        } else {
            int Code = JsonUtil.GetJsonInt(result, "Code");
            switch (Code) {
                case Constant.NORMAL:
                    JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
                        mStaffId = JsonUtil.GetJsonObjLongValue(jsonObj,
                                "ManagerStaffId");
                        mStaffName = JsonUtil.GetJsonObjStringValue(jsonObj,
                                "Manager");
                    }
                    myhandle.sendEmptyMessage(MSG_GET_MANAGER_SUCCEED);
                    return;
                case Constant.EXCEPTION:
                default:
                    errMsg = JsonUtil.GetJsonString(result, "Msg");
                    break;
            }
        }

        myhandle.sendEmptyMessage(MSG_GET_MANAGER_FAIL);
    }

    // 获取任务刷卡信息
    private void getActorDataFromServer() {
        ArrayList<ActorBean> list = new ArrayList<ActorBean>();

        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("saId", String.valueOf(mtask.SaId));
        paramValues.put("sessionId", Property.SessionId);
        if (SMTabActivity.mStationCode != null)
            paramValues.put("stationCode", SMTabActivity.mStationCode);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKACTORSIGNINFO;

        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_gettasksigninfo);
        } else {
            int Code = JsonUtil.GetJsonInt(result, "Code");
            switch (Code) {
                case Constant.NORMAL:
                    JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                    if (null != jsonArray) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                            ActorBean actorBean = new ActorBean();
//						actorBean.StaffId = JsonUtil.GetJsonObjLongValue(
//								jsonObj, "StaffId");
                            actorBean.StaffName = JsonUtil.GetJsonObjStringValue(
                                    jsonObj, "StaffName");
                            actorBean.IsOnline = JsonUtil.GetJsonObjBooleanValue(
                                    jsonObj, "IsOnline");
//						actorBean.IsOwner = JsonUtil.GetJsonObjBooleanValue(
//								jsonObj, "IsOwner");
                            actorBean.IsCompleted = JsonUtil.GetJsonObjBooleanValue(
                                    jsonObj, "IsCompleted");
                            list.add(actorBean);
                        }
                    }
                    Message msg = new Message();
                    msg.what = MSG_GET_ACTOR_SUCCEED;
                    msg.obj = list;
                    myhandle.sendMessage(msg);
                    return;
                case Constant.EXCEPTION:
                default:
                    errMsg = JsonUtil.GetJsonString(result, "Msg");
                    break;
            }
        }

        myhandle.sendEmptyMessage(MSG_GET_ACTOR_FAIL);
    }

    // 执行组员列表Adapter
    private class TeamAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<ActorBean> list;

        public TeamAdapter(Context mContext, ArrayList<ActorBean> list) {
            mInflater = LayoutInflater.from(mContext);
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null)
                return 0;
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            if (list == null)
                return null;
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setData(ArrayList<ActorBean> models) {
            list = models;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActorBean bean = list.get(position);
            ViewHolder holder;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.site_actor_item, null);
                holder = new ViewHolder();

                holder.memberName = (TextView) convertView
                        .findViewById(R.id.actorName);
                holder.statusImage = (ImageView) convertView
                        .findViewById(R.id.status_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.memberName.setText(bean.StaffName);
            if (bean.IsCompleted) {
                holder.statusImage.setImageResource(R.drawable.site_done);
            } else if (bean.IsOnline) {
                holder.statusImage.setImageResource(R.drawable.site_duty);
            } else
                holder.statusImage.setImageResource(R.drawable.site_unduty);

            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView statusImage;
        TextView memberName;
    }

    private class ActorBean {
        //		public long StaffId;
        public String StaffName;
        //		public boolean IsOwner;
        public boolean IsOnline;
        public boolean IsCompleted;
    }
}
