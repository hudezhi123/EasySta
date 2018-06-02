package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.TB_Shift_Out_Train;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Shift_In_TrainAdapter extends BaseAdapter
{

    private ArrayList<TB_Shift_Out_Train> todoList;
    private Context context;
    LayoutInflater infl = null;
    private long shiftId = 0;

    public Shift_In_TrainAdapter(Context context,
            ArrayList<TB_Shift_Out_Train> todoList, long shiftId)
    {

        this.context = context;
        this.todoList = todoList;
        this.infl = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.shiftId = shiftId;
    }

    public int getCount()
    {
        if (todoList == null)
            return 0;
        return todoList.size();
    }

    public TB_Shift_Out_Train getItem(int position)
    {
        if (getCount() == 0)
            return null;
        return todoList.get(position);
    }

    public long getItemId(int position)
    {
        TB_Shift_Out_Train shiftOutTrain = getItem(position);
        if (shiftOutTrain == null)
            return 0;
        else
            return shiftOutTrain.Shift_Out_Train_Id;
    }

    private OnClickListener remarkLis(final String trainNum)
    {
        return new OnClickListener()
        {

            public void onClick(View v)
            {
                Intent intent = new Intent(context, Shift_In_Remark.class);
                Bundle bundle = new Bundle();
                bundle.putString("TrainNo", trainNum);
                bundle.putLong("ShiftId", shiftId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };
    }

    private OnClickListener showTrainTasks(final String trainNum)
    {
        return new OnClickListener()
        {

            public void onClick(View v)
            {
                Intent intent = new Intent(context,
                        Shift_In_Show_Team_Train_Task.class);
                Bundle bundle = new Bundle();
                bundle.putString("TrainNo", trainNum);
                bundle.putLong("ShiftId", shiftId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TB_Shift_Out_Train shiftOutTrain = getItem(position);
        if (shiftOutTrain == null)
            return null;
        long shift_Detail_Id = shiftOutTrain.Shift_Detail_Id;
        final String trainNum = shiftOutTrain.TrainNum;

        convertView = infl.inflate(R.layout.shift_in_train_list_item, null);

        TextView labTrainNum = (TextView) convertView
                .findViewById(R.id.labTrainNum);

        Button btnRemarksButton = (Button) convertView
                .findViewById(R.id.btnRemarks), btnShiftOutInfo = (Button) convertView
                .findViewById(R.id.btnShiftOutInfo);

        labTrainNum.setText(trainNum);
        btnShiftOutInfo.setOnClickListener(remarkLis(trainNum));
        if (shift_Detail_Id == 0)
        {
            btnRemarksButton.setVisibility(View.GONE);
        }
        else
        {
            btnRemarksButton.setOnClickListener(showTrainTasks(trainNum));
        }

        return convertView;
    }

}
