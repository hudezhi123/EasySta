package easyway.Mobile.DevFault;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Attach.CancelTask;
import easyway.Mobile.Attach.FileUploadTask;
import easyway.Mobile.Attach.IFileUpload;
import easyway.Mobile.Attach.ITaskCancel;
import easyway.Mobile.Data.DevFaultCate;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.ImgCompress;
import easyway.Mobile.util.JsonUtil;

/**
 * Created by JSC on 2018/1/4.
 */

public class DeviceFaultReportActivity extends ActivityEx implements View.OnClickListener {
    @BindView(id=R.id.et_device_name)
    EditText etDeviceName;
    @BindView(id=R.id.et_device_area)
    EditText etDeviceArea;
    @BindView(id=R.id.et_device_location)
    EditText etDeviceLocation;
    @BindView(id=R.id.bt_fault_desc)
    Button btFaultDesc;
    @BindView(id=R.id.et_fault_desc)
    EditText etFaultDesc;
    @BindView(id=R.id.bt_device_photo)
    Button btDevicePhoto;
    @BindView(id=R.id.lv_photo_list)
    ListView lvPhotoList;
    @BindView(id=R.id.bt_device_report)
    Button btDeviceReport;
    private ArrayList<DevFaultCate> lstParaDesc; // 故障描述
    private long faultId;
    private int selTemplateIndex = 0;
    private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private int DEVICE_CATE_ACTIVITY_REQUEST_CODE = 300;
    private Uri fileUri;

    //	private long devtype;
    private String devcode;
    private long devid = 0;
    private String devname;
    private String devlocation;
    private String TwName;

    private AttachAdapter mAdapter;
    private ArrayList<String> attachList = new ArrayList<String>();


    private final int MSG_PARAM_SUCCEED = 1; // 获取参数成功
    private final int MSG_PARAM_DESC_FAIL = 3; // 获取上报类型失败
    private final int MSG_REPORT_SUCCEED = 4; // 上报成功
    private final int MSG_REPORT_FAIL = 5; // 上报失败
    private final int MSG_ATTACH_COUNT_ERROR = 6; //附件个数大于1
    private final int MSG_DEVFAULT_MESSAGE_EMPTY = 7; //故障描述不能为空

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case 1001:
                    Toast.makeText(DeviceFaultReportActivity.this, "result = " + ((String) msg.obj), Toast.LENGTH_SHORT).show();
                    break;

                case MSG_PARAM_SUCCEED:

