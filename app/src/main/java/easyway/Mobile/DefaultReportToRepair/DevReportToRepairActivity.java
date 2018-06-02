package easyway.Mobile.DefaultReportToRepair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Attach.CancelTask;
import easyway.Mobile.Attach.ITaskCancel;
import easyway.Mobile.Data.ResultCode;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.ImgCompress;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.ResponseResultUtils;

public class DevReportToRepairActivity extends ActivityEx implements View.OnClickListener {

    private ExtAudioRecorder extAudioRecorder;
    private String reportType = "";
    private AttachAdapter mAdapter;

    private ArrayList<String> lstParaType; // 设备类型
    private ArrayList<String> attachList = new ArrayList<String>(); // 上传照片、视频、音频的地址列表
    private ListView mListView;

    private int selTemplateIndex = 0;

    private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100,
            CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Uri fileUri;

    private String repairId;
    private Button btnRecordAudio;
    private Button btnFMOpenVideo;
    private Button btnFMOpenCamera;
    private Button btnReport;
    private EditText txFaultContent;
    private Button btnPickupDesc;                                    // 上报类型
    private EditText editPosition;

    private PopupWindow mSoundRecorderWindow;        // 按下录音提示窗口
    private ImageView mMicImageView;                            // 录制音频时麦克风的Image
    private boolean mProgressBarEable = true;                // 判断是否停止线程
    private int mCurrentVoice;                                        // 获取当前音量

