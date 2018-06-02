package easyway.Mobile.treeview.recyclertreeview_lib;

/**
 * Created by tlh on 2016/10/1 :)
 */

public interface LayoutItemType {
    public static final int TYPE_PARENT_TYPE = 1;
    public static final int TYPE_CHILD_FILE = 2;

    int getLayoutId();

    int getType();
}
