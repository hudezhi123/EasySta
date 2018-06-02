package easyway.Mobile.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.FileReader;
import java.io.Reader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class CommonFunc {
    private static String Server = null;
    public static final String CONFIG = "USER_CONFIG";
    public static final String CONFIG_USERNAME = "USERNAME";
    public static final String CONFIG_PASSWORD = "PASSWORD";

    public static final String CONFIG_SessionId = "SessionId";
    public static final String CONFIG_StaffId = "StaffId";

    public static final String CONFIG_SERVER = "SERVER";

    public static String MAC_STR = "";
    public static String VERSION_NAME = "";

    /**
     * 日期转化为时间
     *
     * @param curDttm
     * @return
     * @author Tyr Tao
     */
    public static String ConvertData2Str(Date curDttm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(curDttm);
        return str;
    }

    public static String ConvertData2Str(Date curDttm, String dataFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
        String str = sdf.format(curDttm);
        return str;
    }

    /**
     * 获取本地IP地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        String ipAddress = "0.0.0.0";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress().toString();
                        if (!ipAddress.contains(":"))
                            return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return ipAddress;
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @param cache
     * @return
     */
    //DevicePolicyManagewr.getWifiMacAddress()
    public static String GetMac(Context context, ACache cache) {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            if (mac == null) {
                //WifiManager
                mac_s = GetMacAddr(context);
            } else {
                mac_s = byte2hex(mac);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cache.put("macAddress", mac_s);
        return mac_s;
    }

    /**
     * byte转换为16进制
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                if (n < len - 1) {
                    hs = hs.append(stmp).append(":");
                } else {
                    hs = hs.append(stmp);
                }

            }
        }
        return String.valueOf(hs);
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    public static String GetMacAddr(Context context) {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;

    }

    /**
     * 通过文件名导入文件
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);

        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    private static HashMap<String, Long> mpHandelId = new HashMap<String, Long>();

    private static void PutHandelId(String handleName, long handleId) {
        mpHandelId.put(handleName, handleId);
    }

    private static long GetHandelId(String handleName) {
        if (mpHandelId.containsKey(handleName)) {
            return mpHandelId.get(handleName);
        } else {
            return 0;
        }
    }

    /**
     * 获取C#的空与空格
     *
     * @param str
     * @return
     */
    public static String GetCSharpString(String str) {
        if (str == null)
            return "";
        if (str.equals("null") || str.equals("&nbsp;") || str == "null")
            return "";
        str = str.trim();
        return str;
    }

    /*
     * 获取服务器地址
     */
    public static String GetServer(Context context) {
        if (Server == null) {
            SharedPreferences sp = context.getSharedPreferences(
                    CommonFunc.CONFIG, 0);
            Server = sp.getString(CONFIG_SERVER, "");
        }

        return Server;
    }

    /**
     * 设定服务器地址
     *
     * @param server
     * @param context
     */
    public static void SetServer(String server, Context context) {
        if (server != null) {
            Server = server;
            SharedPreferences sp = context.getSharedPreferences(
                    CommonFunc.CONFIG, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CONFIG_SERVER, server);
            editor.commit();
        }
    }

    /**
     * 获取xml为json
     *
     * @param xml
     * @return
     */
    private static String GetXmlJson(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xml));
            Document dom = builder.parse(inputSource);
            Element root = dom.getDocumentElement();
            String json = root.getTextContent();
            return json;
        } catch (Exception ex) {
            return "";
        }
    }

    /*
     * 是否为数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String ClearSameItem(String str, String regularExpression) {
        String[] arr = str.split(regularExpression);
        ArrayList<String> arrList = new ArrayList<String>();
        for (String item : arr) {
            if (item == null) {
                continue;
            }
            item = item.trim();
            if (item.equals("")) {
                continue;
            }

            if (arrList.contains(item)) {
                continue;
            }

            arrList.add(item);
        }
        return arrList.toString();
    }

    /**
     * 向图片中添加数字显示
     *
     * @param srcImg
     * @param total
     * @return
     */
    public static Bitmap AddNum2Img(Bitmap srcImg, int total) {
        if (total <= 0)
            return srcImg;

        int width = srcImg.getWidth();

        Canvas canvas = new Canvas(srcImg);

        Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(Color.RED);
        countPaint.setTextSize(30f);

        countPaint.setTypeface(Typeface.DEFAULT_BOLD);
        String numValues = "";
        if (total > 99) {
            numValues = "99+";
            canvas.drawText(numValues, width - 52, 29, countPaint);
        } else if (total >= 10) {
            numValues = String.valueOf(total);
            canvas.drawText(numValues, width - 36, 29, countPaint);
        } else {
            numValues = String.valueOf(total);
            canvas.drawText(numValues, width - 20, 29, countPaint);
        }
        return srcImg;

    }

    /**
     * 获取提醒总数
     *
     * @param res
     * @param resId
     * @param total
     * @return
     */
    private static Bitmap GetNotifyWithTotal(Resources res, int resId, int total) {
        if (total <= 0)
            return null;
        Bitmap icon;
        Drawable iconDrawable = res.getDrawable(resId);
        if (iconDrawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) iconDrawable;
            icon = bd.getBitmap();
        } else {
            return null;
        }
        int iconSize = (int) res.getDimension(android.R.dimen.app_icon_size);
        Bitmap contactIcon = Bitmap.createBitmap(iconSize, iconSize,
                Config.ARGB_8888);

        Canvas canvas = new Canvas(contactIcon);

        // 拷贝图片
        Paint iconPaint = new Paint();
        iconPaint.setDither(true);// 防抖动
        iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
        Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
        Rect dst = new Rect(0, 0, iconSize, iconSize);
        canvas.drawBitmap(icon, src, dst, iconPaint);

		/*
         * Paint pCircleBig = new Paint(); pCircleBig.setColor(Color.RED);
		 * pCircleBig.setDither(true); pCircleBig.setAntiAlias(true);
		 * pCircleBig.setFilterBitmap(true); canvas.drawCircle(48, 23, 18,
		 * pCircleBig);
		 */

        // 启用抗锯齿和使用设备的文本字距
        Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(Color.RED);
        countPaint.setTextSize(30f);

        countPaint.setTypeface(Typeface.DEFAULT_BOLD);
        String numValues = "";
        if (total > 99) {
            numValues = "99+";
            canvas.drawText(numValues, iconSize - 38, 29, countPaint);
        } else if (total >= 10) {
            numValues = String.valueOf(total);
            canvas.drawText(numValues, iconSize - 36, 29, countPaint);
        } else {
            numValues = String.valueOf(total);
            canvas.drawText(numValues, iconSize - 20, 29, countPaint);
        }

        return contactIcon;
    }

    public static void randomSleep() {
        Random random = new Random();
        int sleep = random.nextInt() * 3000 + 1;
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
