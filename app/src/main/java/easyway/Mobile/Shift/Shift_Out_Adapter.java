package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Shift_Out_Adapter extends BaseAdapter
{

    private ArrayList<String> todoList;
    private Context context;
    LayoutInflater infl = null;
    private long teamId = 0;
//    private Handler handel = null;
    private ArrayList<String> tempList = new ArrayList<String>();

    public Shift_Out_Adapter(Context context, ArrayList<String> todoList,
            long teamId, Handler handel)
    {
        this.context = context;
        this.todoList = todoList;
        this.teamId = teamId;
//        this.handel = handel;
        this.infl = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount()
    {
        if (todoList == null)
            return 0;
        return todoList.size();
    }

    public String getItem(int position)
    {
        if (getCount() == 0)
            return null;
        return todoList.get(position);
    }

    public long getItemId(int position)
    {
        return 0;
    }

    private OnClickListener remarkLis(final String trainNum)
    {
        return new OnClickListener()
        {

            public void onClick(View v)
            {
                Intent intent = new Intent(context, Shift_Out_Remark.class);
                Bundle bundle = new Bundle();
                bundle.putString("TrainNo", trainNum);
                bundle.putLong("TeamId", teamId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };
    }

    private boolean isChoosed(String trainNum)
    {
        return tempList.contains(trainNum);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final String trainNum = getItem(position);
        final ViewHolder holder;
        if (null == convertView)
        {
            convertView = infl
                    .inflate(R.layout.shift_out_train_list_item, null);
            holder = new ViewHolder();

            holder.itemView = (RelativeLayout) convertView
                    .findViewById(R.id.item_View);
            holder.chkTrainNum = (ImageView) convertView
                    .findViewById(R.id.chkTrainNum);
            holder.labTrainNum = (TextView) convertView
                    .findViewById(R.id.labTrainNum);

            holder.btnRemarksButton = (Button) convertView
                    .findViewById(R.id.btnRemarks);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.labTrainNum.setText(trainNum);
        holder.chkTrainNum
                .setImageResource(isChoosed(trainNum) ? R.drawable.checkbox_selected
                        : R.drawable.checkbox_normal);
        holder.itemView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                holder.chkTrainNum
                        .setImageResource(R.drawable.checkbox_selected);
                Log.d("Shift_Out_Adapter", "...................");
                if (!isChoosed(trainNum))
                {
                    holder.chkTrainNum
                            .setImageResource(R.drawable.checkbox_selected);

                    tempList.add(trainNum);
                }
                else
                {
                    holder.chkTrainNum
                            .setImageResource(R.drawable.checkbox_normal);
                    tempList.remove(trainNum);
                }
            }
        });

        holder.btnRemarksButton.setOnClickListener(remarkLis(trainNum));

        // convertView = infl.inflate(R.layout.shift_out_train_list_item, null);
        //
        // CheckBox chkTrainNum = (CheckBox) convertView
        // .findViewById(R.id.chkTrainNum);
        // chkTrainNum.setOnCheckedChangeListener(new OnCheckedChangeListener()
        // {
        //
        // @Override
        // public void onCheckedChanged(CompoundButton buttonView,
        // boolean isChecked)
        // {
        // int what = 0;
        // if (isChecked)
        // {
        // what = 1;
        // }
        // else
        // {
        // what = 0;
        // }
        // Message message = Message.obtain(handel, what, trainNum);
        // handel.sendMessage(message);
        // }
        // });
        // TextView labTrainNum = (TextView) convertView
        // .findViewById(R.id.labTrainNum);
        //
        // Button btnRemarksButton = (Button) convertView
        // .findViewById(R.id.btnRemarks);
        //
        // labTrainNum.setText(trainNum);
        // btnRemarksButton.setOnClickListener(remarkLis(trainNum));

        return convertView;
    }

    private static class ViewHolder
    {
        private View itemView;
        private ImageView chkTrainNum;
        private TextView labTrainNum;
        private Button btnRemarksButton;
    }
}
