package easyway.Mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils
{
    public Bitmap GetLoacalBitmap(Context c, String url, int screenWidth,
            int screenHeight)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(url, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight)
        {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        }
        else
        {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }

        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(url, opt);
        return bm;
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException
    {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        while ((count = is.read(data, 0, 4096)) != -1)
        {
            bytestream.write(data, 0, count);
        }
        data = null;
        return bytestream.toByteArray();
    }

    public byte[] GetHttpBitmap(String url)
    {
        URL myFileUrl = null;
        InputStream in = null;
        try
        {

            myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            in = conn.getInputStream();
            int resCode = conn.getResponseCode();
            if (resCode != 200)
            {
                System.gc();
                return null;
            }
            myFileUrl = null;
            System.gc();

            byte[] bytes = InputStreamToByte(in);
            CloseStream(in, null);
            return bytes;

        }
        catch (Exception e)
        {
            return null;
        }
    }

    public void CloseStream(InputStream in, OutputStream out)
    {
        try
        {
            if (null != in)
            {
                in.close();
                in = null;
            }
            if (null != out)
            {
                out.close();
                out = null;
            }
        }
        catch (Exception e)
        {

        }
    }
}
