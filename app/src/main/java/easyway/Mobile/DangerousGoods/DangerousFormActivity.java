package easyway.Mobile.DangerousGoods;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.JsonUtil;

public class DangerousFormActivity extends ActivityEx implements View.OnClickListener {

    @BindView(id = R.id.btnset)
    private Button btnAlreadyDone;
    @BindView(id = R.id.spinner_group_name_danger)
    private Spinner group;
    @BindView(id = R.id.btn_date_danger)
    private Button date;
    @BindView(id = R.id.btn_time_danger)
    private Button time;
    @BindView(id = R.id.edit_carrier_danger)
    private EditText carrier;
    @BindView(id = R.id.radiogroup_sex_danger)
    private RadioGroup sexGroup;
    @BindView(id = R.id.edit_connect_method_danger)
    private EditText phoneNumber;
    @BindView(id = R.id.edit_train_no_danger)
    private EditText trainNo;
    @BindView(id = R.id.edit_ticket_no_danger)
    private EditText ticketNo;
    @BindView(id = R.id.edit_address_danger)
    private EditText address;
    @BindView(id = R.id.spinner_cate_name_danger)
    private Spinner cateName;
    @BindView(id = R.id.edit_identity_describ_danger)
    private EditText describe;
    @BindView(id = R.id.edit_count_danger)
    private EditText count;
    @BindView(id = R.id.edit_quality_danger)
    private EditText quality;
    @BindView(id = R.id.edit_catcher_name_danger)
    private EditText catchPeople;
    @BindView(id = R.id.edit_dealt_people_danger)
    private EditText dealtPeople;
    @BindView(id = R.id.edit_addition_danger)
    private EditText addition;
    @BindView(id = R.id.spinner_position_name_danger)
    private Spinner position;
    @BindView(id = R.id.spinner_where_to_go_name_danger)
    private Spinner goWhere;
    @BindView(id = R.id.linear_pic_content_danger)
    private LinearLayout picContent;
    @BindView(id = R.id.btn_submit_danger)
    private Button submit;

    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialot;
    private AlertDialog choosePicDialog;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    private static final int REQUEST_CODE_TAKE_PHOTO = 90;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 91;
    private static final int REQUEST_CODE_CROP_PHOTO = 92;

    private static final int MSG_RESULT_EMPITY = 1;
    private static final int MSG_SAVE_EXCEPTION = 2;
    private static final int MSG_SAVE_SUCCESS = 3;

    private File tempFile;
    private Bitmap bitmap;

    private Context mContext;

    private List<String> picPathList;

    private AddImageAdapter adapter;
    private RecyclerView picListView;
    private LinearLayoutManager linearManager;