    private final int MSG_SOUND_AMPLITUDE = 100;        // 音量变化
    private final int MSG_PARAM_TYPE_FAIL = 1;                 // 获取参数成功
    private final int MSG_PARAM_TYPE_SUCCEES = 2;                // 获取上报类型失败
    private final int MSG_REPORT_SUCCEED = 3;                // 上报成功
    private final int MSG_REPORT_FAIL = 4;                    // 上报失败

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MSG_SOUND_AMPLITUDE:        // 音量变化
                    int volume = (Integer) msg.obj;
                    if (volume <= 100) {
                        mMicImageView.setBackgroundResource(R.drawable.mic_one);
                    } else if (100 < volume && volume <= 200) {
                        mMicImageView.setBackgroundResource(R.drawable.mic_two);
                    } else if (200 < volume && volume <= 300) {
                        mMicImageView.setBackgroundResource(R.drawable.mic_three);
                    } else if (300 < volume && volume <= 500) {
                        mMicImageView.setBackgroundResource(R.drawable.mic_four);
                    }
                    break;
                case MSG_PARAM_TYPE_FAIL:            // 获取上报类型失败
                    AlertDialog.Builder builder = new AlertDialog.Builder(DevReportToRepairActivity.this);
                    builder.setTitle(R.string.Prompt);
                    builder.setIcon(R.drawable.error);
                    builder.setCancelable(false);
                    builder.setMessage(R.string.exp_getparam);
                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DevReportToRepairActivity.this.finish();
                        }
                    });
                    builder.create().show();
                    break;
                case MSG_PARAM_TYPE_SUCCEES:
                    String types = (String) msg.obj;
                    lstParaType = new ArrayList<>();
                    String[] devices = types.split(",");
                    for (int i = 0; i < devices.length; i++) {
                        lstParaType.add(devices[i]);
                    }
                    btnPickupDesc.setOnClickListener(ShowSelectTemplate());
                    break;
                case MSG_REPORT_SUCCEED:            // 上报成功
                    repairId = (String) msg.obj;
                    AlertDialog.Builder builderRep = new AlertDialog.Builder(DevReportToRepairActivity.this);
                    builderRep.setTitle(R.string.Prompt);
                    builderRep.setIcon(R.drawable.ok);
                    builderRep.setMessage(R.string.live_case_post_success);
                    builderRep.setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (attachList == null || attachList.size() == 0) {
                                        finish();
                                        return;
                                    }
                                    UploadAttach(attachList);
                                }
                            });
                    builderRep.create().show();
                    break;
                case MSG_REPORT_FAIL:            // 上报失败
                    showErrMsg((String) msg.obj);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_device_report2repair);

        Button searchBtn = (Button) findViewById(R.id.btnset);
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setText(R.string.View);
        searchBtn.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.listview_attach_file);

        btnPickupDesc = (Button) findViewById(R.id.btn_choose_fault_type);
        editPosition = (EditText) findViewById(R.id.edit_device_position_rtr);

        txFaultContent = (EditText) findViewById(R.id.edit_report_content);
        // 录音按钮的事件
        btnRecordAudio = (Button) findViewById(R.id.btn_attach_audio);
        btnRecordAudio.setOnClickListener(this);

        btnFMOpenVideo = (Button) findViewById(R.id.btn_attach_video);
        btnFMOpenVideo.setOnClickListener(this);

        btnReport = (Button) findViewById(R.id.btn_fault_report_repair);
        btnReport.setOnClickListener(this);

        btnFMOpenCamera = (Button) findViewById(R.id.btn_attach_photo);
        btnFMOpenCamera.setOnClickListener(this);

        mAdapter = new AttachAdapter(this);
        mListView.setAdapter(mAdapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_report_to_repair);
        initView();

        new Thread() {
            public void run() {
                getParam();
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        if (extAudioRecorder != null) {
            stopRecord();
            extAudioRecorder = null;
            mProgressBarEable = false;
        }
        super.onDestroy();
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

    // 获取上报级别及类型
    private void getParam() {
        HashMap<String, String> paramValue = new HashMap<>();
        paramValue.put("sessionId", Property.SessionId);
        paramValue.put("StationCode", Property.StationCode);
        String methodName = Constant.GET_REPAIR_TYPES;
        String methodPath = Constant.MP_REPAIR;
        WebServiceManager webServiceManager = new WebServiceManager(
                DevReportToRepairActivity.this, methodName, paramValue);
        String result = webServiceManager.OpenConnect(methodPath);
        Message msg = myhandle.obtainMessage();
        ResponseResultUtils.parseJsonResult(result, msg);
        if (msg.what == ResultCode.CODE_IS_USELESS) {
            msg.what = MSG_PARAM_TYPE_FAIL;
        }
        msg.what = MSG_PARAM_TYPE_SUCCEES;
        myhandle.sendMessage(msg);
    }

    // 故障报修
    private void Post2Server() {
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("deviceType", reportType);
        paramValues.put("devicePosition", position);
        paramValues.put("deviceDesc", remarks);
        paramValues.put("stationCode", Property.StationCode);
        String methodPath = Constant.MP_REPAIR;
        String methodName = Constant.ADD_REPAIR_INFO;
        WebServiceManager webServiceManager = new WebServiceManager(
                DevReportToRepairActivity.this, methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);
        Message msg = myhandle.obtainMessage();
        ResponseResultUtils.parseJsonResult(result, msg);
        ResponseResultUtils.parseJsonResult(result, msg);
        int code = msg.what;
        switch (code) {
            case ResultCode.DATA_IS_STRING:
                msg.what = MSG_REPORT_SUCCEED;
                break;
            case ResultCode.CODE_IS_911:
                break;
        }
        myhandle.sendMessage(msg);
    }

    // 附件上传
    private void UploadAttach(ArrayList<String> list) {
        if (list.size() == 0) {
            return;
        }
        IAttachFileUpload iupload = new IAttachFileUpload() {
            @Override
            public void OnUploadEnd(int ret, ArrayList<String> lstFail) {
                switch (ret) {
                    case AttachUploadTask.RET_UPLOAD_SUCCEED:        // 上传成功
                        DevReportToRepairActivity.this.finish();
                        break;
                    case AttachUploadTask.RET_UPLOAD_FAIL:                // 上传失败
                        ShowContinue2Upload(lstFail);
                        break;
                    case AttachUploadTask.RET_FILE_NOT_EXIST:            // 附件不存在
                        showToast(R.string.exp_attachnotexist);
                        break;
                    default:
                        break;
                }
            }
        };

        AttachUploadTask upload = new AttachUploadTask(DevReportToRepairActivity.this, list,
                repairId + "", AttachUploadTask.CATEGORY_LIVECASE, iupload);
        upload.execute();
    }

    // 取消上报
    private void CancleDevReportToRepairActivity() {
        ITaskCancel iCancel = new ITaskCancel() {
            @Override
            public void OnCancelEnd(int ret) {
                switch (ret) {
                    case CancelTask.RET_CANCEL_FAIL:            // 取消失败
                        ShowContinue2Cancel();
                        break;
                    case CancelTask.RET_CANCEL_SUCCEED:        // 取消成功
                        showToast(R.string.canclelivecasesucceed);
                        DevReportToRepairActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    // 取消上报重试
    public void ShowContinue2Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DevReportToRepairActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(false);

        builder.setMessage(R.string.canclelivecasefail);
        // 重试
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //CancleDevReportToRepairActivity();
                    }
                });

        // 取消
        builder.setNegativeButton(R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DevReportToRepairActivity.this.finish();
                    }
                });
        builder.create().show();
    }

    // 附件重传
    public void ShowContinue2Upload(final ArrayList<String> list) {
        if (null == list || list.size() == 0) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(DevReportToRepairActivity.this);
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
                        CancleDevReportToRepairActivity();
                    }
                });

        // 取消
        builder.setNegativeButton(R.string.Cancel, null);
        builder.create().show();
    }

    // 选择上报类型
    private View.OnClickListener ShowSelectTemplate() {
        return new View.OnClickListener() {

            public void onClick(View v) {
                if (lstParaType == null)
                    return;

                String[] m = new String[lstParaType.size()];
                for (int i = 0; i < lstParaType.size(); i++) {
                    m[i] = lstParaType.get(i);
                }
                AlertDialog.Builder build = new AlertDialog.Builder(
                        DevReportToRepairActivity.this);

                build.setTitle(R.string.lc_sel_type);
                build.setSingleChoiceItems(m, selTemplateIndex, selTemplate());
                build.setPositiveButton(R.string.OK, selTemplate());
                build.setNegativeButton(R.string.Cancel, null);
                build.show();
            }
        };
    }

    // 弹出选择框之后给文本框赋值
    private DialogInterface.OnClickListener selTemplate() {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    selTemplateIndex = which;
                } else {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        if (selTemplateIndex < 0) {
                            return;
                        }

                        if (lstParaType == null || lstParaType.size() <= selTemplateIndex)
                            return;

                        reportType = lstParaType.get(selTemplateIndex);
                        btnPickupDesc.setText(reportType);
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {        // 拍照
                String filePath = fileUri.getPath();
                ImgCompress.Compress(filePath, filePath,
                        800, 600, 80);
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
                IntercomCtrl.open_intercom(DevReportToRepairActivity.this);
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

    /**
     * 初始化popupWindow上的控件
     */
    private void initPopupWindow() {
        mMicImageView = (ImageView) mSoundRecorderWindow.getContentView()
                .findViewById(R.id.img_mic);
    }

    /**
     * 音频子线程 系统音量变化是否会发出广播，按广播接收方式进行处理
     */
    private Runnable mAudioPbRunnable = new Runnable() {
        @Override
        public void run() {
            while (mProgressBarEable) {
                try {
                    mCurrentVoice = extAudioRecorder.getMaxAmplitude();
                } catch (IllegalStateException e) {
                    mCurrentVoice = 0;
                } catch (Exception e) {
                    mCurrentVoice = 0;
                }
                // 得到当前音量
                if (extAudioRecorder != null) {
                    Message msg = new Message();
                    msg.what = MSG_SOUND_AMPLITUDE;
                    msg.obj = mCurrentVoice / 100;
                    myhandle.sendMessage(msg);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }
    };

    private void recordVoice() {
        fileUri = CommonUtils.getOutputMediaFileUri(DevReportToRepairActivity.this,
                CommonUtils.MEDIA_TYPE_AUDIO);
        // 弹出一个麦的View
        mSoundRecorderWindow = new PopupWindow(getLayoutInflater().inflate(
                R.layout.media_ability, null), ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mSoundRecorderWindow.showAtLocation(findViewById(R.id.activity_dev_report_to_repair),
                Gravity.CENTER, 0, 0);
        initPopupWindow();

        if (fileUri != null) {
            extAudioRecorder = ExtAudioRecorder.getInstanse(true); // compressed
            extAudioRecorder.setOutputFile(fileUri.getPath());
            IntercomCtrl.close_intercom(DevReportToRepairActivity.this);
            try {
                extAudioRecorder.prepare();
                extAudioRecorder.start();
                mProgressBarEable = true;
                // 启动线程刷新音量变化
                new Thread(mAudioPbRunnable).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void openMp4() {
        boolean hasCarema = CommonUtils.checkCameraHardware(DevReportToRepairActivity.this);
        if (!hasCarema) {
            AlertDialog.Builder build = new AlertDialog.Builder(DevReportToRepairActivity.this);

            build.setTitle(R.string.Prompt);
            build.setIcon(R.drawable.information);
            build.setPositiveButton(R.string.OK, null);
            build.setMessage(R.string.NonCarema);
            build.show();
            return;
        }
        IntercomCtrl.close_intercom(DevReportToRepairActivity.this);

        // 创建拍照Intent并将控制权返回给调用的程序
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = CommonUtils.getOutputMediaFileUri(DevReportToRepairActivity.this,
                CommonUtils.MEDIA_TYPE_VIDEO);
        if (fileUri == null) {
            return;
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // 启动图像捕获Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    private String remarks;
    private String position;

    private void report() {
        showProgressDialog(R.string.lc_posting);
        remarks = txFaultContent.getText().toString().trim();
        position = editPosition.getText().toString().trim();
        new Thread() {
            public void run() {
                if (!ExitApplication.isBoYuan) {
                    try {
                        Thread.sleep((long) (Math.random() * 5000 + 1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Post2Server();
            }
        }.start();
    }

    private void openCamera() {
        boolean hasCarema = CommonUtils.checkCameraHardware(DevReportToRepairActivity.this);
        if (!hasCarema) {
            AlertDialog.Builder build = new AlertDialog.Builder(DevReportToRepairActivity.this);

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
        fileUri = CommonUtils.getOutputMediaFileUri(DevReportToRepairActivity.this,
                CommonUtils.MEDIA_TYPE_IMAGE);
        if (fileUri == null) {
            return;
        }

        // 设置图片文件名
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // 启动图像捕获Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void stopRecord() {
        // dissmiss 麦View
        if (extAudioRecorder == null)
            return;

        mProgressBarEable = false;

        if (extAudioRecorder.getState().equals(ExtAudioRecorder.State.RECORDING)) {
            IntercomCtrl.open_intercom(DevReportToRepairActivity.this);
            extAudioRecorder.stop();
            extAudioRecorder.release();

            mSoundRecorderWindow.dismiss();
            AddAttachList(fileUri.getPath());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnset: // 查看情况上报列表
                stopRecord();
                Intent intent = new Intent(DevReportToRepairActivity.this, DevReportListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_attach_audio: // 添加语音
                // dissmiss 麦View
                if (mSoundRecorderWindow != null
                        && mSoundRecorderWindow.isShowing()) {
                    stopRecord();
                } else {
                    recordVoice();
                }
                break;
            case R.id.btn_attach_video: // 添加视频
                stopRecord();
                openMp4();
                break;
            case R.id.btn_attach_photo: // 添加照片
                stopRecord();
                openCamera();
                break;
            case R.id.btn_fault_report_repair: // 情况上报
                if (reportType == null || reportType.trim().length() == 0) {
                    showToast(R.string.warnselectreporttype);
                } else {
                    stopRecord();
                    String remarks = txFaultContent.getText().toString().trim();
                    String position = editPosition.getText().toString().trim();
                    if (TextUtils.isEmpty(remarks)) {
                        showToast("上报内容不能为空");
                        return;
                    } else if (TextUtils.isEmpty(position)) {
                        showToast("位置信息不能为空！");
                    }

                    report();
                }
                break;
            default:
                break;
        }
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
            DevReportToRepairActivity.AttachAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.livecase_report_item, null);
                holder = new DevReportToRepairActivity.AttachAdapter.ViewHolder();
                holder.AttachId = (TextView) convertView
                        .findViewById(R.id.annexId);
                holder.AttachUrl = (TextView) convertView
                        .findViewById(R.id.annexUrl);
                holder.btnDelete = (Button) convertView
                        .findViewById(R.id.btnDelete);

                convertView.setTag(holder);
            } else {
                holder = (DevReportToRepairActivity.AttachAdapter.ViewHolder) convertView.getTag();
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
}
