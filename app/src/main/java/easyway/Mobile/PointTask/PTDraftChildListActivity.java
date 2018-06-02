package easyway.Mobile.PointTask;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

/*
 * 重点任务子任务列表（草稿）
 */
public class PTDraftChildListActivity extends ActivityEx {
    private PullRefreshListView mListView;
    private PTAddAdapter mAdapter;
    private ArrayList<ChildMode> mList;
    private boolean isPullRefresh = false;
    private long mSaId = -1;
    private MajorMode mMajor;

    private final int MSG_GETTASK_FAIL = 0;
    private final int MSG_GETTASK_SUCCEED = 1;
    private final int MSG_CANCEL_TASK_SUCC = 2;
    private final int MSG_CANCEL_TASK_FAIL = 3;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MSG_GETTASK_FAIL:
                    if (isPullRefresh) {
                        isPullRefresh = false;
                        mListView.onRefreshComplete();
                    }
                    showToast(errMsg);
                    break;
                case MSG_GETTASK_SUCCEED:
                    if (isPullRefresh) {
                        isPullRefresh = false;
                        mListView.onRefreshComplete();
                    }

                    mList = (ArrayList<ChildMode>) msg.obj;
                    mAdapter.setData(mList);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_CANCEL_TASK_SUCC:
                    showToast(R.string.task_cancel_succeed);
                    getPointTask();
                    break;
                case MSG_CANCEL_TASK_FAIL:
                    showToast(R.string.task_cancel_fail);
                    ShowContinue2Cancel();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pointtask_childlist);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.task_title_childlist);

        mListView = (PullRefreshListView) findViewById(R.id.tasklist);
        mAdapter = new PTAddAdapter(this, mList);
        mListView.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mMajor = (MajorMode) bundle
                    .getSerializable(PTAddActivity.KEY_MAJOR);
        }

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }

        mListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullRefresh = true;
                getPointTask();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int index = arg2 - 1;

                if (mList == null)
                    return;

                if (index < 0 || index > (mList.size() - 1))
                    return;

                showSelectDialog(index);
            }
        });

        Button addBtn = (Button) findViewById(R.id.btnset);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setText(R.string.task_add);
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(PTDraftChildListActivity.this,
                        PTAddChildActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable(PTAddActivity.KEY_MAJOR, mMajor);
                extras.putInt(PTAddActivity.KEY_INDEX,
                        PTAddActivity.INDEX_FROM_EDIT);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPointTask();
    }

    // 获取重点任务信息
    private void getPointTask() {
        showProgressDialog(R.string.GettingData);

        new Thread() {
            public void run() {
                GetPublisherEmphasisTask();
            }
        }.start();
    }

    // 获取重点任务信息
    private void GetPublisherEmphasisTask() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        if (mMajor != null)
            parmValues.put("taskId", String.valueOf(mMajor.TaskId));
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);

        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKACTOR;

        WebServiceManager webServiceManager = new WebServiceManager(this,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdata);
            myhandle.sendEmptyMessage(MSG_GETTASK_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                ArrayList<TaskChild> list = TaskChild.ParseFromString(result);

                Message message = new Message();
                message.what = MSG_GETTASK_SUCCEED;
                message.obj = ParseTaskChild2ChildMode(list);
                myhandle.sendMessage(message);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_GETTASK_FAIL);
                break;
        }
    }

    private ArrayList<ChildMode> ParseTaskChild2ChildMode(
            ArrayList<TaskChild> list) {
        if (list == null)
            return null;

        ArrayList<ChildMode> modelist = new ArrayList<ChildMode>();
        for (TaskChild child : list) {
            ChildMode mode = new ChildMode();
            mode.SaId = child.SaId;
            mode.PositionName = child.PositionName;
            mode.PId = child.PId;
            mode.Workspace = child.Workspace;
            mode.TwId = child.TwId;
            mode.BeginWorkTime = DateUtil.formatDate(child.BeginWorkTime, DateUtil.HH_MM_SS);
            mode.EndWorkTime = DateUtil.formatDate(child.EndWorkTime,DateUtil.HH_MM_SS);
            mode.TaskRemark = child.TaskRemark;
            mode.StaffId = child.StaffId;
            mode.StaffName = child.StaffName;
            mode.AttachList = child.AttachList;

            modelist.add(mode);
        }

        return modelist;
    }

    private void showSelectDialog(final int index) {
        String[] strs = new String[]{getString(R.string.task_delete),
                getString(R.string.task_edit)};

        AlertDialog dlg = new AlertDialog.Builder(PTDraftChildListActivity.this)
                .setTitle("")
                .setItems(strs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 1) { // 编辑
                            Intent intent = new Intent(
                                    PTDraftChildListActivity.this,
                                    PTAddChildActivity.class);
                            Bundle extras = new Bundle();
                            extras.putSerializable(PTAddActivity.KEY_MAJOR,
                                    mMajor);
                            extras.putSerializable(PTAddActivity.KEY_CHILD,
                                    mList.get(index));
                            extras.putInt(PTAddActivity.KEY_INDEX,
                                    PTAddActivity.INDEX_FROM_EDIT);
                            intent.putExtras(extras);
                            startActivity(intent);
                        } else { // 取消
                            LogUtil.i("index -->" + index + "  /  "
                                    + mList.get(index).SaId);
                            mSaId = mList.get(index).SaId;
                            TaskCancel();
                        }
                    }
                }).create();
        dlg.show();
    }

    // 取消任务
    private void TaskCancel() {
        showProgressDialog(R.string.task_notify_canceling);
        new Thread() {
            public void run() {
                HashMap<String, String> paramValues = new HashMap<String, String>();
                paramValues.put("sessionId", Property.SessionId);
                paramValues.put("saId", String.valueOf(mSaId));

                String methodPath = Constant.MP_TASK;
                String methodName = Constant.MN_DELETE_TASKITEM;
                WebServiceManager webServiceManager = new WebServiceManager(
                        getApplicationContext(), methodName, paramValues);
                String result = webServiceManager.OpenConnect(methodPath);

                if (result == null || result.equals("")) {
                    myhandle.sendEmptyMessage(MSG_CANCEL_TASK_FAIL);
                }

                int Code = JsonUtil.GetJsonInt(result, "Code");

                switch (Code) {
                    case Constant.NORMAL:
                        myhandle.sendEmptyMessage(MSG_CANCEL_TASK_SUCC);
                        break;
                    case Constant.EXCEPTION:
                    default:
                        myhandle.sendEmptyMessage(MSG_CANCEL_TASK_FAIL);
                        break;
                }

                return;
            }
        }.start();
    }

    // 重新取消任务
    private void ShowContinue2Cancel() {
        AlertDialog.Builder builder = new Builder(PTDraftChildListActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(false);

        builder.setMessage(R.string.task_cancel_fail);
        // 重新取消任务
        builder.setPositiveButton(R.string.task_cancel_retry,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        TaskCancel();
                    }
                });

        // 取消
        builder.setNegativeButton(R.string.task_cancel_cancel, null);
        builder.create().show();
    }
}
