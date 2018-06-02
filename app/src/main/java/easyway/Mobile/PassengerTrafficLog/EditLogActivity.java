package easyway.Mobile.PassengerTrafficLog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;

/**
 * Created by JSC on 2017/11/20.
 */

public class EditLogActivity extends ActivityEx implements View.OnClickListener {

    private TextView title;
    @BindView(id=R.id.spinner_group_no_sell_log)
    Spinner spinnerGroupNoSellLog;//班组
    @BindView(id=R.id.et_station_head)
    EditText etStationHead;//站长
    @BindView(id=R.id.et_people_ticket_after)
    EditText etPeopleTicketAfter;//补票人数
    @BindView(id=R.id.et_ticket_amount_money)
    EditText etTicketAmountMoney;//补票款
    @BindView(id=R.id.et_superheavy_num_units)
    EditText etSuperheavyNumUnits;//超重件数
    @BindView(id=R.id.et_superheavy_weight)
    EditText etSuperheavyWeight;//超重重量
    @BindView(id=R.id.et_superheavy_amount_money)
    EditText etSuperheavyAmountMoney;//超重金额
    @BindView(id=R.id.et_forfeiture_people)
    EditText etForfeiturePeople;//罚没危险品人数
    @BindView(id=R.id.et_forfeiture_piece)
    EditText etForfeiturePiece;//罚没危险品件数
    @BindView(id=R.id.et_sale_tickets_sheets)
    EditText etSaleTicketsSheets;//售票张数
    @BindView(id=R.id.et_sale_tickets_income)
    EditText etSaleTicketsIncome;//售票收入
    @BindView(id=R.id.et_sale_tickets_overflow_loss)
    EditText etSaleTicketsOverflowLoss;//售票溢赔
    @BindView(id=R.id.et_good_people)
    EditText etGoodPeople;//好人好事人数
    @BindView(id=R.id.et_good_piece)
    EditText etGoodPiece;//好人好事件数
    @BindView(id=R.id.et_prevention_accidents_people)
    EditText etPreventionAccidentsPeople;//防止事故人数
    @BindView(id=R.id.et_prevention_accidents_piece)
    EditText etPreventionAccidentsPiece;//防止事故件数
    @BindView(id=R.id.et_happens_accidents_people)
    EditText etHappensAccidentsPeople;//发生事故人数
    @BindView(id=R.id.et_happens_accidents_piece)
    EditText etHappensAccidentsPiece;//发生事故件数
    @BindView(id=R.id.et_superior_prompt)
    EditText etSuperiorPrompt;//上级指示
    @BindView(id=R.id.et_keypoint)
    EditText etKeypoint;//重点工作
    @BindView(id=R.id.et_work_condition)
    EditText etWorkCondition;//工作情况
    @BindView(id=R.id.et_handover_matters)
    EditText etHandoverMatters;//交接事项
    @BindView(id=R.id.bt_submit)
    Button btSubmit;//提交按钮

    private SearchPtlLogBean.DataBean logInfo;
    private String dateString;
    private boolean IsEditable = false;
    private final static int SUCCESS_SUBMIT = 1;
    private final static int ERROR_SUBMIT = 2;
    private final static int EXCEPTION_SUBMIT = 3;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_SUBMIT:
                    closeProgressDialog();
                    String message = (String) msg.obj;
                    Toast.makeText(EditLogActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                    btSubmit.setVisibility(View.GONE);
                    finish();
                    break;
                case ERROR_SUBMIT:
                    closeProgressDialog();
                    Toast.makeText(EditLogActivity.this, "提交失败!", Toast.LENGTH_SHORT).show();
                    break;
                case EXCEPTION_SUBMIT:
                    closeProgressDialog();
                    Toast.makeText(EditLogActivity.this, "提交日志失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_traffic_layout);
        AnnotateUtil.initBindView(this);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        logInfo = (SearchPtlLogBean.DataBean) intent.getSerializableExtra("logInfo");
        LogUtil.e("logInfo==" + logInfo);
        if (logInfo != null) {
            dateString = logInfo.getWorkTime();
            String dateStr = DateUtil.formatDate(dateString,DateUtil.YYYY_MM_DD);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                java.util.Date date = sdf.parse(dateStr);
                String weekStr = DateUtil.getWeekOfDate(date);
                title.setText(dateStr + " " + weekStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            etStationHead.setText(logInfo.getDutyOfficer() + "");
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


        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(this);
        }

        btSubmit = (Button) findViewById(R.id.bt_submit);
        IsEditable = intent.getBooleanExtra("IsEditable", false);
        if (IsEditable) {
            setTextEditable(IsEditable);
            btSubmit.setVisibility(View.VISIBLE);
        } else {
            setTextEditable(IsEditable);
            btSubmit.setVisibility(View.INVISIBLE);
        }
        if (btSubmit != null) {
            btSubmit.setOnClickListener(this);
        }
    }

    private void setTextEditable(boolean isEditable) {
        etStationHead.setFocusable(isEditable);
        etPeopleTicketAfter.setFocusable(isEditable);
        etTicketAmountMoney.setFocusable(isEditable);
        etSuperheavyNumUnits.setFocusable(isEditable);
        etSuperheavyWeight.setFocusable(isEditable);
        etSuperheavyAmountMoney.setFocusable(isEditable);
        etForfeiturePeople.setFocusable(isEditable);
        etForfeiturePiece.setFocusable(isEditable);
        etSaleTicketsSheets.setFocusable(isEditable);
        etSaleTicketsIncome.setFocusable(isEditable);
        etSaleTicketsOverflowLoss.setFocusable(isEditable);
        etGoodPeople.setFocusable(isEditable);
        etGoodPiece.setFocusable(isEditable);
        etPreventionAccidentsPeople.setFocusable(isEditable);
        etPreventionAccidentsPiece.setFocusable(isEditable);
        etHappensAccidentsPeople.setFocusable(isEditable);
        etHappensAccidentsPiece.setFocusable(isEditable);
        etSuperiorPrompt.setFocusable(isEditable);
        etKeypoint.setFocusable(isEditable);
        etWorkCondition.setFocusable(isEditable);
        etHandoverMatters.setFocusable(isEditable);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturn: // 返回
                finish();
                break;
            case R.id.bt_submit:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendData();
                    }
                }).start();

                break;
        }

    }

    private void sendData() {
        String date = DateUtil.formatDate(dateString, DateUtil.YYYY_MM_DD_HH_MM_SS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();
        String newDate = sdf.format(date1);

        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.UPDATEPTT_WORKLOG;

        HashMap<String, String> logParam = new HashMap<String, String>();
        logParam.put("ID", logInfo.getID() + "");
        logParam.put("CreateDate", newDate);
        logParam.put("WorkDate", date);
        logParam.put("WorkTime", newDate);
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
        String jsonStr = gson.toJson(logParam);
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("josnObj", jsonStr);

        WebServiceManager webServiceManager = new WebServiceManager(
                EditLogActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result != null && !TextUtils.isEmpty(result)) {
            LogUtil.e("返回结果result==" + result);

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
                        String data = JsonUtil.GetJsonString(result, "Data");
                        if (data.equals("更新客运工作日志成功")) {
                            if (TextUtils.isEmpty(messageStr)) {
                                msg.obj = JsonUtil.GetJsonString(result, "Data");
                            } else {
                                msg.obj = messageStr;
                            }
                            msg.what = SUCCESS_SUBMIT;
                            mHander.sendMessage(msg);
                        }
                        break;
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
        } else {
            mHander.sendEmptyMessage(EXCEPTION_SUBMIT);
        }
    }
}
