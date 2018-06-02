package easyway.Mobile.DefaultReportToRepair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import easyway.Mobile.R;
import easyway.Mobile.util.LogUtil;

/**
 * Created by JSC on 2017/12/4.
 */

public class RepairPicAdapter extends BaseAdapter {
    private Context context;
    private List<RepairPicBean.DataBean> list;

    public RepairPicAdapter(Context context, List<RepairPicBean.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_repair_pic_item_layout, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.repair_pic_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LogUtil.e("图片地址==" + list.get(position).getLinkUrl());
        Glide.with(context).load(list.get(position).getLinkUrl()).into(holder.imageView);
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
