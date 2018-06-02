package easyway.Mobile.DevFault;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Data.DFMainData;
import easyway.Mobile.Data.FinishRepairResult;
import easyway.Mobile.Data.GetDevInGroupResult;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.DragImageView;
import easyway.Mobile.util.ViewUtil;

public class SYBDRHandlerActivity extends ActivityEx implements View.OnClickListener {

    private Activity act;
    @BindView(id = R.id.syb_GroupNumber)
    private TextView GroupNumber;
    @BindView(id = R.id.syb_isGroup)
    private TextView isGroup;
    @BindView(id = R.id.syb_ShowGroup)
    private Button ShowGroup;
    @BindView(id = R.id.syb_Number)
    private TextView Number;
    @BindView(id = R.id.syb_Name)
    private TextView Name;

    @BindView(id = R.id.syb_devLocation)
    private TextView devLocation;

    @BindView(id = R.id.syb_twName)
    private TextView twName;

    @BindView(id = R.id.syb_breakdownContent)
    private TextView breakdownContent;
    @BindView(id = R.id.syb_ShowExceptionImage)
    private Button ShowExceptionImage;
    @BindView(id = R.id.syb_ExceptionFromImage)
    private TextView ExceptionFromImage;
    @BindView(id = R.id.syb_BreakdownDescribe)
    private TextView BreakdownDescribe;
    @BindView(id = R.id.syb_UpBreakdownPeopleName)
    private TextView UpBreakdownPeopleName;
    @BindView(id = R.id.syb_UpBreakdownJob)
    private TextView UpBreakdownJob;
    @BindView(id = R.id.syb_UpBreakdownTime)
    private TextView UpBreakdownTime;
    @BindView(id = R.id.syb_RepairsName)
    private TextView RepairsName;
    @BindView(id = R.id.syb_RepairsTime)
    private TextView RepairsTime;
    @BindView(id = R.id.syb_ConfirmRepairsName)
    private TextView ConfirmRepairsName;
    @BindView(id = R.id.syb_ConfirmRepairsTime)
    private TextView ConfirmRepairsTime;
    @BindView(id = R.id.syb_Status)
    private TextView Status;

    @BindView(id = R.id.btn_start_repair_syb)
    private Button startRepair;
    @BindView(id = R.id.btn_end_repair_syb)
    private Button finishRepair;

    @BindView(id = R.id.btnReturn)
    private Button btnReturn;
    @BindView(id = R.id.title)
    private TextView title;

    @BindView(id = R.id.syb_RepairsView)
    private LinearLayout RepairsView;
    @BindView(id = R.id.syb_ConfirmView)
    private LinearLayout ConfirmView;

    @BindView(id = R.id.syb_drHandlerView)
    private LinearLayout drHandlerView;

    @BindView(id = R.id.syb_ShowGroupView)
    private LinearLayout ShowGroupView;
    @BindView(id = R.id.syb_GroupNumView)
    private LinearLayout GroupNumView;

    private DFMainData bean;

    private ArrayList<GetDevInGroupResult> devsInGroupList = new ArrayList<GetDevInGroupResult>();

    private final int StartRepairSuccess = 1;
    private final int StartRepairFail = 2;
    private final int NetError = 3;

    private final int GET_DEVINGROUP_SUCCESS = 4;
    private final int USPENDREPAIRDEVFAULT_SUCCESS = 5;
    private final int USPENDREPAIRDEVFAULT_FAIL = 6;
    private final int RESUMEREPAIRDEVFAULT_SUCCESS = 7;
    private final int RESUMEREPAIRDEVFAULT_FAIL = 8;
    private final int DFNotMCSucess = 9;
    private final int DFNotMCFail = 10;
    private final int MCOk = 11;
    private final int MCFail = 12;
    private final int FINISH_REPAIR_SUCCESS = 13;
    private final int FINISH_REPAIR_ERROR = 14;
    private final int FINISH_REPAIR_FAIL = 15;

    private int status;

    private PopupWindow popupWindow;

