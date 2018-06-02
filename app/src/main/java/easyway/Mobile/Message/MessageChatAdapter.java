package easyway.Mobile.Message;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 对话框Adapter
 */
class MessageChatAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private List<ZWTMessage> mlist;
	private IOnDataChange iOnDataChange;
	public final static int MSG_recripe_ok = 13;
	public final static int MSG_recripe_no = 14;
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_recripe_ok:
				notifyDataSetChanged();
				Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_LONG)
						.show();
				break;
			case MSG_recripe_no:
				if ((String) msg.obj == null) {
					Toast.makeText(mContext, "回执失败", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mContext, (String) msg.obj,
							Toast.LENGTH_LONG).show();
				}

				break;

			default:
				break;
			}
		}
	};

	public MessageChatAdapter(Context context, List<ZWTMessage> list,
			IOnDataChange iOnDataChange) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mlist = list;
		this.iOnDataChange = iOnDataChange;
	}

	@Override
	public int getCount() {
		if (mlist == null) {
			return 0;
		} else {
			return mlist.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		if (mlist == null)
			return null;

		if (arg0 < mlist.size()) {
			return mlist.get(arg0);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setData(List<ZWTMessage> list) {
		mlist = list;
	}

	private void getSendReceipt(ZWTMessage Msg) {

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);

		parmValues.put("id", Long.toString(Msg.MsgId));

		String methodPath = Constant.MP_SMS;
		String methodName = Constant.MN_GET_SendReceipt;
		WebServiceManager webServiceManager = new WebServiceManager(mContext,
				methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {

			myHandler.sendEmptyMessage(MessageChat.MSG_recripe_no);
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:

			Msg.receipt = "false";

			DBHelper dbHelper = new DBHelper(mContext);
			try {
				String sql = "update " + DBHelper.MESSAGE_TABLE_NAME + " set "
						+ DBHelper.MESSAGE_receipt + " = '" + Msg.receipt
						+ "' where " + DBHelper.MESSAGE_ID + " = '" + Msg.Id
						+ "';";

				dbHelper.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbHelper.close();
			}

			String msgs = JsonUtil.GetJsonString(result, "Msg");

			Message msg = new Message();
			msg.what = MessageChat.MSG_recripe_ok;
			msg.obj = msgs;
			myHandler.sendMessage(msg);
			break;
		case Constant.EXCEPTION:
		default:
			String errMsg = JsonUtil.GetJsonString(result, "Msg");

			Message msg1 = new Message();
			msg1.what = MessageChat.MSG_recripe_no;
			msg1.obj = errMsg;
			myHandler.sendMessage(msg1);

			break;
		}

	}

	@Override
	public View getView(int arg0, View contentView, ViewGroup arg2) {
		final ZWTMessage message = (ZWTMessage) getItem(arg0);
		if (null != message) {
			ViewHolder holder;
			if (null == contentView) {
				contentView = mInflater.inflate(R.layout.msg_chat_item, null);
				holder = new ViewHolder();

				// 发送的消息
				holder.LayoutSend = (LinearLayout) contentView
						.findViewById(R.id.LayoutSend);
				holder.txtSendCreateTime = (TextView) contentView
						.findViewById(R.id.txtSendCreateTime);
				holder.txtSendContent = (TextView) contentView
						.findViewById(R.id.txtSendContent);
				holder.imgSendContent = (ImageView) contentView
						.findViewById(R.id.imgSendContent);
				holder.btnSendStatus = (Button) contentView
						.findViewById(R.id.btnSendStatus);

				// 接收到的消息
				holder.LayoutReceive = (LinearLayout) contentView
						.findViewById(R.id.LayoutReceive);
				holder.txtReceiveCreateTime = (TextView) contentView
						.findViewById(R.id.txtReceiveCreateTime);
				holder.txtReceiveContent = (TextView) contentView
						.findViewById(R.id.txtReceiveContent);
				holder.imgReceiveContent = (ImageView) contentView
						.findViewById(R.id.imgReceiveContent);
				holder.btnReceiveStatus = (Button) contentView
						.findViewById(R.id.btnReceiveStatus);
				holder.btn_return = (Button) contentView
						.findViewById(R.id.btn_return);
				contentView.setTag(holder);
			} else {
				holder = (ViewHolder) contentView.getTag();
			}

			if (message.type == MessageType.TYPE_SEND) { // 消息类型为发送
				holder.LayoutSend.setVisibility(View.VISIBLE);
				holder.LayoutReceive.setVisibility(View.GONE);

				holder.txtSendCreateTime.setText(message.createTime);
			} else { // 消息类型为接收
				holder.LayoutSend.setVisibility(View.GONE);
				holder.LayoutReceive.setVisibility(View.VISIBLE);

				holder.txtReceiveCreateTime.setText(message.createTime);
				holder.btnReceiveStatus.setVisibility(View.GONE);

				if (message.receipt.equals("true")) {
					holder.btn_return.setVisibility(View.VISIBLE);

				} else {
					holder.btn_return.setVisibility(View.GONE);
				}

			}

			holder.btn_return.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new Thread() {
						public void run() {
							getSendReceipt(message);
						}

					}.start();

				}
			});

			holder.btnSendStatus.setVisibility(View.GONE);

			switch (message.status) {
			case ZWTMessage.STATUS_SENDFAIL:
				holder.btnSendStatus.setVisibility(View.VISIBLE);
				break;
			case ZWTMessage.STATUS_UNSEND:
				break;
			case ZWTMessage.STATUS_UNREAD: // 未读消息状态更新为已读
				message.status = ZWTMessage.STATUS_READED;
				ZWTMessage.UpdateStatus(mContext, message);
				break;
			default:
				break;
			}

			holder.btnSendStatus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog dlg = new AlertDialog.Builder(mContext)
							.setTitle("")
							.setItems(
									mContext.getResources().getStringArray(
											R.array.MsgResend),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int item) {
											if (item == 0) { // 重发消息
												iOnDataChange
														.onSendMessage(message);
											}
										}
									}).create();
					dlg.show();
				}
			});

			if (message.content.equals("") || message.content.length() == 0) { // 消息内容为文本
				if (message.type == MessageType.TYPE_SEND) {
					holder.txtSendContent.setVisibility(View.GONE);
				} else {
					holder.txtReceiveContent.setVisibility(View.GONE);
				}
			} else { // 消息内容为文本
				if (message.type == MessageType.TYPE_SEND) {
					holder.txtSendContent.setText(StringUtil.Encode(
							message.content, false));
					holder.txtSendContent.setVisibility(View.VISIBLE);
				} else {
					holder.txtReceiveContent.setText(StringUtil.Encode(
							message.content, false));
					holder.txtReceiveContent.setVisibility(View.VISIBLE);
				}
			}

			if (message.attach.equals("")) {
				// do nothing
				if (message.type == MessageType.TYPE_SEND) {
					holder.imgSendContent.setVisibility(View.GONE);
				} else {
					holder.imgReceiveContent.setVisibility(View.GONE);
				}
			} else {
				String filepath = ""; // 文件地址
				if (message.type == MessageType.TYPE_SEND) {
					filepath = message.attach;
				} else {
					filepath = CommonUtils.getFilePath(mContext)
							+ message.attach;
				}

				if (message.type == MessageType.TYPE_SEND) {
					holder.imgSendContent.setVisibility(View.VISIBLE);
				} else {
					holder.imgReceiveContent.setVisibility(View.VISIBLE);
				}
				if (CommonUtils.fileIsExists(filepath)) { // 文件已存在
					if (message.attach.endsWith(".wav")) { // 文件为音频文件
						if (message.isplay) {
							holder.imgSendContent
									.setImageResource(R.drawable.audiostop);
							holder.imgReceiveContent
									.setImageResource(R.drawable.audiostop);
						} else {
							holder.imgSendContent
									.setImageResource(R.drawable.audioplay);
							holder.imgReceiveContent
									.setImageResource(R.drawable.audioplay);
						}

						holder.imgSendContent
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										iOnDataChange.onPlay(message.Id,
												!message.isplay);
									}

								});
						holder.imgReceiveContent
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										iOnDataChange.onPlay(message.Id,
												!message.isplay);
									}

								});
					} else if (message.attach.endsWith(".jpg")) { // 文件为图片
						Bitmap bm = BitmapFactory.decodeFile(filepath);

						if (bm != null) {
							Bitmap icon = ThumbnailUtils.extractThumbnail(bm,
									96, 96);
							bm.recycle();

							holder.imgSendContent.setImageBitmap(icon);
							holder.imgReceiveContent.setImageBitmap(icon);
							holder.imgSendContent
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											String filepath = "";
											if (message.type == MessageType.TYPE_SEND) {
												filepath = message.attach;
											} else {
												filepath = CommonUtils
														.getFilePath(mContext)
														+ message.attach;
											}

											File file = new File(filepath);
											if (file != null
													&& file.isFile() == true) {
												Intent intent = new Intent();
												intent.setAction(android.content.Intent.ACTION_VIEW);
												intent.setDataAndType(
														Uri.fromFile(file),
														"image/*");
												mContext.startActivity(intent);
											}
										}
									});
							holder.imgReceiveContent
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											String filepath = "";
											if (message.type == MessageType.TYPE_SEND) {
												filepath = message.attach;
											} else {
												filepath = CommonUtils
														.getFilePath(mContext)
														+ message.attach;
											}

											File file = new File(filepath);

											if (file != null
													&& file.isFile() == true) {
												// 检查是否是更换车底图片
												if (message.attach
														.startsWith("Coach") == true) {
													Intent intent = new Intent(
															mContext,
															MessageImageView.class);
													Bundle bundle = new Bundle();
													bundle.putString("path",
															filepath);
													intent.putExtras(bundle);
													mContext.startActivity(intent);
												} else {
													Intent intent = new Intent();
													intent.setAction(android.content.Intent.ACTION_VIEW);
													intent.setDataAndType(
															Uri.fromFile(file),
															"image/*");
													mContext.startActivity(intent);
												}
											}
										}
									});
						}
					}
				} else { // 文件不存在，需从平台下载
					if (message.dattachstatus == ZWTMessage.STATUS_DATTACH_ING) {
						holder.imgSendContent.setImageResource(R.drawable.tp3);
						holder.imgReceiveContent
								.setImageResource(R.drawable.tp3);
						holder.imgSendContent.setOnClickListener(null);
						holder.imgReceiveContent.setOnClickListener(null);
					} else {
						if (message.dattachstatus == ZWTMessage.STATUS_DATTACH_FAIL) {
							holder.imgSendContent
									.setImageResource(R.drawable.tp1);
							holder.imgReceiveContent
									.setImageResource(R.drawable.tp1);
						} else {
							holder.imgSendContent
									.setImageResource(R.drawable.tp2);
							holder.imgReceiveContent
									.setImageResource(R.drawable.tp2);
						}

						holder.imgSendContent
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										new DownloadAttachThread(mContext,
												message, iOnDataChange).start();
									}
								});
						holder.imgReceiveContent
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										new DownloadAttachThread(mContext,
												message, iOnDataChange).start();
									}
								});
					}

					// TODO
				}
			}

			contentView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {

					if (message.content == null
							|| message.content.length() == 0) { // 附件信息
						AlertDialog dlg = new AlertDialog.Builder(mContext)
								.setTitle("")
								.setItems(
										mContext.getResources().getStringArray(
												R.array.MsgDelete),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int item) {
												if (item == 0) { // 删除信息
													ZWTMessage.Delete(mContext,
															message);

													if (iOnDataChange != null)
														iOnDataChange
																.onDelete(message.Id);
												}
											}
										}).create();
						dlg.show();
					} else { // 文字信息
						AlertDialog dlg = new AlertDialog.Builder(mContext)
								.setTitle("")
								.setItems(
										mContext.getResources().getStringArray(
												R.array.MsgTransmit),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int item) {
												if (item == 0) { // 删除信息
													ZWTMessage.Delete(mContext,
															message);
													if (iOnDataChange != null)
														iOnDataChange
																.onDelete(message.Id);
												} else if (item == 1) { // 转发信息
													if (iOnDataChange != null)
														iOnDataChange
																.onTransmit(StringUtil
																		.Encode(message.content,
																				false));
												}
											}
										}).create();
						dlg.show();
					}

					return false;
				}

			});

			return contentView;
		}

		return null;
	}

	static class ViewHolder {
		LinearLayout LayoutSend;
		TextView txtSendCreateTime;
		Button btnSendStatus;
		TextView txtSendContent;
		ImageView imgSendContent;

		LinearLayout LayoutReceive;
		TextView txtReceiveCreateTime;
		Button btnReceiveStatus, btn_return;

		TextView txtReceiveContent;
		ImageView imgReceiveContent;
	}
}