    private DangerousObjectResult dangerousObjectResult = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_RESULT_EMPITY:
                    showToast("数据上传失败！");
                    break;
                case MSG_SAVE_EXCEPTION:
                    String msgException = (String) msg.obj;
                    showToast(msgException);
                    break;
                case MSG_SAVE_SUCCESS:
                    String msgSuccess = (String) msg.obj;
                    showToast(msgSuccess);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangerous);
        mContext = this;
        AnnotateUtil.initBindView(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String flags = bundle.getString(Flags.TO_DANGER_FLAG);
            if (Flags.Flag_FROM_DANGERLIST_TO_DANGER.equals(flags)) {
                dangerousObjectResult = (DangerousObjectResult) bundle.getSerializable(Flags.DANGER_NAME);
                initTitleBar(false);
                initListener();
                initView(dangerousObjectResult);
                IsEditableUtils.edibleStateChanged(false, DangerousFormActivity.this);
            } else if (Flags.FLAG_FROM_MAIN_TO_DANGER.equals(flags)) {
                dangerousObjectResult = new DangerousObjectResult();
                dangerousObjectResult.setStationCode(Property.StationCode);
                initTitleBar(true);
                initListener();
                initView();
                IsEditableUtils.edibleStateChanged(true, DangerousFormActivity.this);
            }
        }

        initDialog();
        initRecyclerView();
    }

    //初始化
    private void initTitleBar(boolean isVisible) {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.DangerousModel);
        if (isVisible) {
            btnAlreadyDone.setText(R.string.AlreadySubmit);
            btnAlreadyDone.setVisibility(View.VISIBLE);
            btnAlreadyDone.setOnClickListener(this);
        } else {
            btnAlreadyDone.setVisibility(View.GONE);
        }

    }

    private void initPicChoose() {
//        imgExceptionFirst.setOnClickListener(this);
//        imgDeletePicFirst.setOnClickListener(this);
//        imgExceptionSecond.setOnClickListener(this);
//        imgDeletePicSecond.setOnClickListener(this);
    }

    private void initRecyclerView() {
        picListView = (RecyclerView) findViewById(R.id.recycler_pic_listview);
        picPathList = new ArrayList<>();
        linearManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new AddImageAdapter(this, picPathList);
        picListView.setLayoutManager(linearManager);
        picListView.setAdapter(adapter);
    }

    private void initListener() {
        group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cateName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        position.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        goWhere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_sex_male_danger:
                        dangerousObjectResult.setSex(true);
                        break;
                    case R.id.radio_sex_female_danger:
                        dangerousObjectResult.setSex(false);
                        break;
                }
            }
        });
    }

    private void initView(DangerousObjectResult result) {
        if (result != null) {
            String groupNo = result.getDealGroupNo();
            int index = -1;
            switch (groupNo) {
                case "一班":
                    index = 0;
                    break;
                case "二班":
                    index = 1;
                    break;
                case "三班":
                    index = 2;
                    break;
                case "四班":
                    index = 3;
                    break;
            }
            group.setSelection(index);
            String dateResult = result.getFindDate();
            String part[] = dateResult.split("T");
            String dateStr = part[0];
            String datePart[] = dateStr.split("-");
            mYear = Integer.parseInt(datePart[0]);
            mMonth = Integer.parseInt(datePart[1]);
            mDay = Integer.parseInt(datePart[2]);
            String timeStr = part[1];
            String[] timePart = timeStr.split(":");
            mHour = Integer.parseInt(timePart[0]);
            mMinute = Integer.parseInt(timePart[1]);
            date.setText(dateStr);
            time.setText(timeStr);
            carrier.setText(result.getFullName());
            if (result.isSex()) {
                sexGroup.getChildAt(0).setSelected(true);
            } else {
                sexGroup.getChildAt(1).setSelected(false);
            }
            phoneNumber.setText(result.getContact());
            trainNo.setText(result.getTrainNo());
            ticketNo.setText(result.getTicketNo());
            address.setText(result.getHomeAddr());
            if (!TextUtils.isEmpty(result.getProdName())) {
                String cate = result.getProdName();
                switch (cate) {
                    case "禁止携带":
                        cateName.setSelection(0);
                        break;
                    case "限量携带":
                        cateName.setSelection(1);
                        break;
                }
            }
            describe.setText(result.getProdNameDetail());
            count.setText(result.getProdTotal() + "");
            quality.setText(result.getProdQuity() + "");
            catchPeople.setText(result.getFindStaffName());
            if (!TextUtils.isEmpty(result.getProdGoTo())) {
                String orientation = result.getProdGoTo();
                switch (orientation) {
                    case "收缴":
                        goWhere.setSelection(0);
                        break;
                    case "放弃":
                        goWhere.setSelection(1);
                        break;
                    case "带回":
                        goWhere.setSelection(2);
                        break;
                }
            }
            dealtPeople.setText(result.getDealStaffName());
            addition.setText(result.getRemark());
        }

    }

    private void initView() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        date.setText(formatDate(mYear, mMonth, mDay));
        time.setText(formatTime(mHour, mMinute));
        date.setOnClickListener(this);
        time.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;
            date.setText(formatDate(mYear, mMonth, mDay));
        }
    };
    ;
    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            time.setText(formatTime(mHour, mMinute));
        }
    };
    ;

    private void initDialog() {

        dateDialog = new DatePickerDialog(this, dateListener, mYear, mMonth, mDay);

        timeDialot = new TimePickerDialog(this, timeListener, mHour, mMinute, true);

        AlertDialog.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        choostPhotoAction();
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        takePhotoAction();
                        break;
                }
            }
        };
        choosePicDialog = new AlertDialog.Builder(this)
                .setPositiveButton("图片", listener)
                .setNegativeButton("拍照", listener)
                .setTitle("图片编辑")
                .create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnset:
                Intent intent = new Intent(DangerousFormActivity.this, AlreadySubmitFormActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_date_danger:
                dateDialog.show();
                break;
            case R.id.btn_time_danger:
                timeDialot.show();
                break;
            case R.id.btn_submit_danger:
                String result = getDangerousObject();
                if (!TextUtils.isEmpty(result)) {
                    saveDangerousInfo(result);
                }
                break;
        }
    }

    private void saveDangerousInfo(final String dangerousObjectResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("jsonDangerous", dangerousObjectResult);
                String methodPath = Constant.MP_TASK;
                String methodName = Constant.DANGEROUS_SAVE;
                WebServiceManager webServiceManager = new WebServiceManager(
                        DangerousFormActivity.this, methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                if (TextUtils.isEmpty(result)) {
                    mHandler.sendEmptyMessage(MSG_RESULT_EMPITY);
                }
                Message msg = mHandler.obtainMessage();
                msg.obj = JsonUtil.GetJsonString(result, "Msg");
                boolean MsgType = JsonUtil.GetJsonBoolean(result, "MsgType");
                if (MsgType) {
                    msg.what = MSG_SAVE_SUCCESS;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = MSG_SAVE_EXCEPTION;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }


    private String getDangerousObject() {

        if (isCompleteFill()) {
            String jsonResult = new Gson().toJson(dangerousObjectResult);
            return jsonResult;
        } else {
            return null;
        }
    }

    private boolean isCompleteFill() {
        String groupNumber = (String) group.getSelectedItem();
        String dateString = date.getText().toString();
        String timeString = time.getText().toString();
        String dateResult = dateString + " " + timeString;
        String carrierStr = carrier.getText().toString();
        String phoneStr = phoneNumber.getText().toString();
        String trainNoStr = trainNo.getText().toString();
        String ticketStr = ticketNo.getText().toString();
        String addressStr = address.getText().toString();
        String cateNameStr = (String) cateName.getSelectedItem();
        String cateDescribe = describe.getText().toString();
        String countStr = count.getText().toString();
        String qualityStr = quality.getText().toString();
        String catchNameStr = catchPeople.getText().toString();
        String dealPeopleStr = dealtPeople.getText().toString();
        String positionStr = (String) position.getSelectedItem();
        String whereTogoStr = (String) goWhere.getSelectedItem();
        String remarkStr = addition.getText().toString();
        if (!TextUtils.isEmpty(carrierStr)
                && !TextUtils.isEmpty(phoneStr)
                && !TextUtils.isEmpty(trainNoStr)
                && !TextUtils.isEmpty(addressStr)
                && !TextUtils.isEmpty(cateDescribe)
                && !TextUtils.isEmpty(countStr)
                && !TextUtils.isEmpty(qualityStr)
                && !TextUtils.isEmpty(catchNameStr)
                && !TextUtils.isEmpty(dealPeopleStr)
                && !TextUtils.isEmpty(remarkStr)
                && !TextUtils.isEmpty(ticketStr)
                && !TextUtils.isEmpty(positionStr)) {
            dangerousObjectResult.setDealGroupNo(groupNumber);
            dangerousObjectResult.setFindDate(dateResult);
            dangerousObjectResult.setFullName(carrierStr);
            dangerousObjectResult.setContact(phoneStr);
            dangerousObjectResult.setTrainNo(trainNoStr);
            dangerousObjectResult.setTicketNo(ticketStr);
            dangerousObjectResult.setHomeAddr(addressStr);
            dangerousObjectResult.setProdName(cateNameStr);
            dangerousObjectResult.setProdNameDetail(cateDescribe);
            dangerousObjectResult.setProdTotal(Integer.parseInt(countStr));
            dangerousObjectResult.setProdQuity(Integer.parseInt(qualityStr));
            dangerousObjectResult.setFindStaffName(catchNameStr);
            dangerousObjectResult.setDealStaffName(dealPeopleStr);
            dangerousObjectResult.setProdGoTo(whereTogoStr);
            dangerousObjectResult.setDealPosition(positionStr);
            dangerousObjectResult.setRemark(remarkStr);
            return true;
        } else {
            showToast("信息填写不完整，请继续填写");
            return false;
        }

    }

    private String formatTime(int hour, int minute) {
        String strHour = "" + hour;
        String strMinute = "" + minute;
        if (hour < 10) {
            strHour = "0" + hour;
        }
        if (minute < 10) {
            strMinute = "0" + minute;
        }
        return strHour + ":" + strMinute;
    }

    private String formatDate(int year, int month, int day) {
        String strMonth = "" + month;
        String strDay = "" + day;
        if (month < 10) {
            strMonth = "0" + month;
        }
        if (day < 10) {
            strDay = "0" + day;
        }
        return year + "-" + strMonth + "-" + strDay;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {

        } else {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_PHOTO:
                    Uri uri = data.getData();
                    bitmap = null;
                    if (uri != null) {
                        String picPath = getFilePathFromUri(this, uri);
                        picPathList.add(picPath);
                        adapter.addItem(picPathList.size());
                    } else {
                        bitmap = (Bitmap) data.getExtras().get("data");
                    }
                    break;
                case REQUEST_CODE_TAKE_PHOTO:
                    picPathList.add(tempFile.getAbsolutePath());
                    adapter.addItem(picPathList.size());
                    break;
            }
        }
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, "", null, "");
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(index);
        cursor.close();
        cursor = null;
        return path;
    }

