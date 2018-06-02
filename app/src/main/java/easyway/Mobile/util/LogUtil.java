package easyway.Mobile.util;

import android.util.Log;

import java.io.File;

public class LogUtil {
    private static final String tag = "zwt";
    private static final boolean debug = false;

    public static void e(String msg) {
        if (debug) {
            Log.e(tag, msg);
            writerLog(msg);
        }
    }

    public static void w(String msg) {
        if (debug) {
            Log.w(tag, msg);
            writerLog(msg);
        }
    }

    public static void i(String msg) {
        if (debug) {
            Log.i(tag, msg);
            writerLog(msg);
        }
    }

    public static void d(String msg) {
        if (debug) {
            Log.d(tag, msg);
            writerLog(msg);
        }
    }

    public static void writerLog(String msg) {
        try {
            String path = "/sdcard/zwtlog.txt";
            File file = new File(path);
            long fileSizel = FileSizeUtil.getFileSize(file);
            double sizeStr = FileSizeUtil.FormetFileSize(fileSizel, FileSizeUtil.SIZETYPE_MB);
            if (sizeStr >= 10) {
                FileSizeUtil.deleteFile(path);
            }
            LogWriter mLogWriter = LogWriter.open("/sdcard/zwtlog.txt");
            mLogWriter.print(msg);
            mLogWriter.close();
        } catch (Exception e) {
//	        e.printStackTrace();
        }
    }

}
