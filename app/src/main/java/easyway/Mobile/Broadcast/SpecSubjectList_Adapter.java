package easyway.Mobile.Broadcast;

import java.util.ArrayList;

import easyway.Mobile.R;
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

public class SpecSubjectList_Adapter extends BaseAdapter {
    private ArrayList<BroadcastInfo> listBroadcastInfo = new ArrayList<BroadcastInfo>();
    private Context context;
    private LayoutInflater mInflater;

    public SpecSubjectList_Adapter(Context context,
            ArrayList<BroadcastInfo> listBroadcastInfo) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.listBroadcastInfo = listBroadcastInfo;
    }

    public int getCount() {
        return listBroadcastInfo.size();
    }

    public BroadcastInfo getItem(int position) {
        return listBroadcastInfo.get(position);
    }

    public long getItemId(int position) {
        return getItem(position).id;
    }

    public void setData(ArrayList<BroadcastInfo> models) {
        listBroadcastInfo = models;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.specsubject_list_item,
                    null);
            holder = new ViewHolder();
            holder.itemView = (LinearLayout) convertView
                    .findViewById(R.id.item_view);
            holder.BroadcastTitle = (TextView) convertView
                    .findViewById(R.id.BroadcastTitle);
            holder.BroadcastCategory = (TextView) convertView
                    .findViewById(R.id.BroadcastCategory);
            holder.PlayTime = (TextView) convertView
                    .findViewById(R.id.PlayTime);
            holder.OpStatus = (TextView) convertView
                    .findViewById(R.id.OpStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.BroadcastTitle.setText(context
                .getString(R.string.broad_spec_subject_title)
                + ":"
                + getItem(position).Title);

        holder.BroadcastCategory.setText(context
                .getString(R.string.broad_category)
                + ":"
                + getItem(position).Category);

        holder.PlayTime.setText(context.getString(R.string.broad_PlayTime) + ":"
                + getItem(position).PlayTime);

        // OpStatus
        String opStatus = getItem(position).OpStatus;
        if (StringUtil.isNullOrEmpty(opStatus)) {
            opStatus = context.getString(R.string.broad_play_waitting);
        }
        holder.OpStatus.setText(context.getString(R.string.broad_OpStatus) + ":"
                + opStatus);

        holder.itemView.setOnClickListener(viewLis(getItem(position).id));

        return convertView;
    }

    private OnClickListener viewLis(final long id) {
        return new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPlayRecord.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };
    }

    static class ViewHolder {
        View itemView;
        TextView BroadcastTitle;
        TextView BroadcastCategory;
        TextView PlayTime;
        TextView OpStatus;
    }

}
