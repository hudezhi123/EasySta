package easyway.Mobile.Data;

import java.util.List;

/**
 * Created by boy on 2017/5/21.
 */

public class SewageWaterResult {

    /**
     * MsgType : true
     * Msg :
     * Data : [{"TaskId":342163,"BeginWorkTime":"2017-02-03 08:00:00","EndWorkTime":"2017-02-03 19:05:00","TaskName":"2.3到达西闸机"},{"TaskId":342164,"BeginWorkTime":"2017-02-03 08:00:00","EndWorkTime":"2017-02-03 20:30:00","TaskName":"2.3到达东闸机"},{"TaskId":342188,"BeginWorkTime":"2017-02-03 08:37:00","EndWorkTime":"2017-02-03 08:57:00","TRNO_PRO":"G4020","TaskName":"G4020次作业任务"},{"TaskId":342189,"BeginWorkTime":"2017-02-03 08:40:00","EndWorkTime":"2017-02-03 09:00:00","TRNO_PRO":"D6807","TaskName":"D6807次作业任务"},{"TaskId":342192,"BeginWorkTime":"2017-02-03 08:47:00","EndWorkTime":"2017-02-03 09:07:00","TRNO_PRO":"D2506","TaskName":"D2506次作业任务"}]
     * SessionId :
     * Total : 5
     * Code : 1000
     * Version :
     */

    private boolean MsgType;
    private String Msg;
    private String SessionId;
    private int Total;
    private int Code;
    private String Version;
    private List<DataBean> Data;

    public boolean isMsgType() {
        return MsgType;
    }

    public void setMsgType(boolean MsgType) {
        this.MsgType = MsgType;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String SessionId) {
        this.SessionId = SessionId;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * TaskId : 342163
         * BeginWorkTime : 2017-02-03 08:00:00
         * EndWorkTime : 2017-02-03 19:05:00
         * TaskName : 2.3到达西闸机
         * TRNO_PRO : G4020
         */

        private int TaskId;
        private String BeginWorkTime;
        private String EndWorkTime;
        private String TaskName;
        private String TRNO_PRO;
        private boolean isAllowed;
        private boolean isSubmit;

        public boolean isSubmit() {
            return isSubmit;
        }

        public void setSubmit(boolean submit) {
            isSubmit = submit;
        }

        public boolean isAllowed() {
            return isAllowed;
        }

        public void setAllowed(boolean allowed) {
            isAllowed = allowed;
        }

        public int getTaskId() {
            return TaskId;
        }

        public void setTaskId(int TaskId) {
            this.TaskId = TaskId;
        }

        public String getBeginWorkTime() {
            return BeginWorkTime;
        }

        public void setBeginWorkTime(String BeginWorkTime) {
            this.BeginWorkTime = BeginWorkTime;
        }

        public String getEndWorkTime() {
            return EndWorkTime;
        }

        public void setEndWorkTime(String EndWorkTime) {
            this.EndWorkTime = EndWorkTime;
        }

        public String getTaskName() {
            return TaskName;
        }

        public void setTaskName(String TaskName) {
            this.TaskName = TaskName;
        }

        public String getTRNO_PRO() {
            return TRNO_PRO;
        }

        public void setTRNO_PRO(String TRNO_PRO) {
            this.TRNO_PRO = TRNO_PRO;
        }
    }

}
