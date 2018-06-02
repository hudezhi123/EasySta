package easyway.Mobile.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

import easyway.Mobile.Broadcast.BroadcastTabActivity;
import easyway.Mobile.Caution.CautionListActivity;
import easyway.Mobile.Contacts.Contacts;
import easyway.Mobile.DangerousGoods.DangerousFormActivity;
import easyway.Mobile.DangerousGoods.Flags;
import easyway.Mobile.DefaultReportToRepair.DevReportToRepairActivity;
import easyway.Mobile.DevFault.DeviceFaultActivity;
import easyway.Mobile.DevFault.TaskDevUnusual;
import easyway.Mobile.DevFault.TaskExtractSwageActivity;
import easyway.Mobile.LightingControl.LightingControlActivity;
import easyway.Mobile.LiveCase.LiveCase;
import easyway.Mobile.MainFramework;
import easyway.Mobile.Message.MessageList;
import easyway.Mobile.PassengerTrafficLog.LogListActivity;
import easyway.Mobile.Patrol.PatrolBrowseActivity;
import easyway.Mobile.PatrolTask.PatrolTaskActivity2;
import easyway.Mobile.PointTask.PTListActivity;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.ReportChart.ReportsActivity;
import easyway.Mobile.SellTicktLog.SellLogListActivity;
import easyway.Mobile.Shift.ShiftInHomeActivity;
import easyway.Mobile.Shift.Shift_Out;
import easyway.Mobile.ShiftNew.ShiftTabActivity;
import easyway.Mobile.SiteRules.SRTabActivity;
import easyway.Mobile.StationSchedule.StationSchedule;
import easyway.Mobile.Task.TaskTabActivity;
import easyway.Mobile.TrainAD.TrainADTabActivity;
import easyway.Mobile.TrainSearch.TSTabActivity;
import easyway.Mobile.TrainTrack.UnitRoadEncroachment;
import easyway.Mobile.VacationApply.VacationApplyActivity;
import easyway.Mobile.VacationAudit.VacationAuditListActivity;
import easyway.Mobile.Watering.WateringTabActivity;
import easyway.Mobile.site_monitoring.SMTabActivity;
import easyway.Mobile.util.PTTUtil;


/**
 * Created by boy on 2018/2/5.
 */

