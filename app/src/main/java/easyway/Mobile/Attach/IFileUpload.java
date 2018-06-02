package easyway.Mobile.Attach;

import java.util.ArrayList;

public interface IFileUpload {
	void OnUploadEnd(int ret, ArrayList<String> lstFail);
}
