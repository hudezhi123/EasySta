package easyway.Mobile.Net;

public class Constant {
    public static final String MP_ISTATIONSERVICE = "IStationService.asmx";
    public static final String MP_WSINTERNAL = "WsInternal.asmx";//人工广播、保障附件
    public static final String MP_SMS = "WebService/SMS.asmx";//短信息
    public static final String MP_BROADCAST = "WebService/Broadcast.asmx";//播放
    public static final String MP_DEVFAULT = "WebService/DevFault.asmx";
    public static final String MP_REPAIR = "WebService/RepairDevice.asmx";
    public static final String MP_TASK = "WebService/Task.asmx";
    public static final String MP_PERMISSION = "WebService/Permission.asmx";
    public static final String MP_TASKMONITORING = "WebService/TaskMonitoring.asmx";
    public static final String MP_QUERYINFO = "WebService/QueryInfo.asmx";
    public static final String MP_SHIFT = "WebService/Shift.asmx";
    public static final String MP_SPARK = "WebService/spark.asmx";
    public static final String MP_TRAININFO = "WebService/TrainInfo.asmx";
    public static final String MP_ATTACHMENT = "WebService/Attachment.asmx";
    public static final String MP_PATROL = "WebService/Patrol.asmx";
    public static final String MP_REGULATIONS = "WebService/Regulations.asmx";//规章制度
    public static final String MP_ATTANDENCE = "WebService/Attandence.asmx";
    public static final String MP_SERVICE = "iLamp/Service.asmx";
    public static final String MP_Notify = "WebService/Notify.asmx";

    // MP_ISTATIONSERVICE
    public static final String MN_LOGIN = "Login";
    public static final String MN_ACTIVE_STATUS = "ActiveStatus";
    public static final String MN_CHECK_UPDATE = "CheckForUpdates";
    public static final String MN_CHECK_UPDATE2GQT = "CheckForUpdates2GQT";
    public static final String MN_PAY_BROADCAST = "PayBroadcast";
    public static final String MN_GET_BROADCAST_CONTENTBYID = "GetBroadcastContentByID";
    public static final String MN_GET_DATAVERSION = "GetDataVerion";
    public static final String MN_CHANGE_PSW = "ChangePassword";
    public static final String MN_GET_ONLINE_USER = "GetOnlineUsers";

    // MP_WSINTERNAL
    public static final String MN_SAVE_BROAD_MANUAL = "SaveBroadManual";
    public static final String MN_BROAD_POST_MANUAL = "Broad_Post_Manual";

    // MP_SMS
    public static final String MN_GET_UNREAD_MESSAGE = "GetUserUnReadMessage";
    public static final String MN_SEND_MESSAGE = "SendMessage";
    public static final String MN_GET_GROUP = "GetAllGroupByUser";
    public static final String MN_GET_SendReceipt = "SendReceipt";

    public static final String MN_GET_ALL = "GetAll";
    // MP_BROADCAST
    public static final String MN_GET_BROADCASTI_NFO = "GetBroadcastInfo";
    public static final String MN_GET_BROADCAST_AREA = "GetAllBroadcastArea";
    public static final String MN_GET_PLAY_RECORDS = "GetPlayRecords";

    // MP_DEVFAULT
    public static final String MN_GET_DEV_FAULT = "GetDevFault";
    public static final String MN_GET_GROUP_DEV_FAULT = "GetGroupDevices";
    public static final String MN_ADD_DEV_FAULT = "AddDevFault";
    public static final String MN_GET_DEVICE_INFO = "GetDeviceInfo";
    public static final String MN_GET_DEV_FAULT_IMAGE = "GetDevFaultImage";
    public static final String MN_SAVE_LIVECASE_REPORT = "SaveLiveCaseReport";
    public static final String MN_GET_LIVECASE_REPORT = "GetLiveCaseReport";
    public static final String MN_UPLOAD_LIVECASE_ATTACH = "UploadLiveCaseAttach";
    public static final String MN_GET_FAULT_TEMPLATE = "GetAllFaultTemplateByDevId";
    public static final String MN_GET_DEV_FAULT_CATE = "GetDevFaultCate";


