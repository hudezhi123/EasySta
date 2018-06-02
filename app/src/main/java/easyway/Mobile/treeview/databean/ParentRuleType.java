package easyway.Mobile.treeview.databean;

import java.io.Serializable;

/**
 * Created by boy on 2017/5/8.
 */

public class ParentRuleType implements Serializable {
    public String id;
    public String text;

    public ParentRuleType() {
    }

    public ParentRuleType(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ParentRuleType{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
