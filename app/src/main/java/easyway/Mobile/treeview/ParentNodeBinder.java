package easyway.Mobile.treeview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import easyway.Mobile.R;
import easyway.Mobile.treeview.recyclertreeview_lib.LayoutItemType;
import easyway.Mobile.treeview.recyclertreeview_lib.TreeNode;
import easyway.Mobile.treeview.recyclertreeview_lib.TreeViewBinder;


/**
 * Created by boy on 2017/5/8.
 */

public class ParentNodeBinder extends TreeViewBinder<ParentNodeBinder.ViewHolder> {

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        holder.ivArrow.setRotation(0);
        holder.ivArrow.setImageResource(R.drawable.contact_type_dep);
        int rotateDegree = node.isExpand() ? 90 : 0;
        holder.ivArrow.setRotation(rotateDegree);
        ParentTab parentTab = (ParentTab) node.getContent();
        holder.tvName.setText(parentTab.typeName);
//        if (node.isLeaf())
//            holder.ivArrow.setVisibility(View.INVISIBLE);
//        else holder.ivArrow.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.parenttab_item;
    }

    @Override
    public int getType() {
        return LayoutItemType.TYPE_PARENT_TYPE;
    }

    public class ViewHolder extends TreeViewBinder.ViewHolder {
        private ImageView ivArrow;
        private TextView tvName;

        public ViewHolder(View rootView) {
            super(rootView);
            this.ivArrow = (ImageView) rootView.findViewById(R.id.img_parenttab_type_icon);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_parenttab_name);
        }

        public ImageView getIvArrow() {
            return ivArrow;
        }

        public TextView getTvName() {
            return tvName;
        }
    }
}