    public static final String MN_GET_Dsp_FAULT = "GetUsingSpareParts"; //
    public static final String MN_GET_Repair_FAULT = "RepairDevFault";
    public static final String MN_GET_ok_FAULT = "ConfirmDevFault";// ConfirmDevFault
    public static final String MN_PARTOL_DEVICES = "PartolDevFault";
    public static final String MN_NotConfirmDevFault = "NotConfirmDevFault";
    public static final String MN_BEGINREPAIRDEVFAULT = "BeginRepairDevFault";
    public static final String MN_SAVEBROKENDEVSPAREPARTS = "SaveBrokenDevSpareParts";
    public static final String MN_SUSPENDREPAIRDEVFAULT = "SuspendRepairDevFault";//中止维修
    public static final String MN_RESUMEREPAIRDEVFAULT = "ResumeRepairDevFault";//继续维修
    public static final String MN_GET_DEVINGROUP = "GetDevInGroup";
    public static final String MN_GET_ALLDEVCATE = "GetAllDevCate";
    public static final String MN_GET_ALLDEVSUPPLIER = "GetAllDevSupplier";
    public static final String MN_SAVE_BROKENDEVSPAREPARTS = "SaveBrokenDevSpareParts";
    public static final String MN_SAVE_DEVREPAIRLOG = "SaveDevRepairLog";//保存维修日志
    public static final String MN_GET_DEVSPAREPARTS = "GetDevSpareParts";//获取单个备品备件详细信息
    public static final String MN_GET_DEVSPAREPARTSUSINGHISTORY = "GetDevSparePartsUsingHistory";//获取备品备件使用记录
    public static final String MN_BEGINREPAIRDEVSPAREPARTS = "BeginRepairDevSpareParts";// 开始维修备品备件
    public static final String MN_FIXDEVSPAREPARTS = "FixDevSpareParts";//维修完成备品备件
    public static final String MN_RETIRESPAREPARTS = "RetireSpareParts";//报废备品备件
    public static final String MN_SAVEDEVISSUEREPAIRIMAGE = "SaveDevIssueRepairImage";//维修处理上传图片
    // MP_TASK
    public static final String MN_ADD_TASK = "AddTask";
    public static final String MN_SAVE_TASKMAJOR = "SaveTaskMajor";
    public static final String MN_SAVE_TASKITEM = "SaveTaskItem";
    public static final String MN_PUBLISH_TASK = "PublicTask";
    public static final String MN_DELETE_TASK = "DeleteTaskMajor";
    public static final String MN_DELETE_TASKITEM = "DeleteTaskItem";
    public static final String MN_GET_TASK = "GetAllTask";
    public static final String MN_GET_ACTOR_SAVE_PROMPT_CARED = "GetTaskActorSafePromptCared";
    public static final String MN_SET_TASK_STARTING = "SetTaskStarting";
    public static final String MN_SET_TASK_COMPLETE = "SetTaskComplete";
    public static final String MN_SET_TASK_Garbage = "setActor2Grabage";
    public static final String MN_GET_EMPHASIS_TASK = "GetAllEmphasisTask";
    public static final String MN_GET_TASKBYWORKSPACE = "GetTask4WorkSpace";
    public static final String MN_GET_PUBLISHER_EMPHASIS_TASK = "GetPublisherEmphasisTask";
    public static final String MN_GET_TEAM_DAY = "GetAllTeamByDay";
    public static final String MN_GET_DUTYSTAFF_TEAM = "GetDutyStaffByTeam";
    public static final String MN_GET_TASKVIEW_SHIFTOUT = "GetAllTaskView4ShiftOut";
    public static final String MN_GET_TASKVIEW_SHIFTIN = "GetAllTask4ShiftIn";
    public static final String MN_UPDATE_IMPORTANT_TASK_STAFF = "UpdateImportantTaskWithStaff";
    public static final String MN_UPDATE_IMPORTANT_TASK = "UpdateImportantTask";
    public static final String MN_GET_RELATED_TASK = "GetAllRelatedTask";
    public static final String MN_GET_RUNNING_TASK = "GetRunningTask";
    public static final String MN_ACCEPT_MAJORTASK = "AcceptMajorTask";
    public static final String MN_GET_TASKMAIN = "GetAllTaskMain";
    public static final String MN_GET_TASKACTOR = "GetTaskActorByTaskId";
    public static final String MN_GET_PLATFORM_TASK = "GetPlatformTask";
    public static final String MN_GET_TICKETCHECK_TASK = "GetTicketCheckTask";
    public static final String MN_GET_POSITION = "GetAllPosition";
    public static final String MN_GET_TASKWORKSPACE = "GetAllTaskWorkspace";
    public static final String MN_GET_TASKPWRS = "GetAllPositionWorkspace";
    public static final String MN_GET_TASKWORKSPACEBYPID = "GetAllTaskWorkspaceByDutyPID";
    public static final String MN_GET_TASKEXCEPT = "GetTaskOnlineExcept";

