package easyway.Mobile.treeview;


import easyway.Mobile.R;
import easyway.Mobile.treeview.databean.ParentRuleType;
import easyway.Mobile.treeview.recyclertreeview_lib.LayoutItemType;

/**
 * Created by boy on 2017/5/8.
 */

public class ParentTab implements LayoutItemType {
    public String typeName;
    public ParentRuleType parentRuleType;

    public ParentTab(String typeName) {
        this.typeName = typeName;
    }

    public ParentTab(ParentRuleType parentRuleType) {
        this.parentRuleType = parentRuleType;
        this.typeName = parentRuleType.text;
    }

    @Override
    public int getLayoutId() {
        return R.layout.parenttab_item;
    }

    @Override
    public int getType() {
        return LayoutItemType.TYPE_PARENT_TYPE;
    }
}
