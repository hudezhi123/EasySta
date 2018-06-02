package easyway.Mobile.Caution;

import java.io.File;
import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.DateUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * 记事本Adapter
 */
public class CautionListAdapter extends BaseAdapter {
    private ArrayList<Caution> Clist;
    private Context context;
    private LayoutInflater mInflater;
    private IOnDataChange iOnDataChange;

    public CautionListAdapter(Context context, ArrayList<Caution> list) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.Clist = list;
    }

    public int getCount() {
        if (Clist == null)
            return 0;
        return Clist.size();
    }

    public Caution getItem(int position) {
        if (Clist == null) {
            return null;
        } else {
            return Clist.get(position);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<Caution> models) {
        Clist = models;
    }

    public void setIOnDataChange(IOnDataChange iOnDataChange) {
        this.iOnDataChange = iOnDataChange;
    }

    public View getView(final int position, View convertView, ViewGroup arg2) {
        final Caution caution = getItem(position);
        if (caution == null)
            return null;

        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.caution_list_item, null);
            holder = new ViewHolder();
            holder.itemView = (RelativeLayout) convertView
                    .findViewById(R.id.item_view);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            holder.txtAttachPhoto = (TextView) convertView
                    .findViewById(R.id.txtPhoto);
            holder.txtAttachAudio = (TextView) convertView
                    .findViewById(R.id.txtAudio);
            holder.txtTitle = (TextView) convertView
                    .findViewById(R.id.txtTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTime.setText(DateUtil.formatDate(caution.time, DateUtil.HH_MM));
        if (CommonUtils.datecheck(caution.date + " " + caution.time))
            holder.txtTime.setTextColor(Color.BLACK);
        else
            holder.txtTime.setTextColor(Color.GRAY);

        // 音频附件
        if (caution.attachaudio == null || caution.attachaudio.equals(""))
            holder.txtAttachAudio.setVisibility(View.INVISIBLE);
        else
            holder.txtAttachAudio.setVisibility(View.VISIBLE);

        if (caution.isplay) // 音频是否播放中
            holder.txtAttachAudio.setBackgroundResource(R.drawable.audiostop);
        else
            holder.txtAttachAudio.setBackgroundResource(R.drawable.audioplay);

        holder.txtAttachAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iOnDataChange != null)
                    iOnDataChange.onPlay(caution.ID, !caution.isplay);
            }
        });

        // 图片附件
        if (caution.attachphoto == null || caution.attachphoto.equals(""))
            holder.txtAttachPhoto.setVisibility(View.INVISIBLE);
        else
            holder.txtAttachPhoto.setVisibility(View.VISIBLE);
        holder.txtAttachPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String filepath = CommonUtils.getFilePath(context)
                        + caution.attachphoto;

                File file = new File(filepath);
                if (file != null
                        && file.isFile() == true) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(
                            Uri.fromFile(file),
                            "image/*");
                    context.startActivity(intent);
                }
            }
        });

        // 标题
        holder.txtTitle.setText(caution.title);

        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CautionDetailActivity.class);
                intent.putExtra(CautionEditActivity.KEY_CAUTIONID, caution.ID);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (iOnDataChange != null)
                    iOnDataChange.onLongClick(caution.ID);
                return false;
            }

        });

        switch (caution.level) {
            case Caution.LEVEL_HIGH:
                holder.txtTitle.setTextColor(context.getResources().getColor(R.color.red));
                break;
            case Caution.LEVEL_NORMAL:
                holder.txtTitle.setTextColor(context.getResources().getColor(R.color.yellow));
                break;
            case Caution.LEVEL_LOW:
                holder.txtTitle.setTextColor(context.getResources().getColor(R.color.green));
                break;
            default:
                holder.txtTitle.setTextColor(context.getResources().getColor(R.color.gray));
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout itemView;
        TextView txtTime; // 时间
        TextView txtAttachPhoto; // 照片附件
        TextView txtAttachAudio; // 语音附件
        TextView txtTitle; // 标题 （+ 级别）
    }
}
