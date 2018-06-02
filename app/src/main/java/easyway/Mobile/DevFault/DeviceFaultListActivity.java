package easyway.Mobile.DevFault;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Contacts.Contacts;
import easyway.Mobile.Data.DFMainData;
import easyway.Mobile.Data.DevFaultReport;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.ShowProgress;

/**
 * Created by JSC on 2018/1/4.
 */

public class DeviceFaultListActivity extends ActivityEx implements DeviceFaultActivity.ShowSearchBarListener {
    private final int limit = 10;
    private long startIndex = 0, totalItems = 0;
    private String startDate = "", endDate = "";
    private Date startDt = new Date(), endDt = new Date();
    private String confirmStatus = "";

    private ArrayList<DFMainData> devFaultList = null;
    private boolean isPullRefresh = false;
    private PullRefreshListView gv_queryList;
    private DFAdapter mAdpater;
    private boolean addedFoot = false;
    private Button btnReporter; // 选择上报人
    private Staff mStaff;

    private final int MSG_GET_DATA_SUCCEED = 1;
    private final int MSG_GET_DATA_FAIL = 2;
    private final int REQUEST_CODE_SELECTUSER = 100;

    private final int MSG_GET_Repair_ok = 3;
    private final int MSG_GET_ok_SUCCEED = 4;
    private final int MSG_GET_ok_FAIL = 5;
    private final int NET_ERROR = 6;
    public static final int CONFIRM_SUCCESS = 7;
    public static final int CONFIRM_FAIL = 8;
    public static final int CONFIRM_ERROR = 9;

    //for listView add more
    private boolean isLastRow;
    private boolean isTopRow;

    public static Activity instance;
    private LinearLayout layoutSearch;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            ShowProgress.closeProgressDialog();
            closeProgressDialog();
            if (isPullRefresh) {
                gv_queryList.onRefreshComplete();
                isPullRefresh = false;
                //这是设置View的是否允许刷新
                gv_queryList.setRefreshable(false);
            }

