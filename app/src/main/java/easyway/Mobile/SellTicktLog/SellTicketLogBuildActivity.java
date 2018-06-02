package easyway.Mobile.SellTicktLog;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.DangerousGoods.IsEditableUtils;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.JsonUtil;

public class SellTicketLogBuildActivity extends ActivityEx implements View.OnClickListener {

    @BindView(id = R.id.text_time_detail)
    private TextView textLogTitle;
    @BindView(id = R.id.spinner_group_no_sell_log)
    private Spinner groupNo;
    @BindView(id = R.id.edit_whether_sell_log)
    private EditText editWhether;
    @BindView(id = R.id.edit_staff_onduty)
    private EditText staffOnDuty;
    @BindView(id = R.id.edit_total_ticket)
    private EditText totalTicket;
    @BindView(id = R.id.edit_total_money)
    private EditText totalMoney;
    @BindView(id = R.id.edit_agency_total_ticket)
    private EditText agencyTotalTicket;
    @BindView(id = R.id.edit_agency_total_money)
    private EditText agencyTotalMoney;
    @BindView(id = R.id.edit_dif_total_ticket)
    private EditText difTotalTicket;
    @BindView(id = R.id.edit_dif_ticket_return)
    private EditText difReturnTicket;
    @BindView(id = R.id.edit_dif_money_return)
    private EditText difReturnMoney;
    @BindView(id = R.id.edit_boss_order)
    private EditText bossOrder;
    @BindView(id = R.id.edit_emphasis)
    private EditText emphasis;
    @BindView(id = R.id.edit_work_status)
    private EditText workStatus;
    @BindView(id = R.id.edit_exchange_item)
    private EditText exchangeItem;
    @BindView(id = R.id.btn_submit_ticket_sell_log)
    private Button submitForm;

    private boolean can_edit = false;

    private final static int SUCCESS_SUBMIT = 1;

    private final static int ERROR_SUBMIT = 2;

    private final static int EXCEPTION_SUBMIT = 3;

    private SellTicketLog mSellLog;
    private String jsonObj;
    private String flag;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_SUBMIT:
                    closeProgressDialog();
                    String message = (String) msg.obj;
                    Toast.makeText(SellTicketLogBuildActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                    submitForm.setVisibility(View.GONE);
                    SellTicketLogBuildActivity.this.finish();
                    break;
                case ERROR_SUBMIT:
                    closeProgressDialog();
                    Toast.makeText(SellTicketLogBuildActivity.this, "提交失败!", Toast.LENGTH_SHORT).show();
                    break;
                case EXCEPTION_SUBMIT:
                    closeProgressDialog();
                    Toast.makeText(SellTicketLogBuildActivity.this, "提交日志失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_ticket_log_build);
        mSellLog = new SellTicketLog();
        AnnotateUtil.initBindView(this);
        getFormContent();
    }


    private void getFormContent() {
        Button addBtn = (Button) findViewById(R.id.btnset);
        addBtn.setText("编辑");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            flag = bundle.getString("Flag");
            if ("New".equals(flag)) {
                addBtn.setVisibility(View.INVISIBLE);
                addBtn.setEnabled(false);
                TextView title = (TextView) findViewById(R.id.title);
                title.setText("新建日志");
                String weekday = bundle.getString("WeekDay");
                String date = bundle.getString("Date");
                if (DateUtil.IsDaytime()) {
                    mSellLog.WorkDate = date + " 12:00:00";
                } else {
                    mSellLog.WorkDate = date + " 07:59:59";
                }
                textLogTitle.setText(date + " " + weekday);
                initView();
            } else if ("Show".equals(flag)) {
                addBtn.setVisibility(View.VISIBLE);
                addBtn.setOnClickListener(this);
                boolean isEditable = bundle.getBoolean("IsEditable");
                mSellLog = (SellTicketLog) bundle.getSerializable("LogItem");
                initView(mSellLog);
                IsEditableUtils.edibleStateChanged(false, SellTicketLogBuildActivity.this);
                if (isEditable) {
                    addBtn.setVisibility(View.VISIBLE);
                    addBtn.setAlpha(1);
                    addBtn.setEnabled(true);
                } else {
                    addBtn.setVisibility(View.GONE);
                }
            }
        }
    }

    private void init() {
        submitForm.setOnClickListener(this);
    }

    private void initView() {
        init();
    }

