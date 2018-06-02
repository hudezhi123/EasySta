package easyway.Mobile.DevFault;

/**
 * Created by JSC on 2018/1/4.
 */

public class ShowSearchBarListenerManager {
    private DeviceFaultActivity.ShowSearchBarListener mListener;

    private ShowSearchBarListenerManager() {
    }

    private static final ShowSearchBarListenerManager manager = new ShowSearchBarListenerManager();

    public static ShowSearchBarListenerManager getInstance() {
        return manager;
    }

    public void setShowListener(DeviceFaultActivity.ShowSearchBarListener showSearchBarListener) {
       this.mListener = showSearchBarListener;
    }
    public void showSearch(boolean isShow){
        if(mListener!=null){
            mListener.showSearch(isShow);
        }
    }
}
