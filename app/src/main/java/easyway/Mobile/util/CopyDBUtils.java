package easyway.Mobile.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by boy on 2017/12/6.
 */

public class CopyDBUtils {

    public static final int CACHE = 0;
    public static final int FILES = 1;
    public static final int DATABASE = 2;

    private static byte[] otherReader(Context context, String fileName, int type) {
        // 判断文件名
        if (fileName == null || "".equals(fileName)) {
            return null;
        }
        FileInputStream inputStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File file = null;
        switch (type) {
            case CACHE:
                file = new File(context.getCacheDir(), fileName);
                break;
            case FILES:
                file = new File(context.getFilesDir(), fileName);
                break;
            case DATABASE:
                file = new File(context.getDatabasePath(fileName).getAbsolutePath());
                break;
        }
        // 操作文件
        try {
            inputStream = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
                baos.flush();
            }
            return baos.toByteArray();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param context
     * @param DB_NAME 数据库名称
     */
    public static void copyDB(Context context, String DB_NAME) {
        byte[] data = otherReader(context, DB_NAME, DATABASE);
        if (data == null) {
            return;
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir = new File(Environment.getExternalStorageDirectory(), "DB");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String timeStamp = format.format(new Date());
            File file = new File(dir, DB_NAME + timeStamp);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    OutputStream output = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(output);
                    bos.write(data);
                    output.close();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void Copy(Context context) {
        File file = context.getDatabasePath("Easyway_zwt");
        if (file.exists()) {
            try {
                FileInputStream input = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(input);
                File dir = new File(Environment.getExternalStorageDirectory(), "AB");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + "Easyway_zwt.db");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int len = 0;
                byte[] buffer = new byte[1024 * 8];
                while ((len = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                    bos.flush();
                }
                input.close();
                bis.close();
                bos.close();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
