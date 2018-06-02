package easyway.Mobile.DevFault;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import easyway.Mobile.Data.Result;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.DFMainData;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.ShowProgress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * 璁惧鎶ラ殰Adapter
 */
public class DFAdapter extends BaseAdapter {
    private ArrayList<DFMainData> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    Handler myhandle;

    public DFAdapter(Context context, ArrayList<DFMainData> devFaultList,
                     Handler myhandle) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = devFaultList;
        this.myhandle = myhandle;
    }

    public int getCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    public DFMainData getItem(int position) {
        if (mList == null)
            return null;

        if (position >= getCount()) {
            return null;
        } else {
            return mList.get(position);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<DFMainData> devFaultList) {
        mList = devFaultList;
    }

    @SuppressLint("ResourceAsColor")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DFMainData faultBean = mList.get(position);
        ViewHolder holder;

        convertView = mInflater.inflate(R.layout.device_fault_item, null);
        holder = new ViewHolder();
        holder.itemView = (LinearLayout) convertView.findViewById(R.id.item_view);
        holder.DevName = (TextView) convertView.findViewById(R.id.DevName);
        holder.FaultContent = (TextView) convertView
                .findViewById(R.id.FaultContent);
        holder.ConfirmStatus = (TextView) convertView
                .findViewById(R.id.ConfirmStatus);
        holder.Reporteder = (TextView) convertView
                .findViewById(R.id.Reporteder);
        holder.ReportedTime = (TextView) convertView
                .findViewById(R.id.ReportedTime);
        holder.btn_ok = (Button) convertView.findViewById(R.id.btn_ok);

        long Id = faultBean.getId();

        holder.btn_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShowProgress.showProgressDialog(mContext.getResources().getString(R.string.Loading), mContext);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        getOkData(faultBean);
                    }
                }).start();

            }
        });

        holder.DevName.setText(faultBean.getDevName());
        holder.FaultContent.setText(faultBean.getFaultContent());
        holder.ConfirmStatus.setText(getAppStatus(faultBean.getAppStatus(), holder.ConfirmStatus));
        holder.Reporteder.setText(faultBean.getReporteder());
        holder.ReportedTime.setText(DateUtil.formatDate(faultBean.getReportedTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
        if (faultBean.getAppStatus() == 1) {
            holder.btn_ok.setVisibility(View.VISIBLE);
        } else {
            holder.btn_ok.setVisibility(View.GONE);
        }
        long time_ = 0;
        SimpleDateFormat format = null;
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        if (faultBean.getAppStatus() != 1 && faultBean.getAppStatus() != 2) {
            long current_time = System.currentTimeMillis();
            try {
                Date date = format.parse(faultBean.getReportedTime());
                time_ = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long deltaTime = current_time - time_;
            if (deltaTime > 0 && deltaTime < 24 * 60 * 60 * 1000) {
                // theam_gray
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.theme_gray));
            } else if (deltaTime >= 24 * 60 * 60 * 1000 && deltaTime <= 48 * 60 * 60 * 1000) {
                // yellow
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.theam_yellow));
            } else if (deltaTime > 48 * 60 * 60 * 1000) {
                // red
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.theam_red));
            }

        } else {
            // theam_gray
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.theme_gray));
        }
        String devissueimgfrom = CommonFunc
                .GetCSharpString(faultBean.getDevIssueImgFrom());

        boolean hasAttach = false;

        if (!devissueimgfrom.equals("")) {
            String[] listAppendix = devissueimgfrom.split(";");
            if (listAppendix.length > 0) {
                final ArrayList<String> list = new ArrayList<String>();
                for (String filePath : listAppendix) {
                    File file = new File(filePath);
                    String fileName = file.getName();
                    int i = fileName.lastIndexOf('.');
                    if ((i > -1) && (i < (fileName.length() - 1))) {
                        String extension = fileName.substring(i + 1);
                        if (extension.equals("")) {
                            continue;
                        }
                        if (extension.equals("jpg") || extension.equals("jpeg")
                                || extension.equals("gif")
                                || extension.equals("bmp")
                                || extension.equals("png")
                                || extension.equals("wav")) {
                            list.add(filePath);
                        }
                    }

                }
                hasAttach = true;
            }

        } else {

        }

        return convertView;
    }

    private void MaintenanceConfirm(final int id) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("dfId", String.valueOf(id));
                String methodPath = Constant.MP_DEVFAULT;
                String methodName = Constant.MN_GET_ok_FAULT;
                WebServiceManager webServiceManager = new WebServiceManager(mContext,
                        methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);
                Gson gson = new Gson();
                Type type = new TypeToken<Result<String>>() {
                }.getType();
                Result<String> ServerData = gson.fromJson(result, type);
                if (result != null && result.length() > 0) {
                    if (ServerData.isMsgType()) {
                        myhandle.sendMessage(myhandle.obtainMessage(DFList.CONFIRM_SUCCESS, ServerData.getData()));
                    } else {
                        myhandle.sendEmptyMessage(DFList.CONFIRM_FAIL);
                    }
                } else {
                    myhandle.sendEmptyMessage(DFList.CONFIRM_ERROR);
                }

            }

            ;
        }.start();
    }

    /**
     * @param faultBean
     */
    protected void getOkData(DFMainData faultBean) {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("dfId", String.valueOf(faultBean.getId()));
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.MN_GET_ok_FAULT;
        WebServiceManager webServiceManager = new WebServiceManager(mContext,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            myhandle.sendEmptyMessage(5);
        }
        Message msg = new Message();
        msg.obj = result;
        msg.what = 4;
        myhandle.sendMessage(msg);

    }

    private String getAppStatus(int value, TextView textView) {
        String appstatus_str = null;
        if (value == 0) {
            appstatus_str = mContext.getResources().getString(
                    R.string.dev_fault_summit_audit);
            textView.setTextColor(mContext.getResources().getColor(R.color.red));
        } else if (value == 1) {
            appstatus_str = mContext.getResources().getString(
                    R.string.dev_fault_audit_pass);
            textView.setTextColor(mContext.getResources().getColor(R.color.color_3D86C8));
        } else if (value == 2) {
            appstatus_str = mContext.getResources().getString(
                    R.string.dev_fault_repair_finish);
            textView.setTextColor(mContext.getResources().getColor(R.color.gray));
        } else if (value == 3) {
            appstatus_str = mContext.getResources().getString(
                    R.string.dev_fault_repairing);
            textView.setTextColor(mContext.getResources().getColor(R.color.green));
        } else if (value == 4) {
            appstatus_str = mContext.getResources().getString(
                    R.string.dev_fault_stopRepair);
            textView.setTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        return appstatus_str;
    }

    private static class ViewHolder {
        LinearLayout itemView;
        TextView DevName;
        TextView FaultContent;
        TextView Remarks;
        TextView ConfirmStatus;
        TextView Reporteder;
        TextView ReportedTime;
        Button btn_completion, btn_ok;

    }

}
