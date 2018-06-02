package easyway.Mobile.PassengerTrafficLog;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

public class NewEditLogActivity extends ActivityEx implements View.OnClickListener {

    private TextView title;
    @BindView(id = R.id.spinner_group_no_sell_log)
    Spinner spinnerGroupNoSellLog;//班组
    @BindView(id = R.id.edit_whether_pt_log)
    EditText editWhether; // 天气
    @BindView(id = R.id.et_station_head)
    EditText etStationHead;//站长
    @BindView(id = R.id.et_people_ticket_after)
    EditText etPeopleTicketAfter;//补票人数
    @BindView(id = R.id.et_ticket_amount_money)
    EditText etTicketAmountMoney;//补票款
    @BindView(id = R.id.et_superheavy_num_units)
    EditText etSuperheavyNumUnits;//超重件数
    @BindView(id = R.id.et_superheavy_weight)
    EditText etSuperheavyWeight;//超重重量
    @BindView(id = R.id.et_superheavy_amount_money)
    EditText etSuperheavyAmountMoney;//超重金额
    @BindView(id = R.id.et_forfeiture_people)
    EditText etForfeiturePeople;//罚没危险品人数
    @BindView(id = R.id.et_forfeiture_piece)
    EditText etForfeiturePiece;//罚没危险品件数
    @BindView(id = R.id.et_sale_tickets_sheets)
    EditText etSaleTicketsSheets;//售票张数
    @BindView(id = R.id.et_sale_tickets_income)
    EditText etSaleTicketsIncome;//售票收入
    @BindView(id = R.id.et_sale_tickets_overflow_loss)
    EditText etSaleTicketsOverflowLoss;//售票溢赔
    @BindView(id = R.id.et_good_people)
    EditText etGoodPeople;//好人好事人数
    @BindView(id = R.id.et_good_piece)
    EditText etGoodPiece;//好人好事件数
    @BindView(id = R.id.et_prevention_accidents_people)
    EditText etPreventionAccidentsPeople;//防止事故人数
    @BindView(id = R.id.et_prevention_accidents_piece)
    EditText etPreventionAccidentsPiece;//防止事故件数
    @BindView(id = R.id.et_happens_accidents_people)
    EditText etHappensAccidentsPeople;//发生事故人数
    @BindView(id = R.id.et_happens_accidents_piece)
    EditText etHappensAccidentsPiece;//发生事故件数
    @BindView(id = R.id.et_superior_prompt)
    EditText etSuperiorPrompt;//上级指示
    @BindView(id = R.id.et_keypoint)
    EditText etKeypoint;//重点工作
    @BindView(id = R.id.et_work_condition)
    EditText etWorkCondition;//工作情况
    @BindView(id = R.id.et_handover_matters)
    EditText etHandoverMatters;//交接事项
    @BindView(id = R.id.bt_submit)
    Button btSubmit;//提交按钮

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_SUBMIT:
                    closeProgressDialog();
                    String message = (String) msg.obj;
                    Toast.makeText(NewEditLogActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                    btSubmit.setVisibility(View.GONE);
                    NewEditLogActivity.this.finish();
                    break;
                case ERROR_SUBMIT:
                    closeProgressDialog();
                    Toast.makeText(NewEditLogActivity.this, "提交失败!", Toast.LENGTH_SHORT).show();
                    break;
                case EXCEPTION_SUBMIT:
                    closeProgressDialog();
                    Toast.makeText(NewEditLogActivity.this, "提交日志失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final static int SUCCESS_SUBMIT = 1;

    private final static int ERROR_SUBMIT = 2;

    private final static int EXCEPTION_SUBMIT = 3;

    private boolean can_edit = false;
    private String jsonObj;

    private SearchPtlLogBean.DataBean logInfo;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_log);
        AnnotateUtil.initBindView(this);
        logInfo = new SearchPtlLogBean.DataBean();
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
                    logInfo.setWorkDate(date + " 12:00:00");
                } else {
                    logInfo.setWorkDate(date + " 07:59:59");
                }
                title.setText(date + " " + weekday);
                initView();
            } else if ("Show".equals(flag)) {
                addBtn.setVisibility(View.VISIBLE);
                addBtn.setOnClickListener(this);
                boolean isEditable = bundle.getBoolean("IsEditable");
                logInfo = (SearchPtlLogBean.DataBean) bundle.getSerializable("LogInfo");
                initView(logInfo);
                IsEditableUtils.edibleStateChanged(false, NewEditLogActivity.this);
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

    private void initView() {
        btSubmit.setOnClickListener(this);
    }

    private void initView(SearchPtlLogBean.DataBean logInfo) {
        initView();
        editWhether.setText(logInfo.getWeather() + "");
        etStationHead.setText(logInfo.getDutyOfficer() + "");
        String ClassNumber = logInfo.getClasses();
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
        spinnerGroupNoSellLog.setSelection(position);
        etPeopleTicketAfter.setText(logInfo.getTicketsPerson() + "");
        etTicketAmountMoney.setText(logInfo.getTicketsMoney() + "");
        etSuperheavyNumUnits.setText(logInfo.getWeightNum() + "");
        etSuperheavyWeight.setText(logInfo.getWeights() + "");
        etSuperheavyAmountMoney.setText(logInfo.getWeightMoney() + "");
        etForfeiturePeople.setText(logInfo.getDangerPerson() + "");
        etForfeiturePiece.setText(logInfo.getDangerNum() + "");
        etSaleTicketsSheets.setText(logInfo.getSellTicketNum() + "");
        etSaleTicketsIncome.setText(logInfo.getSellTicketMoney() + "");
        etSaleTicketsOverflowLoss.setText(logInfo.getSellTicketOverflow() + "");
        etGoodPeople.setText(logInfo.getGoodDeedPerson() + "");
        etGoodPiece.setText(logInfo.getGoodDeedNum() + "");
        etPreventionAccidentsPeople.setText(logInfo.getReTroublePerson() + "");
        etPreventionAccidentsPiece.setText(logInfo.getReTroubleNum() + "");
        etHappensAccidentsPeople.setText(logInfo.getTroublePerson() + "");
        etHappensAccidentsPiece.setText(logInfo.getTroubleNum() + "");
        etSuperiorPrompt.setText(logInfo.getSuperiorRemark() + "");
        etKeypoint.setText(logInfo.getMainWork() + "");
        etWorkCondition.setText(logInfo.getWorkRemark() + "");
        etHandoverMatters.setText(logInfo.getMatterRemark() + "");
    }


    private boolean setLogInfo() {
        if (TextUtils.isEmpty(etStationHead.getText().toString())) {
            closeProgressDialog();
            Toast.makeText(this, "值班人员未填写", Toast.LENGTH_SHORT).show();
            return false;
        }

        HashMap<String, String> logParam = new HashMap<String, String>();
        if (logInfo.getID() != 0) {
            logParam.put("ID", logInfo.getID() + "");
        }
        logParam.put("CreateDate", DateUtil.getNowDate());
        logParam.put("WorkDate", DateUtil.formatDate(logInfo.getWorkDate(), DateUtil.YYYY_MM_DD_HH_MM_SS));
        logParam.put("WorkTime", DateUtil.getNowDate());
        logParam.put("Weather", editWhether.getText().toString());
        logParam.put("Classes", spinnerGroupNoSellLog.getSelectedItem() + "");
        logParam.put("DutyOfficer", etStationHead.getText().toString());
        logParam.put("TicketsPerson", etPeopleTicketAfter.getText().toString());
        logParam.put("TicketsMoney", etTicketAmountMoney.getText().toString());
        logParam.put("WeightNum", etSuperheavyNumUnits.getText().toString());
        logParam.put("Weights", etSuperheavyWeight.getText().toString());
        logParam.put("WeightMoney", etSuperheavyAmountMoney.getText().toString());
        logParam.put("DangerPerson", etForfeiturePeople.getText().toString());
        logParam.put("DangerNum", etForfeiturePiece.getText().toString());
        logParam.put("SellTicketNum", etSaleTicketsSheets.getText().toString());
        logParam.put("SellTicketMoney", etSaleTicketsIncome.getText().toString());
        logParam.put("SellTicketOverflow", etSaleTicketsOverflowLoss.getText().toString());
        logParam.put("GoodDeedPerson", etGoodPeople.getText().toString());
        logParam.put("GoodDeedNum", etGoodPiece.getText().toString());
        logParam.put("ReTroublePerson", etPreventionAccidentsPeople.getText().toString());
        logParam.put("ReTroubleNum", etPreventionAccidentsPiece.getText().toString());
        logParam.put("TroublePerson", etHappensAccidentsPeople.getText().toString());
        logParam.put("TroubleNum", etHappensAccidentsPiece.getText().toString());
        logParam.put("SuperiorRemark", etSuperiorPrompt.getText().toString());
        logParam.put("MainWork", etKeypoint.getText().toString());
        logParam.put("WorkRemark", etWorkCondition.getText().toString());
        logParam.put("MatterRemark", etHandoverMatters.getText().toString());
        Gson gson = new Gson();
        jsonObj = gson.toJson(logParam);
        return true;
    }

    private void submitFormData() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("josnObj", jsonObj);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = "";
        if ("New".equals(flag)) {
            methodName = Constant.INSERTPTT_WORKLOG;
        } else if ("Show".equals(flag)) {
            methodName = Constant.UPDATEPTT_WORKLOG;
        }
        WebServiceManager webServiceManager = new WebServiceManager(
                NewEditLogActivity.this, methodName, parmValues);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                showProgressDialog("数据正在上传...");
                if (setLogInfo()) {
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
                IsEditableUtils.edibleStateChanged(can_edit, NewEditLogActivity.this);
                if (can_edit) {
                    editWhether.requestFocus();
                }
                break;
        }
    }
}
