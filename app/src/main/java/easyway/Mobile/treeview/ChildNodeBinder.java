package easyway.Mobile.treeview;

import android.view.View;
import android.widget.TextView;

import easyway.Mobile.R;
import easyway.Mobile.treeview.recyclertreeview_lib.LayoutItemType;
import easyway.Mobile.treeview.recyclertreeview_lib.TreeNode;
import easyway.Mobile.treeview.recyclertreeview_lib.TreeViewBinder;

/**
 * Created by boy on 2017/5/8.
 */

public class ChildNodeBinder extends TreeViewBinder<ChildNodeBinder.ViewHolder> {

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        ChildTab child = (ChildTab) node.getContent();
        holder.tvName.setText(child.fileName);
    }

    @Override
    public int getLayoutId() {
        return R.layout.childtab_item;
    }

    @Override
    public int getType() {
        return LayoutItemType.TYPE_CHILD_FILE;
    }

    public class ViewHolder extends TreeViewBinder.ViewHolder {
        public TextView tvName;

        public ViewHolder(View rootView) {
            super(rootView);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_childtab_name);
        }

    }
}
