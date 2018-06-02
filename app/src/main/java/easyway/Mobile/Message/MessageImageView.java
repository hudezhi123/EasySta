package easyway.Mobile.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;

public class MessageImageView extends ActivityEx {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_image_view);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int screen_width = metric.widthPixels; // 屏幕宽度（像素）
		int screen_height = metric.heightPixels; // 屏幕高度（像素）

		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			finish();
		}
		String path = bundle.getString("path");

		Bitmap bmp = BitmapFactory.decodeFile(path);
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int pageCount = height / screen_height + 1;
		File file = new File(path);

		LinearLayout layout = (LinearLayout) findViewById(R.id.view_content);

		InputStream istream = null;
		try {
			istream = this.getContentResolver().openInputStream(Uri.fromFile(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BitmapRegionDecoder decoder = null;
		try {
			decoder = BitmapRegionDecoder.newInstance(istream, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < pageCount; i++) {
			for (int j = 0; j < 1; j++) {
				ImageView iv = new ImageView(this);
				// iv.setBackgroundColor(0xFF000000);
				iv.setScaleType(ImageView.ScaleType.FIT_XY);
				int k = 1;
				int nw = (j * width / k);
				int nh = (i * screen_height / k);

				Bitmap bMap = decoder.decodeRegion(new Rect(nw, nh, (nw + width / k), (nh + screen_height / k)), null);
				iv.setImageBitmap(bMap);
				layout.addView(iv);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}