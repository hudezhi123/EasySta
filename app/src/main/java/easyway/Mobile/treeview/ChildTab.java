package easyway.Mobile.treeview;


import easyway.Mobile.R;
import easyway.Mobile.treeview.databean.ChildRuleFile;
import easyway.Mobile.treeview.recyclertreeview_lib.LayoutItemType;

/**
 * Created by boy on 2017/5/8.
 */

public class ChildTab implements LayoutItemType {
    public String fileName;
    public ChildRuleFile childRuleFile;

    public ChildTab(String fileName) {
        this.fileName = fileName;
    }

    public ChildTab(ChildRuleFile childRuleFile) {
        this.childRuleFile = childRuleFile;
        this.fileName = childRuleFile.RTitle;
    }

    @Override
    public int getLayoutId() {
        return R.layout.childtab_item;
    }

    @Override
    public int getType() {
        return LayoutItemType.TYPE_CHILD_FILE;
    }
}
