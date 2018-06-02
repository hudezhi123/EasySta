package easyway.Mobile.PatrolTask;

import java.util.List;
import java.util.Map;

import easyway.Mobile.PatrolTask.ChildTask;

/**
 * Created by boy on 2017/12/13.
 */

public class PatrolTask {

    private String ID;
    private String name;
    private String status;
    private int count;

    private Map<String,List<ChildTask>> detailMap;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<String, List<ChildTask>> getDetailMap() {
        return detailMap;
    }

    public void setDetailMap(Map<String, List<ChildTask>> detailMap) {
        this.detailMap = detailMap;
    }

}