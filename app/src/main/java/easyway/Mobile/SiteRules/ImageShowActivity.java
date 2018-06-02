package easyway.Mobile.SiteRules;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;


public class ImageShowActivity extends ActivityEx {
    private ImageView imgShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        initView();
        getData();
    }

    private void initView() {
        imgShow = (ImageView) findViewById(R.id.img_show);
    }

    private void getData() {
        String filePath = getIntent().getStringExtra("IMGFilePath");
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(filePath)) {
            bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                imgShow.setImageBitmap(bitmap);
                bitmap = null;
                bitmap.recycle();
            } else {
                Toast.makeText(this, "图片不存在", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "图片不存在", Toast.LENGTH_SHORT).show();
        }

        return;
    }
}
