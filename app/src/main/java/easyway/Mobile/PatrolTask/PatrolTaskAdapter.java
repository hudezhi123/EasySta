package easyway.Mobile.PatrolTask;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import easyway.Mobile.R;
import easyway.Mobile.util.DateUtil;


/**
 * Created by boy on 2017/12/12.
 */

public class PatrolTaskAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<PatrolTask> parentList;

    public PatrolTaskAdapter(Context context, List<PatrolTask> parentList) {
        mContext = context;
        this.parentList = parentList;
    }

    @Override
    public int getGroupCount() {
        return parentList == null ? 0 : parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (parentList != null && parentList.size() > 0) {
            PatrolTask task = parentList.get(groupPosition);
            if (task != null && task.getDetailMap() != null) {
                return task.getCount() + 1;
            }
            return task.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public PatrolTask getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public ChildTask getChild(int groupPosition, int childPosition) {
        PatrolTask task = parentList.get(groupPosition);
        if (task.getCount() == 0) {
            return null;
        } else {
            return task.getDetailMap().get(task.getID()).get(childPosition);
        }

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentHolder parentHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.undo_patrol_task_item, parent, false);
            parentHolder = new ParentHolder();
            parentHolder.textSpace = (TextView) convertView.findViewById(R.id.text_undo_task_name);
            parentHolder.textCount = (TextView) convertView.findViewById(R.id.text_undo_task_count);
            parentHolder.textStatus = (TextView) convertView.findViewById(R.id.text_undo_task_status);
            parentHolder.imgIcon = (ImageView) convertView.findViewById(R.id.img_count_gray);
            convertView.setTag(parentHolder);
        } else {
            parentHolder = (ParentHolder) convertView.getTag();
        }
        if (getChildrenCount(groupPosition) <= 0) {
            parentHolder.imgIcon.setVisibility(View.GONE);
        } else {
            parentHolder.imgIcon.setVisibility(View.VISIBLE);
        }
        if (isExpanded) {
            parentHolder.imgIcon.setRotation(90);
        } else {
            parentHolder.imgIcon.setRotation(0);
        }
        PatrolTask task = getGroup(groupPosition);
        parentHolder.textSpace.setText(task.getName());
        parentHolder.textStatus.setText(task.getStatus());
        parentHolder.textCount.setText(task.getCount() + "");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder child = null;
        if (convertView == null) {
            child = new ChildHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.undo_patrol_task_item_child, parent, false);
            child.textPerson = (TextView) convertView.findViewById(R.id.text_undo_task_child_person);
            child.textTime = (TextView) convertView.findViewById(R.id.text_undo_task_child_time);
            convertView.setTag(child);
        } else {
            child = (ChildHolder) convertView.getTag();
        }
        ChildTask task = getChild(groupPosition, childPosition);
        child.textPerson.setText(task.getUserName());
        if (childPosition == 0) {
            child.textTime.setText(task.getTime());
            child.textTime.setBackgroundColor(Color.argb(0xff, 0xaa, 0xaa, 0xcc));
            child.textPerson.setBackgroundColor(Color.argb(0xff, 0xaa, 0xaa, 0xcc));
        } else {
            child.textTime.setText(DateUtil.formatDate(task.getTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
            child.textTime.setBackgroundColor(Color.WHITE);
            child.textPerson.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class ChildHolder {
        TextView textPerson;
        TextView textTime;
    }

    private class ParentHolder {
        TextView textSpace;
        TextView textStatus;
        TextView textCount;
        ImageView imgIcon;
    }
}
