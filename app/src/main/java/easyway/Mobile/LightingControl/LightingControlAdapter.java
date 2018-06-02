package easyway.Mobile.LightingControl;

import java.util.ArrayList;

import easyway.Mobile.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class LightingControlAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<LightingControl> mList;
	private ILightingControl iLightingControl;
	private Context mContext;

	private final int MSG_LAMPPOINTS_FAIL = 4;
	private final int MSG_LAMPPOINTS_SUCCEED = 5;

	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// closeProgressDialog();

			switch (msg.what) {
			case MSG_LAMPPOINTS_FAIL:
				Toast.makeText(mContext, R.string.Lighting_Control_FAIL,
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_LAMPPOINTS_SUCCEED:

				notifyDataSetChanged();

				Toast.makeText(mContext, R.string.Lighting_Control_SUCCEED,
						Toast.LENGTH_SHORT).show();

				break;

			}
		}
	};

	public LightingControlAdapter(Context context,
			ArrayList<LightingControl> list) {
		super();
		mInflater = LayoutInflater.from(context);
		mList = list;
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<LightingControl> models) {
		mList = models;
	}

	public void setILightingControl(ILightingControl iLightingControl) {
		this.iLightingControl = iLightingControl;
	}

	public void updateResetButtonStatus(SeekBar skTrack, Button btnReset,
			int val) {
		if (skTrack.getProgress() != val) {
			btnReset.setTextColor(Color.BLACK);
			btnReset.setEnabled(true);
		} else {
			btnReset.setTextColor(Color.GRAY);
			btnReset.setEnabled(false);
		}
	}

	public void changeTextWeightPosition(int value, TextView txtWeight) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = value * 420 / 100 + 9;

		if (value > 99) {
			params.leftMargin -= 5;
		} else if (value < 10) {
			params.leftMargin += 10;
		}
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		txtWeight.setLayoutParams(params);
		txtWeight.setText(String.valueOf(value));

	}

	public void showDialog(Context ctx, final LightingControl obj,
			final String num) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setCancelable(false);
		builder.setTitle(R.string.alert_dialog_reset_value_title);
		builder.setPositiveButton(R.string.alert_dialog_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						new Thread() {
							public void run() {

								if (LightingControl.SetLampStatus(mContext,
										obj.Id + "", num)) {
									Message msg = new Message();
									obj.CurrentVal = Integer.parseInt(num);
									msg.what = MSG_LAMPPOINTS_SUCCEED;
									myhandle.sendMessage(msg);

								} else {

									myhandle.sendEmptyMessage(MSG_LAMPPOINTS_FAIL);
								}
							}
						}.start();
					}
				});
		builder.setNeutralButton(R.string.alert_dialog_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		builder.create().show();

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		final LightingControl obj = (LightingControl) getItem(position);
		if (obj == null)
			return null;

		// if (null == convertView) {
		convertView = mInflater.inflate(
				R.layout.lighting_control_listview_item, null);
		holder = new ViewHolder();
		holder.txtCircuit = (TextView) convertView.findViewById(R.id.circuit);
		holder.btn_on = (Button) convertView.findViewById(R.id.btn_on);
		//把默认和全关按钮对换了。因为杨松说功能错了。
		holder.btn_off = (Button) convertView.findViewById(R.id.btn_default);
		holder.btn_default = (Button) convertView
				.findViewById(R.id.btn_off);
		holder.btn_custom = (Button) convertView.findViewById(R.id.btn_custom);

		if (obj.CurrentVal == 1) {
			holder.btn_on.setClickable(false);
			holder.btn_off.setClickable(true);
			holder.btn_default.setClickable(true);
			holder.btn_custom.setClickable(true);
			holder.btn_on.setBackgroundResource(R.drawable.bg_btn_text);
		} else if (obj.CurrentVal == 2) {
			holder.btn_on.setClickable(true);
			holder.btn_off.setClickable(false);
			holder.btn_default.setClickable(true);
			holder.btn_custom.setClickable(true);
			holder.btn_off.setBackgroundResource(R.drawable.bg_btn_text);
		} else if (obj.CurrentVal == 3) {
			holder.btn_on.setClickable(true);
			holder.btn_off.setClickable(true);
			holder.btn_default.setClickable(false);
			holder.btn_custom.setClickable(true);
			holder.btn_default.setBackgroundResource(R.drawable.bg_btn_text);
		} else if (obj.CurrentVal == 4) {
			holder.btn_on.setClickable(true);
			holder.btn_off.setClickable(true);
			holder.btn_default.setClickable(true);
			holder.btn_custom.setClickable(false);
			holder.btn_custom.setBackgroundResource(R.drawable.bg_btn_text);
		}

		holder.btn_on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (obj.CurrentVal != 1) {
					showDialog(mContext, obj, 1 + "");
				}

			}
		});
		holder.btn_off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (obj.CurrentVal != 2) {
					showDialog(mContext, obj, 2 + "");
				}

			}

		});
		holder.btn_default.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (obj.CurrentVal != 3) {
					showDialog(mContext, obj, 3 + "");
				}

			}
		});
		holder.btn_custom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (obj.CurrentVal == 1) {

					holder.btn_on.setBackgroundResource(R.drawable.bg_btn_text);
				} else if (obj.CurrentVal == 2) {

					holder.btn_off
							.setBackgroundResource(R.drawable.bg_btn_text);
				} else if (obj.CurrentVal == 3) {

					holder.btn_default
							.setBackgroundResource(R.drawable.bg_btn_text);
				} else if (obj.CurrentVal == 4) {

					holder.btn_custom
							.setBackgroundResource(R.drawable.bg_btn_text);
				}

				Intent intent = new Intent(mContext,
						LightingControlCustumActivity.class);
				intent.putExtra("lampId", obj.Id);
				mContext.startActivity(intent);
			}
		});

		// convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }

		//
		holder.txtCircuit.setText(obj.Circuit);

		return convertView;

	}

	class ViewHolder {
		TextView txtCircuit;
		Button btn_on, btn_off, btn_default, btn_custom;

	}
}