    public static final String MN_GET_MONITOR = "GetMonitor";
    public static final String MN_GET_MONITORTASK = "GetMonitorTask";

    public static final String MN_GET_TASKACTORSIGNINFO = "GetTaskActorSignInfo";

    public static final String MN_GET_WATERTASK = "GetAllWaterTask";
    public static final String MN_APPLYWATER = "ApplyWater";

    public static final String MN_GET_LAMPAREA = "GetLampArea";
    public static final String MN_GET_LAMPSTATUS = "GetLampStatus";
    public static final String MN_SET_LAMPSTATUS = "SetLampStatus";
    public static final String MN_GET_LAMPPOINTS = "GetLampPoints";
    public static final String GetNotCompletedTaskNum = "GetNotCompletedTaskNum";
    public static final String MN_SET_LAMPPOINTS = "SetLampPoints";
    public static final String MN_GetTaskDevFault = "GetTaskDevFault";
    public static final String MN_FixTaskDevFault = "FixTaskDevFault";

    //shift_in
    public static final String MN_SHIFT2INGETALLSHIFTOUT = "Shift2_In_GetAllShiftOut";//未接班所有交班信息
    public static final String MN_SHIFT2INSAVE = "Shift2_In_Save";//接班
    public static final String MN_SHIFT2ALLDETAILS = "Shift2_AllDetails";//明细
    public static final String MN_SHIFT2INSETACTOR = "Shift2_In_SetActor";//分配
    public static final String MN_SHIFT2MYSHIFT = "Shift2_MyShift";//我的交接班
    // MP_PERMISSION
    public static final String MN_GET_PERMISSION = "GetPermission";

    // MP_TASKMONITORING
    public static final String MN_GET_TEAMSUMMARYINFO = "GetAllTeamSummaryInfo";
    public static final String MN_GET_EACH_TEAMSUMMARYINFO = "GetEachTeamSummaryInfo";

    public static final String MN_GET_TEAMHEAD_INFO = "GetTeamHeadmanInfo";
    public static final String MN_GET_TASK_SIGNININFO = "GetTaskSignInInfo";

