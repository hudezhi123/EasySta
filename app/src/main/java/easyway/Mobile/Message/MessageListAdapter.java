package easyway.Mobile.Message;

import java.util.ArrayList;

import easyway.Mobile.DevFault.DFList;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.StringUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 短信主界面Adapter
 */
public class MessageListAdapter extends BaseExpandableListAdapter {
    private ArrayList<MessageType> mTypeList = null;
    private ArrayList<ArrayList<ZWTMessage>> mMsgList = null;
    private Context context;
    private DBHelper dbHelper = null;
    private LayoutInflater mInflater;
    private IOnDataChange iOnDateChange;

    public MessageListAdapter(Context context, IOnDataChange iOnDateChange) {
        this.context = context;
        this.iOnDateChange = iOnDateChange;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<ArrayList<ZWTMessage>> msgList,
                        ArrayList<MessageType> typeList) {
        mTypeList = typeList;
        mMsgList = msgList;
    }

    public Object getChild(int groupPosition, int childPosition) {
        if (mMsgList == null)
            return null;

        if (mMsgList.size() <= groupPosition)
            return null;

        if (mMsgList.get(groupPosition).size() <= childPosition)
            return null;

        return mMsgList.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.msg_list_item, parent, false);
            holder = new ViewHolder();
            holder.txtContact = (TextView) convertView
                    .findViewById(R.id.txtContact);
            holder.txtCreateTime = (TextView) convertView
                    .findViewById(R.id.txtCreateTime);
            holder.txtContent = (TextView) convertView
                    .findViewById(R.id.txtContent);
            holder.imgAttach = (ImageView) convertView
                    .findViewById(R.id.imgAttach);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mMsgList == null)
            return null;
        final ZWTMessage message = mMsgList.get(groupPosition).get(
                childPosition);

        holder.txtContact.setText(message.contactName);
        holder.txtCreateTime.setText(DateUtil.formatDate(message.createTime, DateUtil.YYYY_MM_DD_HH_MM));
        holder.txtContent.setText(StringUtil.Encode(message.content, false));

        if (message.status == ZWTMessage.STATUS_UNREAD) {
            holder.txtContact.setTextColor(context.getResources().getColor(
                    R.color.black));
            holder.txtContent.setTextColor(context.getResources().getColor(
                    R.color.black));
            holder.txtCreateTime.setTextColor(context.getResources().getColor(
                    R.color.black));
        } else {
            holder.txtContact.setTextColor(context.getResources().getColor(
                    R.color.gray));
            TextPaint paint = holder.txtContact.getPaint();
            paint.setFakeBoldText(false);

            holder.txtContent.setTextColor(context.getResources().getColor(
                    R.color.gray));
            holder.txtCreateTime.setTextColor(context.getResources().getColor(
                    R.color.gray));
        }

        if (message.attach == null
                || message.attach.trim().length() == 0) {
            holder.txtContent.setVisibility(View.VISIBLE);
            holder.imgAttach.setVisibility(View.GONE);
        } else {
            holder.txtContent.setVisibility(View.GONE);
            holder.imgAttach.setVisibility(View.VISIBLE);
        }

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ZWTMessage.UpdateStatus(context, message.Id, 1);
                    }
                }).start();
                if (message.type == MessageType.TYPE_SEND
                        || message.type == MessageType.TYPE_NORMAL) {
                    Intent intent = new Intent(context, MessageChat.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(MessageChat.TAG_STAFF_ID, message.contactId);
                    bundle.putString(MessageChat.TAG_STAFF_NAME, message.contactName);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
                if (message.type == MessageType.TYPE_DF_NOTICE) {
                    Intent intent = new Intent(context, DFList.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, MessageView.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("MsgId", message.MsgId);
                    bundle.putLong("id", message.Id);
                    bundle.putString("contactName", message.contactName);
                    bundle.putString("createTime", message.createTime);
                    bundle.putString("content", message.content);
                    bundle.putString("attach", message.attach);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });

        convertView.setPadding(80, 0, 5, 0);
        convertView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                AlertDialog dlg = new AlertDialog.Builder(context)
                        .setTitle("")
                        .setItems(
                                context.getResources().getStringArray(
                                        R.array.MsgDelete),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int item) {
                                        if (item == 0) {
                                            String sql = "";
                                            if (message.type == MessageType.TYPE_SEND
                                                    || message.type == MessageType.TYPE_NORMAL) {
                                                sql = "delete from "
                                                        + DBHelper.MESSAGE_TABLE_NAME
                                                        + " where "
                                                        + DBHelper.MESSAGE_OWNERID
                                                        + " = '"
                                                        + message.ownerId
                                                        + "' and "
                                                        + DBHelper.MESSAGE_CONTACTID
                                                        + " = '"
                                                        + message.contactId
                                                        + "';";
                                            } else {
                                                sql = "delete from "
                                                        + DBHelper.MESSAGE_TABLE_NAME
                                                        + " where "
                                                        + DBHelper.MESSAGE_ID
                                                        + "= '"
                                                        + message.Id
                                                        + "';";
                                            }

                                            dbHelper = new DBHelper(context);
                                            dbHelper.execSQL(sql);
                                            dbHelper.close();

                                            if (iOnDateChange != null)
                                                iOnDateChange.onDataChange();
                                        }
                                    }
                                }).create();
                dlg.show();
                return false;
            }

        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtContact;
        TextView txtCreateTime;
        TextView txtContent;
        ImageView imgAttach;
    }

    static class ParentHolder {
        ImageView imgArrow;
        TextView textContent;
    }

    public int getChildrenCount(int groupPosition) {
        if (mMsgList == null)
            return 0;

        if (mMsgList.size() <= groupPosition)
            return 0;
        return mMsgList.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        if (mTypeList == null)
            return null;

        if (mTypeList.size() <= groupPosition)
            return 0;

        return mTypeList.get(groupPosition);
    }

    public int getGroupCount() {
        if (mTypeList == null)
            return 0;
        return mTypeList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        MessageType tb_Message_Type = mTypeList.get(groupPosition);
        ParentHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.station_msg_parent_tag, parent, false);
            holder = new ParentHolder();
            holder.imgArrow = (ImageView) convertView.findViewById(R.id.img_arrow_stationmsg);
            holder.textContent = (TextView) convertView.findViewById(R.id.text_content_station_msg);
            convertView.setTag(holder);
        } else {
            holder = (ParentHolder) convertView.getTag();
        }
        if (tb_Message_Type.id == 0) {
            holder.textContent.setText(tb_Message_Type.name + "       ("
                    + tb_Message_Type.totalNum + ")");
        } else {
            holder.textContent.setText(tb_Message_Type.name + "       ("
                    + tb_Message_Type.unreadNum + "/"
                    + tb_Message_Type.totalNum + ")");
        }
        if (isExpanded) {
            holder.imgArrow.setRotation(90);
        } else {
            holder.imgArrow.setRotation(0);
        }
        return convertView;
    }

    public TextView getMessageTypeView(MessageType messageType, TextView text) {
        // Set the text starting position
        if (messageType.id == 0) {
            text.setText(messageType.name + "       ("
                    + messageType.totalNum + ")");
        } else {
            text.setText(messageType.name + "       ("
                    + messageType.unreadNum + "/"
                    + messageType.totalNum + ")");
        }
        return text;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
