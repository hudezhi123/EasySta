package easyway.Mobile.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    public static final int YYYY_MM_DD_T_HH_MM_SS_SSS = 1;
    public static final int YYYY_MM_DD_T_HH_MM_SS = 2;
    public static final int YYYY_MM_DD_HH_MM_SS_SSS = 3;
    public static final int YYYY_MM_DD_HH_MM_SS = 4;
    public static final int YYYY_MM_DD_HH_MM = 5;
    public static final int YYYY_MM_DD = 6;
    public static final int HH_MM_SS = 7;
    public static final int HH_MM = 8;
    public static final int DAYTIME = 1; //白天
    public static final int NIGHT = 2;  //晚上

    private static final String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static String getCurrDate() {
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month + 1;
        int date = t.monthDay;
        int hour = t.hour;
        int minute = t.minute;
        if (minute / 10 <= 0)
            return year + "-" + month + "-" + date + " " + hour + ":0" + minute;
        else
            return year + "-" + month + "-" + date + " " + hour + ":" + minute;
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static String getTodayTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
        return timeStamp;
    }

    public static String getWeekOfDate(Date dt) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week < 0)
            week = 0;
        return weekDays[week];
    }


    public static String getClassesOfDate(String date) {
        String str = "";
        try {

            if (date.contains("T")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date1 = sd.parse(date);
                long logTime = date1.getTime();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date1);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 001);
                long time8 = cal.getTime().getTime();

                Calendar cal1630 = Calendar.getInstance();
                cal1630.setTime(date1);
                cal1630.set(Calendar.HOUR_OF_DAY, 18);
                cal1630.set(Calendar.SECOND, 0);
                cal1630.set(Calendar.MINUTE, 30);
                cal1630.set(Calendar.MILLISECOND, 001);
                long time1630 = cal1630.getTime().getTime();
                if (logTime > time8 & logTime < time1630) {
                    str = "客运工作日志(白班)";
                } else {
                    str = "客运工作日志(夜班)";
                }

            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date2 = new Date(date);
                long logTime = date2.getTime();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date2);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 001);
                long time8 = cal.getTime().getTime();

                Calendar cal1830 = Calendar.getInstance();
                cal1830.setTime(date2);
                cal1830.set(Calendar.HOUR_OF_DAY, 18);
                cal1830.set(Calendar.SECOND, 0);
                cal1830.set(Calendar.MINUTE, 30);
                cal1830.set(Calendar.MILLISECOND, 0);
                long time1830 = cal1830.getTime().getTime();
                if (logTime > time8 & logTime < time1830) {
                    str = "客运工作日志(白班)";
                } else {
                    str = "客运工作日志(夜班)";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean isClassesOfDay(String date) {
        boolean isDay = false;
        try {

            if (date.contains("T")) {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date1 = sd.parse(date);
                long logTime = date1.getTime();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date1);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 001);
                long time8 = cal.getTime().getTime();

                Calendar cal1630 = Calendar.getInstance();
                cal1630.setTime(date1);
                cal1630.set(Calendar.HOUR_OF_DAY, 18);
                cal1630.set(Calendar.SECOND, 0);
                cal1630.set(Calendar.MINUTE, 30);
                cal1630.set(Calendar.MILLISECOND, 001);
                long time1630 = cal1630.getTime().getTime();
//                Log.e("DateUtil","logTime="+logTime+",time8="+time8+",time1630="+time1630);
                if (logTime > time8 & logTime < time1630) {
                    isDay = true;
                } else {
                    isDay = false;
                }

            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date2 = new Date(date);
                long logTime = date2.getTime();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date2);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 001);
                long time8 = cal.getTime().getTime();
                Calendar cal1830 = Calendar.getInstance();
                cal1830.setTime(date2);
                cal1830.set(Calendar.HOUR_OF_DAY, 18);
                cal1830.set(Calendar.SECOND, 0);
                cal1830.set(Calendar.MINUTE, 30);
                cal1830.set(Calendar.MILLISECOND, 001);
                long time1830 = cal1830.getTime().getTime();
                if (logTime > time8 & logTime < time1830) {
                    isDay = true;
                } else {
                    isDay = false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isDay;
    }

    public static boolean isClasses0_8() {
        boolean isDay = false;
        try {
            long nowTimeLong = System.currentTimeMillis();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 001);
            long time0 = cal.getTime().getTime();

            Calendar cal8 = Calendar.getInstance();
            cal8.set(Calendar.HOUR_OF_DAY, 8);
            cal8.set(Calendar.SECOND, 0);
            cal8.set(Calendar.MINUTE, 0);
            cal8.set(Calendar.MILLISECOND, 001);
            long time8 = cal8.getTime().getTime();
//                Log.e("DateUtil","logTime="+logTime+",time8="+time8+",time1630="+time1630);
            if (nowTimeLong > time0 & nowTimeLong < time8) {
                isDay = true;
            } else {
                isDay = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDay;
    }

    public static boolean isClassesOfNight() {
        boolean isNight = false;
        long nowTimeLong = System.currentTimeMillis();
        Calendar cal1830 = Calendar.getInstance();
        cal1830.set(Calendar.HOUR_OF_DAY, 18);
        cal1830.set(Calendar.SECOND, 0);
        cal1830.set(Calendar.MINUTE, 30);
        cal1830.set(Calendar.MILLISECOND, 0);
        long time1830 = cal1830.getTime().getTime();

        if (nowTimeLong > time1830) {
            isNight = true;
        }
        return isNight;
    }


    /**
     * 获取当前准确时间
     *
     * @return
     */
    public static String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dateStr = format.format(date);
        format = null;
        return dateStr;
    }

    /**
     * 获取当前时间 时分秒
     *
     * @return
     */
    public static String getTime() {
        String now = getNowDate();
        String time[] = now.split(" ");
        return time[1];
    }

    /**
     * @return
     */
    public static boolean IsDaytime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        int flag = getWhiteOrNight(dateStr);
        if (flag == NIGHT) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前时间 是白班还是夜班
     *
     * @param workDate
     * @return
     */
    public static int getWhiteOrNight(String workDate) {
        if (workDate.contains("T")) {
            workDate = workDate.replace("T", " ");
        }
        if (workDate.contains("-")) {
            String[] date_time = new String[2];
            date_time = workDate.split(" ");
            workDate = date_time[1];
        }
        String time[] = workDate.split(":");
        double Hour = Integer.parseInt(time[0]);
        double Minute = Integer.parseInt(time[1]);
        Hour += Minute / 60;
        if ((Hour >= 18.5 && Hour <= 24) || (Hour >= 0 && Hour <= 8)) {
            return NIGHT;
        } else {
            return DAYTIME;
        }
    }

    public static String formatDate(String str, int flag) {
        if (str.contains("T")) {
            str = str.replace("T", " ");
        }
        if (str.contains("-") && (flag == 7 || flag == 8)) {
            String[] date_time = new String[2];
            date_time = str.split(" ");
            str = date_time[1];
        }
        SimpleDateFormat format = new SimpleDateFormat();
        switch (flag) {
            case YYYY_MM_DD_T_HH_MM_SS_SSS:
                format.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                break;
            case YYYY_MM_DD_T_HH_MM_SS:
                format.applyPattern("yyyy-MM-dd'T'HH:mm:ss");
                break;
            case YYYY_MM_DD_HH_MM_SS_SSS:
                format.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
                break;
            case YYYY_MM_DD_HH_MM_SS:
                format.applyPattern("yyyy-MM-dd HH:mm:ss");
                break;
            case YYYY_MM_DD_HH_MM:
                format.applyPattern("yyyy-MM-dd HH:mm");
                break;
            case YYYY_MM_DD:
                format.applyPattern("yyyy-MM-dd");
                break;
            case HH_MM_SS:
                format.applyPattern("HH:mm:ss");
                break;
            case HH_MM:
                format.applyPattern("HH:mm");
                break;
        }
        ParsePosition pop = new ParsePosition(0);
        Date date = format.parse(str, pop);
        String result = format.format(date);
        return result;
    }

}