    private void initView(SellTicketLog logItem) {
        init();
        staffOnDuty.setText(logItem.DutyOfficer);
        String ClassNumber = logItem.Classes;
        int position = -1;
        switch (ClassNumber) {
            case "一班":
                position = 0;
                break;
            case "二班":
                position = 1;
                break;
            case "三班":
                position = 2;
                break;
            case "四班":
                position = 3;
                break;
        }
        groupNo.setSelection(position);
        totalTicket.setText(logItem.SellTicketNum + "");
        totalMoney.setText(logItem.SellTicketMoney + "");
        agencyTotalTicket.setText(logItem.AgentTicketsNum + "");
        agencyTotalMoney.setText(logItem.AgentTicketsMoney + "");
        difTotalTicket.setText(logItem.AbnormalBounceTicketNum + "");
        difReturnTicket.setText(logItem.BounceTicketNum + "");
        difReturnMoney.setText(logItem.BounceMoney + "");
        bossOrder.setText(logItem.SuperiorRemark);
        emphasis.setText(logItem.MainWork);
        workStatus.setText(logItem.WorkRemark);
        exchangeItem.setText(logItem.MatterRemark);
        if (TextUtils.isEmpty(logItem.Weather)) {
            editWhether.setText("");
        } else {
            editWhether.setText(logItem.Weather);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_ticket_sell_log:
                showProgressDialog("数据正在上传...");
                if (setSellLog()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            submitFormData();
                        }
                    }).start();
                }
                break;
            case R.id.btnset:
                can_edit = !can_edit;
                IsEditableUtils.edibleStateChanged(can_edit, SellTicketLogBuildActivity.this);
                if (can_edit) {
                    staffOnDuty.requestFocus();
                }
                break;
        }

    }


    private boolean setSellLog() {
        if (TextUtils.isEmpty(staffOnDuty.getText().toString())) {
            closeProgressDialog();
            Toast.makeText(this, "值班人员未填写", Toast.LENGTH_SHORT).show();
            return false;
        }
        HashMap<String, Object> paramValue = new HashMap<>();
        mSellLog.Weather = editWhether.getText().toString();
        mSellLog.Classes = (String) groupNo.getSelectedItem();
        mSellLog.DutyOfficer = staffOnDuty.getText().toString();
        mSellLog.SellTicketNum = (int) getContentOfEdit(totalTicket);
        mSellLog.SellTicketMoney = getContentOfEdit(totalMoney);
        mSellLog.AgentTicketsNum = (int) getContentOfEdit(agencyTotalTicket);
        mSellLog.AgentTicketsMoney = getContentOfEdit(agencyTotalMoney);
        mSellLog.AbnormalBounceTicketNum = (int) getContentOfEdit(difTotalTicket);
        mSellLog.BounceTicketNum = (int) getContentOfEdit(difReturnTicket);
        mSellLog.BounceMoney = getContentOfEdit(difReturnMoney);
        mSellLog.CreateDate = DateUtil.getNowDate();
        mSellLog.WorkTime = DateUtil.getNowDate();
        mSellLog.SuperiorRemark = bossOrder.getText().toString();
        mSellLog.MainWork = emphasis.getText().toString();
        mSellLog.WorkRemark = workStatus.getText().toString();
        mSellLog.MatterRemark = exchangeItem.getText().toString();
        if (mSellLog.ID == 0) {
            paramValue.put("Weather", mSellLog.Weather);
            paramValue.put("WorkDate", mSellLog.WorkDate);
            paramValue.put("AbnormalBounceTicketNum", mSellLog.AbnormalBounceTicketNum);
            paramValue.put("AgentTicketsMoney", mSellLog.AgentTicketsMoney);
            paramValue.put("AgentTicketsNum", mSellLog.AgentTicketsNum);
            paramValue.put("BounceMoney", mSellLog.BounceMoney);
            paramValue.put("BounceTicketNum", mSellLog.BounceTicketNum);
            paramValue.put("Classes", mSellLog.Classes);
            paramValue.put("CreateDate", mSellLog.CreateDate);
            paramValue.put("DutyOfficer", mSellLog.DutyOfficer);
            paramValue.put("MainWork", mSellLog.MainWork);
            paramValue.put("MatterRemark", mSellLog.MatterRemark);
            paramValue.put("SuperiorRemark", mSellLog.SuperiorRemark);
            paramValue.put("WorkRemark", mSellLog.WorkRemark);
            paramValue.put("WorkTime", mSellLog.WorkTime);
            paramValue.put("SellTicketNum", mSellLog.SellTicketNum);
            paramValue.put("SellTicketMoney", mSellLog.SellTicketMoney);
            jsonObj = new Gson().toJson(paramValue);
        } else {
            jsonObj = new Gson().toJson(mSellLog);
        }
        return true;
    }

    private double getContentOfEdit(EditText edit) {
        if (TextUtils.isEmpty(edit.getText() + "")) {
            return 0;
        } else {
            return Double.parseDouble(edit.getText().toString());
        }
    }

    private void submitFormData() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("josnObj", jsonObj);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = "";
        if ("New".equals(flag)) {
            methodName = Constant.INSERT_TICKET_LOG;
        } else if ("Show".equals(flag)) {
            methodName = Constant.UPDATE_TICKET_LOG;
        }
        WebServiceManager webServiceManager = new WebServiceManager(
                SellTicketLogBuildActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (TextUtils.isEmpty(result)) {
            mHander.sendEmptyMessage(EXCEPTION_SUBMIT);
        }
        Message msg = mHander.obtainMessage();
        String messageStr = JsonUtil.GetJsonString(result, "Msg");
        boolean MsgType = JsonUtil.GetJsonBoolean(result, "MsgType");
        int code = JsonUtil.GetJsonInt(result, "Code");
        if (MsgType) {
            switch (code) {
                case Constant.EXCEPTION:
                    mHander.sendEmptyMessage(EXCEPTION_SUBMIT);
                    break;
                case Constant.NORMAL_ZERO:
                case Constant.NORMAL:
                    if (TextUtils.isEmpty(messageStr)) {
                        msg.obj = JsonUtil.GetJsonString(result, "Data");
                    } else {
                        msg.obj = messageStr;
                    }
                    msg.what = SUCCESS_SUBMIT;
                    mHander.sendMessage(msg);
                    break;
            }
        } else {
            msg.what = ERROR_SUBMIT;
            mHander.sendMessage(msg);
        }
    }

}