            switch (msg.what) {
                case MSG_GET_DATA_SUCCEED:
                    closeProgressDialog();
                    if (devFaultList == null)
                        devFaultList = new ArrayList<DFMainData>();

                    if (startIndex == 0)
                        devFaultList.clear();
                    devFaultList.addAll((ArrayList<DFMainData>) msg.obj);
                    mAdpater.setData(devFaultList);
                    mAdpater.notifyDataSetChanged();
                    break;
                case MSG_GET_DATA_FAIL:
                    closeProgressDialog();
                    showToast(errMsg);
                    break;
                case MSG_GET_Repair_ok:
                    showProgressDialog("刷新数据");
                    getData();
                    break;
                case MSG_GET_ok_SUCCEED:
                    showProgressDialog("刷新数据");
                    getData();
                    break;
                case MSG_GET_ok_FAIL:
                    showToast("提交失败，稍后再试");
                    break;
                case NET_ERROR:
                    showToast(R.string.ConnectFail);
                    break;
                case CONFIRM_SUCCESS:
                    closeProgressDialog();
                    String confirm_msg = (String) msg.obj;
                    showToast(confirm_msg);
                    break;
                case CONFIRM_FAIL:

                    break;
                case CONFIRM_ERROR:

                    break;
                default:
                    break;
            }
        }
    };

    private static boolean isFromOnCreate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_fault_list);
        isFromOnCreate = true;
        instance = this;
        showDialogData();
        initView();
        startIndex = 0;
        getData();
        ShowSearchBarListenerManager.getInstance().setShowListener(this);
    }

    private void showDialogData() {

        if (getIntent().getBooleanExtra("shebei", false)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View myLoginView = layoutInflater.inflate(R.layout.baozhang_data,
                    null);
            final EditText et_input = (EditText) myLoginView
                    .findViewById(R.id.et_input);

            AlertDialog dlg = new AlertDialog.Builder(this)
                    .setTitle("维修完成")
                    .setView(myLoginView)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    showProgressDialog("数据加载中");
                                    final int dfId = getIntent().getIntExtra("dfId", -1);
                                    final String dspIds = getIntent().getStringExtra("dspIds");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            getRepairData(et_input.getText()
                                                    .toString(), dfId, dspIds);
                                        }
                                    }).start();

                                }

                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {

                                }
                            }).create();
            dlg.show();
        }

    }

    private void getRepairData(String data, int dfId, String dspIds) {

        // showProgressDialog("数据加载中");
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("dfId", String.valueOf(dfId));
        parmValues.put("dspIds", dspIds);
        parmValues.put("RepIssue", data);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_Repair_FAULT;
        WebServiceManager webServiceManager = new WebServiceManager(
                this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        Gson gson = new Gson();
        Type type = new TypeToken<Result<String>>() {
        }.getType();
        Result<String> getDevSpare = gson.fromJson(result, type);
        if (result != null && result.length() > 0) {
            if (getDevSpare.isMsgType()) {
                myhandle.sendMessage(myhandle.obtainMessage(MSG_GET_Repair_ok, getDevSpare.getData()));
            } else {
                myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
            }
        } else {
            myhandle.sendEmptyMessage(NET_ERROR);
        }
//		if (result == null || result.equals("")) {
//			myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
//			return;
//		}
//		Message msg = new Message();
//		msg.obj = result;
//		msg.what = MSG_GET_Repair_ok;
//		myhandle.sendMessage(msg);

    }

    private void initView() {
        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);

        EditText etSearch = (EditText) findViewById(R.id.search_edit);
        etSearch.setHint(R.string.search_input_devname);

        // 选择上报人
        btnReporter = (Button) findViewById(R.id.btnReporter);
        btnReporter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(DeviceFaultListActivity.this, Contacts.class);
                intent.putExtra(Contacts.KEY_FLAG, Contacts.FLAG_POINTTASK);
                startActivityForResult(intent, REQUEST_CODE_SELECTUSER);
            }
        });

        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(searchListener());

        Button btnStartDate = (Button) findViewById(R.id.btnStartDate);
        DatePickerDialog buildDStart = new DatePickerDialog(this,
                onDateSetLis(btnStartDate), 1900 + startDt.getYear(),
                startDt.getMonth(), startDt.getDate());
        btnStartDate.setOnClickListener(showDatePicker(buildDStart));

        Button btnEndDate = (Button) findViewById(R.id.btnEndDate);
        DatePickerDialog buildDEnd = new DatePickerDialog(this,
                onDateSetLis(btnEndDate), 1900 + startDt.getYear(),
                startDt.getMonth(), startDt.getDate());
        btnEndDate.setOnClickListener(showDatePicker(buildDEnd));

        gv_queryList = (PullRefreshListView) findViewById(R.id.lstDevFault);
        mAdpater = new DFAdapter(this, devFaultList, myhandle);
        gv_queryList.setAdapter(mAdpater);
        gv_queryList.setonRefreshListener(new PullRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                isPullRefresh = true;
                startIndex = 0;
                getData();
            }
        });
        gv_queryList.setRefreshable(false);
        isLastRow = false;
        isTopRow = false;
        AddMoreListener();
        Go2Detail();
    }

    private void AddMoreListener() {
        gv_queryList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (devFaultList == null) {
                        startIndex = 0;
                    } else {
                        startIndex = devFaultList.size();
                    }
                    getData();
                }
                if (isTopRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //只有在最上面的时候才能刷新。不然都没法拉上去了。
                    gv_queryList.setRefreshable(true);
                }
                isLastRow = false;
                isTopRow = false;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    isLastRow = true;
                }
                if (gv_queryList.getFirstVisiblePosition() == 0) {
                    isTopRow = true;
                }
            }
        });
    }

    // 获取数据
    private void getData() {
        showProgressDialog(R.string.GettingData);
        new Thread() {
            public void run() {
                if (!ExitApplication.isBoYuan) {
                    try {
                        Thread.sleep((long) (Math.random() * 5000 + 1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                GetDevFault();
            }
        }.start();
    }

    private DatePickerDialog.OnDateSetListener onDateSetLis(final Button btn) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            @SuppressLint("SimpleDateFormat")
            public void onDateSet(DatePicker arg0, int year, int monthOfYear,
                                  int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (btn.getId() == R.id.btnStartDate) {
                    startDt.setYear(year - 1900);
                    startDt.setMonth(monthOfYear);
                    startDt.setDate(dayOfMonth);
                    btn.setText(dateFormat.format(startDt));
                    startDate = dateFormat.format(startDt);
                } else {
                    endDt.setYear(year - 1900);
                    endDt.setMonth(monthOfYear);
                    endDt.setDate(dayOfMonth);
                    btn.setText(dateFormat.format(endDt));
                    endDate = dateFormat.format(endDt);
                }

            }
        };
        return listener;
    }

    private View.OnClickListener showDatePicker(final AlertDialog dialog) {
        View.OnClickListener lis = new View.OnClickListener() {
            public void onClick(View v) {
                dialog.show();
            }
        };

        return lis;
    }

    private View.OnClickListener searchListener() {
        View.OnClickListener lis = new View.OnClickListener() {

            public void onClick(View v) {
                startIndex = 0;
                totalItems = 0;
                getData();
            }
        };
        return lis;
    }

    private void GetDevFault() {
        EditText txDevName = (EditText) findViewById(R.id.search_edit);
        String devName = txDevName.getText().toString();
        String staffname = mStaff == null ? "*" : mStaff.StaffName;// txReporter.getText().toString();
        String searchValues = "DevName=" + devName + ";StaffName=" + staffname
                + ";StaTime=" + startDate + ";EndTime=" + endDate;
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("limit", String.valueOf(limit));
        parmValues.put("start", String.valueOf(startIndex));
        parmValues.put("searchValues", searchValues);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_DEV_FAULT;
        WebServiceManager webServiceManager = new WebServiceManager(
                this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            closeProgressDialog();
            errMsg = getString(R.string.exp_getdata);
            myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
//			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
//
//			for (int i = 0; i < jsonArray.length(); i++) {
//				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
//				DevFaultReport devFault = new DevFaultReport();
//				devFault.Id = JsonUtil.GetJsonObjLongValue(jsonObj, "Id");
//				devFault.DevCode = JsonUtil.GetJsonObjStringValue(jsonObj,
//						"DevCode");
//				devFault.DevName = JsonUtil.GetJsonObjStringValue(jsonObj,
//						"DevName");
//				devFault.ReportedTime = JsonUtil.GetJsonObjStringValue(jsonObj,
//						"ReportedTime");
//				devFault.FaultContent = JsonUtil.GetJsonObjStringValue(jsonObj,
//						"FaultContent");
//				devFault.DevIssueImgFrom = JsonUtil.GetJsonObjStringValue(
//						jsonObj, "DevIssueImgFrom");
//				// devFault.Remarks = JsonUtil.GetJsonObjStringValue(jsonObj,
//				// "Remarks");
//				// devFault.ConfirmStatus = JsonUtil.GetJsonObjStringValue(
//				// jsonObj, "ConfirmStatus");
//				devFault.AppStatus = JsonUtil.GetJsonObjStringValue(jsonObj,
//						"AppStatus");
//				devFault.Reporteder = JsonUtil.GetJsonObjStringValue(jsonObj,
//						"Reporteder");
//				devFault.UsingDeptId = JsonUtil.GetJsonLong(result,
//						"UsingDeptId");
//				devFault.UsingEmpoyeeId = JsonUtil.GetJsonLong(result,
//						"UsingEmpoyeeId");
//				list.add(devFault);
//			}

//			totalItems = JsonUtil.GetJsonLong(result, "total");
                Gson gson = new Gson();
                Type type = new TypeToken<ResultForListData<DFMainData>>() {
                }.getType();
                ResultForListData<DFMainData> ReturnData = gson.fromJson(result, type);
                List<DFMainData> dataList = ReturnData.getData();
                Log.e("zwt", "设备查询结果" + dataList);
                totalItems = ReturnData.getTotal();
                Message msg = new Message();
                msg.obj = dataList;
                msg.what = MSG_GET_DATA_SUCCEED;
                myhandle.sendMessage(msg);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromOnCreate) {

        } else {
            getData();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        isFromOnCreate = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
        } else if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECTUSER:
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        mStaff = (Staff) bundle.getSerializable(Contacts.KEY_STAFF);
                        if (mStaff != null) {
                            btnReporter.setText(mStaff.StaffName);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void Go2Detail() {
        gv_queryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //position-- because for have headView index+1 .so -1;
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.e("zwt", "点击的下标" + position--);
                DFMainData base = mAdpater.getItem(position--);
                int status = base.getAppStatus();
                if (!"XAB".equals(Property.StationCode)) {
                    Intent go2Detail = new Intent(DeviceFaultListActivity.this, SYBDRHandlerActivity.class);
                    go2Detail.putExtra("bean", base);
                    go2Detail.putExtra("status", status);
                    startActivity(go2Detail);
                } else {
                    Intent go2Detail = new Intent(DeviceFaultListActivity.this, DRHandlerActivity.class);
                    go2Detail.putExtra("bean", base);
                    go2Detail.putExtra("status", status);
                    startActivity(go2Detail);
                }

//                Intent go2Detail = new Intent(DeviceFaultListActivity.this, SYBDRHandlerActivity.class);
//                go2Detail.putExtra("bean", base);
//                go2Detail.putExtra("status", status);
//                startActivity(go2Detail);

            }
        });
    }


    @Override
    public void showSearch(final boolean isShow) {
        LogUtil.e("isShow==" + isShow);


        if (isShow) {
            layoutSearch.setVisibility(View.VISIBLE);
        } else {
            layoutSearch.setVisibility(View.GONE);
        }

    }
}
