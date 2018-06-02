package easyway.Mobile.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class ZWTSeekBar extends SeekBar {
	private Drawable thumb;
	private Paint paint;
	private String value = "75";

	public ZWTSeekBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ZWTSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);
		Typeface font = Typeface.create(Typeface.DEFAULT , Typeface.ITALIC);
		paint.setTypeface(font);
		thumb = getThumb();

	}

	public ZWTSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Rect rect = thumb.getBounds();
		float fontwidth = paint.measureText(value);
		canvas.drawText(value, (thumb.getIntrinsicWidth() - fontwidth / 2) + rect.left, thumb.getIntrinsicHeight() / 2,
				paint);
		canvas.restore();
	}

	public void SetValue(String value) {
		this.value = value;
		invalidate();
	}
}
