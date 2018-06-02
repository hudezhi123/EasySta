package easyway.Mobile.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import easyway.Mobile.Data.GetTaskActorResult;
import easyway.Mobile.R;

/**
 * Created by boy on 2017/5/3.
 */

public class SecureTipsAdapter extends BaseAdapter {
    private Context context;
    private List<GetTaskActorResult.DataBean> dataBeanList;

    public SecureTipsAdapter() {
    }

    public SecureTipsAdapter(Context context, List<GetTaskActorResult.DataBean> dataBeanList) {
        this.context = context;
        this.dataBeanList = dataBeanList;
    }

    private void setList(List<GetTaskActorResult.DataBean> dataBeanList) {
        this.dataBeanList = dataBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataBeanList.size();
    }

    @Override
    public GetTaskActorResult.DataBean getItem(int position) {
        return dataBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GetTaskActorResult.DataBean dataBean = getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.secure_tips_item, parent, false);
            viewHolder.textTitle = (TextView) convertView.findViewById(R.id.secure_tips_item_title);
            viewHolder.textContent = (TextView) convertView.findViewById(R.id.text_secure_tips_content);
            viewHolder.textDuty = (TextView) convertView.findViewById(R.id.text_secure_tips_duty);
            viewHolder.textRemark = (TextView) convertView.findViewById(R.id.text_secure_tips_remark);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textTitle.setText(dataBean.getTitle());
        viewHolder.textContent.setText(dataBean.getContent());
        viewHolder.textDuty.setText(dataBean.getDuty());
        viewHolder.textRemark.setText(dataBean.getRemark());
        return convertView;
    }

    private static class ViewHolder {
        TextView textTitle, textContent, textDuty, textRemark;
    }
}
