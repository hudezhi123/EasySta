package easyway.Mobile.PatrolTask;

/**
 * Created by boy on 2018/1/4.
 */

public class ChildTask {
    private String userName;
    private String time;
    private String objectID;
    private String objectName;

    public ChildTask(){}

    public ChildTask(String userName, String time) {
        this.userName = userName;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
