package easyway.Mobile.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.List;

import easyway.Mobile.bean.VersionInfoResult;


/**
 * Created by boy on 2017/4/11.
 */

public class DataVersionsUtil {

    public static final String VERSIONS = "data_versions";

    public static HashMap<String, Integer> versions = new HashMap<>();

    private static SharedPreferences sharedPreferences;
    private Context context;


    public DataVersionsUtil() {
    }

    public DataVersionsUtil(Context context, String TAG) {
        this.context = context;
        sharedPreferences = getShare(TAG);
    }

    private SharedPreferences getShare(String TAG) {
        return context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    private void inputShare(String key, String content) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, content);
        editor.commit();
    }

    public void setEditionInfo(VersionInfoResult editionInfo) {
        if (editionInfo != null && editionInfo.getData() != null && editionInfo.getData().size() > 0) {
            List<VersionInfoResult.DataBean> dataBeanList = editionInfo.getData();
            for (VersionInfoResult.DataBean dataBean : dataBeanList) {
                inputShare(dataBean.getName(), dataBean.getVersion());
            }
        }
    }

    public int getEditionVersion(String key) {
        int version = -1;
        if (sharedPreferences.contains(key)) {
            version = sharedPreferences.getInt(key, -1);
        } else {
            inputShare(key, version);
        }
        return version;
    }


    /**
     * one item
     *
     * @param key
     * @param version
     */
    public void setVersion(String key, int version) {
        inputShare(key, version);
    }

    private void inputShare(String key, int version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, version);
        editor.commit();
    }
}
