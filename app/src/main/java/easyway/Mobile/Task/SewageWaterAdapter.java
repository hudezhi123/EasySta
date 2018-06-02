package easyway.Mobile.Task;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.Data.SewageWaterResult;
import easyway.Mobile.DevFault.TaskExtractSwageActivity;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.JsonUtil;

/**
 * Created by boy on 2017/5/21.
 */

public class SewageWaterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SewageWaterResult.DataBean> dataBeanList;
    private Context context;
    private Handler handler;

    public SewageWaterAdapter(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        dataBeanList = new ArrayList<SewageWaterResult.DataBean>();
    }

    public void setData(List<SewageWaterResult.DataBean> list) {
        this.dataBeanList = list;
        notifyDataSetChanged();
    }


    public void updateItem(int position, SewageWaterResult.DataBean dataBean) {
        notifyItemChanged(position, dataBean);
    }

    @Override
    public SewageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sewage_water_item, parent, false);
        SewageHolder holder = new SewageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SewageWaterResult.DataBean dataBean = dataBeanList.get(position);
        if (holder instanceof SewageHolder) {
            ((SewageHolder) holder).textName.setText(dataBean.getTaskName());
            if (dataBean.isAllowed()) {
                if (dataBean.isSubmit()) {
                    ((SewageHolder) holder).btnSubmit.setText("已提交");
                    ((SewageHolder) holder).btnSubmit.setBackgroundColor(Color.GRAY);
                    ((SewageHolder) holder).btnSubmit.setClickable(false);
                    ((SewageHolder) holder).btnSubmit.setOnClickListener(null);
                } else {
                    ((SewageHolder) holder).btnSubmit.setText("提交");
                    ((SewageHolder) holder).btnSubmit.setBackgroundResource(R.color.green);
                    ((SewageHolder) holder).btnSubmit.setClickable(true);
                    ((SewageHolder) holder).btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final SewageWaterResult.DataBean bean = dataBean;
                                    bean.setSubmit(true);
                                    setStatus(position, bean);
                                }
                            }).start();
                        }
                    });
                }
            } else {
                ((SewageHolder) holder).btnSubmit.setText("提交");
                ((SewageHolder) holder).btnSubmit.setBackgroundColor(Color.GRAY);
                ((SewageHolder) holder).btnSubmit.setClickable(false);
                ((SewageHolder) holder).btnSubmit.setOnClickListener(null);
            }
        }
    }


    private void setStatus(int position, SewageWaterResult.DataBean dataBean) {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("taskId", String.valueOf(dataBean.getTaskId()));
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.SEND_CALLBACK_SEWAGEWATER;
        WebServiceManager webServiceManager = new WebServiceManager(
                context, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (TextUtils.isEmpty(result)) {
            return;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        Message message = handler.obtainMessage();
        if (Code == TaskExtractSwageActivity.CODE_NORMAL) {
            message.obj = dataBean;
            message.arg1 = position;
            message.what = TaskExtractSwageActivity.CODE_NORMAL;
        } else if (Code == TaskExtractSwageActivity.CODE_ERROR) {
            String msg = JsonUtil.GetJsonString(result, "Msg");
            message.obj = msg;
            message.what = TaskExtractSwageActivity.CODE_ERROR;
        }
        handler.sendMessage(message);
    }

    @Override
    public int getItemCount() {
        return dataBeanList.size();
    }

    class SewageHolder extends RecyclerView.ViewHolder {
        TextView textName;
        Button btnSubmit;

        public SewageHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_task_sewage_name);
            btnSubmit = (Button) itemView.findViewById(R.id.btn_submit_status_sewage);
        }
    }
}
