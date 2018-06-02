package easyway.Mobile.Broadcast;

import easyway.Mobile.R;
import android.os.Handler;
import android.os.Message;

public class BroadcastPostHandler extends Handler
{
    private BroadcastManualAddActivity boardAddM;

    public BroadcastPostHandler(BroadcastManualAddActivity boardAddM)
    {
        this.boardAddM = boardAddM;
    }

    @Override
    public void handleMessage(Message message)
    {
        switch (message.what)
        {
            case R.string.broad_area_post_success:
                boardAddM.CloseForm();
                break;

            default:
                break;
        }
    }
}
