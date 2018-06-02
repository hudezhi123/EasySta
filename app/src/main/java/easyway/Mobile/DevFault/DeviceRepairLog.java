package easyway.Mobile.DevFault;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.CommonUtils;

public class DeviceRepairLog extends ActivityEx implements OnClickListener {

    private Activity act;
    @BindView(id = R.id.DeviceStatusDescribe)
    private EditText DeviceStatusDescribe;
    @BindView(id = R.id.RepairDescribe)
    private EditText RepairDescribe;
    @BindView(id = R.id.devLogSaveBtn)
    private Button devLogSaveBtn;
    @BindView(id = R.id.devLogCancelBtn)
    private Button devLogCancelBtn;
    //    @BindView(id = R.id.btn_add_pic_log)
//    private Button btnAddPic;
    @BindView(id = R.id.btnReturn)
    private Button btnReturn;
    @BindView(id = R.id.title)
    private TextView title;
//    @BindView(id = R.id.text_path_pic_log)
//    private TextView textPicPath;
    private Uri fileUri;

    private final static int SAVE_DEVREPAIRLOG_SUCCESS = 0;
    private final int SAVE_DEVREPAIRLOG_FAIL = 1;
    private final int NET_ERROR = 2;
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    private final int SAVEDEVISSUEREPAIRIMG_SUCCESS = 4;
    private final int SAVEDEVISSUEREPAIRIMG_FAIL = 5;


    private int dfId;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case SAVE_DEVREPAIRLOG_SUCCESS:
                    closeProgressDialog();
                    String saveStr = (String) msg.obj;
                    showToast(saveStr);
                    DeviceRepairLog.this.finish();
                    break;
                case SAVE_DEVREPAIRLOG_FAIL:
                    showToast("保存失败");
                    break;
                case NET_ERROR:
                    showToast(R.string.ConnectFail);
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
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.act_device_repair_log);
        AnnotateUtil.initBindView(act);
        initView();
    }

    private void initView() {
        title.setText("添加维修日志");
        btnReturn.setOnClickListener(this);
        dfId = getIntent().getIntExtra("dfId", -1);
        devLogSaveBtn.setOnClickListener(this);
        devLogCancelBtn.setOnClickListener(this);
//        btnAddPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnReturn:
                finish();
                break;
            case R.id.devLogSaveBtn:
                String repIssue = DeviceStatusDescribe.getText().toString().trim();
                String fixedRemark = RepairDescribe.getText().toString().trim();
                if (TextUtils.isEmpty(repIssue) || TextUtils.isEmpty(fixedRemark)) {
                    showToast("请检查信息填写是否完整");
                    return;
                }
                SaveDevRepairLog(repIssue, fixedRemark);
                break;
            case R.id.devLogCancelBtn:
                finish();
                break;
//            case R.id.btn_add_pic_log:
//                openCamera();
//                break;
            default:
                break;
        }
    }

    private void SaveDevRepairLog(final String repIssue, final String fixedRemark) {
        showProgressDialog("正在保存设备维修记录");
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(dfId));
                parmValues.put("repIssue", repIssue);
                parmValues.put("fixedRemark", fixedRemark);
                parmValues.put("stationCode", Property.OwnStation.Code);
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_SAVE_DEVREPAIRLOG;
                WebServiceManager webServiceManager = new WebServiceManager(act,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Log.e("zwt", "返回结果" + result);
                Gson gson = new Gson();
                Type type = new TypeToken<Result<String>>() {
                }.getType();
                Result<String> saveDevRep = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (saveDevRep.isMsgType()) {
                        handler.sendMessage(handler.obtainMessage(SAVE_DEVREPAIRLOG_SUCCESS, saveDevRep.getData()));
                    } else {
                        handler.sendEmptyMessage(SAVE_DEVREPAIRLOG_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(NET_ERROR);
                }

            }

            ;
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
//        if (resultCode == RESULT_OK) {
//            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//                final String filePath = fileUri.getPath();
//                textPicPath.setText(filePath);
//                ImgCompress.Compress(filePath, filePath, 800, 600, 80);
//                File f = new File(filePath);
//                if (!f.exists()) {
//                    return;
//                }
//                if (f.isDirectory()) {
//                    return;
//                }
//
//                final ImageView imgView = new ImageView(this);
//                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                imgView.setImageBitmap(bitmap);
//                final String fileStreamString = BitmapUtil.bitmapToBase64(bitmap);
//                new AlertDialog.Builder(this).setTitle("上传图片")
//                        .setIcon(android.R.drawable.ic_dialog_info)
//                        .setView(imgView)
//                        .setPositiveButton("上传", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                uploadImg(filePath, fileStreamString);
//                            }
//                        })
//                        .setNegativeButton("取消", null).show();
//            }
//
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadImg(final String filePath, final String fileStreamString) {
        showProgressDialog("正在上传图片");
        new Thread() {
            @Override
            public void run() {
                super.run();
                File file = new File(filePath);
                String fileName = file.getName();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(dfId));
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
                    handler.sendEmptyMessage(NET_ERROR);
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


}
