package easyway.Mobile.Shift;

import android.os.Handler;
import android.os.Message;

public class Shift_Out_Handler extends Handler
{
    private Shift_Out_Remark shiftOutRemark;

    public Shift_Out_Handler(Shift_Out_Remark shiftOutRemark)
    {
        this.shiftOutRemark = shiftOutRemark;
    }

    @Override
    public void handleMessage(Message message)
    {
        switch (message.what)
        {
            case 1:
                shiftOutRemark.finish();
                break;
            default:
                break;
        }
    }
}
