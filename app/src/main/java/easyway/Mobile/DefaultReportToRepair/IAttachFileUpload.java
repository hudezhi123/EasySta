package easyway.Mobile.DefaultReportToRepair;

import java.util.ArrayList;

/**
 * Created by boy on 2017/11/30.
 */

public interface IAttachFileUpload {
    void OnUploadEnd(int ret, ArrayList<String> lstFail);
}
