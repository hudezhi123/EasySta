package easyway.Mobile;

import java.util.ArrayList;

import android.content.Context;

import easyway.Mobile.Data.Station;
import easyway.Mobile.Login.BasicDataVersion;
import easyway.Mobile.util.PTTUtil;

/*
 *	信息管理 
 */
public class Property {
    // public static String PACKAGE_NAME_INTERNALSPEAK = "com.android.intercom";
    // // 对讲机包名
    public static String PACKAGE_NAME_INTERNALSPEAK = "com.dfl.ptt"; // 对讲机包名

    public static String SessionId;

    public static String StationCode;

    // 手机屏幕大小
    public static int screenwidth = 0;
    public static int screenheight = 0;

    // 用户信息
    public static String UserName;
    public static long UserId;
    public static long StaffId;
    public static String StaffName;

    // 所在部门信息
    public static String DeptId;
    public static String DepName;

    // VOIP信息
    public static String VOIPId;
    public static String VOIPPwd;

    public static String IsTeamLeader;
    public static String VOIPServiceAddress;
    public static String VOIPServicePort;
    public static boolean IsSIPON = false;

    public static Station OwnStation; // 当前归属站
    public static ArrayList<Station> ChargeStation; // 有权限站列表

    public static void Reset(Context ctx) {
        SessionId = "";
        UserName = "";
        UserId = 0;
        StaffId = 0;
        StaffName = "";
        DeptId = "";
        DepName = "";
        VOIPServiceAddress = "";
        VOIPServicePort = "";
        VOIPId = "";
        IsTeamLeader = "";
        VOIPPwd = "";
        BasicDataVersion.ResetVersions();
        if (IsSIPON) {
            PTTUtil.Logout(ctx);
        }
        IsSIPON = false;
        OwnStation = null;
        ChargeStation = null;
    }

    public static ArrayList<Station> AllStation;
}
