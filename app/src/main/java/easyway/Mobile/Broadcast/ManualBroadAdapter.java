package easyway.Mobile.Broadcast;

import java.io.IOException;
import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.TB_PA_PlayRecords;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.StringUtil;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ManualBroadAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<TB_PA_PlayRecords> listPlayRecords;
    private Context context;
    private MediaPlayer mediaplayer = new MediaPlayer();

    public ManualBroadAdapter(Context context,
            ArrayList<TB_PA_PlayRecords> listPlayRecords) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.listPlayRecords = listPlayRecords;

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(ManualBroadAdapter.this.context);
			}
		});
    }

    public void onDestroy() {
        if (mediaplayer != null) {
            if (mediaplayer.isPlaying()) {
                mediaplayer.stop();
            }
            mediaplayer.release();
    		IntercomCtrl.open_intercom(context);
        }
    }


    public int getCount() {
        return listPlayRecords.size();
    }

    public TB_PA_PlayRecords getItem(int arg0) {
        return listPlayRecords.get(arg0);
    }

    public long getItemId(int arg0) {
        return getItem(arg0).id;
    }
    
    public void setData(ArrayList<TB_PA_PlayRecords> models) {
        listPlayRecords = models;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.manual_board_item, null);
            holder = new ViewHolder();
            holder.itemView = (LinearLayout) convertView
                    .findViewById(R.id.item_view);
            holder.OperatingArea = (TextView) convertView
                    .findViewById(R.id.OperatingArea);
            holder.Operator = (TextView) convertView
                    .findViewById(R.id.Operator);
            holder.PlayTime = (TextView) convertView
                    .findViewById(R.id.PlayTime);
            holder.OpStatus = (TextView) convertView
                    .findViewById(R.id.OpStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.OperatingArea.setText(context
                .getString(R.string.broad_OperatingArea)
                + ":"
                + getItem(position).OperatingArea);

        holder.Operator.setText(context.getString(R.string.broad_Operator) + ":"
                + getItem(position).Operator);

        holder.PlayTime.setText(context.getString(R.string.broad_PlayTime) + ":"
                + getItem(position).PlayTime);

        // OpStatus
        String opStatus = getItem(position).OpStatus;
        if (StringUtil.isNullOrEmpty(opStatus)) {
            opStatus = context.getString(R.string.broad_play_waitting);
        }
        holder.OpStatus.setText(context.getString(R.string.broad_OpStatus) + ":"
                + opStatus);

        holder.itemView.setOnClickListener(playAudioLis(getItemId(position)));

        return convertView;
    }

    private OnClickListener playAudioLis(final long playId) {
        return new OnClickListener() {
            public void onClick(View v) {
                String fileName = CommonFunc.GetServer(context)
                        + context.getString(R.string.broad_Audit_Remote_Path)
                        + String.valueOf(playId) + ".wav";
                Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.broad_manual);
                builder.setPositiveButton(R.string.OK, null);

                try {
                	IntercomCtrl.close_intercom(context);
                    mediaplayer.reset();
                    mediaplayer.setDataSource(fileName);
                    mediaplayer.prepare();
                    mediaplayer.start();
                } catch (IllegalStateException e) {
                    builder.setMessage(context
                            .getString(R.string.broad_record_play_exception_from_server)
                            + e.getMessage());
                    builder.show();
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    builder.setMessage(context
                            .getString(R.string.broad_record_play_exception_from_server));
                    builder.show();
                }
            }
        };
    }

    static class ViewHolder {
        View itemView;
        TextView OperatingArea;
        TextView Operator;
        TextView PlayTime;
        TextView OpStatus;
    }
}
