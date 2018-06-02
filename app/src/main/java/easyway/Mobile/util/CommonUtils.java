package easyway.Mobile.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import easyway.Mobile.Property;
import easyway.Mobile.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

@SuppressLint("SimpleDateFormat")
public class CommonUtils {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    /**
     * 检查设备是否提供摄像头
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true; // 摄像头存在
        } else {
            return false; // 摄像头不存在
        }
    }

    public static Uri getOutputMediaFileUri(Context context, int type) {
        File picFile = getOutputMediaFile(context, type);
        if (picFile == null)
            return null;
        return Uri.fromFile(picFile);
    }

    /**
     * 为保存图片或视频创建File
     */
    private static File getOutputMediaFile(Context context, int type) {

        File mediaStorageDir = new File(
                context.getString(R.string.config_attach_dir));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.d("failed to create directory");
                return null;
            }
        }

        // 创建媒体文件名
        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        switch (type) {
            case MEDIA_TYPE_IMAGE:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + Property.StaffId + "_" + timeStamp + ".jpg");
                break;
            case MEDIA_TYPE_AUDIO:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "Audio_" + Property.StaffId + "_" + timeStamp + ".wav");
                break;
            case MEDIA_TYPE_VIDEO:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "VID_" + Property.StaffId + "_" + timeStamp + ".3gp");
                break;
            default:
                mediaFile = null;
                break;
        }

        return mediaFile;
    }

    public static String getFilePath(Context context) {
        return context.getString(R.string.config_attach_dir);
    }

    /**
     * 判断文件是否存在
     */
    public static boolean fileIsExists(String filepath) {
        if (filepath == null)
            return false;

        try {
            File f = new File(filepath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // 生成文件保存地址
    public static String getUpdateFile(Context context, String url) {
        String fileShortName = getFileNameFromPath(url);
        String localPath = context.getString(R.string.config_attach_dir);
        File parentDir = new File(localPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        return localPath + File.separator + fileShortName;
    }

    // 从网络获取文件
    public static boolean getFileFromServer(String WebURL, String filePath) {
        try {
            URL url = new URL(WebURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            if (is != null) {
                File file = new File(filePath);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024 * 40];
                int len;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                bis.close();
                is.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getFileFromServerNew(String WebURL, String filePath) {
        try {

            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(WebURL);
            HttpResponse response;

            response = client.execute(get);
            HttpEntity entity = response.getEntity();

            InputStream is = entity.getContent();
            FileOutputStream fileOutputStream = null;
            if (is != null) {

                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }

                fileOutputStream = new FileOutputStream(file);

                byte[] b = new byte[1024];
                int charb = -1;
                while ((charb = is.read(b)) != -1) {
                    fileOutputStream.write(b, 0, charb);

                }
            }
            fileOutputStream.flush();
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date GetCSharpDate(String value) {
        if (value == null)
            return null;

        try {
            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date d = sdf.parse(value);
            return d;
        } catch (Exception ex) {
            return null;
        }
    }

    // 获取下一天
    public static String GetNextDate(String str) {
        if (str == null)
            return "";

        String result = "";
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(str);
            date.setTime(date.getTime() + 1000 * 60 * 60 * 24);
            result = format.format(date);
        } catch (ParseException e) {
        }

        return result;
    }

    // 获取前一天
    public static String GetPreviousDate(String str) {
        if (str == null)
            return "";

        String result = "";
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(str);
            date.setTime(date.getTime() - 1000 * 60 * 60 * 24);
            result = format.format(date);
        } catch (ParseException e) {
        }

        return result;
    }

    @SuppressLint("SimpleDateFormat")
    public static String ConvertDate(Date date) {
        if (date == null)
            return "";
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format1.format(date);
    }

    /**
     * 时间转成 "MM-dd HH:mm"
     * <p>
     * Date
     *
     * @return String
     */
    public static String date2String(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat strFormat = new SimpleDateFormat("HH:mm");
        String result = "";
        try {
            result = strFormat.format(FRIEND_MANAGER_FORMATTER.parse(str));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * 时间转成 "yyyy-MM-dd"
     * <p>
     * Date
     *
     * @return String
     */
    public static String date2Stringyyyymmdd(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat strFormat = new SimpleDateFormat("yyyy-MM-dd");
        String result = "";
        try {
            result = strFormat.format(FRIEND_MANAGER_FORMATTER.parse(str));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * 临时添加
     * @param str
     * @return
     */
    public static String date2StringT(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat strFormat = new SimpleDateFormat("yyyy-MM-dd");
        String result = "";
        try {
            result = strFormat.format(FRIEND_MANAGER_FORMATTER.parse(str));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * 时间转成 "HH:mm:ss"
     * <p>
     * Date
     *
     * @return String
     */
    public static String date2StringHHmmss(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat strFormat = new SimpleDateFormat("HH:mm:ss");
        String result = "";
        try {
            result = strFormat.format(FRIEND_MANAGER_FORMATTER.parse(str));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * 时间转成 "HH:mm"
     * <p>
     * Date
     *
     * @return String
     */
    public static String date2StringHHmm(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat strFormat = new SimpleDateFormat("HH:mm");
        String result = "";
        try {
            result = strFormat.format(FRIEND_MANAGER_FORMATTER.parse(str));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }


    public static String date2StringHHmmT(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat strFormat = new SimpleDateFormat("HH:mm");
        String result = "";
        try {
            result = strFormat.format(FRIEND_MANAGER_FORMATTER.parse(str));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * 时间转成 "HH:mm"
     * <p>
     * Date
     *
     * @return String
     */
    public static String HHmmss2StringHHmm(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "HH:mm:ss");
        SimpleDateFormat strFormat = new SimpleDateFormat("HH:mm");
        String result = "";
        try {
            result = strFormat.format(FRIEND_MANAGER_FORMATTER.parse(str));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }



    // 判断时间与当前时刻
    @SuppressLint("SimpleDateFormat")
    public static boolean datecheck(String str) {
        /**
         * 日期formatter
         */
        SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        try {
            Date date = FRIEND_MANAGER_FORMATTER.parse(str);
            if (date.getTime() > System.currentTimeMillis())
                return true;
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return false;
    }



    public static boolean isToday(String str) {
        if (str == null)
            return false;

        SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd");
        String today = Format.format(new Date(System.currentTimeMillis()));
        if (today.equals(str))
            return true;
        else
            return false;
    }

    public static String getFileNameFromPath(String filePath) {
        if (filePath == null) {
            return "";
        }

        if (!filePath.contains("/")) {
            return filePath;
        }

        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    // 删除文件、文件夹
    public static void deleteFile(File file) {
        if (file == null)
            return;

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
        }
    }
}
