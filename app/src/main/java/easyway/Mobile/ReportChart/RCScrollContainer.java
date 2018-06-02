package easyway.Mobile.ReportChart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class RCScrollContainer extends LinearLayout {
	/*
	 * 一个视图工具，阻止 拦截onTouch事件传递给其自控件
	 */
	public RCScrollContainer(Context context) {
		super(context);
	}

	public RCScrollContainer(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
}