    // MP_QUERYINFO
    public static final String MN_GET_PARA_CODE = "GetAllParameDetailsByCode";
    // MP_SHIFT
    public static final String MN_SAVE_SHIFTSTAFF = "SaveShiftStaff";
    public static final String MN_GET_STAFFS = "GetAllStaffs";
    public static final String MN_GET_SHIFTOUT = "GetShiftOut";
    public static final String MN_SAVE_SHIFTIN = "SaveShiftIn";
    public static final String MN_SAVE_SHIFTOUT = "SaveShiftOut";
    public static final String MN_CHECK_MODIFY = "CheckModify";
    public static final String MN_GET_ALLSHIFTOUT = "GetAllShiftOut";
    public static final String MN_SAVE_DETAIL = "SaveDetail";
    public static final String MN_GET_SHIFTDETAIL = "GetShiftDetail";
    public static final String MN_UPLOAD_SHIFTOUTVOICE = "UploadShiftOutVoice";
    public static final String MN_UPLOAD_SHIFTINVOICE = "UploadShiftInVoice";
    public static final String MN_GET_TRAINNO4SHIFTOUT = "GetAllTrainNo4ShiftOut";
    public static final String MN_GET_SHIFTOUTTRAINNUMS = "GetShiftOutTrainNums";
    public static final String MN_SAVE_SHIFT = "SaveShift";
    public static final String MN_GetShiftOutALLInfo = "GetShiftOutALLInfo";
    public static final String MN_GetShiftOutInfo = "GetShiftOutInfo";
    public static final String MN_GetStaffScheDetail = "GetStaffScheDetail";
    public static final String MN_SaveShiftInInfo = "SaveShiftInInfo";
    public static final String MN_SaveScheStaff = "SaveScheStaff";
    public static final String MN_GET_HANDOVER = "GetHandoverList";
    public static final String MN_SET_HANDOVER = "SetHandoverDone";
    public static final String MN_GET_ATTENDANCE = "GetHandoverAttendance";
    public static final String MN_Shift2_Out_Start = "Shift2_Out_Start";
    public static final String MN_Shift2_Out_GetOpeningTask = "Shift2_Out_GetOpeningTask";

    // MP_TRAININFO
    public static final String MN_GET_LANE = "GetAllLane";
    public static final String MN_GET_TRAINGO = "GetAllTrainGo";
    public static final String MN_GET_OCSRS = "GetOCSRS";
    public static final String MN_GET_OCSMTMINFO = "GetOCSMTMInfo";
    public static final String MN_GET_LANEOCCUPANCY = "GetAllLaneOccupancy";
    public static final String MN_GET_LANEALLMPS = "GetLaneAllMPS";//获取指定股道所有列车到发信息
    public static final String MN_GET_SCHEDULE = "GetTrainSchedule";
    public static final String MN_GET_STATION = "GetTrainStation";
    public static final String MN_GET_TRAINTYPE = "GetTrainTypeInfo";
    public static final String MN_GET_TRAINGOARR = "GetAllTrainGoARR";
    public static final String MN_GET_TRAINGODEP = "GetAllTrainGoDEP";
    public static final String MN_GET_PLATFORM = "GetAllPlatForm";
    public static final String MN_GET_VIASTATION = "GetAllViaStation";
    public static final String MN_GET_LINKSTATION = "GetLinkStation";
    public static final String MN_GET_TRAINGOAPP = "GetAllTrainGoApproach";
    public static final String MN_GET_DUTYWORKSPACE = "GetDutyWorkspace";
    public static final String MN_GET_MPSLog = "GetMPSLog";  //根据车次获取今日到发变更
    public static final String MN_GET_TRAINGOLATE = "GetTrainGoLater";

    // MP_SPARK
    public static final String MN_GET_TEAMID = "GetTeamId";
    public static final String MN_GET_STAFF = "GetAllStaff";
    public static final String MN_GET_DEPT = "GetAllDept ";
    public static final String MN_GET_STAFFDEPTRELATION = "GetStaffDeptRelationShip";
    public static final String MN_GET_STATIONCODE = "GetAllStationCode";
    public static final String MN_GET_TASKWORKSPACECATE = "GetTaskWorkspaceCate";
    public static final String MN_GET_CMKEYVALUEXML = "GetCMKeyValueXML";
    public static final String MN_GET_ACTIVITYSTATUS = "ActiveStatus";//心跳检测

    // MP_REGULATIONS
    public static final String MN_GET_REGULATIONSALL = "GetAll";

    // MP_ATTACHMENT
    public static final String MN_POST_ATTACH = "PostAttachment";
    public static final String MN_DELETE_ATTACH = "Del4Attach";

