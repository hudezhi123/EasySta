package easyway.Mobile.LiveCase;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Attach.AttachList;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 情况上报Adapter
 */
public class LiveCaseListAdapter extends BaseAdapter {
    private ArrayList<LiveCaseReport> LClist;
    private Context context;
    private LayoutInflater mInflater;

    public LiveCaseListAdapter(Context context,
            ArrayList<LiveCaseReport> list) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        if (list == null) {
        	list = new ArrayList<LiveCaseReport>();
        }
        this.LClist = list;
    }

    public int getCount() {
        return LClist.size();
    }

    public LiveCaseReport getItem(int position) {
        if (position >= getCount()) {
            return null;
        } else {
            return LClist.get(position);
        }
    }

    public long getItemId(int position) {
        return 0;
    }

    public void setData(ArrayList<LiveCaseReport> models) {
    	LClist = models;
    }

    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.livecase_list_item, null);
            holder = new ViewHolder();
            holder.itemView = (LinearLayout) convertView
                    .findViewById(R.id.item_view);
            holder.reprotType = (TextView) convertView
                    .findViewById(R.id.reprotType);
            holder.reportLevel = (TextView) convertView
                    .findViewById(R.id.reportLevel);
            holder.reportTime = (TextView) convertView
                    .findViewById(R.id.reportTime);
            holder.reportor = (TextView) convertView
                    .findViewById(R.id.reportor);
            holder.remark = (TextView) convertView.findViewById(R.id.remark);
            holder.statusConfirm = (TextView) convertView
                    .findViewById(R.id.statusConfirm);
            holder.imgattach = (TextView) convertView
                    .findViewById(R.id.imgattach);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.reprotType.setText(LClist.get(position).ReportedType);
        holder.reportLevel.setText(LClist.get(position).ReportedLevel);
        holder.reportTime
                .setText(CommonUtils.ConvertDate(LClist
                                .get(position).ReportedTime));
        holder.reportor.setText(LClist.get(position).Reporteder);
        
        String remarks = LClist.get(position).Remarks;
        if (remarks == null) {
        	holder.remark.setText("");
        } else {
        	remarks = StringUtil.Encode(remarks, false);
            holder.remark.setText(remarks);
        }
        holder.statusConfirm.setText(LClist.get(position).ConfirmStatus);
        String appendixUrl = CommonFunc.GetCSharpString(LClist
                .get(position).AppendixUrl);
        
        if (!appendixUrl.equals("")) {
            String[] listAppendix = appendixUrl.split(";");
            final ArrayList<String> list = new ArrayList<String>();
            for (String charSequence : listAppendix) {
                list.add(charSequence);
            }

            if (listAppendix.length > 0) {
                holder.itemView.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        Intent intent = new Intent(context,
                                AttachList.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("url", list);
//                        bundle.putString("liveCaseReportId", String
//                                .valueOf(LClist.get(position).id));
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            }
            
            if (listAppendix.length > 0) {
            	holder.imgattach.setVisibility(View.VISIBLE);
            } else {
            	holder.imgattach.setVisibility(View.GONE);
            }
        } else {
        	holder.imgattach.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
        }

        return convertView;
    }

    static class ViewHolder {
        View itemView;
        TextView reprotType;
        TextView reportLevel;
        TextView reportor;
        TextView reportTime;
        TextView remark;
        TextView statusConfirm;
        TextView imgattach;
    }
}
