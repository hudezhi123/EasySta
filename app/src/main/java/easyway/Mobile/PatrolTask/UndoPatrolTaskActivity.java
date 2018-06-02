package easyway.Mobile.PatrolTask;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;

public class UndoPatrolTaskActivity extends ActivityEx implements View.OnClickListener {

    private List<PatrolTask> parentList;
    private PatrolTaskAdapter mAdapter;
    private ExpandableListView listView;
    private ImageView imgCount, imgStates;
    private boolean isUpCount = false;
    private boolean isUpState = false;
    private static final int UNDO_LIST_SUCCESS = 1001;
    private static final int UNDO_LIST_EXCEPTION = 1002;
    private static final int COMPLETE_LIST_SUCCESS = 2001;
    private static final int COMPLETE_LIST_EXCEPTION = 2002;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UNDO_LIST_SUCCESS:
                    if (parentList != null && parentList.size() > 0) {
                        parentList.clear();
                    }
                    if (msg.obj != null) {
                        parentList.addAll((List<PatrolTask>) msg.obj);
                    }
                case UNDO_LIST_EXCEPTION:
                    getCompleteDevice();
                    break;
                case COMPLETE_LIST_SUCCESS:
                    if (msg.obj != null) {
                        parentList.addAll((List<PatrolTask>) msg.obj);
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                case COMPLETE_LIST_EXCEPTION:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undo_patrol_task);
        init();
        getData();
    }


    private void init() {
        initTitleBar();
        initView();
        initList();
        listView = (ExpandableListView) findViewById(R.id.listview_undo_task);
        mAdapter = new PatrolTaskAdapter(this, parentList);
        listView.setAdapter(mAdapter);
    }

    private void initView() {
        imgCount = (ImageView) findViewById(R.id.img_count);
        imgStates = (ImageView) findViewById(R.id.img_status);
        imgCount.setOnClickListener(this);
        imgStates.setOnClickListener(this);
    }

    private void initList() {
        parentList = new ArrayList<>();
    }


    private List<PatrolTask> inCountOrder(List<PatrolTask> taskList, boolean isUp) {
        PatrolTask max_task = null;
        if (taskList == null || taskList.size() <= 0) {
            return null;
        }
        for (int i = 0; i < taskList.size() - 1; i++) {
            for (int j = i + 1; j < taskList.size(); j++) {
                if (taskList.get(i).getCount() < taskList.get(j).getCount()) {
                    max_task = taskList.get(j);
                    taskList.set(j, taskList.get(i));
                    taskList.set(i, max_task);
                }
            }
        }
        if (!isUp) {
            List<PatrolTask> tasks = new ArrayList<>();
            for (int i = taskList.size() - 1; i >= 0; i--) {
                tasks.add(taskList.get(i));
            }
            taskList.clear();
            taskList.addAll(tasks);
        }
        return taskList;
    }


    /**
     * 按照状态的升序排列
     *
     * @param taskList
     * @param isUp     true 为升序  即  未巡检  巡检
     * @return
     */
    private List<PatrolTask> inStatesOrder(List<PatrolTask> taskList, boolean isUp) {
        if (taskList == null || taskList.size() <= 0) {
            return null;
        }
        List<PatrolTask> unPt = new ArrayList<>();
        List<PatrolTask> cPt = new ArrayList<>();
        for (PatrolTask task : taskList) {
            if (task.getStatus().equals("未巡检")) {
                unPt.add(task);
            } else {
                cPt.add(task);
            }
        }
        taskList.clear();
        if (isUp) {
            taskList.addAll(unPt);
            taskList.addAll(cPt);
        } else {
            taskList.addAll(cPt);
            taskList.addAll(unPt);
        }
        return taskList;
    }

    private void initTitleBar() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("未巡检对象");
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUndoDeviceTaskData();
            }
        }).start();
    }

    /**
     * 获取未完成的设备巡检对象
     */
    private void getUndoDeviceTaskData() {
        HashMap<String, String> paramValue = new HashMap<>();
        paramValue.put("sessionId", Property.SessionId);
        paramValue.put("stationCode", Property.StationCode);
        String methodName = Constant.PATROL_WAIT_TO_TASK;
        String methodPath = Constant.MP_PATROL;
        WebServiceManager service = new WebServiceManager(UndoPatrolTaskActivity.this, methodName, paramValue);
        String jsonResult = service.OpenConnect(methodPath);
        parseUndoList(jsonResult);
    }

    private void parseUndoList(String jsonResult) {
        if (TextUtils.isEmpty(jsonResult)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(jsonResult);
            int Code = obj.optInt("Code");
            if (Code == 1000) {
                JSONArray datas = obj.optJSONArray("Data");
                if (datas != null && datas.length() > 0) {
                    List<PatrolTask> taskList = new ArrayList<>();
                    PatrolTask task = null;
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject data = datas.optJSONObject(i);
                        task = new PatrolTask();
                        task.setStatus("未巡检");
                        task.setCount(0);
                        task.setName(data.optString("InspectArea"));
                        task.setID(data.optString("ObjectId"));
                        taskList.add(task);
                    }
                    Message msg = myHandler.obtainMessage();
                    msg.obj = taskList;
                    msg.what = UNDO_LIST_SUCCESS;
                    myHandler.sendMessage(msg);
                }
            } else {
                myHandler.sendEmptyMessage(UNDO_LIST_EXCEPTION);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCompleteDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> paramValue = new HashMap<>();
                paramValue.put("sessionId", Property.SessionId);
                paramValue.put("stationCode", Property.StationCode);
                String methodName = Constant.PATROL_ALREADY_TASK;
                String methodPath = Constant.MP_PATROL;
                WebServiceManager service = new WebServiceManager(UndoPatrolTaskActivity.this, methodName, paramValue);
                String jsonResult = service.OpenConnect(methodPath);
                parseCompleteList(jsonResult);
            }
        }).start();
    }

    private void parseCompleteList(String jsonResult) {
        if (TextUtils.isEmpty(jsonResult)) {
            myHandler.sendEmptyMessage(COMPLETE_LIST_EXCEPTION);
            return;
        }
        try {
            JSONObject obj = new JSONObject(jsonResult);
            int Code = obj.optInt("Code");
            if (Code == 1000) {
                JSONArray datas = obj.optJSONArray("Data");
                if (datas != null && datas.length() > 0) {
                    List<PatrolTask> ptList = new ArrayList<>();
                    Map<String, List<ChildTask>> childMap = new HashMap<>();
                    List<ChildTask> taskList = new ArrayList<>();
                    ChildTask task = null;
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject data = datas.optJSONObject(i);
                        task = new ChildTask();
                        task.setTime(data.optString("InspectTime"));
                        task.setObjectID(data.optString("ObjectId"));
                        task.setObjectName(data.optString("ObjectName"));
                        task.setUserName(data.optString("InspectorUser"));
                        taskList.add(task);
                    }
                    for (ChildTask child : taskList) {
                        if (childMap.containsKey(child.getObjectID())) {
                            childMap.get(child.getObjectID()).add(child);
                        } else {
                            List<ChildTask> tasks = new ArrayList<>();
                            tasks.add(new ChildTask("巡检人员", "巡检时间"));
                            tasks.add(child);
                            childMap.put(child.getObjectID(), tasks);
                        }
                    }
                    for (Map.Entry<String, List<ChildTask>> entry : childMap.entrySet()) {
                        PatrolTask pt = new PatrolTask();
                        pt.setStatus("已巡检");
                        pt.setID(entry.getKey());
                        Map<String,List<ChildTask>> detailMap = new HashMap<>();
                        detailMap.put(entry.getKey(),entry.getValue());
                        pt.setDetailMap(detailMap);
                        if (entry.getValue().size() == 0) {
                            pt.setCount(0);
                        } else {
                            pt.setCount(entry.getValue().size() - 1);
                            pt.setName(entry.getValue().get(1).getObjectName());
                        }
                        ptList.add(pt);
                    }
                    Message msg = myHandler.obtainMessage();
                    msg.what = COMPLETE_LIST_SUCCESS;
                    msg.obj = ptList;
                    myHandler.sendMessage(msg);
                } else {
                    myHandler.sendEmptyMessage(COMPLETE_LIST_SUCCESS);
                }
            } else {
                myHandler.sendEmptyMessage(COMPLETE_LIST_EXCEPTION);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_count:
                isUpCount = !isUpCount;
                if (isUpCount) {
                    imgCount.setRotation(180);
                } else {
                    imgCount.setRotation(0);
                }
                parentList = inCountOrder(parentList, !isUpCount);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.img_status:
                isUpState = !isUpState;
                if (isUpState) {
                    imgStates.setRotation(180);
                } else {
                    imgStates.setRotation(0);
                }
                parentList = inStatesOrder(parentList, isUpState);
                mAdapter.notifyDataSetChanged();
                break;
        }

    }
}
