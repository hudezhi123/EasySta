package easyway.Mobile.DevFault;

/**
 * 已报障 维修处理
 */

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.DFMainData;
import easyway.Mobile.Data.GetDevInGroupResult;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.BitmapUtil;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.DragImageView;
import easyway.Mobile.util.ImgCompress;
import easyway.Mobile.util.ViewUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DRHandlerActivity extends ActivityEx implements OnClickListener {

    private Activity act;
    @BindView(id = R.id.GroupNumber)
    private TextView GroupNumber;
    @BindView(id = R.id.isGroup)
    private TextView isGroup;
    @BindView(id = R.id.ShowGroup)
    private Button ShowGroup;
    @BindView(id = R.id.Number)
    private TextView Number;
    @BindView(id = R.id.Name)
    private TextView Name;
    @BindView(id = R.id.breakdownContent)
    private TextView breakdownContent;
    @BindView(id = R.id.ShowExceptionImage)
    private Button ShowExceptionImage;
    @BindView(id = R.id.ExceptionFromImage)
    private TextView ExceptionFromImage;
    @BindView(id = R.id.BreakdownDescribe)
    private TextView BreakdownDescribe;
    @BindView(id = R.id.UpBreakdownPeopleName)
    private TextView UpBreakdownPeopleName;
    @BindView(id = R.id.UpBreakdownJob)
    private TextView UpBreakdownJob;
    @BindView(id = R.id.UpBreakdownTime)
    private TextView UpBreakdownTime;
    @BindView(id = R.id.RepairsName)
    private TextView RepairsName;
    @BindView(id = R.id.RepairsTime)
    private TextView RepairsTime;
    @BindView(id = R.id.ConfirmRepairsName)
    private TextView ConfirmRepairsName;
    @BindView(id = R.id.ConfirmRepairsTime)
    private TextView ConfirmRepairsTime;
    @BindView(id = R.id.Status)
    private TextView Status;
    @BindView(id = R.id.StartRepairs)
    private Button StartRepairs;
    @BindView(id = R.id.DamagedParts)
    private Button DamagedParts;
    @BindView(id = R.id.AddRepairLog)
    private Button AddRepairLog;
    @BindView(id = R.id.UseSpare)
    private Button UseSpare;
    @BindView(id = R.id.StopRepair)
    private Button StopRepair;
    @BindView(id = R.id.ContinueRepair)
    private Button ContinueRepair;

    @BindView(id = R.id.btnReturn)
    private Button btnReturn;
    @BindView(id = R.id.title)
    private TextView title;

    @BindView(id = R.id.RepairsView)
    private LinearLayout RepairsView;
    @BindView(id = R.id.ConfirmView)
    private LinearLayout ConfirmView;
    @BindView(id = R.id.OperationView)
    private LinearLayout OperationView;
    @BindView(id = R.id.OperationView1)
    private LinearLayout OperationView1;

    @BindView(id = R.id.DFUpload)
    private Button DFUpload;
    @BindView(id = R.id.DFMaintenanceConfirm)
    private Button DFMaintenanceConfirm;
    @BindView(id = R.id.DFNotMC)
    private Button DFNotMC;

    @BindView(id = R.id.drHandlerView)
    private LinearLayout drHandlerView;

    @BindView(id = R.id.ShowGroupView)
    private LinearLayout ShowGroupView;
    @BindView(id = R.id.GroupNumView)
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
    private final int SAVEDEVISSUEREPAIRIMG_SUCCESS = 13;
    private final int SAVEDEVISSUEREPAIRIMG_FAIL = 14;

    private int status;

    private PopupWindow popupWindow;

    public static Activity instance;

    private Uri fileUri;
    private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case StartRepairSuccess:
                    Status.setText(getResources().getString(R.string.dev_fault_repairing));
                    Status.setTextColor(getResources().getColor(R.color.green));
                    StartRepairs.setEnabled(false);
                    DamagedParts.setEnabled(true);
                    AddRepairLog.setEnabled(true);
                    UseSpare.setEnabled(true);
                    StopRepair.setEnabled(true);
                    ContinueRepair.setEnabled(false);
                    OperationView.setVisibility(View.VISIBLE);
                    OperationView1.setVisibility(View.GONE);
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
                    StartRepairs.setEnabled(false);
                    DamagedParts.setEnabled(false);
                    AddRepairLog.setEnabled(false);
                    UseSpare.setEnabled(false);
                    StopRepair.setEnabled(false);
                    ContinueRepair.setEnabled(true);
                    OperationView.setVisibility(View.VISIBLE);
                    OperationView1.setVisibility(View.GONE);
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
                    StartRepairs.setEnabled(false);
                    DamagedParts.setEnabled(true);
                    AddRepairLog.setEnabled(true);
                    UseSpare.setEnabled(true);
                    StopRepair.setEnabled(true);
                    ContinueRepair.setEnabled(false);
                    OperationView.setVisibility(View.VISIBLE);
                    OperationView1.setVisibility(View.GONE);
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
                    StartRepairs.setEnabled(false);
                    DamagedParts.setEnabled(true);
                    AddRepairLog.setEnabled(true);
                    UseSpare.setEnabled(true);
                    StopRepair.setEnabled(true);
                    ContinueRepair.setEnabled(false);
                    OperationView.setVisibility(View.VISIBLE);
                    OperationView1.setVisibility(View.GONE);
                    break;
                case DFNotMCFail:
                    closeProgressDialog();
                    showToast("确认未修复失败");
                    break;
                case MCOk:
                    closeProgressDialog();
                    String MCOKStr = (String) msg.obj;
                    showToast(MCOKStr);
                    Status.setText(getResources().getString(R.string.dev_fault_repair_finish));
                    Status.setTextColor(getResources().getColor(R.color.gray));
                    OperationView.setVisibility(View.GONE);
                    OperationView1.setVisibility(View.GONE);
                    break;
                case MCFail:
                    closeProgressDialog();
                    showToast("确认修复失败");
                    break;
                case SAVEDEVISSUEREPAIRIMG_SUCCESS:
                    closeProgressDialog();
                    String saveImgStr = (String) msg.obj;
                    showToast(saveImgStr);
                    break;
                case SAVEDEVISSUEREPAIRIMG_FAIL:
                    closeProgressDialog();
                    showToast("上传图片失败");
                    break;
                case NetError:
                    closeProgressDialog();
                    showToast(R.string.ConnectFail);
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
        setContentView(R.layout.activity_dr_handler);
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
        status = getIntent().getIntExtra("status", -1);
        bean = (DFMainData) getIntent()
                .getSerializableExtra("bean");
        GroupNumber.setText(bean.getGroupId());
        isGroup.setText(bean.isIsGroup() ? "是" : "否");
        Number.setText(bean.getDevCode());
        Name.setText(bean.getDevName());
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
        UseSpare.setOnClickListener(this);
        ContinueRepair.setOnClickListener(this);
        DFUpload.setOnClickListener(this);
        DFMaintenanceConfirm.setOnClickListener(this);
        DFNotMC.setOnClickListener(this);
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
                StartRepairs.setEnabled(true);
                DamagedParts.setEnabled(false);
                AddRepairLog.setEnabled(false);
                UseSpare.setEnabled(false);
                StopRepair.setEnabled(false);
                ContinueRepair.setEnabled(false);
                OperationView.setVisibility(View.VISIBLE);
                OperationView1.setVisibility(View.GONE);
                break;
            case 1:
                SText = getResources().getString(R.string.dev_fault_audit_pass);
                Status.setTextColor(getResources().getColor(R.color.color_3D86C8));
                RepairsView.setVisibility(View.GONE);
                OperationView.setVisibility(View.GONE);
                OperationView1.setVisibility(View.VISIBLE);
                break;
            case 2:
                SText = getResources().getString(R.string.dev_fault_repair_finish);
                Status.setTextColor(getResources().getColor(R.color.gray));
                OperationView.setVisibility(View.GONE);
                OperationView1.setVisibility(View.GONE);
                break;
            case 3:
                SText = getResources().getString(R.string.dev_fault_repairing);
                Status.setTextColor(getResources().getColor(R.color.green));
                StartRepairs.setEnabled(false);
                DamagedParts.setEnabled(true);
                AddRepairLog.setEnabled(true);
                UseSpare.setEnabled(true);
                StopRepair.setEnabled(true);
                ContinueRepair.setEnabled(false);
                ConfirmView.setVisibility(View.GONE);
                OperationView.setVisibility(View.VISIBLE);
                OperationView1.setVisibility(View.GONE);
                break;
            case 4:
                SText = getResources().getString(R.string.dev_fault_stopRepair);
                Status.setTextColor(getResources().getColor(R.color.yellow));
                StartRepairs.setEnabled(false);
                DamagedParts.setEnabled(false);
                AddRepairLog.setEnabled(false);
                UseSpare.setEnabled(false);
                StopRepair.setEnabled(false);
                ContinueRepair.setEnabled(true);
                ConfirmView.setVisibility(View.GONE);
                OperationView.setVisibility(View.VISIBLE);
                OperationView1.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        Status.setText(SText);
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
        popupWindow_view.setOnTouchListener(new OnTouchListener() {
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
            case R.id.ShowGroup:
                getDevInGroup();
                break;
            case R.id.ShowExceptionImage:
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
                openCamera();
                break;
            case R.id.DFMaintenanceConfirm:
                MaintenanceConfirm();
                break;
            case R.id.DFNotMC:
                DontMC();
                break;
            default:
                break;
        }
    }


    //损坏零件入库
    public void DamagedParts(View view) {
        Intent go2 = new Intent(act, DRHarmPartStorageActivity.class);
        go2.putExtra("dfId", bean.getId());
        startActivity(go2);
    }

    //添加设备维修日志
    public void AddRepairLog(View view) {
        Intent go2 = new Intent(act, DeviceRepairLog.class);
        go2.putExtra("dfId", bean.getId());
        startActivity(go2);
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

    // 拍照
    private void openCamera() {
        boolean hasCarema = CommonUtils.checkCameraHardware(act);
        if (!hasCarema) {
            AlertDialog.Builder build = new AlertDialog.Builder(act);

            build.setTitle(R.string.Prompt);
            build.setIcon(R.drawable.information);
            build.setPositiveButton(R.string.OK, null);
            build.setMessage(R.string.NonCarema);
            build.show();
            return;
        }
        // 创建拍照Intent并将控制权返回给调用的程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 创建保存图片的文件
        fileUri = CommonUtils.getOutputMediaFileUri(act,
                CommonUtils.MEDIA_TYPE_IMAGE);
        if (fileUri == null) {
            return;
        }

        // 设置图片文件名
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // 启动图像捕获Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                final String filePath = fileUri.getPath();
                ImgCompress.Compress(filePath, filePath, 800, 600, 80);
                File f = new File(filePath);
                if (!f.exists()) {
                    return;
                }
                if (f.isDirectory()) {
                    return;
                }

                final ImageView imgView = new ImageView(this);
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                imgView.setImageBitmap(bitmap);
                final String fileStreamString = BitmapUtil.bitmapToBase64(bitmap);
                new AlertDialog.Builder(this).setTitle("上传图片")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(imgView)
                        .setPositiveButton("上传", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                uploadImg(filePath, fileStreamString);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImg(final String filePath, final String fileStreamString) {
        showProgressDialog("正在上传图片");
        new Thread() {
            @Override
            public void run() {
                super.run();
//					FileAccessEx fileAccessEx;
//					try {
//						fileAccessEx = new FileAccessEx(filePath, 0);
//						Long fileLength = fileAccessEx.getFileLength();
//						byte[] buffer = new byte[FileAccessEx.PIECE_LENGHT];
//						FileAccessEx.Detail detail;
//						long nRead = 0l;
//						long nStart = 0l;
//						boolean completed = false;
//
                File file = new File(filePath);
                String fileName = file.getName();
//						while (nStart < fileLength) {
//							detail = fileAccessEx.getContent(nStart);
//							nRead = detail.length;
//							buffer = detail.b;
//
//							nStart += nRead;
//
//							if (nStart < fileLength) {
//								completed = false;
//							} else {
//								completed = true;
//							}
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(bean.getId()));
                parmValues.put("fileName", fileName);
                parmValues.put("fileStreamString", fileStreamString);
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_SAVEDEVISSUEREPAIRIMAGE;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Log.e("zwt", "返回结果" + result);
//							Gson gson = new Gson();
//							Type type = new TypeToken<ResultForListData<GetDevInGroupResult>>(){}.getType();
//							ResultForListData<GetDevInGroupResult> devsInGroup = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (result.equals("true")) {
                        handler.sendMessage(handler.obtainMessage(SAVEDEVISSUEREPAIRIMG_SUCCESS, "上传图片成功"));
                    } else {
                        handler.sendEmptyMessage(SAVEDEVISSUEREPAIRIMG_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(NetError);
                }
//							if(completed)
//								handler.sendMessage(handler.obtainMessage(SAVEDEVISSUEREPAIRIMG_SUCCESS, "上传图片成功"));

//						}
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
            }

            ;
        }.start();


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
                Log.e("zwt", "返回结果" + result);
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
    public void StartRepairs(View view) {
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
                Log.e("zwt", "返回结果" + result);
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
                Log.e("zwt", "返回结果" + result);
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

    private void MaintenanceConfirm() {
        showProgressDialog("确认修复");
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(bean.getId()));
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_GET_ok_FAULT;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Gson gson = new Gson();
                Type type = new TypeToken<Result<String>>() {
                }.getType();
                Result<String> ServerData = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (ServerData.isMsgType()) {
                        handler.sendMessage(handler.obtainMessage(MCOk, ServerData.getData()));
                    } else {
                        handler.sendEmptyMessage(MCFail);
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