//    private void addImageContent(Bitmap bitmap) {
//        if (bitmap != null) {
//            imgExceptionFirst.setImageBitmap(bitmap);
//            imgDeletePicFirst.setVisibility(View.VISIBLE);
//            imgExceptionFirst.setClickable(false);
//        }

//        final View view = LayoutInflater.from(mContext).inflate(R.layout.danger_pic_item_layout, null);
//        ImageView deletePic = (ImageView) view.findViewById(R.id.img_delete_pic_item);
//        ImageView contentItemPic = (ImageView) view.findViewById(R.id.img_exception_pic_item);
//        contentItemPic.setImageBitmap(bitmap);
//        picContent.addView(view);
//        deletePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                picContent.removeView(view);
//            }
//        });
//    }

//    private void saveExceptionIcon(Bitmap bitmap) {
//        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            return;
//        }
//        File dirPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        if (!dirPath.exists()) {
//            dirPath.mkdirs();
//        }
//        File headIconPath = new File(dirPath, "head.png");
//        BufferedOutputStream bos = null;
//        try {
//            bos = new BufferedOutputStream(new FileOutputStream(headIconPath));
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 判断是否存在照相机
     *
     * @return
     */
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    /**
     * 拍照
     */
    private void takePhotoAction() {
        if (hasCamera()) {
            Intent takeIntent = new Intent();
            takeIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            File tempPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String timeStamp = dateFormat.format(new Date());
                tempFile = File.createTempFile("exception_"
                        + Property.StationCode + "_"
                        + Property.StaffName + "_"
                        + timeStamp, ".png", tempPath);  //零时文件，暂存拍摄的照片
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivityForResult(takeIntent, REQUEST_CODE_TAKE_PHOTO);
        } else {
            Toast.makeText(this, "该设备不存在照相机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选择照片
     */
    private void choostPhotoAction() {
        Intent chooseIntent = new Intent();
        chooseIntent.setAction(Intent.ACTION_GET_CONTENT);
        chooseIntent.setType("image/*");
        startActivityForResult(chooseIntent, REQUEST_CODE_CHOOSE_PHOTO);
    }


    /**
     * 对图片进行二次采样
     *
     * @param width
     * @param height
     * @return
     */
    private Bitmap createThumbnial(int width, int height, String picPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picPath, options);
        int oldWidth = options.outWidth;
        int oldHeight = options.outHeight;
        int widthRatio = oldWidth / width;
        int heightRatio = oldHeight / height;
        options.inSampleSize = widthRatio < heightRatio ? widthRatio : heightRatio;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(picPath, options);
    }


    private class AddImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_HEAD = 0;
        public static final int TYPE_BODY = 1;
        public static final int TYPE_FOOT = 2;


        private List<String> list;
        private Context mContext;

        public AddImageAdapter(Context context, List<String> list) {
            this.mContext = context;
            this.list = list;
        }

        public void addItem(int position) {
            notifyItemInserted(position);
        }

        public void deleteItem(int position) {
            notifyItemRemoved(position);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == list.size()) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 1 : list.size() + 1;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_BODY) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.danger_pic_item_layout, parent, false);
                return new BodyHolder(view);
            } else if (viewType == TYPE_FOOT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.danger_pic_item_footer_layout, parent, false);
                return new FooterHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof BodyHolder) {
                ((BodyHolder) holder).imgPicContent.setImageBitmap(BitmapFactory.decodeFile(list.get(position)));
            } else if (holder instanceof FooterHolder) {
                ((FooterHolder) holder).imgAddPic.setImageResource(R.drawable.add_pic_icon);
            }
        }


        class BodyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imgPicContent;
            ImageView imgPicDelete;

            public BodyHolder(View itemView) {
                super(itemView);
                imgPicContent = (ImageView) itemView.findViewById(R.id.img_exception_pic_item);
                imgPicDelete = (ImageView) itemView.findViewById(R.id.img_delete_pic_item);
                imgPicDelete.setOnClickListener(this);
                imgPicContent.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.img_exception_pic_item:
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(list.get(getLayoutPosition()))), "image/*");
                        mContext.startActivity(intent);
                        break;
                    case R.id.img_delete_pic_item:
                        picPathList.remove(getLayoutPosition());
                        deleteItem(getLayoutPosition());
                        break;
                }
            }

        }

        class FooterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imgAddPic;

            public FooterHolder(View itemView) {
                super(itemView);
                imgAddPic = (ImageView) itemView.findViewById(R.id.img_pic_icon_footer);
                imgAddPic.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                choosePicDialog.show();
            }
        }
    }
}
