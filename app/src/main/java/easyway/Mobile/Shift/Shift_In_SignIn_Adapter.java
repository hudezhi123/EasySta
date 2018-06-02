package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.R;
import easyway.Mobile.Data.Duty_Staff_Shift;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Shift_In_SignIn_Adapter extends BaseAdapter
{

    // private Context context;
    private ArrayList<Duty_Staff_Shift> staff_Shift_List = new ArrayList<Duty_Staff_Shift>();
    private HashMap<Long, String> hmUnSignSigned = new HashMap<Long, String>();
    public Shift_In_SignIn_Handler handler;
    LayoutInflater infl = null;

    public Shift_In_SignIn_Adapter(Context context,
            ArrayList<Duty_Staff_Shift> staff_Shift_List,
            HashMap<Long, String> hmUnSignSigned)
    {
        // this.context = context;
        this.staff_Shift_List = staff_Shift_List;
        this.hmUnSignSigned = hmUnSignSigned;
        this.infl = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount()
    {

        return staff_Shift_List.size();
    }

    public Duty_Staff_Shift getItem(int position)
    {
        return staff_Shift_List.get(position);
    }

    public long getItemId(int position)
    {
        return staff_Shift_List.get(position).SID;
    }

    private OnCheckedChangeListener chkSIdChgLis(
            final Duty_Staff_Shift staffShift)
    {
        return new OnCheckedChangeListener()
        {

            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked)
            {
                int what = 0;
                if (isChecked)
                {
                    what = 1;
                }
                else
                {
                    what = 0;
                }
                Message message = Message.obtain(handler, what, staffShift);
                handler.sendMessage(message);
            }
        };
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Duty_Staff_Shift staffShift = getItem(position);

        convertView = infl.inflate(R.layout.shift_in_sign_in_list_item, null);

        // CheckBox
        CheckBox chkSId = (CheckBox) convertView.findViewById(R.id.chkSId);
        chkSId.setChecked(!hmUnSignSigned.containsKey(staffShift.staffId));
        chkSId.setOnCheckedChangeListener(chkSIdChgLis(staffShift));
        chkSId.setText(staffShift.staffName);
        chkSId.setTextColor(Color.BLACK);
        return convertView;
    }

}