    public static final String MN_UPLOAD_FILE = "UploadFile";
    public static final String MN_UPLOAD_FILE1 = "SaveDevIssueRepairImage";

    // MP_PATROL
    public static final String MN_CHECK_PATROL = "CheckPatrol";
    public static final String MN_GET_PATROL = "GetAllPatrol";

    // MP_ATTANDENCE
    public static final String MN_APPLY_VACATION = "ApplyVacation";
    public static final String MN_AUDIT_VACATION = "AuditVacation";
    public static final String MN_CANCEL_VACATION = "CancelVacation";
    public static final String MN_GET_VACATION = "GetVacation";
    public static final String MN_GET_VACATION_TYPE = "GetVacationType";
    //MP_NOTIFY
    public static final String MN_SETISRECEIPT = "SetIsReceipt";

    public static final int WT_SHANGSHUI = 0;
    public static final int WT_STATION = 1;
    public static final int WT_WAITINGHALL = 2;
    public static final int WT_CHECKIN = 3;
    public static final int WT_EXIT = 4;

    public static final int NORMAL = 1000;
    public static final int EXCEPTION = 911;
    public static final int EXP_NET_NO_CONNECT = 1001;
    public static final int EXP_NET_CONNECT_TIMEOUT = 1002;
    public static final int EXP_NET_READ_TIME_OUT = 1003;
    public static final int EXP_NET_SERVICE_ER = 1004;
    public static final int EXP_NET_CONNECT_ERROR = 1007;
    public static final int EXP_SERVICE_INTERNAL_ER = 1005;
    public static final int EXP_SERVICE_PARA_ER = 1006;
    public static final int NORMAL_ZERO = 0;
    public static final int TEST_CODE = 3838;


    public static final int EXP_LOGIN_USERNAME_ER = 2001;
    public static final int EXP_LOGIN_PASSWORD_ER = 2002;
    public static final int EXP_SESSION_INVALID = 2003;
    public static boolean isPartolDevices = false;

    public static final String SEWAGEWATER_METHOD = "GetTask4Gabage";
    public static final String SEND_CALLBACK_SEWAGEWATER = "set2Grabage";
    //MAC地址保存路径
    public static String MAC_ADDR = "MacAddress";

    public static final String DANGEROUS_SAVE = "Dangerous_Save";
    public static final String DANGEROUS_FIND = "Dangerous_Find";

    //售票工作日志
    public static final String DELETE_TICKET_LOG = "DeleteTicketWorkLog";
    public static final String UPDATE_TICKET_LOG = "UpdateTicketWorkLog";
    public static final String INSERT_TICKET_LOG = "InsertTicketWorkLog";
    public static final String SEARCH_TICKET_LOG = "SearchTicketWorkLog";
    //客运工作日志
    public static final String SEARCHPTT_WORKLOG = "SearchPttWorkLog";
    public static final String UPDATEPTT_WORKLOG = "UpdatePttWorkLog";
    public static final String INSERTPTT_WORKLOG = "InsertPttWorkLog";
    public static final String DELETEPTT_WORKLOG = "DeletePttWorkLog";
    //故障报修
    public static final String ADD_REPAIR_INFO = "AddRepairInfo";
    public static final String GET_REPAIR_TYPES = "GetAllRepairTypes";
    public static final String UPLOAD_ATTACH = "SaveRepairImage";
    public static final String QUERY_REPAIR_LIST = "QueryRepairList";
    public static final String QUERY_REPAIR_PIC_BY_REPAIRID = "QueryRepairPicByRepairId";
    //巡检任务
    public static final String PATROL_VALID_STAFF="CheckIsValidInspector";
    public static final String PATROL_WAIT_TO_TASK="GetAllWaitInspectObject";
    public static final String PATROL_ALREADY_TASK="GetAllCompleteInspectObject";
    public static final String PATROL_ALL_TASK="GetAllInspectObject";
    public static final String PATROL_IS_TASK="CheckIsValidInspectObject";
    public static final String PATROL_SUBMIT_TASK="SaveInspectResult";
}
