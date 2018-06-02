package easyway.Mobile.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/** 
 * @author lushiju 2016-7-5 下午3:12:11 
 * 类说明 
 */
public class BitmapUtil {  
	  
    /** 
     * 生成一个二维码图像 
     *  
     * @param url 
     *            传入的字符串，通常是一个URL 
     * @param QR_WIDTH 
     *            宽度（像素值px） 
     * @param QR_HEIGHT 
     *            高度（像素值px） 
     * @return 
     */  
    public static final Bitmap create2DCoderBitmap(String url, int QR_WIDTH,  
            int QR_HEIGHT) {  
        try {  
            // 判断URL合法性  
            if (url == null || "".equals(url) || url.length() < 1) {  
                return null;  
            }  
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
            // 图像数据转换，使用了矩阵转换  
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,  
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);  
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];  
            // 下面这里按照二维码的算法，逐个生成二维码的图片，  
            // 两个for循环是图片横列扫描的结果  
            for (int y = 0; y < QR_HEIGHT; y++) {  
                for (int x = 0; x < QR_WIDTH; x++) {  
                    if (bitMatrix.get(x, y)) {  
                        pixels[y * QR_WIDTH + x] = 0xff000000;  
                    } else {  
                        pixels[y * QR_WIDTH + x] = 0xffffffff;  
                    }  
                }  
            }  
            // 生成二维码图片的格式，使用ARGB_8888  
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,  
                    Bitmap.Config.ARGB_8888);  
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);  
            // 显示到一个ImageView上面  
            // sweepIV.setImageBitmap(bitmap);  
            return bitmap;  
        } catch (WriterException e) {  
            Log.i("log", "生成二维码错误" + e.getMessage());  
            return null;  
        }  
    }  
  
    private static final int BLACK = 0xff000000;  
    private static final int WHITE = 0xffffffff; 
  
    /** 
     * 生成一个二维码图像 
     *  
     * @param url 
     *            传入的字符串，通常是一个URL 
     * @param widthAndHeight 
     *           图像的宽高 
     * @return 
     */  
    public static Bitmap createQRCode(String str, int widthAndHeight)  
            throws WriterException {  
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        BitMatrix matrix = new MultiFormatWriter().encode(str,  
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight,hints);  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        int[] pixels = new int[width * height];  
  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if (matrix.get(x, y)) {  
                    pixels[y * width + x] = BLACK;  
                }else
                	pixels[y * width + x] = WHITE;
            }  
        }  
        Bitmap bitmap = Bitmap.createBitmap(width, height,  
                Bitmap.Config.ARGB_8888);  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return bitmap;  
    }  
    
    public static String saveImageToGallery(Context context, Bitmap bmp,String devNameId) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "sybQP");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = devNameId+"-"+System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        String path = file.getPath();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
    	}
        
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
    				file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return path;
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }
    
    /**
    * 加载本地图片
    * http://bbs.3gstdy.com
    * @param url
    * @return
    */
    public static Bitmap getLoacalBitmap(String url) {
         try {
              FileInputStream fis = new FileInputStream(url);
              return BitmapFactory.decodeStream(fis);
         } catch (FileNotFoundException e) {
              e.printStackTrace();
              return null;
         }
    }
    public static String bitmapToBase64(Bitmap bitmap) {  
		  
	    String result = null;  
	    ByteArrayOutputStream baos = null;  
	    try {  
	        if (bitmap != null) {  
	            baos = new ByteArrayOutputStream();  
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	  
	            baos.flush();  
	            baos.close();  
	  
	            byte[] bitmapBytes = baos.toByteArray();  
	            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        try {  
	            if (baos != null) {  
	                baos.flush();  
	                baos.close();  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    return result;  
	}  
}  