    public static Activity instance;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case StartRepairSuccess:
                    Status.setText(getResources().getString(R.string.dev_fault_repairing));
                    Status.setTextColor(getResources().getColor(R.color.green));
                    setBtnBg(startRepair, false);
                    setBtnBg(finishRepair, true);
                    break;
                case GET_DEVINGROUP_SUCCESS:
                    closeProgressDialog();
                    devsInGroupList = (ArrayList<GetDevInGroupResult>) msg.obj;
                    showPopView(devsInGroupList, 1);
                    break;
                case USPENDREPAIRDEVFAULT_SUCCESS:
                    Status.setText(getResources().getString(R.string.dev_fault_stopRepair));
                    Status.setTextColor(getResources().getColor(R.color.yellow));
                    closeProgressDialog();
                    String suspendStr = (String) msg.obj;
                    showToast(suspendStr);
                    break;
                case USPENDREPAIRDEVFAULT_FAIL:
                    closeProgressDialog();
                    showToast("中止维修失败");
                    break;
                case RESUMEREPAIRDEVFAULT_SUCCESS:
                    closeProgressDialog();
                    Status.setText(getResources().getString(R.string.dev_fault_repairing));
                    Status.setTextColor(getResources().getColor(R.color.green));
                    String resumeStr = (String) msg.obj;
                    showToast(resumeStr);
                    break;
                case RESUMEREPAIRDEVFAULT_FAIL:
                    closeProgressDialog();
                    showToast("继续维修失败");
                    break;
                case StartRepairFail:
                    closeProgressDialog();
                    showToast(R.string.GetDataFail);
                    break;
                case DFNotMCSucess:
                    closeProgressDialog();
                    String notMCStr = (String) msg.obj;
                    showToast(notMCStr);
                    Status.setText(getResources().getString(R.string.dev_fault_repairing));
                    Status.setTextColor(getResources().getColor(R.color.green));

                    break;
                case DFNotMCFail:
                    closeProgressDialog();
                    showToast("确认未修复失败");
                    break;
                case MCOk:

                    break;
                case MCFail:
                    closeProgressDialog();
                    showToast("确认修复失败");
                    break;
                case NetError:
                    closeProgressDialog();
                    showToast(R.string.ConnectFail);
                    break;
                case FINISH_REPAIR_ERROR:
                    showToast((String) msg.obj);
                    break;
                case FINISH_REPAIR_FAIL:
                    showToast((String) msg.obj);
                    break;
                case FINISH_REPAIR_SUCCESS:
                    closeProgressDialog();
                    showToast((String) msg.obj);
                    Status.setText(getResources().getString(R.string.dev_fault_audit_pass));
                    Status.setTextColor(getResources().getColor(R.color.gray));
                    setBtnBg(finishRepair, false);
                    SYBDRHandlerActivity.this.finish();
//                    AddRepairLog();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        instance = this;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        setContentView(R.layout.activity_sybdrhandler);
        AnnotateUtil.initBindView(act);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        title.setText("维修处理");
        btnReturn.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        status = intent.getIntExtra("status", -1);
        bean = (DFMainData) intent.getSerializableExtra("bean");
        if (bean == null) {
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            return;
        }
        GroupNumber.setText(bean.getGroupId());
        isGroup.setText(bean.isIsGroup() ? "是" : "否");
        Number.setText(bean.getDevCode());
        Name.setText(bean.getDevName());
        devLocation.setText(bean.getLocation());
        twName.setText(bean.getTwName());
        breakdownContent.setText(bean.getFaultContent());
        ExceptionFromImage.setText(bean.getDevIssueImgFrom());
        BreakdownDescribe.setText(bean.getRemark() == null ? "" : bean.getRemark());
        UpBreakdownPeopleName.setText(bean.getReporteder());
        UpBreakdownJob.setText(bean.getRepStaffType());
        UpBreakdownTime.setText(bean.getReportedTime());
        RepairsName.setText(bean.getFixStaffName() == null ? "" : bean.getFixStaffName());
        RepairsTime.setText(bean.getFixTime() == null ? "" : bean.getFixTime());
        ConfirmRepairsName.setText(bean.getConfirmStaffName() == null ? "" : bean.getConfirmStaffName());
        ConfirmRepairsTime.setText(bean.getConfirmTime() == null ? "" : bean.getConfirmTime());
        ShowGroup.setOnClickListener(this);
        ShowExceptionImage.setOnClickListener(this);
        String url = bean.getAppendixUrl();
        int status = bean.getAppStatus();
        String SText = "";
        if (!bean.isIsGroup()) {
            GroupNumView.setVisibility(View.GONE);
            ShowGroupView.setVisibility(View.GONE);
        }
        switch (status) {
            case 0:
                SText = getResources().getString(R.string.dev_fault_summit_audit);
                Status.setTextColor(getResources().getColor(R.color.red));
                ConfirmView.setVisibility(View.GONE);
                RepairsView.setVisibility(View.GONE);
                setBtnBg(startRepair, true);
                setBtnBg(finishRepair, false);
                break;
            case 1:
                SText = getResources().getString(R.string.dev_fault_audit_pass);
                Status.setTextColor(getResources().getColor(R.color.color_3D86C8));
                RepairsView.setVisibility(View.GONE);
                break;
            case 2:
                SText = getResources().getString(R.string.dev_fault_repair_finish);
                Status.setTextColor(getResources().getColor(R.color.gray));
                setBtnBg(startRepair, false);
                setBtnBg(finishRepair, false);
                break;
            case 3:
                SText = getResources().getString(R.string.dev_fault_repairing);
                Status.setTextColor(getResources().getColor(R.color.green));
                ConfirmView.setVisibility(View.GONE);
                setBtnBg(startRepair, false);
                setBtnBg(finishRepair, true);
                break;
            case 4:
                SText = getResources().getString(R.string.dev_fault_stopRepair);
                Status.setTextColor(getResources().getColor(R.color.yellow));
                ConfirmView.setVisibility(View.GONE);
                break;
            default:
                setBtnBg(startRepair, true);
                setBtnBg(finishRepair, false);
                break;
        }
        startRepair.setOnClickListener(this);
        finishRepair.setOnClickListener(this);
        Status.setText(SText);
    }

    private void setBtnBg(Button btnView, boolean flag) {
        btnView.setEnabled(flag);
        btnView.setClickable(flag);
        if (flag) {
            btnView.setTextColor(getResources().getColor(R.color.black));
            btnView.setAlpha(1.0f);
        } else {
            btnView.setTextColor(getResources().getColor(R.color.gray));
            btnView.setAlpha(0.9f);
        }
    }

    private void showPopView(ArrayList<GetDevInGroupResult> devList, int type) {
        ViewUtil.backgroundAlpha(act, 0.5f);
        View popupWindow_view = null;
        switch (type) {
            case 1:
                // 获取自定义布局文件activity_popupwindow_left.xml的视图
                popupWindow_view = getLayoutInflater().inflate(R.layout.device_list, null, false);
                // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
                ListView device_List = (ListView) popupWindow_view.findViewById(R.id.device_List);
                if (devList == null || devList.size() <= 0) {
                    showToast(" 该组没有其他设备！");
                    ViewUtil.backgroundAlpha(act, 1f);
                    return;
                }
                DeviceInGroupApter adapter = new DeviceInGroupApter(act, devList);
                device_List.setAdapter(adapter);
                break;
            case 2:
                popupWindow_view = getLayoutInflater().inflate(R.layout.img_showpop, null, false);
                DragImageView imageView = (DragImageView) popupWindow_view.findViewById(R.id.checkExpImg);
                String url = bean.getAppendixUrl();
                if (url == null || url.equals("")) {
                    showToast("图片地址为空！");
                    ViewUtil.backgroundAlpha(act, 1f);
                    return;
                }
                url = url.replaceAll("http://192.168.57.65:8005/", CommonFunc.GetServer(act));
                ImageLoader.getInstance().displayImage(url, imageView);
                break;
            default:
                break;
        }

        popupWindow = new PopupWindow(popupWindow_view, 600, 600, true);
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.AnimationFade);
        // 这里是位置显示方式,在屏幕的左侧
        popupWindow.showAtLocation(drHandlerView, Gravity.CENTER, 0, 0);
        // 点击其他地方消失
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                    ViewUtil.backgroundAlpha(act, 1f);
                }
                return false;
            }
        });


    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnReturn:
                finish();
                break;
            case R.id.syb_ShowGroup:
                getDevInGroup();
                break;
            case R.id.syb_ShowExceptionImage:
                showPopView(null, 2);
                break;
            case R.id.UseSpare:
                Intent spareIntent = new Intent(act, SparesActivity.class);
                spareIntent.putExtra("dfId", bean.getId());
                startActivity(spareIntent);
                break;
            case R.id.ContinueRepair:
                ContinueRepair();
                break;
            case R.id.DFUpload:

                break;
            case R.id.DFMaintenanceConfirm:

                break;
            case R.id.DFNotMC:
                DontMC();
                break;
            case R.id.btn_start_repair_syb:
                StartRepairs();
                break;
            case R.id.btn_end_repair_syb:
                getRepairData();
                break;
            default:
                break;
        }
    }


    private void getRepairData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // showProgressDialog("数据加载中");
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", bean.getId() + "");
                parmValues.put("dspIds", bean.getDevId() + "");
                parmValues.put("RepIssue", bean.getFaultContent());
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_GET_Repair_FAULT;
                WebServiceManager webServiceManager = new WebServiceManager(
                        SYBDRHandlerActivity.this, methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Gson gson = new Gson();
                FinishRepairResult finishRepair = gson.fromJson(result, FinishRepairResult.class);
                Message msg = handler.obtainMessage();
                if (finishRepair == null) {
                    msg.obj = "提交失敗，未完成！";
                    msg.what = FINISH_REPAIR_FAIL;
                } else {
                    msg.obj = finishRepair.getData();
                    if (finishRepair.isMsgType()) {
                        msg.what = FINISH_REPAIR_SUCCESS;
                    } else {
                        msg.what = FINISH_REPAIR_FAIL;

                    }
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    //损坏零件入库
    public void DamagedParts(View view) {
        Intent go2 = new Intent(act, DRHarmPartStorageActivity.class);
        go2.putExtra("dfId", bean.getId());
        startActivity(go2);
    }

    //添加设备维修日志
    private void AddRepairLog() {
        Intent go2 = new Intent(act, DeviceRepairLog.class);
        go2.putExtra("dfId", bean.getId());
        startActivity(go2);
        this.finish();
    }

    //中止维修
    public void StopRepair(View view) {
//		Intent go2 = new Intent(act,DeviceRepairLog.class);
//		startActivity(go2);
        final EditText et = new EditText(this);
        et.setHint("中止维修原因");
        new AlertDialog.Builder(this).setTitle("中止维修")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (TextUtils.isEmpty(input)) {
                            showToast("中止维修原因不能为空");
                        } else {
                            SuspendRepair(input);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }


    private void getDevInGroup() {
        showProgressDialog("正在获取设备");
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("groupId", bean.getGroupId());
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_GET_DEVINGROUP;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Gson gson = new Gson();
                Type type = new TypeToken<ResultForListData<GetDevInGroupResult>>() {
                }.getType();
                ResultForListData<GetDevInGroupResult> devsInGroup = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (devsInGroup.isMsgType()) {
                        handler.sendMessage(handler.obtainMessage(GET_DEVINGROUP_SUCCESS, devsInGroup.getData()));
                    } else {
                        handler.sendEmptyMessage(StartRepairFail);
                    }
                } else {
                    handler.sendEmptyMessage(NetError);
                }

            }

            ;
        }.start();
    }

    //开始维修
    public void StartRepairs() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(bean.getId()));
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_BEGINREPAIRDEVFAULT;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Gson gson = new Gson();
                Type type = new TypeToken<Result<String>>() {
                }.getType();
                Result<String> ServerData = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (ServerData.isMsgType()) {
                        handler.sendMessage(handler.obtainMessage(StartRepairSuccess, ServerData.getData()));
                    } else {
                        handler.sendEmptyMessage(StartRepairFail);
                    }
                } else {
                    handler.sendEmptyMessage(NetError);
                }

            }

            ;
        }.start();
    }

    public void SuspendRepair(final String remark) {
        showProgressDialog("中止维修");
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(bean.getId()));
                parmValues.put("remark", remark);
                parmValues.put("stationCode", Property.OwnStation.Code);
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_SUSPENDREPAIRDEVFAULT;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Log.e("zwt", "返回结果" + result);
                Gson gson = new Gson();
                Type type = new TypeToken<Result<String>>() {
                }.getType();
                Result<String> ServerData = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (ServerData.isMsgType()) {
                        handler.sendMessage(handler.obtainMessage(USPENDREPAIRDEVFAULT_SUCCESS, ServerData.getData()));
                    } else {
                        handler.sendEmptyMessage(USPENDREPAIRDEVFAULT_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(NetError);
                }

            }

            ;
        }.start();
    }


    public void ContinueRepair() {
        showProgressDialog("继续维修");
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(bean.getId()));
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_RESUMEREPAIRDEVFAULT;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Gson gson = new Gson();
                Type type = new TypeToken<Result<String>>() {
                }.getType();
                Result<String> ServerData = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (ServerData.isMsgType()) {
                        handler.sendMessage(handler.obtainMessage(RESUMEREPAIRDEVFAULT_SUCCESS, ServerData.getData()));
                    } else {
                        handler.sendEmptyMessage(RESUMEREPAIRDEVFAULT_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(NetError);
                }

            }

            ;
        }.start();
    }


    private void DontMC() {
        showProgressDialog("确认未修复");
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(bean.getId()));
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_NotConfirmDevFault;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                if (result != null || result.length() > 1) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Result<String>>() {
                    }.getType();
                    Result<String> ServerData = gson.fromJson(result, type);
                    Log.e("zwt", "返回结果" + ServerData);
                    if (ServerData.isMsgType()) {
                        handler.sendMessage(handler.obtainMessage(DFNotMCSucess, ServerData.getData()));
                    } else {
                        handler.sendEmptyMessage(DFNotMCFail);
                    }
                } else {
                    handler.sendEmptyMessage(NetError);
                }
            }

            ;
        }.start();
    }

}