public class StartIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            } else {
                if (ReceiverConstant.ACTION_START_PERMISSION.equals(action)) {
                    String Permission = intent.getStringExtra("Permission");
                    OpenActivity(context, Permission);
                }
            }
        }
    }

    public void OpenActivity(Context context, String intentName) {

        // 巡检任务
        if (intentName.equals("PatrolTask")) {
            Intent intent = new Intent(context, PatrolTaskActivity2.class);
            context.startActivity(intent);
        }

        //客运日志
        if (intentName.equals("IStationWorkLog")) {
            Intent intent = new Intent(context, LogListActivity.class);
            context.startActivity(intent);
            return;
        }

        // 售票日志
        if (intentName.equals("TicketSellWorkLog")) {
            Intent intent = new Intent(context, SellLogListActivity.class);
            context.startActivity(intent);
            return;
        }

        //故障报修
        if (intentName.equals("FaultReportToRepair")) {
            Intent intent = new Intent(context, DevReportToRepairActivity.class);
            context.startActivity(intent);
        }

        // 代办工作
        if (intentName.equals("MyTaskList")) {
            Intent intent = new Intent(context,
                    TaskTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 到发通告
        if (intentName.equals("QueryTrainArrLeave")) {
            Intent intent = new Intent(context,
                    TrainADTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 股道占用
        if (intentName.equals("QueryUnitRoadStatus")) {
            Intent intent = new Intent(context, UnitRoadEncroachment.class);
            context.startActivity(intent);
            return;
        }

        // 站内短信
        if (intentName.equals("MessageList")) {
            Intent intent = new Intent(context, MessageList.class);
            context.startActivity(intent);
            return;
        }

        // 联系人
        if (intentName.equals("Contact")) {
            Intent intent = new Intent(context, Contacts.class);
            context.startActivity(intent);
            return;
        }

        // 设备报障
        if (intentName.equals("CaptureActivity")) {
            Intent intent = new Intent(context, DeviceFaultActivity.class);
            context.startActivity(intent);
            return;
        }

        // 交班
        if (intentName.equals("Shift_Out")) {
            Intent intent = new Intent(context, Shift_Out.class);
            context.startActivity(intent);
            return;
        }

        //交接班
        if (intentName.equals("Shift_Out_In")) {
            Intent intent = new Intent(context,
                    ShiftTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 接班
        if (intentName.equals("Shift_In")) {
            Intent intent = new Intent(context, ShiftInHomeActivity.class);
            context.startActivity(intent);
            return;
        }

        // 移动广播
        if (intentName.equals("MobileRadio")) {
            Intent intent = new Intent(context, BroadcastTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 情况上报
        if (intentName.equals("LiveCaseReport")) {
            Intent intent = new Intent(context, LiveCase.class);
            context.startActivity(intent);
            return;
        }

        // 现场监控
        if (intentName.equals("siteMonitor")) {
            Intent intent = new Intent(context, SMTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 小工具
        if (intentName.equals("Tools")) {
            try {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                        "com.android.flashlight");
                context.startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(context, R.string.noToolClient,
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
            return;
        }

        // 站内集群通
        if (intentName.equals("InternalSpeak")) {
            try {
                //去打开集群通应用。
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                        PTTUtil.SIPUA_PACKAGE_NAME);
                context.startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(context, R.string.noPTTClient,
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
            return;
        }

        // 站内对讲
        if (intentName.equals("zwt_Permission_xChat")) {  //zwt_Permission_xChat
            try {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : list) {
                    if (info.processName.equals("com.easyway.interphone")) {

                    }
                }
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                        PTTUtil.XCHAT_PACKAGE_NAME);
                Bundle bundle = new Bundle();
                bundle.putString("sessionID", Property.SessionId);
                bundle.putString("ServerSocket", Property.VOIPServiceAddress);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(bundle);
                context.startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(context, R.string.noPTTClient,
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
            return;
        }

        // 站内通话
        if (intentName.equals("Internal_talk")) {   //Internal_talk
            try {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                        PTTUtil.SIPUA_PACKAGE_NAME);
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(context, context.getString(R.string.noSIPClient), Toast.LENGTH_LONG)
                        .show();
                return;
            }
            return;
        }

        // 现场指挥
        if (intentName.equals("LiveControl")) {
            Intent intent = new Intent(context, Contacts.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Contacts.KEY_FLAG, Contacts.FLAG_LIVECONTROL);
            intent.putExtras(bundle);
            context.startActivity(intent);
            return;
        }

        // 列车时刻表
        if (intentName.equals("StationSchedule")) {   // StationSchedule
            Intent intent = new Intent(context, StationSchedule.class);
            context.startActivity(intent);
            return;
        }

        // 时刻查询
        if (intentName.equals("TimerSearch")) {  //TimerSearch
            Intent intent = new Intent(context, TSTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 重点任务
        if (intentName.equals("PointTask")) {
            Intent intent = new Intent(context, PTListActivity.class);
            context.startActivity(intent);
            return;
        }

        // 图形报表
        if (intentName.equals("ReportChart")) {
            Intent intent = new Intent(context,
                    ReportsActivity.class);
            context.startActivity(intent);
            return;
        }

        // 站内规章
        if (intentName.equals("SiteRules")) {   //SiteRules
            Intent intent = new Intent(context, SRTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 客运巡检
        if (intentName.equals("PassengerPatrol")) {
            Intent intent = new Intent(context,
                    PatrolBrowseActivity.class);
            context.startActivity(intent);
            return;
        }

        // 记事本
        if (intentName.equals("Notebook")) {
            Intent intent = new Intent(context,
                    CautionListActivity.class);
            context.startActivity(intent);
            return;
        }

        //危险品
        if (intentName.equals("ZWT_Dangerous")) {
            Intent intent = new Intent(context,
                    DangerousFormActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Flags.TO_DANGER_FLAG, Flags.FLAG_FROM_MAIN_TO_DANGER);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }

        // 上水管理
        if (intentName.equals("Watering")) {
            Intent intent = new Intent(context,
                    WateringTabActivity.class);
            context.startActivity(intent);
            return;
        }

        // 请假申请
        if (intentName.equals("VacationApply")) {
            Intent intent = new Intent(context,
                    VacationApplyActivity.class);
            context.startActivity(intent);
            return;
        }

        // 请假审核
        if (intentName.equals("VacationAudit")) {
            Intent intent = new Intent(context,
                    VacationAuditListActivity.class);
            context.startActivity(intent);
            return;
        }

        // 照明控制
        if (intentName.equals("LinghtControl")) {
            Intent intent = new Intent(context,
                    LightingControlActivity.class);
            context.startActivity(intent);
            return;
        }

        //设备异常
        if (intentName.equals("TaskDevFault")) {
            Intent intent = new Intent(context, TaskDevUnusual.class);
            context.startActivity(intent);
            return;
        }

        //吸污功能
        if (intentName.equals("Grabage")) {
            Intent intent = new Intent(context, TaskExtractSwageActivity.class);
            context.startActivity(intent);
            return;
        }
    }
}