                    break;
                case MSG_PARAM_DESC_FAIL:
                    AlertDialog.Builder builder = new AlertDialog.Builder(DeviceFaultReportActivity.this);
                    builder.setTitle(R.string.Prompt);
                    builder.setIcon(R.drawable.error);
                    builder.setCancelable(false);
                    builder.setMessage(R.string.exp_getparam);
                    builder.setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DeviceFaultReportActivity.this.finish();
                                }
                            });

                    if (DeviceFaultReportActivity.this == null || DeviceFaultReportActivity.this.isFinishing()) {
                        builder = null;
                        return;
                    } else {
                        builder.create().show();
                    }

                    break;
                case MSG_REPORT_SUCCEED:
                    AlertDialog.Builder builderRep = new AlertDialog.Builder(DeviceFaultReportActivity.this);
                    builderRep.setTitle(R.string.Prompt);
                    builderRep.setIcon(R.drawable.ok);
                    builderRep.setMessage(R.string.dev_fault_post_success);
                    builderRep.setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (attachList == null
                                            || attachList.size() == 0) {
                                        finish();
                                        return;
                                    }

                                    UploadAttach(attachList);
                                }
                            });
                    if (DeviceFaultReportActivity.this == null || DeviceFaultReportActivity.this.isFinishing()) {
                        builderRep = null;
                        return;
                    } else {
                        builderRep.create().show();
                    }
                    builderRep.create().show();
                    break;
                case MSG_REPORT_FAIL:
                    showErrMsg(DeviceFaultReportActivity.this.errMsg);
                    break;
                case MSG_DEVFAULT_MESSAGE_EMPTY:
                    AlertDialog.Builder builderDev = new AlertDialog.Builder(DeviceFaultReportActivity.this);
                    builderDev.setTitle(R.string.Prompt);
                    builderDev.setIcon(R.drawable.error);
                    builderDev.setCancelable(false);
                    builderDev.setMessage(R.string.exp_devfault_message_empty);
                    builderDev.setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    if (DeviceFaultReportActivity.this == null || DeviceFaultReportActivity.this.isFinishing()) {
                        builderDev = null;
                        return;
                    } else {
                        builderDev.create().show();
                    }
                    builderDev.create().show();
                    break;
                case MSG_ATTACH_COUNT_ERROR:
                    AlertDialog.Builder builderAtt = new AlertDialog.Builder(DeviceFaultReportActivity.this);
                    builderAtt.setTitle(R.string.Prompt);
                    builderAtt.setIcon(R.drawable.error);
                    builderAtt.setCancelable(false);
                    builderAtt.setMessage(R.string.exp_attach_num_error);
                    builderAtt.setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    if (DeviceFaultReportActivity.this == null || DeviceFaultReportActivity.this.isFinishing()) {
                        builderAtt = null;
                        return;
                    } else {
                        builderAtt.create().show();
                    }

                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_fault_report_layout);
        AnnotateUtil.initBindView(this);
        initView();


        new Thread() {
            public void run() {
                getParam();
            }
        }.start();
    }

    private void initView() {
        mAdapter = new AttachAdapter(this);
        lvPhotoList.setAdapter(mAdapter);

        btDevicePhoto.setOnClickListener(this);
        btFaultDesc.setOnClickListener(ShowSelectTemplate());
        btDeviceReport.setOnClickListener(this);


    }

    // 获取设备故障类型
    private void getParam() {

        // 故障描述
        lstParaDesc = DevFaultCate.GetDevFaultCateByCode(DevFaultCate.PARAM_CODE_DESC, DeviceFaultReportActivity.this);
        if (lstParaDesc == null || lstParaDesc.size() == 0) {
            myhandle.sendEmptyMessage(MSG_PARAM_DESC_FAIL);
            return;
        }
        myhandle.sendEmptyMessage(MSG_PARAM_SUCCEED);
    }


    // 拍照
    private void openCamera() {
        boolean hasCarema = CommonUtils.checkCameraHardware(DeviceFaultReportActivity.this);
        if (!hasCarema) {
            AlertDialog.Builder build = new AlertDialog.Builder(DeviceFaultReportActivity.this);

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
        fileUri = CommonUtils.getOutputMediaFileUri(DeviceFaultReportActivity.this,
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
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    private void PostFault() {
        showProgressDialog(R.string.dev_fault_posting);

        new Thread() {
            public void run() {
                PostDevFault();
            }
        }.start();
    }

    private View.OnClickListener ShowSelectTemplate() {
        return new View.OnClickListener() {

            public void onClick(View v) {
                if (lstParaDesc == null)
                    return;
                String[] m = new String[lstParaDesc.size()];
                for (int i = 0; i < lstParaDesc.size(); i++) {
                    m[i] = lstParaDesc.get(i).name.trim();
                }

                AlertDialog.Builder build = new AlertDialog.Builder(
                        DeviceFaultReportActivity.this);

                build.setTitle(R.string.dev_fault_sel_desc);
                build.setSingleChoiceItems(m, selTemplateIndex, selTemplate());
                build.setPositiveButton(R.string.OK, selTemplate());
                build.setNegativeButton(R.string.Cancel, null);
                if (DeviceFaultReportActivity.this == null || DeviceFaultReportActivity.this.isFinishing()) {
                    build = null;
                } else {
                    build.show();
                }

//				startActivityForResult(new Intent(DeviceFaultReportActivity.this,DFCate.class), DEVICE_CATE_ACTIVITY_REQUEST_CODE);
            }
        };
    }

    private DialogInterface.OnClickListener selTemplate() {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which > 0) {
                    selTemplateIndex = which;
                } else {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        if (selTemplateIndex < 0) {
                            return;
                        }

                        btFaultDesc
                                .setText(lstParaDesc.get(selTemplateIndex).name);
                    }
                }

            }
        };
    }

    // 附件上传
    private void UploadAttach(ArrayList<String> list) {
        if (list.size() == 0) {
            return;
        }

        IFileUpload iupload = new IFileUpload() {
            @Override
            public void OnUploadEnd(int ret, ArrayList<String> lstFail) {
                switch (ret) {
                    case FileUploadTask.RET_UPLOAD_SUCCEED:
                        DeviceFaultReportActivity.this.finish();
                        break;
                    case FileUploadTask.RET_UPLOAD_FAIL:
                        ShowContinue2Upload(lstFail);
                        break;
                    case FileUploadTask.RET_FILE_NOT_EXIST:
                        showToast(R.string.exp_attachnotexist);
                        break;
                    default:
                        break;
                }
            }
        };

        FileUploadTask upload = new FileUploadTask(DeviceFaultReportActivity.this, list,
                faultId, FileUploadTask.CATEGORY_DEVFAULT, iupload);
        upload.execute();
    }

    public void ShowContinue2Upload(final ArrayList<String> list) {
        if (null == list || list.size() == 0) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceFaultReportActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(false);

        builder.setMessage(R.string.dev_fault_attach_Upload_continue);
        // 文件重传
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        UploadAttach(list);
                    }
                });

        // 取消上报
        builder.setNeutralButton(R.string.canclelivecase,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CancleDevReport();
                    }
                });

        // 取消
        builder.setNegativeButton(R.string.Cancel, null);
        builder.create().show();
    }

    // 取消上报
    private void CancleDevReport() {
        ITaskCancel iCancel = new ITaskCancel() {
            @Override
            public void OnCancelEnd(int ret) {
                switch (ret) {
                    case CancelTask.RET_CANCEL_FAIL:
                        ShowContinue2Cancel();
                        break;
                    case CancelTask.RET_CANCEL_SUCCEED:
                        showToast(R.string.dev_fault_cancle_succeed);
                        DeviceFaultReportActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }
        };

        CancelTask cancel = new CancelTask(DeviceFaultReportActivity.this, faultId,
                FileUploadTask.CATEGORY_DEVFAULT, iCancel);
        cancel.execute();
    }

    // 取消上报重试
    public void ShowContinue2Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceFaultReportActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(false);

        builder.setMessage(R.string.dev_fault_cancle_fail);
        // 重试
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        CancleDevReport();
                    }
                });

        // 取消
        builder.setNegativeButton(R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DeviceFaultReportActivity.this.finish();
                    }
                });
        builder.create().show();
    }

    // 情况上报
    private void PostDevFault() {
        if (etFaultDesc.getText().toString().equals("")) {
            errMsg = getString(R.string.exp_devfault);
            myhandle.sendEmptyMessage(MSG_DEVFAULT_MESSAGE_EMPTY);
            return;
        }

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("devId", String.valueOf(devid));
        parmValues.put("faultContent",
                URLEncoder.encode(etFaultDesc.getText().toString().trim()));

        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_ADD_DEV_FAULT;
        WebServiceManager webServiceManager = new WebServiceManager(
                DeviceFaultReportActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_devfault);
            myhandle.sendEmptyMessage(MSG_REPORT_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil
                        .GetJsonArray(result, "Data");
                if (jsonArray == null || jsonArray.length() <= 0) {
                    errMsg = getString(R.string.exp_devfault);
                    myhandle.sendEmptyMessage(MSG_REPORT_FAIL);
                } else {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
                    faultId = JsonUtil.GetJsonObjLongValue(jsonObj, "ID");
                    if (faultId == 0) {
                        errMsg = getString(R.string.exp_devfault);
                        myhandle.sendEmptyMessage(MSG_REPORT_FAIL);
                    } else {
                        myhandle.sendEmptyMessage(MSG_REPORT_SUCCEED);
                    }
                }
                break;
            case Constant.EXCEPTION:
                errMsg = "故障上报异常";
                myhandle.sendEmptyMessage(MSG_REPORT_FAIL);
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_REPORT_FAIL);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                String filePath = fileUri.getPath();
                ImgCompress.Compress(filePath, filePath, 800, 600, 80);
                File f = new File(filePath);
                if (!f.exists()) {
                    return;
                }
                if (f.isDirectory()) {
                    return;
                }
                if (attachList.contains(filePath)) {
                    return;
                }

                AddAttachList(filePath);
            } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) { // 录像
                String filePath = fileUri.getPath();
                File f = new File(filePath);
                if (!f.exists()) {
                    return;
                }
                if (f.isDirectory()) {
                    return;
                }
                if (attachList.contains(filePath)) {
                    return;
                }

                AddAttachList(filePath);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // 附件adapter
    class AttachAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        public AttachAdapter(Context context) {
            this.mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            if (attachList == null)
                return 0;
            else
                return attachList.size();
        }

        public String getItem(int position) {
            if (attachList == null)
                return null;
            else
                return attachList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.livecase_report_item,
                        null);
                holder = new ViewHolder();
                holder.AttachId = (TextView) convertView
                        .findViewById(R.id.annexId);
                holder.AttachUrl = (TextView) convertView
                        .findViewById(R.id.annexUrl);
                holder.btnDelete = (Button) convertView
                        .findViewById(R.id.btnDelete);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String filePath = getItem(position);
            String fileName = CommonUtils.getFileNameFromPath(filePath);
            holder.AttachId.setText(String.valueOf(position + 1) + ":");
            holder.AttachUrl.setText(fileName);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachList.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView AttachId;
            TextView AttachUrl;
            Button btnDelete;
        }
    }

    /**
     * 添加附件到list
     */
    private void AddAttachList(String attachUrl) {
        if (!attachList.contains(attachUrl)) {
            attachList.add(0, attachUrl);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_device_photo) {
            if (mAdapter.getCount() > 0) {
                myhandle.sendEmptyMessage(MSG_ATTACH_COUNT_ERROR);
                return;
            }
        }
        switch (v.getId()) {
            case R.id.bt_device_photo:
                openCamera();
                break;
            case R.id.bt_device_report:
                PostFault();
                break;
            default:
                break;
        }
    }
}
