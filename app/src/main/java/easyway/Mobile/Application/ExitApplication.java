package easyway.Mobile.Application;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import easyway.Mobile.Interface.Backlog;
import easyway.Mobile.LockScreenShow.ImMessage;
import easyway.Mobile.Shift.IAddStaff;
import easyway.Mobile.util.LogUtil;

import android.app.Activity;
import android.app.Application;

public class ExitApplication extends Application implements UncaughtExceptionHandler {
    public static boolean isBoYuan = true;
    public static boolean isX_Chat_Start = false;
    public static boolean isX_Chat_Exit = false;
    private List<Activity> activityList = new LinkedList<Activity>();
    private static ExitApplication instance;
    private ImMessage mlis;
    private IAddStaff mstaff;
    private Backlog mBacklog;
    public volatile static int UnReadNum = 0;
    private String temp;
    private int num;
    ArrayList<Long> mdata;
    ArrayList<String> mdata1;

    private ExitApplication() {
        mlis = new ImMessage() {

            @Override
            public String getMsg() {
                return temp;
            }

            @Override
            public void SetMsg(String s) {
                temp = s;
            }
        };

        mBacklog = new Backlog() {

            @Override
            public void setNum(int num1) {
                num = num1;
            }

            @Override
            public int getNum() {
                return num;
            }
        };

        mstaff = new IAddStaff() {

            @Override
            public ArrayList<ArrayList<?>> getData() {
                ArrayList<ArrayList<?>> list = new ArrayList<ArrayList<?>>();
                list.add(0, mdata);
                list.add(1, mdata1);
                return list;
            }

            @Override
            public void setData(ArrayList<Long> data, ArrayList<String> data1) {
                mdata = data;
                mdata1 = data1;
            }
        };
        //给当钱线程添加异常捕获。
//		Thread.currentThread().setUncaughtExceptionHandler(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //鍗曚緥妯″紡涓幏鍙栧敮涓?鐨凟xitApplication瀹炰緥
    public static ExitApplication getInstance() {
        if (null == instance) {
            instance = new ExitApplication();
        }
        return instance;

    }

    //娣诲姞Activity鍒板鍣ㄤ腑
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    //閬嶅巻鎵?鏈堿ctivity骞秄inish

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);

    }

    public ImMessage getmLisner() {
        return mlis;
    }

    public Backlog getmBacklog() {
        return mBacklog;
    }

    public IAddStaff getMStaff() {
        return mstaff;
    }


    @Override
    /**
     * 当发后，未捕获得异常时，由此处统一捕获
     * @param thread
     * @param ex
     */
    public void uncaughtException(Thread thread, Throwable error) {

        //运行此处时，该程序，必死无异，
        // 唯一能做的事就是，早死早超生。
        // 同时，可以留点遗言。
        LogUtil.writerLog(error.toString());
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
